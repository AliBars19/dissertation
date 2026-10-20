# dissertation
this is my dissertation, welcome to graduation

**How accurate are TfL's live Tube arrival predictions?** A measurement study of TfL's live
arrival predictions (the research core), plus a live Tube tracking iOS app, "Flightradar for
trains" (the demo).

BSc Computer Science final-year project, City, University of London.

## Layout
| Path | What lives there |
|------|------------------|
| `docs/research-question.md` | The research question and every revision of it |
| `docs/decision-log.md` | Every decision with its reasoning (`D-NNN`) |
| `docs/risks.md` | Risk register (`R-NN`) |
| `docs/lit-review/` | Literature review notes and search log |
| `docs/pilot/` | One-line pilot: line choice, logger schema, results |
| `docs/sessions/` | Dated notes from each working session |
| `docs/ai-use-log.md` | Every use of AI tools in this project |
| `logger/` | Data logger for the TfL Unified API and TrackerNet |
| `data/` | Logged and derived data (commit policy to be decided) |
| `app/` | The iOS app (Swift/SwiftUI, xtool on Fedora) |

## AI use
All code is written by hand. AI tools were used only for early planning and documentation;
see `docs/ai-use-log.md` and decision D-007.

## Data
Powered by TfL Open Data.
This project is not affiliated with or endorsed by Transport for London.