package com.loganalyzer.report;

import com.loganalyzer.db.LogRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

//Generates annd prints a formatted log analysis report

public class ReportGenerator {

    //Repository used to fetch log statistics
    private final LogRepository repository;

    //Initializes the report generator
    public ReportGenerator(LogRepository repository){
        this.repository = repository;
    }

    //Prints the complete analysis report
    public void printFullReport() throws SQLException{
        printHeader("LOG ANALYSIS REPORT");

        //Displaying log count grouped by severity
        printSection("Entries by Severity");
        Map<String, Long> bySeverity = repository.countBySeverity();
        if(bySeverity.isEmpty())
        {
            System.out.println("  (no data)");
        }
        else
        {
            bySeverity.forEach((severity, count) ->
                    System.out.printf("  %-10s %d%n", severity, count));
        }
        // Display most frequent root causes
        printSection("Top Root Causes (Recurring Error Patterns)");
        List<LogRepository.RootCauseCount> rootCauses = repository.topRootCauses(10);
        if (rootCauses.isEmpty()) {
            System.out.println("  (no classified root causes found)");
        } else {
            int rank = 1;
            for (LogRepository.RootCauseCount rc : rootCauses) {
                System.out.printf("  %2d. %-30s %d occurrence(s)%n",
                        rank++, rc.rootCause(), rc.count());
            }
        }

        // Display components with the highest error count
        printSection("Most Error-Prone Components (by logger)");
        List<LogRepository.LoggerErrorCount> loggers = repository.topErrorProneLoggers(10);
        if (loggers.isEmpty()) {
            System.out.println("  (no error-level entries found)");
        } else {
            for (LogRepository.LoggerErrorCount lc : loggers) {
                System.out.printf("  %-40s %d error(s)%n",
                        lc.logger(), lc.count());
            }
        }

        // Display daily error trend as a simple bar chart
        printSection("Error Trend by Day");
        Map<String, Long> trend = repository.errorTrendByDay();
        if (trend.isEmpty()) {
            System.out.println("  (no error/fatal entries found)");
        } else {
            trend.forEach((day, count) -> {
                String bar = "#".repeat((int) Math.min(count, 50));
                System.out.printf("  %-12s %-50s %d%n", day, bar, count);
            });
        }

        System.out.println();
    }

    // Prints the report title
    private void printHeader(String title) {
        System.out.println("=".repeat(60));
        System.out.println(title);
        System.out.println("=".repeat(60));
    }

    // Prints a section heading
    private void printSection(String title) {
        System.out.println();
        System.out.println("-- " + title + " " + "-".repeat(Math.max(0, 50 - title.length())));
    }
}



