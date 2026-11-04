-- ============================================
-- KKP SKATE CROSS
-- MIGRATION V5: OPENING CHECK-IN
-- ============================================

-- 1. CHECKPOINTS

CREATE TABLE checkpoints (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    event_id BIGINT NOT NULL REFERENCES events(id),
    name VARCHAR(120) NOT NULL,
    location_description VARCHAR(240) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    UNIQUE (event_id, name),
    UNIQUE (id, event_id)
);

-- 2. CHECKPOINT GROUPS

CREATE TABLE checkpoint_groups (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    event_id BIGINT NOT NULL REFERENCES events(id),
    checkpoint_id BIGINT NOT NULL REFERENCES checkpoints(id),

    group_code VARCHAR(40) NOT NULL,
    target_size INTEGER NOT NULL DEFAULT 5,
    assigned_by_user_id BIGINT REFERENCES users(id),

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CHECK (target_size > 0),

    UNIQUE (event_id, group_code),

    FOREIGN KEY (checkpoint_id, event_id)
        REFERENCES checkpoints(id, event_id)
);

-- 3. OPENING CHECKINS

CREATE TABLE opening_checkins (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    registration_id BIGINT NOT NULL REFERENCES registrations(id),
    checked_in_by_user_id BIGINT NOT NULL REFERENCES users(id),
    qr_pass_id BIGINT REFERENCES qr_passes(id),

    method VARCHAR(10) NOT NULL,
    manual_validation_note TEXT,
    checked_in_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CHECK (method IN ('QR', 'MANUAL')),

    CHECK (
        method <> 'MANUAL'
        OR manual_validation_note IS NOT NULL
    ),

    UNIQUE (registration_id),
    UNIQUE (id, registration_id)
);

-- 4. OPENING CHECKIN ATHLETES

CREATE TABLE opening_checkin_athletes (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    opening_checkin_id BIGINT NOT NULL
        REFERENCES opening_checkins(id),

    registration_id BIGINT NOT NULL
        REFERENCES registrations(id),

    athlete_id BIGINT NOT NULL
        REFERENCES athletes(id),

    checkpoint_group_id BIGINT NOT NULL
        REFERENCES checkpoint_groups(id),

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    UNIQUE (registration_id, athlete_id),

    FOREIGN KEY (opening_checkin_id, registration_id)
        REFERENCES opening_checkins(id, registration_id),

    FOREIGN KEY (registration_id, athlete_id)
        REFERENCES registration_athletes(
            registration_id,
            athlete_id
        )
);