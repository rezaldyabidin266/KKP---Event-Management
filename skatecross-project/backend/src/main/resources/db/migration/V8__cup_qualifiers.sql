-- ============================================
-- KKP SKATE CROSS
-- MIGRATION V8: CUP QUALIFIERS
-- ============================================

CREATE TABLE cup_qualifiers (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    cup_bracket_id BIGINT NOT NULL REFERENCES cup_brackets(id),
    category_id BIGINT NOT NULL REFERENCES categories(id),

    category_entry_id BIGINT NOT NULL
        REFERENCES category_entries(id),

    source_result_id BIGINT REFERENCES race_results(id),

    seed_number INTEGER,
    qualification_time_ms BIGINT,

    selected_by_user_id BIGINT NOT NULL REFERENCES users(id),
    selected_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CHECK (seed_number IS NULL OR seed_number >= 1),

    CHECK (
        qualification_time_ms IS NULL
        OR qualification_time_ms > 0
    ),

    UNIQUE (cup_bracket_id, category_entry_id),

    FOREIGN KEY (cup_bracket_id, category_id)
        REFERENCES cup_brackets(id, category_id),

    FOREIGN KEY (category_entry_id, category_id)
        REFERENCES category_entries(id, category_id)
);