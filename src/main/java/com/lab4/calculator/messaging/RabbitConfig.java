package com.lab4.calculator.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConfig {

    public static final String EXCHANGE = "lab4.exchange";
    public static final String QUEUE = "lab4.async.tasks";
    public static final String ROUTING_KEY = "lab4.async.task";

    @Bean
    public Queue asyncQueue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public org.springframework.amqp.core.Exchange appExchange() {
        return ExchangeBuilder.directExchange(EXCHANGE).durable(true).build();
    }

    @Bean
    public Binding binding(Queue asyncQueue, org.springframework.amqp.core.Exchange appExchange) {
        return BindingBuilder.bind(asyncQueue).to(appExchange).with(ROUTING_KEY).noargs();
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(producerJackson2MessageConverter());
        return template;
    }
}
