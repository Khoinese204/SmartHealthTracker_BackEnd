package com.example.smarthealth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
        // Nếu muốn custom thêm (modules, naming strategy, ...) thì làm ở đây
    }
}
