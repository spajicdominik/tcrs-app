-- Until now a match reached its tournament only through group_id. That breaks for the
-- elimination phase, where group_id is null by design (a quarter-final belongs to no
-- group), leaving those rows with no way back to their tournament at all.
--
-- match.tournament_id becomes the real link; group_id keeps its existing meaning of
-- "which group this belongs to, if it is a group match".

ALTER TABLE match
    ADD COLUMN tournament_id BIGINT REFERENCES tournament(id);

-- Backfill: every existing match is a group match, so its tournament is the one its
-- group already points at. Done before the NOT NULL below, which would otherwise fail.
UPDATE match m
SET tournament_id = g.tournament_id
FROM tournament_group g
WHERE m.group_id = g.id
  AND m.tournament_id IS NULL;

ALTER TABLE match
    ALTER COLUMN tournament_id SET NOT NULL;

-- Every read of the bracket and the "are all group matches played?" check filter on
-- this column, so it earns an index.
CREATE INDEX idx_match_tournament_id ON match (tournament_id);
