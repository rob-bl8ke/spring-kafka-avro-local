package com.example.domain;

import com.example.utils.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class HelloService {
    public String getGreeting(String name) {
        return "Hello, " + StringUtils.capitalize(name) + "!";
    }
}
