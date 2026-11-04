-- ============================================
-- KKP SKATE CROSS
-- MIGRATION V9
-- AUDIT, INDEXES, CONSTRAINTS, TRIGGERS
-- ============================================

-- ============================================
-- 1. AUDIT LOGS
-- ============================================

CREATE TABLE audit_logs (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    event_id BIGINT REFERENCES events(id),
    actor_user_id BIGINT REFERENCES users(id),

    table_name VARCHAR(80) NOT NULL,
    row_id BIGINT,
    action VARCHAR(30) NOT NULL,

    before_data JSONB,
    after_data JSONB,

    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ============================================
-- 2. ADDITIONAL FOREIGN KEY
-- ============================================

-- Assignment kategori harus sesuai dengan event.

ALTER TABLE event_assignments
ADD CONSTRAINT fk_event_assignments_category_event
FOREIGN KEY (category_id, event_id)
REFERENCES categories(id, event_id);

-- ============================================
-- 3. INDEXES
-- ============================================

-- Authentication & Role Access

CREATE INDEX ix_users_role_id
ON users(role_id);

CREATE INDEX ix_users_is_active
ON users(is_active);

CREATE INDEX ix_role_access_page_key
ON role_access(page_key);

CREATE UNIQUE INDEX uq_users_email_case_insensitive
ON users(lower(email));

-- Event & Category

CREATE INDEX ix_events_event_date
ON events(event_date);

CREATE INDEX ix_events_status
ON events(status);

CREATE INDEX ix_event_assignments_event_id_user_id
ON event_assignments(event_id, user_id);

CREATE INDEX ix_event_assignments_category_id
ON event_assignments(category_id);

CREATE INDEX ix_categories_event_id_gender
ON categories(event_id, gender);

CREATE INDEX ix_category_permission_requests_category_id_status
ON category_permission_requests(category_id, status);

CREATE INDEX ix_category_permission_requests_requested_by_user_id
ON category_permission_requests(requested_by_user_id);

-- Rundown

CREATE INDEX ix_rundowns_event_id_status
ON rundowns(event_id, status);

CREATE INDEX ix_rundown_items_category_id
ON rundown_items(category_id);

CREATE INDEX ix_rundown_items_starts_at
ON rundown_items(starts_at);

CREATE INDEX ix_rundown_reviews_rundown_id_reviewed_at
ON rundown_reviews(rundown_id, reviewed_at);

-- Registration

CREATE INDEX ix_participant_pics_phone
ON participant_pics(phone);

CREATE INDEX ix_athletes_full_name_birth_date
ON athletes(full_name, birth_date);

CREATE INDEX ix_registrations_pic_id
ON registrations(pic_id);

CREATE INDEX ix_registrations_event_id_status
ON registrations(event_id, status);

CREATE INDEX ix_category_entries_category_id_registration_order
ON category_entries(category_id, registration_order);

CREATE INDEX ix_category_entries_event_id
ON category_entries(event_id);

-- Payment

CREATE INDEX ix_invoices_status
ON invoices(status);

CREATE INDEX ix_payments_invoice_id_status
ON payments(invoice_id, status);

CREATE INDEX ix_payments_gateway_external_payment_id
ON payments(gateway, external_payment_id);

CREATE INDEX ix_payment_webhooks_payment_id
ON payment_webhooks(payment_id);

-- Cup

CREATE INDEX ix_cup_match_decisions_cup_match_id_decided_at
ON cup_match_decisions(cup_match_id, decided_at);

-- Race & Verification

CREATE INDEX ix_races_category_id_race_type
ON races(category_id, race_type);

CREATE INDEX ix_races_cup_match_id
ON races(cup_match_id);

CREATE INDEX ix_race_participants_category_entry_id
ON race_participants(category_entry_id);

CREATE INDEX ix_prerace_deadline_changes_race_id_created_at
ON prerace_deadline_changes(race_id, created_at);

CREATE INDEX ix_race_results_review_status
ON race_results(review_status);

CREATE INDEX ix_race_results_reported_by_user_id
ON race_results(reported_by_user_id);

CREATE INDEX ix_result_reviews_race_result_id_reviewed_at
ON result_reviews(race_result_id, reviewed_at);

-- Audit

CREATE INDEX ix_audit_logs_event_id_created_at
ON audit_logs(event_id, created_at);

CREATE INDEX ix_audit_logs_table_name_row_id
ON audit_logs(table_name, row_id);

-- ============================================
-- 4. PARTIAL UNIQUE INDEXES
-- ============================================

-- Tidak boleh ada penugasan aktif ganda.

CREATE UNIQUE INDEX uq_active_assignment_general
ON event_assignments(event_id, user_id, role_code)
WHERE category_id IS NULL AND is_active = TRUE;

CREATE UNIQUE INDEX uq_active_assignment_category
ON event_assignments(
    event_id,
    user_id,
    role_code,
    category_id
)
WHERE category_id IS NOT NULL AND is_active = TRUE;

-- Nomor start tidak boleh ganda dalam kategori.

CREATE UNIQUE INDEX uq_entry_start_number
ON category_entries(category_id, start_number)
WHERE start_number IS NOT NULL;

-- Identitas transaksi gateway tidak boleh ganda.

CREATE UNIQUE INDEX uq_payment_gateway_external_id
ON payments(gateway, external_payment_id)
WHERE external_payment_id IS NOT NULL;

-- Hanya satu match final per bracket.

CREATE UNIQUE INDEX uq_final_match_per_bracket
ON cup_matches(cup_bracket_id)
WHERE is_final = TRUE;

-- Seed Cup harus unik jika digunakan.

CREATE UNIQUE INDEX uq_cup_qualifier_seed
ON cup_qualifiers(cup_bracket_id, seed_number)
WHERE seed_number IS NOT NULL;

-- Heat eliminasi tidak boleh duplikat.

CREATE UNIQUE INDEX uq_elimination_heat_attempt
ON races(category_id, heat_number, attempt_number)
WHERE race_type = 'ELIMINATION'
  AND heat_number IS NOT NULL;

-- Percobaan race Cup harus unik per match.

CREATE UNIQUE INDEX uq_cup_race_attempt
ON races(cup_match_id, attempt_number)
WHERE race_type = 'CUP';

-- ============================================
-- 5. AUTOMATIC UPDATED_AT
-- ============================================

CREATE OR REPLACE FUNCTION fn_set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_users_updated_at
BEFORE UPDATE ON users
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_roles_updated_at
BEFORE UPDATE ON roles
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_role_access_updated_at
BEFORE UPDATE ON role_access
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_events_updated_at
BEFORE UPDATE ON events
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_event_assignments_updated_at
BEFORE UPDATE ON event_assignments
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_categories_updated_at
BEFORE UPDATE ON categories
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_category_permission_requests_updated_at
BEFORE UPDATE ON category_permission_requests
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_category_permissions_updated_at
BEFORE UPDATE ON category_permissions
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_rundowns_updated_at
BEFORE UPDATE ON rundowns
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_rundown_items_updated_at
BEFORE UPDATE ON rundown_items
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_participant_pics_updated_at
BEFORE UPDATE ON participant_pics
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_athletes_updated_at
BEFORE UPDATE ON athletes
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_registrations_updated_at
BEFORE UPDATE ON registrations
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_category_entries_updated_at
BEFORE UPDATE ON category_entries
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_invoices_updated_at
BEFORE UPDATE ON invoices
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_invoice_items_updated_at
BEFORE UPDATE ON invoice_items
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_payments_updated_at
BEFORE UPDATE ON payments
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_qr_passes_updated_at
BEFORE UPDATE ON qr_passes
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_checkpoints_updated_at
BEFORE UPDATE ON checkpoints
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_checkpoint_groups_updated_at
BEFORE UPDATE ON checkpoint_groups
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_opening_checkins_updated_at
BEFORE UPDATE ON opening_checkins
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_opening_checkin_athletes_updated_at
BEFORE UPDATE ON opening_checkin_athletes
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_cup_brackets_updated_at
BEFORE UPDATE ON cup_brackets
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_cup_matches_updated_at
BEFORE UPDATE ON cup_matches
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_races_updated_at
BEFORE UPDATE ON races
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_race_participants_updated_at
BEFORE UPDATE ON race_participants
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_prerace_checks_updated_at
BEFORE UPDATE ON prerace_checks
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_race_results_updated_at
BEFORE UPDATE ON race_results
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_cup_qualifiers_updated_at
BEFORE UPDATE ON cup_qualifiers
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

-- ============================================
-- 6. TWO-ACCOUNT RESULT VERIFICATION
-- ============================================

CREATE OR REPLACE FUNCTION fn_guard_result_review()
RETURNS TRIGGER AS $$
DECLARE
    originator BIGINT;
BEGIN
    SELECT reported_by_user_id
    INTO originator
    FROM race_results
    WHERE id = NEW.race_result_id;

    IF originator IS NULL THEN
        RAISE EXCEPTION
            'Race result % not found',
            NEW.race_result_id;
    END IF;

    IF NEW.reviewer_user_id = originator THEN
        RAISE EXCEPTION
            'Reviewer must differ from result submitter (user %)',
            originator;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_result_reviews_two_accounts
BEFORE INSERT OR UPDATE OF reviewer_user_id, race_result_id
ON result_reviews
FOR EACH ROW
EXECUTE FUNCTION fn_guard_result_review();

-- ============================================
-- 7. GUARD FINAL RACE RESULT
-- ============================================

CREATE OR REPLACE FUNCTION fn_guard_race_result_final()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'UPDATE' THEN

        IF OLD.review_status = 'FINAL' THEN

            IF (
                NEW.elapsed_ms,
                NEW.record_type,
                NEW.finish_position,
                NEW.reported_by_user_id,
                NEW.attempt_number
            ) IS DISTINCT FROM (
                OLD.elapsed_ms,
                OLD.record_type,
                OLD.finish_position,
                OLD.reported_by_user_id,
                OLD.attempt_number
            ) THEN

                RAISE EXCEPTION
                    'FINAL results are immutable; create a new attempt for corrections';

            END IF;

            IF NEW.review_status <> 'FINAL' THEN
                RAISE EXCEPTION
                    'A FINAL race result cannot be reverted';
            END IF;

        END IF;

    END IF;

    IF NEW.review_status = 'FINAL' THEN

        -- New results must first pass submission and review.
        IF TG_OP = 'INSERT' THEN
            RAISE EXCEPTION
                'Cannot insert a result directly as FINAL';
        END IF;

        IF OLD.review_status IS DISTINCT FROM 'FINAL' THEN

            IF NOT EXISTS (
                SELECT 1
                FROM result_reviews r
                WHERE r.race_result_id = NEW.id
                  AND r.decision = 'APPROVED'
                  AND r.reviewer_user_id <> NEW.reported_by_user_id
            ) THEN

                RAISE EXCEPTION
                    'Cannot finalize result without a second-account approval';

            END IF;

        END IF;

    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_race_results_final_guard
BEFORE INSERT OR UPDATE OF
    review_status,
    elapsed_ms,
    record_type,
    finish_position,
    reported_by_user_id,
    attempt_number
ON race_results
FOR EACH ROW
EXECUTE FUNCTION fn_guard_race_result_final();

-- ============================================
-- 8. CATEGORY PERMISSION VALIDATION
-- ============================================

CREATE OR REPLACE FUNCTION fn_guard_category_permission_date()
RETURNS TRIGGER AS $$
DECLARE
    v_event_date DATE;
BEGIN

    SELECT e.event_date
    INTO v_event_date
    FROM categories c
    JOIN events e ON e.id = c.event_id
    WHERE c.id = NEW.category_id;

    IF NEW.effective_date <> v_event_date THEN

        RAISE EXCEPTION
            'Permission effective_date must match event date';

    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_category_permission_date
BEFORE INSERT OR UPDATE OF category_id, effective_date
ON category_permissions
FOR EACH ROW
EXECUTE FUNCTION fn_guard_category_permission_date();