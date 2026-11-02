# Questions for my consultant (Vahid Rafe)

EVERYTHING BELOW IS WRITTEN BY AI FOLLOWING THE IDEATION SESSION ON 27.10.26

Written 2026-10-27, before the PDD deadline (Wed 11 Nov 2026, 20:00). I record the answers here,
with the date and how I got them (meeting or email), and then log any resulting decision in
`docs/decision-log.md`. Anything agreed in a meeting also goes into my meeting notes.

## A. Ethics and approval (blocks the PDD)
- [ ] A1. My core evaluation uses public TfL feeds and my own stopwatch timing, with no human
      participants. Does that qualify as the low-risk route (D-004)?
- [ ] A2. If I am the only "participant" (for example, I test the app myself), is an ethics
      section still needed in the PDD? I understand the LSEPI section is mandatory either way.
      How much depth do you expect for a project like this?
- [ ] A3. Would a small survey of commuters change the ethics route? What approval and materials
      would that need, and by when (final ethics materials are due 21 Feb 2027)?
- [ ] A4. Hand-timing trains on station platforms: does that need any ethics or station
      permission, as long as I film or photograph nobody?
- [ ] A5. Is the pilot logger (public data, no participants) allowed to run before the PDD and
      ethics form are approved, or must I wait for approval?

## B. Project scope and marking
- [ ] B1. The project is a measurement study plus an app. Does this satisfy the "substantial
      design-and-build" rule, and is the app/logger build weight enough?
- [ ] B2. Is a research question of the form "can a lookup-table correction built from the
      study's own data make TfL's predictions more accurate and more stable?" specific and
      challenging enough for a BSc project? Should it be a hypothesis instead?
- [ ] B3. How should I show that commuters benefited when I cannot observe their behaviour? Is an
      objective measure (reduced prediction error for a stated line and horizon) acceptable as
      the test, with behavioural benefit stated as not measured?
- [ ] B4. Which lines and how many should the main study cover, given about 20 weeks between PDD
      submission and the final submission?

## C. Data
- [ ] C1. Is it acceptable to use historical or third-party archived data instead of logging my
      own, if it exists? (I am researching whether such data exists.)
- [ ] C2. The ground truth is inferred from TrackerNet position data and validated with a small
      stopwatch sample and, where lines overlap, Darwin. Is this validation plan sufficient?
- [ ] C3. May I commit logged data to the public repo, subject to the TfL terms?

## D. Naming, licences and AI
- [ ] D1. I want to name the app. Avoiding "TfL" in the name, do you see any issue with the
      project title and the app name being different?
- [ ] D2. My code is written entirely by hand and AI is used only for early planning and
      documentation. Is the Use of AI appendix as I plan it (prompts, summaries, what I did with
      each) sufficient?
- [ ] D3. Do you want the repo access and meeting log in a particular format?

## E. Process
- [ ] E1. Will you review a draft PDD before 11 Nov, and when?
- [ ] E2. Is there a schedule of group and one-to-one meetings I should plan around?
- [ ] E3. Can a Gantt chart in an appendix, with the choices described in the main text, count
      towards the work plan section?

## F. Survey, testers, data and LSEPI (added 2026-10-31)
A1 and A3 above assume no participants. I now plan a small commuter survey as a fully planned
output, so these questions replace that assumption and should be asked together with A1 and A3.
- [ ] F1. The survey would involve commuters trying the app. Can ethics approval for this stay at
      consultant level (low risk), and which materials must you approve before I recruit
      (participant information sheet, consent form, questionnaire)?
- [ ] F2. How should I recruit, and are there limits on who may take part (adults only, friends and
      course mates, numbers)?
- [ ] F3. I build the app on Linux with xtool. I believe getting it onto other people's iPhones
      needs a paid Apple developer account (for example for TestFlight), and that free accounts
      limit devices and how long a build lasts, but I have not verified this. Does City offer a
      developer account or another route? If not, would a recorded demo or screenshots shown to
      participants be acceptable instead of installs?
- [ ] F4. Survey answers are personal data. What storage, anonymisation and retention does City
      expect under UK GDPR?
- [ ] F5. The app reads the user's location live on the device and never stores or sends it. Does
      that still count as personal data processing that I must address in the PDD?
- [ ] F6. May I commit the raw logged TfL data to the public repo, if the TfL licence allows
      redistribution? If TfL later changes its terms, is it enough that the data was collected
      under the earlier terms, or should I publish only derived aggregates?
- [ ] F7. The app will show a one-time first-launch warning that it is a research prototype. Is
      that enough protection against users over-trusting a corrected arrival time, given the
      handbook's point about naive users?

## Answers
| Date | Question | Answer | How (meeting / email) | Decision logged |
|------|----------|--------|-----------------------|-----------------|
