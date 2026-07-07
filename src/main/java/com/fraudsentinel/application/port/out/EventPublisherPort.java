package com.fraudsentinel.application.port.out;

import com.fraudsentinel.domain.event.TransactionCreatedEvent;

public interface EventPublisherPort {

    void publish(TransactionCreatedEvent event);
}