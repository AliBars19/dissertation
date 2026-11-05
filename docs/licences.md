# Licence and reuse register

Handbook 8.3 and 8.6 require that every piece of reused data, code, tool and text has its licence
checked and obeyed, and that reuse is documented in the Method chapter, the Results chapter and
Appendix B. This register is where that evidence is collected from day one.

Status values: `UNVERIFIED` (not yet read at source), `VERIFIED` (read at source, date and URL recorded)

| ID | Item | Used for | Licence / terms | Source URL | Status | Checked on | Obligations (attribution, restrictions, redistribution) | Where documented |
|----|------|----------|-----------------|------------|--------|------------|----------------------------------------------------------|------------------|
| L-01 | TfL Unified API data | Predictions under evaluation; app display | TfL Transport Data Service licence, based on Open Government Licence v2.0 with TfL amendments | https://tfl.gov.uk/corporate/terms-and-conditions/transport-data-service | VERIFIED | 05/11/2026| Credit "Powered by TfL Open Data", no roundel, no implied endorsement. Contains OS data © Crown copyright and database rights 2016' and Geomni UK Map data © and database rights [2019] . The logger data can be published to the repo | Method, Results, App, README |
| L-02 | TfL TrackerNet feed | Not used (D-014) | | | N/A | 05/11/2026 | None | |
| L-03 | TfL API key | Authenticated access, rate limits | TfL Transport Data Service licence, based on Open Government Licence v2.0 with TfL amendments | https://tfl.gov.uk/corporate/terms-and-conditions/transport-data-service | VERIFIED | 05/11/2026 | Rate limited to 500 calls per minute per data feed. Key is used by my logger and app builds, never committed to the repo or published. If it leaks, I replace it. All other rules are that of L-01.  | Method |
| L-04 | Darwin (National Rail) | Not used (D-016) | | | N/A | 05/11/2026 | None | |
| L-05 | xtool | Building and signing the iOS app on Linux | MIT License| https://github.com/xtool-org/xtool/blob/main/LICENSE.md | VERIFIED | 05/11/2026 | Apple Developer Account is already in possession so that only need is met. | Method, Appendix B |
| L-06 | Swift toolchain and standard libraries | App| Apache 2.0 with Runtime Library Exception| https://www.swift.org/legal/license.html | VERIFIED | 05/11/2026 | No specific notices for us to be aware of | Appendix B |
| L-07 | Apple developer / device terms | Installing on own iPhone | Xcode and Apple SDKs Agreement | https://www.apple.com/legal/sla/docs/xcode.pdf| VERIFIED | 05/11/2026 | We currently have an Apple Dveeloper Account so nothing to worry about | Method |
| L-08 | Third-party Swift packages | Anything added later | None at the moment | | N/A | 05/11/2026 | Nothing used at the moment. Add a row before using anything. | Appendix B |
| L-09 | Tutorials, Stack Overflow, docs snippets | Ideas or code adapted by hand | None at the moment | | N/A | 05/11/2026 | Nothing used at the moment. Add a row before using anything. | Appendix B, code comments |
| L-11 | Station positions, stop order, line shapes | App map, drawn as my own schematic | TfL Transport Data Service licence (L-01) | https://api.tfl.gov.uk/Line/victoria/Route/Sequence/inbound | VERIFIED | 05/11/2026 | Data from the Unified API under L-01, including its attribution statements. Layout and icons designed by me. TfL's Tube map design, logos and roundel are not reproduced. | App, Appendix B |
| L-12 | Own work: repo licence | Releasing my logger and app code | MIT License | `LICENSE` in this repo | VERIFIED | 05/11/2026 | Applies to my own code only. Logged TfL data stays under L-01, and third-party material stays under its own terms.| README, Appendix B |
| L-13 | EAS (Expo) | Cloud builds and TestFlight submission (D-015) | Expo Terms of Service | https://expo.dev/terms and https://expo.dev/pricing | VERIFIED | 05/11/2026 | Free plan: up to 15 iOS builds (per month) will only use these builds for testflight, local testing will be done via xtool. No attribution duty found. | Method, Appendix B |
| L-14 | Apple Developer Program | TestFlight distribution to testers (D-015) | Apple Developer Program License Agreement | https://developer.apple.com/support/terms/apple-developer-program-license-agreement/ and https://developer.apple.com/programs/ | VERIFIED | 05/11/2026 | Testflight requires a privacy policy in the app and user consent before collecting user or device data. | Method, LSEPI, App |
| L-15 | Java (Eclipse Temurin JDK 17) | Logger (D-008) | GPL v2 with Classpath Exception (the JDK's `legal/java.base/LICENSE`) | `~/.local/jdk-17/legal/java.base/LICENSE` | VERIFIED | 05/11/2026 | The Classpath Exception lets my own code link to the JDK without taking its licence. My default `java` is Red Hat OpenJDK 25, so confirm which version the logger uses. | Method, Appendix B |
| L-16 | SQLite | Lookup tables (SQLite preferred, D-012) | Public domain | https://www.sqlite.org/copyright.html | VERIFIED | 05/11/2026 |Public use no concerns here | Appendix B |

## Rules for this register
1. Add a row before using anything, not after.
2. Record the exact URL and the date it was read; terms change.
3. If the terms are unclear, ask the consultant and record the answer here.
4. Anything copied, adapted or auto-generated is flagged per file in Appendix B (handbook 8.6).