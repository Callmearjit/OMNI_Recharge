package com.recharge_service.recharge_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.recharge_service.recharge_service.client.OperatorClient;
import com.recharge_service.recharge_service.client.PaymentClient;
import com.recharge_service.recharge_service.dto.PaymentRequest;
import com.recharge_service.recharge_service.dto.PaymentResponse;
import com.recharge_service.recharge_service.dto.PlanResponse;
import com.recharge_service.recharge_service.entity.Recharge;
import com.recharge_service.recharge_service.repository.RechargeRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class RechargeService {

    private static final Logger log = LoggerFactory.getLogger(RechargeService.class);

    @Autowired
    private RechargeRepository repository;

    @Autowired
    private OperatorClient operatorClient;

    @Autowired
    private PaymentClient paymentClient;

    @CircuitBreaker(name = "paymentService", fallbackMethod = "fallback")
    public Recharge createRecharge(Recharge recharge) {

        if (repository.findByIdempotencyKey(recharge.getIdempotencyKey()).isPresent()) {
            throw new RuntimeException("Duplicate Request");
        }

        PlanResponse plan = operatorClient.getPlan(recharge.getPlanId());

        recharge.setStatus("PENDING");
        Recharge saved = repository.save(recharge);

        try {
            PaymentRequest request = new PaymentRequest();
            request.setRechargeId(saved.getId());
            request.setAmount(plan.getAmount());
            request.setIdempotencyKey(saved.getIdempotencyKey());
            request.setUserId(saved.getUserId());

            PaymentResponse response = paymentClient.processPayment(request);

            if ("SUCCESS".equals(response.getStatus())) {
                saved.setStatus("SUCCESS");
                // payment-service publishes the RabbitMQ notification event —
                // do NOT publish here too or notification-service receives duplicates
            } else {
                saved.setStatus("FAILED");
                log.warn("Payment returned non-SUCCESS status '{}' for rechargeId: {}",
                        response.getStatus(), saved.getId());
            }

        } catch (Exception e) {
            log.error("Payment processing error for rechargeId {}: {}", saved.getId(), e.getMessage(), e);
            saved.setStatus("FAILED");
        }

        return repository.save(saved);
    }

    public Recharge fallback(Recharge recharge, Exception ex) {
        log.error("Payment service circuit breaker triggered: {}", ex.getMessage());
        recharge.setStatus("FAILED");
        repository.save(recharge);
        throw new RuntimeException("Payment service temporarily unavailable. Please try again later.");
    }
}
