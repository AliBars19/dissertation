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
- **Date:** 2026-10-13 · **Status:** DECIDED · **Decided by:** me
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
- **Alternatives considered:** Regression or ML correction model (rejected: scope).
- **Consequences:** The analysis is descriptive statistics over the logged data. The app's
  "usually late" hints are simple aggregates by (line, horizon, time of day, ...).

## D-004: No human participants in the core evaluation; low-risk ethics route
- **Date:** 2026-10-13 · **Status:** PROPOSED (confirm with my consultant) · **Decided by:** me
- **Context:** City requires ethics review proportional to risk.
- **Decision:** The core evaluation uses benchmarks and logged data only, no participants, so
  the low-risk ethics route should apply.
- **Reasoning:** No personal data is collected; the feeds are public operational data.
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

## D-008: Logger also written in Swift (on Linux)
- **Date:** 2026-10-13 · **Status:** PROPOSED (awaiting my own decision)
- **Context:** I said the project is "done by hand fully in Swift". The logger runs on
  Linux, not the phone, so this reading needs confirming.
- **Decision (if confirmed):** The data logger is a Swift command-line program running on
  Linux (my machine).
- **Alternatives:** A different language for the logger only (e.g. Java).

## D-009: Version numbering: `prot-NN` for prototypes, semantic versions after
- **Date:** 2026-10-13 · **Status:** DECIDED · **Decided by:** me
- **Decision:** Prototypes are numbered `prot-01`, `prot-02`, `prot-03`, ... Once the project
  moves from prototype to the actual app, releases use semantic versions (`MAJOR.MINOR.PATCH`)
  starting at `0.5.0`.
