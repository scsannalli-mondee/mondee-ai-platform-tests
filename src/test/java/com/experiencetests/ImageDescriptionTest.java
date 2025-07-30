package com.experiencetests;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.aiservices.ApiClient;
import com.utils.JsonFileReader;


public class ImageDescriptionTest {
    @ParameterizedTest
    @ValueSource(strings = {"random"})
    @DisplayName("Test image description generator")
    @Tag("regression")
    public void testImageDescription(String imageFileName) throws IOException {
        ApiClient client = new ApiClient();
        String jsonBody = JsonFileReader.readJsonFile("imagedescription/" + imageFileName + ".json");
        var response = client.generateImageDescription("experience/ai/generateSummary", jsonBody);
        assertNotNull(response);
    }
}
