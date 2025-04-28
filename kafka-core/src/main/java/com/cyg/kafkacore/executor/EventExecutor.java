package com.cyg.kafkacore.executor;

import com.cyg.kafkacore.task.EventTask;
import jakarta.annotation.PostConstruct;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EventExecutor implements Runnable {

    private BlockingQueue<Runnable> queue;

    // Future state
    // private static final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();

    @Value("${executor.max.queue.size}")
    private int maxQueueSize;

    @PostConstruct
    public void init() {
        queue = new ArrayBlockingQueue<>(maxQueueSize);
    }

    @Override
    public void run() {
        log.info("Executor has begun running");
        try {
            while (true) {
                if (!queue.isEmpty()) {

                    try {
                        queue.take().run();
                    } catch (InterruptedException e) {
                        log.error("Failed to process OfferTask : ", e);
                    }
                }
            }

        } catch (Exception e) {
            log.error("The OfferExecutor failed to execute !!!", e);
            System.exit(-1);
        }
    }

    public void submitTask(EventTask offer) {
        log.info("Task submitted");

        if (queue.size() < maxQueueSize) {
            queue.add(offer);
        } else {
            log.warn("Queue is full !!! Task will not be added.");
            // Do something
        }
    }
}
