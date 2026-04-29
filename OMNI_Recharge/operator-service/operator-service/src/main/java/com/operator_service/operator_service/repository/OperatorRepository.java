package com.operator_service.operator_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.operator_service.operator_service.entity.Operator;

public interface OperatorRepository extends JpaRepository<Operator, Long> {
	
	
}
