package ru.cti.omiliachatbot.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;

public class PromptMapper {

    public static ArrayList<Prompt> convert(String jsonString) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<Prompt> comments = objectMapper.readValue(jsonString, new TypeReference<ArrayList<Prompt>>() {
        });

        return comments;
    }
}
