# Licence and reuse register

Handbook 8.3 and 8.6 require that every piece of reused data, code, tool and text has its licence
checked and obeyed, and that reuse is documented in the Method chapter, the Results chapter and
Appendix B. This register is where that evidence is collected from day one.

Status values: `UNVERIFIED` (not yet read at source), `VERIFIED` (read at source, date and URL
recorded), `N/A`. Nothing below is verified yet. Entries say what to check, not what the answer is.

| ID | Item | Used for | Licence / terms | Source URL | Status | Checked on | Obligations (attribution, restrictions, redistribution) | Where documented |
|----|------|----------|-----------------|------------|--------|------------|----------------------------------------------------------|------------------|
| L-01 | TfL Unified API data | Predictions under evaluation; app display | | | UNVERIFIED | | Brief says: credit "Powered by TfL Open Data", no roundel, no implied endorsement. Check: may the raw logged data be republished or committed to a public repo? Any caching or retention limits? | Method, Results, App, README |
| L-02 | TfL TrackerNet feed | Train positions for inferring actual arrivals | | | UNVERIFIED | | Check: is the feed covered by the same terms as L-01? Key terms? Is it documented as supported or deprecated? | Method, Results |
| L-03 | TfL API key | Authenticated access, rate limits | | | UNVERIFIED | | Check: registration terms, rate limits, rules on sharing keys.  | Method |
| L-04 | Darwin (National Rail) | Validating inferred actuals where lines overlap | | | UNVERIFIED | | Check: how access is obtained, whether use for a student project is allowed, attribution wording, redistribution of derived data | Method, Results |
| L-05 | xtool | Building and signing the iOS app on Linux | | | UNVERIFIED | | Check: licence of the tool itself; what it needs from Apple (account type, signing, expiry of installed builds) | Method, Appendix B |
| L-06 | Swift toolchain and standard libraries | App, and logger (may or may not be swift)| | | UNVERIFIED | | Check: licence text and any runtime library notices | Appendix B |
| L-07 | Apple developer / device terms | Installing on own iPhone | | | UNVERIFIED | | Check: free vs paid account limits and terms | Method |
| L-08 | Third-party Swift packages | Anything added later | | | UNVERIFIED | | One row per package: name, version, licence, what it does, which of my files use it | Appendix B |
| L-09 | Tutorials, Stack Overflow, docs snippets | Ideas or code adapted by hand | | | UNVERIFIED | | One row per source: URL, date accessed, what was adapted, which file. Handbook 8.6 asks for this in Appendix B. | Appendix B, code comments |
| L-10 | Raschke "Trackernet Stringlines" (2025) | Method ideas; any figures or code | | | UNVERIFIED | | If any figure, code or data is reproduced: permission or licence needed (handbook 8.3, 8.5). Citing is not permission. | Context, Method |
| L-11 | Map tiles / MapKit / station coordinates | App map | | | UNVERIFIED | | Check: source of station locations and line geometry, licence and attribution requirements | App, Appendix B |
| L-12 | Own work: repo licence | Releasing logger, app and analysis | | | UNVERIFIED | | Decide: licence for own code; check City's IP rules (handbook 2.9: University may copy submissions for assessment) | README |

## Rules for this register
1. Add a row before using anything, not after.
2. Record the exact URL and the date it was read; terms change.
3. If the terms are unclear, ask the consultant and record the answer here.
4. Anything copied, adapted or auto-generated is flagged per file in Appendix B (handbook 8.6).
