-- V3: uppercase the remaining enum CHECK constraints + defaults so they match
-- the Java enum names stored by @Enumerated(EnumType.STRING).
--   * app_user.role  was missed in V2
--   * tournament.phase and reservation.match_type are now mapped as Java enums

-- app_user.role
ALTER TABLE app_user DROP CONSTRAINT app_user_role_check;
ALTER TABLE app_user ADD CONSTRAINT app_user_role_check
    CHECK (role IN ('PLAYER', 'ADMIN'));
ALTER TABLE app_user ALTER COLUMN role SET DEFAULT 'PLAYER';

-- tournament.phase
ALTER TABLE tournament DROP CONSTRAINT tournament_phase_check;
ALTER TABLE tournament ADD CONSTRAINT tournament_phase_check
    CHECK (phase IN ('GROUP', 'ELIMINATION', 'FINISHED', 'CLOSED'));
ALTER TABLE tournament ALTER COLUMN phase SET DEFAULT 'GROUP';

-- reservation.match_type
ALTER TABLE reservation DROP CONSTRAINT reservation_match_type_check;
ALTER TABLE reservation ADD CONSTRAINT reservation_match_type_check
    CHECK (match_type IN ('FRIENDLY', 'TOURNAMENT'));
ALTER TABLE reservation ALTER COLUMN match_type SET DEFAULT 'FRIENDLY';
