package com.recharge_service.recharge_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.recharge_service.recharge_service.entity.Recharge;

// ─────────────────────────────────────────────────────────────────────────────
// FIX: Changed findByUserId signature from Long to String to match
// the updated Recharge.userId field type.
// ─────────────────────────────────────────────────────────────────────────────
public interface RechargeRepository extends JpaRepository<Recharge, Long> {
    Optional<Recharge> findByIdempotencyKey(String key);
    List<Recharge> findByUserId(String userId);
}