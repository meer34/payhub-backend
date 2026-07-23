ALTER TABLE refresh_tokens
DROP CONSTRAINT refresh_tokens_token_key;

ALTER TABLE refresh_tokens
RENAME COLUMN token TO token_hash;

ALTER TABLE refresh_tokens
ALTER COLUMN token_hash TYPE VARCHAR(255);

ALTER TABLE refresh_tokens
ADD COLUMN device_name VARCHAR(150);

ALTER TABLE refresh_tokens
ADD COLUMN ip_address VARCHAR(100);

ALTER TABLE refresh_tokens
ADD COLUMN last_used_at TIMESTAMP WITH TIME ZONE;

CREATE INDEX idx_refresh_token_user
ON refresh_tokens(user_id);

CREATE INDEX idx_refresh_token_hash
ON refresh_tokens(token_hash);