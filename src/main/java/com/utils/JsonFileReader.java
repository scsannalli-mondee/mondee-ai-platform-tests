package com.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonFileReader {
    /**
     * Reads JSON content from a file in the resources/data directory.
     * 
     * @param relativePath The relative path to the JSON file from
     *                     resources/data folder at project root
     * @return The JSON content as a String
     * @throws IOException if file not found or read error
     */
    public static String readJsonFile(String relativePath) throws IOException {
        return new String(Files.readAllBytes(
                Paths.get("resources/data/" + relativePath)));
    }

    /**
     * Reads the value for the key env.base_url from the given JSON file.
     * 
     * @param filePath The relative or absolute path to the JSON file.
     * @return The value for env.base_url, or null if not found.
     * @throws IOException if file not found or parse error.
     */
    public static String getEnvBaseUrl(String filePath) throws IOException {
        try (var reader = new BufferedReader(
                new InputStreamReader(JsonFileReader.class.getResourceAsStream(filePath)))) {
            JsonElement configElement = JsonParser.parseReader(reader);
            if (configElement == null) {
                throw new IOException("Configuration file not found or invalid JSON: " + filePath);
            }
            JsonObject config = configElement.getAsJsonObject();
            String env = config.has("environment") ? config.get("environment").getAsString() : "UAT";
            JsonObject environments = config.getAsJsonObject("environments");
            JsonObject envObj = environments.getAsJsonObject(env);
            return envObj.get("base_url").getAsString();
        }
    }

    public static String getExperienceServiceURL(String configPath) throws IOException {
        try (var reader = new BufferedReader(
                new InputStreamReader(JsonFileReader.class.getResourceAsStream(configPath)))) {
            JsonElement configElement = JsonParser.parseReader(reader);
            if (configElement == null) {
                throw new IOException("Configuration file not found or invalid JSON: " + configPath);
            }
            JsonObject config = configElement.getAsJsonObject();
            String env = config.has("environment") ? config.get("environment").getAsString() : "UAT";
            JsonObject environments = config.getAsJsonObject("environments");
            JsonObject envObj = environments.getAsJsonObject(env);
            return envObj.get("experience_service_url").getAsString();
        }
    }

    public static String getAiServicesURL(String configPath) throws IOException {
        try (var reader = new BufferedReader(
                new InputStreamReader(JsonFileReader.class.getResourceAsStream(configPath)))) {
            JsonElement configElement = JsonParser.parseReader(reader);
            if (configElement == null) {
                throw new IOException("Configuration file not found or invalid JSON: " + configPath);
            }
            JsonObject config = configElement.getAsJsonObject();
            String env = config.has("environment") ? config.get("environment").getAsString() : "UAT";
            JsonObject environments = config.getAsJsonObject("environments");
            JsonObject envObj = environments.getAsJsonObject(env);
            return envObj.get("ai_services_url").getAsString();
        }
    }
}
