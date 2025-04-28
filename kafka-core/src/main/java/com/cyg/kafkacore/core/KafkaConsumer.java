package com.cyg.kafkacore.core;

import com.cyg.kafkacore.config.kafka.KafkaConsumerConfig;
import com.cyg.kafkacore.executor.EventExecutor;
import com.cyg.kafkacore.task.EventTask;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

public abstract class KafkaConsumer<T extends SpecificRecord> {

    private final EventExecutor executor;

    public KafkaConsumer(EventExecutor executor) {
        this.executor = executor;
    }

    protected abstract void consumeMessage(ConsumerRecord<String, T> consumerRecord) throws KafkaException;

    protected abstract ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            KafkaConsumerConfig consumerConfig);

    protected void processMessage(EventTask eventTask) {
        executor.submitTask(eventTask);
    }
}
