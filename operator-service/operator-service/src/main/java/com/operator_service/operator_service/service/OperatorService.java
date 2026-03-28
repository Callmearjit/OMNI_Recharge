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
        return planRepository.findAll()
                .stream()
                .filter(p -> p.getOperator().getId().equals(operatorId))
                .toList();
    }
}
