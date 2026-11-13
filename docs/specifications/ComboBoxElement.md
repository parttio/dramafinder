# Combo Box

`ComboBoxElement` wraps `<vaadin-combo-box>` (Flow: `ComboBox`). It lets a test
pick an item by its label, type a filter and inspect what the drop-down offers,
open and close the overlay, and check the usual field state (label, helper,
validation, read-only, clear button, prefix, tooltip).

| | |
|---|---|
| Web component | `<vaadin-combo-box>` |
| Flow component | `com.vaadin.flow.component.combobox.ComboBox` |
| Wrapper | `org.vaadin.addons.dramafinder.element.ComboBoxElement` |
| Typed query | `find(page, ComboBoxElement.class)` → `InputFieldQuery` |
| API | [ComboBoxElement](../../skills/vaadin-playwright-test/api-reference.md#comboboxelement) |

For a combo box that keeps several values, see
[Multi-Select Combo Box](MultiSelectComboBoxElement.md).

## Finding the component

```java
// By the visible label (exact, case-sensitive) or part of it
ComboBoxElement sortBy = ComboBoxElement.getByLabel(page, "Sort by");
ComboBoxElement filterable = ComboBoxElement.getByLabelContaining(page, "filterable");

// Scoped to a container
ComboBoxElement inForm = ComboBoxElement.getByLabel(page.locator("#container"), "Country");

// Fallbacks for a combo box without a visible label
ComboBoxElement byId = ComboBoxElement.getById(page, "country");
ComboBoxElement byTestId = ComboBoxElement.getByTestId(page, "country"); // Flow: setTestId("country")

// Typed query: label, placeholder or accessible name (aria-label / aria-labelledby)
ComboBoxElement same = find(page, ComboBoxElement.class).withLabel("Sort by").single();
ComboBoxElement byPlaceholder = find(page, ComboBoxElement.class).withPlaceholder("Enter text here").single();
ComboBoxElement unlabelled = find(page, ComboBoxElement.class).withAccessibleName("Invisible label").single();
```

`getByLabel` matches the whole label of the component's own `label` slot. An
`aria-label` or a placeholder is not a label: `getByLabel(page, "Invisible label")`
finds nothing, use `withAccessibleName(..)` or `withPlaceholder(..)` instead.
None of these lookups applies `.first()`, so two combo boxes with the same label
fail with a strict-mode error; `getByLabelContaining` matches a case-insensitive
substring.

## Selecting an item

```java
// View
ComboBox<String> comboBox = new ComboBox<>();
comboBox.setItems("Most recent first", "Rating: high to low", "Rating: low to high");
comboBox.setLabel("Sort by");
```

```java
// Test
ComboBoxElement sortBy = ComboBoxElement.getByLabel(page, "Sort by");
sortBy.selectItem("Rating: high to low");   // opens the overlay, clicks the item
sortBy.assertValue("Rating: high to low");

sortBy.setValue("Rating: low to high");     // types the label, waits for the item, closes the overlay
sortBy.assertValue("Rating: low to high");
```

- `selectItem(label)` opens the overlay and clicks the item whose **whole** label
  is `label`: `"Item 1"` does not pick `"Item 10"`, and two items with the same
  label fail with a strict-mode error.
- `setValue(label)` types the label as a filter, waits until the matching item
  is shown and closes the overlay. It fails when no item has that label.
- `assertValue(expected)` reads the text in the input; `assertValue("")` (or
  `null`) asserts that nothing is selected.

## Filtering

`setFilter` types into the input with real key presses, which is what makes the
combo box filter (in memory or through the server). It empties the input first,
so a second call replaces the previous filter instead of appending to it.

```java
// View
ComboBox<String> comboBox = new ComboBox<>();
comboBox.setItems("Apple", "Apricot", "Banana", "Blueberry", "Cherry",
        "Date", "Elderberry", "Fig", "Grape");
comboBox.setLabel("Filterable ComboBox");
```

```java
// Test
ComboBoxElement fruit = ComboBoxElement.getByLabel(page, "Filterable ComboBox");

// Filter and select in one step
fruit.filterAndSelectItem("Apr", "Apricot");
fruit.assertValue("Apricot");

// Or filter manually and inspect the narrowed list
fruit.setFilter("Apr");
fruit.assertItemCount(1);     // Apricot
fruit.setFilter("Ch");
fruit.assertItemCount(1);     // Cherry, not "AprCh"
fruit.close();
```

`filterAndSelectFirstItem(filter)` clicks whatever item the drop-down offers
first for the filter. Use it when the filter alone identifies the item (a unique
code, the start of a long label); prefer `filterAndSelectItem(filter, label)`
when you know the label, so a change in item order cannot select a different
item unnoticed. It fails with an `AssertionError` naming the filter when no item
matches before the timeout.

```java
fruit.filterAndSelectFirstItem("ap");
fruit.assertValue("Apple");
fruit.assertClosed();
```

`getFilter()` returns the current `filter` property (`""` when there is none).

## Lazy data providers

A combo box backed by a lazy (server-side) data provider works the same way:
`selectItem`, `setFilter` and `filterAndSelectItem` narrow the results on the
server exactly as they do for in-memory items, and each action waits for the
server round-trip.

```java
// View
ComboBox<String> comboBox = new ComboBox<>();
comboBox.setLabel("Lazy ComboBox");
comboBox.setItems(
        query -> allItems.stream()
                .filter(item -> item.toLowerCase().contains(query.getFilter().orElse("").toLowerCase()))
                .skip(query.getOffset()).limit(query.getLimit()),
        query -> (int) allItems.stream()
                .filter(item -> item.toLowerCase().contains(query.getFilter().orElse("").toLowerCase()))
                .count());
```

```java
// Test
ComboBoxElement lazy = ComboBoxElement.getByLabel(page, "Lazy ComboBox");
lazy.filterAndSelectItem("Item 250", "Item 250");
lazy.assertValue("Item 250");

lazy.setFilter("Item 49");   // Item 49, Item 490 … Item 499
lazy.assertItemCount(11);
lazy.close();
```

## Reading the items

`getItemLabels()` returns every item the drop-down offers for the current
filter, in order. It opens the overlay if needed, waits for the items to load
and, with a lazy data provider, requests the pages not loaded yet one after the
other, so you get all items, not only the rendered ones. It leaves the overlay
as it found it.

```java
ComboBoxElement sortBy = ComboBoxElement.getByLabel(page, "Sort by");
assertEquals(List.of("Most recent first", "Rating: high to low", "Rating: low to high"),
        sortBy.getItemLabels());
sortBy.assertClosed();       // was closed, so it is closed again

ComboBoxElement lazy = ComboBoxElement.getByLabel(page, "Lazy ComboBox");
lazy.setFilter("Item 49");
assertEquals(11, lazy.getItemLabels().size());
lazy.assertOpened();         // was open for the filter, so it stays open
```

`getItemLabels()` throws `IllegalStateException` when the items do not load
before the timeout, for example on a read-only or disabled combo box whose
overlay does not open.

`assertItemCount(n)` and `getOverlayItemCount()` count the items **rendered** in
the open overlay; they are scoped to this combo box, so two open combo boxes do
not see each other's items.

## The selected item vs the input text

`getValue()` and `assertValue(..)` read the text in the input, which also shows
a filter being typed or a custom value. `getSelectedItemLabel()` reads the
component's `selectedItem`: it ignores a typed filter and returns `null` when no
item is selected, including when the field shows a custom value.

```java
ComboBoxElement sortBy = ComboBoxElement.getByLabel(page, "Sort by");
assertNull(sortBy.getSelectedItemLabel());
sortBy.selectItem("Rating: high to low");
assertEquals("Rating: high to low", sortBy.getSelectedItemLabel());
```

## Custom values

With `setAllowCustomValue(true)` the user can commit text that is not an item.
Type it into the input and press Enter, as a user does:

```java
// View
ComboBox<String> comboBox = new ComboBox<>();
comboBox.setItems("Option 1", "Option 2", "Option 3");
comboBox.setLabel("Custom value ComboBox");
comboBox.setAllowCustomValue(true);
comboBox.addCustomValueSetListener(e -> comboBox.setValue(e.getDetail()));
```

```java
// Test
ComboBoxElement custom = ComboBoxElement.getByLabel(page, "Custom value ComboBox");
custom.getInputLocator().fill("Custom entry");
custom.getInputLocator().press("Enter");
custom.assertValue("Custom entry");
```

Don't use `setValue("Custom entry")` here: it waits for an item with that label.

## Clearing the selection

`clear()` clears as a user does: it clicks the clear button when the app shows
it; otherwise it empties the input and leaves the field. Either way the value
becomes `null` on the server.

```java
// View
ComboBox<String> comboBox = new ComboBox<>("Clearable ComboBox", "Apple", "Banana", "Cherry");
comboBox.setValue("Banana");
```

```java
// Test
ComboBoxElement clearable = ComboBoxElement.getByLabel(page, "Clearable ComboBox");
clearable.assertClearButtonNotVisible();
clearable.clear();
clearable.assertValue("");
clearable.assertClosed();
```

With `setClearButtonVisible(true)` you can also click the button yourself:

```java
ComboBoxElement withButton = ComboBoxElement.getByLabel(page, "ComboBox with placeholder and clear button");
withButton.selectItem("Most recent first");
withButton.assertClearButtonVisible();
withButton.clickClearButton();
withButton.assertValue("");
```

Don't clear with `setValue("")`: on a combo box it waits for an item labelled
`""`.

## Opening and closing

```java
ComboBoxElement fruit = ComboBoxElement.getByLabel(page, "Filterable ComboBox");
fruit.assertClosed();
fruit.open();                 // sets the opened property
fruit.assertOpened();
fruit.assertItemCount(9);
fruit.close();
fruit.assertClosed();

fruit.clickToggleButton();    // clicks the toggle button, as a user does
fruit.assertOpened();
```

`isOpened()` returns the current state without waiting, and
`getToggleButtonLocator()` gives the toggle button for your own Playwright
assertions.

## Label, helper, placeholder and prefix

```java
// View
ComboBox<String> comboBox = new ComboBox<>();
comboBox.setLabel("Sort by");
comboBox.setHelperText("Helper text");
comboBox.setPrefixComponent(new Span("Prefix"));
comboBox.setTooltipText("Tooltip for combo box");
comboBox.addClassName("custom-combo-box");
```

```java
// Test
ComboBoxElement sortBy = ComboBoxElement.getByLabel(page, "Sort by");
sortBy.assertLabel("Sort by");
sortBy.assertHelperHasText("Helper text");
sortBy.assertPrefixHasText("Prefix");
sortBy.assertTooltipHasText("Tooltip for combo box");
sortBy.assertCssClass("custom-combo-box");

ComboBoxElement withPlaceholder = ComboBoxElement.getByLabel(page, "ComboBox with placeholder and clear button");
withPlaceholder.assertPlaceholder("Enter text here");
```

`assertLabel(null)` asserts there is no visible label, and
`assertPrefixHasText(null)` that there is no prefix. A helper *component*
(`setHelperComponent(..)`) is reachable through `getHelperLocator()`:

```java
ComboBoxElement outer = ComboBoxElement.getByLabel(page, "ComboBox with helper component");
ComboBoxElement helper = new ComboBoxElement(outer.getHelperLocator());
helper.assertHelperHasText("Internal helper");
```

## Accessible name

`assertAriaLabel(..)` checks the `aria-label` of the input. With
`setAriaLabelledBy(..)` there is no `aria-label`, so assert `null`:

```java
find(page, ComboBoxElement.class).withAccessibleName("Invisible label").single()
        .assertAriaLabel("Invisible label");
find(page, ComboBoxElement.class).withAccessibleName("Labelled by").single()
        .assertAriaLabel(null);
```

## Validation

```java
// View
ComboBox<String> comboBox = new ComboBox<>();
comboBox.setLabel("ComboBox with theme");
binder.forField(comboBox).asRequired("Required field").bind("name");
Button validate = new Button("Validate", e -> binder.validate());
```

```java
// Test
ComboBoxElement required = ComboBoxElement.getByLabel(page, "ComboBox with theme");
required.assertValid();
ButtonElement.getByText(page, "Validate").click();
required.assertInvalid();
required.assertErrorMessage("Required field");
```

## Enabled, read-only, focus and allowed characters

```java
ComboBoxElement toggled = ComboBoxElement.getByLabel(page, "Enabled/Disabled Field");
toggled.assertDisabled();
ButtonElement.getByText(page, "Enable/Disable").click();
toggled.assertEnabled();

ComboBoxElement readOnly = ComboBoxElement.getByLabel(page, "Read-only ComboBox");
readOnly.assertReadOnly();
readOnly.assertValue("Banana");

ComboBoxElement other = ComboBoxElement.getByLabel(page, "ComboBox with helper component");
other.focus();               // focuses the input
other.assertIsFocused();

ComboBoxElement codes = ComboBoxElement.getByLabel(page, "Allowed char pattern ComboBox");
codes.assertAllowedCharPattern("[A-Z]");
```

Enablement and focus are read on the inner input, not the host.

## Theme variants

```java
// View: comboBox.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
ComboBoxElement.getByLabel(page, "ComboBox with theme").assertTheme("small");
```

`assertTheme` compares the whole `theme` attribute; use
`assertHasThemeVariant("small")` to check one variant among several.

## Pitfalls

- **`setValue("")` is not a clear.** It types `""` and waits for an item with
  that label. Use `clear()` or `clickClearButton()`.
- **`setValue` needs an existing item.** For a custom value, fill the input and
  press Enter (see [Custom values](#custom-values)).
- **`getValue()` is the input text.** While a filter is typed it returns the
  filter; use `getSelectedItemLabel()` for the selected item.
- **Item labels match exactly.** `selectItem`, `setValue` and
  `filterAndSelectItem` need the item's whole label, case-sensitively; a partial
  label no longer selects anything.
- **`assertItemCount` counts rendered items.** For the full list of a lazy combo
  box, use `getItemLabels()`.
- **`open()` and `close()` set the `opened` property.** Use
  `clickToggleButton()` when the test must click like a user.

## See also

- [Multi-Select Combo Box](MultiSelectComboBoxElement.md), [Select](SelectElement.md),
  [List Box](ListBoxElement.md)
- [Finding components](../guide/finding-components.md),
  [Assertions](../guide/assertions.md),
  [Actions and waiting](../guide/actions-and-waiting.md),
  [Core concepts](../guide/core-concepts.md)
- Full example: [ComboBoxViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/ComboBoxViewIT.java)
