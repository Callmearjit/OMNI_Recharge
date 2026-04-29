package com.notification_service.notification_service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.notification_service.notification_service.dto.TransactionEvent;
import com.notification_service.notification_service.messaging.RabbitConsumer;
import com.notification_service.notification_service.service.NotificationService;

@ExtendWith(MockitoExtension.class)
class RabbitConsumerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private RabbitConsumer rabbitConsumer;

    // ✅ SUCCESS CASE
    @Test
    void testReceiveMessage_Success() {
        TransactionEvent event = new TransactionEvent();
        event.setId(1L);
        event.setRechargeId(101L);
        event.setUserId("arjit");
        event.setAmount(299.0);
        event.setStatus("SUCCESS");
        event.setTransactionRef("TXN-UUID-001");

        rabbitConsumer.receiveMessage(event);

        verify(notificationService, times(1))
                .processNotification(event);
    }

    // ✅ NULL FIELD CASE
    @Test
    void testReceiveMessage_WithNullFields() {
        TransactionEvent event = new TransactionEvent();
        event.setRechargeId(102L);
        event.setUserId("rohit");
        event.setAmount(null);
        event.setStatus("FAILED");

        rabbitConsumer.receiveMessage(event);

        verify(notificationService, times(1))
                .processNotification(event);
    }

    // ✅ FAILED TRANSACTION
    @Test
    void testReceiveMessage_FailedTransaction() {
        TransactionEvent event = new TransactionEvent();
        event.setId(2L);
        event.setRechargeId(103L);
        event.setUserId("priya");
        event.setAmount(199.0);
        event.setStatus("FAILED");
        event.setTransactionRef("TXN-UUID-002");

        rabbitConsumer.receiveMessage(event);

        verify(notificationService, times(1))
                .processNotification(event);
    }

    // ✅ EXCEPTION CASE (simulate service failure → DLQ trigger)
    @Test
    void testReceiveMessage_Exception_ThrowsRuntime() {
        TransactionEvent event = new TransactionEvent();
        event.setRechargeId(104L);

        // simulate exception
        org.mockito.Mockito.doThrow(new RuntimeException("DB error"))
                .when(notificationService)
                .processNotification(event);

        assertThrows(RuntimeException.class,
                () -> rabbitConsumer.receiveMessage(event));
    }
}