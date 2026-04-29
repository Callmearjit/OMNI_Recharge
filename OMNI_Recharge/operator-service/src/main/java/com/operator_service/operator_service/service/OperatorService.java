package com.operator_service.operator_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.operator_service.operator_service.entity.Operator;
import com.operator_service.operator_service.entity.Plan;
import com.operator_service.operator_service.repository.OperatorRepository;
import com.operator_service.operator_service.repository.PlanRepository;

@Service
public class OperatorService {

    @Autowired
    private OperatorRepository operatorRepository;

    @Autowired
    private PlanRepository planRepository;

    public List<Operator> getAllOperators() {
        return operatorRepository.findAll();
    }

    public List<Plan> getPlansByOperator(Long operatorId) {
        return planRepository.findByOperatorId(operatorId);
    }

    public Plan getPlanById(Long planId) {
        return planRepository.findById(planId).orElseThrow(() -> new RuntimeException("Plan not found"));
    }

    public String addPlan(Plan plan) {
        planRepository.save(plan);
        return "Plan added successfully";
    }

    public String updatePlan(Long planId, Plan updatedPlan) {
        Plan existing = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found with id: " + planId));

        existing.setAmount(updatedPlan.getAmount());
        existing.setValidity(updatedPlan.getValidity());
        existing.setOperator(updatedPlan.getOperator());

        planRepository.save(existing);
        return "Plan updated successfully";
    }

    public String deletePlan(Long planId) {
        if (!planRepository.existsById(planId)) {
            throw new RuntimeException("Plan not found with id: " + planId);
        }
        planRepository.deleteById(planId);
        return "Plan deleted successfully";
    }
}
