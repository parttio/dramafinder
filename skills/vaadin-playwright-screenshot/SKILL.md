---
name: vaadin-playwright-screenshot
description: Capture screenshots of a running DramaFinder/Vaadin app by writing a temporary Java/Playwright test that batch-captures every key visual state in one run. Pass the acceptance criteria to screenshot as the argument. Use after implementing UI changes when you need screenshots to review.
---

# Vaadin Playwright Screenshot

> **Invoke this skill via the Skill tool — do not just read this `SKILL.md`.**
> Invoking is what loads this guidance (and anything it references) into
> context.

To capture what the user sees, **do not drive the browser interactively via MCP
by default** — write a temporary DramaFinder test that performs the whole flow
and captures all screenshots in one batch run. One run replaces dozens of MCP
round-trips and avoids accessibility-tree dumps entirely. Then review the
captured images.

## Input: the acceptance criteria to screenshot

This skill takes **acceptance criteria** as its argument — the testable
statements the UI must satisfy. Each criterion tells you *what to screenshot*:
one screenshot that proves it holds. The argument may be either:

- **Inline criteria**, e.g. "Login page shows email + password fields",
  "Invalid login shows an error notification", "After login, the order list is
  visible"; or
- **A reference** to where the criteria live — a spec file
  (`docs/specs/checkout.md`), a ticket ID, a section heading. Read the reference
  and extract the criteria before writing the test.

If nothing is provided, ask for the criteria (or derive them from the spec /
ticket for the change under test) before writing the test — the criteria are
what the capture run must cover, one screenshot each. Map every criterion to at
least one `shot(...)`, and use the criterion text to name the shot.

Unless a criterion specifies otherwise, use a **1920x1080** viewport.

## Prerequisite: the DramaFinder agent helpers must be available

This skill relies on the `org.vaadin.addons.dramafinder.agent` helpers
(`VisualVerificationTest`, `AgentReporting`), which ship inside DramaFinder
**1.1.6 and later**. They're on the classpath of any project that depends on a
recent enough DramaFinder — no per-project setup. If `VisualVerificationTest`
won't resolve, the project is on an older DramaFinder (or doesn't depend on it):
stop and say so rather than hand-rolling an equivalent base class.

## Prerequisite: the application must be running

The temp test connects to an **already running** application — it never boots
the app itself. Keeping the app out of the test run is what makes iterations
cheap. Before running the capture test:

1. Check whether the app already responds on its URL.
2. If it doesn't, start it the way this project runs its app. Start it in the
   background, wait for it to answer, and remember that you started it so you
   can stop it afterwards.

## The capture loop

1. Ensure the app is running with the required state (see prerequisite above
   and "Reaching the state" below).
2. Write the temp test at
   `src/test/java/<project-package>/agent/AgentVerifyIT.java`
   (fixed name, fixed `agent` sub-package — overwrite the previous one, never
   commit it). Extend
   `org.vaadin.addons.dramafinder.agent.VisualVerificationTest`,
   which already wires in the `AgentReporting` extension. Capture one screenshot
   per acceptance criterion.
3. Run only that test with the project's build tool, with quiet output:
    - Maven: `mvn -q surefire:test -Dtest=AgentVerifyIT`
    - Gradle: `./gradlew test --tests '*.AgentVerifyIT' --console=plain -q`
      If a `scripts/agent-verify.*` wrapper exists in the project, prefer it —
      it
      prints only pass/fail, assertion messages, and the report path.
      Point the test at a non-default host/port with
      `-Ddramafinder.agent.baseUrl=http://localhost:9000` (or the
      `DRAMAFINDER_BASE_URL` env var). Run with `-Dheadless=false` to watch it.
4. Read the report directory — `target/agent-report/` (Maven) or
   `build/agent-report/` (Gradle): it holds each numbered screenshot
   (`01-…png`, `02-…png`, …). On failure the `AgentReporting` extension also
   writes `failure.txt` (assertion message + trimmed stack trace + URL),
   `failure.png` (full-page screenshot), and `component-snapshot.txt` (semantic
   component snapshot). Review the captured images against the acceptance
   criteria.
5. Record, per criterion, whether its screenshot confirms it. Delete or
   overwrite the temp test when done.

## Writing the temp test

Write the test as a normal DramaFinder test. For how to locate Vaadin components
and assert on them, **invoke the `vaadin-playwright-test` skill via the Skill
tool** (don't just read its `SKILL.md` — invoking is what loads its bundled API
reference). This skill only adds the visual-capture concerns on top of it:

- Extend `org.vaadin.addons.dramafinder.agent.VisualVerificationTest` (not the
  usual base class) — it connects to the already-running app, wires in the
  `AgentReporting` extension, and sets a 1920x1080 viewport. Override the
  viewport per test, and add 375x812 and 768x1024 passes when a criterion has
  responsive requirements.
- For the exact API of `VisualVerificationTest` and the other agent helpers
  (`open`, `shot`, `baseUrl`, the report/snapshot types), read the bundled
  **`agent-api-reference.md`** next to this file — **do not unzip the DramaFinder
  sources jar**. In a consumer project where the skill isn't checked out, fetch
  it (one request) from
  `https://raw.githubusercontent.com/parttio/dramafinder/master/skills/vaadin-playwright-screenshot/agent-api-reference.md`.
- `shot("name")` for each acceptance criterion and each **unique visual state**
  along the way — named after the criterion (`01-login-fields`,
  `02-invalid-login-error`, `03-order-list`). Screenshots are auto-numbered into
  the report directory.
- Assert behavior only lightly (enough to know the flow progressed). Behaviour
  is covered by the behavioral tests (pyramid layer 3) — don't duplicate those
  assertions here. Screenshots that prove each criterion are the deliverable.

## Reaching the state

- **Deep-link with stable selectors** where possible; drive elements by
  DramaFinder locators, button text, `aria-label`, or stable `name` attributes.
- If a criterion needs data or a logged-in user, script the minimal setup steps
  inside the same temp test — still one batch run, never set up state
  interactively via MCP. Screenshot only the states the criteria call for, not
  the setup steps.

## Steps

All steps must be done; thoroughness over speed.

1. Acceptance criteria in hand (provided as the argument, or gathered from the
   spec/ticket)
2. App running (started by you if needed) with the required state
3. Temp test reaches the state each criterion describes and captures one
   screenshot per criterion (plus any unique intermediate states)
4. Screenshots reviewed against the acceptance criteria
5. Results recorded — note, per criterion, pass/fail and any visual issues
