package com.operator_service.operator_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.operator_service.operator_service.dto.OperatorRequest;
import com.operator_service.operator_service.dto.OperatorResponse;
import com.operator_service.operator_service.entity.Operator;
import com.operator_service.operator_service.entity.Plan;
import com.operator_service.operator_service.exception.ResourceNotFoundException;
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

    public OperatorResponse createOperator(OperatorRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Operator name must not be blank");
        }
        Operator operator = new Operator();
        operator.setName(request.getName());
        Operator saved = operatorRepository.save(operator);
        return new OperatorResponse(saved.getId(), saved.getName());
    }

    public List<Plan> getPlansByOperator(Long operatorId) {
        if (!operatorRepository.existsById(operatorId)) {
            throw new ResourceNotFoundException("Operator not found with id: " + operatorId);
        }
        return planRepository.findByOperatorId(operatorId);
    }

    public Plan getPlanById(Long planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with id: " + planId));
    }

    public String addPlan(Plan plan) {
        planRepository.save(plan);
        return "Plan added successfully";
    }

    public String updatePlan(Long planId, Plan updatedPlan) {
        Plan existing = planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with id: " + planId));

        existing.setAmount(updatedPlan.getAmount());
        existing.setValidity(updatedPlan.getValidity());
        existing.setOperator(updatedPlan.getOperator());

        planRepository.save(existing);
        return "Plan updated successfully";
    }

    public String deletePlan(Long planId) {
        if (!planRepository.existsById(planId)) {
            throw new ResourceNotFoundException("Plan not found with id: " + planId);
        }
        planRepository.deleteById(planId);
        return "Plan deleted successfully";
    }
}
