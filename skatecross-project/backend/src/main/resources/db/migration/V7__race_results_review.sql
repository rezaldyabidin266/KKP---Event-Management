-- ============================================
-- KKP SKATE CROSS
-- MIGRATION V7: RACE AND RESULTS
-- ============================================

-- 1. RACES

CREATE TABLE races (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    category_id BIGINT NOT NULL REFERENCES categories(id),
    cup_match_id BIGINT REFERENCES cup_matches(id),

    race_type VARCHAR(15) NOT NULL,
    heat_number INTEGER,
    attempt_number INTEGER NOT NULL DEFAULT 1,
    capacity INTEGER,

    scheduled_at TIMESTAMPTZ,
    started_at TIMESTAMPTZ,
    pre_race_deadline_at TIMESTAMPTZ,

    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CHECK (race_type IN ('ELIMINATION', 'CUP')),

    CHECK (
        (race_type = 'CUP') = (cup_match_id IS NOT NULL)
    ),

    CHECK (attempt_number >= 1),
    CHECK (heat_number IS NULL OR heat_number >= 1),
    CHECK (capacity IS NULL OR capacity > 0),

    CHECK (
        status IN (
            'SCHEDULED',
            'RUNNING',
            'COMPLETED',
            'CANCELLED'
        )
    ),

    UNIQUE (id, category_id),

    FOREIGN KEY (cup_match_id, category_id)
        REFERENCES cup_matches(id, category_id)
);

-- 2. RACE PARTICIPANTS

CREATE TABLE race_participants (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    race_id BIGINT NOT NULL REFERENCES races(id),
    category_id BIGINT NOT NULL REFERENCES categories(id),

    category_entry_id BIGINT NOT NULL
        REFERENCES category_entries(id),

    start_position INTEGER NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CHECK (start_position > 0),

    UNIQUE (race_id, category_entry_id),
    UNIQUE (race_id, start_position),

    FOREIGN KEY (race_id, category_id)
        REFERENCES races(id, category_id),

    FOREIGN KEY (category_entry_id, category_id)
        REFERENCES category_entries(id, category_id)
);

-- 3. PRERACE CHECKS

CREATE TABLE prerace_checks (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    race_participant_id BIGINT NOT NULL
        REFERENCES race_participants(id),

    checked_by_user_id BIGINT NOT NULL REFERENCES users(id),

    deadline_at TIMESTAMPTZ NOT NULL,
    status VARCHAR(15) NOT NULL,
    checked_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    note TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CHECK (status IN ('READY', 'DQ', 'WITHDRAWN')),

    UNIQUE (race_participant_id)
);

-- 4. PRERACE DEADLINE CHANGES

CREATE TABLE prerace_deadline_changes (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    race_id BIGINT NOT NULL REFERENCES races(id),
    changed_by_user_id BIGINT NOT NULL REFERENCES users(id),

    old_deadline_at TIMESTAMPTZ,
    new_deadline_at TIMESTAMPTZ NOT NULL,

    reason TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 5. RACE RESULTS

CREATE TABLE race_results (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    race_participant_id BIGINT NOT NULL
        REFERENCES race_participants(id),

    reported_by_user_id BIGINT NOT NULL REFERENCES users(id),

    attempt_number INTEGER NOT NULL DEFAULT 1,
    record_type VARCHAR(16) NOT NULL DEFAULT 'TIME',

    elapsed_ms BIGINT,
    finish_position SMALLINT,

    review_status VARCHAR(16) NOT NULL DEFAULT 'DRAFT',
    notes TEXT,

    submitted_at TIMESTAMPTZ,
    finalized_at TIMESTAMPTZ,

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CHECK (attempt_number > 0),

    CHECK (
        record_type IN (
            'TIME',
            'DQ',
            'WITHDRAWN',
            'WALKOVER'
        )
    ),

    CHECK (
        review_status IN (
            'DRAFT',
            'SUBMITTED',
            'REJECTED',
            'FINAL'
        )
    ),

    CHECK (
        record_type <> 'TIME'
        OR (elapsed_ms IS NOT NULL AND elapsed_ms > 0)
    ),

    CHECK (
        record_type = 'TIME'
        OR elapsed_ms IS NULL
    ),

    CHECK (
        finish_position IS NULL
        OR finish_position >= 1
    ),

    UNIQUE (race_participant_id, attempt_number)
);

-- 6. RESULT REVIEWS

CREATE TABLE result_reviews (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    race_result_id BIGINT NOT NULL REFERENCES race_results(id),
    reviewer_user_id BIGINT NOT NULL REFERENCES users(id),

    decision VARCHAR(12) NOT NULL,
    notes TEXT,

    reviewed_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CHECK (decision IN ('APPROVED', 'REJECTED'))
);