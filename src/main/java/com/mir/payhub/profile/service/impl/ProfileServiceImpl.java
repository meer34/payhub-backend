package com.mir.payhub.profile.service.impl;

import com.mir.payhub.exception.BadRequestException;
import com.mir.payhub.exception.ResourceNotFoundException;
import com.mir.payhub.profile.dto.request.ProfileCreateRequest;
import com.mir.payhub.profile.dto.request.ProfileUpdateRequest;
import com.mir.payhub.profile.dto.response.ProfileResponse;
import com.mir.payhub.profile.entity.Address;
import com.mir.payhub.profile.entity.Profile;
import com.mir.payhub.profile.enums.OnboardingStatus;
import com.mir.payhub.profile.enums.ProfileType;
import com.mir.payhub.profile.repository.ProfileRepository;
import com.mir.payhub.profile.service.ProfileService;
import com.mir.payhub.security.service.CustomUserPrincipal;
import com.mir.payhub.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
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
                .taxId(request.getTaxId())
                // Personal fields
                .name(request.getName())
                .dateOfBirth(request.getDateOfBirth())
                .nationality(request.getNationality())
                .occupation(request.getOccupation())
                // Business fields
                .legalBusinessName(request.getLegalBusinessName())
                .businessType(request.getBusinessType())
                .registrationNumber(request.getRegistrationNumber())
                .industry(request.getIndustry())
                .website(request.getWebsite())
                // Map the embedded address object
                .address(mapAddress(request.getAddress()))
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

        // Update Shared fields
        if (request.getTaxId() != null) profile.setTaxId(request.getTaxId());

        // Update Personal fields
        if (request.getName() != null) profile.setName(request.getName());
        if (request.getDateOfBirth() != null) profile.setDateOfBirth(request.getDateOfBirth());
        if (request.getNationality() != null) profile.setNationality(request.getNationality());
        if (request.getOccupation() != null) profile.setOccupation(request.getOccupation());

        // Update Business fields
        if (request.getLegalBusinessName() != null) profile.setLegalBusinessName(request.getLegalBusinessName());
        if (request.getBusinessType() != null) profile.setBusinessType(request.getBusinessType());
        if (request.getRegistrationNumber() != null) profile.setRegistrationNumber(request.getRegistrationNumber());
        if (request.getIndustry() != null) profile.setIndustry(request.getIndustry());
        if (request.getWebsite() != null) profile.setWebsite(request.getWebsite());

        // Update Embedded Address fields safely
        if (request.getAddress() != null) {
            var reqAddr = request.getAddress();
            if (profile.getAddress() == null) {
                profile.setAddress(new Address());
            }
            Address currentAddr = profile.getAddress();
            if (reqAddr.getStreet() != null) currentAddr.setStreet(reqAddr.getStreet());
            if (reqAddr.getCity() != null) currentAddr.setCity(reqAddr.getCity());
            if (reqAddr.getState() != null) currentAddr.setState(reqAddr.getState());
            if (reqAddr.getPostalCode() != null) currentAddr.setPostalCode(reqAddr.getPostalCode());
            if (reqAddr.getCountry() != null) currentAddr.setCountry(normalizeCountry(reqAddr.getCountry()));
        }

        profile.setOnboardingStatus(determineStatus(profile));
        return toResponse(profile);
    }

    public Profile currentProfile() {
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
        log.info("Determining onboarding status for profile type: {}", profile.getProfileType());

        // Base validations shared by both profiles
        boolean structureValid = profile.getAddress() != null && hasText(profile.getAddress().getCountry()) && hasText(profile.getTaxId());
        if (!structureValid) {
            return OnboardingStatus.PROFILE_INCOMPLETE;
        }

        boolean complete;
        Address addr = profile.getAddress();

        if (profile.getProfileType() == ProfileType.PERSONAL) {
            complete = hasText(profile.getUser().getFirstName())
                    && hasText(profile.getUser().getLastName())
                    && hasText(profile.getTaxId())
                    && profile.getDateOfBirth() != null
                    && hasText(profile.getNationality())
                    && hasText(profile.getOccupation())
                    && hasText(addr.getStreet())
                    && hasText(addr.getCity());
        } else {
            complete = hasText(profile.getLegalBusinessName())
                    && hasText(profile.getBusinessType())
                    && hasText(profile.getRegistrationNumber())
                    && hasText(profile.getIndustry())
                    && hasText(addr.getStreet())
                    && hasText(addr.getCity());
        }

        return complete ? OnboardingStatus.PROFILE_COMPLETE : OnboardingStatus.PROFILE_INCOMPLETE;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String normalizeCountry(String country) {
        return country == null ? null : country.toUpperCase(Locale.ROOT);
    }

    private Address mapAddress(com.mir.payhub.profile.dto.request.AddressRequest requestAddress) {
        if (requestAddress == null) return null;
        return Address.builder()
                .street(requestAddress.getStreet())
                .city(requestAddress.getCity())
                .state(requestAddress.getState())
                .postalCode(requestAddress.getPostalCode())
                .country(normalizeCountry(requestAddress.getCountry()))
                .build();
    }

    private ProfileResponse toResponse(Profile profile) {
        // Safe mapping for embedded components to avoid NullPointerException
        com.mir.payhub.profile.dto.response.AddressResponse addressResp = null;
        if (profile.getAddress() != null) {
            Address addr = profile.getAddress();
            addressResp = com.mir.payhub.profile.dto.response.AddressResponse.builder()
                    .street(addr.getStreet())
                    .city(addr.getCity())
                    .state(addr.getState())
                    .postalCode(addr.getPostalCode())
                    .country(addr.getCountry())
                    .build();
        }

        return ProfileResponse.builder()
                .id(profile.getId())
                .profileType(profile.getProfileType())
                .onboardingStatus(profile.getOnboardingStatus())
                .taxId(profile.getTaxId())
                // Personal fields
                .name(profile.getName())
                .dateOfBirth(profile.getDateOfBirth())
                .nationality(profile.getNationality())
                .occupation(profile.getOccupation())
                // Business fields
                .legalBusinessName(profile.getLegalBusinessName())
                .businessType(profile.getBusinessType())
                .registrationNumber(profile.getRegistrationNumber())
                .industry(profile.getIndustry())
                .website(profile.getWebsite())
                // Nested layout response
                .address(addressResp)
                .build();
    }
}
