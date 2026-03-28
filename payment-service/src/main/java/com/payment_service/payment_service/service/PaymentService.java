package com.payment_service.payment_service.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.payment_service.payment_service.dto.PaymentRequest;
import com.payment_service.payment_service.dto.PaymentResponse;
import com.payment_service.payment_service.entity.Transaction;
import com.payment_service.payment_service.enums.PaymentStatus;
import com.payment_service.payment_service.repository.TransactionRepository;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {

        log.info("Processing payment for rechargeId: {}", request.getRechargeId());

        //validate that amount is correct or not
        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new IllegalArgumentException("Invalid amount");
        }
        /*
         * In real-world systems (like recharge/payment):

🚨 Problem without Idempotency

Imagine this scenario:

User clicks "Pay ₹100"
Network is slow → user clicks again 😬
Backend receives 2 identical requests

👉 Without idempotency:

₹100 gets deducted TWICE
Two transactions created ❌
Customer angry
         */

        // Idempotency check
        if (transactionRepository.existsByIdempotencyKey(request.getIdempotencyKey())) {
            return new PaymentResponse(null, "DUPLICATE", "Duplicate payment request");
        }

        Transaction txn = new Transaction();

        txn.setRechargeId(request.getRechargeId());
        txn.setAmount(request.getAmount());

        //Simulated user (later from JWT)
        txn.setUserId(1L);

        txn.setTransactionRef(UUID.randomUUID().toString());
        txn.setCreatedAt(LocalDateTime.now());
        txn.setIdempotencyKey(request.getIdempotencyKey());

        try {
            txn.setStatus(PaymentStatus.PENDING);

            //payment logic
            if (request.getAmount() > 0) {
                txn.setStatus(PaymentStatus.SUCCESS);
            } else {
                txn.setStatus(PaymentStatus.FAILED);
            }

            transactionRepository.save(txn);

            //Send full transaction event
            if (txn.getStatus() == PaymentStatus.SUCCESS) {
                rabbitTemplate.convertAndSend(
                        "recharge-exchange",
                        "recharge.success",
                        txn
                );

                log.info("Payment successful for rechargeId: {}", request.getRechargeId());
            }

            return new PaymentResponse(
                    txn.getId(),
                    txn.getStatus().name(),
                    "Payment processed"
            );

        } catch (Exception e) {
            log.error("Payment failed", e);
            return new PaymentResponse(null, "FAILED", "Payment error");
        }
    }
}