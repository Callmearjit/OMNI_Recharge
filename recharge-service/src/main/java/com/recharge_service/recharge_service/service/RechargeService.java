
package com.recharge_service.recharge_service.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.recharge_service.recharge_service.client.OperatorClient;
import com.recharge_service.recharge_service.client.PaymentClient;
import com.recharge_service.recharge_service.dto.PaymentRequest;
import com.recharge_service.recharge_service.dto.PaymentResponse;
import com.recharge_service.recharge_service.dto.PlanResponse;
import com.recharge_service.recharge_service.messaging.RabbitProducer;
import com.recharge_service.recharge_service.entity.Recharge;
import com.recharge_service.recharge_service.repository.RechargeRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
@Service
public class RechargeService {

    @Autowired
    private RechargeRepository repository;

    @Autowired
    private OperatorClient operatorClient;

    @Autowired
    private PaymentClient paymentClient;

    @Autowired
    private RabbitProducer rabbitProducer;

    @CircuitBreaker(name = "paymentService", fallbackMethod = "fallback")
    public Recharge createRecharge(Recharge recharge) {

        // 1. Idempotency check - prevent duplicate recharges
        if (repository.findByIdempotencyKey(recharge.getIdempotencyKey()).isPresent()) {
            throw new RuntimeException("Duplicate Request");
        }

        // 2. Fetch real plan amount from operator-service
        PlanResponse plan = operatorClient.getPlan(recharge.getPlanId());

        // 3. Save as PENDING first
        recharge.setStatus("PENDING");
        Recharge saved = repository.save(recharge);

        try {
            // 4. Process payment with real plan amount
            PaymentRequest request = new PaymentRequest();
            request.setRechargeId(saved.getId());
            request.setAmount(plan.getAmount());        // ← real amount from plan
            request.setIdempotencyKey(saved.getIdempotencyKey());

            PaymentResponse response = paymentClient.processPayment(request);

            if ("SUCCESS".equals(response.getStatus())) {
                // 5. Update status to SUCCESS
                saved.setStatus("SUCCESS");

                // 6. Send notification via RabbitMQ
                rabbitProducer.sendRechargeEvent(
                    "Recharge successful for mobile: " + saved.getMobileNumber()
                    + " | Plan: " + plan.getValidity()
                    + " | Amount: " + plan.getAmount()
                );
            } else {
                saved.setStatus("FAILED");
            }

        } catch (Exception e) {
            e.printStackTrace();
            saved.setStatus("FAILED");
        }

        return repository.save(saved);
    }

    // Circuit breaker fallback - called when paymentService is down
    public Recharge fallback(Recharge recharge, Exception ex) {
        recharge.setStatus("FAILED");
        repository.save(recharge);
        throw new RuntimeException("Payment service temporarily unavailable. Try again later.");
    }
}