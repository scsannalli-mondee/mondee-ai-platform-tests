package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

public class ApiClientTest {
    
    @Test
    public void testGetApiData() throws IOException {
        ApiClient client = new ApiClient();
        try {
            var data = client.getApiData("/posts/1");
            assertNotNull(data);
            System.out.println(data.getObject().toString());
            assertTrue(data.getObject().has("title"));
            assertTrue(data.getObject().has("body"));
        } catch (RuntimeException e) {
            fail("API request failed: " + e.getMessage());
        }
    }
}
