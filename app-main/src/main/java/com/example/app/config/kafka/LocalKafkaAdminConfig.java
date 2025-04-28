package com.example.app.config.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaAdmin;

import com.cyg.kafkacore.config.kafka.KafkaAdminConfigInterface;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@Profile("local")
public class LocalKafkaAdminConfig implements KafkaAdminConfigInterface  {
    
    private final LocalConfig config;

    @Autowired
    public LocalKafkaAdminConfig(LocalConfig config) {
        this.config = config;
    }

    @Bean
    public KafkaAdmin adminFactory() {

        Map<String, Object> configProps = new HashMap<>();
        configProps.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());

        return new KafkaAdmin(configProps);
    }
}
