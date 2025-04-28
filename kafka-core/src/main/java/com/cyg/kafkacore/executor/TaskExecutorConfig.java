package com.cyg.kafkacore.executor;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TaskExecutorConfig {

    @Autowired
    private EventExecutor executor;

    @PostConstruct
    public void startExecutor() {
        Thread executorThread = new Thread(executor);

        executorThread.start();
    }
}
