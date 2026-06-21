package com.fraudsentinel.infrastructure.security;

import com.fraudsentinel.application.port.out.UserRepositoryPort;
import com.fraudsentinel.domain.user.Role;
import com.fraudsentinel.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepositoryPort userRepositoryPort;
    private final JwtTokenService jwtTokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public record AuthResult(String accessToken, String refreshToken, String email, String role) {}

    public AuthResult register(String email, String password) {
        if (userRepositoryPort.existsByEmail(email)) {
            throw new IllegalArgumentException("Email ja cadastrado: " + email);
        }

        var user = User.create(email, passwordEncoder.encode(password), Role.ANALYST);
        userRepositoryPort.save(user);

        return generateTokens(email, user.getRole().name());
    }

    public AuthResult login(String email, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        var user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado"));

        return generateTokens(email, user.getRole().name());
    }

    public AuthResult refresh(String refreshToken) {
        if (!jwtTokenService.isValidToken(refreshToken)) {
            throw new IllegalArgumentException("Refresh token invalido");
        }

        var type = jwtTokenService.getTokenType(refreshToken);
        if (!"refresh".equals(type)) {
            throw new IllegalArgumentException("Token nao e do tipo refresh");
        }

        var email = jwtTokenService.getEmail(refreshToken);
        var role = jwtTokenService.getRole(refreshToken);

        return generateTokens(email, role);
    }

    private AuthResult generateTokens(String email, String role) {
        var accessToken = jwtTokenService.generateAccessToken(email, role);
        var refreshToken = jwtTokenService.generateRefreshToken(email, role);
        return new AuthResult(accessToken, refreshToken, email, role);
    }
}