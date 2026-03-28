package com.notification_service.notification_service.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.notification_service.notification_service.dto.TransactionEvent;

@Component
public class RabbitConsumer {

    private static final Logger log = LoggerFactory.getLogger(RabbitConsumer.class);

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @RabbitListener(queues = "recharge-queue")
    public void receiveMessage(String rawMessage) {

        TransactionEvent event;
        try {
            event = objectMapper.readValue(rawMessage, TransactionEvent.class);
        } catch (Exception e) {
            log.error("Failed to deserialize notification message: {}", rawMessage, e);
            return;
        }

        log.info("Notification received for rechargeId: {}", event.getRechargeId());

        System.out.println("==============================");
        System.out.println("Recharge Notification");
        System.out.println("Recharge ID  : " + event.getRechargeId());
        System.out.println("User ID      : " + event.getUserId());
        System.out.println("Amount       : Rs." + event.getAmount());
        System.out.println("Status       : " + event.getStatus());
        System.out.println("Txn Ref      : " + event.getTransactionRef());
        System.out.println("Time         : " + event.getCreatedAt());
        System.out.println("==============================");

        sendEmail(event);
        sendSms(event);
    }

    private void sendEmail(TransactionEvent event) {
        log.info("Email sent to user {} for recharge {}", event.getUserId(), event.getRechargeId());
    }

    private void sendSms(TransactionEvent event) {
        log.info("SMS sent to user {} for recharge {}", event.getUserId(), event.getRechargeId());
    }

	public void receiveMessage(TransactionEvent event) {
		// TODO Auto-generated method stub
		
	}
}