package com.fraudsentinel.presentation.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionRequest(

        @NotNull(message = "userId e obrigatorio")
        UUID userId,

        @NotNull(message = "amount e obrigatorio")
        @DecimalMin(value = "0.01", message = "amount deve ser maior que zero")
        BigDecimal amount,

        @NotBlank(message = "currency e obrigatoria")
        @Pattern(regexp = "^[A-Z]{3}$", message = "currency deve seguir padrao ISO 4217")
        String currency,

        String description,
        String originAccount,
        String targetAccount,
        String deviceId,
        String ipAddress,
        String location
) {}