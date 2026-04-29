
package com.recharge_service.recharge_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.recharge_service.recharge_service.recharge.Recharge;

public interface RechargeRepository extends JpaRepository<Recharge, Long> {

}