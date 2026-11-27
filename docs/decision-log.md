# Decision log

Every project decision, with its reasoning. IDs are permanent. A changed decision is not
edited away: it gets a new entry and the old one is marked `SUPERSEDED by D-NNN`.

Status values: `DECIDED`, `PROPOSED` (awaiting my decision or my consultant), `SUPERSEDED`.

> Provenance: D-001 to D-007 were my decisions, made before or at the start of the 2026-10-13
> session. An AI assistant drafted the write-up from my brief (see `docs/ai-use-log.md`).
> Lines marked **[AI-expanded]** are reasoning the AI added beyond what I stated, to be
> confirmed, rewritten or deleted.

---

## D-001: Data sources: TfL Unified API for predictions, TrackerNet for positions
- **Date:** 2026-10-13 · **Status:** SUPERSEDED by D-014 · **Decided by:** me
- **Context:** The study evaluates TfL's live Tube arrival predictions, so it needs (a) the
  predictions themselves and (b) an independent idea of when trains actually arrived. No
  official open source of actual Tube arrival times exists.
- **Decision:** Use the TfL Unified API (`/Line/{id}/Arrivals`) as the source of the
  predictions being evaluated. Use TrackerNet (`PredictionDetailed`, via
  `api.tfl.gov.uk/TrackerNet`, API key required) for train position descriptions ("At X",
  "Between X and Y") from which actual arrival times are inferred.
- **Reasoning:** The Unified API is what passengers and third-party apps see, so it is the
  thing worth evaluating. TrackerNet exposes per-train location text that the Unified API
  does not, which is the only open signal close to "the train is at the platform now".
- **Alternatives considered:** Using the Unified API alone and treating the last prediction
  as the actual (rejected, see D-002). Hand-timing everything (does not scale).
- **Consequences:** Two feeds must be joined per train (see risk R-01). Depends on a legacy
  feed (R-02). Needs an API key stored outside the repo.

## D-002: Ground-truth inference is a methods contribution; avoid circularity
- **Date:** 2026-10-13 · **Status:** DECIDED · **Decided by:** me
- **Context:** Without official actuals, any accuracy number depends on how "actual arrival"
  is defined. Prior work (Chan 2016, TfL buses) used "last prediction = actual", which is
  unvalidated and circular: it measures predictions against predictions.
- **Update 2026-11-05:** Darwin is dropped; the hand-timed stopwatch sample is the only validation (D-016).
- **Update 2026-11-02:** The positions now come from the Unified API's `currentLocation` field, not
  TrackerNet (D-014). The rule against using TfL's last prediction stands.
- **Decision:** Infer actual arrival times from TrackerNet position changes, never from TfL's
  own last prediction. Validate the inference against (a) a small hand-timed stopwatch sample
  and (b) Darwin (National Rail actuals) at stations where Tube and National Rail services
  share track or stations.
- **Reasoning:** Using an independent signal breaks the circularity. Validation gives the
  method a measured error bar, which turns it into a contribution in its own right rather
  than an unstated assumption.
- **Alternatives considered:** Last-prediction-as-actual (rejected: circular). Pure
  hand-timing (rejected as the main method: too few observations, but kept as validation).
- **Consequences:** Ground-truth precision is limited by poll interval (R-04). The
  stopwatch sample needs a written protocol. Darwin access needs its own registration.
  **[AI-expanded]** The Darwin check only works on lines with shared running (this affects
  pilot line choice, see `docs/pilot/`).

## D-003: No correction or ML model
- **Date:** 2026-10-13 · **Status:** DECIDED · **Decided by:** me
- **Context:** A natural extension is a model that "corrects" TfL's predictions.
- **Decision:** No correction or ML model. The project is a measurement study plus the app.
  The app may show findings-based context such as "3 min (usually ~1 min late)", driven by
  lookup tables built from the study's results.
- **Reasoning:** A model adds complexity that the research question does not need, and its
  evaluation would compete for time with the data collection that the study depends on.
- **Update 2026-11-02:** Still no model, but the lookup tables are now tested, not only displayed:
  SQ3 and H1 test whether they reduce TfL's prediction error (D-013).
- **Alternatives considered:** Regression or ML correction model (rejected: scope).
- **Consequences:** The analysis is descriptive statistics over the logged data. The app's
  "usually late" hints are simple aggregates by (line, horizon, time of day, ...).

## D-004: No human participants in the core evaluation; low-risk ethics route
- **Date:** 2026-10-13 · **Status:** PROPOSED (confirm with my consultant) · **Decided by:** me
- **Context:** City requires ethics review proportional to risk.
- **Decision:** The core evaluation uses benchmarks and logged data only, no participants, so
  the low-risk ethics route should apply.
- **Reasoning:** No personal data is collected; the feeds are public operational data.
- **Update 2026-11-02:** I now plan a commuter survey (SQ4 in `docs/research-question.md`), so "no
  human participants" no longer holds for the whole project. This stays open until my consultant
  answers F1 in `docs/consultant-questions.md`.
- **Open points:** Confirm with my consultant. **[AI-expanded]** Ask specifically whether (a) any
  later app usability test with other people, and (b) me hand-timing trains on platforms,
  change the route.

## D-005: Native Swift/SwiftUI on Fedora with xtool
- **Date:** 2026-10-13 · **Status:** DECIDED · **Decided by:** me
- **Context:** Dev machine is Fedora Linux; no Mac; test device is an iPhone 11 (see update).
- **Decision:** Build a native Swift/SwiftUI app with xtool
  (github.com/xtool-org/xtool). No simulator, no hot reload; rebuild and install over USB.
- **Reasoning:** Native SwiftUI suits a map-heavy live app, and xtool is a way to build and
  sign iOS apps without a Mac.
- **Consequences:** Slow edit-build-install loop, so UI work benefits from paper wireframes
  first. Early "hello world on my iPhone" spike de-risks the toolchain. **[AI-expanded]**
  Free Apple developer accounts limit how long an installed build stays valid; check what
  xtool needs for signing.
- **Update 2026-11-05:** The test device is an iPhone 11 on iOS 27.2, not an iPhone 15. A
  hello-world app built with xtool runs on it.

## D-006: Location stays on the device
- **Date:** 2026-10-13 · **Status:** DECIDED · **Decided by:** me
- **Decision:** User location is used only on the device and is never sent to any server.
- **Reasoning:** Privacy by design, and it keeps the project outside personal-data handling,
  which supports D-004.

## D-007: No AI in coding; AI only for early thinking stages
- **Date:** 2026-10-13 · **Status:** DECIDED · **Decided by:** me
- **Decision:** All code (app, logger, analysis, tests) is written by me, by hand. AI
  assistants are used only for the early thought-process stages (research-question
  refinement, planning, decisions, risk analysis, documentation tidy-up). The AI does
  nothing in the repo unless I ask for it, and there is no AI configuration file in the
  repo (no `CLAUDE.md`). Every AI contribution is logged in `docs/ai-use-log.md`.
- **Reasoning:** I want to understand and own every line, and the implementation is part
  of what is assessed.
- **Consequences:** **[AI-expanded]** Check City's current generative-AI policy for
  dissertations and whether a declaration of AI use is required; the AI-use log is the
  evidence for it.

## D-008: Logger written in Java (on Linux)
- **Date:** 2026-10-27 · **Status:** DECIDED · **Decided by:** me
- **Context:** I had said the project is done by hand fully in Swift, but the logger runs on
  Linux, not the phone, so the language is a separate choice from the app's (D-005).
- **Decision:** The data logger is a Java command-line program running on my Linux machine.
  The app stays Swift/SwiftUI. All code is still written by me, by hand (D-007).
- **Reasoning:** I know Java and find it easier to understand than the alternatives, and the
  logger has to run reliably unattended for weeks.
- **Alternatives considered:** Swift on Linux (would share model types with the app), Python.
  Typescript and C++ I judged to have no advantage for this job.
- **Consequences:** **[AI-expanded]** Models for the TfL JSON are written twice, once in Java and
  once in Swift. Parsing JSON in Java needs a library or hand-written parsing; any library is
  reused software that goes in `docs/licences.md` (L-08) and Appendix B. A JDK 17 is already on my
  PATH.
- **Update 2026-11-06:** The logger runs on my OVHcloud VPS, not my Linux laptop (D-019).

## D-009: Version numbering: `prot-NN` for prototypes, semantic versions after
- **Date:** 2026-10-13 · **Status:** DECIDED · **Decided by:** me
- **Decision:** Prototypes are numbered `prot-01`, `prot-02`, `prot-03`, ... Once the project
  moves from prototype to the actual app, releases use semantic versions (`MAJOR.MINOR.PATCH`)
  starting at `0.5.0`.
- **Update 2026-11-02:** `prot-NN` applies to the app itself, starting once it is built. The logger
  and the wireframes each have their own separate version sequence (names not chosen yet).

## D-010: App concept: flat 2D map with a pull-up sheet; 2D before 3D; no accounts
- **Date:** 2026-11-02 · **Status:** DECIDED · **Decided by:** me
- **Context:** I think existing apps such as TfL Go do not feel live, and do not let you see the
  trains moving between stations. Sketches are in `docs/design/wireframes.md`.
- **Decision:** One main screen, a flat 2D map. Live data is requested only when I tap a line or a
  station, which opens a pull-up sheet with live arrivals and a 3D view. No account system for now.
  The 2D map and sheet get built and working before any 3D work starts.
- **Open points:** What the map shows before a tap. I want to use logged data matching the device's
  time; see D-012.
- **Consequences:** **[AI-expanded]** Train positions between stations are estimates, because
  the feed reports "between X and Y", so the UI labels them as estimated. The 3D view can only
  be schematic unless an open source of tunnel depth turns up.

## D-011: Whole-network coverage decided after the logger results
- **Date:** 2026-11-02 · **Status:** PROPOSED (awaiting logger results) · **Decided by:** me
- **Decision:** I lean towards covering the whole TfL map, but I make the final decision once I
  have results from the logger.
- **Alternative:** **[AI-expanded]** Draw the whole network, but show live trains and corrected
  times only on lines that pass the pilot's go / no-go, because the train join may be poor on some
  lines (R-01).

## D-012: Bundled data or a server, and the storage format, decided after measuring
- **Date:** 2026-11-02 · **Status:** DECIDED 2026-11-06 (served, see D-021) · **Decided by:** me
- **Context:** The app needs data derived from the logger. Bundling it avoids a server, but I am
  concerned about the size of 7 days of data. A server adds uptime and database fetches.
- **Decision:** I decide between bundling and a server, and between JSON, SQLite or another format,
  once I have a feel for how big the data is and how it has to be stored.
- **Alternatives:** Static files bundled in the app; a hosted server with a database.
- **Update 2026-11-02:** The app looks up one row by station, so SQLite is my preferred format. I
  will decide on bundling when the time comes, but probably will not bundle everything into the
  app.
- **Update 2026-11-06:** Decided: the data is served, not bundled. The app asks a small API on my
  VPS for the logged snapshot matching the current time and day (D-021).

## D-013: Research question v1: measure, then correct
- **Date:** 2026-11-02 · **Status:** DECIDED · **Decided by:** me
- **Decision:** v1 is in `docs/research-question.md`: measure how accurate and stable TfL's
  predictions are, then test whether a lookup-table correction reduces their error. Stability is
  measured and reported, but only accuracy is tested (H1). The survey is a fourth sub-question.
- **Reasoning:** I want to try to beat TfL, not only measure it, and "better" for me means more
  accurate and more stable. **[AI-expanded]** A lookup table can shift a time by a typical lateness
  but is not designed to stop predictions jumping around, so a stability hypothesis would need
  smoothing, which goes beyond lookup tables. The question can still be answered if the correction
  fails, and stability needs no ground truth, so it survives if the position data proves unusable.
- **Alternatives considered:** Predicting arrival times directly (reads as building a predictor,
  against D-003); correction as the only headline question; testing stability too; dropping
  stability; the survey as an objective rather than a sub-question.

## D-014: Drop TrackerNet; use only the Unified API
- **Date:** 2026-11-02 · **Status:** DECIDED · **Decided by:** me
- **Context:** I had planned to use TrackerNet for train positions (D-001). A one-off request on
  2026-11-02 to `/Line/victoria/Arrivals` returned a `currentLocation` field on all 224 records, for
  example "At Seven Sisters" and "Between Tottenham Hale and Seven Sisters".
- **Decision:** The project does not use TrackerNet. The Unified API supplies both the predictions
  and the position text used to infer actual arrivals.
- **Reasoning:** TrackerNet is an older system, so I am not going to bother with it.
  **[AI-expanded]** Predictions and positions now arrive in the same record, so there are no two
  feeds to join, and the legacy-feed risk (R-02) goes away.
- **Alternatives considered:** Keeping TrackerNet as the position source; testing both feeds and
  comparing them first.
- **Consequences:** **[AI-expanded]** I give up any extra TrackerNet fields, such as train and trip
  numbers, without having compared them. I checked `currentLocation` on one line at one moment only,
  so the pilot has to confirm it works on my chosen lines. All data now depends on one feed. Positions
  and predictions come from the same TfL source, so only the stopwatch sample is an
  independent check (Darwin was later dropped, D-016).

## D-015: Testers get the app through TestFlight, built in the cloud with EAS
- **Date:** 2026-11-02 · **Status:** DECIDED · **Decided by:** me
- **Context:** I have no Mac (D-005), and survey participants need the app on their own iPhones
  (SQ4).
- **Decision:** I use EAS to build the app in the cloud and get it onto TestFlight for testers. To
  test on my own phone I keep using xtool.
- **Consequences:** **[AI-expanded]** Needs a paid Apple Developer Program membership and an Expo
  account. Expo's docs say EAS is "designed to work for any native project", but they do not mention
  Swift Package Manager or projects with no Xcode project, which is how xtool builds, so an early
  test build has to confirm this. External testers may need Beta App Review on the first build, and
  builds expire after 90 days.

## D-016: Drop Darwin; validate with the stopwatch sample only
- **Date:** 2026-11-05 · **Status:** DECIDED · **Decided by:** me
- **Context:** D-002 planned two checks on inferred arrivals: a hand-timed stopwatch sample and, where
  lines overlap, Darwin (National Rail).
- **Decision:** The project does not use Darwin. The stopwatch sample is the only independent check.
- **Reasoning:** **[AI-expanded]** Darwin reports National Rail trains, and I have no evidence that it
  covers Tube trains, so a National Rail arrival says nothing about when a Tube train arrived.
- **Consequences:** **[AI-expanded]** The stopwatch sample carries SQ1 alone, so it needs a written
  protocol and enough observations across lines and times of day. My consultant answered "yes" (C2)
  to a plan that included Darwin, so I mention this change at my next meeting.

## D-017: Repo licence is MIT
- **Date:** 2026-11-05 · **Status:** DECIDED · **Decided by:** me
- **Decision:** My code is released under the MIT License.
- **Alternatives considered:** No licence until the project is marked, Apache 2.0, GPL 3.0, CC BY 4.0.
- **Consequences:** **[AI-expanded]** Anyone may reuse the code if they keep the notice, including
  while the project is being assessed. The licence does not cover TfL data or third-party material.

## D-018: Map drawn by me from TfL data
- **Date:** 2026-11-05 · **Status:** DECIDED · **Decided by:** me
- **Decision:** The app's map is my own design, derived from Unified API data (station positions and
  stop order). I design my own icons and do not use TfL's logos, roundel or Tube map design.
- **Reasoning:** I can derive the map from the data, and I cannot use TfL's logos.


## D-019: Logger runs on my OVHcloud VPS
- **Date:** 2026-11-06 · **Status:** DECIDED · **Decided by:** me
- **Context:** The logger must run without gaps for weeks so ill run it on my server
- **Decision:** The Java logger (D-008) runs on an OVHcloud VPS that I own.
- **Alternatives considered:** My Fedora laptop left on or a raspberry pi.
- **Consequences:** **[AI-expanded]** The logger runs as a service that restarts after a crash or
  reboot. The VPS clock must stay synced (for example with chrony), because inferred arrival times
  depend on it, and all times are stored in UTC. The TfL API key lives in an environment variable or
  a config file on the VPS, never in the repo. Free disk space on the VPS limits how much raw data I
  keep (D-020).
- **Update 2026-11-19:** How it is deployed, since 2026-11-17. I build it on the VPS from the repo with
  `./gradlew installDist` and run the start script as a systemd service (`tfl-logger.service`) with
  `Restart=always` (10 seconds) and a 30-second stop timeout, so it survives logouts, crashes and
  reboots, and its shutdown hook finishes the current request before it stops. Its settings, including
  the API keys, are in `~/tfl-logger.env`, outside the repo so `git pull`
  cannot touch them. Data lives in `~/tfl-data/`. To update it: `git pull`, `./gradlew installDist`,
  `sudo systemctl restart tfl-logger`.
  During the 48-hour run the data was in `~/dissertation/data/`, inside the repo folder

## D-020: Main dataset is two full weeks of the whole network
- **Date:** 2026-11-06 · **Status:** DECIDED · **Decided by:** me
- **Context:** One request for all 11 Tube lines on 2026-11-06 returned 3,825 records and 3.6 MB
  (146 KB gzipped). At one request every 30 seconds that is about 10 GB a day raw whic is too much to store anywhere
- **Decision:** The main dataset is two consecutive full weeks of every Tube line, after the pilot.
  Raw responses are kept compressed on the VPS (about 6 GB for two weeks at 30-second requests).
  The request interval is set from the pilot.
- **Reasoning:** **[AI-expanded]** H1 needs the lookup table to be built on earlier data and tested
  on later data it has not seen. With one week, it would be built on weekdays and tested on a
  weekend, and weekend service differs. Two weeks lets me build on week 1 and test on week 2 with the
  same days of the week.
- **Consequences:** **[AI-expanded]** The raw data is too big for the GitHub repo (single files over
  100 MB are rejected), so the repo gets a sample plus derived tables, and the full raw data stays on
  the VPS and a backup. The stopwatch sample must be taken during the two logged weeks, or there are
  no inferred arrivals to check it against. The two weeks should avoid the Christmas period. This
  replaces the continuous collection from 21 December to 7 March in my PDD Gantt chart; I tell my
  consultant at the next meeting.
- **Update 2026-11-19:** The two weeks are 2026-12-07 00:00 to 2026-12-20 23:59 UTC, all 11 Tube lines,
  if the pilot's go/no-go passes (D-027). Switching to all lines is one change to `LOGGER_LINES` in
  `~/tfl-logger.env` and a restart. The stopwatch sample for this dataset is taken inside these two weeks.

## D-021: Logged trains are replayed through an API on my VPS
- **Date:** 2026-11-06 · **Status:** DECIDED · **Decided by:** me
- **Context:** When no line is selected, the app shows what the logger recorded at the same time and
  day of the week instead of live data. D-012 left open whether that data is bundled or served.
- **Decision:** A small API on my VPS (D-019) serves the app the saved raw response closest to the
  requested time and day. The app does not bundle the logged data.
- **Alternatives considered:** Bundling a summary of typical positions in the app; serving a summary
  instead of raw snapshots.
- **Reasoning:** **[AI-expanded]** The raw responses are kept anyway (D-022), and one full-network
  snapshot is about 146 KB gzipped, which is small enough to send to a phone.
- **Consequences:** **[AI-expanded]** This adds a third component, written by me by hand, alongside the
  logger and the app, and it has to stay up while testers use the app. A replay shows one real past
  day, including any disruption on it, so the app labels it with the date it was recorded, not as
  typical. Serving logged TfL data needs the same attribution as the app (L-01). The SQ3 lookup-table
  correction is separate and is built from the logger's database. Details are decided before prot-02.

## D-022: Logger database schema
- **Date:** 2026-11-06 · **Status:** DECIDED · **Decided by:** me
- **Context:** One full-network response on 2026-11-06 had 3,825 records. Stored with every field,
  that is about 223 bytes per record in SQLite, or about 34 GB for two weeks at one request every 30
  seconds, against 19 GB free on the VPS.
- **Decision:** `logger/schema.sql` defines seven tables:
  - `tflrequests`: one row per request, including failed ones (sent and received times, HTTP status,
    size, record count, TfL's response timestamp, error, raw file, lines requested).
  - `tflresponses`: one row per prediction, linked to its request. It keeps `vehicleId`, line,
    station (`naptanId`), platform, `direction`, destination, `towards`, `currentLocation`,
    `timeToStation` and `expectedArrival`.
  - `lines`, `stations`, `platforms`, `towards`, `locations`: lookup tables, so each repeated text
    value is stored once and predictions store only its ID.
  - Dropped fields: `stationName` and `destinationName` (kept once in `stations`), `timestamp` (the
    same for the whole response, so stored in `tflrequests`), `timeToLive`, `id`, `$type`,
    `operationType`, `modeName`, `lineName`, `bearing` and the `timing` object.
  - Raw responses are kept as compressed files on the VPS, and moved off once at the end.
  - **Update 2026-11-06:** Raw files are one gzipped JSON Lines file per UTC hour, named like
    `raw/2026-12-07/2026-12-07T08Z.jsonl.gz`. Each line is one response, starting with its
    `request_id` and `sent_at`. A new file is opened each hour and the previous one closed cleanly,
    so a crash loses at most the current hour's last write. `tflrequests.raw_file` holds the file
    name, so the replay API (D-021) can find any request.
  - **Update 2026-11-06:** Every request has a 10-second time limit. A failed, timed-out or empty
    request still gets a `tflrequests` row, so gaps show up in the data. Which failures count as gaps,
    and when TfL's data counts as stale, is decided in the analysis after the pilot.
- **Reasoning:** **[AI-expanded]** In that response, `timeToLive` equalled `expectedArrival` in every
  record, `timestamp` was the same in every record, and TfL's `id` was repeated in 352 records, so it
  cannot be a key. `direction` was empty in 576 records, and `platformName` still gave the direction
  in 544 of them. `vehicleId` is only unique within a line, so a train is identified by line and
  `vehicleId` together. `expectedArrival` equals TfL's timestamp plus `timeToStation`, so it is kept
  for convenience, not because it adds information.
- **Consequences:** **[AI-expanded]** The logger switches foreign keys on for every connection
  (`PRAGMA foreign_keys = ON`), or the links are not checked. Only predictions that changed since the
  previous request are stored; what counts as "changed", and the request interval, are set from the
  pilot. Indexes are chosen after the pilot, once the real size and queries are known. The patterns
  above come from one response, so the pilot checks that they hold.
- **Update 2026-11-19:** Storing only changed predictions is dropped: the logger stores every prediction.
  The 48-hour run measured about 47 MB a day for the Victoria line (36 MB raw files and 55 MB of
  database over 45.7 hours, about 58 bytes per prediction). Scaled to all 11 lines, about 700 MB a day,
  or 8 to 12 GB for two weeks, which fits the VPS. Keeping every prediction also keeps the analysis
  simpler, since nothing has to be reconstructed.

## D-023: Pilot line is the Victoria line
- **Date:** 2026-11-06 · **Status:** DECIDED · **Decided by:** me
- **Decision:** The pilot (`docs/pilot/README.md`) logs the Victoria line only.
- **Reasoning:** **[AI-expanded]** It is self-contained with no branches, so every train runs the same
  route between Walthamstow Central and Brixton, which makes following one train simpler. I already
  have a sample response for it (2026-11-02 and 2026-11-06).
- **Consequences:** **[AI-expanded]** Problems that only appear on branching or shared lines (the
  Northern line's branches, trains on the District line changing destination, shared track on the
  Circle and Hammersmith & City lines) are not tested by the pilot, so they are checked when the main
  collection starts.

## D-024: `request_id` comes from SQLite, not a counter in the program
- **Date:** 2026-11-12 · **Status:** DECIDED · **Decided by:** me
- **Context:** My first version counted requests in `Main`. That counter starts again at 1 every time
  the logger restarts, so after a restart its IDs would clash with rows already in `tflrequests`.
- **Decision:** SQLite assigns `request_id` (`INTEGER PRIMARY KEY`), and `insertRequest` returns it with
  `INSERT ... RETURNING request_id`. The request row is inserted first, and the raw file line is written
  with that ID.
- **Alternatives considered:** Keeping my counter and setting it from `SELECT MAX(request_id)` at start-up.
- **Reasoning:** **[AI-expanded]** One source of truth that survives restarts, with no extra code. Tested
  on 2026-11-13: IDs carried on after a restart, and matched the raw file.

## D-025: Every request is recorded, and any 2xx status counts as success
- **Date:** 2026-11-13 · **Status:** DECIDED · **Decided by:** me
- **Decision:** A response with a 2xx status and a usable body (an array with no line breaks) is
  saved. Everything else still gets a `tflrequests` row, through one method (`recordFailure`), with
  the error: `HTTP <status>`, `Bad body`, or the exception type (for example `HttpTimeoutException`).
  Failures never stop the logger. After more than 10 failures in a row it logs a warning, and it logs
  when it recovers.
- **Reasoning:** **[AI-expanded]** Gaps must show up in the data, not as silent holes (R-03), so the
  analysis can tell "no trains" from "logger could not see them".

## D-026: Repeated and stale responses are kept and handled in the analysis
- **Date:** 2026-11-19 · **Status:** DECIDED · **Decided by:** me
- **Context:** In a 10-minute test on 2026-11-10, 7 of 21 responses repeated an earlier TfL timestamp,
  once a later request returned older data, and TfL's data was about 49 seconds old when it arrived.
- **Decision:** The logger saves every response as it arrives, including repeats. The analysis orders
  observations by TfL's timestamp (`tfl_newest_timestamp`), not by `sent_at`, and drops exact repeats.
- **Consequences:** **[AI-expanded]** Repeats cost some disk space but nothing is lost. The age of the
  data and TfL's update interval, not my 30-second requests, limit how precisely an arrival can be
  timed (R-04). The pilot measures both.

## D-027: Pilot from 19 November to 2 December, analysed once at the end
- **Date:** 2026-11-19 · **Status:** DECIDED · **Decided by:** me
- **Context:** The logger was finished and running on the VPS by 2026-11-17, ahead of the pilot planned
  for 7 to 20 December.
- **Decision:** The Victoria line pilot runs from 2026-11-19 00:00 to 2026-12-02 23:59 UTC. Its go/no-go
  criteria are fixed today in `docs/pilot/README.md`. I build the analysis pipeline on the 48-hour run
  before the pilot (development data), then run it once on the pilot data when the pilot ends. The
  stopwatch sample is taken inside the pilot window. The iOS app is built after the go/no-go, with the
  scope the results support.
- **Reasoning:** **[AI-expanded]** Fixing the criteria before looking at the pilot data, and tuning only on
  separate data, stops me moving the goalposts. Starting about 2.5 weeks early leaves slack before
  Christmas and lets the main collection start in early January.
- **Consequences:** **[AI-expanded]** This changes the dates in my PDD Gantt chart; I tell my consultant at
  the next meeting. The app now comes after the pilot and the main collection planning, so it must still
  reach testers in time for the survey, after full ethics approval (final ethics materials due 21 February
  2027).

## D-028: The analysis is written in Python
- **Date:** 2026-11-19 · **Status:** DECIDED · **Decided by:** me
- **Decision:** The analysis pipeline (following trains, inferring arrivals, prediction errors,
  stability, the lookup-table correction and the report's charts) is written by me, by hand, in Python
  with pandas and matplotlib, reading `logger.db` directly (D-007).
- **Alternatives considered:** Java, the same language as the logger; plain SQL queries.
- **Reasoning:** **[AI-expanded]** pandas reads SQLite directly and suits exploring time-ordered data, and
  matplotlib produces the report's figures. Each library gets a licence row with its version when I
  install it.

## D-029: The published timetable is a third baseline, saved daily from the main collection
- **Date:** 2026-11-20 · **Status:** DECIDED · **Decided by:** me
- **Decision:** The main study compares four times for each station and moment: the published
  timetable, TfL's live prediction, my lookup-table correction, and the inferred actual arrival. Each
  estimate answers the same question at the same moment: when is the next train due at this station?
  The timetable is fetched from the Unified API (`/Line/{id}/Timetable/{stopId}`, both ends of each
  line) once a day by a small script of my own, separate from the logger, from 2026-12-07 for all 11
  lines. No timetables are saved for the pilot.
- **Last trains and closures:** the timetable labels observations; it never removes them. Each station
  gets its own timetabled last time (the last departure from the terminus plus the minutes to that
  station), so the line quietens down station by station until it is fully shut. Observations after
  that time are labelled "after timetable" and reported separately. A run of empty responses outside
  timetabled hours is a scheduled closure; one inside timetabled hours is flagged and looked into.
- **Alternatives considered:** a line-wide cut-off at the last departure, treating anything later as no
  trains; finding closures from the timetable instead of the data; starting the daily snapshots during
  the pilot.
- **Reasoning:** **[AI-expanded]** Without a timetable baseline a reader cannot tell how much TfL's live
  predictions add over the published plan. A line-wide cut-off would delete real trains: on
  2026-11-18 the last departure from Brixton was 00:28, but the trip to Walthamstow Central takes 31
  minutes, and a train was still being predicted from 00:38 to 00:43. A timetable only shows what was
  planned, so engineering works, strikes and late running are found from the data. A separate script
  keeps the logger unchanged. The timetable is to the minute and trains run every 90 seconds at peak,
  so its error is reported by time of day.
- **Effect on the research question:** adds SQ5 (`docs/research-question.md`); H1 is unchanged. This is
  a change since the PDD, so I tell my consultant.
- **Licence:** TfL Unified API data, already covered by L-01.

## D-030: Position text becomes one of four states, in the order a train moves
- **Date:** 2026-11-24 · **Status:** DECIDED · **Decided by:** me
- **Decision:** Each `currentLocation` text is turned into a state and a station. Between two
  stations a train goes through four states in order: **AT** the station, **DEPARTED** from it,
  **BETWEEN** the two, **ARRIVING** at the next, then **AT** the next station.

  | TfL text | State | Station |
  |---|---|---|
  | `At X`, `At X Platform N`, `Departing X` | AT | X |
  | `At Platform` | AT | the station the prediction is for |
  | `Departed X`, `Left X` | DEPARTED | X |
  | `Between X and Y` | BETWEEN | Y (X kept as the station before) |
  | `Approaching X` | ARRIVING | X |

  Depots and sidings (`Brixton Area`, `Victoria Siding`, `At Brixton Sidings` and similar) and the
  text `0` are not parsed and not used to follow trains. They are counted and reported, not silently
  dropped.
- **What the states are not:** the four states are TfL's own labels put in order. They are not
  measured positions, so DEPARTED, BETWEEN and ARRIVING are not equal thirds of the distance or
  time between stations.
- **Alternatives considered:** keeping TfL's seven wordings as they are; `Departing X` as its own
  state (it means the train is still at the platform, so it counts as AT); parsing depots as a state.
- **Reasoning:** **[AI-expanded]** An ordered cycle makes following a train and inferring an arrival
  simple: an arrival at S is the change from BETWEEN or ARRIVING towards S to AT S. Counting
  `Departing X` as AT stops arrivals being missed when a train is only seen as it is about to leave
  (5,610 rows in the development data). Depots and `0` say nothing about a train in service (3,500
  rows, 0.7%).
- **Still open, decided at step 8:** whether an arrival can also be inferred when AT S is never seen
  (for example ARRIVING S followed by DEPARTED S), with a wider bracket and a flag.

## D-031: One position per train per update, by majority vote
- **Date:** 2026-11-26 · **Status:** DECIDED · **Decided by:** me
- **Decision:** A train has one prediction per station ahead of it, and each carries its own
  position text. For each train (`line_id` plus `vehicle_id`) in each new response, the position
  (state plus station, D-030) is the one most of its predictions give. Each train-update also gets a
  `disagreed` flag, true when its predictions gave more than one position, so these can be checked
  separately later.
- **Alternatives considered:** the position furthest along the route; the position from the
  prediction for the nearest station; leaving out train-updates whose predictions disagree.
- **Reasoning:** **[AI-expanded]** In the development data 5,696 of 57,211 train-updates (10%)
  disagree, almost always between neighbouring states. In the example checked (train 210 at
  20:35:46 on 2026-11-17, six predictions "Between Oxford Circus and Green Park" and one "At Oxford
  Circus"), the single odd one out was left over from the previous update, so the majority was
  right. Leaving out disagreeing updates would lose 10% of observations.
- **Ties and consistency (decided 2026-11-26):** every position gets a progress score, how far along
  its journey the train is: the station's place in the line's order in the direction of travel, times
  four, plus the state's rank (BETWEEN towards it 1, ARRIVING 2, AT 3, DEPARTED 4). Three rules:
  1. Ties (441 train-updates, 0.8%) are left to the plain vote for now. Settling them by the higher
     progress changes only 231 train-updates (0.4%), almost all between neighbouring states such as
     DEPARTED and BETWEEN for the same pair of stations, so it is revisited after the main collection
     and added only if the all-lines data shows it matters (decided 2026-11-26).
  2. Within a trip, progress cannot go down. A trip ends when the train's direction changes or when
     it has not been seen for more than 10 minutes (the same limit as pilot criterion 5; a full
     Victoria line trip takes about 32 minutes). An update further back than the best progress so far
     in its trip is treated as a leftover, dropped and counted. In the development data this gives
     1,483 trips and 199 dropped updates (0.35%), mostly one state or one station back. Without the
     10-minute rule, the 122 trains that left service and came back in the same direction would look
     like 4,375 backwards updates (7.65%) (decided 2026-11-26).
  3. Skipped states are allowed (for example AT one station straight to AT the next); they are not
     filled in, and the arrival rule at step 8 brackets them.
- **Station order and direction:** stations are numbered north to south, or east to west on lines that
  run that way. The direction of travel is TfL's own `direction` field, voted per
  train-update like the position, ignoring blanks. On the Victoria line `inbound` is always
  southbound and `outbound` always northbound (178,850 and 148,604 predictions, none contradicting
  the train's movement). The field is blank on 21% of predictions, almost all at the two termini;
  2,923 train-updates (5%) have no value at all and take the direction of the train's previous update,
  which agrees with the train's movement in 99.7% of the cases that can be checked, leaving 5 with
  none. The platform name was rejected: at the termini it names the platform a train will leave
  from, so 19% of predictions gave the opposite direction to their own train. Which of `inbound` and
  `outbound` runs along the station order is checked for each line before the main collection. Branching lines (Northern, District and others) and the Circle line, a loop, need
  their own treatment before the main collection.
- **Evidence for the tie rule:** **[AI-expanded]** across the 441 ties, taking the position that
  changed since the train's previous update matched its next position 50% of the time, against 27%
  for an arbitrary pick, which fits the odd one out usually being a leftover from the update before.

## D-032: The main collection polls every 10 seconds
- **Date:** 2026-11-26 · **Status:** DECIDED · **Decided by:** me
- **Decision:** From the main collection (2026-12-07), the logger requests TfL every 10 seconds
  instead of every 30. The pilot keeps running at 30 seconds until 2026-12-02, so its go/no-go
  criteria are judged on 30-second data. The change to the logger is made during the go/no-go window
  (2026-12-03 to 12-06).
- **Alternatives considered:** keeping 30 seconds; 15 seconds.
- **Reasoning:** **[AI-expanded]** A separate one-hour test on 2026-11-26 (Victoria line, 20:36 to
  21:36 UTC, `docs/pilot/README.md`) showed TfL's data sometimes changes within 10 seconds. Compared
  with 30-second polling in the same hour, 10-second polling saw a train AT the station for 82.6% of
  arrivals instead of 69.8%, and the median arrival bracket fell from 60.2 s to 40.3 s, so every
  inferred arrival is about ±20 s instead of ±30 s.
- **Still to settle before 2026-12-07 (storage):** storing every response at 10 seconds would be about
  24 to 36 GB for two weeks of all 11 lines, against about 19 GB free on the VPS. Storing only the
  responses with a newer TfL timestamp (about a third of them at 10 seconds) would keep storage close
  to the current projection, but changes D-022 (store everything). Decided at go/no-go.

## D-033: Arrivals are bracketed by progress, and criterion 1 needs a bracket of 90 s or less
- **Date:** 2026-11-26 · **Status:** DECIDED · **Decided by:** me
- **Decision:** An inferred arrival at station S is the first update in a trip whose progress score
  (D-031) reaches S's AT value; the bracket is that update and the one before it, and the arrival time
  is the midpoint. Arrivals where no update said "At S" (missed-AT arrivals) count, flagged
  `seen_at = False`. Pilot criterion 1 becomes the share of expected stops with an inferred arrival
  whose bracket is 90 s or less, passing at 80%. Recorded as a dated revision in
  `docs/pilot/README.md`, made before any pilot data was analysed.
- **Alternatives considered:** keeping the original wording (only arrivals where "At S" was seen);
  counting every arrival with no bracket limit; a limit of 120 s; a limit of 30 s.
- **Reasoning:** **[AI-expanded]** On the development data, 19,981 arrivals were found, 14,455 (72.3%)
  with "At S" seen. Missed-AT arrivals happen at every station, about 1 in 4, and are as precise as
  the others (median bracket 60.7 s against 59.9 s), so leaving them out would fail criterion 1
  (72.3% against 80%) on good data. With no limit the measure is 100% by construction. 90 s passes on
  the development data (86.6%) and in the 30-second version of the 2026-11-26 test hour (93.3%), and
  means at most two missed updates in a row; 120 s (99.5%) was judged too loose, and 30 s (16.4%)
  cannot be met at 30-second polling. The main study sets its own bracket limit on the 10-second data.

## D-034: The station where a train turns round is an expected stop, termini included
- **Date:** 2026-11-26 · **Status:** DECIDED · **Decided by:** me
- **Decision:** A trip's expected stops are every station between where it was first seen and the
  station where the train turns round, including that station. Usually that is a terminus, but a
  train that turns round early (for example at Seven Sisters) has that station as its last expected
  stop, and the stations beyond it are not expected. The arrival at the turning station is bracketed
  by the last update of the trip and the first update of the same train's next trip, when that next
  trip runs the other way, starts within 10 minutes and starts AT a station.
- **Alternatives considered:** leaving the two termini out of the pilot's expected stops.
- **Reasoning:** **[AI-expanded]** When a train reaches a terminus, TfL's predictions switch straight to
  its next trip, so the arrival shows up as the start of the next trip, not as part of this one (only
  14 and 5 terminus arrivals were found within trips). Linking each trip to the next recovers 1,222
  turning-station arrivals in the development data (Walthamstow Central and Brixton almost all of
  them), with a median bracket of 84.8 s; 785 are 90 s or less. Including them (and the 19 terminus arrivals
  found inside trips, none counted twice), criterion 1 is 85.3% (21,203 expected stops) and criterion
  2 is 60.011 s. Leaving the termini out would hide early
  turn-rounds and part of each trip.
- **Criterion 2 note:** on the development data the median bracket is 60.002 s without the termini
  and 60.011 s with them, just over the 60 s pass mark, because the median falls on two TfL updates
  about 60 s apart. The pass mark is not changed. If the pilot lands just over too, the outcome is
  "Partial", with 10-second polling for the main collection (D-032) as the fix already evidenced
  (median bracket 40.3 s in the 2026-11-26 test hour).

## D-035: Arrival brackets are measured in whole seconds for the criteria
- **Date:** 2026-11-26 · **Status:** DECIDED · **Decided by:** me
- **Decision:** When criteria 1 and 2 are worked out, each arrival bracket is rounded to the nearest
  whole second. The pass marks (bracket of 90 s or less for at least 80% of expected stops; median
  bracket of 60 s or less) do not change.
- **Alternatives considered:** keeping exact times to the millisecond; moving the limits between the
  clusters described below.
- **How it was noticed:** the step 9 hand count of one trip (train 210, Walthamstow Central to
  Brixton, 2026-11-17 20:15 to 20:46, 15 expected stops) had three brackets of 90.029, 90.057 and
  90.337 s that failed the 90 s limit by fractions of a second.
- **Reasoning:** **[AI-expanded]** A bracket is always about one, two, three or four TfL updates wide,
  so brackets cluster around 30, 60, 90 and 120 s, each spread by a few hundred milliseconds of jitter
  in TfL's timestamps (development data, to the nearest second: 5,841 brackets from 29 to 31 s, 7,434
  from 59 to 61 s, 5,348 from 89 to 91 s, 120 at 120 s). Both pass marks fall in the middle of a
  cluster, so with exact times the result flips on milliseconds of clock noise: 2,303 brackets lie
  between 90.0 and 91.0 s, and the median is 60.011 s. TfL's own predictions (`expectedArrival`) are
  in whole seconds, so whole seconds is the precision of the thing being judged. On the development
  data: criterion 1 is 95.0% (85.3% with exact times) and criterion 2 is 60 s (60.011 s), so both pass.
  Decided on the development data before any pilot data was analysed.

## D-036: How stopwatch timings are matched to inferred arrivals (criterion 3)
- **Date:** 2026-11-27 · **Status:** DECIDED · **Decided by:** me
- **Decision:** Each stopwatch timing is matched to the inferred arrival at the same station, in the
  same direction (Southbound platform = `inbound`, Northbound = `outbound` on the Victoria line),
  closest in time. If no inferred arrival is within 180 s, the timing is unmatched and listed in the
  results, not dropped. Every matched difference counts in the median, however large. The difference
  is the inferred arrival minus the stopwatch time, so a positive value means the method places the
  arrival too late.
- **Alternatives considered:** a tight cap (for example 30 s); no cap at all.
- **Reasoning:** **[AI-expanded]** On the development data, southbound trains reach Oxford Circus a
  median of 120 s apart, and only 1 gap in 10 is under 60 s. Matching to the nearest arrival
  therefore picks the right train whenever the method is off by less than about half that gap (about
  60 s), which is well above criterion 3's 30 s pass mark, so wrong-train matches cannot produce a
  false pass. A tight cap would turn every large error into "unmatched" and leave only the good
  timings in the median, so criterion 3 would pass by construction. With no cap, a timing taken while
  the logger or TfL had nothing (for example during an outage) would be matched to a train far away:
  a made-up timing at Oxford Circus at 03:00 on 2026-11-18, when the line was closed, is 9,241 s from
  the nearest inferred arrival. 180 s only catches that case. Decided on the development data before
  any pilot data was analysed, and before any stopwatch timings were taken.
