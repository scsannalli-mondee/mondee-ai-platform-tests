package com.aiservices;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.utils.JsonFileReader;

public class ApiClient {
    private static final String CONFIG_PATH = "/config/app_config.json";
    private final String baseUrl;
    private final String experienceServiceUrl;
    private final String aiServicesUrl;

    public ApiClient() throws IOException {
        this.baseUrl = JsonFileReader.getEnvBaseUrl(CONFIG_PATH);
        this.experienceServiceUrl = JsonFileReader.getExperienceServiceURL(CONFIG_PATH);
        this.aiServicesUrl = JsonFileReader.getAiServicesURL(CONFIG_PATH);
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

    public JsonNode postApiData(String endpoint, String jsonBody) {
        try {
            String fullUrl = baseUrl + endpoint;
            HttpResponse<JsonNode> response = Unirest.post(fullUrl)
                    .header("Content-Type", "application/json")
                    .body(jsonBody)
                    .asJson();
            JsonNode body = response.getBody();
            System.out.println(body.getObject().toString());
            return body;
        } catch (Exception e) {
            throw new RuntimeException("Failed to post API data: " + e.getMessage(), e);
        }
    }


    public String postApiDataForVideo(String endpoint, String jsonBody) {
        try {
            System.out.println("Posting API data for video: " + jsonBody);
            String fullUrl = baseUrl + endpoint;
            HttpResponse<byte[]> response = Unirest.post(fullUrl)
                    .header("Content-Type", "application/json")
                    .body(jsonBody).asBytes();
                
            byte[] body = response.getBody();
            String videoPath = "videos/" + System.currentTimeMillis() + ".mp4";
            System.out.println("Video path: " + Paths.get(videoPath).toAbsolutePath().toString());
            Files.write(Paths.get(videoPath), body);
            System.out.println("Saved video to: " + videoPath);
            System.out.println(body);
            return body.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to post API data: " + e.getMessage(), e);
        }
    }

    public Object postApiDataForVideoAsync(String string, String jsonBody) {
        try {
            System.out.println("Posting API data for video: " + jsonBody);
            String fullUrl = baseUrl + string;
            HttpResponse<JsonNode> response = Unirest.post(fullUrl)
                    .header("Content-Type", "application/json")
                    .body(jsonBody).asJson();
                
            JsonNode body = response.getBody();
            System.out.println(body.getObject().toString());
            return body.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to post API data: " + e.getMessage(), e);
        }
    }

    public Object generateImageDescription(String string, String jsonBody) {
        try {
            System.out.println("Posting API data for video: " + jsonBody);
            String fullUrl = experienceServiceUrl + string;
            HttpResponse<JsonNode> response = Unirest.post(fullUrl)
                    .header("Content-Type", "application/json")
                    .body(jsonBody).asJson();
                
            JsonNode body = response.getBody();
            System.out.println(body.getObject().toString());
            return body.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to post API data: " + e.getMessage(), e);
        }
    }

    public Object executeAIPrompt(String endpoint, String jsonBody) {
        try {
            System.out.println("Posting AI prompt request: " + jsonBody);
            String fullUrl = aiServicesUrl + endpoint;
            System.out.println("Full URL: " + fullUrl);
            HttpResponse<JsonNode> response = Unirest.post(fullUrl)
                .header("Content-Type", "application/json")
                .body(jsonBody)
                .asJson();

            System.out.println("Response code: " + response.getStatus());
            
            JsonNode body = response.getBody();
            if (body != null) {
                String responseStr;
                if (body.getObject() != null) {
                    responseStr = body.getObject().toString();
                } else if (body.getArray() != null) {
                    responseStr = body.getArray().toString();
                } else {
                    responseStr = body.toString();
                }
                System.out.println("AI Prompt Response: " + responseStr);
                return responseStr;
            } else {
                System.out.println("Response body is null");
                return "Response body is null - Status: " + response.getStatus();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute AI prompt: " + e.getMessage(), e);
        }
    }

    public Object generateFlightRecommendation(String string, String jsonBody) {
        try {
            System.out.println("Posting API data for flight recommendation: " + jsonBody);
            String fullUrl = "http://34.75.166.221" + string;
            HttpResponse<JsonNode> response = Unirest.post(fullUrl)
                .header("Content-Type", "application/json")
                .body(jsonBody)
                .asJson();

            JsonNode body = response.getBody();
            System.out.println("Response code: " + response.getStatus());
            System.out.println(body.getObject().toString());
            return body.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to post API data: " + e.getMessage(), e);
        }
    }

    public Object createExperienceWithPackage(String endpoint, String jsonBody) {
        try {
            System.out.println("Creating experience with package: " + jsonBody);
            // Remove leading slash from endpoint if experienceServiceUrl already has trailing slash
            String cleanEndpoint = endpoint.startsWith("/") ? endpoint.substring(1) : endpoint;
            String fullUrl = experienceServiceUrl + cleanEndpoint;
            System.out.println("Full URL: " + fullUrl);
            HttpResponse<JsonNode> response = Unirest.post(fullUrl)
                .header("Content-Type", "application/json")
                .header("x-api-key", "default-auth-key")
                .body(jsonBody)
                .asJson();

            System.out.println("Response code: " + response.getStatus());
            
            JsonNode body = response.getBody();
            if (body != null) {
                String responseStr;
                if (body.getObject() != null) {
                    responseStr = body.getObject().toString();
                } else if (body.getArray() != null) {
                    responseStr = body.getArray().toString();
                } else {
                    responseStr = body.toString();
                }
                System.out.println("Experience Builder Response: " + responseStr);
                return responseStr;
            } else {
                System.out.println("Response body is null");
                return "Response body is null - Status: " + response.getStatus();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create experience: " + e.getMessage(), e);
        }
    }
}
