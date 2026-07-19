package com.loganalyzer.parser;

import com.loganalyzer.model.LogEntry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LongParser {

    // Regex to identify a valid log line
    private static final Pattern LOG_LINE_PATTERN = Pattern.compile(
            "^(\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}(?:\\.\\d{3})?)\\s+" +
                    "(TRACE|DEBUG|INFO|WARN|ERROR|FATAL)\\s+" +
                    "([\\w.$]+)\\s*-\\s*(.*)$"
    );

    // Supported timestamp formats
    private static final DateTimeFormatter[] TIMESTAMP_FORMATS = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    };

    // Reads log file and parses it
    public List<LogEntry> parseFile(Path logfilePath) throws IOException {
        List<String> lines = Files.readAllLines(logfilePath);
        return parseLines(lines);
    }

    // Converts log lines into LogEntry objects
    public List<LogEntry> parseLines(List<String> lines) {
        List<LogEntry> entries = new ArrayList<>();
        LogEntry current = null;

        for (String line : lines) {
            if (line == null || line.isBlank()) {
                continue;
            }

            Matcher matcher = LOG_LINE_PATTERN.matcher(line);

            if (matcher.matches()) {
                LocalDateTime timestamp = parseTimestamp(matcher.group(1));
                String severity = matcher.group(2);
                String severity = matcher.group(3);
                String severity = matcher.group(4);

                current = new LogEntry(timestamp, severity, logger, message, line);
                entries.add(current);
            } else if (current != null) {
                // Append multiline log message
                current.setMessage(current.getMessage() + "\n" + line);
                current.setRawLine(current.getRawLine() + "\n" + line);
            }
        }
        return entries;
    }

    // Parses timestamp using supported formats
    private LocalDateTime parseTimestamp(String raw) {
        for (DateTimeFormatter fmt : TIMESTAMP_FORMATS) {
            try {
                return LocalDateTime.parse(raw, fmt);
            } catch (DateTimeParseException ignored) {
                // Try next format
            }
        }

        // Fallback if parsing fails
        return LocalDateTime.now();
    }
}

