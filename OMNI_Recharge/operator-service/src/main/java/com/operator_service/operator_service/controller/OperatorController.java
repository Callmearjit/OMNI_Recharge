package com.operator_service.operator_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.operator_service.operator_service.entity.Operator;
import com.operator_service.operator_service.entity.Plan;
import com.operator_service.operator_service.service.OperatorService;

@RestController
@RequestMapping("/operators")
public class OperatorController {

    @Autowired
    private OperatorService operatorService;

    // ── Public read endpoints (no auth needed) ───────────────────────
    @GetMapping
    public List<Operator> getOperators() {
        return operatorService.getAllOperators();
    }

    @GetMapping("/{id}/plans")
    public List<Plan> getPlans(@PathVariable Long id) {
        return operatorService.getPlansByOperator(id);
    }

    @GetMapping("/plans/{planId}")
    public Plan getPlan(@PathVariable Long planId) {
        return operatorService.getPlanById(planId);
    }

    // ── ADMIN write endpoints ─────────────────────────────────────────
    @PostMapping("/plans")
    public ResponseEntity<String> addPlan(
            @RequestBody Plan plan,
            @RequestHeader("X-Role") String role) {

        // ─────────────────────────────────────────────────────────────
        // FIX: Was throwing RuntimeException("Access Denied") which:
        //   1. Gets caught by GlobalExceptionHandler and returns 400 Bad Request
        //      instead of the correct 403 Forbidden.
        //   2. Leaks exception class names into stack traces in logs.
        //
        // Now returns ResponseEntity.status(403) directly.
        // The gateway JwtFilter already blocks non-admins, so this is a
        // belt-and-suspenders safety check inside the service itself.
        // ─────────────────────────────────────────────────────────────
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body("Access Denied: Admin only");
        }

        return ResponseEntity.ok(operatorService.addPlan(plan));
    }

    @PutMapping("/plans/{planId}")
    public ResponseEntity<String> updatePlan(
            @PathVariable Long planId,
            @RequestBody Plan updatedPlan,
            @RequestHeader("X-Role") String role) {

        // FIX: Same as above — was throw RuntimeException, now returns 403
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body("Access Denied: Admin only");
        }

        return ResponseEntity.ok(operatorService.updatePlan(planId, updatedPlan));
    }

    @DeleteMapping("/plans/{planId}")
    public ResponseEntity<String> deletePlan(
            @PathVariable Long planId,
            @RequestHeader("X-Role") String role) {

        // FIX: Same as above — was throw RuntimeException, now returns 403
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body("Access Denied: Admin only");
        }

        return ResponseEntity.ok(operatorService.deletePlan(planId));
    }
}