# Debugging failed tests

## Read the failure report

Every failing test that extends `AbstractBasePlaywrightIT` (or
`SpringPlaywrightIT`) writes a report to
`target/agent-report/<TestClass>/<testMethod>/` (`build/agent-report/` under
Gradle):

| File | Contents |
|---|---|
| `failure.txt` | the assertion message, a trimmed stack trace and the page URL |
| `failure.png` | a full-page screenshot at the moment of failure |
| `component-snapshot.txt` | the Vaadin components on the page with their labels, values and states |
| `view-outline.txt` | each component with a DramaFinder lookup that reaches it, verified against the page |

Start with `component-snapshot.txt`: it usually shows at a glance that the
label is `"E-mail"` rather than `"Email"`, or that the field holds the value but
is still `invalid`. Turn the report off with `-Ddramafinder.agentReport=false`.

## A lookup that finds nothing or too much

| Symptom | Likely cause |
|---|---|
| `Timeout ... waiting for locator(...)` | the label is not the whole visible label: case, punctuation, trailing `*`... Copy it from `view-outline.txt`, or use `getByLabelContaining` |
| `strict mode violation: ... resolved to 2 elements` | two components match. Scope the lookup to a container, use an id, or `find(..)` with more filters |
| `getByLabel` finds nothing on a field named by `setAriaLabel` | a label is the **visible** label: use `find(page, X.class).withAccessibleName(..)` |
| a field found by its placeholder | use `find(page, X.class).withPlaceholder(..)` |

Generate the outline of a view on demand, in a throwaway test:

```java
@Test
void outline() {
    ButtonElement.getByText(page, "New contact").click(); // open what you need first
    ViewOutline.write(page);                             // target/agent-report/view-outline.txt
}
```

## Watch the browser

```bash
mvn verify -Dit.test=ContactViewIT -Dheadless=false
```

Add `page.pause()` in the test to stop there with the Playwright inspector open:
from it you can step through the remaining actions and try locators against the
live page.

## Record a trace

A Playwright trace records every action with DOM snapshots, network and console:

```java
@BeforeEach
void startTracing() {
    page.context().tracing().start(new Tracing.StartOptions()
            .setScreenshots(true).setSnapshots(true).setSources(true));
}

@AfterEach
void stopTracing(TestInfo info) {
    page.context().tracing().stop(new Tracing.StopOptions()
            .setPath(Paths.get("target/traces/" + info.getDisplayName() + ".zip")));
}
```

```bash
mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI \
  -Dexec.args="show-trace target/traces/sendShowsAThankYouNotification().zip"
```

!!! note

    JUnit runs your `@AfterEach` before the base class closes the page, so the
    trace is complete.

## Catch client-side errors

A JavaScript error in the browser — a broken custom component, a failing
`executeJs` — does not fail a test on its own. Track them and assert there are
none:

```java
import org.vaadin.addons.dramafinder.ClientSideErrors;

ClientSideErrors errors;

@BeforeEach
void trackErrors() {
    errors = ClientSideErrors.track(page);
}

@AfterEach
void noClientSideErrors() {
    errors.assertNone();
}
```

`getErrors()` lists the `console.error` messages and uncaught exceptions
recorded so far; `clear()` forgets them after asserting an expected one;
`stop()` stops recording.

## Flaky tests

A test that sometimes fails almost always reads state too early:

- replace `assertEquals(x, element.getX())` with `element.assertX(x)` — see
  [Assertions](assertions.md);
- replace `Thread.sleep(..)` with the assertion of the state you waited for;
- a raw Playwright action (`locator.press("Enter")`) does not wait for the
  server: follow it with `waitForVaadinIdle()` — see
  [Actions and waiting](actions-and-waiting.md);
- a `GridElement.RowElement` or `CellElement` is tied to a rendered row; find it
  again after scrolling to another row.
