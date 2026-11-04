-- ============================================
-- KKP SKATE CROSS
-- MIGRATION V3: REGISTRATION
-- ============================================

-- 1. PARTICIPANT PICS

CREATE TABLE participant_pics (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    full_name VARCHAR(160) NOT NULL,
    phone VARCHAR(32) NOT NULL,
    email VARCHAR(254),
    club_name VARCHAR(160),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 2. ATHLETES

CREATE TABLE athletes (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    full_name VARCHAR(160) NOT NULL,
    birth_date DATE NOT NULL,
    gender VARCHAR(12) NOT NULL,
    club_name VARCHAR(160),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CHECK (gender IN ('MALE', 'FEMALE'))
);

-- 3. REGISTRATIONS

CREATE TABLE registrations (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    event_id BIGINT NOT NULL REFERENCES events(id),
    pic_id BIGINT NOT NULL REFERENCES participant_pics(id),

    registration_number BIGINT NOT NULL,
    registration_code VARCHAR(60) NOT NULL,
    registration_type VARCHAR(12) NOT NULL DEFAULT 'TEAM',
    status VARCHAR(25) NOT NULL DEFAULT 'AWAITING_PAYMENT',

    registered_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CHECK (registration_number > 0),
    CHECK (registration_type IN ('TEAM', 'INDIVIDUAL')),
    CHECK (
        status IN (
            'DRAFT',
            'AWAITING_PAYMENT',
            'CONFIRMED',
            'CANCELLED',
            'EXPIRED'
        )
    ),

    UNIQUE (event_id, registration_number),
    UNIQUE (event_id, registration_code),
    UNIQUE (id, event_id)
);

-- 4. REGISTRATION ATHLETES
-- Satu pendaftaran dapat memiliki banyak atlet.
-- Satu atlet juga dapat muncul pada pendaftaran berbeda.

CREATE TABLE registration_athletes (
    registration_id BIGINT NOT NULL REFERENCES registrations(id),
    athlete_id BIGINT NOT NULL REFERENCES athletes(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    PRIMARY KEY (registration_id, athlete_id)
);

-- 5. CATEGORY ENTRIES
-- Menghubungkan atlet dengan kategori yang diikuti.

CREATE TABLE category_entries (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    registration_id BIGINT NOT NULL REFERENCES registrations(id),
    event_id BIGINT NOT NULL REFERENCES events(id),
    athlete_id BIGINT NOT NULL REFERENCES athletes(id),
    category_id BIGINT NOT NULL REFERENCES categories(id),

    start_number INTEGER,
    registration_order BIGINT NOT NULL,
    entry_status VARCHAR(20) NOT NULL DEFAULT 'REGISTERED',
    fee_snapshot NUMERIC(14,2) NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CHECK (registration_order > 0),
    CHECK (fee_snapshot >= 0),
    CHECK (start_number IS NULL OR start_number > 0),

    CHECK (
        entry_status IN (
            'REGISTERED',
            'CONFIRMED',
            'WITHDRAWN',
            'DISQUALIFIED'
        )
    ),

    UNIQUE (registration_id, athlete_id, category_id),
    UNIQUE (category_id, athlete_id),
    UNIQUE (id, category_id),

    FOREIGN KEY (registration_id, event_id)
        REFERENCES registrations(id, event_id),

    FOREIGN KEY (category_id, event_id)
        REFERENCES categories(id, event_id),

    FOREIGN KEY (registration_id, athlete_id)
        REFERENCES registration_athletes(registration_id, athlete_id)
);