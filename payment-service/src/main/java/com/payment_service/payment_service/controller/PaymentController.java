
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

import com.payment_service.payment_service.dto.PaymentRequest;
import com.payment_service.payment_service.dto.PaymentResponse;
import com.payment_service.payment_service.entity.Transaction;
import com.payment_service.payment_service.repository.TransactionRepository;
import com.payment_service.payment_service.service.PaymentService;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private TransactionRepository transactionRepository;

    // ── Process a payment (internal, called by recharge-service) ─────
    @PostMapping
    public PaymentResponse pay(@RequestBody PaymentRequest request) {
        return paymentService.processPayment(request);
    }

    // ── Get transaction by ID ────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<?> getTransaction(
            @PathVariable Long id,
            @RequestHeader("X-Role") String role) {

        Transaction txn = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        return ResponseEntity.ok(txn);
    }

    // ── Get transactions for a specific recharge ─────────────────────
    @GetMapping("/recharge/{rechargeId}")
    public List<Transaction> getByRechargeId(@PathVariable Long rechargeId) {
        return transactionRepository.findByRechargeId(rechargeId);
    }

    // ── ADMIN: View all transactions ─────────────────────────────────
    @GetMapping("/all")
    public ResponseEntity<?> getAllTransactions(
            @RequestHeader("X-Role") String role) {

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body("Access Denied: Admin only");
        }

        return ResponseEntity.ok(transactionRepository.findAll());
    }
}