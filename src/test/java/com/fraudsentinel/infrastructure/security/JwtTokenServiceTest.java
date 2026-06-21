package com.fraudsentinel.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenServiceTest {

    private JwtTokenService jwtTokenService;

    @BeforeEach
    void setUp() {
        jwtTokenService = new JwtTokenService(
                "fraud-sentinel-super-secret-key-that-is-at-least-256-bits-long-for-hs256",
                900000,
                604800000
        );
    }

    @Test
    @DisplayName("Deve gerar access token valido")
    void shouldGenerateValidAccessToken() {
        var token = jwtTokenService.generateAccessToken("caio@test.com", "ANALYST");

        assertNotNull(token);
        assertTrue(jwtTokenService.isValidToken(token));
        assertEquals("caio@test.com", jwtTokenService.getEmail(token));
        assertEquals("ANALYST", jwtTokenService.getRole(token));
        assertEquals("access", jwtTokenService.getTokenType(token));
    }

    @Test
    @DisplayName("Deve gerar refresh token valido")
    void shouldGenerateValidRefreshToken() {
        var token = jwtTokenService.generateRefreshToken("caio@test.com", "ANALYST");

        assertNotNull(token);
        assertTrue(jwtTokenService.isValidToken(token));
        assertEquals("refresh", jwtTokenService.getTokenType(token));
    }

    @Test
    @DisplayName("Cada token deve ter jti unico")
    void shouldGenerateUniqueJti() {
        var token1 = jwtTokenService.generateAccessToken("caio@test.com", "ANALYST");
        var token2 = jwtTokenService.generateAccessToken("caio@test.com", "ANALYST");

        assertNotEquals(jwtTokenService.getJti(token1), jwtTokenService.getJti(token2));
    }

    @Test
    @DisplayName("Deve rejeitar token invalido")
    void shouldRejectInvalidToken() {
        assertFalse(jwtTokenService.isValidToken("token.invalido.aqui"));
    }

    @Test
    @DisplayName("Deve rejeitar token expirado")
    void shouldRejectExpiredToken() {
        var expiredService = new JwtTokenService(
                "fraud-sentinel-super-secret-key-that-is-at-least-256-bits-long-for-hs256",
                0,
                0
        );

        var token = expiredService.generateAccessToken("caio@test.com", "ANALYST");

        assertFalse(jwtTokenService.isValidToken(token));
    }

    @Test
    @DisplayName("Deve extrair expiration do token")
    void shouldExtractExpiration() {
        var token = jwtTokenService.generateAccessToken("caio@test.com", "ANALYST");

        assertNotNull(jwtTokenService.getExpiration(token));
    }
}