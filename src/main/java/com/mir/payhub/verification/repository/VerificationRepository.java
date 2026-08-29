package com.mir.payhub.verification.repository;

import com.mir.payhub.verification.entity.Verification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VerificationRepository extends JpaRepository<Verification, UUID> {
    Optional<Verification> findByProfileId(UUID profileId);
}
