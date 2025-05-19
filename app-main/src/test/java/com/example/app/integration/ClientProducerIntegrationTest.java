package com.example.app.integration;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.example.app.kafka.ClientProducer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
public class ClientProducerIntegrationTest {

    @Autowired
    private ClientProducer clientProducer; // Let Spring inject this

    @Test
    public void testKafkaProducer() {
        clientProducer.sendMessage(UUID.randomUUID(), UUID.randomUUID());
        log.info("Kafka Producer Test has just sent a message!");
    }
}