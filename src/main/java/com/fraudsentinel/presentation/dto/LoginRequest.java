package com.fraudsentinel.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank(message = "email e obrigatorio")
        @Email(message = "email invalido")
        String email,

        @NotBlank(message = "password e obrigatorio")
        String password
) {}