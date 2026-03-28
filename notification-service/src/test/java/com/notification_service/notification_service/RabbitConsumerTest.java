package com.notification_service.notification_service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.notification_service.notification_service.dto.TransactionEvent;
import com.notification_service.notification_service.messaging.RabbitConsumer;

@ExtendWith(MockitoExtension.class)
class RabbitConsumerTest {

    @Spy
    private RabbitConsumer rabbitConsumer;

    //Message received and processed
    @Test
    void testReceiveMessage_Success() {
        TransactionEvent event = new TransactionEvent();
        event.setId(1L);
        event.setRechargeId(101L);
        event.setUserId(5L);
        event.setAmount(299.0);
        event.setStatus("SUCCESS");
        event.setTransactionRef("TXN-UUID-001");
        event.setCreatedAt(LocalDateTime.now());

        // Should not throw any exception
        rabbitConsumer.receiveMessage(event);

        // Verify it was called once
        verify(rabbitConsumer, times(1)).receiveMessage(event);
    }

    //Message with null fields handled gracefully
    @Test
    void testReceiveMessage_WithNullFields() {
        TransactionEvent event = new TransactionEvent();
        event.setRechargeId(102L);
        event.setUserId(6L);
        event.setAmount(null);   // null amount
        event.setStatus("FAILED");

        // Should not throw exception even with null amount
        rabbitConsumer.receiveMessage(event);

        verify(rabbitConsumer, times(1)).receiveMessage(event);
    }

    //Failed transaction event
    @Test
    void testReceiveMessage_FailedTransaction() {
        TransactionEvent event = new TransactionEvent();
        event.setId(2L);
        event.setRechargeId(103L);
        event.setUserId(7L);
        event.setAmount(199.0);
        event.setStatus("FAILED");
        event.setTransactionRef("TXN-UUID-002");
        event.setCreatedAt(LocalDateTime.now());

        rabbitConsumer.receiveMessage(event);

        verify(rabbitConsumer, times(1)).receiveMessage(event);
    }
}
