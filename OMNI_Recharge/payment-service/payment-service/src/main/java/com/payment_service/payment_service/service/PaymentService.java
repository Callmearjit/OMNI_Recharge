
package com.payment_service.payment_service.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.payment_service.payment_service.dto.PaymentRequest;
import com.payment_service.payment_service.entity.Transaction;
import com.payment_service.payment_service.enums.PaymentStatus;
import com.payment_service.payment_service.repository.TransactionRepository;

@Service
public class PaymentService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public String processPayment(PaymentRequest request) {

        Transaction txn = new Transaction();
        txn.setRechargeId(request.getRechargeId());
        txn.setAmount(request.getAmount());

        // simulate success
        txn.setStatus(PaymentStatus.SUCCESS.name());

        transactionRepository.save(txn);

        // send event
        rabbitTemplate.convertAndSend(
                "recharge-exchange",
                "recharge.success",
                request.getRechargeId()
        );

        return "Payment successful";
    }
}