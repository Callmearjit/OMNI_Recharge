
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

import com.recharge_service.recharge_service.entity.Recharge;
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

    // ── Initiate a recharge (USER or ADMIN) ──────────────────────────
    @PostMapping
    public Recharge recharge(@Valid @RequestBody Recharge recharge,
                             @RequestHeader("X-User-Id") String userId) {
        recharge.setUserId(Long.parseLong(userId));
        return service.createRecharge(recharge);
    }

    // ── View recharge history for the logged-in user ─────────────────
    @GetMapping("/history")
    public List<Recharge> getMyRechargeHistory(
            @RequestHeader("X-User-Id") String userId) {
        return rechargeRepository.findByUserId(Long.parseLong(userId));
    }

    // ── Track a specific recharge status ─────────────────────────────
    @GetMapping("/{id}/status")
    public ResponseEntity<?> getRechargeStatus(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-Role") String role) {

        Recharge recharge = rechargeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recharge not found"));

        // Users can only view their own recharges; admins can view any
        if (!role.equals("ADMIN") && !recharge.getUserId().equals(Long.parseLong(userId))) {
            return ResponseEntity.status(403).body("Access Denied: You can only view your own recharges");
        }

        return ResponseEntity.ok(recharge);
    }

    // ── ADMIN: View all recharges ────────────────────────────────────
    @GetMapping("/all")
    public ResponseEntity<?> getAllRecharges(
            @RequestHeader("X-Role") String role) {

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body("Access Denied: Admin only");
        }

        return ResponseEntity.ok(rechargeRepository.findAll());
    }
}