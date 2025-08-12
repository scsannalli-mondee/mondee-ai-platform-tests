package com.experienceCategoriesTests;

import com.aiservices.ApiClient;
import com.aiservices.Constants;
import com.utils.JsonFileReader;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;

/**
 * Test class to validate AI-generated experience categories
 * Tests the accuracy and relevance of categories generated for different
 * experience types
 */
public class ExperienceCategoriesTests {

    private ApiClient client;

    @ParameterizedTest(name = "Generate Categories by category type: {0}")
    @ValueSource(strings = { 
        // "Activity",
    //  "Events", 
    //  "Stay", 
    //  "Food", 
     "Tours", 
    //  "Attraction", 
    //  "Rentals", 
    //  "Transport",
    //  "Service" 
    })
    // @ValueSource(strings = { "events"})
    @Tag("smoke")
    @Tag("regression")
    @DisplayName("Generate and validate AI experience categories for different service types")
    public void testGenerateExperienceCategories(String experienceCategory) throws IOException {
        client = new ApiClient();
        System.out.println("Starting Experience Categories Generation Test for: category_" + experienceCategory);
        String categoryJsonString = JsonFileReader.readJsonFile("categories/categoryJson.json");

        String descriptionFileName = "category_" + experienceCategory.toLowerCase() + "_description.json";

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
        
        // Collect validation errors instead of failing immediately
        StringBuilder allValidationErrors = new StringBuilder();
        
        // Validate experienceTypes are valid sub-categories of experienceCategory
        if (responseObject.has("experienceTypes")) {
            JsonElement experienceTypesElement = responseObject.get("experienceTypes");
            if (experienceTypesElement.isJsonArray()) {
                String validationError = validateExperienceTypes(categoryJsonString, experienceCategoryElement, 
                                                                experienceTypesElement, "category_" + experienceCategory);
                if (validationError != null) {
                    allValidationErrors.append(validationError).append("\n");
                }
            }
        }
        
        // Fail at the end if there were any validation errors, but allow test to complete
        if (allValidationErrors.length() > 0) {
            System.out.println("📋 Test completed with validation errors:");
            System.out.println(allValidationErrors.toString());
            fail("ExperienceType validation failed:\n" + allValidationErrors.toString());
        }
    }

    @ParameterizedTest(name = "Generate Categories by experience description: {0}")
    @ValueSource(strings = { 
        "dandeli_adventure",
        "jaisalmer_safari", 
        "udaipur_leather",
        "channapatna_toys",
        "ujire_wellness",
        "isha_yoga_center",
        "bonalu",
        "gudi_padwa",
        "ar_rahman_concert",
        "dialogue_in_dark"
    })
    @Tag("smoke")
    @Tag("regression")
    @Tag("category-generation-with-description")
    @DisplayName("Validate AI categorization for categories with description scenarios")
    public void testGenerateCategoriesWithDescription(String experienceFile) throws IOException {
        client = new ApiClient();
        System.out.println("Starting Category Generation with Description Test for: " + experienceFile);

        // Load expected categories configuration
        String expectedCategoriesJsonString = JsonFileReader.readJsonFile("categories/expected_categories.json");
        JsonObject expectedCategoriesData = JsonParser.parseString(expectedCategoriesJsonString).getAsJsonObject();
        String expectedCategory = expectedCategoriesData.get(experienceFile).getAsString();
        System.out.println("Expected category for " + experienceFile + ": " + expectedCategory);
        
        // Load category JSON
        String categoryJsonString = JsonFileReader.readJsonFile("categories/categoryJson.json");

        // Load the specific experience description
        String descriptionFileName = experienceFile + "_description.json";
        String descriptionJsonString = JsonFileReader.readJsonFile("categories/" + descriptionFileName);
        JsonObject descriptionData = JsonParser.parseString(descriptionJsonString).getAsJsonObject();
        JsonObject requestParams = descriptionData.getAsJsonObject("requestParams");
        
        // Add categoryJson and experienceCategory to request
        requestParams.addProperty("categoryJson", categoryJsonString);
        System.out.println("Added experienceCategory parameter: " + expectedCategory);
        
        // Execute AI prompt
        Object responseObj = client.executeAIPrompt(Constants.EXECUTE_PROMPT_ENDPOINT, descriptionData.toString());
       
        // Validate response is not null
        assertNotNull(responseObj, "Response should not be null for " + experienceFile);
        String responseString = responseObj.toString();
        JsonObject responseObject = JsonParser.parseString(responseString).getAsJsonObject();
        
        // Validate response structure
        assertTrue(responseObject.has("experienceCategory"), "Response should contain experienceCategory field for " + experienceFile);
        JsonElement experienceCategoryElement = responseObject.get("experienceCategory");
        assertNotNull(experienceCategoryElement, "experienceCategory should not be null for " + experienceFile);
        
        // Validate that the expected category is present in the AI response
        boolean foundExpectedCategory = false;
        if (experienceCategoryElement.isJsonArray()) {
            for (JsonElement element : experienceCategoryElement.getAsJsonArray()) {
                if (element.getAsString().equalsIgnoreCase(expectedCategory)) {
                    foundExpectedCategory = true;
                    break;
                }
            }
        } 
        
        assertTrue(foundExpectedCategory, 
            "AI should categorize '" + experienceFile + "' as '" + expectedCategory + "' but got: " + experienceCategoryElement);
        
        // Collect validation errors instead of failing immediately
        StringBuilder allValidationErrors = new StringBuilder();
        
        // Validate experienceTypes are valid sub-categories of experienceCategory
        if (responseObject.has("experienceTypes")) {
            JsonElement experienceTypesElement = responseObject.get("experienceTypes");
            if (experienceTypesElement.isJsonArray()) {
                String validationError = validateExperienceTypes(categoryJsonString, experienceCategoryElement, 
                                                                experienceTypesElement, experienceFile);
                if (validationError != null) {
                    allValidationErrors.append(validationError).append("\n");
                }
            }
        }
        
        System.out.println("Full Response: " + responseString);
        
        // Fail at the end if there were any validation errors, but allow test to complete
        if (allValidationErrors.length() > 0) {
            System.out.println("📋 Test completed with validation errors:");
            System.out.println(allValidationErrors.toString());
            fail("ExperienceType validation failed:\n" + allValidationErrors.toString());
        }
    }
    
    /**
     * Validates that experienceTypes are valid sub-categories of the generated experienceCategory
     * Returns validation errors instead of failing immediately
     */
    private String validateExperienceTypes(String categoryJsonString, JsonElement experienceCategoryElement, 
                                       JsonElement experienceTypesElement, String testContext) {
        try {
            // Parse the category hierarchy
            JsonObject categoryHierarchy = JsonParser.parseString(categoryJsonString).getAsJsonObject();
            
            // Get all valid experienceTypes for the generated experienceCategories
            Set<String> validExperienceTypes = new HashSet<>();
            for (JsonElement categoryElement : experienceCategoryElement.getAsJsonArray()) {
                String category = categoryElement.getAsString();
                if (categoryHierarchy.has(category)) {
                    JsonObject categoryObject = categoryHierarchy.getAsJsonObject(category);
                    for (String experienceType : categoryObject.keySet()) {
                        validExperienceTypes.add(experienceType);
                    }
                }
            }
            
            // Collect validation errors instead of failing immediately
            StringBuilder validationErrors = new StringBuilder();
            boolean hasErrors = false;
            
            for (JsonElement experienceTypeElement : experienceTypesElement.getAsJsonArray()) {
                String experienceType = experienceTypeElement.getAsString();
                if (!validExperienceTypes.contains(experienceType)) {
                    hasErrors = true;
                    validationErrors.append(String.format(
                        "  ❌ experienceType '%s' is not a valid sub-category of %s. Valid types: %s%n",
                        experienceType, experienceCategoryElement, validExperienceTypes));
                }
            }
            
            if (hasErrors) {
                String errorMessage = String.format("ExperienceType validation failed for %s:%n%s", 
                    testContext, validationErrors.toString());
                System.out.println("⚠️ " + errorMessage);
                return errorMessage;
            } else {
                System.out.println("✅ All experienceTypes are valid sub-categories: " + experienceTypesElement);
                return null; // No errors
            }
            
        } catch (Exception e) {
            String errorMessage = "Failed to validate experienceTypes for " + testContext + ": " + e.getMessage();
            System.out.println("⚠️ " + errorMessage);
            return errorMessage;
        }
    }
}
