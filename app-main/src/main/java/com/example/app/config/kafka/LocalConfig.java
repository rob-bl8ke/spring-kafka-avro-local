package com.example.app.config.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;

import lombok.Data;

@Configuration
@Data
@Profile({"local", "test"})
@EnableKafka
public class LocalConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
}
