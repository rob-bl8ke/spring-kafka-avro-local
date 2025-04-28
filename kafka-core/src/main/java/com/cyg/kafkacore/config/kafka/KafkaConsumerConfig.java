package com.cyg.kafkacore.config.kafka;

import com.amazonaws.services.schemaregistry.utils.AWSSchemaRegistryConstants;
import com.amazonaws.services.schemaregistry.utils.AvroRecordType;
import com.cyg.kafkacore.config.Config;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import software.amazon.awssdk.services.glue.model.DataFormat;

@Slf4j
@EnableKafka
@Configuration
@ConditionalOnProperty(name = "kafka.consumer.enabled", havingValue = "true", matchIfMissing = false)
public class KafkaConsumerConfig {

    private final Config config;

    @Value("${spring.kafka.consumer.keyDeserializer}")
    private String keyDeserializer;

    @Value("${spring.kafka.consumer.valueDeserializer}")
    private String valueDeserializer;

    @Value("${spring.kafka.consumer.keyDelegateDeserializer}")
    private String keyDelegateDeserializer;

    @Value("${spring.kafka.consumer.valueDelegateDeserializer}")
    private String valueDelegateDeserializer;

    @Autowired
    public KafkaConsumerConfig(Config config) {
        this.config = config;
    }

    public ConsumerFactory<String, Object> consumerFactory(String schemaName) {
        Map<String, Object> configProps = new HashMap<>();

        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());

        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);

        configProps.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, keyDelegateDeserializer);
        configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, valueDelegateDeserializer);

        configProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, config.getSecurityProtocol());
        //        configProps.put(CommonClientConfigs.DEFAULT_SECURITY_PROTOCOL, securityProtocol);
        configProps.put(SaslConfigs.SASL_MECHANISM, config.getSaslMechanism());
        configProps.put(SaslConfigs.SASL_JAAS_CONFIG, config.getJaasConfig());
        configProps.put(SaslConfigs.SASL_CLIENT_CALLBACK_HANDLER_CLASS, config.getHandlerClass());

        configProps.put(AWSSchemaRegistryConstants.AWS_REGION, config.getAwsRegion());
        configProps.put(AWSSchemaRegistryConstants.SCHEMA_NAME, schemaName);
        configProps.put(AWSSchemaRegistryConstants.DATA_FORMAT, DataFormat.AVRO.name());
        configProps.put(AWSSchemaRegistryConstants.REGISTRY_NAME, config.getRegistryName());
        configProps.put(AWSSchemaRegistryConstants.AVRO_RECORD_TYPE, AvroRecordType.SPECIFIC_RECORD.getName());

        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    private ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(String schemaName) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(schemaName));
        return factory;
    }

    // Method to create a KafkaListenerContainerFactory with a specific schema name
    public ConcurrentKafkaListenerContainerFactory<String, Object> createKafkaListenerContainerFactory(
            String schemaName) {
        return kafkaListenerContainerFactory(schemaName);
    }
}
