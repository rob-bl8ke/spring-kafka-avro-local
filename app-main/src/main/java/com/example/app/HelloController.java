package com.example.app;

import com.cyg.kafkacore.HelloWorldService;
import com.example.app.kafka.ClientProducer;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    private final HelloWorldService helloWorldService;
    private final ClientProducer clientProducer;

    public HelloController(HelloWorldService helloWorldService, ClientProducer clientProducer) {
        this.helloWorldService = helloWorldService;
        this.clientProducer = clientProducer;
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(defaultValue = "world") String name) {
        clientProducer.sendMessage(UUID.randomUUID(), UUID.randomUUID());
        
        return helloWorldService.capitalize(name);

    }
}
