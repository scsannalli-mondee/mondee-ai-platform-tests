package com.reelgenerator;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.aiservices.ApiClient;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.utils.GenericValidations;
import com.utils.JsonFileReader;

import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;

// @Execution(ExecutionMode.CONCURRENT)
public class ReelBuilderTest {
    @ParameterizedTest
    @ValueSource(strings = {"randoms"})
    @DisplayName("Test Reel Builder API video generation")
    public void testReelBuilderVideoGeneration(String videoDataFileName) throws IOException {
        ApiClient client = new ApiClient();
        String jsonBody = JsonFileReader.readJsonFile("videocreation/" + videoDataFileName + ".json");
        var response = client.postApiDataForVideo("/generate-video", jsonBody);
        assertNotNull(response);
        String responseBody = response;
        assertNotNull(responseBody);
        System.out.println("Video generated successfully: " + response);
    }

    @ParameterizedTest
    @ValueSource(strings = {"random-async"})
    @DisplayName("Test Reel Builder API video generation with async true")
    public void testReelBuilderVideoGenerationAsyncTrue(String videoDataFileName) throws IOException {
        ApiClient client = new ApiClient();
        String jsonBody = JsonFileReader.readJsonFile("videocreation/" + videoDataFileName + ".json");
        var response = client.postApiDataForVideoAsync("/generate-video", jsonBody);
        assertNotNull(response);
        String videoId = GenericValidations.waitForVideoProcessing(client, response);
        var videoStatusResponse = client.getApiData("/video-status/" + videoId);
        JsonObject videoStatusResponseObj = JsonParser.parseString(videoStatusResponse.toString()).getAsJsonObject();
        System.out.println(videoStatusResponseObj.toString());
        String videoStatus = videoStatusResponseObj.get("status").getAsString();
        assertEquals("completed", videoStatus);
        System.out.println("Video status: " + videoStatus);
    } 

}
