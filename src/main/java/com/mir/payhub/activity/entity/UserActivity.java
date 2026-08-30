package com.mir.payhub.activity.entity;

import com.mir.payhub.activity.enums.ActivityType;
import com.mir.payhub.common.entity.BaseEntity;
import com.mir.payhub.profile.entity.Profile;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_activity")
public class UserActivity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ActivityType type;

    @Column(nullable = false, length = 3)
    private String title;

    @Column(length = 100)
    private String description;

    @Column(length = 100)
    private String status;

    @Column(length = 100)
    private String referenceId;
}
