package com.productiontools.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JiraObjectMapper implements ObjectMapper {
        private final com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper  = new com.fasterxml.jackson.databind.ObjectMapper();

        @Override
        public <T> T readValue(String value, Class<T> valueType) {
            try {
                return jacksonObjectMapper.readValue(value, valueType);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String writeValue(Object value) {
            try {
                return jacksonObjectMapper.writeValueAsString(value);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
}
