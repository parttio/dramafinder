# Select

`SelectElement` wraps `<vaadin-select>` (Flow: `Select`). It lets a test pick an
item from the drop-down by its label, assert the shown value and placeholder,
and check the usual field state (label, helper, prefix, validation, tooltip,
theme).

| | |
|---|---|
| Web component | `<vaadin-select>` |
| Flow component | `com.vaadin.flow.component.select.Select` |
| Wrapper | `org.vaadin.addons.dramafinder.element.SelectElement` |
| Typed query | `find(page, SelectElement.class)` → `SelectQuery` |
| API | [SelectElement](../../skills/vaadin-playwright-test/api-reference.md#selectelement) |

## Finding the component

```java
// By the visible label (exact, case-sensitive) or part of it
SelectElement sortBy = find(page, SelectElement.class).withLabel("Sort by").single();
SelectElement themed = find(page, SelectElement.class).withLabelContaining("with theme").single();

// Scoped to a container
SelectElement inForm = find(page.locator("#container"), SelectElement.class).withLabel("Sort by").single();

// Fallbacks for a select without a visible label
SelectElement byId = SelectElement.getById(page, "sort");
SelectElement byTestId = SelectElement.getByTestId(page, "sort");

// Typed query: label, placeholder or accessible name
SelectElement byPlaceholder = find(page, SelectElement.class).withPlaceholder("Enter text here").single();
SelectElement unlabelled = find(page, SelectElement.class).withAccessibleName("Invisible label").single();
```

`vaadin-select` does not reflect its placeholder on the host, so
`SelectQuery.withPlaceholder(..)` reads the text the select shows in its value
button. An `aria-label` or a placeholder is not a label: `withLabel` does not
find them. No lookup applies `.first()`; `withLabelContaining` matches a
case-insensitive substring.

## Selecting an item

```java
// View
Select<String> select = new Select<>();
select.setItems("Most recent first", "Rating: high to low", "Rating: low to high");
select.setLabel("Sort by");
```

```java
// Test
SelectElement sortBy = find(page, SelectElement.class).withLabel("Sort by").single();
sortBy.assertValue(null);                  // nothing selected yet
sortBy.selectItem("Rating: high to low");  // opens the drop-down, clicks the item
sortBy.assertValue("Rating: high to low");
```

`selectItem(label)` clicks the select to open it, then clicks the item whose
**whole** label is `label` (`"Option 1"` does not pick `"Option 10"`; two items
with the same label fail with a strict-mode error). It waits for the server
round-trip.

`assertValue(expected)` compares the text of the value button;
`assertValue(null)` and `assertValue("")` assert that nothing is selected.
`getValue()` returns the same text when the test needs it.

## Placeholder

```java
// View
select.setLabel("Select with placeholder and clear button");
select.setPlaceholder("Enter text here");
```

```java
// Test
SelectElement select = find(page, SelectElement.class).withLabel("Select with placeholder and clear button").single();
select.assertPlaceholder("Enter text here");
assertEquals("Enter text here", select.getPlaceholder());
```

On a select, `assertPlaceholder(text)` checks that the value button is in its
placeholder state and shows `text`, so it fails once an item is selected.
`assertPlaceholder(null)` asserts that the select has no placeholder at all,
whether an item is selected or not.

## Label, helper, prefix and tooltip

```java
// View
select.setLabel("Sort by");
select.setHelperText("Helper text");
select.setPrefixComponent(new Span("Prefix"));
select.setTooltipText("Tooltip for select");
select.addClassName("custom-text-field");
```

```java
// Test
SelectElement sortBy = find(page, SelectElement.class).withLabel("Sort by").single();
sortBy.assertLabel("Sort by");
sortBy.assertHelperHasText("Helper text");
sortBy.assertPrefixHasText("Prefix");
sortBy.assertTooltipHasText("Tooltip for select");
sortBy.assertCssClass("custom-text-field");

find(page, SelectElement.class).withLabel("Select with helper component").single().assertPrefixHasText(null); // no prefix
```

A helper component is reachable through `getHelperLocator()`:

```java
SelectElement outer = find(page, SelectElement.class).withLabel("Select with helper component").single();
SelectElement helper = new SelectElement(outer.getHelperLocator());
helper.assertHelperHasText("Internal helper");
```

## Accessible name

From Vaadin 25.4 the select puts its `aria-label` on the value button; up to
25.3 it rendered it as a screen-reader-only label (`slot="sr-label"`).
`getAriaLabel()` and `assertAriaLabel(..)` read either. With
`setAriaLabelledBy(..)` there is no ARIA label, so assert `null`:

```java
find(page, SelectElement.class).withAccessibleName("Invisible label").single()
        .assertAriaLabel("Invisible label");
find(page, SelectElement.class).withAccessibleName("Labelled by").single()
        .assertAriaLabel(null);
```

## Validation

```java
// View
Select<String> select = new Select<>();
select.setLabel("Select with theme");
binder.forField(select).asRequired("Required field").bind("name");
Button validate = new Button("Validate", e -> binder.validate());
```

```java
// Test
SelectElement required = find(page, SelectElement.class).withLabel("Select with theme").single();
required.assertValid();
find(page, ButtonElement.class).withAccessibleName("Validate").single().click();
required.assertInvalid();
required.assertErrorMessage("Required field");
```

## Enabled, focus and theme

```java
SelectElement toggled = find(page, SelectElement.class).withLabel("Enabled/Disabled Field").single();
toggled.assertDisabled();
find(page, ButtonElement.class).withAccessibleName("Enable/Disable").single().click();
toggled.assertEnabled();

SelectElement other = find(page, SelectElement.class).withLabel("Select with helper component").single();
other.focus();
other.assertIsFocused();

// View: select.addThemeVariants(SelectVariant.LUMO_SMALL);
find(page, SelectElement.class).withLabel("Select with theme").single().assertTheme("small");
```

Enablement and focus are read on the value button, not the host.

## Pitfalls

- **Select with `selectItem`, not `setValue`.** The select has no text input:
  the `setValue(String)` and `clear()` it inherits from the field mixins fill an
  input, which the value button is not. Use `selectItem(label)` to choose an
  item.
- **Item labels match exactly** and case-sensitively; a partial label selects
  nothing.
- **`assertPlaceholder` needs an empty select.** It checks the shown text, which
  is the selected item once there is one.
- **No open/close or item-list helpers.** `selectItem` opens the drop-down
  itself. To inspect the items, open it with `click()` and assert with
  Playwright on `select.getLocator().locator(SelectElement.FIELD_ITEM_TAG_NAME)`
  (the `vaadin-select-item` elements, which `selectItem` also looks for inside
  the select).

## See also

- [Combo Box](ComboBoxElement.md), [List Box](ListBoxElement.md),
  [Radio Button Group](RadioButtonGroupElement.md)
- [Finding components](../guide/finding-components.md),
  [Assertions](../guide/assertions.md),
  [Actions and waiting](../guide/actions-and-waiting.md),
  [Core concepts](../guide/core-concepts.md)
- Full example: [SelectViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/SelectViewIT.java)
