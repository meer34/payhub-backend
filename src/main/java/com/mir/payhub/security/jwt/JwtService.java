package com.mir.payhub.security.jwt;

import com.mir.payhub.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    public static final String TOKEN_TYPE_CLAIM = "token_type";
    private static final String ACCESS_TOKEN_TYPE = "ACCESS";
    private static final String REFRESH_TOKEN_TYPE = "REFRESH";

    private final JwtProperties jwtProperties;

    public String generateAccessToken(User user) {
        return generateAccessToken(new HashMap<>(), user);
    }

    public String generateAccessToken(Map<String, Object> extraClaims, User user) {
        return buildToken(
                extraClaims,
                user,
                jwtProperties.accessTokenExpiration(),
                ACCESS_TOKEN_TYPE
        );
    }

    public String generateRefreshToken(User user) {
        return buildToken(
                new HashMap<>(),
                user,
                jwtProperties.refreshTokenExpiration(),
                REFRESH_TOKEN_TYPE
        );
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            User user,
            long expiration,
            String tokenType
    ) {

        return Jwts.builder()
                .claims(extraClaims)
                .claim(TOKEN_TYPE_CLAIM, tokenType)
                .id(UUID.randomUUID().toString())
                .subject(user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public OffsetDateTime extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration)
                .toInstant()
                .atOffset(ZoneOffset.UTC);
    }

    public <T> T extractClaim(
            String token,
            Function<Claims, T> resolver
    ) {

        Claims claims = extractAllClaims(token);

        return resolver.apply(claims);
    }

    public boolean isTokenValid(
            String token,
            UserDetails userDetails
    ) {

        String username = extractUsername(token);

        return username.equals(userDetails.getUsername())
                && !isTokenExpired(token)
                && ACCESS_TOKEN_TYPE.equals(extractClaim(token, claims ->
                        claims.get(TOKEN_TYPE_CLAIM, String.class)));
    }

    private boolean isTokenExpired(String token) {

        return extractClaim(token, Claims::getExpiration)
                .before(new Date());
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {

        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.secretKey());

        return Keys.hmacShaKeyFor(keyBytes);
    }
}
