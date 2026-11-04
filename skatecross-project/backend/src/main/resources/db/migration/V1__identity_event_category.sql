
-- ============================================
-- KKP SKATE CROSS COMPETITION
-- DATABASE MIGRATION V1
-- Identity, Role, Event, Category
-- ============================================
-- PostgreSQL 16
-- Spring Boot 2.7.18 + Java 8
-- Role-Based Access Control: 2 tabel
-- ============================================

-- ============================================
-- 1. ROLES
-- Master Role
-- ============================================

CREATE TABLE roles (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL,
    description TEXT,

    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT chk_roles_code
        CHECK (code ~ '^[A-Z][A-Z0-9_]*$')
);


-- ============================================
-- 2. ROLE_ACCESS
-- Hak akses halaman berdasarkan role
-- Tidak menggunakan tabel pages
-- ============================================

CREATE TABLE role_access (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    role_id BIGINT NOT NULL,
    page_key VARCHAR(100) NOT NULL,

    can_view BOOLEAN NOT NULL DEFAULT FALSE,
    can_create BOOLEAN NOT NULL DEFAULT FALSE,
    can_edit BOOLEAN NOT NULL DEFAULT FALSE,
    can_delete BOOLEAN NOT NULL DEFAULT FALSE,
    can_approve BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_role_access_role
        FOREIGN KEY (role_id)
        REFERENCES roles(id),

    CONSTRAINT uq_role_access_role_page
        UNIQUE (role_id, page_key),

    CONSTRAINT chk_role_access_page_key
        CHECK (page_key ~ '^[A-Z][A-Z0-9_]*$')
);


-- ============================================
-- 3. USERS
-- Akun login aplikasi
-- Satu user memiliki satu role
-- ============================================

CREATE TABLE users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    role_id BIGINT NOT NULL,
    full_name VARCHAR(160) NOT NULL,
    email VARCHAR(254) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,

    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_users_role
        FOREIGN KEY (role_id)
        REFERENCES roles(id),

    CONSTRAINT uq_users_email
        UNIQUE (email)
);


-- ============================================
-- 4. EVENTS
-- Master event kompetisi
-- ============================================

CREATE TABLE events (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    event_code VARCHAR(40) NOT NULL,
    name VARCHAR(200) NOT NULL,

    event_date DATE NOT NULL,

    registration_open_at TIMESTAMPTZ NOT NULL,
    registration_close_at TIMESTAMPTZ NOT NULL,

    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',

    created_by_user_id BIGINT,

    next_registration_no BIGINT NOT NULL DEFAULT 1,

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_events_created_by
        FOREIGN KEY (created_by_user_id)
        REFERENCES users(id),

    CONSTRAINT uq_events_code
        UNIQUE (event_code),

    CONSTRAINT chk_events_registration_period
        CHECK (
            registration_open_at < registration_close_at
        ),

    CONSTRAINT chk_events_registration_number
        CHECK (next_registration_no > 0),

    CONSTRAINT chk_events_status
        CHECK (
            status IN (
                'DRAFT',
                'PUBLISHED',
                'COMPLETED',
                'CANCELLED'
            )
        )
);


-- ============================================
-- 5. CATEGORIES
-- Kategori lomba pada masing-masing event
-- ============================================

CREATE TABLE categories (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    event_id BIGINT NOT NULL,

    category_code VARCHAR(40) NOT NULL,
    name VARCHAR(160) NOT NULL,

    min_age SMALLINT NOT NULL,
    max_age SMALLINT NOT NULL,
    gender VARCHAR(12) NOT NULL,

    fee NUMERIC(14,2) NOT NULL DEFAULT 0,
    quota INTEGER NOT NULL,

    cup_size SMALLINT NOT NULL DEFAULT 8,

    pre_race_window_minutes SMALLINT
        NOT NULL DEFAULT 15,

    registration_open_at TIMESTAMPTZ,
    registration_close_at TIMESTAMPTZ,

    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_categories_event
        FOREIGN KEY (event_id)
        REFERENCES events(id),

    CONSTRAINT uq_categories_event_code
        UNIQUE (event_id, category_code),

    CONSTRAINT uq_categories_id_event
        UNIQUE (id, event_id),

    CONSTRAINT chk_categories_age
        CHECK (
            min_age >= 0
            AND max_age >= min_age
        ),

    CONSTRAINT chk_categories_gender
        CHECK (
            gender IN (
                'MALE',
                'FEMALE',
                'MIXED'
            )
        ),

    CONSTRAINT chk_categories_fee
        CHECK (fee >= 0),

    CONSTRAINT chk_categories_quota
        CHECK (quota >= 1),

    CONSTRAINT chk_categories_cup_size
        CHECK (
            cup_size IN (4, 8, 16, 32)
        ),

    CONSTRAINT chk_categories_prerace_window
        CHECK (pre_race_window_minutes > 0),

    CONSTRAINT chk_categories_registration_period
        CHECK (
            registration_open_at IS NULL
            OR registration_close_at IS NULL
            OR registration_open_at < registration_close_at
        )
);


-- ============================================
-- 6. EVENT_ASSIGNMENTS
-- Penugasan panitia berdasarkan event/kategori
-- Berbeda dari role akses aplikasi
-- ============================================

CREATE TABLE event_assignments (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    event_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,

    role_code VARCHAR(30) NOT NULL,

    category_id BIGINT,

    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    assigned_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_event_assignments_event
        FOREIGN KEY (event_id)
        REFERENCES events(id),

    CONSTRAINT fk_event_assignments_user
        FOREIGN KEY (user_id)
        REFERENCES users(id),

    CONSTRAINT chk_event_assignments_role
        CHECK (
            role_code IN (
                'STAFF',
                'JUDGE',
                'EVENT_CHAIR',
                'CATEGORY_PIC',
                'OWNER'
            )
        )
);


-- ============================================
-- 7. CATEGORY_PERMISSION_REQUESTS
-- Pengajuan izin pelaksanaan kategori
-- ============================================

CREATE TABLE category_permission_requests (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    category_id BIGINT NOT NULL,

    requested_by_user_id BIGINT NOT NULL,

    readiness_notes TEXT,

    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',

    requested_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    decided_by_user_id BIGINT,
    decided_at TIMESTAMPTZ,
    decision_notes TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_category_requests_category
        FOREIGN KEY (category_id)
        REFERENCES categories(id),

    CONSTRAINT fk_category_requests_requested_by
        FOREIGN KEY (requested_by_user_id)
        REFERENCES users(id),

    CONSTRAINT fk_category_requests_decided_by
        FOREIGN KEY (decided_by_user_id)
        REFERENCES users(id),

    CONSTRAINT chk_category_requests_status
        CHECK (
            status IN (
                'PENDING',
                'APPROVED',
                'REJECTED'
            )
        )
);


-- ============================================
-- 8. CATEGORY_PERMISSIONS
-- Status LOCKED / UNLOCKED setiap kategori
-- pada tanggal pelaksanaan
-- ============================================

CREATE TABLE category_permissions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    category_id BIGINT NOT NULL,

    effective_date DATE NOT NULL,

    status VARCHAR(20) NOT NULL DEFAULT 'LOCKED',

    latest_request_id BIGINT,

    unlocked_at TIMESTAMPTZ,

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_category_permissions_category
        FOREIGN KEY (category_id)
        REFERENCES categories(id),

    CONSTRAINT fk_category_permissions_latest_request
        FOREIGN KEY (latest_request_id)
        REFERENCES category_permission_requests(id),

    CONSTRAINT uq_category_permissions_date
        UNIQUE (category_id, effective_date),

    CONSTRAINT chk_category_permissions_status
        CHECK (
            status IN (
                'LOCKED',
                'UNLOCKED'
            )
        )
);
