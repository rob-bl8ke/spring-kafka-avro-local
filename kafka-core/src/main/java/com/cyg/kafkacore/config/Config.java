package com.cyg.kafkacore.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class Config {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${aws.region}")
    private String awsRegion;

    @Value("${security.protocol}")
    private String securityProtocol;

    @Value("${sasl.mechanism}")
    private String saslMechanism;

    @Value("${sasl.jaas.config}")
    private String jaasConfig;

    @Value("${sasl.client.callback.handler.class}")
    private String handlerClass;

    @Value("${aws.glue.schema-registry.registry-name}")
    private String registryName;
}
