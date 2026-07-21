package com.loganalyzer.analyzer;

import com.loganalyzer.model.LogEntry;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RootCauseAnalyzer {
    // Stores a root cause and its matching regex
    private record Rule(String rootCause, Pattern pattern) {
    }
    // List of root cause detection rules
    private static final List<Rule> RULES = List.of(
            new Rule("DB_CONNECTION_TIMEOUT", Pattern.compile(
                    "connect.*(timeout|timed out)|timeout.*connect", Pattern.CASE_INSENSITIVE)),
            new Rule("DB_CONNECTION_REFUSED", Pattern.compile(
                    "connection refused", Pattern.CASE_INSENSITIVE)),
            new Rule("NULL_POINTER", Pattern.compile(
                    "NullPointerException", Pattern.CASE_INSENSITIVE)),
            new Rule("OUT_OF_MEMORY", Pattern.compile(
                    "OutOfMemoryError|out of memory", Pattern.CASE_INSENSITIVE)),
            new Rule("SQL_SYNTAX_ERROR", Pattern.compile(
                    "SQLSyntaxErrorException|syntax error", Pattern.CASE_INSENSITIVE)),
            new Rule("DEADLOCK", Pattern.compile(
                    "deadlock", Pattern.CASE_INSENSITIVE)),
            new Rule("AUTH_FAILURE", Pattern.compile(
                    "authentication failed|unauthorized|401", Pattern.CASE_INSENSITIVE)),
            new Rule("FILE_NOT_FOUND", Pattern.compile(
                    "FileNotFoundException|no such file", Pattern.CASE_INSENSITIVE)),
            new Rule("HTTP_5XX", Pattern.compile(
                    "\\b5\\d{2}\\b.*(error|response)|internal server error", Pattern.CASE_INSENSITIVE)),
            new Rule("DISK_FULL", Pattern.compile(
                    "no space left on device|disk full", Pattern.CASE_INSENSITIVE)),
            new Rule("STACK_OVERFLOW", Pattern.compile(
                    "StackOverflowError", Pattern.CASE_INSENSITIVE)),
            new Rule("CLASS_CAST", Pattern.compile(
                    "ClassCastException", Pattern.CASE_INSENSITIVE))
    );

    // Default root cause if no rule matches
    private static final String UNKNOWN = "UNCLASSIFIED";

    // Analyze one log entry and tag it with a root cause
    public void analyze(LogEntry entry) {
        String message = entry.getMessage();
        String rootCause = UNKNOWN;

        //Check each rule
        for (Rule rule : RULES) {
            Matcher m = rule.pattern().matcher(message);

            if (m.find()) {
                rootCause = rule.rootCause();
                break;
            }
        }
        //Save analysis result
        entry.setRootCause(rootCause);
        entry.setSignature(buildSignature(entry, rootCause));
    }

    // Analyze all log entries
    public void analyzeAll(List<LogEntry> entries) {
        for (LogEntry entry : entries) {
            analyze(entry);
        }
    }

    // Count entries by root cause
    public Map<String, Long> summarizeByRootCause(List<LogEntry> entries) {
        Map<String, Long> counts = new LinkedHashMap<>();

        for (LogEntry entry : entries) {
            counts.merge(entry.getRootCause(), 1L, Long::sum);
        }
        return counts;
    }
    // Builds a unique signature for grouping
    private String buildSignature(LogEntry entry, String rootCause) {
        return entry.getLogger() + "::" + rootCause;
    }
}