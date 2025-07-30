package com.utils;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Simple HTML Report Generator for Test Execution Details
 */
public class HtmlReportGenerator {
    
    private static final Map<String, TestExecution> testExecutions = new ConcurrentHashMap<>();
    private static final List<TestResult> testResults = new CopyOnWriteArrayList<>();
    private static final String REPORT_DIR = "target/html-reports";
    private static final String TEMPLATE_DIR = "resources/config/templates";
    
    // Test execution tracking
    public static class TestExecution {
        public String testName;
        public String className;
        public String status;
        public LocalDateTime startTime;
        public LocalDateTime endTime;
        public long durationMs;
        public String errorMessage;
        public List<String> logs;
        public Map<String, Object> details;
        
        public TestExecution(String testName, String className) {
            this.testName = testName;
            this.className = className;
            this.status = "RUNNING";
            this.startTime = LocalDateTime.now();
            this.logs = new ArrayList<>();
            this.details = new HashMap<>();
        }
        
        public String getFormattedStartTime() {
            return startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        
        public String getFormattedEndTime() {
            return endTime != null ? endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "";
        }
        
        public String getFormattedDuration() {
            if (durationMs < 1000) {
                return durationMs + "ms";
            } else if (durationMs < 60000) {
                return String.format("%.2fs", durationMs / 1000.0);
            } else {
                return String.format("%.2fm", durationMs / 60000.0);
            }
        }
    }
    
    public static class TestResult {
        public String testName;
        public String className;
        public String status;
        public String startTime;
        public String endTime;
        public String duration;
        public String errorMessage;
        public List<String> logs;
        public Map<String, Object> details;
        
        public TestResult(TestExecution execution) {
            this.testName = execution.testName;
            this.className = execution.className;
            this.status = execution.status;
            this.startTime = execution.getFormattedStartTime();
            this.endTime = execution.getFormattedEndTime();
            this.duration = execution.getFormattedDuration();
            this.errorMessage = execution.errorMessage;
            this.logs = new ArrayList<>(execution.logs);
            this.details = new HashMap<>(execution.details);
        }
    }
    
    public static class TestSummary {
        public int totalTests;
        public int passedTests;
        public int failedTests;
        public int skippedTests;
        public String executionTime;
        public List<TestResult> results;
        public String reportGeneratedAt;
        
        public double getPassRate() {
            return totalTests > 0 ? (double) passedTests / totalTests * 100 : 0;
        }
        
        public String getFormattedPassRate() {
            return String.format("%.1f%%", getPassRate());
        }
    }
    
    /**
     * Start tracking a test execution
     */
    public static void startTest(String testName, String className) {
        String key = className + "." + testName;
        TestExecution execution = new TestExecution(testName, className);
        testExecutions.put(key, execution);
        System.out.println("Started test: " + key);
    }
    
    /**
     * End test execution and record result
     */
    public static void endTest(String testName, String className, boolean passed, String errorMessage) {
        String key = className + "." + testName;
        TestExecution execution = testExecutions.get(key);
        
        if (execution != null) {
            execution.endTime = LocalDateTime.now();
            execution.durationMs = java.time.Duration.between(execution.startTime, execution.endTime).toMillis();
            execution.status = passed ? "PASSED" : "FAILED";
            execution.errorMessage = errorMessage;
            
            testResults.add(new TestResult(execution));
            System.out.println("Ended test: " + key + " - " + execution.status);
        }
    }
    
    /**
     * Add log entry to current test
     */
    public static void addLog(String testName, String className, String logMessage) {
        String key = className + "." + testName;
        TestExecution execution = testExecutions.get(key);
        
        if (execution != null) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
            execution.logs.add("[" + timestamp + "] " + logMessage);
        }
    }
    
    /**
     * Add test detail
     */
    public static void addTestDetail(String testName, String className, String key, Object value) {
        String testKey = className + "." + testName;
        TestExecution execution = testExecutions.get(testKey);
        
        if (execution != null) {
            execution.details.put(key, value);
        }
    }
    
    /**
     * Generate HTML report
     */
    public static void generateReport() {
        try {
            // Create report directory
            Path reportDir = Paths.get(REPORT_DIR);
            Files.createDirectories(reportDir);
            
            // Create template directory if it doesn't exist
            Path templateDir = Paths.get(TEMPLATE_DIR);
            Files.createDirectories(templateDir);
            
            // Create HTML template if it doesn't exist
            createHtmlTemplate();
            
            // Calculate summary
            TestSummary summary = calculateSummary();
            
            // Generate HTML report
            generateHtmlReport(summary);
            
            // Generate CSS file
            generateCssFile();
            
            System.out.println("HTML Report generated at: " + REPORT_DIR + "/test-report.html");
            
        } catch (Exception e) {
            System.err.println("Error generating HTML report: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static TestSummary calculateSummary() {
        TestSummary summary = new TestSummary();
        summary.totalTests = testResults.size();
        summary.passedTests = (int) testResults.stream().filter(r -> "PASSED".equals(r.status)).count();
        summary.failedTests = (int) testResults.stream().filter(r -> "FAILED".equals(r.status)).count();
        summary.skippedTests = (int) testResults.stream().filter(r -> "SKIPPED".equals(r.status)).count();
        summary.results = new ArrayList<>(testResults);
        summary.reportGeneratedAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        // Calculate total execution time
        if (!testResults.isEmpty()) {
            long totalMs = testResults.stream()
                .mapToLong(r -> {
                    try {
                        if (r.duration.endsWith("ms")) {
                            return Long.parseLong(r.duration.replace("ms", ""));
                        } else if (r.duration.endsWith("s")) {
                            return (long) (Double.parseDouble(r.duration.replace("s", "")) * 1000);
                        } else if (r.duration.endsWith("m")) {
                            return (long) (Double.parseDouble(r.duration.replace("m", "")) * 60000);
                        }
                        return 0;
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                })
                .sum();
            
            if (totalMs < 1000) {
                summary.executionTime = totalMs + "ms";
            } else if (totalMs < 60000) {
                summary.executionTime = String.format("%.2fs", totalMs / 1000.0);
            } else {
                summary.executionTime = String.format("%.2fm", totalMs / 60000.0);
            }
        } else {
            summary.executionTime = "0ms";
        }
        
        return summary;
    }
    
    private static void createHtmlTemplate() throws IOException {
        Path templatePath = Paths.get(TEMPLATE_DIR, "test-report.mustache");
        
        if (!Files.exists(templatePath)) {
            String template = """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Test Execution Report</title>
    <link rel="stylesheet" href="report-style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>Test Execution Report</h1>
            <p class="report-date">Generated on: {{reportGeneratedAt}}</p>
        </header>
        
        <section class="summary">
            <h2>Test Summary</h2>
            <div class="summary-cards">
                <div class="card total">
                    <h3>Total Tests</h3>
                    <span class="number">{{totalTests}}</span>
                </div>
                <div class="card passed">
                    <h3>Passed</h3>
                    <span class="number">{{passedTests}}</span>
                </div>
                <div class="card failed">
                    <h3>Failed</h3>
                    <span class="number">{{failedTests}}</span>
                </div>
                <div class="card skipped">
                    <h3>Skipped</h3>
                    <span class="number">{{skippedTests}}</span>
                </div>
            </div>
            <div class="summary-details">
                <p><strong>Pass Rate:</strong> {{formattedPassRate}}</p>
                <p><strong>Execution Time:</strong> {{executionTime}}</p>
            </div>
        </section>
        
        <section class="test-results">
            <h2>Test Results</h2>
            <table class="results-table">
                <thead>
                    <tr>
                        <th>Test Name</th>
                        <th>Class</th>
                        <th>Status</th>
                        <th>Start Time</th>
                        <th>Duration</th>
                        <th>Details</th>
                    </tr>
                </thead>
                <tbody>
                    {{#results}}
                    <tr class="{{status}}">
                        <td>{{testName}}</td>
                        <td>{{className}}</td>
                        <td><span class="status-badge {{status}}">{{status}}</span></td>
                        <td>{{startTime}}</td>
                        <td>{{duration}}</td>
                        <td>
                            <button class="details-btn" onclick="toggleDetails('{{className}}-{{testName}}')">
                                View Details
                            </button>
                        </td>
                    </tr>
                    <tr class="details-row" id="{{className}}-{{testName}}-details" style="display: none;">
                        <td colspan="6">
                            <div class="test-details">
                                {{#errorMessage}}
                                <div class="error-section">
                                    <h4>Error Message:</h4>
                                    <pre>{{errorMessage}}</pre>
                                </div>
                                {{/errorMessage}}
                                
                                {{#logs}}
                                {{#.}}
                                <div class="logs-section">
                                    <h4>Test Logs:</h4>
                                    <pre class="logs">{{#logs}}{{.}}
{{/logs}}</pre>
                                </div>
                                {{/.}}
                                {{/logs}}
                                
                                <div class="test-data-section">
                                    <h4>Test Data:</h4>
                                    <pre class="test-data">{{#details}}{{#.}}{{@key}}: {{.}}
{{/.}}{{/details}}</pre>
                                </div>
                            </div>
                        </td>
                    </tr>
                    {{/results}}
                </tbody>
            </table>
        </section>
    </div>
    
    <script>
        function toggleDetails(testId) {
            const detailsRow = document.getElementById(testId + '-details');
            if (detailsRow.style.display === 'none') {
                detailsRow.style.display = 'table-row';
            } else {
                detailsRow.style.display = 'none';
            }
        }
    </script>
</body>
</html>
            """;
            
            Files.write(templatePath, template.getBytes());
        }
    }
    
    private static void generateHtmlReport(TestSummary summary) throws IOException {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("config/templates/test-report.mustache");
        
        Path reportPath = Paths.get(REPORT_DIR, "test-report.html");
        try (FileWriter writer = new FileWriter(reportPath.toFile())) {
            mustache.execute(writer, summary);
        }
    }
    
    private static void generateCssFile() throws IOException {
        String css = """
/* Test Report Styles */
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

body {
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
    background-color: #f5f5f5;
    color: #333;
    line-height: 1.6;
}

.container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
}

header {
    text-align: center;
    margin-bottom: 30px;
}

header h1 {
    color: #2c3e50;
    font-size: 2.5em;
    margin-bottom: 10px;
}

.report-date {
    color: #666;
    font-size: 1.1em;
}

.summary {
    background: white;
    border-radius: 8px;
    padding: 25px;
    margin-bottom: 30px;
    box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.summary h2 {
    color: #2c3e50;
    margin-bottom: 20px;
    font-size: 1.8em;
}

.summary-cards {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 20px;
    margin-bottom: 20px;
}

.card {
    background: #f8f9fa;
    border-radius: 6px;
    padding: 20px;
    text-align: center;
    border-left: 4px solid #ddd;
}

.card.total { border-left-color: #3498db; }
.card.passed { border-left-color: #27ae60; }
.card.failed { border-left-color: #e74c3c; }
.card.skipped { border-left-color: #f39c12; }

.card h3 {
    font-size: 0.9em;
    color: #666;
    margin-bottom: 10px;
    text-transform: uppercase;
    letter-spacing: 1px;
}

.card .number {
    font-size: 2.5em;
    font-weight: bold;
    color: #2c3e50;
}

.summary-details {
    background: #ecf0f1;
    border-radius: 6px;
    padding: 15px;
}

.summary-details p {
    margin-bottom: 5px;
    font-size: 1.1em;
}

.test-results {
    background: white;
    border-radius: 8px;
    padding: 25px;
    box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.test-results h2 {
    color: #2c3e50;
    margin-bottom: 20px;
    font-size: 1.8em;
}

.results-table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 20px;
}

.results-table th {
    background: #34495e;
    color: white;
    padding: 12px;
    text-align: left;
    font-weight: 600;
}

.results-table td {
    padding: 12px;
    border-bottom: 1px solid #ecf0f1;
}

.results-table tr:hover {
    background: #f8f9fa;
}

.status-badge {
    padding: 4px 12px;
    border-radius: 4px;
    font-size: 0.85em;
    font-weight: bold;
    text-transform: uppercase;
}

.status-badge.PASSED {
    background: #27ae60;
    color: white;
}

.status-badge.FAILED {
    background: #e74c3c;
    color: white;
}

.status-badge.SKIPPED {
    background: #f39c12;
    color: white;
}

.details-btn {
    background: #3498db;
    color: white;
    border: none;
    padding: 6px 12px;
    border-radius: 4px;
    cursor: pointer;
    font-size: 0.9em;
}

.details-btn:hover {
    background: #2980b9;
}

.details-row {
    background: #f8f9fa;
}

.test-details {
    padding: 20px;
    background: white;
    border-radius: 6px;
    margin: 10px;
}

.test-details h4 {
    color: #2c3e50;
    margin-bottom: 10px;
    font-size: 1.1em;
}

.test-details pre {
    background: #f4f4f4;
    padding: 15px;
    border-radius: 4px;
    overflow-x: auto;
    font-family: 'Courier New', monospace;
    font-size: 0.9em;
    line-height: 1.4;
}

.error-section pre {
    background: #ffebee;
    border-left: 4px solid #e74c3c;
}

.logs {
    max-height: 300px;
    overflow-y: auto;
}

@media (max-width: 768px) {
    .container {
        padding: 10px;
    }
    
    .summary-cards {
        grid-template-columns: 1fr 1fr;
    }
    
    .results-table {
        font-size: 0.9em;
    }
    
    .results-table th,
    .results-table td {
        padding: 8px;
    }
}
        """;
        
        Path cssPath = Paths.get(REPORT_DIR, "report-style.css");
        Files.write(cssPath, css.getBytes());
    }
    
    /**
     * Clear all test data (useful for new test runs)
     */
    public static void clearTestData() {
        testExecutions.clear();
        testResults.clear();
    }
    
    /**
     * Get current test statistics
     */
    public static Map<String, Object> getTestStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTests", testResults.size());
        stats.put("passedTests", testResults.stream().filter(r -> "PASSED".equals(r.status)).count());
        stats.put("failedTests", testResults.stream().filter(r -> "FAILED".equals(r.status)).count());
        stats.put("skippedTests", testResults.stream().filter(r -> "SKIPPED".equals(r.status)).count());
        return stats;
    }
}
