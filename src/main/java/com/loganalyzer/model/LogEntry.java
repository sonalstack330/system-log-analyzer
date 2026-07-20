package com.loganalyzer.model;

import java.time.LocalDateTime;

public class LogEntry {

    private LocalDateTime timestamp;
    private String severity;
    private String logger;
    private String message;
    private String rawLine;

    private String rootCause;
    private String signature;

    public LogEntry() {
    }

    public LogEntry(LocalDateTime timestamp, String severity, String logger, String message, String rawLine) {
        this.timestamp = timestamp;
        this.severity = severity;
        this.logger = logger;
        this.message = message;
        this.rawLine = rawLine;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getLogger() {
        return logger;
    }

    public void setLogger(String logger) {
        this.logger = logger;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRawLine() {
        return rawLine;
    }

    public void setRawLine(String rawLine) {
        this.rawLine = rawLine;
    }

    public String getRootCause() {
        return rootCause;
    }

    public void setRootCause(String rootCause) {
        this.rootCause = rootCause;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

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