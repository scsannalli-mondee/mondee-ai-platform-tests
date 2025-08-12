#!/bin/bash

# Enhanced Test Runner Script for Experience Categories Tests
# This script runs specific tests and generates both Maven and Enhanced HTML reports
# 
# Features:
# - Runs parameterized tests for real-world experience categorization
# - Generates detailed HTML reports showing:
#   * Test names with actual parameter values (e.g., "bonalu", "dandeli_adventure")
#   * Expected vs Actual categories with visual indicators
#   * Execution times and test status
#   * Professional styling with category matching highlights
#
# Usage:
#   ./run-experience-tests.sh                    # Run category generation tests (default)
#   ./run-experience-tests.sh category          # Run category generation tests  
#   ./run-experience-tests.sh categories        # Run category generation tests  
#   ./run-experience-tests.sh description       # Run category generation with description tests
#   ./run-experience-tests.sh both-generation   # Run both category generation methods together
#   ./run-experience-tests.sh all               # Run all tests

echo "🚀 Starting AI Experience Categories Test Execution..."

# Determine which tests to run based on argument
if [ "$1" = "category" ] || [ "$1" = "categories" ]; then
    TEST_METHOD="testGenerateExperienceCategories"
    echo "📋 Running Category Generation Tests"
    echo "   Running: ExperienceCategoriesTests#testGenerateExperienceCategories"
elif [ "$1" = "description" ]; then
    TEST_METHOD="testGenerateCategoriesWithDescription"
    echo "📋 Running Category Generation with Description Tests"
    echo "   Running: ExperienceCategoriesTests#testGenerateCategoriesWithDescription"
elif [ "$1" = "real-world" ]; then
    TEST_METHOD="testGenerateExperienceCategories"
    echo "📋 Running Category Generation Tests"
    echo "   Running: ExperienceCategoriesTests#testGenerateExperienceCategories"
elif [ "$1" = "both-generation" ]; then
    TEST_METHOD="testGenerateExperienceCategories,testGenerateCategoriesWithDescription"
    echo "📋 Running Both Category Generation Tests (by type + with description)"
    echo "   Running: ExperienceCategoriesTests#testGenerateExperienceCategories"
    echo "   Running: ExperienceCategoriesTests#testGenerateCategoriesWithDescription"
elif [ "$1" = "all" ]; then
    TEST_METHOD=""
    echo "📋 Running All Experience Categories Tests"
else
    TEST_METHOD="testGenerateExperienceCategories"
    echo "📋 Running Category Generation Tests (default)"
    echo "   Running: ExperienceCategoriesTests#testGenerateExperienceCategories"
fi

# Set the test class
TEST_CLASS="ExperienceCategoriesTests"

# Build the Maven command
if [ -z "$TEST_METHOD" ]; then
    MAVEN_CMD="mvn test -Dtest=$TEST_CLASS surefire-report:report"
    echo "   Running: $TEST_CLASS (all methods)"
elif [[ "$TEST_METHOD" == *","* ]]; then
    # Multiple methods - need to run them with separate -Dtest parameters
    IFS=',' read -ra METHODS <<< "$TEST_METHOD"
    TEST_PATTERNS=""
    for method in "${METHODS[@]}"; do
        if [ -z "$TEST_PATTERNS" ]; then
            TEST_PATTERNS="$TEST_CLASS#$method"
        else
            TEST_PATTERNS="$TEST_PATTERNS,$TEST_CLASS#$method"
        fi
    done
    MAVEN_CMD="mvn test -Dtest=\"$TEST_PATTERNS\" surefire-report:report"
    echo "   Running: $TEST_PATTERNS"
else
    MAVEN_CMD="mvn test -Dtest=$TEST_CLASS#$TEST_METHOD surefire-report:report"
    echo "   Running: $TEST_CLASS#$TEST_METHOD"
fi

echo ""
echo "⏳ Executing tests..."

# Run the test with enhanced reporting
eval $MAVEN_CMD
TEST_EXIT_CODE=$?

# Always generate reports regardless of test results
echo ""
if [ $TEST_EXIT_CODE -eq 0 ]; then
    echo "✅ Tests completed successfully!"
else
    echo "⚠️ Tests completed with failures!"
    echo "💡 Check reports below for detailed failure information."
fi

echo ""
echo "📊 Generated Reports:"
echo "1. 📄 Standard Maven Report:"
echo "   file://$(pwd)/target/site/surefire-report.html"
echo ""
echo "2. 🌟 Enhanced Detailed Report:"
echo "   file://$(pwd)/target/enhanced-reports/enhanced-test-report.html"
echo ""
echo "🔍 Enhanced Report Features:"
echo "   • Parameter names (e.g., 'Bonalu', 'Dandeli Adventure')"
echo "   • Expected vs Actual categories comparison"
echo "   • Visual indicators for category matches/mismatches"
echo "   • Detailed execution times and test status"
echo "   • Experience Types validation results"

if [ $TEST_EXIT_CODE -ne 0 ]; then
    echo "   • ⚠️ Failed test details and error analysis"
fi

echo ""

# Open the enhanced report (works on macOS)
if command -v open &> /dev/null; then
    echo "🌐 Opening Enhanced Report in browser..."
    open "file://$(pwd)/target/enhanced-reports/enhanced-test-report.html"
else
    echo "📖 To view the enhanced report, open:"
    echo "   file://$(pwd)/target/enhanced-reports/enhanced-test-report.html"
fi

echo ""
if [ $TEST_EXIT_CODE -eq 0 ]; then
    echo "🎉 Test execution and reporting completed!"
else
    echo "� Test execution completed with failures - check reports for details"
    echo "💡 Tests continued to run and reports were generated for analysis"
fi

# Exit with the original test exit code for CI/CD integration
exit $TEST_EXIT_CODE
