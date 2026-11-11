# logger/

The data logger that polls the TfL Unified API thall run on my VPS

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
10. **Write to SQLite.** Create the tables from `schema.sql`, switch on foreign keys for each
    connection, fill the lookup tables as new values appear, and write `tflrequests` and
    `tflresponses` rows.
    *Check:* row counts match the record counts, and every ID points to a real lookup row.
11. **Gap and health logging.** Failed, timed-out, rate-limited and empty requests all get a
    `tflrequests` row with the error.
    *Check:* with the network turned off for a minute, those requests appear as failed rows.
12. **Survive failures.** One bad response, a timeout or a database error is logged and the logger
    carries on with the next request.
    *Check:* feeding it broken JSON does not stop it.
13. **Run as a service.** Build with `./gradlew installDist`, copy the result to the VPS, and run it
    as a systemd service that restarts after a crash or reboot.
    *Check:* after rebooting the VPS, the logger is running again without me starting it.
14. **48-hour test run.**
    *Check:* no unexplained gaps, the clock stays right, and the measured size per day fits the VPS.

### D. Pilot 
15. **Go/no-go criteria**, written before the pilot starts: how often a train can be followed from
    "Approaching X" to "At X", how often TfL's data actually changes (which sets the request
    interval), and the real data size per day.
16. **Run the pilot** and write up the results and the go/no-go decision in `docs/pilot/`.

### E. Main collection
17. **Two full weeks of all 11 lines**, at the interval the pilot showed.
18. **Stopwatch sample** taken inside those two weeks.
19. **Back up and publish:** raw data off the VPS, then to a data archive such as Zenodo; the repo
    gets a sample and the derived tables.