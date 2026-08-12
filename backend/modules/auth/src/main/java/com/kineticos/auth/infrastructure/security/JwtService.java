package com.kineticos.auth.infrastructure.security;

import com.kineticos.shared.error.DomainException;
import com.kineticos.shared.error.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

/**
 * Emisión y validación de tokens JWT (access + refresh).
 */
@Service
public class JwtService {

    private final SecretKey key;
    private final JwtProperties properties;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
        this.key = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(Long userId, String email, List<String> roles) {
        return build(userId, email, roles, "access", properties.expirationMs());
    }

    public String generateRefreshToken(Long userId, String email) {
        return build(userId, email, List.of(), "refresh", properties.refreshExpirationMs());
    }

    public Claims parse(String token) {
        try {
            return Jwts.parser().verifyWith(key).build()
                    .parseSignedClaims(token).getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            throw new DomainException(401, ErrorCode.INVALID_TOKEN, "Token inválido o expirado");
        }
    }

    public boolean isRefreshToken(Claims claims) {
        return "refresh".equals(claims.get("type", String.class));
    }

    private String build(Long userId, String email, List<String> roles, String type, long ttlMs) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
                .claim("type", type)
                .claim("roles", roles)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(ttlMs)))
                .signWith(key)
                .compact();
    }
}