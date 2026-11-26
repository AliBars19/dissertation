# Pilot: Victoria line, Unified API only, 19 November to 2 December 2026

**Goal:** find out how reliably actual arrivals can be inferred from the Unified API's position text
(`currentLocation`, D-014) on one line, before committing to a full-network study.

**Pilot window:** 2026-11-19 00:00 UTC to 2026-12-02 23:59 UTC, two full weeks (D-027). This replaces
7 to 20 December in my PDD Gantt chart. Development data, used to build and tune the analysis, is
the 48-hour run before it (2026-11-17 14:45 to 2026-11-18 23:59 UTC); the pilot data is analysed once,
when the pilot ends.

## Checklist
- [x] Choose the line, with reasons recorded as a decision: Victoria (D-023).
- [x] Define what counts as a usable inferred arrival, and the rate that means "go" for the main
      study, including how well `vehicleId` tracks one train over time: see "Go/no-go criteria" below
      (2026-11-19).
- [x] Design the logger data schema on paper, including gap and outage logging: `logger/schema.sql`
      and D-022.
- [x] Get a Unified API key; decide where secrets live: registered 2026-11-06 (L-03), kept in a
      git-ignored `.env` locally and on the VPS.
- [x] Decide where raw data is stored and whether any of it is committed: hourly gzipped files on the
      VPS, moved off once at the end; the repo gets a sample and derived tables (D-020, D-022).
- [x] Write the logger by hand (D-007): built 2026-11-06 to 2026-11-17, running on the VPS
      (`logger/README.md`).
- [ ] Run the pilot; record start/end dates and logger uptime.
- [ ] Hand-time a small stopwatch sample on the pilot line, inside the pilot window (protocol below).
- [ ] Write up pilot results and the go / no-go decision.

## Go/no-go criteria (decided 2026-11-19, before any pilot data was analysed)
**Definitions**
- **A train** is one `lineId` plus `vehicleId` (D-022).
- **An inferred arrival** of a train at station S is the first observation with `currentLocation`
  "At S" after an observation of the same train "Approaching S" or "Between X and S". Its time is
  the midpoint of those two observations' TfL timestamps, and its precision is the time between them.
  Observations are ordered by TfL timestamp, and exact repeats are dropped (D-026).
- **An expected stop** is each station a train should pass between two consecutive termini on its
  route.

| # | Question | Measure | Pass mark |
|---|---|---|---|
| 1 | Can a train be followed? | Share of expected stops that have an inferred arrival with a bracket of 90 s or less (revised 2026-11-26, see below) | at least 80% |
| 2 | How precise is an inferred arrival? | Median time between the two bracketing observations | 60 s or less |
| 3 | Do inferred arrivals match reality? | Median absolute difference from the stopwatch sample | 30 s or less, over at least 20 arrivals |
| 4 | How often does TfL's data change? | Distinct TfL timestamps per hour | measured only; sets the request interval |
| 5 | Is the logger reliable? | Failed requests; longest gap | under 1%, and no gap over 10 minutes |

**Clarification (2026-11-20, from the development data, before any pilot data was analysed):** a
successful request that returns no predictions (`[]`) means the line was closed. It is not a failed
request and not a gap under criterion 5, and closed periods are left out of criterion 4. Closed
periods are found from the data, not from a timetable.

**Revision (2026-11-26, from the development data, before any pilot data was analysed, D-033):**
- **An inferred arrival** at station S is now found from the train's progress score (D-031): the
  first update in a trip whose progress reaches S's AT value. The bracket is that update and the one
  before it, and the arrival time is their midpoint. This includes stops where no update ever said
  "At S" because the train arrived and left between two updates ("missed-AT arrivals"); each arrival
  is flagged `seen_at` so both kinds can be reported. The original wording counted only arrivals where
  "At S" was seen.
- **Criterion 1** now asks for an inferred arrival with a bracket of 90 s or less, at least 80% of
  expected stops.
- **Why:** on the development data (30-second polling) about 1 in 4 arrivals at every station are
  missed-AT arrivals (from 17% at Victoria to 56% at Finsbury Park), and their brackets are as narrow
  as the others (median 60.7 s against 59.9 s). Under the original wording criterion 1 was 72.3% and
  would fail on data that is fine. Under the new wording it is 86.6%. Counting every arrival would
  make the measure 100% by construction, so the 90-second limit keeps it a real test: knowing each
  arrival to within a minute and a half, which at 30-second polling is at most two missed updates in
  a row. 30 seconds was considered and rejected: at 30-second polling only 16% of brackets are that
  narrow, because a bracket cannot be shorter than the time between two updates of one train.
- **Brackets in whole seconds (D-035):** for criteria 1 and 2 each bracket is rounded to the nearest
  second. Brackets cluster around 30, 60, 90 and 120 s (one to four TfL updates) with a few hundred
  milliseconds of jitter, and both pass marks sit in the middle of a cluster, so exact times would make
  the result depend on clock noise (2,303 development-data brackets lie between 90.0 and 91.0 s, and
  the median is 60.011 s). TfL's predictions are in whole seconds. Noticed in the step 9 hand count,
  where one trip had brackets of 90.029, 90.057 and 90.337 s. The pass marks are unchanged.
- **Expected stops include the station where each train turns round, termini included (D-034).**
- **Not changed:** criteria 2 to 5 and their pass marks. The main study (10-second polling, D-032)
  sets its own, likely tighter, bracket limit for which arrivals to use, worked out on that data.

**Outcomes**
- **Go:** criteria 1, 2, 3 and 5 pass. The main collection covers all 11 lines (D-020).
- **Partial:** if a criterion fails only because of a fixable rule (for example the arrival rule),
  I change the rule on the development data, say so in the report, and re-run on the pilot once. If
  the method works on some lines and not others, the main collection and the app cover the lines
  that work (D-011).
- **No-go:** if criterion 3 fails, the arrival method is rethought before any main collection.

## Stopwatch protocol (draft)
I do not need to see the logger's data while timing. I record what I see, and match it afterwards.
1. **Clock:** a phone clock showing seconds, set to network time (accurate to under a second).
   Times are written in UTC (in winter, UK time is UTC).
2. **Where:** a Victoria line platform, for example Oxford Circus or Victoria, noting the station
   and platform (for example "Southbound - Platform 4").
3. **What to time:** the moment the train comes to a stop at the platform. Optionally also the moment
   its doors close.
4. **Record per train:** date, station, platform, arrival time to the second, and anything unusual
   (held at the platform, announcement, crowding).
5. **How many:** at least 20 arrivals, on at least two different days and times inside the pilot
   window.
6. **Matching:** each timed arrival is matched to the inferred arrival at the same station and
   platform closest in time (Victoria trains are at least about 100 seconds apart, so the match is
   unambiguous). Unmatched timings are reported, not dropped.

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
- **Overnight closure:** on the night of 17 to 18 November (a weeknight, so no Night Tube), TfL
  returned an empty list (`[]`, 2 bytes, HTTP 200) for 546 requests in two runs: 7 from 00:34:37 to
  00:37:37 UTC, then, after one late train was seen from 00:38:07 to 00:43:07, 539 from 00:43:37 to
  05:12:37 UTC (4 h 29 min). The first train of the morning was seen at 05:13:07. The Victoria line
  runs all night on Friday and Saturday nights, so the pilot should show this on weeknights only.
- **Separate 10-second test (2026-11-26, 20:36 to 21:36 UTC, Victoria line):** a one-off run outside
  the pilot logger, 360 requests every 10 s from my laptop with the secondary key, analysed with the
  pipeline's own functions and compared with every third request (what 30-second polling sees in the
  same hour). TfL's data changed as often as every 10 s (gaps between new timestamps of 10 s: 22,
  20 s: 23, 30 s: 19, 40 s: 10). Out-of-sync responses rose with faster polling: older data was 31% of
  responses at 10 s against 14% at 30 s.

  | Polling every | Arrivals | AT seen | Median bracket | Brackets of 90 s or less |
  |---|---|---|---|---|
  | 10 s | 351 | 82.6% | 40.3 s | 99.7% |
  | 30 s (same hour) | 344 | 69.8% | 60.2 s | 93.3% |

  One evening hour on one line, so it informs the polling rate for the main collection at go/no-go
  rather than settling it. Polling every 10 s triples requests, so storage needs planning first.
- **Missing directions:** in my local test files (Victoria line), 5,089 of the 5,091 predictions with
  no `direction` were for the two terminus stations, Walthamstow Central (2,734) and Brixton (2,355),
  and in all 5,089 the platform and `towards` disagree (for example a Northbound platform with
  `towards` Brixton). They look like trains arriving at the end of the line, about to reverse. This
  matters for following a train across a reversal (R-01), and the pilot checks it on the VPS data.
