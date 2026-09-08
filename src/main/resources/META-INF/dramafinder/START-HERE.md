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
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MyViewIT extends AbstractBasePlaywrightIT {

    @LocalServerPort
    private int port;

    @Override
    public String getUrl() {
        return "http://localhost:" + port;
    }

    @Override
    public String getView() {
        return "/my-view"; // appended to getUrl() before each test
    }

    @Test
    public void filtersTheGrid() {
        TextFieldElement.getByLabel(page, "Name").setValue("ada");
        GridElement.get(page).assertRowCount(1);
    }
}
```

`AbstractBasePlaywrightIT` opens the `page` field before each test, navigates it to
`getUrl() + getView()`, waits for Vaadin to go idle, and closes it after. The browser
is headless unless the `headless` system property or the `HEADLESS` environment
variable says otherwise. Playwright's browser binaries must already be installed.

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

## Three things that are easy to get wrong

**Factory methods are not uniform.** Most fields have `getByLabel(Page, String)`,
containers have `get(Page)`, and `getById(Page, String)` exists on only a few. An
element has exactly the factories the index table lists for it — assuming one that
isn't listed is the most common way to lose a compile round-trip. Every wrapper is
also constructible from a `Locator`: `new TextFieldElement(someLocator)`.

**A grid cell has two locators.** `getTableCellLocator()` is the `<td>`; the rendered
content lives in a `<vaadin-grid-cell-content>` elsewhere in the DOM, reachable via
`getCellContentLocator()`. `getLocator()`, inherited from `VaadinElement`, is the grid
itself, not the cell. For text, prefer `CellElement.getText()` / `assertText(String)`,
or `GridElement.assertCellContent(row, column, expected)`.

**Assertions retry; getters do not.** Every `assertX` method polls via
`page.waitForCondition(...)`, so it tolerates a value the client has not rendered yet.
A bare `getX()` compared with `assertEquals` does not, and is flaky against a lazily
loaded grid or a debounced field. Reach for the `assertX` method.

## More

- Full API reference: `META-INF/dramafinder/api-reference.md` (in this jar)
- Source and issues: https://github.com/parttio/dramafinder
- Claude Code skills: `/plugin marketplace add parttio/dramafinder`
