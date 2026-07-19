package com.loganalyzer.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    // SQLite database URL
    private final String jdbcUrl;

    // Initializes database path and loads JDBC driver
    public DatabaseManager(String dbFilePath) {
        this.jdbcUrl = "jdbc:sqlite:" + dbFilePath;

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC driver not found on classpath", e);
        }
    }

    // Returns a database connection
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl);
    }

    // Creates table and indexes if they do not exist
    public void initSchema() throws SQLException {

        String createTable = """
            CREATE TABLE IF NOT EXISTS log_entries (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                timestamp TEXT NOT NULL,
                severity TEXT NOT NULL,
                logger TEXT NOT NULL,
                message TEXT NOT NULL,
                root_cause TEXT,
                signature TEXT,
                raw_line TEXT
            )
            """;

        // Index for severity column
        String createIndexSeverity =
                "CREATE INDEX IF NOT EXISTS idx_severity ON log_entries(severity)";

        // Index for root cause column
        String createIndexRootCause =
                "CREATE INDEX IF NOT EXISTS idx_root_cause ON log_entries(root_cause)";

        // Index for timestamp column
        String createIndexTimestamp =
                "CREATE INDEX IF NOT EXISTS idx_timestamp ON log_entries(timestamp)";

        // Execute SQL statements
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createTable);
            stmt.execute(createIndexSeverity);
            stmt.execute(createIndexRootCause);
            stmt.execute(createIndexTimestamp);
        }
    }
}