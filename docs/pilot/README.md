# Pilot: Victoria line, Unified API only, 1 to 2 weeks

**Goal:** find out how reliably actual arrivals can be inferred from the Unified API's position text
(`currentLocation`, D-014) on one line, before committing to a full-network study.

**Dates in my plan:** 7 to 20 December 2026 (Gantt chart, PDD Appendix B).

## Checklist
- [x] Choose the line, with reasons recorded as a decision: Victoria (D-023).
- [ ] Define what counts as a usable inferred arrival, and the rate that means "go" for the main
      study. Include how well `vehicleId` tracks one train over time.
- [x] Design the logger data schema on paper, including gap and outage logging: `logger/schema.sql`
      and D-022.
- [x] Get a Unified API key; decide where secrets live: registered 2026-11-06 (L-03), kept in a
      git-ignored `.env` locally and on the VPS.
- [x] Decide where raw data is stored and whether any of it is committed: hourly gzipped files on the
      VPS, moved off once at the end; the repo gets a sample and derived tables (D-020, D-022).
- [x] Write the logger by hand (D-007): built 2026-11-06 to 2026-11-17, running on the VPS
      (`logger/README.md`).
- [ ] Run the pilot; record start/end dates and logger uptime.
- [ ] Hand-time a small stopwatch sample on the pilot line (protocol written first).
- [ ] Write up pilot results and the go / no-go decision.

## Settled so far
- **Where it runs:** my OVHcloud VPS (D-019).
- **Storage:** SQLite database with seven tables (`logger/schema.sql`, D-022), plus one gzipped JSON
  Lines file of raw responses per UTC hour, named like `raw/2026-12-07/2026-12-07T08Z.jsonl.gz`.
- **Times:** all stored in UTC, ISO 8601 with a `Z`.
- **Gaps:** every request has a 10-second time limit and always gets a `tflrequests` row, even when
  it fails. Which failures count as gaps, and when data counts as stale, is decided in the analysis.
- **Libraries:** Jackson, xerial `sqlite-jdbc`, Gradle and JUnit 6 (L-18 to L-21).

## Questions the pilot answers
- How often can a train be followed from "Approaching X" to "At X" (R-01)?
- How often does TfL's data actually change? This sets the request interval (R-04, D-022).
- Do the patterns seen in one response on 2026-11-06 hold, for example `timeToLive` always equal to
  `expectedArrival` (D-022)?
- How much data a day does it really produce?

## Early findings (before the pilot)
- **Stale and repeated data (10-minute test, 2026-11-10):** 7 of 21 responses repeated an earlier
  TfL timestamp, once a later request returned older data, and TfL's data was a median of 49 seconds
  old on arrival. The analysis orders by TfL's timestamp and drops repeats (D-026).
- **48-hour run (2026-11-17 to 2026-11-19, Victoria line):** 5,474 requests, 13 failed (0.24%), one
  5.4-minute gap during a VPS reboot, 993,441 predictions. About 47 MB a day in total, about 58
  bytes per stored prediction; storing every prediction fits for the main collection (D-022).
- **Missing directions:** in my local test files (Victoria line), 5,089 of the 5,091 predictions with
  no `direction` were for the two terminus stations, Walthamstow Central (2,734) and Brixton (2,355),
  and in all 5,089 the platform and `towards` disagree (for example a Northbound platform with
  `towards` Brixton). They look like trains arriving at the end of the line, about to reverse. This
  matters for following a train across a reversal (R-01), and the pilot checks it on the VPS data.
