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
- [ ] Write the logger by hand (D-007).
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
- **Libraries:** Jackson, xerial `sqlite-jdbc`, Gradle and JUnit 5 (L-18 to L-21).

## Questions the pilot answers
- How often can a train be followed from "Approaching X" to "At X" (R-01)?
- How often does TfL's data actually change? This sets the request interval (R-04, D-022).
- Do the patterns seen in one response on 2026-11-06 hold, for example `timeToLive` always equal to
  `expectedArrival` (D-022)?
- How much data a day does it really produce?
