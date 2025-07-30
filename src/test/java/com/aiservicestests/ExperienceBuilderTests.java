package com.aiservicestests;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.Tag;

import com.aiservices.ApiClient;
import com.aiservices.Constants;
import com.utils.JsonFileReader;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;

public class ExperienceBuilderTests {
    
    @ParameterizedTest
    @ValueSource(strings = {"mumbai_spa_heritage_package"})
    @DisplayName("Test Experience Builder - Create Experience with Package")
    @Tag("smoke")
    @Tag("regression")
    public void testCreateExperienceWithPackage(String packageDataFileName) throws IOException {
        ApiClient client = new ApiClient();
        String jsonBody = JsonFileReader.readJsonFile("experiencebuilder/" + packageDataFileName + ".json");
        var response = client.createExperienceWithPackage(Constants.CREATE_EXPERIENCE_ENDPOINT, jsonBody);
        
        assertNotNull(response, "Experience builder response should not be null");
        System.out.println("Experience created successfully for " + packageDataFileName + ": " + response);
        
        // Parse the response JSON
        JsonObject responseObject = JsonParser.parseString(response.toString()).getAsJsonObject();
        responseObject.addProperty("test_filename", packageDataFileName);
        
        // Validate basic response structure
        validateBasicResponseStructure(responseObject);
        
        // Perform specific validations for known test cases
        performSpecificValidations(packageDataFileName, responseObject);
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"bengaluru_bike_rental_service", "mumbai_wedding_photography_service"})
    @DisplayName("Test Experience Builder - Create Experience with Service")
    @Tag("smoke")
    @Tag("regression")
    public void testCreateExperienceWithService(String serviceDataFileName) throws IOException {
        ApiClient client = new ApiClient();
        String jsonBody = JsonFileReader.readJsonFile("experiencebuilder/" + serviceDataFileName + ".json");
        var response = client.createExperienceWithPackage(Constants.CREATE_EXPERIENCE_ENDPOINT, jsonBody);
        
        assertNotNull(response, "Experience builder response should not be null");
        System.out.println("Experience created successfully for service " + serviceDataFileName + ": " + response);
        
        // Parse the response JSON
        JsonObject responseObject = JsonParser.parseString(response.toString()).getAsJsonObject();
        responseObject.addProperty("test_filename", serviceDataFileName);
        
        // Validate basic response structure for service
        validateBasicResponseStructure(responseObject);
        
        // Perform specific validations for known service test cases
        performServiceSpecificValidations(serviceDataFileName, responseObject);
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"invalid_empty_package", "invalid_missing_products", "invalid_malformed_json", "invalid_product_structure"})
    @DisplayName("Test Experience Builder - Negative Cases for Bad Requests")
    @Tag("regression")
    public void testCreateExperienceWithInvalidPackage(String invalidPackageType) throws IOException {
        ApiClient client = new ApiClient();
        String jsonBody = generateInvalidTestData(invalidPackageType);
        
        System.out.println("Testing invalid package type: " + invalidPackageType);
        System.out.println("Request payload: " + jsonBody);
        
        var response = client.createExperienceWithPackage(Constants.CREATE_EXPERIENCE_ENDPOINT, jsonBody);
        
        // For invalid requests, we should get a proper error response
        assertNotNull(response, "Response should not be null even for invalid requests");
        System.out.println("Response for invalid package type " + invalidPackageType + ": " + response);
        
        // Check if the response contains status information
        if (response.toString().contains("Status:")) {
            String responseStr = response.toString();
            
            // For bad requests, we expect 400 status code
            if (invalidPackageType.equals("invalid_malformed_json")) {
                assertTrue(responseStr.contains("400"), 
                          "Malformed JSON should return 400 Bad Request. Got: " + responseStr);
            } else {
                // For missing required fields, we should also get 400
                assertTrue(responseStr.contains("400") || responseStr.contains("422"), 
                          "Missing required fields should return 400 Bad Request or 422 Unprocessable Entity. Got: " + responseStr);
            }
        } else {
            // If response is a JSON object, check for error structure
            try {
                JsonObject responseObject = JsonParser.parseString(response.toString()).getAsJsonObject();
                
                // Check for standard error response structure
                if (responseObject.has("status")) {
                    int statusCode = responseObject.get("status").getAsInt();
                    assertEquals(400, statusCode, 
                               "Invalid requests should return 400 Bad Request status");
                }
                
                if (responseObject.has("error")) {
                    String errorMessage = responseObject.get("error").getAsString();
                    assertEquals("Bad Request", errorMessage, 
                               "Error message should be 'Bad Request'");
                }
                
                // Should not contain valid experience data
                assertFalse(responseObject.has("experiencejson") && 
                           !responseObject.getAsJsonObject("experiencejson").entrySet().isEmpty(),
                           "Invalid requests should not return valid experience data");
                           
            } catch (Exception parseException) {
                fail("Response should be valid JSON with error structure. Got: " + response.toString());
            }
        }
        
        System.out.println("Invalid request properly handled for: " + invalidPackageType);
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"invalid_empty_service", "invalid_missing_description", "invalid_malformed_service_json", "invalid_wrong_channel"})
    @DisplayName("Test Experience Builder - Negative Cases for Service Bad Requests")
    @Tag("regression")
    public void testCreateExperienceWithInvalidService(String invalidServiceType) throws IOException {
        ApiClient client = new ApiClient();
        String jsonBody = generateInvalidServiceTestData(invalidServiceType);
        
        System.out.println("Testing invalid service type: " + invalidServiceType);
        System.out.println("Request payload: " + jsonBody);
        
        var response = client.createExperienceWithPackage(Constants.CREATE_EXPERIENCE_ENDPOINT, jsonBody);
        
        // For invalid requests, we should get a proper error response
        assertNotNull(response, "Response should not be null even for invalid requests");
        System.out.println("Response for invalid service type " + invalidServiceType + ": " + response);
        
        // Check if the response contains status information
        if (response.toString().contains("Status:")) {
            String responseStr = response.toString();
            
            // For bad requests, we expect 400 status code
            if (invalidServiceType.equals("invalid_malformed_service_json")) {
                assertTrue(responseStr.contains("400"), 
                          "Malformed JSON should return 400 Bad Request. Got: " + responseStr);
            } else {
                // For missing required fields, we should also get 400
                assertTrue(responseStr.contains("400") || responseStr.contains("422"), 
                          "Missing required fields should return 400 Bad Request or 422 Unprocessable Entity. Got: " + responseStr);
            }
        } else {
            // If response is a JSON object, check for error structure
            try {
                JsonObject responseObject = JsonParser.parseString(response.toString()).getAsJsonObject();
                
                // Check for standard error response structure
                if (responseObject.has("status")) {
                    int statusCode = responseObject.get("status").getAsInt();
                    assertEquals(400, statusCode, 
                               "Invalid service requests should return 400 Bad Request status");
                }
                
                if (responseObject.has("error")) {
                    String errorMessage = responseObject.get("error").getAsString();
                    assertEquals("Bad Request", errorMessage, 
                               "Error message should be 'Bad Request'");
                }
                
                // Should not contain valid experience data
                assertFalse(responseObject.has("experiencejson") && 
                           !responseObject.getAsJsonObject("experiencejson").entrySet().isEmpty(),
                           "Invalid service requests should not return valid experience data");
                           
            } catch (Exception parseException) {
                fail("Response should be valid JSON with error structure. Got: " + response.toString());
            }
        }
        
        System.out.println("Invalid service request properly handled for: " + invalidServiceType);
    }
    
    private String generateInvalidTestData(String invalidType) {
        switch (invalidType) {
            case "invalid_empty_package":
                // Empty JSON object - missing required fields
                return "{}";
                
            case "invalid_missing_products":
                // Missing required 'products' field
                return "{\n" +
                       "  \"channel\": \"PACKAGE\"\n" +
                       "}";
                       
            case "invalid_malformed_json":
                // Syntactically invalid JSON
                return "{\n" +
                       "  \"channel\": \"PACKAGE\",\n" +
                       "  \"products\": [\n" +
                       "    {\n" +
                       "      \"productCatalogId\": \"invalid\n" +  // Missing closing quote and bracket
                       "  \n" +
                       "}";
                       
            case "invalid_product_structure":
                // Valid JSON but invalid product structure (missing required fields)
                return "{\n" +
                       "  \"channel\": \"PACKAGE\",\n" +
                       "  \"products\": [\n" +
                       "    {\n" +
                       "      \"invalidField\": \"test\"\n" +  // Missing required fields like productCatalogId, type, location
                       "    }\n" +
                       "  ]\n" +
                       "}";
                       
            default:
                return "{}";
        }
    }
    
    private String generateInvalidServiceTestData(String invalidType) {
        switch (invalidType) {
            case "invalid_empty_service":
                // Empty JSON object - missing required fields
                return "{}";
                
            case "invalid_missing_description":
                // Missing required 'description' field for SERVICE channel
                return "{\n" +
                       "  \"channel\": \"SERVICE\"\n" +
                       "}";
                       
            case "invalid_malformed_service_json":
                // Syntactically invalid JSON
                return "{\n" +
                       "  \"channel\": \"SERVICE\",\n" +
                       "  \"description\": \"Bike rentals in\n" +  // Missing closing quote
                       "}";
                       
            case "invalid_wrong_channel":
                // Invalid channel value
                return "{\n" +
                       "  \"channel\": \"INVALID_CHANNEL\",\n" +
                       "  \"description\": \"Some service description\"\n" +
                       "}";
                       
            default:
                return "{}";
        }
    }
    
    private void validateBasicResponseStructure(JsonObject response) {
        System.out.println("Validating basic response structure for " + response.get("test_filename").getAsString());
        
        // Check that response contains the Experience Builder specific field
        assertTrue(response.has("experiencejson"), "Response should contain experiencejson field");
        
        JsonObject experienceJson = response.getAsJsonObject("experiencejson");
        assertNotNull(experienceJson, "experiencejson should not be null");
        
        // Validate key fields in the experience JSON
        assertTrue(experienceJson.has("caption"), "Experience should have a caption");
        assertTrue(experienceJson.has("summary"), "Experience should have a summary");
        assertTrue(experienceJson.has("plan"), "Experience should have a plan");
        assertTrue(experienceJson.has("experienceTypes"), "Experience should have experienceTypes");
        assertTrue(experienceJson.has("location"), "Experience should have location");
    }
    
    private void performSpecificValidations(String packageDataFileName, JsonObject response) {
        System.out.println("Performing specific validations for " + packageDataFileName);
        
        if (packageDataFileName.contains("mumbai")) {
            validateMumbaiExperienceResponse(response);
        }
        
        // Add more specific validations for other package types as needed
    }
    
    private void performServiceSpecificValidations(String serviceDataFileName, JsonObject response) {
        System.out.println("Performing service specific validations for " + serviceDataFileName);
        
        if (serviceDataFileName.contains("bengaluru_bike_rental")) {
            validateBengaluruBikeRentalResponse(response);
        } else if (serviceDataFileName.contains("mumbai_wedding_photography")) {
            validateMumbaiWeddingPhotographyResponse(response);
        }     
        // Add more specific validations for other service types as needed
    }
    
    private void validateMumbaiExperienceResponse(JsonObject response) {
        JsonObject experienceJson = response.getAsJsonObject("experiencejson");
        
        // Validate location is Mumbai
        JsonObject location = experienceJson.getAsJsonObject("location");
        assertTrue(location.get("city").getAsString().equalsIgnoreCase("Mumbai"), 
                  "Experience location should be Mumbai");
        assertTrue(location.get("country").getAsString().equalsIgnoreCase("India"), 
                  "Experience country should be India");
        
        // Validate the caption contains meaningful text
        String caption = experienceJson.get("caption").getAsString();
        assertFalse(caption.isEmpty(), "Caption should not be empty");
        assertTrue(caption.toLowerCase().contains("mumbai") || caption.toLowerCase().contains("heritage") || 
                   caption.toLowerCase().contains("wellness"), 
                   "Caption should mention Mumbai, heritage, or wellness themes");
        
        // Validate that the plan contains activities
        JsonArray plan = experienceJson.getAsJsonArray("plan");
        assertTrue(plan.size() > 0, "Plan should contain at least one day");
        
        JsonObject firstDay = plan.get(0).getAsJsonObject();
        JsonArray schedule = firstDay.getAsJsonArray("schedule");
        assertTrue(schedule.size() >= 2, "First day should contain at least 2 activities (spa + heritage walk)");
        
        // Validate FAQ section exists (may or may not have questions)
        assertTrue(experienceJson.has("faq"), "FAQ section should exist");
        JsonArray faq = experienceJson.getAsJsonArray("faq");
        assertNotNull(faq, "FAQ array should not be null");
        
        System.out.println("Mumbai experience validation passed successfully");
    }
    
    private void validateBengaluruBikeRentalResponse(JsonObject response) {
        JsonObject experienceJson = response.getAsJsonObject("experiencejson");  
        // Validate location contains Bengaluru
        JsonObject location = experienceJson.getAsJsonObject("location");
        assertTrue(location.get("city").getAsString().equalsIgnoreCase("Bengaluru") || 
                   location.get("city").getAsString().equalsIgnoreCase("Bangalore"), 
                   "Experience location should be Bengaluru/Bangalore");
        assertTrue(location.get("country").getAsString().equalsIgnoreCase("India"), 
                   "Experience country should be India");
        
        // Validate the caption or summary contains bike rental related terms
        String caption = experienceJson.get("caption").getAsString();
        assertFalse(caption.isEmpty(), "Caption should not be empty");
        
        JsonArray summary = experienceJson.getAsJsonArray("summary");
        String summaryText = summary.toString().toLowerCase();
        assertTrue(summaryText.contains("bike") || summaryText.contains("rental") || 
                   summaryText.contains("cycling") || summaryText.contains("bengaluru") ||
                   summaryText.contains("bangalore"),
                   "Summary should mention bike, rental, cycling, or Bengaluru themes");
        
        // Validate experience types are relevant
        JsonArray experienceTypes = experienceJson.getAsJsonArray("experienceTypes");
        assertTrue(experienceTypes.size() > 0, "Experience should have at least one type");
        
        System.out.println("Bengaluru bike rental validation passed successfully");
    }
    
    private void validateMumbaiWeddingPhotographyResponse(JsonObject response) {
        JsonObject experienceJson = response.getAsJsonObject("experiencejson");    
        // Validate location is Mumbai
        JsonObject location = experienceJson.getAsJsonObject("location");
        assertTrue(location.get("city").getAsString().equalsIgnoreCase("Mumbai"), 
                  "Experience location should be Mumbai");
        assertTrue(location.get("country").getAsString().equalsIgnoreCase("India"), 
                  "Experience country should be India");
        
        // Validate the caption or summary contains photography/wedding related terms
        String caption = experienceJson.get("caption").getAsString();
        assertFalse(caption.isEmpty(), "Caption should not be empty");
        
        JsonArray summary = experienceJson.getAsJsonArray("summary");
        String summaryText = summary.toString().toLowerCase();
        assertTrue(summaryText.contains("photography") || summaryText.contains("wedding") || 
                   summaryText.contains("photographer") || summaryText.contains("videographer") ||
                   summaryText.contains("ceremony"),
                   "Summary should mention photography, wedding, or ceremony themes");
        
        // Validate experience types are relevant
        JsonArray experienceTypes = experienceJson.getAsJsonArray("experienceTypes");
        assertTrue(experienceTypes.size() > 0, "Experience should have at least one type");
        
        System.out.println("Mumbai wedding photography validation passed successfully");
    }
}
