package com.example;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import java.io.IOException;
import com.example.utils.JsonFileReader;

public class ApiClient {
    private static final String CONFIG_PATH = "/config/app_config.json";
    private final String baseUrl;

    public ApiClient() throws IOException {
        this.baseUrl = JsonFileReader.getEnvBaseUrl(CONFIG_PATH);
    }

    public JsonNode getApiData(String endpoint) {
        try {
            String fullUrl = baseUrl + endpoint;
            HttpResponse<JsonNode> response = Unirest.get(fullUrl)
                    .header("accept", "application/json")
                    .asJson();
            JsonNode body = response.getBody();
            return body;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get API data: " + e.getMessage(), e);
        }
    }
}
