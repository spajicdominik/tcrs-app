-- The club now wants a second knockout format. In PER_POSITION every finishing place
-- gets its own bracket: all group winners play off for first, all runners-up for
-- second, and so on, so nobody stops playing when the groups end.
--
-- Existing tournaments keep the original single-bracket behaviour.
ALTER TABLE tournament
    ADD COLUMN elimination_format VARCHAR(20) NOT NULL DEFAULT 'SINGLE_BRACKET';

-- Which place a knockout match is being played for: 1 for the winners' bracket, 2 for
-- the runners-up, and so on. NULL for group matches and for the single-bracket format,
-- where there is only one thing to play for.
ALTER TABLE match
    ADD COLUMN bracket_position INTEGER;
