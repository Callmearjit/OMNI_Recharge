
package com.payment_service.payment_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payment_service.payment_service.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	boolean existsByIdempotencyKey(String idempotencyKey);
	List<Transaction> findByRechargeId(Long rechargeId);
}