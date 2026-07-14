package com.fraudsentinel.presentation.controller;

import com.fraudsentinel.application.port.in.AuthUseCase;
import com.fraudsentinel.presentation.dto.AuthResponse;
import com.fraudsentinel.presentation.dto.LoginRequest;
import com.fraudsentinel.presentation.dto.RegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUseCase authUseCase;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        var result = authUseCase.register(request.email(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(result.accessToken(), result.refreshToken(), result.email(), result.role()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        var result = authUseCase.login(request.email(), request.password());
        return ResponseEntity.ok(
                new AuthResponse(result.accessToken(), result.refreshToken(), result.email(), result.role()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody Map<String, String> request) {
        var refreshToken = request.get("refreshToken");
        var result = authUseCase.refresh(refreshToken);
        return ResponseEntity.ok(
                new AuthResponse(result.accessToken(), result.refreshToken(), result.email(), result.role()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        var token = authHeader.replace("Bearer ", "");
        authUseCase.logout(token);
        return ResponseEntity.noContent().build();
    }
}