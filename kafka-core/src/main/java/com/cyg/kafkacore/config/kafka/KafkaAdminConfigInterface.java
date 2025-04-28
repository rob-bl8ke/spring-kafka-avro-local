package com.cyg.kafkacore.config.kafka;

import org.springframework.kafka.core.KafkaAdmin;

public interface KafkaAdminConfigInterface {
    KafkaAdmin adminFactory();
}
