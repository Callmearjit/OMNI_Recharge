package com.operator_service.operator_service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.operator_service.operator_service.entity.Operator;
import com.operator_service.operator_service.entity.Plan;
import com.operator_service.operator_service.repository.OperatorRepository;
import com.operator_service.operator_service.repository.PlanRepository;
import com.operator_service.operator_service.service.OperatorService;

@ExtendWith(MockitoExtension.class)
class OperatorServiceTest {

    @Mock
    private OperatorRepository operatorRepository;

    @Mock
    private PlanRepository planRepository;

    @InjectMocks
    private OperatorService operatorService;

    // Get all operators test
    @Test
    void testGetAllOperators() {
        Operator op1 = new Operator();
        op1.setId(1L);
        op1.setName("Jio");

        Operator op2 = new Operator();
        op2.setId(2L);
        op2.setName("Airtel");

        when(operatorRepository.findAll()).thenReturn(Arrays.asList(op1, op2));

        List<Operator> result = operatorService.getAllOperators();

        assertEquals(2, result.size());
        assertEquals("Jio", result.get(0).getName());
    }

    // Get plans by operator
    @Test
    void testGetPlansByOperator() {
        Plan plan = new Plan();
        plan.setId(1L);
        plan.setAmount(299.0);
        plan.setValidity("28 days");

        when(planRepository.findByOperatorId(1L)).thenReturn(Arrays.asList(plan));

        List<Plan> result = operatorService.getPlansByOperator(1L);

        assertEquals(1, result.size());
        assertEquals(299.0, result.get(0).getAmount());
    }

    // Add plan
    @Test
    void testAddPlan() {
        Plan plan = new Plan();
        plan.setAmount(199.0);
        plan.setValidity("14 days");

        when(planRepository.save(plan)).thenReturn(plan);

        String result = operatorService.addPlan(plan);

        assertEquals("Plan added successfully", result);
        verify(planRepository, times(1)).save(plan);
    }

    // Update plan success
    @Test
    void testUpdatePlan_Success() {
        Plan existing = new Plan();
        existing.setId(1L);
        existing.setAmount(199.0);
        existing.setValidity("14 days");

        Plan updated = new Plan();
        updated.setAmount(299.0);
        updated.setValidity("28 days");

        when(planRepository.findById(1L)).thenReturn(Optional.of(existing));

        String result = operatorService.updatePlan(1L, updated);

        assertEquals("Plan updated successfully", result);
        assertEquals(299.0, existing.getAmount());
        assertEquals("28 days", existing.getValidity());
    }

    //Update plan not found
    @Test
    void testUpdatePlan_NotFound() {
        when(planRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> operatorService.updatePlan(99L, new Plan()));

        assertEquals("Plan not found with id: 99", ex.getMessage());
    }

    //Delete plan success
    @Test
    void testDeletePlan_Success() {
        when(planRepository.existsById(1L)).thenReturn(true);

        String result = operatorService.deletePlan(1L);

        assertEquals("Plan deleted successfully", result);
        verify(planRepository, times(1)).deleteById(1L);
    }

    //Delete plan not found
    @Test
    void testDeletePlan_NotFound() {
        when(planRepository.existsById(99L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> operatorService.deletePlan(99L));

        assertEquals("Plan not found with id: 99", ex.getMessage());
    }
}
