package com.operator_service.operator_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
}
