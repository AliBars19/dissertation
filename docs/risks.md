# Risk register

Likelihood (L) and impact (I) are 1 (low) to 3 (high). Mitigations marked _TBD_ are open work.

| ID | Risk | L | I | Mitigation | Status |
|----|------|---|---|------------|--------|
| R-01 | Joining Unified API and TrackerNet trains fails: IDs do not obviously match, `vehicleId` is unreliable on some lines, trip numbers are reused when trains reverse at termini (Raschke 2025). | 3 | 3 | Pilot on one line before committing (see `docs/pilot/`); measure join rate. | Open |
| R-02 | TrackerNet is a legacy feed and could be retired mid-project. | 2 | 3 | Start logging early; archive raw responses; _TBD fallback_. | Open |
| R-03 | API outages (incl. the Sept 2024 cyberattack), no SLA, no status page. | 2 | 2 | Logger records gaps, stale timestamps, empty responses and its own uptime; outages become data, not silent holes. | Open |
| R-04 | Poll interval limits ground-truth precision (~30 to 45 s). Rate limit 500 calls/min. | 3 | 2 | Quantify precision in the method; validate with stopwatch sample (D-002). | Open |
| R-05 | Licence breach: missing "Powered by TfL Open Data", roundel use, or implied endorsement. | 1 | 2 | Attribution in app and README; no TfL branding. | Open |
| R-06 | xtool toolchain or signing problems on Fedora block app work. | 2 | 2 | Early hello-world-on-iPhone spike. | Open |
| R-07 | Data collection starts late, leaving too little data. Data collection is on the critical path. | 2 | 3 | Pilot first, then start the main logger as early as possible; Gantt chart. | Open |
