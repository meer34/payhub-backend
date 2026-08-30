package com.mir.payhub.profile.dto.request;

import com.mir.payhub.profile.enums.ProfileType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ProfileCreateRequest {

    @NotNull(message = "Profile type is required")
    private ProfileType profileType;

    @Size(max = 50, message = "Tax ID cannot exceed 50 characters")
    private String taxId;

    @Valid
    private AddressRequest address; // Replaced String country with the nested object

    // --- Personal Profile Fields ---
    @Size(max = 150, message = "Name cannot exceed 150 characters")
    private String name;

    private LocalDate dateOfBirth;

    @Size(max = 100, message = "Nationality cannot exceed 100 characters")
    private String nationality;

    @Size(max = 100, message = "Occupation cannot exceed 100 characters")
    private String occupation;

    // --- Business Profile Fields ---
    @Size(max = 200, message = "Legal business name cannot exceed 200 characters")
    private String legalBusinessName;

    @Size(max = 100, message = "Business type cannot exceed 100 characters")
    private String businessType;

    @Size(max = 100, message = "Registration number cannot exceed 100 characters")
    private String registrationNumber;

    @Size(max = 150, message = "Industry description cannot exceed 150 characters")
    private String industry;

    @Size(max = 255, message = "Website URL cannot exceed 255 characters")
    private String website;
}
