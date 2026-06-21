package com.fraudsentinel.application.port.out;

import com.fraudsentinel.domain.user.User;

import java.util.Optional;

public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}