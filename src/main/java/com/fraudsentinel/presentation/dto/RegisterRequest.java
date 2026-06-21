package com.fraudsentinel.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "email e obrigatorio")
        @Email(message = "email invalido")
        String email,

        @NotBlank(message = "password e obrigatorio")
        @Size(min = 8, message = "password deve ter no minimo 8 caracteres")
        String password
) {}