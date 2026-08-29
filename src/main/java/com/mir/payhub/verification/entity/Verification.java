package com.mir.payhub.verification.entity;

import com.mir.payhub.common.entity.BaseEntity;
import com.mir.payhub.profile.entity.Profile;
import com.mir.payhub.verification.enums.VerificationStatus;
import com.mir.payhub.verification.enums.VerificationType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "verifications")
public class Verification extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false, unique = true)
    private Profile profile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private VerificationType verificationType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VerificationStatus status;

    @Column(length = 50)
    private String provider;

    @Column(length = 255)
    private String providerReference;
}
