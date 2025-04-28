package com.example.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class HelloServiceTest {
    private final HelloService helloService = new HelloService();
    @Test
    void testGreeting() {
        String result = helloService.getGreeting("robbie");
        assertEquals("Hello, Robbie!", result);
    }
}
