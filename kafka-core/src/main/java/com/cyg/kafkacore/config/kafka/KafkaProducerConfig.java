package com.cyg.kafkacore.config.kafka;

import com.cyg.kafkacore.config.Config;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Slf4j
@Configuration
@Profile("default")
@ConditionalOnProperty(value = "kafka.producer.enabled", havingValue = "true")
public class KafkaProducerConfig implements KafkaProducerConfigInterface {

    private final Config config;

    // @Value("${spring.kafka.producer.keySerializer}")
    // private String keySerializer;

    // @Value("${spring.kafka.producer.valueSerializer}")
    // private String valueSerializer;

    @Value("${spring.kafka.producer.key-serializer}")
    private Class<?> keySerializer;

    @Value("${spring.kafka.producer.value-serializer}")
    private Class<?> valueSerializer;

    @Value("${spring.kafka.properties.schema-registry-url}")
    private String schemaRegistryUrl;

    @Autowired
    public KafkaProducerConfig(Config config) {
        this.config = config;
    }

    public ProducerFactory<String, Object> producerFactory(String schemaName) {
        Map<String, Object> configProps = new HashMap<>();

        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);

        configProps.put("schema.registry.url", schemaRegistryUrl);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    public KafkaTemplate<String, Object> kafkaTemplate(String schemaName) {
        return new KafkaTemplate<>(producerFactory(schemaName));
    }

    public KafkaTemplate<String, Object> createKafkaTemplate(String schemaName) {
        return kafkaTemplate(schemaName);
    }
}
