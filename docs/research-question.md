# Research question

This file keeps every version of the research question and the reason it changed.

## v0 (2026-10-13 draft)
> How accurate and how stable are TfL's live Tube arrival predictions, how does their error
> vary with prediction horizon, line, time of day and service status, and how reliably can
> actual arrivals be inferred from open data?


## v1 (2026-11-02)
> How accurate and stable are TfL's live Tube arrival predictions, and can a lookup-table
> correction built from logged open data reduce their error?

Wording drafted with Claude Code from my ideas, and I chose it from options (see
`docs/ai-use-log.md` and D-013).

### Sub-questions
- SQ1: How reliably can actual Tube arrival times be inferred from the position text in TfL's Unified API, when
  checked against hand-timed observations and, where lines overlap, Darwin?
- SQ2: How do the error and stability of TfL's predictions vary with line, time
  of day and service status?
- SQ3: Does a lookup-table correction built from earlier logged data reduce prediction error on
  later held-out data, compared with TfL's original predictions?
- SQ4: How do London commuters who try the app rate its usefulness, and how far do they trust
  corrected times shown alongside TfL's?

### Change since first writing v1
SQ1 originally named TrackerNet as the position source. Changed the same day after I dropped it (D-014).

### Hypothesis
- H1 (for SQ3): The correction reduces mean absolute error on held-out data, compared with TfL's
  original predictions.

## Definitions to pin down (each one becomes a decision in the log)
- [ ] "Accurate": which error measure? Signed or absolute? Per prediction or per train?
- [ ] "Stable": how is wobble measured between successive predictions for the same train?
- [ ] "Prediction horizon": minutes-to-arrival at the time the prediction was made?
- [ ] "Actual arrival": the inferred event from `currentLocation` (D-002, D-014), with what precision?
- [ ] "Service status": which source, and at what granularity?
