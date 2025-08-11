# Mondee AI Platform Test Suite

A comprehensive Java Maven project containing all Mondee AI API tests, built using Unirest for HTTP requests and JUnit for testing.

## Prerequisites

- Java 17 or higher
- Maven

## Project Structure

```
├── src/
│   ├── main/java/               # Main source code
│   │   ├── com/aiservices/      # API client classes
│   │   └── com/utils/           # Utility classes (JsonFileReader, HtmlReportGenerator)
│   └── test/java/               # Test classes
│       ├── com/aiservicestests/ # AI service tests
│       ├── com/experiencetests/ # Experience builder tests
│       ├── com/flightrecommendation/ # Flight recommendation tests
│       └── com/reelgenerator/   # Reel generator tests
├── resources/                   # Consolidated resources directory
│   ├── config/                  # Application configuration (app_config.json)
│   ├── test/                    # Test resources (templates, properties)
│   └── data/                    # Test data files
├── target/                      # Build output
│   ├── html-reports/            # Custom HTML test reports
│   └── site/                    # Maven site reports
└── pom.xml                      # Maven configuration
```

## Features

- **Tag-Based Testing**: Run specific test suites using `smoke` and `regression` tags
- **HTML Reporting**: Beautiful, interactive HTML test reports with detailed logs
- **JSON Test Data**: Organized test data files for different API endpoints
- **API Testing**: Comprehensive tests for AI services, experience builders, and more

## Dependencies

- Unirest Java 3.14.5 - HTTP client
- JUnit Jupiter 5.9.2 - Testing framework
- Gson 2.10.1 - JSON processing
- Mustache Java 0.9.10 - HTML template engine

## Running Tests

### Run All Tests
```bash
mvn test
```
or 
```bash
./run-tests.sh
```

### Run by Tags
```bash
# Run only smoke tests (core functionality)
mvn test -Dtest.groups=smoke
`or` 
./run-tests.sh smoke

# Run only regression tests (comprehensive testing)
mvn test -Dtest.groups=regression

# Run all except smoke tests
mvn test -Dtest.excludedGroups=smoke
```

### Run Specific Test Classes
```bash
# Run Experience Builder tests
mvn test -Dtest=ExperienceBuilderTests

# Run Price Plan tests
mvn test -Dtest=PricePlanRulesExtractionTests


# To run the specific test method 
 
```bash
mvn test -Dtest="com.experienceCategoriesTests.ExperienceCategoriesTests#testGenerateExperienceCategories"
 
## Test Reports

After running tests, reports are generated in two locations:

1. **Custom HTML Report**: `target/html-reports/test-report.html` - Interactive report with detailed logs
2. **Surefire Report**: `target/site/surefire-report.html` - Standard Maven test report

## Test Data

Test data files are located in `resources/data/` and organized by feature:
- `experiencebuilder/` - Experience Builder API test data
- `flightrecommendation/` - Flight recommendation test data  
- `smartaifill/` - Smart AI fill test data
- `videocreation/` - Video creation test data

## Usage

The test suite includes multiple API clients and utilities for testing various AI Platform services. Key classes:

- `ApiClient` - Main HTTP client for API interactions
- `JsonFileReader` - Utility for loading test data from JSON files
- `HtmlReportGenerator` - Generates detailed HTML test reports

```java
ApiClient client = new ApiClient();
JsonNode response = client.getApiData("https://your-api-endpoint.com");
```
