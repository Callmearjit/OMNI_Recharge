package com.payment_service.payment_service.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new IllegalArgumentException("Invalid amount: " + request.getAmount());
        }

        // Idempotency check
        if (transactionRepository.existsByIdempotencyKey(request.getIdempotencyKey())) {
            log.warn("Duplicate payment request for key: {}", request.getIdempotencyKey());
            return new PaymentResponse(null, "DUPLICATE", "Duplicate payment request");
        }

        Transaction txn = new Transaction();
        txn.setRechargeId(request.getRechargeId());
        txn.setAmount(request.getAmount());

        // ✅ FIXED: Directly store userId as String — no more Long.parseLong hack
        // Transaction.userId is now String (matches JWT subject which is username)
        txn.setUserId(request.getUserId());

        txn.setTransactionRef(UUID.randomUUID().toString());
        txn.setCreatedAt(LocalDateTime.now());
        txn.setIdempotencyKey(request.getIdempotencyKey());

        try {
            // Amount is already validated > 0 above, so payment always succeeds at this point
            txn.setStatus(PaymentStatus.SUCCESS);

            transactionRepository.save(txn);

            if (txn.getStatus() == PaymentStatus.SUCCESS) {
                rabbitTemplate.convertAndSend(
                        "recharge-exchange",
                        "recharge.success",
                        txn
                );
                log.info("Payment successful for rechargeId: {}", request.getRechargeId());
            }

            return new PaymentResponse(txn.getId(), txn.getStatus().name(), "Payment processed");

        } catch (Exception e) {
            log.error("Payment failed for rechargeId: {}", request.getRechargeId(), e);
            return new PaymentResponse(null, "FAILED", "Payment error: " + e.getMessage());
        }
    }
}