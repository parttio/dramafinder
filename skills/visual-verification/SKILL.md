---
name: visual-verification
description: Visually verify an implemented use case by writing a temporary DramaFinder test (Java/Playwright) against the running app that batch-captures screenshots, then reviewing the screenshots. Use after implementing UI changes. Escalate to Playwright MCP only if the report is insufficient to diagnose a problem.
---

# Visual Verification

Verify what the user sees. The screenshot is the ground truth; DOM and CSS are
helpers. But **do not drive the browser interactively via MCP by default** —
write a temporary DramaFinder test that performs the whole flow and captures all
screenshots in one batch run, then review the images. One run replaces dozens of
MCP round-trips and avoids accessibility-tree dumps entirely.

Unless the use case specifies otherwise, use a **1920x1080** viewport.

## Prerequisite: the application must be running

The temp test connects to an **already running** application — it never boots
the app itself. Keeping the app out of the test run is what makes iterations
cheap. Before running the verification test:

1. Check whether the app already responds on its URL.
2. If it doesn't, start it the way this project runs its app — the project
   knows how (consult its README / `CLAUDE.md` / `AGENTS.md`). Start it in the
   background, wait for it to answer, and remember that you started it so you
   can stop it afterwards.

## The loop

1. Ensure the app is running with the required state (see prerequisite above
   and "Reaching the state" below).
2. Write the temp test at
   `src/test/java/<project-package>/agent/AgentVerifyIT.java`
   (fixed name, fixed `agent` sub-package — overwrite the previous one, never
   commit it). Extend `org.vaadin.addons.dramafinder.agent.VisualVerificationTest`,
   which already wires in the `AgentReporting` extension.
3. Run only that test with the project's build tool, with quiet output:
   - Maven: `mvn -q surefire:test -Dtest=AgentVerifyIT`
   - Gradle: `./gradlew test --tests '*.AgentVerifyIT' --console=plain -q`
   If a `scripts/agent-verify.*` wrapper exists in the project, prefer it — it
   prints only pass/fail, assertion messages, and the report path.
   Point the test at a non-default host/port with
   `-Ddramafinder.agent.baseUrl=http://localhost:9000` (or the
   `DRAMAFINDER_BASE_URL` env var). Run with `-Dheadless=false` to watch it.
4. Read the report directory — `target/agent-report/` (Maven) or
   `build/agent-report/` (Gradle): view each numbered screenshot
   (`01-…png`, `02-…png`, …) and apply the visual validation rules below. On
   failure the `AgentReporting` extension also writes `failure.txt` (assertion
   message + trimmed stack trace + URL), `failure.png` (full-page screenshot),
   and `component-snapshot.txt` (semantic component snapshot).
5. Record results in the per-use-case checklist. Delete or overwrite the temp
   test when done.

## Writing the temp test

Write the test as a normal DramaFinder test — for how to locate Vaadin
components and assert on them, use the **vaadin-playwright-test** skill. This
skill only adds the visual-capture concerns on top of it:

- Extend `org.vaadin.addons.dramafinder.agent.VisualVerificationTest`, which
  connects to the already-running app and wires in the `AgentReporting`
  extension. It never boots the app or a Spring context.
- Navigate with `open("route")` (relative to the base URL) or plain Playwright
  `page.navigate(...)`. The base URL defaults to `http://localhost:8080` and is
  overridable via `-Ddramafinder.agent.baseUrl=...` or `DRAMAFINDER_BASE_URL`.
- Use **DramaFinder locators** for all Vaadin components — never hand-rolled
  shadow-DOM selectors.
- The base class sets a 1920x1080 viewport for you; override per test, and add
  375x812 and 768x1024 passes when the use case has responsive requirements.
- `shot("name")` at every **key interaction point** and each **unique visual
  state** — named descriptively (`01-login`, `02-order-list`,
  `03-submit-dialog`). Screenshots are auto-numbered into the report directory.
- Assert behaviour only lightly (enough to know the flow progressed). Behaviour
  is covered by the browserless tests (pyramid layer 3) — don't duplicate those
  assertions here. Screenshots of unique visual states are the deliverable.

## Reaching the state

- **Deep-link with stable selectors** where possible; drive elements by
  DramaFinder locators, button text, `aria-label`, or stable `name` attributes.
- If the screen under test needs data or a logged-in user, script the minimal
  setup steps inside the same temp test — still one batch run, never set up
  state interactively via MCP. Screenshot only the states under test, not the
  setup steps.

## Validating visual appearance

Review every screenshot in the report directory against:

1. Layout matches expectations (spacing, alignment, sizing)
2. Spacing & padding are consistent — appropriate breathing room, no cramped or
   excessively spaced areas. Nested layouts (AppLayout > VerticalLayout > card)
   don't double-up or collapse padding. Similar views (e.g., all admin views)
   share the same content padding.
3. Typography is readable and consistent
4. Interactive elements are clearly identifiable
5. Responsive behaviour works at common breakpoints when required by the use
   case (mobile, tablet, desktop)
6. Text contrast and readability
   - All text clearly readable against its background (titles, labels, values,
     badges)
   - Colored text (warning/error values, status badges) has sufficient contrast
   - Elements inheriting a different color scheme (dark sidebar vs light
     content) render correctly — CSS custom properties like
     `var(--vaadin-background-color)` may resolve differently per inherited
     scheme
   - No backgrounds swallow their content text

## Escalation — Playwright MCP as last resort

Use the MCP **only** when the batch run cannot answer the question:

- a failure isn't diagnosable from `failure.txt` + screenshots + the semantic
  snapshot, or
- the state is genuinely exploratory (unknown UI, need to poke around
  interactively).

When escalating, start from the evidence the report already produced (open the
failing route directly, don't replay the whole flow), scope any
`browser_snapshot` you take (`depth`, `filename`), and return to the batch loop
as soon as the cause is understood.

## Steps (per use case)

All steps must be done; thoroughness over speed.

1. App running (started by you if needed) with the required state
2. Temp test navigates every route in the use case's UI/Routes section
3. Temp test performs each step of the main flow
4. Screenshots captured at key interaction points and unique visual states
5. Screenshots validated against the rules above
6. Results recorded — note any visual issues in the per-use-case checklist
