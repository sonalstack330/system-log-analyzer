package com.loganalyser.model;

import java.time.LocalDateTime;

/**
 * Represents a single parsed line from an application log file.
 */

public  class LogEntry{

    private LocalDateTime timestamp;
    private String severity;
    private String logger;     // originating class/module
    private String message;    // free-text log message
    private String rawLine;    // original unparsed line
    private String rootCause;  // Filled later after analyzing the log to identify the issue cause
    private String signature;  // Used to group similar log errors together

    public LogEntry() {}

    public LogEntry(LocalDateTime timestamp, String severity, String logger, String message, String rawLine){
        this.timestamp = timestamp;
        this.severity = severity;
        this.logger = logger;
        this.message = message;
        this.rawLine = rawline;
    }

    // Returns timestamp value
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // Updates timestamp value
    public void setTimestamp(LocalDateTime timestamp)
    {
        this.timestamp = timestamp;
    }

    // Returns severity level
    public String getSeverity() {
        return severity;
    }

    // Updates severity level
    public void setSeverity(String severity) {
        this.severity = severity;
    }

    // Returns logger name
    public String getLogger() {
        return logger;
    }

    // Updates logger name
    public void setLogger(String logger) {
        this.logger = logger;
    }

    // Returns log message
    public String getMessage() {
        return message;
    }

    // Updates log message
    public void setMessage(String message) {
        this.message = message;
    }

    // Returns original log line
    public String getRawLine() {
        return rawLine;
    }

    // Updates original log line
    public void setRawLine(String rawLine) {
        this.rawLine = rawLine;
    }

    // Returns identified root cause
    public String getRootCause() {
        return rootCause;
    }

    // Sets identified root cause
    public void setRootCause(String rootCause) {
        this.rootCause = rootCause;
    }

    // Returns error grouping signature
    public String getSignature() {
        return signature;
    }

    // Provides readable representation of LogEntry object
    @Override
    public String toString() {

        return "LogEntry{" +
                "timestamp=" + timestamp +
                ", severity='" + severity + '\'' +
                ", logger='" + logger + '\'' +
                ", message='" + message + '\'' +
                ", rootCause='" + rootCause + '\'' +
                '}';
    }
}
