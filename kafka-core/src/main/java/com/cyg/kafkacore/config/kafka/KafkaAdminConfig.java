package com.cyg.kafkacore.config.kafka;

import com.cyg.kafkacore.config.Config;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

@Slf4j
@Configuration
public class KafkaAdminConfig {

    private final Config config;

    @Autowired
    public KafkaAdminConfig(Config config) {
        this.config = config;
    }

    @Bean
    public KafkaAdmin adminFactory() {

        Map<String, Object> configProps = new HashMap<>();
        configProps.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());

        return new KafkaAdmin(configProps);
    }
}
