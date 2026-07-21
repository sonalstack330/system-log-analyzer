# System Log Analyzer

An automated log analysis tool that parses application log files, classifies
errors by root cause, stores the results in a SQL database, and generates a
summary report (severity breakdown, top recurring error patterns, most
error-prone components, and daily error trends).

**Technologies:** Java 21, SQL (SQLite by default — swappable for MySQL)

## Project Structure

log-analyzer/
├── lib/
│ └── sqlite-jdbc.jar # SQLite JDBC driver (embedded DB, no server needed)
├── sample-logs/
│ └── app.log # Sample log file for testing
├── src/main/java/com/loganalyzer/
│ ├── Main.java # Entry point — wires everything together
│ ├── model/LogEntry.java # Data model for one parsed log line
│ ├── parser/LogParser.java # Regex-based parser (handles stack traces)
│ ├── analyzer/RootCauseAnalyzer.java # Rule-based root-cause classification
│ ├── db/DatabaseManager.java # DB connection + schema setup
│ ├── db/LogRepository.java # SQL insert/query logic
│ └── report/ReportGenerator.java # Formats results into a console report
└── README.md


## How it works

1. **Parse** — `LogParser` reads a log file line by line using a regex that
   matches the common `TIMESTAMP LEVEL logger - message` format. Continuation
   lines (like stack trace frames) are folded into the preceding entry so the
   analyzer sees the full context.
2. **Analyze** — `RootCauseAnalyzer` runs each entry's message through an
   ordered list of regex rules (DB timeouts, NullPointerException, deadlocks,
   OOM, auth failures, etc.) and tags it with a root cause + a normalized
   "signature" (logger + root cause) for grouping.
3. **Store** — `DatabaseManager` creates the `log_entries` table (with indexes
   on severity, root_cause, and timestamp) and `LogRepository` batch-inserts
   all parsed/analyzed entries via JDBC.
4. **Report** — `ReportGenerator` runs aggregate SQL queries (`GROUP BY`,
   `COUNT`, date trend) and prints a formatted summary.

## How to Build

```powershell
javac -d target\classes -cp lib\sqlite-jdbc.jar (Get-ChildItem -Recurse -Filter *.java -Path src | ForEach-Object { $_.FullName })
```

## How to Run

```powershell
java -cp "target\classes;lib\sqlite-jdbc.jar" com.loganalyzer.Main <path-to-log-file> [db-file]
```

Example, using the included sample log:

```powershell
java -cp "target\classes;lib\sqlite-jdbc.jar" com.loganalyzer.Main sample-logs\app.log logs.db
```
If `db-file` is omitted, it defaults to `log_analysis.db`.

## Verified Run

This tool has been built and tested end-to-end: compiled cleanly with
`javac`, executed against the included sample log (`sample-logs/app.log`),
and confirmed to correctly parse, classify, store, and report on all 28
log entries.

![Sample run output 2](screenshot2.png)

![Sample run output 1](screenshot1.png)

*(Screenshot shows the tool being run from the command line against the
sample log file, producing the full analysis report below.)*
