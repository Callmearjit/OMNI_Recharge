
package com.recharge_service.recharge_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.recharge_service.recharge_service.dto.PaymentRequest;
import com.recharge_service.recharge_service.recharge.Recharge;
import com.recharge_service.recharge_service.repository.RechargeRepository;

@Service
public class RechargeService {

    @Autowired
    private RechargeRepository rechargeRepository;

    @Autowired
    private RestTemplate restTemplate;

    public Recharge createRecharge(Recharge recharge) {

        recharge.setStatus("PENDING");
        Recharge saved = rechargeRepository.save(recharge);

        // call payment service
        PaymentRequest request = new PaymentRequest();
        request.setRechargeId(saved.getId());
        request.setAmount(199.0); // from plan later

        restTemplate.postForObject(
                "http://PAYMENT-SERVICE/payments",
                request,
                String.class
        );

        return saved;
    }
}