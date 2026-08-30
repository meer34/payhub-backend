package com.mir.payhub.profile.dto.response;

import com.mir.payhub.profile.enums.OnboardingStatus;
import com.mir.payhub.profile.enums.ProfileType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class ProfileResponse {
    private UUID id;
    private ProfileType profileType;
    private OnboardingStatus onboardingStatus;

    // --- Shared Fields ---
    private String taxId;
    private AddressResponse address; // Changed from String country to a nested object

    // --- Personal Profile Fields ---
    private String name;
    private LocalDate dateOfBirth;
    private String nationality;
    private String occupation;

    // --- Business Profile Fields ---
    private String legalBusinessName;
    private String businessType;
    private String registrationNumber;
    private String industry;
    private String website;
}
