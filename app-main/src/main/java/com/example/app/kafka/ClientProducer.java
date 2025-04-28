package com.example.app.kafka;

import com.cyg.kafkacore.config.kafka.KafkaProducerConfigInterface;
import com.cyg.kafkacore.core.KafkaProducer;
import com.example.app.dto.events.Header;
import com.example.app.dto.events.SomethingHappened;
import com.example.app.dto.events.SomethingHappenedBody;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ClientProducer extends KafkaProducer<SomethingHappened> {
    @Value("${kafka.context-topic.name}")
    private String topicName;

    @Value("${kafka.producers.something-happened.event-type}")
    private String eventType;

    @Value("${kafka.producers.something-happened.schema-id}")
    private String schemaId;

    private String sourceId = "kafka-core-spike";

    public ClientProducer(KafkaProducerConfigInterface config, @Value("${kafka.context-topic.name}") String schemaName) {
        super(config, schemaName);
    }

    @Override
    public void prepareEventSpecificHeader(@NonNull SomethingHappened event) {
        event.setHeader(Header.newBuilder(event.getHeader())
                .setEventId(UUID.randomUUID().toString())
                .setChecksumHash(UUID.randomUUID().toString())
                .setRetryCount("2")
                .setPriority("1")
                .setSecurityContext("Nothing Here")
                .setTenantId(UUID.randomUUID().toString())
                .setCausationId(UUID.randomUUID().toString())
                .setCorrelationId(UUID.randomUUID().toString())
                .setContentType("application/avro")
                .setEncoding("UTF-8")
                .setDomain("Whatzit")
                .setTimestamp(Instant.now())
                .setEventType(eventType)
                .setEventVersion("1")
                .setAggregatedId(UUID.randomUUID().toString())
                .setAggregatedType(UUID.randomUUID().toString())
                .setSchemaId(schemaId)
                .setSourceId(sourceId)
                .setBusinessUnit("Domain")
                .setMetadata(new HashMap<>())
                .build());
    }

    public void sendMessage(UUID applicationId, UUID correlationId) {

        SomethingHappenedBody body = SomethingHappenedBody.newBuilder()
                .setApplicationId(applicationId.toString())
                .build();

        // Header header = Header.newBuilder()

        //         .setEventType(eventType)
        //         .setSchemaId(schemaId)
        //         .setSourceId(sourceId)
        //         .build();

        // SomethingHappened event = new SomethingHappened(header, body);
        // sendMessage(event, event.getBody().getApplicationId(), topicName);
        SomethingHappened event = new SomethingHappened();
        event.setBody(body);
        sendMessage(event, UUID.randomUUID().toString(), topicName);
    }
}