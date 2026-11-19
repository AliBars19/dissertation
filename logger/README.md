# logger/

The data logger that polls the TfL Unified API that runs on my VPS

## Build plan
Each step ends with a check that must pass before I start the next one.

### A. Setup (DONE)
1. **API key.** Registered, primary and secondary keys in a git-ignored `.env` (L-03).
2. **VPS ready.** Java 17 installed, clock synced, enough free disk space. 
3. **Test fetch from the VPS.** One real response saved, to use as a test fixture. Done.

### B. Design (DONE)
4. **Database schema.** Seven tables in `schema.sql` (D-022). Done.
5. **Raw files.** One gzipped JSON Lines file per UTC hour, e.g.
   `raw/2026-12-07/2026-12-07T08Z.jsonl.gz` (D-022). Done.
6. **Gaps.** Every request has a 10-second time limit and always gets a `tflrequests` row (D-022).
   Done.
7. **Libraries.** Jackson, xerial `sqlite-jdbc`, Gradle, JUnit 6 (L-18 to L-21); Gradle project
   set up with `gradle init`, template extras removed. Done.

### C. Build 
8. **Fetch and save raw.**(DONE) Set my own package and main class; add Jackson and `sqlite-jdbc` to
   the build and record their versions in L-18 and L-19. The logger reads the key from the
   environment, requests the Victoria line every 30 seconds with a 10-second limit, and appends each
   response as one line (starting with its `request_id` and `sent_at`) to the current UTC hour's
   gzipped file, starting a new file when the hour changes.
   *Check:* after 10 minutes, `zcat` on the file shows about 20 lines.
9. **Parse.** (DONE) Turn each response into prediction records, keeping only the fields in D-022.
   *Check:* unit tests pass on saved responses, including one with empty `direction` and
   `destinationNaptanId`.
10. **Write to SQLite.** (done)Create the tables from `schema.sql`, switch on foreign keys for each
    connection, fill the lookup tables as new values appear, and write `tflrequests` and
    `tflresponses` rows.
    *Check:* row counts match the record counts, and every ID points to a real lookup row.
11. **Gap and health logging.** (done) Failed, timed-out, rate-limited and empty requests all get a
    `tflrequests` row with the error.
    *Check:* with the network turned off for a minute, those requests appear as failed rows.
12. **Survive failures.** (DONE) One bad response, a timeout or a database error is logged and the logger
    carries on with the next request.
    *Check:* feeding it broken JSON does not stop it.
13. **Run as a service.** (DONE 2026-11-17) Built on the VPS with `./gradlew installDist` and run as
    the systemd service `tfl-logger.service`, with its settings in `~/tfl-logger.env` (D-019).
    *Check:* after the VPS rebooted on 2026-11-19, the logger was running again without me starting it,
    and `systemctl stop` ran the shutdown hook ("Stopped cleanly").
14. **48-hour test run.** (DONE 2026-11-19)
    *Check:* no unexplained gaps, the clock stays right, and the measured size per day fits the VPS.
    *Result:* from 2026-11-17 14:45 to 2026-11-19 12:26 UTC (45 h 41 min): 5,474 requests, 13 failed
    (11 `HttpTimeoutException`, 2 `IOException`), 993,441 predictions saved. One gap of 5.4 minutes
    while the VPS rebooted; no crashes (`NRestarts=0`). Size: 36 MB of raw files and a 55 MB database,
    about 47 MB a day for the Victoria line, or about 58 bytes per prediction. Scaled to all 11 lines
    (about 15 times the records), that is about 700 MB a day and 8 to 12 GB for two weeks, which fits
    the VPS (D-022).

### D. Pilot 
15. **Go/no-go criteria**, written before the pilot starts: how often a train can be followed from
    "Approaching X" to "At X", how often TfL's data actually changes (which sets the request
    interval), and the real data size per day.
16. **Run the pilot** and write up the results and the go/no-go decision in `docs/pilot/`.

### E. Main collection
17. **Two full weeks of all 11 lines**, 7 to 20 December 2026 (D-020), at the interval the pilot
    showed, if the pilot passes.
18. **Stopwatch sample** taken inside those two weeks.
19. **Back up and publish:** raw data off the VPS, then to a data archive such as Zenodo; the repo
    gets a sample and the derived tables.