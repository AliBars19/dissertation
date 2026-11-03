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
- **Context:** Dev machine is Fedora Linux; no Mac; test device is an iPhone 15.
- **Decision:** Build a native Swift/SwiftUI app with xtool
  (github.com/xtool-org/xtool). No simulator, no hot reload; rebuild and install over USB.
- **Reasoning:** Native SwiftUI suits a map-heavy live app, and xtool is a way to build and
  sign iOS apps without a Mac.
- **Consequences:** Slow edit-build-install loop, so UI work benefits from paper wireframes
  first. Early "hello world on my iPhone" spike de-risks the toolchain. **[AI-expanded]**
  Free Apple developer accounts limit how long an installed build stays valid; check what
  xtool needs for signing.

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
- **Date:** 2026-11-02 · **Status:** PROPOSED (awaiting measured data sizes) · **Decided by:** me
- **Context:** The app needs data derived from the logger. Bundling it avoids a server, but I am
  concerned about the size of 7 days of data. A server adds uptime and database fetches.
- **Decision:** I decide between bundling and a server, and between JSON, SQLite or another format,
  once I have a feel for how big the data is and how it has to be stored.
- **Alternatives:** Static files bundled in the app; a hosted server with a database.
- **Update 2026-11-02:** The app looks up one row by station, so SQLite is my preferred format. I
  will decide on bundling when the time comes, but probably will not bundle everything into the
  app.

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
  and predictions come from the same TfL source, so only the stopwatch sample and Darwin are
  independent checks.

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
