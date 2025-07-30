package com.utils;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.AfterAllCallback;

/**
 * Simple JUnit 5 Extension for HTML Report Generation
 */
public class HtmlReportExtension implements BeforeEachCallback, AfterEachCallback, AfterAllCallback {
    
    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        String testName = context.getDisplayName();
        String className = context.getTestClass().map(Class::getSimpleName).orElse("Unknown");
        
        HtmlReportGenerator.startTest(testName, className);
        HtmlReportGenerator.addLog(testName, className, "Test execution started");
    }
    
    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        String testName = context.getDisplayName();
        String className = context.getTestClass().map(Class::getSimpleName).orElse("Unknown");
        
        // Check if test passed or failed
        boolean passed = !context.getExecutionException().isPresent();
        String errorMessage = null;
        
        if (context.getExecutionException().isPresent()) {
            Throwable throwable = context.getExecutionException().get();
            errorMessage = throwable.getMessage();
            if (errorMessage == null) {
                errorMessage = throwable.getClass().getSimpleName();
            }
            
            // Add stack trace as test detail
            StringBuilder stackTrace = new StringBuilder();
            for (StackTraceElement element : throwable.getStackTrace()) {
                stackTrace.append(element.toString()).append("\n");
            }
            HtmlReportGenerator.addTestDetail(testName, className, "stackTrace", stackTrace.toString());
        }
        
        HtmlReportGenerator.endTest(testName, className, passed, errorMessage);
        
        String statusMessage = passed ? "Test completed successfully" : "Test failed";
        HtmlReportGenerator.addLog(testName, className, statusMessage);
    }
    
    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        // Generate report after all tests in this class are completed
        System.out.println("Generating HTML report for class: " + 
            context.getTestClass().map(Class::getSimpleName).orElse("Unknown"));
        HtmlReportGenerator.generateReport();
        
        // Print summary to console
        var stats = HtmlReportGenerator.getTestStatistics();
        System.out.println("\n=== Test Execution Summary ===");
        System.out.println("Total Tests: " + stats.get("totalTests"));
        System.out.println("Passed: " + stats.get("passedTests"));
        System.out.println("Failed: " + stats.get("failedTests"));
        System.out.println("Skipped: " + stats.get("skippedTests"));
        System.out.println("HTML Report: target/html-reports/test-report.html");
        System.out.println("===============================\n");
    }
    
    /**
     * Utility method to add test logs from within test methods
     */
    public static void addLog(String message) {
        // This is a simplified approach - in practice, you might need to track current test context
        System.out.println("[HTML Report] " + message);
    }
    
    /**
     * Utility method to add test details from within test methods
     */
    public static void addTestDetail(String key, Object value) {
        // This is a simplified approach - in practice, you might need to track current test context
        System.out.println("[HTML Report] " + key + ": " + value);
    }
}
