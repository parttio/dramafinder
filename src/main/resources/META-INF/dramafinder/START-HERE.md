# DramaFinder — start here

Playwright element wrappers for Vaadin. This file and the full API reference ship
**inside the jar**, so they are readable with no network:

```
JAR=$(ls ~/.m2/repository/org/vaadin/addons/dramafinder/*/dramafinder-*.jar | tail -1)
unzip -p "$JAR" META-INF/dramafinder/START-HERE.md
unzip -p "$JAR" META-INF/dramafinder/api-reference.md      # every signature, ~100 KB
unzip -p "$JAR" META-INF/dramafinder/agent-api-reference.md # screenshot/report helpers
```

**Read those instead of running `javap` on the classes.** The reference is generated
from source on every release, carries the Javadoc one-liners and the real parameter
names, and cannot drift. Decompiled signatures have neither, and the two traps below
are invisible in them.

## A working test

```java
// Spring Boot app: the base class starts it on a random port and builds getUrl()
public class MyViewIT extends org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT {

    @Override
    public String getView() {
        return "my-view"; // appended to getUrl() before each test
    }

    @Test
    public void filtersTheGrid() {
        TextFieldElement.getByLabel(page, "Name").setValue("ada");
        GridElement.get(page).assertRowCount(1);
    }
}
```

Without Spring Boot, extend `org.vaadin.addons.dramafinder.AbstractBasePlaywrightIT`
and override `getUrl()` with the running application's address. `SpringPlaywrightIT`
needs `spring-boot-starter-test`, which a Spring Boot project already has; DramaFinder
itself never adds Spring to the classpath.

With Spring Security, don't log in through the login view: annotate the test method or
class with Spring Security Test's `@WithMockUser(username = "alice", roles = "ADMIN")`
(or `@WithUserDetails("alice")`) and the page starts signed in. `loginAs("bob", "USER")`
switches user mid-test; `newPageAs("bob")` returns a second user's page on the same
view. Needs `spring-security-test` in test scope.

`AbstractBasePlaywrightIT` opens the `page` field before each test, navigates it to
`getUrl() + getView()`, waits for Vaadin to go idle, and closes it after. The browser
is headless unless the `headless` system property or the `HEADLESS` environment
variable says otherwise. Playwright's browser binaries must already be installed.

To go to another view, navigate by its route class rather than a hand-built URL, so
renaming a route cannot break the test silently: `navigate(ProfileView.class)`,
`navigate(ArticleView.class, 42)` for a `HasUrlParameter`, or
`navigate(UserEditView.class, new RouteParameters("userId", "42"))` for a template
route. `assertAt(ProfileView.class)`, `assertPath("profile")` and
`assertQueryParameter("tab", "2")` retry until the browser gets there, e.g. after a
login redirect.

## Finding the wrapper for a component

A component's tag maps to its wrapper by name: `<vaadin-text-field>` →
`TextFieldElement`, `<vaadin-multi-select-combo-box>` → `MultiSelectComboBoxElement`.
All of them live in `org.vaadin.addons.dramafinder.element`.

To look one up in the reference:

```
unzip -p "$JAR" META-INF/dramafinder/api-reference.md > /tmp/dramafinder-api.md

grep -n 'vaadin-multi-select-combo-box' /tmp/dramafinder-api.md   # tag → wrapper + factories
sed -n '/^### GridElement$/,/^### /p' /tmp/dramafinder-api.md     # one element's whole API
```

The reference opens with an index table of every tag, its wrapper, and that wrapper's
factory methods; then a section per element; then the shared mixins whose methods
every element that implements them inherits.

**Let the page tell you.** `org.vaadin.addons.dramafinder.agent.ViewOutline.write(page)`
in a throwaway test writes `target/agent-report/view-outline.txt`: every Vaadin
component on the page with the factory call that reaches it, each one verified
against the live page, plus the full labels of container items (tabs, side-nav
items, checkboxes of a group). Copy lookups from it rather than guessing labels.

## Four things that are easy to get wrong

**Factory methods are not uniform.** Most fields have `getByLabel(Page, String)`,
containers have `get(Page)`, and almost every element has `getById` and `getByTestId`
(fallbacks for a component without a user-visible name; Flow sets the test id with
`setTestId`). An
element has exactly the factories the index table lists for it — assuming one that
isn't listed is the most common way to lose a compile round-trip. Every wrapper is
also constructible from a `Locator`: `new TextFieldElement(someLocator)`. For one
grammar on every element, use the typed query builder
(`find(page, TextFieldElement.class).withLabel("Name").single()`, see "Typed queries"
in the reference): it only offers the filters that fit the component, and
`element.find(X.class)` searches inside a dialog or layout.

**A grid cell has two locators.** `getTableCellLocator()` is the `<td>`; the rendered
content lives in a `<vaadin-grid-cell-content>` elsewhere in the DOM, reachable via
`getCellContentLocator()`. `getLocator()`, inherited from `VaadinElement`, is the grid
itself, not the cell. For text, prefer `CellElement.getText()` / `assertText(String)`,
or `GridElement.assertCellContent(row, column, expected)`.

**A label is matched in full.** Every label- or name-keyed lookup —
`TextFieldElement.getByLabel(page, "Name")`, `ButtonElement.getByText(page, "Save")`,
`DialogElement.getByHeaderText(page, "Delete?")`, and the item lookups such as
`group.getCheckbox("Cheese")` or `tabs.getTab("Details")` — matches the whole label,
case-sensitively, and raises a strict-mode error if two components share it. Passing
part of a label finds nothing: use `getByLabelContaining(page, "Name")` (or
`ButtonElement.getByTextContaining`) for a case-insensitive partial match, which also
fails rather than guessing when several components match. A label is the
**visible** label: `getByLabel` never matches an `aria-label` or a placeholder. Find
those with the query builder, `find(page, TextFieldElement.class).withAccessibleName("Search")`
or `.withPlaceholder("Street")`; components without a visible label (menu bar,
list box, popover, breadcrumbs) are found by accessible name the same way.

**Assertions retry; getters do not.** Every `assertX` method polls via
`page.waitForCondition(...)`, so it tolerates a value the client has not rendered yet.
A bare `getX()` compared with `assertEquals` does not, and is flaky against a lazily
loaded grid or a debounced field. Reach for the `assertX` method. Element actions
(`click`, `setValue`, `selectItem`, …) do wait for the Vaadin server round-trip they
trigger, so the server's response has arrived when they return; to observe a state
while the request is still in flight (a loading indicator), wrap the action in
`AutoWait.withoutWaiting(() -> ...)`.

## When a test fails

Every failing test built on `AbstractBasePlaywrightIT` writes a report to
`target/agent-report/<TestClass>/<testMethod>/` (`build/` under Gradle):
`failure.txt` (assertion message, trimmed stack trace, URL), `failure.png`,
`component-snapshot.txt` listing the Vaadin components on the page with their labels
and values, and `view-outline.txt` with a verified lookup for each of them. Read it
before editing the test. Turn it off with
`-Ddramafinder.agentReport=false`.

## More

- Full API reference: `META-INF/dramafinder/api-reference.md` (in this jar)
- Upgrading tests written for 1.x: `META-INF/dramafinder/MIGRATION-2.0.md` (in this jar)
- Source and issues: https://github.com/parttio/dramafinder
- Claude Code skills: `/plugin marketplace add parttio/dramafinder`
