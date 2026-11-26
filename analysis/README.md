# analysis/

The analysis pipeline written in Python with pandas and matplotlib (D-028). It reads the
logger's SQLite database, follows each train through its `currentLocation` text, infers when it
arrived at each station, and produces the numbers and charts for the pilot's go/no-go decision
(`docs/pilot/README.md`) and, later, the main study.

## Data windows
The pipeline only ever reads the rows inside one time window, set in one place at the top of the
code, so the development/test split cannot be broken by accident.

| Window | From (UTC) | To (UTC) | Use |
|---|---|---|---|
| Development | 2026-11-17 14:45 | 2026-11-18 23:59 | Building and tuning every step |
| Pilot | 2026-11-19 00:00 | 2026-12-02 23:59 | Analysed once, when the pilot ends (D-027) |
| Main collection | 2026-12-07 00:00 | 2026-12-20 23:59 | Main study, all 11 lines (D-020) |

The copy of the database used for development (`data/vps-2026-11-19.db`) also holds about 15 hours
of pilot data and one setup request from 13:43 on 17 November. Both are outside the development
window and are cut off by the loader.

## How it is built
Plain `.py` files run by one script, with the window as a setting, rather than notebooks: running
once on the pilot is then one changed setting, changes show clearly in git, and there is no hidden
state from cells run out of order. A notebook is only used for looking around, never as the
pipeline.

## Build plan
Each step ends with a check that must pass before I start the next one.

### A. Setup
1. **Environment.** (DONE 2026-11-19) A virtual environment in `analysis/.venv` (git-ignored) with
   pandas and matplotlib; exact versions pinned in `requirements.txt`. Python 3.14.10, pandas 3.1.1,
   matplotlib 3.11.2 (L-22 to L-24).
   *Check:* the import prints those versions.
2. **Development copy.** (DONE 2026-11-19) A snapshot of the VPS database taken with SQLite's
   `.backup` (safe while the logger is writing), copied to `data/vps-2026-11-19.db`.
   *Check:* `PRAGMA integrity_check` returns `ok`; 5,764 requests from 2026-11-17 13:43 to
   2026-11-19 15:02 UTC and 1,062,459 predictions. Inside the development window: 3,990 requests.

### B. Build (development data only)
3. **Load.** (FINISHED) The predictions, joined to their lookup tables, in one DataFrame, and the requests in a
   second, both cut to the window, with times turned into UTC datetimes.
   *Check:* the number of prediction rows equals `SUM(record_count)` from an SQL query over the same
   window.
   *Result:* (DONE 2026-11-20) 3,990 requests; `received_at` missing for the 8 failures,
   `tfl_newest_timestamp` missing for 554 (the 8 failures and 546 empty responses while the line was
   closed). 787,251 predictions, equal to `SUM(record_count)`; 16 stations; `destination_name`
   missing for 52 predictions (kept by the `LEFT JOIN`). All times are UTC datetimes cut to the
   millisecond. For every prediction, `expected_arrival` minus `tfl_newest_timestamp` is within one
   second of `time_to_station` (TfL cuts `expected_arrival` to whole seconds), which confirms D-022
   on the whole development window.
4. **Logger health (criterion 5) and line closures.** Failed requests as a share of all requests,
   and the longest gap between successful requests. A successful request with no predictions for a
   line (TfL returns `[]`) is not a failure and not a gap: it means that line was closed. Closed
   periods are found from the data, not from a timetable, because Night Tube nights and engineering
   works differ from the normal hours. For the main collection, one request covers all 11 lines, so a
   closure is found per line from the predictions, not from `record_count`. In the main collection,
   each empty run is labelled against that day's timetable: outside timetabled hours is a scheduled
   closure, inside is flagged and looked into (D-029). No timetables are saved for the pilot, so its
   empty runs are listed without labels.
   *Check:* the same numbers as an SQL query. The development window has two runs of empty
   responses on 2026-11-18: 7 from 00:34:37 to 00:37:37 and 539 from 00:43:37 to 05:12:37 UTC.
   *Progress:* 4.1 (failure rate) and 4.2 (longest gap) done 2026-11-20: 8 of 3,990 requests failed
   (0.20%; 6 `HttpTimeoutException`, 2 `IOException`); the longest gap between successful requests
   is 60.001 s, ending at 2026-11-18 17:23:07 UTC. All 8 gaps of about 60 s are single failed
   requests. Criterion 5 passes on the development data. 4.3 (`empty_runs`) done 2026-11-23: two runs
   of empty responses, requests 1181 to 1187 (7) and 1199 to 1737 (539), matching the SQL check.
   Step 4 DONE.
5. **TfL update rate (criterion 4) and repeats (D-026).** Distinct TfL timestamps per hour, counted
   only while the line is open; responses that repeat or go back to an older TfL timestamp are
   dropped.
   *Check:* the share of repeated responses is close to the 10-minute test (7 of 21). STEP 5 DONE
6. **Position text.** Every distinct form of `currentLocation` listed ("At X", "Approaching X",
   "Between X and Y" and any others), then parsed into a state (AT, DEPARTED, BETWEEN, ARRIVING) and
   station (D-030). Depots, sidings and `0` are counted but not parsed. Station text is matched to
   the `stations` table by a rule (same cleaning on both sides, prefix match for text TfL cut off at
   50 characters, only stations on the train's own line), with a small exceptions file only for what
   the rule cannot match.
   *Check:* at least 99% of the rows that are not depots or `0` get a state and a station; the rest
   are listed.
   *Progress:* 6.1 done 2026-11-24: 82 distinct texts in the 480,711 fresh predictions, saved to
   `analysis/output/locations.csv`. 6.3 (`parse_location`, `add_states`) done 2026-11-24: BETWEEN 210,137,
   AT 194,160, ARRIVING 46,324, DEPARTED 26,590, depots and `0` 3,500, unparsed 0. 19 distinct station
   texts: the 16 stations, two cut off at 50 characters (`Kings Cross St. P`, `Highbury & Isl`) and Step 6 DONE.
7. **Train trails.** One row per train per TfL timestamp, where a train is `lineId` plus `vehicleId`
   (D-022; `vehicleId` is only unique within a line).
   *Check:* one train's trail over an hour reads as a journey along the line.
   *Progress:* 7.1 (`build_trails`, one position per train per update by majority vote, D-031) and
   7.2 (direction from TfL's `direction` field, voted and carried forward) done 2026-11-26: 57,211
   train-updates, 5,696 disagreeing (10%), 44 trains; direction `inbound` 29,551, `outbound` 27,655,
   none 5. The platform name was rejected for direction (wrong at the termini).
   7.3 (`progress`, D-031) and 7.5 (`drop_backwards`) done 2026-11-26: progress runs from 1 to 63; a
   trip ends when the direction changes or the train is unseen for more than 10 minutes, giving 1,483
   trips; 199 updates (0.35%) went backwards and were dropped, leaving 57,012. Train 210's trail from
   20:28 to 20:46 on 2026-11-17 keeps all 20 updates and its progress never goes down. Ties (7.4) are
   left to the plain vote until after the main collection (D-031). Step 7 DONE.
8. **Inferred arrivals**, by the rule in `docs/pilot/README.md` (revised 2026-11-26, D-033): the first
   update in a trip whose progress reaches a station's AT value, bracketed by the update before it,
   with the midpoint as the arrival time and a `seen_at` flag.
   *Check:* 10 inferred arrivals checked by hand against their trails.
   *Progress:* `infer_arrivals` done 2026-11-26: 19,981 arrivals, 14,455 with "At S" seen (72.3%),
   median bracket 60.0 s; train 210's Green Park (28 s, seen) and Vauxhall (60 s, missed-AT) arrivals
   match the worked example. Hand check (10 arrivals drawn with `sample(10, random_state=1)`, checked
   against their trails on 2026-11-26): the station, the bracket, the midpoint and `seen_at` are right
   in all 10 (7 seen, 3 missed-AT). One is physically doubtful: train 267 on 2026-11-18 is "At Pimlico"
   at 19:28:14 and "At Vauxhall" at 19:28:40, 26 s later, although the two stations are about a
   minute apart, so the "At Pimlico" was probably a late position from TfL and the bracket is too
   narrow. The stopwatch sample (criterion 3) is what tests whether TfL's positions run late.
   Step 8 DONE.
9. **Criteria 1 and 2.** Share of expected stops with an inferred arrival whose bracket is 90 s or
   less (D-033), and the median bracket. Expected stops include the station where each train turns
   round, termini included, with that arrival bracketed across the reversal (D-034). Brackets are rounded to
   whole seconds for both criteria (D-035).
   *Progress:* `terminus_arrivals` and `criteria_1_and_2` written 2026-11-26: 19,981 arrivals inside
   trips plus 1,222 turn-round arrivals gives 21,203 expected stops (4 false turn-rounds from trips
   with no direction were found and removed). Hand count, train 210's trip from Walthamstow Central at
   20:15 to Brixton at 20:46 on 2026-11-17: 15 expected stops, brackets from 28.6 to 90.3 s, all 15
   within 90 s in whole seconds (12 with exact times). Exact times give criterion 1 85.3% and criterion
   2 60.011 s; whole seconds give 95.0% and 60 s.
   Step 9 DONE (2026-11-26): `criteria_1_and_2` gives 21,203 expected stops, criterion 1 95.0%
   (pass), criterion 2 60 s (pass), on the development data.
   *Check:* a hand count for one trip matches.
10. **Results table and charts**, written to `analysis/output/`.
    *Check:* running it twice gives identical numbers.
11. **Stopwatch matching (criterion 3).** My timings read from a CSV and matched to inferred arrivals
    at the same station and platform.
    *Check:* every timing is matched or listed as unmatched.

### C. Pilot
12. **One run on the pilot window** after 2 December, then the go/no-go write-up in `docs/pilot/`.

### D. Main study (after the main collection)
Prediction error against inferred arrivals, prediction stability, and the lookup-table correction
(H1: week 1 builds the table, week 2 tests it). The published timetable is a third baseline (SQ5,
D-029): at each moment, the timetable's next train, TfL's prediction and my correction are each
compared with the inferred arrival, and the timetable's error is reported by time of day. Each
station gets its own timetabled last time; observations after it are labelled "after timetable" and
reported separately, not removed. Planned in detail once the pilot is decided.
