# Progress Bar

`ProgressBarElement` wraps `<vaadin-progress-bar>` (Flow: `ProgressBar`). It
asserts the bar's value, range and indeterminate state, and can change them from
the test.

| | |
|---|---|
| Web component | `<vaadin-progress-bar>` |
| Flow component | `com.vaadin.flow.component.progressbar.ProgressBar` |
| Wrapper | `org.vaadin.addons.dramafinder.element.ProgressBarElement` |
| Typed query | `find(page, ProgressBarElement.class)` → `ComponentQuery` |
| API | [ProgressBarElement](../../skills/vaadin-playwright-test/api-reference.md#progressbarelement) |

## Finding the component

A progress bar has no label of its own, so it is found by id, test id or the
typed query:

```java
ProgressBarElement upload = ProgressBarElement.getById(page, "progress-bar");
ProgressBarElement inDialog = ProgressBarElement.getById(page.locator("#import-dialog"), "import-progress");
ProgressBarElement byTestId = ProgressBarElement.getByTestId(page, "upload-progress");
```

```java
import static org.vaadin.addons.dramafinder.element.query.ElementQueries.find;

ProgressBarElement bar = find(page, ProgressBarElement.class).withClassName("upload-bar").single();
ProgressBarElement first = dialog.find(ProgressBarElement.class).first();
```

The constructor narrows the given locator to `vaadin-progress-bar` elements
(`locator.and(page.locator("vaadin-progress-bar"))`), so
`new ProgressBarElement(page.locator("#progress-bar"))` works, while a locator
pointing at another element matches nothing.

## Value

```java
// View
ProgressBar progressBar = new ProgressBar();
progressBar.setId("progress-bar");
progressBar.setValue(0.5);
```

```java
// Test
ProgressBarElement bar = ProgressBarElement.getById(page, "progress-bar");
bar.assertValue(0.5);

double value = bar.getValue();   // 0.5, when the number itself is needed
```

The value is read from the bar's `aria-valuenow` attribute, and `assertValue`
compares it as text, formatted the way the browser reports it: a whole number
without decimals (`50.0` → `"50"`), otherwise up to six decimals with a `.`
separator. The assertion retries, so it waits for a value the server updates:

```java
// e.g. a view whose "Start import" button advances the bar on the server
ButtonElement.getByText(page, "Start import").click();
bar.assertValue(1.0);   // retries until the bar reaches 1.0
```

## Range: min and max

```java
// View
ProgressBar minMax = new ProgressBar(10.5, 100, 50);   // min, max, value
minMax.setId("min-max-progress-bar");
```

```java
// Test
ProgressBarElement bar = ProgressBarElement.getById(page, "min-max-progress-bar");
bar.assertMin(10.5);
bar.assertMax(100);
bar.assertValue(50.0);

Double min = bar.getMin();   // null when aria-valuemin is absent
Double max = bar.getMax();   // null when aria-valuemax is absent
```

## Indeterminate state

```java
// View
ProgressBar indeterminate = new ProgressBar();
indeterminate.setId("indeterminate-progress-bar");
indeterminate.setIndeterminate(true);
```

```java
// Test
ProgressBarElement.getById(page, "indeterminate-progress-bar").assertIndeterminate();
ProgressBarElement.getById(page, "progress-bar").assertNotIndeterminate();

boolean busy = bar.isIndeterminate();   // the reflected indeterminate attribute
```

## Changing the bar from the test

The setters write the web component's properties in the browser and wait for the
server round-trip they may trigger. They are handy for checking how a bar renders
a given state without driving the server:

```java
ProgressBarElement bar = ProgressBarElement.getById(page, "min-max-progress-bar");
bar.setValue(31);
bar.assertValue(31.0);
bar.setMin(12);
bar.assertMin(12);
bar.setMax(120);
bar.assertMax(120);
bar.setIndeterminate(true);
bar.assertIndeterminate();
```

## Theme and style

```java
// View
progressBar.addThemeVariants(ProgressBarVariant.LUMO_SUCCESS);
progressBar.addClassName("upload-bar");
```

```java
// Test
bar.assertTheme("success");
bar.assertCssClass("upload-bar");
```

## Pitfalls

- **No `get(Page)` factory.** Give the bar an id or a test id, or use the typed
  query.
- **Values are compared as formatted text** of `aria-valuenow` /
  `aria-valuemin` / `aria-valuemax`, not as numbers with a tolerance.
- **`assertValue(null)` does not assert absence**: it only checks that
  `aria-valuenow` is not the string `"undefined"`. Use `assertIndeterminate()` to
  check a bar that shows no progress value.

## See also

- [Upload](UploadElement.md)
- [Assertions](../guide/assertions.md)
- [Finding components](../guide/finding-components.md)
- [Actions and waiting](../guide/actions-and-waiting.md)
- Full example: [ProgressBarViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/ProgressBarViewIT.java)
