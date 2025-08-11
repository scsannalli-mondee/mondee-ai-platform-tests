package com.experienceCategoriesTests;

import com.aiservices.ApiClient;
import com.aiservices.Constants;
import com.utils.JsonFileReader;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;

/**
 * Test class to validate AI-generated experience categories
 * Tests the accuracy and relevance of categories generated for different
 * experience types
 */
public class ExperienceCategoriesTests {

    private ApiClient client;

    @ParameterizedTest
    @ValueSource(strings = { "Activity", "Events", "Stay", "Food", "Tours", "Attraction", "Rentals", "Transport",
            "Service" })
    // @ValueSource(strings = { "events"})
    @Tag("smoke")
    @Tag("regression")
    public void testGenerateExperienceCategories(String experienceCategory) throws IOException {
        client = new ApiClient();
        System.out.println("Starting Experience Categories Generation Test for: " + experienceCategory);
        String categoryJsonString = JsonFileReader.readJsonFile("categories/categoryJson.json");

        String descriptionFileName = experienceCategory.toLowerCase() + "_description.json";

        String descriptionJsonString = JsonFileReader.readJsonFile("categories/" + descriptionFileName);
        JsonObject descriptionData = JsonParser.parseString(descriptionJsonString).getAsJsonObject();
        JsonObject requestParams = descriptionData.getAsJsonObject("requestParams");
        requestParams.addProperty("categoryJson", categoryJsonString);
        Object responseObj = client.executeAIPrompt(Constants.EXECUTE_PROMPT_ENDPOINT, descriptionData.toString());
       
        assertNotNull(responseObj, "Response should not be null");
        String responseString = responseObj.toString();
        JsonObject responseObject = JsonParser.parseString(responseString).getAsJsonObject();
        assertTrue(responseObject.has("experienceCategory"), "Response should contain experienceCategory field");
        JsonElement experienceCategoryElement = responseObject.get("experienceCategory");
        assertNotNull(experienceCategoryElement, "experienceCategory should not be null");
        boolean foundCategory = false;
        for (JsonElement element : experienceCategoryElement.getAsJsonArray()) {
            if (element.getAsString().equalsIgnoreCase(experienceCategory)) {
                foundCategory = true;
                break;
            }
        }
        assertTrue(foundCategory, "experienceCategory array should contain: " + experienceCategory);
        System.out.println("✅ Found " + experienceCategory + " in experienceCategory array: " + experienceCategoryElement);
    }
}
