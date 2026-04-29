package com.operator_service.operator_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.operator_service.operator_service.entity.Plan;

public interface PlanRepository extends JpaRepository<Plan, Long> {


}
