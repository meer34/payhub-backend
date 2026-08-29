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
    private String name;
    private String country;
    private LocalDate dateOfBirth;
    private String legalBusinessName;
    private String businessType;
    private String registrationNumber;
}
