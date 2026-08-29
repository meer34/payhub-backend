package com.mir.payhub.profile.entity;

import com.mir.payhub.common.entity.BaseEntity;
import com.mir.payhub.profile.enums.OnboardingStatus;
import com.mir.payhub.profile.enums.ProfileType;
import com.mir.payhub.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "profiles")
public class Profile extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProfileType profileType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OnboardingStatus onboardingStatus;

    @Column(length = 150)
    private String name;

    @Column(length = 2)
    private String country;

    private LocalDate dateOfBirth;

    @Column(length = 200)
    private String legalBusinessName;

    @Column(length = 100)
    private String businessType;

    @Column(length = 100)
    private String registrationNumber;
}
