package com.mir.payhub.auth.entity;

import com.mir.payhub.common.entity.BaseEntity;
import com.mir.payhub.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken extends BaseEntity {

    @Column(nullable = false, unique = true, length = 512)
    private String tokenHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String deviceName;

    private String ipAddress;

    private OffsetDateTime lastUsedAt;

    private OffsetDateTime expiresAt;

    private boolean revoked;

}