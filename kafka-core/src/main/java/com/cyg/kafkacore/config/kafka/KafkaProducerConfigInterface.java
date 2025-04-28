package com.cyg.kafkacore.config.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

public interface KafkaProducerConfigInterface {
    ProducerFactory<String, Object> producerFactory(String schemaName);

    KafkaTemplate<String, Object> kafkaTemplate(String schemaName);

    KafkaTemplate<String, Object> createKafkaTemplate(String schemaName);
}
