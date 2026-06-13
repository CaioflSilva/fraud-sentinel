package com.fraudsentinel.domain.transaction;

import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {

    private UUID id;
    private UUID userId;
    private Money money;
    private TransactionStatus status;
    private String description;
    private String originAccount;
    private String targetAccount;
    private String deviceId;
    private String ipAddress;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Transaction() {}

    public static Transaction create(UUID userId,
                                     Money money,
                                     String description,
                                     String originAccount,
                                     String targetAccount,
                                     String deviceId,
                                     String ipAddress,
                                     String location) {
        var transaction = new Transaction();
        transaction.id = UUID.randomUUID();
        transaction.userId = userId;
        transaction.money = money;
        transaction.status = TransactionStatus.PENDING;
        transaction.description = description;
        transaction.originAccount = originAccount;
        transaction.targetAccount = targetAccount;
        transaction.deviceId = deviceId;
        transaction.ipAddress = ipAddress;
        transaction.location = location;
        transaction.createdAt = LocalDateTime.now();
        transaction.updatedAt = LocalDateTime.now();
        return transaction;
    }

    public static Transaction reconstitute(UUID id,
                                           UUID userId,
                                           Money money,
                                           TransactionStatus status,
                                           String description,
                                           String originAccount,
                                           String targetAccount,
                                           String deviceId,
                                           String ipAddress,
                                           String location,
                                           LocalDateTime createdAt,
                                           LocalDateTime updatedAt) {
        var transaction = new Transaction();
        transaction.id = id;
        transaction.userId = userId;
        transaction.money = money;
        transaction.status = status;
        transaction.description = description;
        transaction.originAccount = originAccount;
        transaction.targetAccount = targetAccount;
        transaction.deviceId = deviceId;
        transaction.ipAddress = ipAddress;
        transaction.location = location;
        transaction.createdAt = createdAt;
        transaction.updatedAt = updatedAt;
        return transaction;
    }

    public void advanceTo(TransactionStatus next) {
        if (!this.status.canTransitionTo(next)) {
            throw new IllegalStateException(
                    "Transicao invalida: " + this.status + " -> " + next
            );
        }
        this.status = next;
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public Money getMoney() { return money; }
    public TransactionStatus getStatus() { return status; }
    public String getDescription() { return description; }
    public String getOriginAccount() { return originAccount; }
    public String getTargetAccount() { return targetAccount; }
    public String getDeviceId() { return deviceId; }
    public String getIpAddress() { return ipAddress; }
    public String getLocation() { return location; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}