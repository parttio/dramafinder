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

The temp test connects to an **already running** application — it does not
start one. Before anything else:

1. Check whether the app responds (e.g. `curl -sf http://localhost:8080 -o /dev/null`).
2. If not running, start it yourself **in the background** with the project's
   dev profile (e.g. `mvn spring-boot:run` / `./gradlew bootRun` with the
   `local` profile), wait for the port to answer, and remember that you started
   it so you can stop it afterwards.
3. Only then run the verification test. Never let the test itself boot the app
   — keeping the app out of the test run is what makes iterations cheap.

## The loop

1. Ensure the app is running with the required state (see prerequisite above
   and "Set up state cheaply" below).
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

- Connect to the **already running app** — `open("route")` (relative to the
  base URL) or plain Playwright `page.navigate(...)`, no Spring context, no app
  restart.
- Use **DramaFinder locators** for all Vaadin components (grid cells, combo
  boxes, dialogs, notifications) — never hand-rolled shadow-DOM selectors.
- The base class sets a 1920x1080 viewport for you; override per test, and add
  375x812 and 768x1024 passes when the use case has responsive requirements.
- `shot("name")` at every **key interaction point** and each **unique visual
  state** — named descriptively (`01-login`, `02-order-list`,
  `03-submit-dialog`). Screenshots are auto-numbered into the report directory.
- Assert behaviour only lightly (enough to know the flow progressed). Behaviour
  is covered by the browserless tests (pyramid layer 3) — don't duplicate those
  assertions here. Screenshots of unique visual states are the deliverable.

Skeleton (use the host project's base package for the `agent` sub-package):

```java
package com.example.myapp.agent;

import org.junit.jupiter.api.Test;
import org.vaadin.addons.dramafinder.agent.VisualVerificationTest;
import org.vaadin.addons.dramafinder.element.GridElement;

class AgentVerifyIT extends VisualVerificationTest { // AgentReporting extension included

    @Test
    void verifyUseCase() {
        page.navigate(baseUrl() + "/login");
        page.fill("input[name='username']", "employee");
        page.fill("input[name='password']", "secret");
        page.keyboard().press("Enter");            // no shadow-DOM submit hunt

        open("orders");                            // relative to baseUrl()
        shot("01-order-list");
        GridElement.get(page).assertRowCount(12);

        // ...drive the main flow with DramaFinder locators, shot() each state
    }
}
```

## Set up state cheaply

How test state is established is a **saved project preference**, not a hard
rule. On first use, check `CLAUDE.md` for a `## Verification preferences`
section. If it's missing, ask the user once:

> "For visual verification setup: use seeded fixtures + deep links (fast,
> requires seeding infra), or drive the real UI flow (slower, no infra needed)?"

Record the answer in `CLAUDE.md` under `## Verification preferences`
(e.g., `state-setup: seeded` or `state-setup: ui-flow`) and never ask again —
the user can edit the file to change it.

**If `state-setup: seeded`**:
- Run the app with its dev/local profile so its seeder populates labelled
  fixtures on an empty DB; the test logs in and deep-links directly to the
  screen under test — no click-through of create → save → submit just to reach
  a state. Look up fixtures, dev logins, and deep-link routes in the project's
  docs (and record their location in `CLAUDE.md` alongside the preference).

**If `state-setup: ui-flow`**:
- Script the setup clicks inside the same temp test (still one batch run —
  never set up state interactively via MCP). Keep setup steps minimal and
  screenshot only the states under test, not the setup steps.

Regardless of mode:
- **Deep-link with stable selectors** where possible; drive elements by
  DramaFinder locators, button text, `aria-label`, or stable `name` attributes.
- **Batch everything.** All navigation, form filling, and screenshots happen
  inside the single test run — never interleave with manual inspection.
- A use case may override the saved preference when it explicitly tests the
  creation flow itself.

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
