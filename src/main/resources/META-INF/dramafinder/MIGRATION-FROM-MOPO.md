# Migrating from Mopo to DramaFinder

[Mopo](https://github.com/viritin/mopo) (`in.virit:mopo`) is a small set of
Playwright helpers for Vaadin: `Mopo`, `GridPw`, `ComboBoxPw`, `DatePickerPw` and
`DateTimePickerPw`. Each wraps a `Locator` you find yourself. DramaFinder covers
everything Mopo does and adds a typed wrapper for every Vaadin component, lookups
by visible label, and retrying assertions. This guide maps each Mopo call to its
DramaFinder equivalent.

## 1. Dependency

Replace the Mopo dependency:

```xml
<dependency>
    <groupId>org.vaadin.addons</groupId>
    <artifactId>dramafinder</artifactId>
    <version>2.0.1</version> <!-- or newer -->
    <scope>test</scope>
</dependency>
```

DramaFinder brings Playwright in transitively. Spring Boot and Vaadin are
`provided`: it uses the ones your project already has.

## 2. Test class

Mopo has no base class, so a Mopo test usually creates its own `Playwright`,
`Browser` and `Page` and keeps a `Mopo` instance next to them. Extend
`SpringPlaywrightIT` instead (or `AbstractBasePlaywrightIT` without Spring Boot,
overriding `getUrl()`); it starts the app on a random port, opens a fresh `page`
for each test and closes it again:

```java
// Before (Mopo)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class AddressFormIT {
    static Playwright playwright = Playwright.create();
    Browser browser = playwright.chromium().launch();
    Page page = browser.newPage();
    Mopo mopo = new Mopo(page);

    @Test
    public void savesAddress() {
        page.navigate("http://localhost:8080/address");
        page.getByLabel("Street").fill("Main St 1");
        mopo.click(page.getByText("Save"));
        assertThat(page.getByText("Saved")).isVisible();
    }
}

// After (DramaFinder)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AddressFormIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "address";
    }

    @Test
    public void savesAddress() {
        TextFieldElement.getByLabel(page, "Street").setValue("Main St 1");
        ButtonElement.getByText(page, "Save").click();
        assertThat(page.getByText("Saved")).isVisible();
    }
}
```

`navigate(AddressView.class)` opens another view by its route class, and
`assertAt(AddressView.class)` checks where the browser is.

## 3. Waiting for the server

| Mopo | DramaFinder |
|---|---|
| `mopo.click(locator)` / `mopo.click(selector)` | `ButtonElement.getByText(page, "Save").click()` — every element action waits for the round-trip it triggers |
| `Mopo.waitForConnectionToSettle(page)` / `mopo.waitForConnectionToSettle()` | `AutoWait.waitForVaadinIdle(page)` |
| raw `locator.click()` followed by `waitForConnectionToSettle` | `locator.click(); AutoWait.afterAction(page);` |

Every DramaFinder action (`click`, `setValue`, `selectItem`, `check`, …) returns
once the Vaadin server round-trip it triggered has finished, so the explicit
`mopo.click(...)` wrappers disappear. Mopo always slept at least 500 ms before
checking; DramaFinder waits only as long as a request is in flight. To look at
the page *while* a request runs (a spinner, a disabled button), wrap the action
in `AutoWait.withoutWaiting(() -> ...)`.

Client-side rendering after the response (an overlay opening, a grid
re-rendering) is not a server round-trip: assert it with a retrying assertion
such as `assertThat(locator).isVisible()` or a DramaFinder `assert…` method.

## 4. Browser errors

| Mopo | DramaFinder |
|---|---|
| `mopo.trackClientSideErrors()` | `ClientSideErrors errors = ClientSideErrors.track(page);` |
| `mopo.getClientSideErrors()` | `errors.getErrors()` |
| `mopo.failOnClientSideErrors()` | `errors.assertNone()` (throws `AssertionError`, not `RuntimeException`) |
| `Mopo.assertNoJsErrors(page)` (deprecated, dev mode only) | `ClientSideErrors.track(page)` … `errors.assertNone()` |

`ClientSideErrors` records `console.error` messages and uncaught exceptions from
the moment `track` is called; unlike `trackClientSideErrors()` it does not sleep
for a second first. `clear()` forgets what was recorded, `stop()` stops
recording. To check every test, track in an overridden `setupTest()` (after
`super.setupTest()`) and assert in `cleanupTest()` (before `super.cleanupTest()`).

## 5. Other `Mopo` helpers

- **`mopo.driveIn(locator, task)`** has no direct equivalent; write the three
  steps out, which also lets you use the component's own assertions:
  ```java
  DialogElement dialog = DialogElement.getByHeaderText(page, "Edit address");
  dialog.assertOpen();
  TextFieldElement.getByLabel(dialog.getLocator(), "Street").setValue("Main St 1");
  ButtonElement.getByText(dialog.getLocator(), "Save").click();
  dialog.assertClosed();
  ```
  For any other component, `assertThat(locator).isVisible()` before and
  `assertThat(locator).isHidden()` after does what `driveIn` did.
- **`mopo.getViewsReportedByDevMode(browser, url)`** has no equivalent: it scrapes
  the dev-mode "route not found" page. List the views to visit explicitly, by
  route class: `navigate(MyView.class)`, or `RouteNavigation.pathOf(MyView.class)`
  for the URL.

## 6. Finding components

Mopo wraps a locator you build; DramaFinder finds the component for you:

```java
// Mopo
GridPw grid = new GridPw(page.locator("#people"));
ComboBoxPw country = new ComboBoxPw(page.getByLabel("Country"));

// DramaFinder
GridElement grid = GridElement.getById(page, "people");
ComboBoxElement country = ComboBoxElement.getByLabel(page, "Country");
```

Every element also has a `new XElement(locator)` constructor, so an existing
locator keeps working during the migration. Label lookups match the **whole**
visible label, case-sensitively, and fail on duplicates instead of picking the
first one; use `getByLabelContaining` to match part of a label. Scope a lookup to a
container by passing a `Locator` instead of the `Page`, or with
`dialog.find(ButtonElement.class).withText("Save").single()`.

## 7. `GridPw` → `GridElement`

| Mopo | DramaFinder |
|---|---|
| `new GridPw(page)` | `GridElement.get(page)` |
| `new GridPw(locator)` | `new GridElement(locator)`, or `GridElement.getById(page, id)` |
| `getRenderedRowCount()` | `getRenderedRowCount()` |
| `getFirstVisibleRowIndex()` / `getLastVisibleRowIndex()` | same names; `-1` when no row is in view. Assert with `assertFirstVisibleRow(i)` / `assertLastVisibleRow(i)` |
| `isRowInView(i)` | `isRowInView(i)`, or `assertRowInView(i)` / `assertRowNotInView(i)` |
| `scrollToIndex(i)` | `scrollToRow(i)` (also `scrollToStart()`, `scrollToEnd()`) |
| `selectRow(i)` / `getRow(i).select()` | `select(i)` / `findRow(i).orElseThrow().select()` |
| `getRow(i)` | `findRow(i).orElseThrow()` — scrolls the row into the DOM when needed |
| `getRow(i).getCell(col)` | `findCell(i, col).orElseThrow().getCellContentLocator()` |
| `getRow(i).getCell("Header")` | `findCell(i, "Header").orElseThrow().getCellContentLocator()` |

Notes:

- `select(i)` clicks the row like a user, so selection listeners fire as they
  would in production and a row that cannot be selected stays unselected. Mopo
  called the grid connector directly.
- `getCell(...)` returned the `vaadin-grid-cell-content` locator. DramaFinder
  returns a `CellElement`: `getCellContentLocator()` is that same locator,
  `getText()` / `assertText(...)` read it, and `click()` clicks it. For the common
  case there is a one-liner: `grid.assertCellContent(0, "Name", "Ada")`.
- `getCell("Header")` in DramaFinder matches the header text exactly and only in
  header cells, where Mopo matched any cell containing the text.
- Beyond Mopo: total row count, sorting, header and footer cells, selection state,
  item details, column lookup by id, and `TreeGridElement` for `TreeGrid`.

## 8. `ComboBoxPw` → `ComboBoxElement`

| Mopo | DramaFinder |
|---|---|
| `new ComboBoxPw(locator)` | `ComboBoxElement.getByLabel(page, "Country")` |
| `filterAndSelectFirst("fin")` | `filterAndSelectFirstItem("fin")` |
| `filter("fin")` | `setFilter("fin")` |
| `selectOption("Finland")` | `selectItem("Finland")`; or `filterAndSelectItem("fin", "Finland")` to filter first |
| `openDropDown()` | `open()`, or `clickToggleButton()` to click the toggle like a user |
| `assertThat(selectionDropdown()).isHidden()` | `assertClosed()` (and `assertOpened()`) |
| reading items from `selectionDropdown()` | `getItemLabels()`, `assertItemCount(n)` |

`selectItem` matches the whole item label, like Mopo's `selectOption`.
`filterAndSelectFirstItem` clicks the first offered item rather than pressing
Enter; prefer `filterAndSelectItem(filter, label)` when you know the label, so a
change in item order cannot select a different item unnoticed. `setValue(label)`
selects by typing the label, and `getValue()` / `assertValue(...)` read the
field.

## 9. `DatePickerPw` → `DatePickerElement`

| Mopo | DramaFinder |
|---|---|
| `new DatePickerPw(locator)` | `DatePickerElement.getByLabel(page, "Birthday")` |
| `getValue()` (a `LocalDate`) | `getValueAsLocalDate()`; assert with `assertValue(LocalDate)` |
| `setValue(LocalDate)` | `setValue(LocalDate)` |
| `getInputString()` | `getInputLocator().inputValue()`; assert with `assertValue("6/15/2024")` |

`setValue(String)` types a locale-formatted date into the input, as a user would.

## 10. `DateTimePickerPw` → `DateTimePickerElement`

| Mopo | DramaFinder |
|---|---|
| `new DateTimePickerPw(locator)` | `DateTimePickerElement.getByLabel(page, "Meeting")` |
| `getValue()` (a `LocalDateTime`) | `getValueAsLocalDateTime()`; assert with `assertValue(LocalDateTime)` |
| `setValue(LocalDateTime)` | `setValue(LocalDateTime)` |
| `getDateInputString()` | `getDateValue()`; assert with `assertDateValue(...)` |
| `getTimeInputString()` | `getTimeValue()`; assert with `assertTimeValue(...)` |

`setDate(String)` and `setTime(String)` type into one sub-field.

## 11. Assertions

Mopo's getters are snapshots: `assertEquals(3, grid.getRenderedRowCount())` reads
once and can race the UI. Prefer DramaFinder's `assert…` methods, which retry
until Playwright's timeout, e.g. `grid.assertRowCount(3)`,
`datePicker.assertValue(LocalDate.of(2024, 6, 15))`,
`comboBox.assertValue("Finland")`.

## Where to look next

- `META-INF/dramafinder/START-HERE.md` (in the jar) — a test skeleton and the
  common pitfalls.
- `META-INF/dramafinder/api-reference.md` (in the jar) — every element and method.
