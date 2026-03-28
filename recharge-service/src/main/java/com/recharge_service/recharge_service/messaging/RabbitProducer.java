package com.recharge_service.recharge_service.messaging;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendRechargeEvent(Long rechargeId, Long userId, Double amount,
                                  String status, String transactionRef) {
        Map<String, Object> event = new HashMap<>();
        event.put("rechargeId", rechargeId);
        event.put("userId", userId);
        event.put("amount", amount);
        event.put("status", status);
        event.put("transactionRef", transactionRef != null ? transactionRef : "N/A");
        event.put("createdAt", LocalDateTime.now().toString());

        rabbitTemplate.convertAndSend(
                "recharge-exchange",
                "recharge.success",
                event
        );
    }

	
}