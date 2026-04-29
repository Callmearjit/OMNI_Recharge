package com.payment_service.payment_service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.payment_service.payment_service.dto.PaymentRequest;
import com.payment_service.payment_service.dto.PaymentResponse;
import com.payment_service.payment_service.repository.TransactionRepository;
import com.payment_service.payment_service.service.PaymentService;


@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private PaymentService paymentService;

    // ✅ Test 1 - Successful payment
    @Test
    void testProcessPayment_Success() {
        PaymentRequest request = new PaymentRequest();
        request.setRechargeId(1L);
        request.setAmount(299.0);
        request.setIdempotencyKey("key-001");

        when(transactionRepository.existsByIdempotencyKey("key-001")).thenReturn(false);

        PaymentResponse response = paymentService.processPayment(request);

        assertEquals("SUCCESS", response.getStatus());
        assertEquals("Payment processed", response.getMessage());
        verify(transactionRepository, times(1)).save(any());
    }

    // ✅ Test 2 - Duplicate payment (idempotency)
    @Test
    void testProcessPayment_Duplicate() {
        PaymentRequest request = new PaymentRequest();
        request.setRechargeId(1L);
        request.setAmount(299.0);
        request.setIdempotencyKey("key-001");

        when(transactionRepository.existsByIdempotencyKey("key-001")).thenReturn(true);

        PaymentResponse response = paymentService.processPayment(request);

        assertEquals("DUPLICATE", response.getStatus());
        assertEquals("Duplicate payment request", response.getMessage());
        verify(transactionRepository, never()).save(any()); // never saves
    }

    // ✅ Test 3 - Invalid amount (null)
    @Test
    void testProcessPayment_NullAmount() {
        PaymentRequest request = new PaymentRequest();
        request.setRechargeId(1L);
        request.setAmount(null);
        request.setIdempotencyKey("key-002");

        assertThrows(IllegalArgumentException.class,
                () -> paymentService.processPayment(request));
    }

    // ✅ Test 4 - Invalid amount (zero)
    @Test
    void testProcessPayment_ZeroAmount() {
        PaymentRequest request = new PaymentRequest();
        request.setRechargeId(1L);
        request.setAmount(0.0);
        request.setIdempotencyKey("key-003");

        assertThrows(IllegalArgumentException.class,
                () -> paymentService.processPayment(request));
    }

    // ✅ Test 5 - RabbitMQ event sent on success
    @Test
    void testProcessPayment_RabbitMQEventSent() {
        PaymentRequest request = new PaymentRequest();
        request.setRechargeId(2L);
        request.setAmount(199.0);
        request.setIdempotencyKey("key-004");

        when(transactionRepository.existsByIdempotencyKey("key-004")).thenReturn(false);

        paymentService.processPayment(request);

        // Verify RabbitMQ message was sent
        verify(rabbitTemplate, times(1))
                .convertAndSend(eq("recharge-exchange"), eq("recharge.success"), any(Object.class));
    }
}
