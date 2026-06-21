package com.fraudsentinel.presentation.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String email,
        String role
) {}