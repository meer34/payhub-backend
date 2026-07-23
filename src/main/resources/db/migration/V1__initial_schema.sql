CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-------------------------------------------------------
-- Roles
-------------------------------------------------------

CREATE TABLE roles
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    name VARCHAR(50) NOT NULL UNIQUE,

    description VARCHAR(255),

    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-------------------------------------------------------
-- Users
-------------------------------------------------------

CREATE TABLE users
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    first_name VARCHAR(100) NOT NULL,

    last_name VARCHAR(100),

    email VARCHAR(255) NOT NULL UNIQUE,

    mobile VARCHAR(20),

    password VARCHAR(255) NOT NULL,

    enabled BOOLEAN NOT NULL DEFAULT TRUE,

    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,

    email_verified BOOLEAN NOT NULL DEFAULT FALSE,

    mobile_verified BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-------------------------------------------------------
-- User Roles
-------------------------------------------------------

CREATE TABLE user_roles
(
    user_id UUID NOT NULL,

    role_id UUID NOT NULL,

    PRIMARY KEY(user_id, role_id),

    CONSTRAINT fk_user_roles_user
        FOREIGN KEY(user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_user_roles_role
        FOREIGN KEY(role_id)
        REFERENCES roles(id)
        ON DELETE CASCADE
);

-------------------------------------------------------
-- Refresh Tokens
-------------------------------------------------------

CREATE TABLE refresh_tokens
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    token VARCHAR(512) NOT NULL UNIQUE,

    user_id UUID NOT NULL,

    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,

    revoked BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_refresh_user
        FOREIGN KEY(user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-------------------------------------------------------
-- Indexes
-------------------------------------------------------

CREATE INDEX idx_users_email
ON users(email);

CREATE INDEX idx_refresh_user
ON refresh_tokens(user_id);

CREATE INDEX idx_refresh_token
ON refresh_tokens(token);

-------------------------------------------------------
-- Seed Roles
-------------------------------------------------------

INSERT INTO roles(name, description)
VALUES
('ROLE_ADMIN', 'System Administrator'),
('ROLE_USER', 'Application User');
