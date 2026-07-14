package com.fraudsentinel.application.port.in;

public interface AuthUseCase {

    AuthResult register(String email, String password);

    AuthResult login(String email, String password);

    AuthResult refresh(String refreshToken);

    void logout(String accessToken);

    record AuthResult(String accessToken, String refreshToken, String email, String role) {}
}