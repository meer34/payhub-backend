package com.mir.payhub.auth.service;

import com.mir.payhub.auth.entity.RefreshToken;
import com.mir.payhub.auth.repository.RefreshTokenRepository;
import com.mir.payhub.common.util.HashUtils;
import com.mir.payhub.security.jwt.JwtService;
import com.mir.payhub.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public String create(User user, String deviceName, String ipAddress) {

        String refreshToken = jwtService.generateRefreshToken(user);

        RefreshToken entity = RefreshToken.builder()
                .user(user)
                .tokenHash(HashUtils.sha256(refreshToken))
                .deviceName(deviceName)
                .ipAddress(ipAddress)
                .expiresAt(jwtService.extractExpiration(refreshToken))
                .lastUsedAt(OffsetDateTime.now())
                .revoked(false)
                .build();

        refreshTokenRepository.save(entity);

        return refreshToken;
    }

    @Transactional(readOnly = true)
    public RefreshToken findByToken(String refreshToken) {

        return refreshTokenRepository
                .findByTokenHash(HashUtils.sha256(refreshToken))
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public RefreshToken findByTokenWithLock(String refreshToken) {

        return refreshTokenRepository
                .findWithLockByTokenHash(HashUtils.sha256(refreshToken))
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public boolean isValid(String refreshToken, RefreshToken entity) {

        if (entity == null) {
            return false;
        }

        if (entity.isRevoked()) {
            return false;
        }

        if (entity.getExpiresAt().isBefore(OffsetDateTime.now())) {
            return false;
        }

        return HashUtils.sha256(refreshToken)
                .equals(entity.getTokenHash());
    }

    @Transactional
    public void revoke(User user) {

        refreshTokenRepository.findByUserAndRevokedFalse(user)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.saveAndFlush(token);
                });
    }

    @Transactional
    public void revoke(RefreshToken refreshToken) {

        refreshToken.setRevoked(true);
        refreshTokenRepository.saveAndFlush(refreshToken);
    }

    @Transactional
    public void updateLastUsed(RefreshToken refreshToken) {

        refreshToken.setLastUsedAt(OffsetDateTime.now());
        refreshTokenRepository.save(refreshToken);
    }
}