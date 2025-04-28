package com.cyg.kafkacore;

import org.springframework.stereotype.Service;

@Service
public class HelloWorldService {
    public String capitalize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
