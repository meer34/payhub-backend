CREATE TABLE verifications
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    profile_id UUID NOT NULL UNIQUE,
    verification_type VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL,
    provider VARCHAR(50),
    provider_reference VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_verifications_profile FOREIGN KEY (profile_id) REFERENCES profiles(id) ON DELETE CASCADE,
    CONSTRAINT chk_verifications_type CHECK (verification_type IN ('KYC', 'KYB')),
    CONSTRAINT chk_verifications_status CHECK (status IN ('PENDING', 'VERIFIED', 'REJECTED'))
);
