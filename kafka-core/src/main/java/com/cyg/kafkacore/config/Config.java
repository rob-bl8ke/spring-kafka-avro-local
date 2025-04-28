package com.cyg.kafkacore.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Data
@Profile("default")
public class Config {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
}
