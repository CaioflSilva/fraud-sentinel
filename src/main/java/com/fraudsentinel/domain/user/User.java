package com.fraudsentinel.domain.user;

import java.time.LocalDateTime;
import java.util.UUID;

public class User {

    private UUID id;
    private String email;
    private String passwordHash;
    private Role role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private User() {}

    public static User create(String email, String passwordHash, Role role) {
        var user = new User();
        user.id = UUID.randomUUID();
        user.email = email;
        user.passwordHash = passwordHash;
        user.role = role;
        user.createdAt = LocalDateTime.now();
        user.updatedAt = LocalDateTime.now();
        return user;
    }

    public static User reconstitute(UUID id,
                                    String email,
                                    String passwordHash,
                                    Role role,
                                    LocalDateTime createdAt,
                                    LocalDateTime updatedAt) {
        var user = new User();
        user.id = id;
        user.email = email;
        user.passwordHash = passwordHash;
        user.role = role;
        user.createdAt = createdAt;
        user.updatedAt = updatedAt;
        return user;
    }

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Role getRole() { return role; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}