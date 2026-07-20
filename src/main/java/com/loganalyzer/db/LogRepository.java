package com.loganalyzer.db;

import com.loganalyzer.model.LogEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LogRepository {

    private static final DateTimeFormatter TS_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private final DatabaseManager dbManager;

    public LogRepository(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void saveAll(List<LogEntry> entries) throws SQLException {
        String sql = """
            INSERT INTO log_entries (timestamp, severity, logger, message, root_cause, signature, raw_line)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = dbManager.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (LogEntry entry : entries) {
                    ps.setString(1, entry.getTimestamp().format(TS_FORMAT));
                    ps.setString(2, entry.getSeverity());
                    ps.setString(3, entry.getLogger());
                    ps.setString(4, entry.getMessage());
                    ps.setString(5, entry.getRootCause());
                    ps.setString(6, entry.getSignature());
                    ps.setString(7, entry.getRawLine());
                    ps.addBatch();
                }
                ps.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public Map<String, Long> countBySeverity() throws SQLException {
        String sql = "SELECT severity, COUNT(*) AS cnt FROM log_entries GROUP BY severity ORDER BY cnt DESC";
        Map<String, Long> results = new LinkedHashMap<>();

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                results.put(rs.getString("severity"), rs.getLong("cnt"));
            }
        }
        return results;
    }

    public List<RootCauseCount> topRootCauses(int limit) throws SQLException {
        String sql = """
            SELECT root_cause, COUNT(*) AS cnt
            FROM log_entries
            WHERE root_cause IS NOT NULL AND root_cause != 'UNCLASSIFIED'
            GROUP BY root_cause
            ORDER BY cnt DESC
            LIMIT ?
            """;

        List<RootCauseCount> results = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(new RootCauseCount(rs.getString("root_cause"), rs.getLong("cnt")));
                }
            }
        }
        return results;
    }

    public Map<String, Long> errorTrendByDay() throws SQLException {
        String sql = """
            SELECT substr(timestamp, 1, 10) AS day, COUNT(*) AS cnt
            FROM log_entries
            WHERE severity IN ('ERROR', 'FATAL')
            GROUP BY day
            ORDER BY day ASC
            """;

        Map<String, Long> results = new LinkedHashMap<>();
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                results.put(rs.getString("day"), rs.getLong("cnt"));
            }
        }
        return results;
    }

    public List<LoggerErrorCount> topErrorProneLoggers(int limit) throws SQLException {
        String sql = """
            SELECT logger, COUNT(*) AS cnt
            FROM log_entries
            WHERE severity IN ('ERROR', 'FATAL')
            GROUP BY logger
            ORDER BY cnt DESC
            LIMIT ?
            """;

        List<LoggerErrorCount> results = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(new LoggerErrorCount(rs.getString("logger"), rs.getLong("cnt")));
                }
            }
        }
        return results;
    }
    public record RootCauseCount(String rootCause, long count) {
    }
    public record LoggerErrorCount(String logger, long count) {
    }
}