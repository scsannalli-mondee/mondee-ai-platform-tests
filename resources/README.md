# Resources Directory Structure

This directory contains all project resources consolidated from the previous `src/main/resources`, `src/test/resources`, and `testdata` directories.

## Directory Structure

```
resources/
├── README.md                    # This file
├── main/                        # Main application resources (formerly src/main/resources)
│   └── config/
│       └── app_config.json     # Application configuration
├── test/                        # Test resources (formerly src/test/resources)
│   ├── junit-platform.properties
│   └── templates/                # Test report templates (now in config)
│       └── test-report.mustache # HTML report template
└── data/                        # Test data files (formerly testdata)
    ├── experiencebuilder/       # Experience Builder test data
    ├── flightrecommendation/    # Flight recommendation test data
    ├── imagedescription/        # Image description test data
    ├── smartaifill/             # Smart AI fill test data
    ├── title/                   # Title creation test data
    └── videocreation/           # Video creation test data
```

## Usage

### Maven Configuration
The project is configured to use these resources through Maven:

- **Main Resources**: `resources` → `target/classes`
- **Test Resources**: `resources/test` → `target/test-classes`

### Test Data Access
Test data files are accessed using `JsonFileReader.readJsonFile(relativePath)` where `relativePath` is relative to the `resources/data` folder.

#### Examples:
```java
// Load video creation test data
String jsonBody = JsonFileReader.readJsonFile("videocreation/randoms.json");

// Load smart AI fill test data  
String promptBody = JsonFileReader.readJsonFile("smartaifill/extract_price_plan_inventory.json");

// Load experience builder test data
String experienceBody = JsonFileReader.readJsonFile("experiencebuilder/mumbai_spa_heritage_package.json");
```

## Benefits of Consolidation

1. **Simplified Structure**: All resources in one location for easier management
2. **Clear Separation**: Main, test, and data resources clearly separated
3. **Easier Navigation**: No need to search across multiple source directories
4. **Better Organization**: Logical grouping of related resources
5. **Reduced Complexity**: Single resource management strategy

## HTML Report Templates

HTML test reports use the Mustache template located at:
- `resources/config/templates/test-report.mustache`

The HtmlReportGenerator automatically loads this template to generate beautiful, interactive test reports.

## Tag-Based Testing

The project supports tag-based test execution:

### Available Tags:
- **`smoke`**: Core functionality tests - fast, critical tests
- **`regression`**: Comprehensive tests including edge cases and negative scenarios

### Usage Examples:
```bash
# Run only smoke tests
mvn test -Dtest.groups=smoke

# Run only regression tests  
mvn test -Dtest.groups=regression

# Run all tests except smoke
mvn test -Dtest.excludedGroups=smoke
```

## Migration Notes

This structure replaces the previous:
- ❌ `src/main/resources/` → ✅ `resources/`
- ❌ `src/test/resources/` → ✅ `resources/test/`  
- ❌ `testdata/` → ✅ `resources/data/`

All file paths and references have been updated accordingly.
