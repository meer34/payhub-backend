package com.mir.payhub.security.jwt;

import com.mir.payhub.security.service.CustomUserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties properties;

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(
                properties.secret().getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateAccessToken(CustomUserPrincipal user) {

        Date now = new Date();

        Date expiry = new Date(
                now.getTime() + properties.accessTokenExpiration()
        );

        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(now)
                .expiration(expiry)
                .claim("uid", user.getId().toString())
                .signWith(signingKey())
                .compact();
    }

    public String generateRefreshToken(CustomUserPrincipal user) {

        Date now = new Date();

        Date expiry = new Date(
                now.getTime() + properties.refreshTokenExpiration()
        );

        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(
            String token,
            Function<Claims, T> resolver
    ) {
        return resolver.apply(extractAllClaims(token));
    }

    public Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(
            String token,
            CustomUserPrincipal user
    ) {
        return extractUsername(token).equals(user.getUsername())
                && !extractAllClaims(token).getExpiration().before(new Date());
    }

}