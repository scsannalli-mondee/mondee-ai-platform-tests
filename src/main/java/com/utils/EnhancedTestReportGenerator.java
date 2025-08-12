package com.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Enhanced Test Report Generator that creates meaningful HTML reports for parameterized tests
 */
public class EnhancedTestReportGenerator {

    // Data class to hold test execution details
    static class TestExecutionDetail {
        String testName;
        String parameterName;
        String expectedCategory;
        String actualCategories;
        String actualExperienceTypes;
        String status;
        String executionTime;
        String methodType;
        boolean hasExpectedCategory;
        
        TestExecutionDetail(String testName, String parameterName, String expectedCategory, 
                          String actualCategories, String actualExperienceTypes, String status, String executionTime, String methodType) {
            this.testName = testName;
            this.parameterName = parameterName;
            this.expectedCategory = expectedCategory;
            this.actualCategories = actualCategories;
            this.actualExperienceTypes = actualExperienceTypes;
            this.status = status;
            this.executionTime = executionTime;
            this.methodType = methodType;
            this.hasExpectedCategory = expectedCategory != null && !expectedCategory.isEmpty();
        }
    }

    public static void generateEnhancedReport(String surefireReportsDir, String outputDir) throws Exception {
        File reportsDir = new File(surefireReportsDir);
        File[] xmlFiles = reportsDir.listFiles((dir, name) -> name.startsWith("TEST-") && name.endsWith(".xml"));
        
        if (xmlFiles == null || xmlFiles.length == 0) {
            System.out.println("No test report XML files found in: " + surefireReportsDir);
            return;
        }

        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append(generateHtmlHeader());
        
        int totalTests = 0;
        int passedTests = 0;
        int failedTests = 0;
        int errorTests = 0;
        double totalTime = 0.0;
        
        List<TestExecutionDetail> allTestDetails = new ArrayList<>();
        
        for (File xmlFile : xmlFiles) {
            Document doc = parseXmlFile(xmlFile);
            NodeList testSuites = doc.getElementsByTagName("testsuite");
            
            for (int i = 0; i < testSuites.getLength(); i++) {
                Element testSuite = (Element) testSuites.item(i);
                String className = testSuite.getAttribute("name");
                
                if (className.contains("ExperienceCategoriesTests")) {
                    List<TestExecutionDetail> testDetails = extractTestDetails(testSuite);
                    allTestDetails.addAll(testDetails);
                    
                    // Update totals
                    totalTests += Integer.parseInt(testSuite.getAttribute("tests"));
                    failedTests += Integer.parseInt(testSuite.getAttribute("failures"));
                    errorTests += Integer.parseInt(testSuite.getAttribute("errors"));
                    totalTime += Double.parseDouble(testSuite.getAttribute("time"));
                }
            }
        }
        
        passedTests = totalTests - failedTests - errorTests;
        
        String summarySection = generateSummarySection(totalTests, passedTests, failedTests, errorTests, totalTime);
        htmlContent.append(summarySection);
        
        // Generate detailed test sections
        htmlContent.append(generateDetailedTestSection(allTestDetails));
        
        htmlContent.append(generateHtmlFooter());
        
        // Write the enhanced HTML report
        Files.createDirectories(Paths.get(outputDir));
        try (FileWriter writer = new FileWriter(outputDir + "/enhanced-test-report.html")) {
            writer.write(htmlContent.toString());
        }
        
        System.out.println("Enhanced HTML test report generated: " + outputDir + "/enhanced-test-report.html");
    }
    
    private static List<TestExecutionDetail> extractTestDetails(Element testSuite) {
        List<TestExecutionDetail> details = new ArrayList<>();
        NodeList testCases = testSuite.getElementsByTagName("testcase");
        
        for (int i = 0; i < testCases.getLength(); i++) {
            Element testCase = (Element) testCases.item(i);
            String methodName = testCase.getAttribute("name");
            String time = testCase.getAttribute("time");
            
            // Determine status
            String status = "PASS";
            if (testCase.getElementsByTagName("failure").getLength() > 0) {
                status = "FAIL";
            } else if (testCase.getElementsByTagName("error").getLength() > 0) {
                status = "ERROR";
            }
            
            // Extract system-out content for expected and actual categories
            NodeList systemOuts = testCase.getElementsByTagName("system-out");
            String systemOutput = "";
            if (systemOuts.getLength() > 0) {
                systemOutput = systemOuts.item(0).getTextContent();
            }
            
            // Parse test details
            TestExecutionDetail detail = parseTestOutput(methodName, systemOutput, status, time, i);
            details.add(detail);
        }
        
        return details;
    }
    
    private static TestExecutionDetail parseTestOutput(String methodName, String systemOutput, 
                                                     String status, String time, int index) {
        String testName = "";
        String parameterName = "";
        String expectedCategory = "";
        String actualCategories = "";
        String methodType = "";
        
        // Determine method type and parameter by parsing system output
        if (methodName.contains("testGenerateCategoriesWithDescription")) {
            methodType = "Category Generation with Description";
            testName = "AI Category Generation from Description";
            
            // Extract parameter from system output
            String parameterPattern = "Starting Category Generation with Description Test for: (.+)";
            java.util.regex.Pattern paramPattern = java.util.regex.Pattern.compile(parameterPattern);
            java.util.regex.Matcher paramMatcher = paramPattern.matcher(systemOutput);
            if (paramMatcher.find()) {
                parameterName = formatExperienceName(paramMatcher.group(1));
            }
            
            // Extract expected category from system output
            String expectedPattern = "Expected category for [^:]+: (.+)";
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(expectedPattern);
            java.util.regex.Matcher matcher = pattern.matcher(systemOutput);
            if (matcher.find()) {
                expectedCategory = matcher.group(1);
            }
            
        } else if (methodName.contains("testGenerateExperienceCategories")) {
            methodType = "Category Generation";
            testName = "AI Category Generation by Type";
            
            // Extract parameter from system output
            String parameterPattern = "Starting Experience Categories Generation Test for: category_(.+)";
            java.util.regex.Pattern paramPattern = java.util.regex.Pattern.compile(parameterPattern);
            java.util.regex.Matcher paramMatcher = paramPattern.matcher(systemOutput);
            if (paramMatcher.find()) {
                parameterName = paramMatcher.group(1);
                expectedCategory = paramMatcher.group(1); // For category generation, expected = parameter
            }
        }
        
        // Extract actual categories from AI response
        String responsePattern = "\"experienceCategory\":\\[([^\\]]+)\\]";
        java.util.regex.Pattern responsePatternCompiled = java.util.regex.Pattern.compile(responsePattern);
        java.util.regex.Matcher responseMatcher = responsePatternCompiled.matcher(systemOutput);
        if (responseMatcher.find()) {
            actualCategories = responseMatcher.group(1).replaceAll("\"", "").trim();
        }
        
        // Extract actual experienceTypes from AI response
        String actualExperienceTypes = "";
        String experienceTypesPattern = "\"experienceTypes\":\\[([^\\]]+)\\]";
        java.util.regex.Pattern experienceTypesPatternCompiled = java.util.regex.Pattern.compile(experienceTypesPattern);
        java.util.regex.Matcher experienceTypesMatcher = experienceTypesPatternCompiled.matcher(systemOutput);
        if (experienceTypesMatcher.find()) {
            actualExperienceTypes = experienceTypesMatcher.group(1).replaceAll("\"", "").trim();
        }
        
        return new TestExecutionDetail(testName, parameterName, expectedCategory, 
                                     actualCategories, actualExperienceTypes, status, time, methodType);
    }
    
    private static String generateDetailedTestSection(List<TestExecutionDetail> testDetails) {
        StringBuilder section = new StringBuilder();
        
        // Group tests by method type
        Map<String, List<TestExecutionDetail>> groupedTests = new HashMap<>();
        for (TestExecutionDetail detail : testDetails) {
            groupedTests.computeIfAbsent(detail.methodType, k -> new ArrayList<>()).add(detail);
        }
        
        for (Map.Entry<String, List<TestExecutionDetail>> entry : groupedTests.entrySet()) {
            String methodType = entry.getKey();
            List<TestExecutionDetail> tests = entry.getValue();
            
            section.append(String.format("<h2>🧪 %s Tests</h2>\n", methodType));
            section.append(generateDetailedTestTable(tests));
        }
        
        return section.toString();
    }
    
    private static String generateDetailedTestTable(List<TestExecutionDetail> tests) {
        StringBuilder table = new StringBuilder();
        
        table.append("""
            <table class="test-table">
                <thead>
                    <tr>
                        <th>Status</th>
                        <th>Test Name</th>
                        <th>Parameter</th>
                        <th>Expected Category</th>
                        <th>Actual Categories</th>
                        <th>Experience Types</th>
                        <th>Execution Time</th>
                    </tr>
                </thead>
                <tbody>
            """);
        
        for (TestExecutionDetail test : tests) {
            String statusClass = getStatusClass(test.status);
            String expectedCategoryDisplay = test.hasExpectedCategory ? test.expectedCategory : "N/A";
            String actualCategoriesDisplay = test.actualCategories.isEmpty() ? "No categories found" : test.actualCategories;
            String actualExperienceTypesDisplay = test.actualExperienceTypes.isEmpty() ? "None" : test.actualExperienceTypes;
            
            // Check if expected category is found in actual categories
            String categoryMatchClass = "";
            if (test.hasExpectedCategory && test.actualCategories.toLowerCase().contains(test.expectedCategory.toLowerCase())) {
                categoryMatchClass = "category-match";
            } else if (test.hasExpectedCategory) {
                categoryMatchClass = "category-mismatch";
            }
            
            table.append(String.format("""
                <tr>
                    <td><span class="status-icon %s"></span>%s</td>
                    <td class="test-name">%s</td>
                    <td><strong>%s</strong></td>
                    <td class="expected-category">%s</td>
                    <td class="actual-categories %s">%s</td>
                    <td class="experience-types">%s</td>
                    <td class="execution-time">%s seconds</td>
                </tr>
                """, statusClass, test.status, test.testName, test.parameterName, 
                expectedCategoryDisplay, categoryMatchClass, actualCategoriesDisplay, actualExperienceTypesDisplay, test.executionTime));
        }
        
        table.append("""
                </tbody>
            </table>
            """);
        
        return table.toString();
    }
    
    private static String getStatusClass(String status) {
        switch (status) {
            case "PASS": return "status-pass";
            case "FAIL": return "status-fail";
            case "ERROR": return "status-error";
            default: return "status-pass";
        }
    }
    
    private static Document parseXmlFile(File xmlFile) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(xmlFile);
    }
    
    private static String generateHtmlHeader() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Enhanced Test Report - Experience Categories Tests</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }
                    .container { max-width: 1200px; margin: 0 auto; background-color: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
                    h1 { color: #333; text-align: center; margin-bottom: 30px; }
                    h2 { color: #2c5282; border-bottom: 2px solid #2c5282; padding-bottom: 10px; }
                    h3 { color: #4a5568; margin-top: 25px; }
                    .summary { background-color: #f7fafc; padding: 20px; border-radius: 8px; margin-bottom: 30px; }
                    .summary-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(150px, 1fr)); gap: 15px; }
                    .summary-item { text-align: center; padding: 15px; background-color: white; border-radius: 6px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
                    .summary-value { font-size: 24px; font-weight: bold; margin-bottom: 5px; }
                    .summary-label { font-size: 14px; color: #666; }
                    .passed { color: #38a169; }
                    .failed { color: #e53e3e; }
                    .error { color: #d69e2e; }
                    .total { color: #3182ce; }
                    .test-table { width: 100%; border-collapse: collapse; margin-top: 15px; }
                    .test-table th { background-color: #2d3748; color: white; padding: 12px; text-align: left; }
                    .test-table td { padding: 10px; border-bottom: 1px solid #e2e8f0; }
                    .test-table tr:nth-child(even) { background-color: #f7fafc; }
                    .status-icon { width: 20px; height: 20px; border-radius: 50%; display: inline-block; margin-right: 8px; }
                    .status-pass { background-color: #38a169; }
                    .status-fail { background-color: #e53e3e; }
                    .status-error { background-color: #d69e2e; }
                    .test-name { font-weight: 500; }
                    .execution-time { color: #666; font-size: 0.9em; }
                    .timestamp { text-align: right; color: #666; font-size: 0.9em; margin-bottom: 20px; }
                    .expected-category { font-weight: 600; color: #2d3748; background-color: #e6fffa; padding: 5px 8px; border-radius: 4px; }
                    .actual-categories { font-weight: 500; }
                    .category-match { color: #38a169; background-color: #f0fff4; padding: 5px 8px; border-radius: 4px; border-left: 4px solid #38a169; }
                    .category-mismatch { color: #e53e3e; background-color: #fef5e7; padding: 5px 8px; border-radius: 4px; border-left: 4px solid #e53e3e; }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>🚀 AI Experience Categories Test Report</h1>
                    <div class="timestamp">Generated on: """ + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + """
                    </div>
            """;
    }
    
    private static String generateSummarySection(int total, int passed, int failed, int errors, double totalTime) {
        double successRate = total > 0 ? (double) passed / total * 100 : 0;
        
        return String.format("""
            <div class="summary">
                <h2>📊 Test Execution Summary</h2>
                <div class="summary-grid">
                    <div class="summary-item">
                        <div class="summary-value total">%d</div>
                        <div class="summary-label">Total Tests</div>
                    </div>
                    <div class="summary-item">
                        <div class="summary-value passed">%d</div>
                        <div class="summary-label">Passed</div>
                    </div>
                    <div class="summary-item">
                        <div class="summary-value failed">%d</div>
                        <div class="summary-label">Failed</div>
                    </div>
                    <div class="summary-item">
                        <div class="summary-value error">%d</div>
                        <div class="summary-label">Errors</div>
                    </div>
                    <div class="summary-item">
                        <div class="summary-value">%.1f%%</div>
                        <div class="summary-label">Success Rate</div>
                    </div>
                    <div class="summary-item">
                        <div class="summary-value">%.2fs</div>
                        <div class="summary-label">Total Time</div>
                    </div>
                </div>
            </div>
            """, total, passed, failed, errors, successRate, totalTime);
    }
    
    private static String formatExperienceName(String experienceFile) {
        return Arrays.stream(experienceFile.split("_"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                .reduce((a, b) -> a + " " + b)
                .orElse(experienceFile);
    }
    
    private static String generateHtmlFooter() {
        return """
                </div>
            </body>
            </html>
            """;
    }
    
    public static void main(String[] args) {
        try {
            String surefireReportsDir = args.length > 0 ? args[0] : "target/surefire-reports";
            String outputDir = args.length > 1 ? args[1] : "target/enhanced-reports";
            
            generateEnhancedReport(surefireReportsDir, outputDir);
        } catch (Exception e) {
            System.err.println("Error generating enhanced report: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
