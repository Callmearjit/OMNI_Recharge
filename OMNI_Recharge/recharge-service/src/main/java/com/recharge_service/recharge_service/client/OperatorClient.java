package com.recharge_service.recharge_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.recharge_service.recharge_service.dto.PlanResponse;

@FeignClient(name = "operator-service")
public interface OperatorClient {

    @GetMapping("/operators/plans/{id}")
    PlanResponse getPlan(@PathVariable Long id);
}
