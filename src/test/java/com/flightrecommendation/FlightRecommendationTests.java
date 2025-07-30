package com.flightrecommendation;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;

import com.aiservices.ApiClient;
import com.utils.JsonFileReader;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;

public class FlightRecommendationTests {

    @ParameterizedTest
    @ValueSource(strings = {"loyalistcustomer"})
    @DisplayName("Test Flight Recommendation API orders recommendations by price")
    @Tag("regression")
    public void testFlightRecommendationOrderByPrice(String flightDataFileName) throws IOException {
        ApiClient client = new ApiClient();
        String jsonBody = JsonFileReader.readJsonFile("flightrecommendation/" + flightDataFileName + ".json");
        var response = client.generateFlightRecommendation("/flight_recommendation/predict", jsonBody);
        System.out.println("API Response: " + response);
        assertNotNull(response, "API response should not be null");
    }
}