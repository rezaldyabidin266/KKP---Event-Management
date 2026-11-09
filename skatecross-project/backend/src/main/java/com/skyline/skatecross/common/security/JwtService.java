package com.skyline.skatecross.common.security;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final Key signingKey;
    private final long expiresInSeconds;

    public JwtService(
            @Value("${app.jwt.secret-base64}") String secretBase64,

            @Value("${app.jwt.expiration-seconds:3600}") long expiresInSeconds) {
        if (expiresInSeconds < 1 ||
                expiresInSeconds > 86400) {
            throw new IllegalArgumentException(
                    "JWT expiration harus 1 sampai 86400 detik");
        }

        this.signingKey = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(secretBase64));

        this.expiresInSeconds = expiresInSeconds;
    }

    public String generateToken(Long userId) {

        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException(
                    "User ID tidak valid");
        }

        long nowMillis = System.currentTimeMillis();

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date(nowMillis))
                .setExpiration(
                        new Date(
                                nowMillis + expiresInSeconds * 1000L))
                .signWith(
                        signingKey,
                        SignatureAlgorithm.HS256)
                .compact();
    }

    public Long extractUserId(String token) {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        try {
            long userId = Long.parseLong(
                    claims.getSubject());

            if (userId <= 0) {
                throw new NumberFormatException(
                        "ID harus positif");
            }

            return userId;

        } catch (
                NumberFormatException | NullPointerException exception) {
            throw new JwtException(
                    "JWT subject tidak valid",
                    exception);
        }
    }

    public long getExpiresInSeconds() {
        return expiresInSeconds;
    }
}