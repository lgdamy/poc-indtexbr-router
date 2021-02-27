package com.tcc.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

/**
 * @author lgdamy@raiadrogasil.com on 25/02/2021
 */
@Configuration
@EnableRabbit
@ConfigurationProperties(prefix = "config.queue")
public class RabbitConfig {

    @Autowired
    private ConnectionFactory factory;

    @Autowired
    private ObjectMapper mapper;

    public static final String ORCAMENTO = "orcamento.criado";
    public static final String TOPIC = "amq.topic";
    private static final String ID = UUID.randomUUID().toString();
    public static final String ORCAMENTO_QUEUE = ORCAMENTO +"."+ ID;


    @Bean
    public Queue criarOrcamento() {
        return QueueBuilder
                .nonDurable(ORCAMENTO_QUEUE)
                .autoDelete()
                .build();
    }

    @Bean
    public Binding bindCriarOrcamento() {
        return BindingBuilder
                .bind(criarOrcamento())
                .to(new TopicExchange(TOPIC))
                .with(ORCAMENTO);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListener() {
        SimpleRabbitListenerContainerFactory connFactory = new SimpleRabbitListenerContainerFactory();
        connFactory.setConnectionFactory(this.factory);
        connFactory.setMessageConverter(new Jackson2JsonMessageConverter(mapper));
        connFactory.setAutoStartup(true);
        connFactory.setMaxConcurrentConsumers(1);
        connFactory.setConcurrentConsumers(1);
        connFactory.setMissingQueuesFatal(false);
        connFactory.setStartConsumerMinInterval(3000L);
        connFactory.setRecoveryInterval(15000L);
        connFactory.setChannelTransacted(true);
        connFactory.setPrefetchCount(1);
        return connFactory;
    }

    public String orcamentoQueueName() {
        return ORCAMENTO_QUEUE;
    }
}
