package com.loganalyzer;

import com.loganalyzer.analyzer.RootCauseAnalyzer;
import com.loganalyzer.db.DatabaseManager;
import com.loganalyzer.db.LogRepository;
import com.loganalyzer.model.LogEntry;
import com.loganalyzer.parser.LogParser;
import com.loganalyzer.report.ReportGenerator;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        // Validate command-line arguments
        if (args.length < 1) {
            System.err.println("Usage: java Main <path-to-log-file> [db-file]");
            System.exit(1);
        }

        // Read log file and database path
        Path logFile = Path.of(args[0]);
        String dbFile = args.length > 1 ? args[1] : "log_analysis.db";

        try {
            // Parse log file into LogEntry objects
            System.out.println("Parsing log file: " + logFile);
            LogParser parser = new LogParser();
            List<LogEntry> entries = parser.parseFile(logFile);
            System.out.println("Parsed " + entries.size() + " log entries.");

            // Identify root causes for log entries
            System.out.println("Running root cause analysis...");
            RootCauseAnalyzer analyzer = new RootCauseAnalyzer();
            analyzer.analyzeAll(entries);

            // Create database and tables
            System.out.println("Initializing database: " + dbFile);
            DatabaseManager dbManager = new DatabaseManager(dbFile);
            dbManager.initSchema();

            // Store parsed logs in the database
            System.out.println("Saving entries to database...");
            LogRepository repository = new LogRepository(dbManager);
            repository.saveAll(entries);

            // Generate and display the report
            System.out.println("Generating report...");
            ReportGenerator reportGenerator = new ReportGenerator(repository);
            reportGenerator.printFullReport();

        } catch (IOException e) {
            // Handle file reading errors
            System.err.println("Failed to read log file: " + e.getMessage());
            System.exit(1);

        } catch (SQLException e) {
            // Handle database errors
            System.err.println("Database error: " + e.getMessage());
            System.exit(1);
        }
    }
}