package com.mir.payhub.profile.dto.request;

import com.mir.payhub.profile.enums.ProfileType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ProfileCreateRequest {

    @NotNull
    private ProfileType profileType;

    @Size(max = 150)
    private String name;

    @Pattern(regexp = "^[A-Za-z]{2}$", message = "Country must be a two-letter code")
    private String country;

    private LocalDate dateOfBirth;

    @Size(max = 200)
    private String legalBusinessName;

    @Size(max = 100)
    private String businessType;

    @Size(max = 100)
    private String registrationNumber;
}
