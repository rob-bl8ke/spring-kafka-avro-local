package com.cyg.kafkacore.config.kafka;

import com.amazonaws.services.schemaregistry.utils.AWSSchemaRegistryConstants;
import com.amazonaws.services.schemaregistry.utils.AvroRecordType;
import com.cyg.kafkacore.config.Config;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import software.amazon.awssdk.services.glue.model.DataFormat;

@Slf4j
@Configuration
@Profile("default")
@ConditionalOnProperty(value = "kafka.producer.enabled", havingValue = "true")
public class KafkaProducerConfig implements KafkaProducerConfigInterface {

    private final Config config;

    @Value("${spring.kafka.producer.keySerializer}")
    private String keySerializer;

    @Value("${spring.kafka.producer.valueSerializer}")
    private String valueSerializer;

    @Autowired
    public KafkaProducerConfig(Config config) {
        this.config = config;
    }

    public ProducerFactory<String, Object> producerFactory(String schemaName) {
        Map<String, Object> configProps = new HashMap<>();

        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);

        configProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, config.getSecurityProtocol());
        configProps.put(SaslConfigs.SASL_MECHANISM, config.getSaslMechanism());
        configProps.put(SaslConfigs.SASL_JAAS_CONFIG, config.getJaasConfig());
        configProps.put(SaslConfigs.SASL_CLIENT_CALLBACK_HANDLER_CLASS, config.getHandlerClass());

        configProps.put(AWSSchemaRegistryConstants.AWS_REGION, config.getAwsRegion());
        configProps.put(AWSSchemaRegistryConstants.SCHEMA_NAME, schemaName);
        configProps.put(AWSSchemaRegistryConstants.DATA_FORMAT, DataFormat.AVRO.name());
        configProps.put(AWSSchemaRegistryConstants.REGISTRY_NAME, config.getRegistryName());
        configProps.put(AWSSchemaRegistryConstants.AVRO_RECORD_TYPE, AvroRecordType.SPECIFIC_RECORD.getName());
        configProps.put(AWSSchemaRegistryConstants.SCHEMA_AUTO_REGISTRATION_SETTING, false);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    public KafkaTemplate<String, Object> kafkaTemplate(String schemaName) {
        return new KafkaTemplate<>(producerFactory(schemaName));
    }

    public KafkaTemplate<String, Object> createKafkaTemplate(String schemaName) {
        return kafkaTemplate(schemaName);
    }
}
