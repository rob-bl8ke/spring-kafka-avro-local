package com.cyg.kafkacore.config.kafka;

import com.amazonaws.services.schemaregistry.utils.AWSSchemaRegistryConstants;
import com.amazonaws.services.schemaregistry.utils.AvroRecordType;
import com.cyg.kafkacore.config.Config;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.common.config.SaslConfigs;
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

        configProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, config.getSecurityProtocol());
        configProps.put(SaslConfigs.SASL_MECHANISM, config.getSaslMechanism());
        configProps.put(SaslConfigs.SASL_JAAS_CONFIG, config.getJaasConfig());
        configProps.put(SaslConfigs.SASL_CLIENT_CALLBACK_HANDLER_CLASS, config.getHandlerClass());
        configProps.put(AWSSchemaRegistryConstants.AVRO_RECORD_TYPE, AvroRecordType.SPECIFIC_RECORD.getName());

        return new KafkaAdmin(configProps);
    }
}
