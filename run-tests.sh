#!/bin/bash

# HTML Test Reporting Script
echo "========================================"
echo "Running Tests with HTML Report Generation"
echo "========================================"

# Clean previous reports
echo "Cleaning previous reports..."
rm -rf target/html-reports
rm -rf target/surefire-reports
rm -rf target/site

# Compile the project
echo "Compiling project..."
mvn clean compile test-compile

if [ $? -ne 0 ]; then
    echo "❌ Compilation failed!"
    exit 1
fi

echo "✅ Compilation successful!"

# Run tests with HTML reporting
echo "Running tests with HTML reporting..."
mvn test

TEST_EXIT_CODE=$?

# Generate Surefire HTML reports
echo "Generating Surefire HTML reports..."
mvn surefire-report:report-only

# Check if HTML reports were generated
if [ -f "target/html-reports/test-report.html" ]; then
    echo "✅ HTML Report generated successfully!"
    echo "📊 Report location: target/html-reports/test-report.html"
    
    # Get absolute path for easier access
    REPORT_PATH=$(realpath target/html-reports/test-report.html)
    echo "📂 Full path: $REPORT_PATH"
    
    # Optional: Open report in default browser (uncomment if desired)
    # open "$REPORT_PATH" 2>/dev/null || xdg-open "$REPORT_PATH" 2>/dev/null || echo "Cannot open browser automatically"
    
else
    echo "⚠️  HTML Report not found. Check test execution."
fi

# Check if Surefire reports were generated
if [ -f "target/site/surefire-report.html" ]; then
    echo "✅ Surefire HTML Report generated!"
    echo "📊 Surefire Report: target/site/surefire-report.html"
else
    echo "⚠️  Surefire HTML Report not generated."
fi

# Print test summary
echo ""
echo "========================================"
echo "Test Execution Summary"
echo "========================================"

if [ -d "target/surefire-reports" ]; then
    TOTAL_TESTS=$(find target/surefire-reports -name "TEST-*.xml" -exec grep -o 'tests="[0-9]*"' {} \; | cut -d'"' -f2 | awk '{sum += $1} END {print sum}')
    FAILED_TESTS=$(find target/surefire-reports -name "TEST-*.xml" -exec grep -o 'failures="[0-9]*"' {} \; | cut -d'"' -f2 | awk '{sum += $1} END {print sum}')
    ERROR_TESTS=$(find target/surefire-reports -name "TEST-*.xml" -exec grep -o 'errors="[0-9]*"' {} \; | cut -d'"' -f2 | awk '{sum += $1} END {print sum}')
    SKIPPED_TESTS=$(find target/surefire-reports -name "TEST-*.xml" -exec grep -o 'skipped="[0-9]*"' {} \; | cut -d'"' -f2 | awk '{sum += $1} END {print sum}')
    
    # Default values if not found
    TOTAL_TESTS=${TOTAL_TESTS:-0}
    FAILED_TESTS=${FAILED_TESTS:-0}
    ERROR_TESTS=${ERROR_TESTS:-0}
    SKIPPED_TESTS=${SKIPPED_TESTS:-0}
    
    PASSED_TESTS=$((TOTAL_TESTS - FAILED_TESTS - ERROR_TESTS - SKIPPED_TESTS))
    
    echo "📊 Total Tests: $TOTAL_TESTS"
    echo "✅ Passed: $PASSED_TESTS"
    echo "❌ Failed: $FAILED_TESTS"
    echo "⚠️  Errors: $ERROR_TESTS"
    echo "⏭️  Skipped: $SKIPPED_TESTS"
    
    if [ $TOTAL_TESTS -gt 0 ]; then
        PASS_RATE=$(echo "scale=1; $PASSED_TESTS * 100 / $TOTAL_TESTS" | bc -l 2>/dev/null || echo "0")
        echo "📈 Pass Rate: ${PASS_RATE}%"
    fi
else
    echo "⚠️  No test results found in target/surefire-reports"
fi

echo ""
echo "Available Reports:"
echo "==================="

if [ -f "target/html-reports/test-report.html" ]; then
    echo "🎯 Custom HTML Report: target/html-reports/test-report.html"
fi

if [ -f "target/site/surefire-report.html" ]; then
    echo "📋 Surefire Report: target/site/surefire-report.html"
fi

if [ -d "target/surefire-reports" ]; then
    echo "📄 XML Reports: target/surefire-reports/"
fi

echo ""
echo "========================================"

# Exit with test exit code
if [ $TEST_EXIT_CODE -eq 0 ]; then
    echo "🎉 All tests completed successfully!"
else
    echo "⚠️  Some tests failed. Check reports for details."
fi

exit $TEST_EXIT_CODE
