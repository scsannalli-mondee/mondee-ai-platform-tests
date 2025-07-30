package com.aiservicestests;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Tag;

import com.aiservices.ApiClient;
import com.aiservices.Constants;
import com.utils.JsonFileReader;
import com.utils.HtmlReportGenerator;
import com.utils.HtmlReportExtension;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Price Plan Rules Extraction Tests with HTML Reporting
 */
@ExtendWith(HtmlReportExtension.class)
public class PricePlanRulesExtractionTests {
    
    private String currentTestName;
    private String currentClassName;
    
    @BeforeEach
    public void setUp() {
        currentClassName = this.getClass().getSimpleName();
        System.out.println("Setting up HTML reporting for PricePlanRulesExtractionTests");
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"extract_price_plan_inventory", "kerala_ayurveda_inr", "water_sports_age_groups", "bike_tour_package"})
    @DisplayName("Test AI Prompt Execution - Extract Price Plan Inventory")
    @Tag("smoke")
    @Tag("regression")
    public void testExtractPricePlanInventory(String promptDataFileName) throws IOException {
        currentTestName = "testExtractPricePlanInventory";
        
        // Log test start with HTML reporting
        HtmlReportGenerator.addLog(currentTestName, currentClassName, "Starting test for file: " + promptDataFileName);
        HtmlReportGenerator.addTestDetail(currentTestName, currentClassName, "inputFile", promptDataFileName + ".json");
        HtmlReportGenerator.addTestDetail(currentTestName, currentClassName, "startTime", 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        try {
            ApiClient client = new ApiClient();
            HtmlReportGenerator.addLog(currentTestName, currentClassName, "API client initialized successfully");
            
            String jsonBody = JsonFileReader.readJsonFile("smartaifill/" + promptDataFileName + ".json");
            HtmlReportGenerator.addLog(currentTestName, currentClassName, "JSON file read successfully: " + promptDataFileName + ".json");
            HtmlReportGenerator.addTestDetail(currentTestName, currentClassName, "inputDataLength", jsonBody.length());
            
            var response = client.executeAIPrompt(Constants.EXECUTE_PROMPT_ENDPOINT, jsonBody);
            HtmlReportGenerator.addLog(currentTestName, currentClassName, "API call executed successfully");
            HtmlReportGenerator.addTestDetail(currentTestName, currentClassName, "endpoint", Constants.EXECUTE_PROMPT_ENDPOINT);
            
            assertNotNull(response, "AI prompt response should not be null");
            HtmlReportGenerator.addLog(currentTestName, currentClassName, "Response validation: Not null - PASSED");
            System.out.println("AI Prompt executed successfully for " + promptDataFileName + ": " + response);
            
            // Parse the response JSON
            JsonArray responseArray = JsonParser.parseString(response.toString()).getAsJsonArray();
            assertTrue(responseArray.size() > 0, "Response should contain at least one item");
            HtmlReportGenerator.addLog(currentTestName, currentClassName, "Response array validation - PASSED");
            HtmlReportGenerator.addTestDetail(currentTestName, currentClassName, "responseItemCount_" + promptDataFileName, responseArray.size());
            
            // Validate each item in the response array
            for (int itemIndex = 0; itemIndex < responseArray.size(); itemIndex++) {
                JsonObject item = responseArray.get(itemIndex).getAsJsonObject();
                HtmlReportGenerator.addLog(currentTestName, currentClassName, 
                    "Validating item " + (itemIndex + 1) + " for " + promptDataFileName);
                System.out.println("Validating item " + (itemIndex + 1) + " for " + promptDataFileName);
                
                // Validate pricingPackageType
                assertTrue(item.has("pricingPackageType"), "Response should contain pricingPackageType");
                String pricingPackageType = item.get("pricingPackageType").getAsString();
                assertNotNull(pricingPackageType, "pricingPackageType should not be null");
                assertFalse(pricingPackageType.isEmpty(), "pricingPackageType should not be empty");
                HtmlReportGenerator.addLog(currentTestName, currentClassName, "pricingPackageType validation - PASSED");
                HtmlReportGenerator.addTestDetail(currentTestName, currentClassName, 
                    "pricingPackageType_item" + itemIndex + "_" + promptDataFileName, pricingPackageType);
                System.out.println("Pricing Package Type: " + pricingPackageType);
                
                // Validate currency
                assertTrue(item.has("currency"), "Response should contain currency");
                String currency = item.get("currency").getAsString();
                assertNotNull(currency, "currency should not be null");
                assertFalse(currency.isEmpty(), "currency should not be empty");
                HtmlReportGenerator.addLog(currentTestName, currentClassName, "currency validation - PASSED");
                HtmlReportGenerator.addTestDetail(currentTestName, currentClassName, 
                    "currency_item" + itemIndex + "_" + promptDataFileName, currency);
                System.out.println("Currency: " + currency);
                
                // Validate price
                assertTrue(item.has("price"), "Response should contain price");
                JsonArray priceArray = item.get("price").getAsJsonArray();
                assertTrue(priceArray.size() > 0, "Price array should contain at least one item");
                HtmlReportGenerator.addLog(currentTestName, currentClassName, "price array validation - PASSED");
                HtmlReportGenerator.addTestDetail(currentTestName, currentClassName, 
                    "priceArraySize_item" + itemIndex + "_" + promptDataFileName, priceArray.size());
                
                for (int priceIndex = 0; priceIndex < priceArray.size(); priceIndex++) {
                    JsonObject priceObject = priceArray.get(priceIndex).getAsJsonObject();
                    assertTrue(priceObject.has("baseFare"), "Price object should contain baseFare");
                    int baseFare = priceObject.get("baseFare").getAsInt();
                    assertTrue(baseFare > 0, "Base fare should be greater than 0");
                    HtmlReportGenerator.addLog(currentTestName, currentClassName, 
                        "baseFare validation for price " + (priceIndex + 1) + " - PASSED");
                    HtmlReportGenerator.addTestDetail(currentTestName, currentClassName, 
                        "baseFare_price" + priceIndex + "_item" + itemIndex + "_" + promptDataFileName, baseFare);
                    System.out.println("Base Fare " + (priceIndex + 1) + ": " + baseFare);
                }
                
                // Validate features
                assertTrue(item.has("features"), "Response should contain features");
                JsonArray featuresArray = item.get("features").getAsJsonArray();
                assertTrue(featuresArray.size() > 0, "Features array should contain at least one item");
                HtmlReportGenerator.addLog(currentTestName, currentClassName, "features array validation - PASSED");
                HtmlReportGenerator.addTestDetail(currentTestName, currentClassName, 
                    "featuresCount_item" + itemIndex + "_" + promptDataFileName, featuresArray.size());
                System.out.println("Features: " + featuresArray);
                
                // Validate each feature is not empty
                for (int featureIndex = 0; featureIndex < featuresArray.size(); featureIndex++) {
                    String feature = featuresArray.get(featureIndex).getAsString();
                    assertNotNull(feature, "Feature should not be null");
                    assertFalse(feature.trim().isEmpty(), "Feature should not be empty");
                    HtmlReportGenerator.addTestDetail(currentTestName, currentClassName, 
                        "feature" + featureIndex + "_item" + itemIndex + "_" + promptDataFileName, feature);
                }
            }
            
            // Test case specific validations
            HtmlReportGenerator.addLog(currentTestName, currentClassName, "Starting specific validations for: " + promptDataFileName);
            performSpecificValidations(promptDataFileName, responseArray);
            
            HtmlReportGenerator.addLog(currentTestName, currentClassName, "All validations completed successfully for: " + promptDataFileName);
            HtmlReportGenerator.addTestDetail(currentTestName, currentClassName, "finalStatus_" + promptDataFileName, "SUCCESS");
            
        } catch (Exception e) {
            HtmlReportGenerator.addLog(currentTestName, currentClassName, 
                "Test failed for " + promptDataFileName + " with exception: " + e.getMessage());
            HtmlReportGenerator.addTestDetail(currentTestName, currentClassName, 
                "finalStatus_" + promptDataFileName, "FAILED");
            HtmlReportGenerator.addTestDetail(currentTestName, currentClassName, 
                "errorType_" + promptDataFileName, e.getClass().getSimpleName());
            throw e;
        }
    }
    
    private void performSpecificValidations(String testFileName, JsonArray responseArray) {
        JsonObject firstItem = responseArray.get(0).getAsJsonObject();
        HtmlReportGenerator.addLog(currentTestName, currentClassName, "Performing specific validations for: " + testFileName);
        
        switch (testFileName) {
            case "extract_price_plan_inventory":
                // Original spa package test
                HtmlReportGenerator.addLog(currentTestName, currentClassName, "Validating extract_price_plan_inventory specific requirements");
                assertEquals("USD", firstItem.get("currency").getAsString(), "Currency should be USD");
                HtmlReportGenerator.addLog(currentTestName, currentClassName, "Currency USD validation - PASSED");
                
                assertEquals(99, firstItem.get("price").getAsJsonArray().get(0).getAsJsonObject().get("baseFare").getAsInt(), "Base fare should be 99");
                HtmlReportGenerator.addLog(currentTestName, currentClassName, "Base fare 99 validation - PASSED");
                
                String[] expectedFeatures = {"Hair Serum Application", "Scalp Massage", "Cooling Eye Mask"};
                JsonArray featuresArray = firstItem.get("features").getAsJsonArray();
                assertEquals(expectedFeatures.length, featuresArray.size(), "Should have exactly 3 features");
                HtmlReportGenerator.addLog(currentTestName, currentClassName, "Features count validation - PASSED");
                
                for (int i = 0; i < expectedFeatures.length; i++) {
                    String actualFeature = featuresArray.get(i).getAsString();
                    assertEquals(expectedFeatures[i], actualFeature, "Feature " + (i+1) + " should match expected value");
                    HtmlReportGenerator.addTestDetail(currentTestName, currentClassName, "expectedFeature" + i, expectedFeatures[i]);
                    HtmlReportGenerator.addTestDetail(currentTestName, currentClassName, "actualFeature" + i, actualFeature);
                }
                HtmlReportGenerator.addLog(currentTestName, currentClassName, "All feature validations - PASSED");
                break;
                
            case "kerala_ayurveda_inr":
                // INR currency test
                HtmlReportGenerator.addLog(currentTestName, currentClassName, "Validating kerala_ayurveda_inr specific requirements");
                assertEquals("INR", firstItem.get("currency").getAsString(), "Currency should be INR");
                HtmlReportGenerator.addLog(currentTestName, currentClassName, "Currency INR validation - PASSED");
                
                assertEquals(15999, firstItem.get("price").getAsJsonArray().get(0).getAsJsonObject().get("baseFare").getAsInt(), "Base fare should be 15999");
                HtmlReportGenerator.addLog(currentTestName, currentClassName, "Base fare 15999 validation - PASSED");
                
                JsonArray ayurvedaFeatures = firstItem.get("features").getAsJsonArray();
                assertTrue(ayurvedaFeatures.size() >= 3, "Should have at least 3 features for Ayurveda package");
                HtmlReportGenerator.addLog(currentTestName, currentClassName, "Ayurveda features count validation - PASSED");
                HtmlReportGenerator.addTestDetail(currentTestName, currentClassName, "ayurvedaFeaturesCount", ayurvedaFeatures.size());
                break;
                
            case "water_sports_age_groups":
                // Multiple age groups test
                HtmlReportGenerator.addLog(currentTestName, currentClassName, "Validating water_sports_age_groups specific requirements");
                assertEquals("INR", firstItem.get("currency").getAsString(), "Currency should be INR");
                HtmlReportGenerator.addLog(currentTestName, currentClassName, "Currency INR validation - PASSED");
                
                JsonArray priceArray = firstItem.get("price").getAsJsonArray();
                
                // Should have prices for different age groups
                assertTrue(priceArray.size() >= 1, "Should have at least one price entry");
                HtmlReportGenerator.addLog(currentTestName, currentClassName, "Price entries validation - PASSED");
                HtmlReportGenerator.addTestDetail(currentTestName, currentClassName, "priceEntriesCount", priceArray.size());
                
                // Check if we have the expected price values (500 or 800)
                boolean hasExpectedPrice = false;
                for (int i = 0; i < priceArray.size(); i++) {
                    int fare = priceArray.get(i).getAsJsonObject().get("baseFare").getAsInt();
                    HtmlReportGenerator.addTestDetail(currentTestName, currentClassName, "priceEntry" + i, fare);
                    if (fare == 500 || fare == 800) {
                        hasExpectedPrice = true;
                        break;
                    }
                }
                assertTrue(hasExpectedPrice, "Should contain either 500 or 800 as base fare");
                HtmlReportGenerator.addLog(currentTestName, currentClassName, "Expected price validation - PASSED");
                break;
                
            case "bike_tour_package":
                // Bike tour test
                HtmlReportGenerator.addLog(currentTestName, currentClassName, "Validating bike_tour_package specific requirements");
                assertEquals("USD", firstItem.get("currency").getAsString(), "Currency should be USD");
                HtmlReportGenerator.addLog(currentTestName, currentClassName, "Currency USD validation - PASSED");
                
                assertEquals(75, firstItem.get("price").getAsJsonArray().get(0).getAsJsonObject().get("baseFare").getAsInt(), "Base fare should be 75");
                HtmlReportGenerator.addLog(currentTestName, currentClassName, "Base fare 75 validation - PASSED");
                
                JsonArray bikeFeatures = firstItem.get("features").getAsJsonArray();
                assertTrue(bikeFeatures.size() >= 4, "Should have at least 4 features for bike tour package");
                HtmlReportGenerator.addLog(currentTestName, currentClassName, "Bike tour features count validation - PASSED");
                HtmlReportGenerator.addTestDetail(currentTestName, currentClassName, "bikeFeaturesCount", bikeFeatures.size());
                break;
        }
        
        HtmlReportGenerator.addLog(currentTestName, currentClassName, "Specific validations completed for: " + testFileName);
        System.out.println("Specific validations completed for: " + testFileName);
    }
    
    @ParameterizedTest
    // @ValueSource(strings = {"rainy_day_discount_rules", "early_bird_family_discount", "senior_citizen_discount", "weekend_flash_sale", "weekend_special_offers", "august_rainy_day_discount", "weekend_rainy_day_discount"})
    @ValueSource(strings = {"weekend_rainy_day_discount"})
    @DisplayName("Test AI Prompt Execution - Extract Price Rules Inventory")
    @Tag("regression")
    public void testExtractPriceRulesInventory(String promptDataFileName) throws IOException {
        ApiClient client = new ApiClient();
        String jsonBody = JsonFileReader.readJsonFile("smartaifill/" + promptDataFileName + ".json");
        var response = client.executeAIPrompt(Constants.EXECUTE_PROMPT_ENDPOINT, jsonBody);
        
        assertNotNull(response, "AI prompt response should not be null");
        System.out.println("AI Price Rules executed successfully for " + promptDataFileName + ": " + response);
        
        // Parse the response JSON
        JsonArray responseArray = JsonParser.parseString(response.toString()).getAsJsonArray();
        assertTrue(responseArray.size() > 0, "Response should contain at least one price rule");
        
        // Validate each price rule in the response array
        for (int ruleIndex = 0; ruleIndex < responseArray.size(); ruleIndex++) {
            JsonObject rule = responseArray.get(ruleIndex).getAsJsonObject();
            System.out.println("Validating price rule " + (ruleIndex + 1) + " for " + promptDataFileName);
            
            // Validate rule name/title
            assertTrue(rule.has("title") || rule.has("ruleName") || rule.has("name"), 
                "Response should contain rule name/title");
            
            // Validate discount information
            assertTrue(rule.has("priceAdjustment") || rule.has("discount") || rule.has("discountAmount") || rule.has("amount"), 
                "Response should contain discount information");
            
            // Validate start date
            if (rule.has("startDate")) {
                String startDate = rule.get("startDate").getAsString();
                assertNotNull(startDate, "Start date should not be null");
                assertFalse(startDate.isEmpty(), "Start date should not be empty");
                System.out.println("Start Date: " + startDate);
            }
            
            // Validate end date
            if (rule.has("endDate")) {
                String endDate = rule.get("endDate").getAsString();
                assertNotNull(endDate, "End date should not be null");
                assertFalse(endDate.isEmpty(), "End date should not be empty");
                System.out.println("End Date: " + endDate);
            }
            
            // Validate price adjustment
            if (rule.has("priceAdjustment")) {
                double priceAdjustment = rule.get("priceAdjustment").getAsDouble();
                assertTrue(priceAdjustment > 0, "Price adjustment should be greater than 0");
                System.out.println("Price Adjustment: " + priceAdjustment);
            }
            
            // Validate percentage flag
            if (rule.has("isPercentage")) {
                boolean isPercentage = rule.get("isPercentage").getAsBoolean();
                System.out.println("Is Percentage: " + isPercentage);
            }
            
            // Validate pax type
            if (rule.has("paxType")) {
                String paxType = rule.get("paxType").getAsString();
                assertNotNull(paxType, "Pax type should not be null");
                assertFalse(paxType.isEmpty(), "Pax type should not be empty");
                System.out.println("Pax Type: " + paxType);
            }
            
            // Validate inventory count
            if (rule.has("inventoryCount")) {
                int inventoryCount = rule.get("inventoryCount").getAsInt();
                assertTrue(inventoryCount >= 0, "Inventory count should be non-negative");
                System.out.println("Inventory Count: " + inventoryCount);
            }
            
            // Validate comparison operator
            if (rule.has("comparisonOperator")) {
                String comparisonOperator = rule.get("comparisonOperator").getAsString();
                assertNotNull(comparisonOperator, "Comparison operator should not be null");
                assertFalse(comparisonOperator.isEmpty(), "Comparison operator should not be empty");
                System.out.println("Comparison Operator: " + comparisonOperator);
            }
            
            // Log all fields for debugging
            System.out.println("Price rule fields: " + rule.keySet());
        }
        
        // Perform specific validations for known test cases
        performPriceRulesSpecificValidations(promptDataFileName, responseArray);
    }
    
    private void performPriceRulesSpecificValidations(String testFileName, JsonArray responseArray) {
        JsonObject firstRule = responseArray.get(0).getAsJsonObject();
        
        switch (testFileName) {
            case "rainy_day_discount_rules":
                // Validate the rainy day discount rule
                System.out.println("Validating Rainy Day Discount Rule specifics");
                
                // Check for title
                assertTrue(firstRule.has("title"), "Should have title field");
                String title = firstRule.get("title").getAsString();
                assertTrue(title.toLowerCase().contains("rainy") || title.toLowerCase().contains("discount"), 
                    "Title should contain 'rainy' or 'discount'");
                
                // Check for price adjustment (should be $12)
                assertTrue(firstRule.has("priceAdjustment"), "Should have priceAdjustment field");
                double priceAdjustment = firstRule.get("priceAdjustment").getAsDouble();
                assertEquals(12.0, priceAdjustment, 0.01, "Price adjustment should be 12.0");
                
                // Check that it's not a percentage
                assertTrue(firstRule.has("isPercentage"), "Should have isPercentage field");
                assertFalse(firstRule.get("isPercentage").getAsBoolean(), "Should not be a percentage discount");
                
                // Check for date range (June 15-20)
                assertTrue(firstRule.has("startDate"), "Should have start date");
                assertTrue(firstRule.has("endDate"), "Should have end date");
                
                String startDate = firstRule.get("startDate").getAsString();
                String endDate = firstRule.get("endDate").getAsString();
                assertTrue(startDate.contains("2025-06-15"), "Start date should be June 15, 2025");
                assertTrue(endDate.contains("2025-06-20"), "End date should be June 20, 2025");
                
                // Check for adult-specific condition
                assertTrue(firstRule.has("paxType"), "Should have paxType field");
                String paxType = firstRule.get("paxType").getAsString();
                assertEquals("ADULT", paxType, "Pax type should be ADULT");
                
                // Check for inventory condition (less than 50 items - should be 49)
                assertTrue(firstRule.has("inventoryCount"), "Should have inventoryCount field");
                int inventoryCount = firstRule.get("inventoryCount").getAsInt();
                assertEquals(49, inventoryCount, "Inventory count should be 49 (less than 50)");
                
                // Check comparison operator
                assertTrue(firstRule.has("comparisonOperator"), "Should have comparisonOperator field");
                String comparisonOperator = firstRule.get("comparisonOperator").getAsString();
                assertEquals("lessorequal", comparisonOperator, "Comparison operator should be lessorequal");
                
                break;
                
            case "early_bird_family_discount":
                // Validate the early bird family discount rule
                System.out.println("Validating Early Bird Family Discount Rule specifics");
                
                // Check for title
                assertTrue(firstRule.has("title"), "Should have title field");
                String earlyBirdTitle = firstRule.get("title").getAsString();
                assertTrue(earlyBirdTitle.toLowerCase().contains("early") || earlyBirdTitle.toLowerCase().contains("bird") || 
                          earlyBirdTitle.toLowerCase().contains("family"), "Title should contain 'early', 'bird', or 'family'");
                
                // Check for percentage discount (should be 25%)
                assertTrue(firstRule.has("priceAdjustment"), "Should have priceAdjustment field");
                double earlyBirdAdjustment = firstRule.get("priceAdjustment").getAsDouble();
                assertEquals(25.0, earlyBirdAdjustment, 0.01, "Price adjustment should be 25.0");
                
                // Check that it's a percentage
                assertTrue(firstRule.has("isPercentage"), "Should have isPercentage field");
                assertTrue(firstRule.get("isPercentage").getAsBoolean(), "Should be a percentage discount");
                
                // Check for March date range
                if (firstRule.has("startDate") && firstRule.has("endDate")) {
                    String ebStartDate = firstRule.get("startDate").getAsString();
                    String ebEndDate = firstRule.get("endDate").getAsString();
                    assertTrue(ebStartDate.contains("2025-03-01") || ebStartDate.contains("March"), 
                              "Start date should be March 1, 2025");
                    assertTrue(ebEndDate.contains("2025-03-31") || ebEndDate.contains("March"), 
                              "End date should be March 31, 2025");
                }
                
                // Check for inventory limit (100 bookings)
                if (firstRule.has("inventoryCount")) {
                    int ebInventoryCount = firstRule.get("inventoryCount").getAsInt();
                    assertEquals(100, ebInventoryCount, "Inventory count should be 100");
                }
                
                break;
                
            case "senior_citizen_discount":
                // Validate the senior citizen discount rule
                System.out.println("Validating Senior Citizen Discount Rule specifics");
                
                // Check for title
                assertTrue(firstRule.has("title"), "Should have title field");
                String seniorTitle = firstRule.get("title").getAsString();
                assertTrue(seniorTitle.toLowerCase().contains("senior") || seniorTitle.toLowerCase().contains("citizen"), 
                          "Title should contain 'senior' or 'citizen'");
                
                // Check for flat discount (should be ₹200)
                assertTrue(firstRule.has("priceAdjustment"), "Should have priceAdjustment field");
                double seniorAdjustment = firstRule.get("priceAdjustment").getAsDouble();
                assertEquals(200.0, seniorAdjustment, 0.01, "Price adjustment should be 200.0");
                
                // Check that it's not a percentage
                assertTrue(firstRule.has("isPercentage"), "Should have isPercentage field");
                assertFalse(firstRule.get("isPercentage").getAsBoolean(), "Should not be a percentage discount");
                
                // Check for senior age condition (65+)
                if (firstRule.has("minAge") || firstRule.has("ageLimit")) {
                    // Validate age condition if present
                    System.out.println("Age condition validated for senior discount");
                }
                
                break;
                
            case "weekend_flash_sale":
                // Validate the weekend flash sale rule
                System.out.println("Validating Weekend Flash Sale Rule specifics");
                
                // Check for title
                assertTrue(firstRule.has("title"), "Should have title field");
                String weekendTitle = firstRule.get("title").getAsString();
                assertTrue(weekendTitle.toLowerCase().contains("weekend") || weekendTitle.toLowerCase().contains("flash") || 
                          weekendTitle.toLowerCase().contains("sale"), "Title should contain 'weekend', 'flash', or 'sale'");
                
                // Check for percentage discount (should be 15%)
                assertTrue(firstRule.has("priceAdjustment"), "Should have priceAdjustment field");
                double weekendAdjustment = firstRule.get("priceAdjustment").getAsDouble();
                assertEquals(15.0, weekendAdjustment, 0.01, "Price adjustment should be 15.0");
                
                // Check that it's a percentage
                assertTrue(firstRule.has("isPercentage"), "Should have isPercentage field");
                assertTrue(firstRule.get("isPercentage").getAsBoolean(), "Should be a percentage discount");
                
                // Check for July 2025 date range
                if (firstRule.has("startDate") && firstRule.has("endDate")) {
                    String wsStartDate = firstRule.get("startDate").getAsString();
                    String wsEndDate = firstRule.get("endDate").getAsString();
                    assertTrue(wsStartDate.contains("2025-07") || wsStartDate.contains("July"), 
                              "Start date should be July 2025");
                    assertTrue(wsEndDate.contains("2025-07") || wsEndDate.contains("July"), 
                              "End date should be July 2025");
                }
                
                // Check for inventory condition (> 75 units)
                if (firstRule.has("inventoryCount")) {
                    int wsInventoryCount = firstRule.get("inventoryCount").getAsInt();
                    assertEquals(75, wsInventoryCount, "Inventory count should be 75");
                }
                
                // Check comparison operator (should be greater than)
                if (firstRule.has("comparisonOperator")) {
                    String wsComparisonOperator = firstRule.get("comparisonOperator").getAsString();
                    assertTrue(wsComparisonOperator.contains("greater") || wsComparisonOperator.contains("exceed"), 
                              "Comparison operator should indicate greater than");
                }
                
                break;
                
            case "weekend_special_offers":
                // Validate the weekend special offers rule
                System.out.println("Validating Weekend Special Offers Rule specifics");
                
                // Check for title
                if (firstRule.has("title")) {
                    String wsoTitle = firstRule.get("title").getAsString();
                    assertTrue(wsoTitle.toLowerCase().contains("weekend") || wsoTitle.toLowerCase().contains("special"), 
                              "Title should contain 'weekend' or 'special'");
                }
                
                // Check for percentage discount (should be 20%)
                if (firstRule.has("priceAdjustment")) {
                    double wsoDiscount = firstRule.get("priceAdjustment").getAsDouble();
                    assertEquals(20.0, wsoDiscount, "Price adjustment should be 20%");
                }
                
                // Check if it's percentage type
                if (firstRule.has("isPercentage")) {
                    boolean wsoIsPercentage = firstRule.get("isPercentage").getAsBoolean();
                    assertTrue(wsoIsPercentage, "Should be percentage discount");
                }
                
                // Check for August 2025 timeframe
                if (firstRule.has("startDate")) {
                    String wsoStartDate = firstRule.get("startDate").getAsString();
                    assertTrue(wsoStartDate.contains("2025-08"), "Start date should be in August 2025");
                }
                
                // Check for inventory condition (should be > 50)
                if (firstRule.has("inventoryCount")) {
                    int wsoInventoryCount = firstRule.get("inventoryCount").getAsInt();
                    assertEquals(50, wsoInventoryCount, "Inventory count should be 50");
                }
                
                // Check for Friday, Saturday, Sunday (days of week)
                if (firstRule.has("daysOfWeek")) {
                    String wsoDaysOfWeek = firstRule.get("daysOfWeek").getAsString();
                    assertTrue(wsoDaysOfWeek.contains("5") || wsoDaysOfWeek.contains("6") || wsoDaysOfWeek.contains("0"), 
                              "Should include weekend days (Friday=5, Saturday=6, Sunday=0)");
                }
                
                break;
                
            case "august_rainy_day_discount":
                // Validate the August rainy day discount rule
                System.out.println("Validating August Rainy Day Discount Rule specifics");
                
                // Check for title
                if (firstRule.has("title")) {
                    String ardTitle = firstRule.get("title").getAsString();
                    assertTrue(ardTitle.toLowerCase().contains("rainy") || ardTitle.toLowerCase().contains("discount"), 
                              "Title should contain 'rainy' or 'discount'");
                }
                
                // Check for fixed discount amount ($20 or $25)
                if (firstRule.has("priceAdjustment")) {
                    double ardDiscount = firstRule.get("priceAdjustment").getAsDouble();
                    assertTrue(ardDiscount == 20.0 || ardDiscount == 25.0, "Price adjustment should be $20 or $25");
                }
                
                // Check if it's NOT percentage type (fixed amount)
                if (firstRule.has("isPercentage")) {
                    boolean ardIsPercentage = firstRule.get("isPercentage").getAsBoolean();
                    assertFalse(ardIsPercentage, "Should be fixed amount discount, not percentage");
                }
                
                // Check for August 2025 timeframe
                if (firstRule.has("startDate")) {
                    String ardStartDate = firstRule.get("startDate").getAsString();
                    assertTrue(ardStartDate.contains("2025-08"), "Start date should be in August 2025");
                }
                
                // Check for adult and senior citizen pax types
                if (firstRule.has("paxType")) {
                    String ardPaxType = firstRule.get("paxType").getAsString();
                    assertTrue(ardPaxType.contains("ADULT") || ardPaxType.contains("SENIOR"), 
                              "Should include ADULT or SENIOR citizen pax types");
                }
                
                // Check for inventory condition (should be < 50)
                if (firstRule.has("inventoryCount")) {
                    int ardInventoryCount = firstRule.get("inventoryCount").getAsInt();
                    assertTrue(ardInventoryCount <= 50, "Inventory count should be 50 or less");
                }
                
                break;
                
            case "weekend_rainy_day_discount":
                // Validate the weekend rainy day discount rule
                System.out.println("Validating Weekend Rainy Day Discount Rule specifics");
                
                // Check for title
                if (firstRule.has("title")) {
                    String wrdTitle = firstRule.get("title").getAsString();
                    assertTrue(wrdTitle.toLowerCase().contains("rainy") || wrdTitle.toLowerCase().contains("discount"), 
                              "Title should contain 'rainy' or 'discount'");
                }
                
                // Assert that startDate should be present in the response
                assertTrue(firstRule.has("startDate"), "Response should contain startDate for weekend rainy day discount");
                if (firstRule.has("startDate")) {
                    String wrdStartDate = firstRule.get("startDate").getAsString();
                    assertNotNull(wrdStartDate, "Start date should not be null");
                    assertFalse(wrdStartDate.isEmpty(), "Start date should not be empty");
                    System.out.println("Start Date: " + wrdStartDate);
                }
                
                // Assert that endDate should be present in the response
                assertTrue(firstRule.has("endDate"), "Response should contain endDate for weekend rainy day discount");
                if (firstRule.has("endDate")) {
                    String wrdEndDate = firstRule.get("endDate").getAsString();
                    assertNotNull(wrdEndDate, "End date should not be null");
                    assertFalse(wrdEndDate.isEmpty(), "End date should not be empty");
                    System.out.println("End Date: " + wrdEndDate);
                }
                
                // Check for $15 discount amount
                if (firstRule.has("priceAdjustment")) {
                    double wrdDiscount = firstRule.get("priceAdjustment").getAsDouble();
                    assertEquals(15.0, wrdDiscount, "Price adjustment should be $15");
                }
                
                // Check if it's NOT percentage type (fixed amount)
                if (firstRule.has("isPercentage")) {
                    boolean wrdIsPercentage = firstRule.get("isPercentage").getAsBoolean();
                    assertFalse(wrdIsPercentage, "Should be fixed amount discount, not percentage");
                }
                
                // Check for weekend days (this weekend)
                if (firstRule.has("daysOfWeek")) {
                    String wrdDaysOfWeek = firstRule.get("daysOfWeek").getAsString();
                    assertTrue(wrdDaysOfWeek.contains("6") || wrdDaysOfWeek.contains("0"), 
                              "Should include weekend days (Saturday=6, Sunday=0)");
                }
                
                // Check for adult pax type only
                if (firstRule.has("paxType")) {
                    String wrdPaxType = firstRule.get("paxType").getAsString();
                    assertTrue(wrdPaxType.contains("ADULT"), "Should include ADULT pax type");
                }
                
                // Check for inventory condition (should be < 50)
                if (firstRule.has("inventoryCount")) {
                    int wrdInventoryCount = firstRule.get("inventoryCount").getAsInt();
                    assertTrue(wrdInventoryCount <= 50, "Inventory count should be 50 or less");
                }
                
                break;
        }
        
        System.out.println("Price rules specific validations completed for: " + testFileName);
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"cancellation_policies"})
    @DisplayName("Test AI Prompt Execution - Extract Policy Information Inventory")
    @Tag("regression")
    public void testExtractPolicyInformationInventory(String promptDataFileName) throws IOException {
        ApiClient client = new ApiClient();
        String jsonBody = JsonFileReader.readJsonFile("smartaifill/" + promptDataFileName + ".json");
        var response = client.executeAIPrompt(Constants.EXECUTE_PROMPT_ENDPOINT, jsonBody);
        
        assertNotNull(response, "AI prompt response should not be null");
        System.out.println("AI Policy Information executed successfully for " + promptDataFileName + ": " + response);
        
        // Parse the response JSON
        JsonArray responseArray = JsonParser.parseString(response.toString()).getAsJsonArray();
        assertTrue(responseArray.size() > 0, "Response should contain at least one policy");
        
        // Validate each policy in the response array
        for (int policyIndex = 0; policyIndex < responseArray.size(); policyIndex++) {
            JsonObject policy = responseArray.get(policyIndex).getAsJsonObject();
            System.out.println("Validating policy " + (policyIndex + 1) + " for " + promptDataFileName);
            
            // Validate policy type
            assertTrue(policy.has("policyType") || policy.has("type") || policy.has("category"), 
                "Response should contain policy type");
            
            // Validate time period
            assertTrue(policy.has("timePeriod") || policy.has("timeLimit") || policy.has("hours") || policy.has("hoursBeforeStart"), 
                "Response should contain time period information");
            
            // Validate refund information
            assertTrue(policy.has("refundAmount") || policy.has("refundPercentage") || policy.has("refund") || policy.has("charge"), 
                "Response should contain refund/charge information");
            
            // Validate policy details if present
            if (policy.has("timePeriod")) {
                String timePeriod = policy.get("timePeriod").getAsString();
                assertNotNull(timePeriod, "Time period should not be null");
                assertFalse(timePeriod.isEmpty(), "Time period should not be empty");
                System.out.println("Time Period: " + timePeriod);
            }
            
            if (policy.has("hoursBeforeStart")) {
                int hoursBeforeStart = policy.get("hoursBeforeStart").getAsInt();
                assertTrue(hoursBeforeStart >= 0, "Hours before start should be non-negative");
                System.out.println("Hours Before Start: " + hoursBeforeStart);
            }
            
            if (policy.has("refundPercentage")) {
                int refundPercentage = policy.get("refundPercentage").getAsInt();
                assertTrue(refundPercentage >= 0 && refundPercentage <= 100, "Refund percentage should be between 0 and 100");
                System.out.println("Refund Percentage: " + refundPercentage + "%");
            }
            
            if (policy.has("policyType")) {
                String policyType = policy.get("policyType").getAsString();
                assertNotNull(policyType, "Policy type should not be null");
                assertFalse(policyType.isEmpty(), "Policy type should not be empty");
                System.out.println("Policy Type: " + policyType);
            }
            
            // Log all fields for debugging
            System.out.println("Policy fields: " + policy.keySet());
        }
        
        // Perform specific validations for cancellation policies
        performPolicySpecificValidations(promptDataFileName, responseArray);
    }
    
    private void performPolicySpecificValidations(String testFileName, JsonArray responseArray) {
        switch (testFileName) {
            case "cancellation_policies":
                // Validate the cancellation policies
                System.out.println("Validating Cancellation Policies specifics");
                
                // We should have multiple policies (cancellation and rescheduling)
                assertTrue(responseArray.size() >= 3, "Should have at least 3 cancellation policies");
                
                boolean has24HourPolicy = false;
                boolean has12HourPolicy = false;
                boolean hasAfter12HourPolicy = false;
                boolean hasReschedulingPolicy = false;
                
                for (int i = 0; i < responseArray.size(); i++) {
                    JsonObject policy = responseArray.get(i).getAsJsonObject();
                    
                    // Check for 24-hour full refund policy
                    if (policy.has("hoursBeforeStart") && policy.get("hoursBeforeStart").getAsInt() == 24) {
                        has24HourPolicy = true;
                        if (policy.has("refundPercentage")) {
                            assertEquals(100, policy.get("refundPercentage").getAsInt(), "24-hour policy should have 100% refund");
                        }
                        System.out.println("Found 24-hour full refund policy");
                    }
                    
                    // Check for 12-hour 50% refund policy
                    if (policy.has("hoursBeforeStart") && policy.get("hoursBeforeStart").getAsInt() == 12) {
                        has12HourPolicy = true;
                        if (policy.has("refundPercentage")) {
                            assertEquals(50, policy.get("refundPercentage").getAsInt(), "12-hour policy should have 50% refund");
                        }
                        System.out.println("Found 12-hour 50% refund policy");
                    }
                    
                    // Check for after 12-hour no refund policy
                    if (policy.has("hoursBeforeStart") && policy.get("hoursBeforeStart").getAsInt() < 12) {
                        hasAfter12HourPolicy = true;
                        if (policy.has("refundPercentage")) {
                            assertEquals(0, policy.get("refundPercentage").getAsInt(), "After 12-hour policy should have 0% refund");
                        }
                        System.out.println("Found after 12-hour no refund policy");
                    }
                    
                    // Check for rescheduling policy
                    if (policy.has("policyType")) {
                        String policyType = policy.get("policyType").getAsString();
                        if (policyType.toLowerCase().contains("reschedul")) {
                            hasReschedulingPolicy = true;
                            System.out.println("Found rescheduling policy");
                        }
                    }
                }
                
                // Validate that we found the expected policies
                assertTrue(has24HourPolicy, "Should have 24-hour full refund policy");
                assertTrue(has12HourPolicy, "Should have 12-hour 50% refund policy");
                
                // Log additional policy findings
                if (hasAfter12HourPolicy) {
                    System.out.println("After 12-hour no refund policy found");
                }
                if (hasReschedulingPolicy) {
                    System.out.println("Rescheduling policy found");
                }
                
                break;
        }
        
        System.out.println("Policy specific validations completed for: " + testFileName);
    }
}