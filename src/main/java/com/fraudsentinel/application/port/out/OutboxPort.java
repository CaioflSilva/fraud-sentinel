package com.fraudsentinel.application.port.out;

import com.fraudsentinel.domain.event.TransactionCreatedEvent;

public interface OutboxPort {

    void saveEvent(TransactionCreatedEvent event);
}