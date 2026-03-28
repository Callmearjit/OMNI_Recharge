package com.recharge_service.recharge_service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.recharge_service.recharge_service.client.OperatorClient;
import com.recharge_service.recharge_service.client.PaymentClient;
import com.recharge_service.recharge_service.dto.PaymentResponse;
import com.recharge_service.recharge_service.dto.PlanResponse;
import com.recharge_service.recharge_service.messaging.RabbitProducer;
import com.recharge_service.recharge_service.entity.Recharge;
import com.recharge_service.recharge_service.repository.RechargeRepository;
import com.recharge_service.recharge_service.service.RechargeService;

@ExtendWith(MockitoExtension.class)
class RechargeServiceTest {

    @Mock
    private RechargeRepository repository;

    @Mock
    private OperatorClient operatorClient;

    @Mock
    private PaymentClient paymentClient;

    @Mock
    private RabbitProducer rabbitProducer;

    @InjectMocks
    private RechargeService rechargeService;

    //Successful recharge test
    @Test
    void testCreateRecharge_Success() {
        Recharge recharge = new Recharge();
        recharge.setMobileNumber("9876543210");
        recharge.setPlanId(1L);
        recharge.setIdempotencyKey("key-001");

        PlanResponse plan = new PlanResponse();
        plan.setAmount(299.0);
        plan.setValidity("28 days");

        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setStatus("SUCCESS");

        Recharge saved = new Recharge();
        saved.setId(1L);
        saved.setMobileNumber("9876543210");
        saved.setPlanId(1L);
        saved.setIdempotencyKey("key-001");
        saved.setStatus("PENDING");

        when(repository.findByIdempotencyKey("key-001")).thenReturn(Optional.empty());
        when(operatorClient.getPlan(1L)).thenReturn(plan);
        when(repository.save(any())).thenReturn(saved);
        when(paymentClient.processPayment(any())).thenReturn(paymentResponse);

        Recharge result = rechargeService.createRecharge(recharge);

        assertNotNull(result);
        verify(rabbitProducer, times(1)).sendRechargeEvent(any(), null, null, null, null);
    }

    //Duplicate recharge test
    @Test
    void testCreateRecharge_Duplicate() {
        Recharge recharge = new Recharge();
        recharge.setIdempotencyKey("key-001");

        when(repository.findByIdempotencyKey("key-001"))
                .thenReturn(Optional.of(recharge));

        assertThrows(RuntimeException.class,
                () -> rechargeService.createRecharge(recharge));

        verify(paymentClient, never()).processPayment(any());
    }

    //Payment failed test
    @Test
    void testCreateRecharge_PaymentFailed() {
        Recharge recharge = new Recharge();
        recharge.setMobileNumber("9876543210");
        recharge.setPlanId(1L);
        recharge.setIdempotencyKey("key-002");

        PlanResponse plan = new PlanResponse();
        plan.setAmount(199.0);
        plan.setValidity("14 days");

        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setStatus("FAILED");

        Recharge saved = new Recharge();
        saved.setId(2L);
        saved.setIdempotencyKey("key-002");
        saved.setStatus("PENDING");

        when(repository.findByIdempotencyKey("key-002")).thenReturn(Optional.empty());
        when(operatorClient.getPlan(1L)).thenReturn(plan);
        when(repository.save(any())).thenReturn(saved);
        when(paymentClient.processPayment(any())).thenReturn(paymentResponse);

        Recharge result = rechargeService.createRecharge(recharge);

        assertNotNull(result);
        verify(rabbitProducer, never()).sendRechargeEvent(any(), null, null, null, null); // no notification
    }

    //Circuit breaker fallback test
    @Test
    void testFallback() {
        Recharge recharge = new Recharge();
        recharge.setIdempotencyKey("key-003");

        when(repository.save(any())).thenReturn(recharge);

        assertThrows(RuntimeException.class,
                () -> rechargeService.fallback(recharge, new RuntimeException("down")));

        verify(repository, times(1)).save(recharge);
    }
}
