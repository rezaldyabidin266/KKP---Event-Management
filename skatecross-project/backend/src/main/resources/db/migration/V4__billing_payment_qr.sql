
-- ============================================
-- KKP SKATE CROSS COMPETITION
-- DATABASE MIGRATION V4
-- BILLING, PAYMENT & QR
-- ============================================
-- PostgreSQL 16
-- Multi Payment Gateway Support
--
-- Supported providers (examples):
-- MIDTRANS, XENDIT, DOKU, DUITKU, TRIPAY
--
-- Dependencies:
-- V1: events, categories, users
-- V3: registrations, category_entries
-- ============================================


-- ============================================
-- 1. INVOICES
-- Master tagihan pendaftaran
--
-- Relationship:
-- registrations (1) -> (0..1) invoices
--
-- Satu pendaftaran memiliki maksimal
-- satu invoice.
-- ============================================

CREATE TABLE invoices (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    registration_id BIGINT NOT NULL,

    invoice_code VARCHAR(60) NOT NULL,
    currency CHAR(3) NOT NULL DEFAULT 'IDR',

    total_amount NUMERIC(14,2) NOT NULL DEFAULT 0,

    status VARCHAR(20) NOT NULL DEFAULT 'UNPAID',

    expires_at TIMESTAMPTZ,
    paid_at TIMESTAMPTZ,

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    -- Foreign Keys

    CONSTRAINT fk_invoices_registration
        FOREIGN KEY (registration_id)
        REFERENCES registrations(id),

    -- Unique Constraints

    CONSTRAINT uq_invoices_registration
        UNIQUE (registration_id),

    CONSTRAINT uq_invoices_code
        UNIQUE (invoice_code),

    -- Untuk composite FK pada invoice_items

    CONSTRAINT uq_invoices_id_registration
        UNIQUE (id, registration_id),

    -- Check Constraints

    CONSTRAINT chk_invoices_total_amount
        CHECK (total_amount >= 0),

    CONSTRAINT chk_invoices_status
        CHECK (
            status IN (
                'UNPAID',
                'PAID',
                'EXPIRED',
                'CANCELLED'
            )
        )
);


-- ============================================
-- 2. CATEGORY ENTRIES - ADDITIONAL CONSTRAINT
--
-- Memungkinkan invoice_items melakukan
-- validasi bahwa item berasal dari
-- pendaftaran yang sama dengan invoice.
--
-- Tidak membuat tabel baru.
-- ============================================

ALTER TABLE category_entries
ADD CONSTRAINT uq_category_entries_id_registration
UNIQUE (id, registration_id);


-- ============================================
-- 3. INVOICE ITEMS
-- Detail tagihan berdasarkan kategori atlet
--
-- Relationship:
-- invoices (1) -> (M) invoice_items
-- category_entries (1) -> (M) invoice_items
--
-- Composite FK memastikan category_entry
-- berada pada registration yang sama
-- dengan invoice.
-- ============================================

CREATE TABLE invoice_items (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    invoice_id BIGINT NOT NULL,
    registration_id BIGINT NOT NULL,
    category_entry_id BIGINT NOT NULL,

    description VARCHAR(200) NOT NULL,

    unit_price NUMERIC(14,2) NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    line_amount NUMERIC(14,2) NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    -- Composite Foreign Keys

    CONSTRAINT fk_invoice_items_invoice_registration
        FOREIGN KEY (invoice_id, registration_id)
        REFERENCES invoices(id, registration_id),

    CONSTRAINT fk_invoice_items_entry_registration
        FOREIGN KEY (category_entry_id, registration_id)
        REFERENCES category_entries(id, registration_id),

    -- Unique Constraints

    CONSTRAINT uq_invoice_items_invoice_entry
        UNIQUE (invoice_id, category_entry_id),

    -- Check Constraints

    CONSTRAINT chk_invoice_items_unit_price
        CHECK (unit_price >= 0),

    CONSTRAINT chk_invoice_items_quantity
        CHECK (quantity > 0),

    CONSTRAINT chk_invoice_items_line_amount
        CHECK (line_amount >= 0)
);


-- ============================================
-- 4. PAYMENTS
-- Riwayat percobaan transaksi pembayaran
--
-- Relationship:
-- invoices (1) -> (M) payments
--
-- Setiap payment adalah satu percobaan
-- pembayaran pada sebuah provider.
--
-- Contoh:
-- Payment #1: MIDTRANS -> EXPIRED
-- Payment #2: XENDIT   -> SUCCEEDED
--
-- Gateway-specific information disimpan
-- menggunakan kolom generic dan JSONB.
-- ============================================

CREATE TABLE payments (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    invoice_id BIGINT NOT NULL,

    -- Payment Gateway Information

    gateway VARCHAR(40) NOT NULL,

    -- ID transaksi yang dibuat aplikasi
    -- untuk dikirim ke gateway

    merchant_reference VARCHAR(100) NOT NULL,

    -- ID transaksi dari gateway

    external_payment_id VARCHAR(120),

    -- Referensi tambahan seperti session ID
    -- atau reference ID dari gateway

    external_reference VARCHAR(120),

    -- Payment Method
    -- Contoh: QRIS, VA, EWALLET, CARD

    payment_method VARCHAR(40),

    -- Payment Amount

    amount NUMERIC(14,2) NOT NULL,

    -- Normalized Application Status

    status VARCHAR(20) NOT NULL DEFAULT 'CREATED',

    -- Status asli dari gateway
    -- Contoh Midtrans: settlement, pending,
    -- expire, deny, cancel

    provider_status VARCHAR(60),

    -- Hosted Checkout URL

    payment_url TEXT,

    -- Token checkout jika gateway
    -- menggunakan token-based checkout

    checkout_token TEXT,

    -- Response provider yang sudah
    -- disanitasi dari informasi sensitif

    provider_response JSONB,

    -- Idempotency untuk pembuatan pembayaran

    idempotency_key VARCHAR(100),

    -- Payment Expiration & Completion

    expires_at TIMESTAMPTZ,
    paid_at TIMESTAMPTZ,

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    -- Foreign Keys

    CONSTRAINT fk_payments_invoice
        FOREIGN KEY (invoice_id)
        REFERENCES invoices(id),

    -- Unique Constraints

    CONSTRAINT uq_payments_gateway_merchant_reference
        UNIQUE (gateway, merchant_reference),

    -- Check Constraints

    CONSTRAINT chk_payments_amount
        CHECK (amount >= 0),

    CONSTRAINT chk_payments_status
        CHECK (
            status IN (
                'CREATED',
                'PENDING',
                'SUCCEEDED',
                'FAILED',
                'EXPIRED',
                'CANCELLED'
            )
        ),

    CONSTRAINT chk_payments_gateway_not_empty
        CHECK (length(trim(gateway)) > 0),

    CONSTRAINT chk_payments_merchant_reference_not_empty
        CHECK (length(trim(merchant_reference)) > 0),

    CONSTRAINT chk_payments_provider_response_object
        CHECK (
            provider_response IS NULL
            OR jsonb_typeof(provider_response) = 'object'
        )
);


-- ============================================
-- 5. PAYMENT IDEMPOTENCY INDEX
--
-- Mencegah pembuatan payment attempt
-- duplikat ketika request API di-retry.
-- ============================================

CREATE UNIQUE INDEX uq_payments_gateway_idempotency
ON payments(gateway, idempotency_key)
WHERE idempotency_key IS NOT NULL;


-- ============================================
-- 6. PAYMENT WEBHOOKS
-- Riwayat notifikasi dari Payment Gateway
--
-- Relationship:
-- payments (1) -> (M) payment_webhooks
--
-- payment_id nullable karena webhook
-- mungkin diterima sebelum transaksi
-- berhasil ditemukan di database.
--
-- Semua webhook harus diverifikasi
-- sebelum mengubah status pembayaran.
-- ============================================

CREATE TABLE payment_webhooks (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    payment_id BIGINT,

    gateway VARCHAR(40) NOT NULL,

    -- External webhook/event ID
    -- Nullable karena tidak semua gateway
    -- mengirimkan ID event tersendiri

    external_event_id VARCHAR(160),

    -- Event type dari gateway
    -- Contoh: payment.succeeded,
    -- payment.failed, settlement

    event_type VARCHAR(80),

    -- Signature Verification Result

    signature_valid BOOLEAN NOT NULL DEFAULT FALSE,

    -- Webhook Processing Status

    process_status VARCHAR(16) NOT NULL DEFAULT 'RECEIVED',

    -- JSON payload dari gateway.
    -- Hanya simpan payload yang sudah
    -- disanitasi dari data sensitif.

    payload JSONB,

    -- SHA-256 dari canonical/raw payload
    -- untuk mendeteksi webhook duplikat

    payload_hash CHAR(64),

    -- Processing Error Details

    processing_error TEXT,

    processed_at TIMESTAMPTZ,

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    -- Foreign Keys

    CONSTRAINT fk_payment_webhooks_payment
        FOREIGN KEY (payment_id)
        REFERENCES payments(id),

    -- Unique Constraints

    CONSTRAINT uq_payment_webhooks_gateway_event
        UNIQUE (gateway, external_event_id),

    -- Check Constraints

    CONSTRAINT chk_payment_webhooks_process_status
        CHECK (
            process_status IN (
                'RECEIVED',
                'PROCESSED',
                'IGNORED',
                'INVALID'
            )
        ),

    CONSTRAINT chk_payment_webhooks_gateway_not_empty
        CHECK (length(trim(gateway)) > 0)
);


-- ============================================
-- 7. PAYMENT WEBHOOK DEDUPLICATION
--
-- Digunakan jika provider tidak mengirim
-- external_event_id.
--
-- Hash webhook dihitung di Spring Boot.
-- ============================================

CREATE UNIQUE INDEX uq_payment_webhooks_gateway_payload_hash
ON payment_webhooks(gateway, payload_hash)
WHERE payload_hash IS NOT NULL;


-- ============================================
-- 8. QR PASSES
-- QR Code untuk Check-in Pembukaan
--
-- BUKAN QRIS untuk pembayaran.
--
-- Relationship:
-- registrations (1) -> (0..1) qr_passes
--
-- QR diterbitkan ketika pendaftaran
-- sudah memenuhi syarat check-in.
-- ============================================

CREATE TABLE qr_passes (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    registration_id BIGINT NOT NULL,

    -- Hash dari random QR token
    -- Jangan simpan plaintext token

    token_hash VARCHAR(128) NOT NULL,

    issued_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    revoked_at TIMESTAMPTZ,

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    -- Foreign Keys

    CONSTRAINT fk_qr_passes_registration
        FOREIGN KEY (registration_id)
        REFERENCES registrations(id),

    -- Unique Constraints

    CONSTRAINT uq_qr_passes_registration
        UNIQUE (registration_id),

    CONSTRAINT uq_qr_passes_token_hash
        UNIQUE (token_hash),

    -- Check Constraints

    CONSTRAINT chk_qr_passes_revocation_time
        CHECK (
            revoked_at IS NULL
            OR revoked_at >= issued_at
        )
);
