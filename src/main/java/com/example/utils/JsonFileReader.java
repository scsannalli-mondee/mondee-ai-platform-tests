package com.example.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class JsonFileReader {
    /**
     * Reads the value for the key env.base_url from the given JSON file.
     * 
     * @param filePath The relative or absolute path to the JSON file.
     * @return The value for env.base_url, or null if not found.
     * @throws IOException if file not found or parse error.
     */
    public static String getEnvBaseUrl(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(JsonFileReader.class.getResourceAsStream(filePath)))) {
            JsonObject config = JsonParser.parseReader(reader).getAsJsonObject();
            if (config == null) {
                throw new IOException("Configuration file not found or invalid JSON: " + filePath);
            }
            JsonObject environments = config.getAsJsonObject("environments");
            JsonObject devEnv = environments.getAsJsonObject("development");
            return devEnv.get("base_url").getAsString();
        }
    }
}
