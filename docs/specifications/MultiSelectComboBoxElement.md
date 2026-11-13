# Multi-Select Combo Box

`MultiSelectComboBoxElement` wraps `<vaadin-multi-select-combo-box>` (Flow:
`MultiSelectComboBox`). It lets a test select and deselect several items by
label, filter the drop-down, assert the selection, reach components rendered
inside items, and check the usual field state.

| | |
|---|---|
| Web component | `<vaadin-multi-select-combo-box>` |
| Flow component | `com.vaadin.flow.component.combobox.MultiSelectComboBox` |
| Wrapper | `org.vaadin.addons.dramafinder.element.MultiSelectComboBoxElement` |
| Typed query | `find(page, MultiSelectComboBoxElement.class)` → `InputFieldQuery` |
| API | [MultiSelectComboBoxElement](../../skills/vaadin-playwright-test/api-reference.md#multiselectcomboboxelement) |

Most of the API mirrors [Combo Box](ComboBoxElement.md); this page focuses on
what differs.

## Finding the component

```java
// By the visible label (exact, case-sensitive) or part of it
MultiSelectComboBoxElement fruits = MultiSelectComboBoxElement.getByLabel(page, "Fruits");
MultiSelectComboBoxElement lazy = MultiSelectComboBoxElement.getByLabelContaining(page, "lazy");

// Scoped to a container
MultiSelectComboBoxElement inForm = MultiSelectComboBoxElement.getByLabel(page.locator("#container"), "Fruits");

// Fallbacks for a field without a visible label
MultiSelectComboBoxElement byId = MultiSelectComboBoxElement.getById(page, "fruits");
MultiSelectComboBoxElement byTestId = MultiSelectComboBoxElement.getByTestId(page, "fruits");

// Typed query: label, placeholder or accessible name
MultiSelectComboBoxElement byPlaceholder =
        find(page, MultiSelectComboBoxElement.class).withPlaceholder("Select items").single();
MultiSelectComboBoxElement unlabelled =
        find(page, MultiSelectComboBoxElement.class).withAccessibleName("Invisible label").single();
```

`getByLabel` matches the whole visible label; `getByLabelContaining` a
case-insensitive substring. Neither applies `.first()`, and an `aria-label` or a
placeholder is not a label.

## Selecting and deselecting items

```java
// View
MultiSelectComboBox<String> comboBox = new MultiSelectComboBox<>();
comboBox.setItems("Apple", "Banana", "Cherry", "Date", "Elderberry");
comboBox.setLabel("Fruits");
```

```java
// Test
MultiSelectComboBoxElement fruits = MultiSelectComboBoxElement.getByLabel(page, "Fruits");
fruits.selectItems("Apple", "Cherry");
fruits.close();
fruits.assertSelectedItems("Cherry", "Apple");   // any order
fruits.assertSelectedCount(2);
fruits.assertValue("Apple, Cherry");

fruits.deselectItem("Apple");
fruits.close();
fruits.assertSelectedItems("Cherry");
```

- `selectItem(label)` / `selectItems(..)` open the overlay and click each item;
  `deselectItem(label)` / `deselectItems(..)` do the same. A click **toggles**
  the item, so selecting an item that is already selected deselects it.
- The overlay stays open after these calls (a user keeps picking): call
  `close()` when the test needs it closed.
- Items match on their whole label: `"Item 1"` does not pick `"Item 10"`.
- `assertSelectedItems(..)` passes when the selection holds exactly these
  labels, in any order. `assertSelectedCount(n)` checks only the number.
- `assertValue(..)` compares the selected labels joined with `", "`;
  `assertValue("")` (or `null`) asserts an empty selection.

## Filtering

`setFilter` types into the input with real key presses and replaces any previous
filter. `filterAndSelectItem(filter, label)` filters, then clicks the item.

```java
MultiSelectComboBoxElement fruits = MultiSelectComboBoxElement.getByLabel(page, "Filterable MultiSelect");
fruits.filterAndSelectItem("Apr", "Apricot");
fruits.close();
fruits.assertSelectedItems("Apricot");

fruits.setFilter("Ch");
fruits.assertItemCount(1);   // Cherry
fruits.close();
```

Lazy data providers behave identically: the filter and the selection go
through the server, and every action waits for the round-trip.

```java
MultiSelectComboBoxElement lazy = MultiSelectComboBoxElement.getByLabel(page, "Lazy MultiSelect");
lazy.filterAndSelectItem("Item 250", "Item 250");
lazy.close();
lazy.assertSelectedItems("Item 250");

lazy.setFilter("Item 49");   // Item 49, Item 490 … Item 499
lazy.assertItemCount(11);
lazy.close();
```

`getFilter()` returns the current filter (`""` when there is none). There is no
`filterAndSelectFirstItem` on the multi-select combo box.

## Reading the items and the selection

```java
MultiSelectComboBoxElement fruits = MultiSelectComboBoxElement.getByLabel(page, "Fruits");
assertEquals(List.of("Apple", "Banana", "Cherry", "Date", "Elderberry"), fruits.getItemLabels());
fruits.assertClosed();       // left as it was

fruits.selectItems("Apple", "Cherry");
List<String> selected = fruits.getSelectedItems();   // read from selectedItems
int count = fruits.getSelectedItemCount();
```

`getItemLabels()` returns every item the drop-down offers for the current
filter, selected or not, loading every page of a lazy data provider; it throws
`IllegalStateException` when the items don't load (for example on a read-only or
disabled field). `assertItemCount(n)` and `getOverlayItemCount()` count the
items **rendered** in the open overlay of this field only.

## Chips

Selected items show as chips in the field. `getChipLocators()` returns the chips
that fit; `getOverflowChipLocator()` the chip that stands for the rest when they
don't.

```java
fruits.selectItems("Apple", "Cherry");
fruits.close();
assertThat(fruits.getChipLocators()).hasCount(2);   // both fit in the field
```

Assert the selection itself with `assertSelectedItems(..)`: which items get a
chip depends on the field's width.

## Components inside items

With a `ComponentRenderer`, `getOverlayItemComponent` returns a typed element for
a component rendered in an item, found by the item's label or its visible index.
The element class must carry `@PlaywrightElement` and have a `Locator`
constructor, which every DramaFinder element has.

```java
// View
comboBox.setRenderer(new ComponentRenderer<>(item -> {
    Button button = new Button("Info " + item);
    button.addClickListener(e -> output.setText("Clicked: " + item));
    return new HorizontalLayout(new Span(item), button);
}));
```

```java
// Test
MultiSelectComboBoxElement custom = MultiSelectComboBoxElement.getByLabel(page, "Custom renderer");
custom.open();
ButtonElement info = custom.getOverlayItemComponent("Banana", ButtonElement.class);
info.click();
assertThat(page.locator("#custom-renderer-output")).hasText("Clicked: Banana");

ButtonElement first = custom.getOverlayItemComponent(0, ButtonElement.class);
assertThat(first.getLocator()).hasText("Info Apple");
```

Both overloads throw `IllegalArgumentException` when the class has no
`@PlaywrightElement` annotation or cannot be instantiated.

## Clearing the selection

`clear()` clears as a user does: it clicks the clear button when the app shows
it; otherwise it deselects every selected item in the overlay and closes it.
Either way the server value becomes an empty set.

```java
// View
MultiSelectComboBox<String> comboBox = new MultiSelectComboBox<>("Clearable MultiSelect");
comboBox.setItems("Apple", "Banana", "Cherry", "Date");
comboBox.setValue(Set.of("Banana", "Date"));
```

```java
// Test
MultiSelectComboBoxElement clearable = MultiSelectComboBoxElement.getByLabel(page, "Clearable MultiSelect");
clearable.assertSelectedItems("Banana", "Date");
clearable.clear();
clearable.assertSelectedCount(0);
clearable.assertClosed();

// With setClearButtonVisible(true)
MultiSelectComboBoxElement withButton =
        MultiSelectComboBoxElement.getByLabel(page, "MultiSelect with placeholder and clear button");
withButton.selectItems("Apple", "Banana");
withButton.close();
withButton.clickClearButton();
withButton.assertSelectedCount(0);
```

Don't clear with `setValue("")`: the `setValue(String)` inherited from the
field mixins only fills the input text and leaves the selection in place.

## Opening and closing

```java
MultiSelectComboBoxElement fruits = MultiSelectComboBoxElement.getByLabel(page, "Filterable MultiSelect");
fruits.open();               // sets the opened property
fruits.assertOpened();
fruits.assertItemCount(9);
fruits.close();
fruits.assertClosed();
fruits.clickToggleButton();  // clicks the toggle like a user
```

`isOpened()` reads the state without waiting; `getToggleButtonLocator()` returns
the toggle button.

## Field state

```java
MultiSelectComboBoxElement fruits = MultiSelectComboBoxElement.getByLabel(page, "Fruits");
fruits.assertLabel("Fruits");
fruits.assertHelperHasText("Helper text");
fruits.assertTooltipHasText("Tooltip for multi-select");
fruits.assertCssClass("custom-multi-select");

MultiSelectComboBoxElement.getByLabel(page, "MultiSelect with placeholder and clear button")
        .assertPlaceholder("Select items");

MultiSelectComboBoxElement readOnly = MultiSelectComboBoxElement.getByLabel(page, "Read-only MultiSelect");
readOnly.assertReadOnly();
readOnly.assertSelectedItems("Apple", "Cherry");

MultiSelectComboBoxElement toggled = MultiSelectComboBoxElement.getByLabel(page, "Enabled/Disabled Field");
toggled.assertDisabled();
ButtonElement.getByText(page, "Enable/Disable").click();
toggled.assertEnabled();

// aria-label on the input; null when the name comes from aria-labelledby
find(page, MultiSelectComboBoxElement.class).withAccessibleName("Labelled by").single()
        .assertAriaLabel(null);
```

Validation and theme variants work as on any field:

```java
// View: binder.forField(comboBox).asRequired("Required field").bind("names");
//       comboBox.addThemeVariants(MultiSelectComboBoxVariant.LUMO_SMALL);
MultiSelectComboBoxElement required = MultiSelectComboBoxElement.getByLabel(page, "MultiSelect with theme");
required.assertTheme("small");
required.assertValid();
ButtonElement.getByText(page, "Validate").click();
required.assertInvalid();
required.assertErrorMessage("Required field");
```

The multi-select combo box also has `assertAllowedCharPattern(..)` and
`focus()` / `assertIsFocused()` (on the input). It has no prefix helpers.

## Pitfalls

- **Selection clicks toggle.** `selectItem` on a selected item deselects it, and
  `deselectItem` on an unselected item selects it. Check the state first when
  in doubt.
- **The overlay stays open** after `selectItem(s)`, `deselectItem(s)` and
  `filterAndSelectItem`. Close it before asserting a closed field or
  interacting with what it covers.
- **`setValue(..)` does not select.** It is the generic text-field action. Use
  `selectItem(s)` to select and `clear()` to empty the selection.
- **`getValue()` is not the input text.** Unlike the single combo box, it joins
  the selected labels from `selectedItems` with `", "`.
- **Chips depend on the width.** Assert the selection, not the chips.

## See also

- [Combo Box](ComboBoxElement.md), [List Box](ListBoxElement.md),
  [Checkbox Group](CheckboxGroupElement.md)
- [Finding components](../guide/finding-components.md),
  [Assertions](../guide/assertions.md),
  [Actions and waiting](../guide/actions-and-waiting.md),
  [Core concepts](../guide/core-concepts.md)
- Full example: [MultiSelectComboBoxViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/MultiSelectComboBoxViewIT.java)
