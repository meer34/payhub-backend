ALTER TABLE refresh_tokens
    ALTER COLUMN token_hash TYPE VARCHAR(512),
    ALTER COLUMN device_name TYPE VARCHAR(150),
    ALTER COLUMN ip_address TYPE VARCHAR(100),
    ALTER COLUMN expires_at SET NOT NULL;

ALTER TABLE refresh_tokens
    ADD CONSTRAINT uq_refresh_tokens_token_hash UNIQUE (token_hash);

CREATE UNIQUE INDEX uq_refresh_tokens_active_user
    ON refresh_tokens (user_id)
    WHERE revoked = false;

ALTER TABLE users
    ADD CONSTRAINT uq_users_mobile UNIQUE (mobile);

CREATE TABLE profiles
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE,
    profile_type VARCHAR(20) NOT NULL,
    onboarding_status VARCHAR(30) NOT NULL,
    name VARCHAR(150),
    country VARCHAR(2),
    date_of_birth DATE,
    legal_business_name VARCHAR(200),
    business_type VARCHAR(100),
    registration_number VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_profiles_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_profiles_type
        CHECK (profile_type IN ('PERSONAL', 'BUSINESS')),

    CONSTRAINT chk_profiles_onboarding_status
        CHECK (
            onboarding_status IN (
                'PROFILE_INCOMPLETE',
                'PROFILE_COMPLETE'
            )
        )
);