package com.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.aiservices.ApiClient;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GenericValidations {
    
    public static String waitForVideoProcessing(ApiClient client, Object response) {
        JsonObject responseBodyObj = JsonParser.parseString(response.toString()).getAsJsonObject();
        String videoId = responseBodyObj.get("id").getAsString();
        String status = responseBodyObj.get("status").getAsString();
        assertEquals("processing", status);
        System.out.println(videoId);
        int count = 0;
        while (!"completed".equals(status)) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            var videoStatusResponse = client.getApiData("/video-status/" + videoId);
            JsonObject videoStatusResponseObj = JsonParser.parseString(videoStatusResponse.toString()).getAsJsonObject();
            status = videoStatusResponseObj.get("status").getAsString();
            System.out.println(status);
            if (count++ > 20) {
                fail("Video status was not 'completed' after 2 minute");
            }
        }
        return videoId;
    }
}
