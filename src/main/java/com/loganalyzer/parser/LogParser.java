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

public class LogParser {

    private static final Pattern LOG_LINE_PATTERN = Pattern.compile(
            "^(\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}(?:\\.\\d{3})?)\\s+" +
                    "(TRACE|DEBUG|INFO|WARN|ERROR|FATAL)\\s+" +
                    "([\\w.$]+)\\s*-\\s*(.*)$"
    );

    private static final DateTimeFormatter[] TIMESTAMP_FORMATS = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    };

    public List<LogEntry> parseFile(Path logFilePath) throws IOException {
        List<String> lines = Files.readAllLines(logFilePath);
        return parseLines(lines);
    }

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
                String logger = matcher.group(3);
                String message = matcher.group(4);

                current = new LogEntry(timestamp, severity, logger, message, line);
                entries.add(current);
            } else if (current != null) {
                current.setMessage(current.getMessage() + "\n" + line);
                current.setRawLine(current.getRawLine() + "\n" + line);
            }
        }

        return entries;
    }

    private LocalDateTime parseTimestamp(String raw) {
        for (DateTimeFormatter fmt : TIMESTAMP_FORMATS) {
            try {
                return LocalDateTime.parse(raw, fmt);
            } catch (DateTimeParseException ignored) {
                // try next format
            }
        }
        return LocalDateTime.now();
    }
}