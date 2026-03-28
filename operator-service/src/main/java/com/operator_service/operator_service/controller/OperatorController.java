package com.operator_service.operator_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/plans")
    public String addPlan(
            @RequestBody Plan plan,
            @RequestHeader("X-Role") String role) {

        if (!role.equals("ADMIN")) {
            throw new RuntimeException("Access Denied");
        }

        return operatorService.addPlan(plan);
    }

   
    @PutMapping("/plans/{planId}")
    public String updatePlan(@PathVariable Long planId, @RequestBody Plan updatedPlan,
            @RequestHeader("X-Role") String role) {

        if (!role.equals("ADMIN")) {
            throw new RuntimeException("Access Denied");
        }

        return operatorService.updatePlan(planId, updatedPlan);
    }

  
    @DeleteMapping("/plans/{planId}")
    public String deletePlan(
            @PathVariable Long planId,
            @RequestHeader("X-Role") String role) {

        if (!role.equals("ADMIN")) {
            throw new RuntimeException("Access Denied");
        }

        return operatorService.deletePlan(planId);
    }
}
