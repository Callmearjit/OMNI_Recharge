
package com.recharge_service.recharge_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.recharge_service.recharge_service.recharge.Recharge;
import com.recharge_service.recharge_service.service.RechargeService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/recharge")
public class RechargeController {

    @Autowired
    private RechargeService rechargeService;

    @PostMapping
    public Recharge recharge(@RequestBody Recharge recharge) {
        return rechargeService.createRecharge(recharge);
    }
}