package com.tcc.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * @author lgdamy@raiadrogasil.com on 25/02/2021
 */
@Component
public class OrcamentoListener {

    @Autowired
    private SimpMessagingTemplate stompTemplate;

    @RabbitListener(containerFactory = "rabbitListener", queues = "#{rabbitConfig.orcamentoQueueName()}")
    public void listen(@Payload Orcamento orcamento) {
        stompTemplate.convertAndSend("/topic/proposals", orcamento);
    }
}
