package com.mir.payhub.auth.service;

import com.mir.payhub.auth.entity.RefreshToken;
import com.mir.payhub.auth.repository.RefreshTokenRepository;
import com.mir.payhub.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Generates a raw refresh token.
     */
    public String generateRawToken() {
        return UUID.randomUUID() + "." + UUID.randomUUID();
    }

    /**
     * Stores only the hashed refresh token.
     */
    public RefreshToken create(User user) {

        refreshTokenRepository.deleteByUser(user);

        String rawToken = generateRawToken();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(passwordEncoder.encode(rawToken));
        refreshToken.setLastUsedAt(OffsetDateTime.now());
        refreshToken.setRevoked(false);

        refreshTokenRepository.save(refreshToken);

        /*
         * IMPORTANT:
         * We return the raw token only once.
         * The client stores it.
         * Database stores only the hash.
         */
        refreshToken.setTokenHash(rawToken);

        return refreshToken;
    }

    /**
     * Verify a presented refresh token.
     */
    public boolean matches(
            RefreshToken stored,
            String presentedToken
    ) {
        return passwordEncoder.matches(
                presentedToken,
                stored.getTokenHash()
        );
    }
}