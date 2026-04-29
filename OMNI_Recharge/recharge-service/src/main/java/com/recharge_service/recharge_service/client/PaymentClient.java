package com.recharge_service.recharge_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import com.recharge_service.recharge_service.dto.PaymentRequest;
import com.recharge_service.recharge_service.dto.PaymentResponse;

@FeignClient(name = "payment-service")
public interface PaymentClient {

    @PostMapping("/payments")
    PaymentResponse  processPayment(PaymentRequest request);
}