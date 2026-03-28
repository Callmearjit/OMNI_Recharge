package com.notification_service.notification_service.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.notification_service.notification_service.dto.TransactionEvent;



@Component
public class RabbitConsumer {

    private static final Logger log = LoggerFactory.getLogger(RabbitConsumer.class);

    @RabbitListener(queues = "recharge-queue")
    public void receiveMessage(TransactionEvent event) {

        log.info("Notification received for rechargeId: {}", event.getRechargeId());

        // 1. Log the event
        System.out.println("==============================");
        System.out.println("Recharge Notification");
        System.out.println("Recharge ID  : " + event.getRechargeId());
        System.out.println("User ID      : " + event.getUserId());
        System.out.println("Amount       : ₹" + event.getAmount());
        System.out.println("Status       : " + event.getStatus());
        System.out.println("Txn Ref      : " + event.getTransactionRef());
        System.out.println("Time         : " + event.getCreatedAt());
        System.out.println("==============================");

        // 2. Later → send real email/SMS
        sendEmail(event);
        sendSms(event);
    }

    private void sendEmail(TransactionEvent event) {
        // TODO: integrate JavaMailSender or SendGrid
        log.info("Email sent to user {} for recharge {}", event.getUserId(), event.getRechargeId());
    }

    private void sendSms(TransactionEvent event) {
        // TODO: integrate Twill or Fast2SMS
        log.info("SMS sent to user {} for recharge {}", event.getUserId(), event.getRechargeId());
    }
}
