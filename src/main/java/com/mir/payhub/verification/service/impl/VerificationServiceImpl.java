package com.mir.payhub.verification.service.impl;

import com.mir.payhub.exception.BadRequestException;
import com.mir.payhub.exception.ResourceNotFoundException;
import com.mir.payhub.profile.entity.Profile;
import com.mir.payhub.profile.enums.OnboardingStatus;
import com.mir.payhub.profile.enums.ProfileType;
import com.mir.payhub.profile.repository.ProfileRepository;
import com.mir.payhub.security.service.CustomUserPrincipal;
import com.mir.payhub.user.entity.User;
import com.mir.payhub.verification.dto.response.VerificationResponse;
import com.mir.payhub.verification.entity.Verification;
import com.mir.payhub.verification.enums.VerificationStatus;
import com.mir.payhub.verification.enums.VerificationType;
import com.mir.payhub.verification.repository.VerificationRepository;
import com.mir.payhub.verification.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {

    private final ProfileRepository profileRepository;
    private final VerificationRepository verificationRepository;

    @Override
    @Transactional(readOnly = true)
    public VerificationResponse getCurrentVerification() {
        Profile profile = currentProfile();
        return verificationRepository.findByProfileId(profile.getId())
                .map(this::toResponse)
                .orElseGet(() -> response(null, typeFor(profile), VerificationStatus.NOT_STARTED, null, null));
    }

    @Override
    @Transactional
    public VerificationResponse startCurrentVerification() {
        Profile profile = currentProfile();
        if (profile.getOnboardingStatus() != OnboardingStatus.PROFILE_COMPLETE) {
            throw new BadRequestException("Complete the profile before starting verification");
        }

        Verification verification = verificationRepository.findByProfileId(profile.getId())
                .orElseGet(() -> Verification.builder()
                        .profile(profile)
                        .verificationType(typeFor(profile))
                        .status(VerificationStatus.NOT_STARTED)
                        .build());

        if (verification.getStatus() == VerificationStatus.PENDING
                || verification.getStatus() == VerificationStatus.VERIFIED) {
            throw new BadRequestException("Verification is already in progress or complete");
        }

        verification.setStatus(VerificationStatus.PENDING);
        return toResponse(verificationRepository.save(verification));
    }

    private Profile currentProfile() {
        return profileRepository.findByUserId(currentUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
    }

    private User currentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof CustomUserPrincipal customUserPrincipal)) {
            throw new IllegalStateException("Authenticated user principal is unavailable");
        }
        return customUserPrincipal.getUser();
    }

    private VerificationType typeFor(Profile profile) {
        return profile.getProfileType() == ProfileType.BUSINESS ? VerificationType.KYB : VerificationType.KYC;
    }

    private VerificationResponse toResponse(Verification verification) {
        return response(
                verification.getId(),
                verification.getVerificationType(),
                verification.getStatus(),
                verification.getProvider(),
                verification.getProviderReference()
        );
    }

    private VerificationResponse response(UUID id, VerificationType type, VerificationStatus status,
                                          String provider, String providerReference) {
        return VerificationResponse.builder()
                .id(id)
                .verificationType(type)
                .status(status)
                .provider(provider)
                .providerReference(providerReference)
                .build();
    }
}
