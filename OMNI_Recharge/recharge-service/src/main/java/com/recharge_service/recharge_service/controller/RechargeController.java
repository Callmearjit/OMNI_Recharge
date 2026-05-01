package com.recharge_service.recharge_service.controller;

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

import com.recharge_service.recharge_service.dto.ErrorDTO;
import com.recharge_service.recharge_service.dto.RechargeRequest;
import com.recharge_service.recharge_service.entity.Recharge;
import com.recharge_service.recharge_service.exception.ResourceNotFoundException;
import com.recharge_service.recharge_service.repository.RechargeRepository;
import com.recharge_service.recharge_service.service.RechargeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/recharges")
public class RechargeController {

    @Autowired
    private RechargeService service;

    @Autowired
    private RechargeRepository rechargeRepository;

    // ── Initiate a recharge ──────────────────────────────────────────
    @PostMapping
    public ResponseEntity<?> recharge(
            @Valid @RequestBody RechargeRequest request,
            @RequestHeader("X-User-Id") String userId) {

        Recharge recharge = new Recharge();
        recharge.setMobileNumber(request.getMobileNumber());
        recharge.setPlanId(request.getPlanId());
        recharge.setIdempotencyKey(request.getIdempotencyKey());
        recharge.setUserId(userId);

        return ResponseEntity.ok(service.createRecharge(recharge));
    }

    // ── Recharge history for logged-in user ─────────────────────────
    @GetMapping("/history")
    public ResponseEntity<?> getMyRechargeHistory(
            @RequestHeader("X-User-Id") String userId) {

        List<Recharge> history = rechargeRepository.findByUserId(userId);
        return ResponseEntity.ok(history);
    }

    // ── Track a specific recharge status ────────────────────────────
    @GetMapping("/{id}/status")
    public ResponseEntity<?> getRechargeStatus(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-Role") String role) {

        Recharge recharge = rechargeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recharge not found with id: " + id));

        if (!"ADMIN".equals(role) && !recharge.getUserId().equals(userId)) {
            return ResponseEntity.status(403)
                    .body(new ErrorDTO(403, "Access Denied: You can only view your own recharges"));
        }

        return ResponseEntity.ok(recharge);
    }

    // ── ADMIN: all recharges ─────────────────────────────────────────
    @GetMapping("/all")
    public ResponseEntity<?> getAllRecharges(
            @RequestHeader("X-Role") String role) {

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body(new ErrorDTO(403, "Access Denied: Admin only"));
        }

        return ResponseEntity.ok(rechargeRepository.findAll());
    }
}
