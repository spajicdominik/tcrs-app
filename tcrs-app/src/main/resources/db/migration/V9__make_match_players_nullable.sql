-- The elimination bracket is created up front, so a semi-final or final exists before
-- the matches feeding it have been played. Those slots hold no player yet, which means
-- player1_id / player2_id have to be nullable.
ALTER TABLE match
    ALTER COLUMN player1_id DROP NOT NULL,
    ALTER COLUMN player2_id DROP NOT NULL;
