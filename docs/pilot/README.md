# Pilot: one line, both feeds, 1 to 2 weeks

**Goal:** find out how cleanly Unified API predictions join to TrackerNet train positions on
one line, before committing to a full-network study.

## Checklist
- [ ] Choose the line, with reasons recorded as a decision (`line-choice.md`, then D-NNN).
- [ ] Define "join success" and the join-rate threshold that means "go" for the main study.
- [ ] Design the logger data schema on paper, including gap and outage logging
      (`schema.md`). Plan before code.
- [ ] Get a TrackerNet API key; decide where secrets live
- [ ] Decide where raw data is stored and whether any of it is committed.
- [ ] Write the logger by hand (D-007).
- [ ] Run the pilot; record start/end dates and logger uptime.
- [ ] Hand-time a small stopwatch sample on the pilot line (protocol written first).
- [ ] Write up pilot results and the go / no-go decision.
