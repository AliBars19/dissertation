# AI use log

Record of every use of AI tools in this project (D-007). AI is used only for early thinking
stages, never for code.

The handbook (8.7) requires, for the PDD and the final submission, a "Use of AI" appendix listing
each use, the prompts or inputs given, what came back, and what was done with it. Vague or
incomplete reporting counts as misconduct, so every row below needs all five columns.

| Date | Tool | Prompt / input | What came back | What I did with it |
|------|------|----------------|----------------|----------------------|
| 2026-10-13 | Claude Code (Claude Opus 5.5) | Project brief: research question v0, decisions 1-6, known risks, prior work, session goals. | Repo folder structure and placeholder READMEs; drafts of `docs/decision-log.md` (D-001 to D-008, AI additions marked **[AI-expanded]**), `docs/risks.md`, `docs/research-question.md` (v0 only), `docs/lit-review/README.md`, `docs/pilot/README.md`; a Socratic opening question on the research question. No code. | |
| 2026-10-20 | Claude Code (Claude Sonnet 5.5) | Request to start licences and citations documentation. | New `docs/licences.md` and `docs/references.md` (all licence entries unverified), plus a deadlines and requirements summary file that I then had deleted as redundant with the PDFs in `UNI-Docs/`; this log restructured to carry prompts and responses. | |
| 2026-11-02 | Claude Code (Claude Sonnet 5.5) | My description of the app concept (flat 2D map, live data on tap, pull-up sheet with arrivals and a 3D view, no accounts) and a request to generate a wireframe. | ASCII wireframes of four screens, plus challenges to my assumptions on whole-network scope, serving data from the logger, estimated train positions and the 3D view. | |
| 2026-11-02 | Claude Code (Claude Opus 5.5) | Request to make a few lo-fi prototypes of the app concept. | Clickable lo-fi mock-ups with fake data, hosted as a private page outside the repo: a first-launch screen and three directions for the map (A quiet map, B typical trains, C line strip). Not project code. | |
| 2026-11-02 | Claude Code (Claude Opus 5.5) | My draft research question ("How accurate are TfL's live train arrival times and can we predict arrival times more accurately than them themselves") and a request to write v1 together. | Critique of the draft, then options for the main question, how to treat stability and how to include the survey. Wording of the v1 question, SQ1 to SQ4 and H1 drafted by the AI. | Chose the options; v1 recorded in `docs/research-question.md`. |
| 2026-11-02 | Claude Code (Claude Opus 5.5) | Requests to confirm whether TrackerNet is still active, to summarise the Unified API response, and to explain how TrackerNet and the Unified API differ. | One-off test requests to both APIs; a summary of the Victoria line response showing `currentLocation` on every record; forum findings and an explanation of the two services. | Decided to drop TrackerNet (D-014). |
