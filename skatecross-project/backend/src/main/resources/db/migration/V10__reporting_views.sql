-- ============================================
-- KKP SKATE CROSS
-- MIGRATION V10: REPORTING VIEWS
-- ============================================

-- ============================================
-- 1. ELIMINATION LEADERBOARD
-- ============================================

CREATE OR REPLACE VIEW v_elimination_leaderboard AS
WITH valid_times AS (
    SELECT
        ca.id AS category_id,
        ce.id AS category_entry_id,
        a.id AS athlete_id,
        a.full_name AS athlete_name,
        MIN(rr.elapsed_ms) AS best_time_ms

    FROM race_results rr

    JOIN race_participants rp
        ON rp.id = rr.race_participant_id

    JOIN races r
        ON r.id = rp.race_id

    JOIN categories ca
        ON ca.id = r.category_id

    JOIN category_entries ce
        ON ce.id = rp.category_entry_id

    JOIN athletes a
        ON a.id = ce.athlete_id

    WHERE r.race_type = 'ELIMINATION'
      AND rr.review_status = 'FINAL'
      AND rr.record_type = 'TIME'

    GROUP BY
        ca.id,
        ce.id,
        a.id,
        a.full_name
)
SELECT
    category_id,
    category_entry_id,
    athlete_id,
    athlete_name,
    best_time_ms,

    DENSE_RANK() OVER (
        PARTITION BY category_id
        ORDER BY best_time_ms ASC
    ) AS rank_in_category

FROM valid_times;

-- ============================================
-- 2. CATEGORY CHAMPIONS
-- ============================================

CREATE OR REPLACE VIEW v_category_champions AS
SELECT
    c.event_id,
    c.id AS category_id,
    c.name AS category_name,

    cm.id AS final_match_id,
    cm.winner_entry_id,

    a.id AS champion_athlete_id,
    a.full_name AS champion_name

FROM cup_matches cm

JOIN cup_brackets b
    ON b.id = cm.cup_bracket_id

JOIN categories c
    ON c.id = b.category_id

LEFT JOIN category_entries e
    ON e.id = cm.winner_entry_id

LEFT JOIN athletes a
    ON a.id = e.athlete_id

WHERE cm.is_final = TRUE
  AND cm.status = 'FINAL';

-- ============================================
-- 3. EVENT CHECK-IN COUNTS
-- ============================================

CREATE OR REPLACE VIEW v_event_checkin_counts AS
SELECT
    e.id AS event_id,
    e.name AS event_name,

    COUNT(DISTINCT oca.id)
        AS checked_in_athlete_count,

    COUNT(DISTINCT oc.id)
        AS checked_in_registration_count

FROM events e

LEFT JOIN registrations rg
    ON rg.event_id = e.id

LEFT JOIN opening_checkins oc
    ON oc.registration_id = rg.id

LEFT JOIN opening_checkin_athletes oca
    ON oca.opening_checkin_id = oc.id

GROUP BY
    e.id,
    e.name;

-- ============================================
-- 4. REGISTRATION BILLING
-- ============================================

CREATE OR REPLACE VIEW v_registration_billing AS
SELECT
    r.id AS registration_id,
    r.event_id,
    r.registration_code,

    r.status AS registration_status,

    i.invoice_code,
    i.total_amount,
    i.status AS invoice_status,

    (
        SELECT MAX(p.paid_at)
        FROM payments p
        WHERE p.invoice_id = i.id
          AND p.status = 'SUCCEEDED'
    ) AS latest_paid_at

FROM registrations r

LEFT JOIN invoices i
    ON i.registration_id = r.id;