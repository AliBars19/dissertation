# analysis/

The analysis pipeline, written by me in Python with pandas and matplotlib (D-028). It reads the
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
3. **Load.** The predictions, joined to their lookup tables, in one DataFrame, and the requests in a
   second, both cut to the window, with times turned into UTC datetimes.
   *Check:* the number of prediction rows equals `SUM(record_count)` from an SQL query over the same
   window.
4. **Logger health (criterion 5).** Failed requests as a share of all requests, and the longest gap
   between successful requests.
   *Check:* the same numbers as an SQL query.
5. **TfL update rate (criterion 4) and repeats (D-026).** Distinct TfL timestamps per hour; responses
   that repeat or go back to an older TfL timestamp are dropped.
   *Check:* the share of repeated responses is close to the 10-minute test (7 of 21).
6. **Position text.** Every distinct form of `currentLocation` listed ("At X", "Approaching X",
   "Between X and Y" and any others), then parsed into a state and station(s).
   *Check:* at least 99% of rows parse; the rest are listed.
7. **Train trails.** One row per train per TfL timestamp, where a train is `lineId` plus `vehicleId`
   (D-022; `vehicleId` is only unique within a line).
   *Check:* one train's trail over an hour reads as a journey along the line.
8. **Inferred arrivals**, by the rule in `docs/pilot/README.md`.
   *Check:* 10 inferred arrivals checked by hand against their trails.
9. **Criteria 1 and 2.** Share of expected stops with an inferred arrival (the Victoria line's 16
   stations in order), and the median bracket between the two observations.
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
(H1: week 1 builds the table, week 2 tests it). Planned in detail once the pilot is decided.
