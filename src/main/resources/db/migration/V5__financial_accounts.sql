CREATE TABLE financial_accounts
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    profile_id UUID NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_financial_accounts_profile FOREIGN KEY (profile_id) REFERENCES profiles(id) ON DELETE CASCADE,
    CONSTRAINT uq_financial_accounts_profile_currency UNIQUE (profile_id, currency),
    CONSTRAINT chk_financial_accounts_currency CHECK (currency ~ '^[A-Z]{3}$'),
    CONSTRAINT chk_financial_accounts_status CHECK (status IN ('ACTIVE', 'SUSPENDED', 'CLOSED'))
);
