ALTER TABLE app_user DROP CONSTRAINT app_user_status_check;
ALTER TABLE app_user ADD CONSTRAINT app_user_status_check
    CHECK (status IN ('PENDING', 'ACTIVE', 'DEACTIVATED'));
ALTER TABLE app_user ALTER COLUMN status SET DEFAULT 'PENDING';