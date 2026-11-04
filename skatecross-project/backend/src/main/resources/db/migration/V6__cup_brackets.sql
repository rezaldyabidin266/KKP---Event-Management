-- ============================================
-- KKP SKATE CROSS
-- MIGRATION V6: CUP BRACKETS
-- ============================================

-- 1. CUP BRACKETS

CREATE TABLE cup_brackets (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    category_id BIGINT NOT NULL REFERENCES categories(id),
    bracket_size SMALLINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',

    created_by_user_id BIGINT NOT NULL REFERENCES users(id),
    published_at TIMESTAMPTZ,

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CHECK (bracket_size IN (4, 8, 16, 32)),

    CHECK (
        status IN (
            'DRAFT',
            'READY',
            'RUNNING',
            'FINAL'
        )
    ),

    UNIQUE (category_id),
    UNIQUE (id, category_id)
);

-- 2. CUP MATCHES

CREATE TABLE cup_matches (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    cup_bracket_id BIGINT NOT NULL REFERENCES cup_brackets(id),
    category_id BIGINT NOT NULL REFERENCES categories(id),

    round_number SMALLINT NOT NULL,
    match_number INTEGER NOT NULL,

    entry_a_id BIGINT,
    entry_b_id BIGINT,
    winner_entry_id BIGINT,

    next_match_id BIGINT REFERENCES cup_matches(id),
    next_slot CHAR(1),

    is_final BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CHECK (round_number >= 1),
    CHECK (match_number >= 1),

    CHECK (
        entry_a_id IS NULL
        OR entry_b_id IS NULL
        OR entry_a_id <> entry_b_id
    ),

    CHECK (
        next_slot IS NULL
        OR next_slot IN ('A', 'B')
    ),

    CHECK (
        status IN (
            'SCHEDULED',
            'RUNNING',
            'WAITING_REVIEW',
            'FINAL',
            'CANCELLED'
        )
    ),

    UNIQUE (cup_bracket_id, round_number, match_number),
    UNIQUE (id, category_id),
    UNIQUE (id, cup_bracket_id),

    FOREIGN KEY (entry_a_id, category_id)
        REFERENCES category_entries(id, category_id),

    FOREIGN KEY (entry_b_id, category_id)
        REFERENCES category_entries(id, category_id),

    FOREIGN KEY (winner_entry_id, category_id)
        REFERENCES category_entries(id, category_id),

    FOREIGN KEY (cup_bracket_id, category_id)
        REFERENCES cup_brackets(id, category_id)
);

-- 3. CUP MATCH DECISIONS

CREATE TABLE cup_match_decisions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    cup_match_id BIGINT NOT NULL REFERENCES cup_matches(id),
    decided_by_user_id BIGINT NOT NULL REFERENCES users(id),

    decision_code VARCHAR(20) NOT NULL,
    winner_entry_id BIGINT REFERENCES category_entries(id),

    reason TEXT,
    decided_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CHECK (
        decision_code IN (
            'FINISH',
            'WALKOVER',
            'REPLAY',
            'NO_CONTEST'
        )
    )
);