ALTER TABLE reservation
    ALTER COLUMN time_start    TYPE timestamp USING time_start    AT TIME ZONE 'UTC',
    ALTER COLUMN time_end      TYPE timestamp USING time_end      AT TIME ZONE 'UTC',
    ALTER COLUMN date_modified TYPE timestamp USING date_modified AT TIME ZONE 'UTC';

ALTER TABLE reservation
    ALTER COLUMN date_modified SET DEFAULT LOCALTIMESTAMP;
