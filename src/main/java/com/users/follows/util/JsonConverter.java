package com.users.follows.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import port.org.json.JSONObject;

public class JsonConverter {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public <T> T convert(JSONObject jsonObject, Class<T> targetClass) throws Exception {
        return objectMapper.readValue(jsonObject.toString(), targetClass);
    }
}
