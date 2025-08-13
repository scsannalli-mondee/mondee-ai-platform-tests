package com.experienceCategoriesTests;

import com.aiservices.ApiClient;
import com.aiservices.Constants;
import com.utils.JsonFileReader;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Test class to validate specific AI-generated experienceTypes
 * Tests the accuracy of experienceTypes generated for different experience descriptions
 * targeting specific experienceType categories within each main category
 */
public class ExperienceTypeValidationTests {

    private ApiClient client;

    /**
     * Validates that AI generates valid experienceTypes that belong to proper categories
     * This test ensures experienceTypes are valid sub-categories according to categoryJson
     */
    @ParameterizedTest(name = "Validate experienceTypes for: {0}")
    @ValueSource(strings = { 
        // Activity - Adventure Sports
        "skydiving_rishikesh", "bungee_jumping_goa", "whitewater_rafting_manali",
        // Activity - Creative Workshops  
        "pottery_classes_jaipur", "cooking_classes_kerala", "dance_workshops_bangalore",
        // Activity - Health & Wellness
        "yoga_retreats_goa", "ayurveda_treatments_kerala", "meditation_centers_dharamshala",
        
        // Events - Festivals
        "holi_festival_mathura", "diwali_celebrations_delhi",
        
        // Stay - Boutique Lodgings
        "heritage_hotels_rajasthan",
        // Stay - Eco-Stays
        "treehouse_lodge_costa_rica",
        // Stay - Farm Stays
        "farmstay_provence",
        
        // Tours - Cultural Tours
        "heritage_walks_delhi",
        // Tours - Nature & Wildlife Tours
        "wildlife_safari_kenya",
        // Tours - Cycling Tours
        "cycling_tour_ireland",
        
        // Attraction - Historical Sites
        "ancient_temples_tamil_nadu",
        // Attraction - Natural Attractions
        "natural_park_iceland",
        // Attraction - Museums & Galleries
        "art_museum_tokyo",
        
        // Rentals - Vehicle Rentals
        "motorbike_rentals_goa",
        // Rentals - Adventure Gear Rentals
        "camping_gear_rockies",
        // Rentals - Water Rentals
        "yacht_rental_mediterranean",
        
        // Food - Food Tastings
        "wine_tasting_napa",
        // Food - Dining Experiences
        "family_dinner_tuscany",
        // Food - Food Workshops
        "bread_making_paris",
        
        // Transport - Scenic Train Rides
        "scenic_train_switzerland",
        // Transport - Ferries
        "island_ferry_maldives",
        // Transport - Private Drivers
        "private_driver_dubai",
        
        // Service - Resorts
        "resort_concierge_bali",
        // Service - Workshop / Hobby Based Class
        "workshop_support_florence",
        // Service - Walking Tours
        "walking_tour_support_rome"
    })
    @Tag("experienceType")
    @Tag("validation")
    @DisplayName("Validate experienceType generation for experience descriptions")
    public void testExperienceTypeGeneration(String experienceKey) throws IOException {
        client = new ApiClient();
        System.out.println("Starting ExperienceType Validation Test for: " + experienceKey);
        
        // Load category structure for validation
        String categoryJsonString = JsonFileReader.readJsonFile("categories/categoryJson.json");
        JsonArray categoryArray = JsonParser.parseString(categoryJsonString).getAsJsonArray();
        JsonObject categoryJson = categoryArray.get(0).getAsJsonObject();
        
        // Load experience description
        String descriptionFileName = experienceKey + "_description.json";
        String descriptionJsonString = JsonFileReader.readJsonFile("experienceTypes/" + descriptionFileName);
        JsonObject descriptionData = JsonParser.parseString(descriptionJsonString).getAsJsonObject();
        
        // Get the requestParams and add categoryJson
        JsonObject requestParams = descriptionData.getAsJsonObject("requestParams");
        requestParams.addProperty("categoryJson", categoryJsonString);
        
        List<String> validationErrors = new ArrayList<>();
        
        // Make API call with retry logic
        Object responseObj = null;
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                System.out.println("Calling AI API (Attempt " + attempt + "/3)");
                responseObj = client.executeAIPrompt(Constants.EXECUTE_PROMPT_ENDPOINT, descriptionData.toString());
                
                if (responseObj != null) {
                    break;
                }
            } catch (Exception e) {
                System.err.println("Attempt " + attempt + " failed: " + e.getMessage());
                if (attempt == 3) {
                    validationErrors.add("Failed to get AI response after 3 attempts: " + e.getMessage());
                }
            }
        }
        
        if (responseObj == null) {
            validationErrors.add("Empty or null response from AI API");
        } else {
            String responseBody = responseObj.toString();
            System.out.println("AI Prompt Response: " + responseBody);
            
            try {
                JsonObject aiResponse = JsonParser.parseString(responseBody).getAsJsonObject();
                
                // Extract experienceTypes from AI response
                JsonArray experienceTypesArray = aiResponse.getAsJsonArray("experienceTypes");
                if (experienceTypesArray == null || experienceTypesArray.size() == 0) {
                    validationErrors.add("No experienceTypes found in AI response");
                } else {
                    List<String> generatedTypes = new ArrayList<>();
                    
                    for (JsonElement typeElement : experienceTypesArray) {
                        String generatedType = typeElement.getAsString();
                        generatedTypes.add(generatedType);
                    }
                    
                    System.out.println("✅ Generated experienceTypes: " + generatedTypes);
                    
                    // Validate that all generated experienceTypes are valid according to categoryJson
                    String experienceTypeValidationErrors = validateExperienceTypes(generatedTypes, categoryJson);
                    if (!experienceTypeValidationErrors.isEmpty()) {
                        validationErrors.add(experienceTypeValidationErrors);
                    } else {
                        System.out.println("✅ All experienceTypes are valid sub-categories: " + generatedTypes);
                    }
                    
                    // Check s_tag value to determine if secondary validation should be performed
                    String sTagValue = requestParams.get("s_tag").getAsString();
                    boolean shouldValidateSecondaryTags = !"[False]".equals(sTagValue) && !"False".equals(sTagValue);
                    
                    // Also validate secondaryTags experienceTypes if they exist and s_tag allows it
                    if (shouldValidateSecondaryTags && aiResponse.has("secondaryTags")) {
                        JsonObject secondaryTags = aiResponse.getAsJsonObject("secondaryTags");
                        if (secondaryTags.has("experienceTypes")) {
                            JsonArray secondaryExperienceTypesArray = secondaryTags.getAsJsonArray("experienceTypes");
                            List<String> secondaryTypes = new ArrayList<>();
                            for (JsonElement typeElement : secondaryExperienceTypesArray) {
                                secondaryTypes.add(typeElement.getAsString());
                            }
                            
                            String secondaryValidationErrors = validateExperienceTypes(secondaryTypes, categoryJson);
                            if (!secondaryValidationErrors.isEmpty()) {
                                validationErrors.add("Secondary experienceTypes validation failed: " + secondaryValidationErrors);
                            } else {
                                System.out.println("✅ All secondary experienceTypes are valid sub-categories: " + secondaryTypes);
                            }
                        }
                    } else if (!shouldValidateSecondaryTags) {
                        System.out.println("⏭️ Skipping secondary experienceTypes validation (s_tag is " + sTagValue + ")");
                    }
                }
                
            } catch (Exception e) {
                validationErrors.add("Failed to parse AI response JSON: " + e.getMessage());
            }
        }
        
        // Report all validation errors at the end
        if (!validationErrors.isEmpty()) {
            String errorMessage = "Validation failed for " + experienceKey + ":\n" + 
                                 String.join("\n", validationErrors);
            fail(errorMessage);
        }
        
        System.out.println("✅ Validation passed for " + experienceKey);
    }
    
    /**
     * Validates that experienceTypes are valid sub-categories according to categoryJson structure
     */
    private String validateExperienceTypes(List<String> experienceTypes, JsonObject categoryJson) {
        List<String> invalidTypes = new ArrayList<>();
        List<String> mainCategoryNames = new ArrayList<>();
        
        for (String experienceType : experienceTypes) {
            boolean isValid = false;
            
            // First check if this is a main category name (which is invalid)
            if (categoryJson.has(experienceType)) {
                mainCategoryNames.add(experienceType);
                continue;
            }
            
            // Check each main category for valid sub-categories
            for (String mainCategory : categoryJson.keySet()) {
                JsonObject categoryData = categoryJson.getAsJsonObject(mainCategory);
                
                // Check if experienceType exists as a sub-category
                if (categoryData.has(experienceType)) {
                    isValid = true;
                    break;
                }
            }
            
            if (!isValid) {
                invalidTypes.add(experienceType);
            }
        }
        
        StringBuilder errorMessage = new StringBuilder();
        
        if (!mainCategoryNames.isEmpty()) {
            errorMessage.append("Main category names used as experienceTypes (invalid): ").append(mainCategoryNames).append(". ");
        }
        
        if (!invalidTypes.isEmpty()) {
            errorMessage.append("Invalid experienceTypes not found in categoryJson: ").append(invalidTypes);
        }
        
        return errorMessage.toString();
    }
}
