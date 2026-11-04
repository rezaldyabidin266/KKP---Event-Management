
-- ============================================
-- KKP SKATE CROSS COMPETITION
-- DATABASE MIGRATION V2
-- Rundown & Approval
-- ============================================
-- PostgreSQL 16
-- ============================================


-- ============================================
-- 1. RUNDOWNS
-- Master susunan acara untuk setiap event
-- Mendukung revisi dan pengajuan approval
-- ============================================

CREATE TABLE rundowns (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    event_id BIGINT NOT NULL,

    revision_number INTEGER NOT NULL DEFAULT 1,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',

    submitted_by_user_id BIGINT,
    submitted_at TIMESTAMPTZ,

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_rundowns_event
        FOREIGN KEY (event_id)
        REFERENCES events(id),

    CONSTRAINT fk_rundowns_submitted_by
        FOREIGN KEY (submitted_by_user_id)
        REFERENCES users(id),

    CONSTRAINT uq_rundowns_event_revision
        UNIQUE (event_id, revision_number),

    CONSTRAINT chk_rundowns_revision
        CHECK (revision_number > 0),

    CONSTRAINT chk_rundowns_status
        CHECK (
            status IN (
                'DRAFT',
                'SUBMITTED',
                'APPROVED',
                'REJECTED'
            )
        )
);


-- ============================================
-- 2. RUNDOWN_ITEMS
-- Detail agenda dari suatu rundown
-- ============================================

CREATE TABLE rundown_items (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    rundown_id BIGINT NOT NULL,
    category_id BIGINT,

    sequence_no INTEGER NOT NULL,

    title VARCHAR(180) NOT NULL,
    phase VARCHAR(25) NOT NULL DEFAULT 'GENERAL',

    starts_at TIMESTAMPTZ NOT NULL,
    ends_at TIMESTAMPTZ NOT NULL,

    area VARCHAR(160) NOT NULL,

    assigned_staff_id BIGINT,

    notes TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_rundown_items_rundown
        FOREIGN KEY (rundown_id)
        REFERENCES rundowns(id),

    CONSTRAINT fk_rundown_items_category
        FOREIGN KEY (category_id)
        REFERENCES categories(id),

    CONSTRAINT fk_rundown_items_staff
        FOREIGN KEY (assigned_staff_id)
        REFERENCES event_assignments(id),

    CONSTRAINT uq_rundown_items_sequence
        UNIQUE (rundown_id, sequence_no),

    CONSTRAINT chk_rundown_items_sequence
        CHECK (sequence_no >= 1),

    CONSTRAINT chk_rundown_items_time
        CHECK (starts_at < ends_at),

    CONSTRAINT chk_rundown_items_phase
        CHECK (
            phase IN (
                'GENERAL',
                'OPENING',
                'ELIMINATION',
                'CUP',
                'CLOSING'
            )
        )
);


-- ============================================
-- 3. RUNDOWN_REVIEWS
-- Riwayat persetujuan atau penolakan rundown
-- ============================================

CREATE TABLE rundown_reviews (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    rundown_id BIGINT NOT NULL,
    reviewed_by_user_id BIGINT NOT NULL,

    decision VARCHAR(12) NOT NULL,
    notes TEXT,

    reviewed_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_rundown_reviews_rundown
        FOREIGN KEY (rundown_id)
        REFERENCES rundowns(id),

    CONSTRAINT fk_rundown_reviews_user
        FOREIGN KEY (reviewed_by_user_id)
        REFERENCES users(id),

    CONSTRAINT chk_rundown_reviews_decision
        CHECK (
            decision IN (
                'APPROVED',
                'REJECTED'
            )
        )
);
