package com.operator_service.operator_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import com.operator_service.operator_service.dto.ErrorDTO;
import com.operator_service.operator_service.dto.OperatorRequest;
import com.operator_service.operator_service.dto.OperatorResponse;
import com.operator_service.operator_service.entity.Operator;
import com.operator_service.operator_service.entity.Plan;
import com.operator_service.operator_service.service.OperatorService;

@RestController
@RequestMapping("/operators")
public class OperatorController {

    @Autowired
    private OperatorService operatorService;

    // ── Public read endpoints ─────────────────────────────────────────
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
    @PostMapping
    public ResponseEntity<?> createOperator(
            @RequestBody OperatorRequest request,
            @RequestHeader("X-Role") String role) {

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorDTO(HttpStatus.FORBIDDEN.value(), "Access Denied: Admin only"));
        }

        OperatorResponse response = operatorService.createOperator(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/plans")
    public ResponseEntity<?> addPlan(
            @RequestBody Plan plan,
            @RequestHeader("X-Role") String role) {

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorDTO(HttpStatus.FORBIDDEN.value(), "Access Denied: Admin only"));
        }

        return ResponseEntity.ok(operatorService.addPlan(plan));
    }

    @PutMapping("/plans/{planId}")
    public ResponseEntity<?> updatePlan(
            @PathVariable Long planId,
            @RequestBody Plan updatedPlan,
            @RequestHeader("X-Role") String role) {

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorDTO(HttpStatus.FORBIDDEN.value(), "Access Denied: Admin only"));
        }

        return ResponseEntity.ok(operatorService.updatePlan(planId, updatedPlan));
    }

    @DeleteMapping("/plans/{planId}")
    public ResponseEntity<?> deletePlan(
            @PathVariable Long planId,
            @RequestHeader("X-Role") String role) {

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorDTO(HttpStatus.FORBIDDEN.value(), "Access Denied: Admin only"));
        }

        return ResponseEntity.ok(operatorService.deletePlan(planId));
    }
}
