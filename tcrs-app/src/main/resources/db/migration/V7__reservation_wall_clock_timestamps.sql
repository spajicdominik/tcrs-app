-- The app serves a single court in Croatia, so reservation times are wall-clock
-- values, not timezone-aware instants. Convert timestamptz -> timestamp, keeping
-- the clock reading intact (an 08:00Z instant becomes the wall time 08:00).
ALTER TABLE reservation
    ALTER COLUMN time_start    TYPE timestamp USING time_start    AT TIME ZONE 'UTC',
    ALTER COLUMN time_end      TYPE timestamp USING time_end      AT TIME ZONE 'UTC',
    ALTER COLUMN date_modified TYPE timestamp USING date_modified AT TIME ZONE 'UTC';

-- keep the column default zone-free too
ALTER TABLE reservation
    ALTER COLUMN date_modified SET DEFAULT LOCALTIMESTAMP;
