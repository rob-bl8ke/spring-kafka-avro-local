package com.cyg.kafkacore.core;

import com.cyg.kafkacore.config.kafka.KafkaProducerConfigInterface;
import java.util.concurrent.CompletableFuture;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

@Slf4j
public abstract class KafkaProducer<T extends SpecificRecordBase> {

    private KafkaTemplate<String, Object> kafkaTemplate;

    protected KafkaProducer(KafkaProducerConfigInterface config, String schemaName) {
        kafkaTemplate = config.createKafkaTemplate(schemaName);
    }

    protected void sendMessage(@NonNull T event, @NonNull CharSequence key, @NonNull String topic)
            throws KafkaException {

        prepareEventSpecificHeader(event);

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, String.valueOf(key), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.debug("Message sent successfully");
            } else {
                log.error("Failed to send message");
                throw new KafkaException("Failed to publish message", ex);
            }
        });
    }

    public abstract void prepareEventSpecificHeader(@NonNull T event);
}
