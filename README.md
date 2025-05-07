# API Unirest Starter

A Java Maven project that demonstrates using Unirest for making HTTP requests and includes JUnit tests.

## Prerequisites

- Java 17 or higher
- Maven

## Project Structure

- `src/main/java/com/example/ApiClient.java` - Main API client class
- `src/test/java/com/example/ApiClientTest.java` - JUnit test class

## Dependencies

- Unirest Java 4.0.0
- JUnit Jupiter 5.9.2

## Running Tests

To run the tests:

```bash
mvn test
```

## Usage

The `ApiClient` class provides a simple method `getApiData()` that takes a URL and returns a JSON response.

```java
ApiClient client = new ApiClient();
JsonNode response = client.getApiData("https://your-api-endpoint.com");
```
