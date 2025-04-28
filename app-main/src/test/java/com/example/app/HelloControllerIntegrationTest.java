package com.example.app;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.cyg.kafkacore.HelloWorldService;
import com.example.app.kafka.ClientProducer;

import static org.mockito.BDDMockito.given;

@WebMvcTest(HelloController.class)
class HelloControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HelloWorldService helloWorldService;

    @MockitoBean
    private ClientProducer clientProducer;

    @Test
    void testHelloEndpoint() throws Exception {
        given(helloWorldService.capitalize("Robbie")).willReturn("Hello, Robbie!");

        mockMvc.perform(get("/hello").param("name", "Robbie"))
            .andExpect(status().isOk())
            .andExpect(content().string("Hello, Robbie!"));
    }
}
