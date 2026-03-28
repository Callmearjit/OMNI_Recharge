package com.recharge_service.recharge_service.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitProducer {
	@Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendRechargeEvent(String message) {
        rabbitTemplate.convertAndSend(
        		 "recharge-exchange",    
        		    "recharge.success", 
                message
        );
    }

}
