package com.cyg.kafkacore.task;

import com.cyg.kafkacore.processor.Processor;
import org.apache.avro.specific.SpecificRecord;

public class EventTask implements Runnable {

    private SpecificRecord record;
    private Processor processor;

    public EventTask(Processor processor, SpecificRecord record) {
        this.record = record;
        this.processor = processor;
    }

    @Override
    public void run() {
        processor.processRecord(record);
    }
}
