package com.mir.payhub.profile.service.impl;

import com.mir.payhub.exception.BadRequestException;
import com.mir.payhub.exception.ResourceNotFoundException;
import com.mir.payhub.profile.dto.request.ProfileCreateRequest;
import com.mir.payhub.profile.dto.request.ProfileUpdateRequest;
import com.mir.payhub.profile.dto.response.ProfileResponse;
import com.mir.payhub.profile.entity.Profile;
import com.mir.payhub.profile.enums.OnboardingStatus;
import com.mir.payhub.profile.enums.ProfileType;
import com.mir.payhub.profile.repository.ProfileRepository;
import com.mir.payhub.profile.service.ProfileService;
import com.mir.payhub.security.service.CustomUserPrincipal;
import com.mir.payhub.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;

    @Override
    @Transactional
    public ProfileResponse create(ProfileCreateRequest request) {
        User user = currentUser();
        if (profileRepository.existsByUserId(user.getId())) {
            throw new BadRequestException("Profile already exists");
        }

        Profile profile = Profile.builder()
                .user(user)
                .profileType(request.getProfileType())
                .name(request.getName())
                .country(normalizeCountry(request.getCountry()))
                .dateOfBirth(request.getDateOfBirth())
                .legalBusinessName(request.getLegalBusinessName())
                .businessType(request.getBusinessType())
                .registrationNumber(request.getRegistrationNumber())
                .build();
        profile.setOnboardingStatus(determineStatus(profile));
        return toResponse(profileRepository.save(profile));
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getCurrentProfile() {
        return toResponse(currentProfile());
    }

    @Override
    @Transactional
    public ProfileResponse update(ProfileUpdateRequest request) {
        Profile profile = currentProfile();
        if (request.getName() != null) profile.setName(request.getName());
        if (request.getCountry() != null) profile.setCountry(normalizeCountry(request.getCountry()));
        if (request.getDateOfBirth() != null) profile.setDateOfBirth(request.getDateOfBirth());
        if (request.getLegalBusinessName() != null) profile.setLegalBusinessName(request.getLegalBusinessName());
        if (request.getBusinessType() != null) profile.setBusinessType(request.getBusinessType());
        if (request.getRegistrationNumber() != null) profile.setRegistrationNumber(request.getRegistrationNumber());
        profile.setOnboardingStatus(determineStatus(profile));
        return toResponse(profile);
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

    private OnboardingStatus determineStatus(Profile profile) {
        boolean complete = profile.getProfileType() == ProfileType.PERSONAL
                ? hasText(profile.getName()) && hasText(profile.getCountry()) && profile.getDateOfBirth() != null
                : hasText(profile.getLegalBusinessName()) && hasText(profile.getBusinessType())
                && hasText(profile.getCountry()) && hasText(profile.getRegistrationNumber());
        return complete ? OnboardingStatus.PROFILE_COMPLETE : OnboardingStatus.PROFILE_INCOMPLETE;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String normalizeCountry(String country) {
        return country == null ? null : country.toUpperCase(Locale.ROOT);
    }

    private ProfileResponse toResponse(Profile profile) {
        return ProfileResponse.builder()
                .id(profile.getId())
                .profileType(profile.getProfileType())
                .onboardingStatus(profile.getOnboardingStatus())
                .name(profile.getName())
                .country(profile.getCountry())
                .dateOfBirth(profile.getDateOfBirth())
                .legalBusinessName(profile.getLegalBusinessName())
                .businessType(profile.getBusinessType())
                .registrationNumber(profile.getRegistrationNumber())
                .build();
    }
}
