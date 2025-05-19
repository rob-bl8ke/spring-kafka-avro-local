package com.example.app.integration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.example.app.config.kafka.LocalConfig;
import com.example.app.config.kafka.LocalKafkaAdminConfig;
import com.example.app.config.kafka.LocalKafkaProducerConfig;
import com.example.app.dto.events.SomethingHappened;
import com.example.app.dto.events.SomethingHappenedBody;
import com.example.app.integration.ClientProducerIntegrationTest.TestConfig;
import com.example.app.kafka.ClientProducer;

import lombok.extern.slf4j.Slf4j;

import static java.util.UUID.randomUUID;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;

@Slf4j
@SpringBootTest(classes = { LocalConfig.class, LocalKafkaAdminConfig.class, LocalKafkaProducerConfig.class, TestConfig.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
@EmbeddedKafka(controlledShutdown = true)
public class ClientProducerIntegrationTest {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private KafkaListenerEndpointRegistry registry;

    @Autowired
    private KafkaTestListener testListener;

    @Autowired
    private ClientProducer clientProducer;

    @Configuration
    @EnableKafka
    static class TestConfig {
        // Define any additional beans or configurations needed for the test
        @Bean
        public KafkaTestListener kafkaTestListener() {
            return new KafkaTestListener();
        }

        // // Add these beans if not already provided by your main config
        // @Bean
        // public ProducerFactory<String, Object> producerFactory(EmbeddedKafkaBroker broker) {
        //     Map<String, Object> props = new HashMap<>(KafkaTestUtils.producerProps(broker));
        //     return new DefaultKafkaProducerFactory<>(props);
        // }

        // @Bean
        // public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        //     return new KafkaTemplate<>(producerFactory);
        // }
    }

    public static class KafkaTestListener {
        AtomicInteger count = new AtomicInteger(0);

        @KafkaListener(groupId = "kafka-integration-test", topics = "context-topic")
        public void listen(@Payload SomethingHappened message) {
            log.info("Received message: {}", message.getBody().getApplicationId());
            count.incrementAndGet();
        }
    }

    @BeforeEach
    public void setUp() {
        testListener.count.set(0);

        // Wait until the partitions are assigned.
        registry.getListenerContainers().stream().forEach(container ->
                ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic()));
    }

    @Test
    void testKafkaProducer() throws Exception {
        // Your test implementation here
         SomethingHappenedBody body = new SomethingHappenedBody();
        body.setApplicationId(randomUUID().toString());
        SomethingHappened message = new SomethingHappened();
        message.setBody(body);

        sendMessage("context-topic", message);

        await().atMost(3, TimeUnit.SECONDS).pollDelay(100, TimeUnit.MILLISECONDS)
                .until(testListener.count::get, equalTo(1));
    }

    private void sendMessage(String topic, SomethingHappened message) throws Exception {
        clientProducer.sendMessage(randomUUID(), randomUUID());
    }
}


// [ERROR] Errors: 
// [ERROR]   ClientProducerIntegrationTest.testKafkaProducer » UnsatisfiedDependency Error creating bean with 
// name 'com.example.app.integration.ClientProducerIntegrationTest': Unsatisfied dependency expressed through field 'registry': 
// No qualifying bean of type 'org.springframework.kafka.config.KafkaListenerEndpointRegistry' available: expected at least 1 bean 
// which qualifies as autowire candidate. 
// Dependency annotations: {@org.springframework.beans.factory.annotation.Autowired(required=true)}