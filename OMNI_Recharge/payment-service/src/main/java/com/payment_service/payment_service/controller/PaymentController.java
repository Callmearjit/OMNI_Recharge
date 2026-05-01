package com.payment_service.payment_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payment_service.payment_service.dto.ErrorDTO;
import com.payment_service.payment_service.dto.PaymentRequest;
import com.payment_service.payment_service.dto.PaymentResponse;
import com.payment_service.payment_service.entity.Transaction;
import com.payment_service.payment_service.exception.ResourceNotFoundException;
import com.payment_service.payment_service.repository.TransactionRepository;
import com.payment_service.payment_service.service.PaymentService;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private TransactionRepository transactionRepository;

    // ── Internal: called by recharge-service via Feign ───────────────
    @PostMapping
    public PaymentResponse pay(@RequestBody PaymentRequest request) {
        return paymentService.processPayment(request);
    }

    // ── Get transaction by ID ────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<?> getTransaction(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String username,
            @RequestHeader("X-Role") String role) {

        Transaction txn = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));

        if (!"ADMIN".equals(role)) {
            if (txn.getUserId() == null) {
                return ResponseEntity.status(403)
                        .body(new ErrorDTO(403, "Access Denied: transaction owner unknown"));
            }
            if (!txn.getUserId().equals(username)) {
                return ResponseEntity.status(403)
                        .body(new ErrorDTO(403, "Access Denied: you can only view your own transactions"));
            }
        }
        return ResponseEntity.ok(txn);
    }
    // ── Get transactions for a recharge ─────────────────────────────
    @GetMapping("/recharge/{rechargeId}")
    public List<Transaction> getByRechargeId(@PathVariable Long rechargeId) {
        return transactionRepository.findByRechargeId(rechargeId);
    }

    // ── ADMIN: all transactions ──────────────────────────────────────
    @GetMapping("/all")
    public ResponseEntity<?> getAllTransactions(
            @RequestHeader("X-Role") String role) {

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body(new ErrorDTO(403, "Access Denied: Admin only"));
        }

        return ResponseEntity.ok(transactionRepository.findAll());
    }
}