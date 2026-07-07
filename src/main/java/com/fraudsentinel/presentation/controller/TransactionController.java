package com.fraudsentinel.presentation.controller;

import com.fraudsentinel.application.port.in.SubmitTransactionUseCase;
import com.fraudsentinel.presentation.dto.TransactionRequest;
import com.fraudsentinel.presentation.dto.TransactionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final SubmitTransactionUseCase submitTransactionUseCase;

    @PostMapping
    public ResponseEntity<TransactionResponse> submit(@Valid @RequestBody TransactionRequest request) {
        var command = new SubmitTransactionUseCase.Command(
                request.userId(),
                request.amount(),
                request.currency(),
                request.description(),
                request.originAccount(),
                request.targetAccount(),
                request.deviceId(),
                request.ipAddress(),
                request.location()
        );

        var transaction = submitTransactionUseCase.execute(command);

        var response = new TransactionResponse(
                transaction.getId(),
                transaction.getUserId(),
                transaction.getMoney().getAmount(),
                transaction.getMoney().getCurrency(),
                transaction.getStatus().name(),
                transaction.getDescription(),
                transaction.getOriginAccount(),
                transaction.getTargetAccount(),
                transaction.getCreatedAt()
        );

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}