package com.mir.payhub.auth.entity;

import com.mir.payhub.common.entity.BaseEntity;
import com.mir.payhub.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken extends BaseEntity {

    @Column(nullable = false, unique = true, length = 512)
    private String tokenHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 150)
    private String deviceName;

    @Column(length = 100)
    private String ipAddress;

    private OffsetDateTime lastUsedAt;

    @Column(nullable = false)
    private OffsetDateTime expiresAt;

    private boolean revoked;

}
