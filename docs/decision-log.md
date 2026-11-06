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
