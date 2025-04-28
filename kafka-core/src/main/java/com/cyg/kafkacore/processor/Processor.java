package com.cyg.kafkacore.processor;

import org.apache.avro.specific.SpecificRecord;

public interface Processor<T extends SpecificRecord> {

    void processRecord(T record);
}
