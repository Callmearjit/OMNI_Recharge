package com.recharge_service.recharge_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public TopicExchange rechargeExchange() {
        return new TopicExchange("recharge-exchange");
    }

    @Bean
    public Queue rechargeQueue() {
        return new Queue("recharge-queue", true);
    }

    @Bean
    public Binding binding(Queue rechargeQueue, TopicExchange rechargeExchange) {
        return BindingBuilder
                .bind(rechargeQueue)
                .to(rechargeExchange)
                .with("recharge.success");
    }
}
