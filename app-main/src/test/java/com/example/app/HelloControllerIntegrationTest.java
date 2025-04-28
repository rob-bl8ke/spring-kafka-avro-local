package com.example.app;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.domain.HelloService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;

@WebMvcTest(HelloController.class)
class HelloControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HelloService helloService;

    @Test
    void testHelloEndpoint() throws Exception {
        given(helloService.getGreeting("Robbie")).willReturn("Hello, Robbie!");

        mockMvc.perform(get("/hello").param("name", "Robbie"))
            .andExpect(status().isOk())
            .andExpect(content().string("Hello, Robbie!"));
    }
}
