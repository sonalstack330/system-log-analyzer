package com.loganalyzer.db;

import com.loganalyzer.model.LogEntry;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.list;

//Handles database operations for log entries.

public class LogRepository {

    //Timestamp format for storing dates in the database
    private static final DateTimeFormatter TS_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    //Database manager object
    private final DatabaseManager dbManager;

    //Constructor
    public LogRepository(DatabaseManager dbManager) {
       this.dbManager = dbManager;
    }

    //Saves all log entries using batch insertion
    public void saveAll(List<LogEntry> entries) throws SQLException{
        String sql = """
           INSERT INTO log_entries (timestamp, severity, logger, message, root_cause, signature, raw_line)
           VALUES (?, ?, ?, ?, ?, ?, ?)
           """;
        try(Connection conn = dbManager.getConnection()){

            //Disable auto commit for batch processing
            conn.setAutoCommit(false);

            try(PreparedStatement ps = conn.preparedStatement(Sql)){

                // Add each log entry to the batch
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

                //Execute batch
                ps.executeBatch();

                //Commit changes
                conn.commit();
            } catch(SQL Exception e){

                //Rollback on failure
                conn.rollback();

                throw e;
            }
        }
    }
}