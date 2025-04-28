package com.example.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class StringUtilsTest {
    @Test
    void testCapitalize() {
        assertEquals("Hello", StringUtils.capitalize("hello"));
        assertEquals("World", StringUtils.capitalize("world"));
        assertEquals(null, StringUtils.capitalize(null));
        assertEquals("", StringUtils.capitalize(""));
    }
}
