package com.skyline.skatecross.common.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import io.jsonwebtoken.JwtException;

class JwtServiceTest {

    // Secret hanya untuk unit test.
    private final JwtService jwtService = new JwtService(
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
            3600);

    @Test
    void shouldGenerateAndValidateToken() {

        String token = jwtService.generateToken(10L);

        Long userId = jwtService.extractUserId(token);

        assertEquals(Long.valueOf(10L), userId);
    }

    @Test
    void shouldRejectInvalidToken() {

        assertThrows(
                JwtException.class,
                () -> jwtService.extractUserId("invalid.jwt.token"));
    }

    @Test
    void shouldRejectInvalidExpiration() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new JwtService(
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
                        0));
    }
}