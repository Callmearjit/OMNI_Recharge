package com.payment_service.payment_service.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // payment-service only PRODUCES to the exchange — it does not consume the queue.
    // The queue (with DLX args) is declared exclusively by notification-service to
    // avoid PRECONDITION_FAILED when both services try to declare the same queue
    // with different arguments.
    @Bean
    public TopicExchange rechargeExchange() {
        return new TopicExchange("recharge-exchange");
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
