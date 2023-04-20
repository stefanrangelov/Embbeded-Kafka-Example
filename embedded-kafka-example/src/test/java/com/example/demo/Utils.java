package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

public class Utils {

    private static TestData testData;

    public static TestData getTestData() {
        if (testData == null) {
            try {
                testData = new ObjectMapper().readValue(new ClassPathResource("data.json").getFile(), TestData.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return testData;
    }
}
