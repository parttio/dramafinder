# Checkbox Group

`CheckboxGroupElement` wraps `<vaadin-checkbox-group>` (Flow: `CheckboxGroup`).
It selects and deselects items by their visible label, asserts the complete
selection, and exposes every item as a [`CheckboxElement`](CheckboxElement.md).

| | |
|---|---|
| Web component | `<vaadin-checkbox-group>` |
| Flow component | `com.vaadin.flow.component.checkbox.CheckboxGroup` |
| Wrapper | `org.vaadin.addons.dramafinder.element.CheckboxGroupElement` |
| Typed query | `find(page, CheckboxGroupElement.class)` → `FieldQuery` |
| API | [CheckboxGroupElement](../../skills/vaadin-playwright-test/api-reference.md#checkboxgroupelement) |

## Finding the component

```java
// View
CheckboxGroup<String> group = new CheckboxGroup<>();
group.setLabel("Basic CheckboxGroup");
group.setItems("Option 1", "Option 2", "Option 3");
group.setId("basic-group");
group.setTestId("toppings");
```

```java
// Test
CheckboxGroupElement group = CheckboxGroupElement.getByLabel(page, "Basic CheckboxGroup");
CheckboxGroupElement partial = CheckboxGroupElement.getByLabelContaining(page, "basic");
CheckboxGroupElement byId = CheckboxGroupElement.getById(page, "basic-group");
CheckboxGroupElement byTestId = CheckboxGroupElement.getByTestId(page, "toppings");

// Scoped, and the typed query
CheckboxGroupElement scoped = CheckboxGroupElement.getByLabel(page.locator("main"), "Basic CheckboxGroup");
CheckboxGroupElement viaQuery = find(page, CheckboxGroupElement.class).withLabel("Basic CheckboxGroup").single();
```

`getByLabel` / `withLabel` match the group's visible label in full,
case-sensitively; the `...Containing` variants match a case-insensitive
substring. Both fail with a strict-mode error on several matches.

## Selecting and deselecting items

`CheckboxGroup`'s client-side `value` is an array of server-generated item
keys (`"1"`, `"2"`, …), not of the item strings a test knows about. Every
selection method therefore takes the checkbox's **visible label**, and
`getSelectedValues()` returns labels as well.

```java
CheckboxGroupElement group = CheckboxGroupElement.getByLabel(page, "Basic CheckboxGroup");
group.assertSelected();                       // nothing selected yet

group.selectByLabel("Option 1", "Option 3");
group.assertSelected("Option 1", "Option 3");

group.deselectByLabel("Option 1");
group.assertSelected("Option 3");

group.deselectAll();
group.assertSelected();                       // no argument asserts an empty selection
```

`selectByLabel` leaves already selected items checked and `deselectByLabel`
leaves already deselected ones alone, so both are safe to repeat. Each call
waits for the server round-trip.

### assertSelected is exhaustive

`assertSelected(String...)` asserts the *complete* selection: the number of
checked checkboxes must equal the number of labels passed, and each label must
be checked. Calling it with no argument (or `null`) asserts that nothing is
selected.

```java
// View
group.setValue(Set.of("Option 1", "Option 3"));
```

```java
// Test
CheckboxGroupElement group = CheckboxGroupElement.getByLabel(page, "Pre-selected Value");
group.assertSelected("Option 1", "Option 3");   // passes
// group.assertSelected("Option 1");            // fails: "Option 3" is selected too
```

### Reading the selection

When the test needs the labels themselves, `getSelectedValues()` returns them
in DOM order (it reads once, without retrying):

```java
group.selectByLabel("Option 2", "Option 3");
List<String> selected = group.getSelectedValues(); // ["Option 2", "Option 3"]
```

## Individual checkboxes

Items are exposed as regular `CheckboxElement` instances, so the whole checkbox
API — including per-item enablement from `setItemEnabledProvider` — is
available:

```java
// View
CheckboxGroup<String> group = new CheckboxGroup<>();
group.setLabel("Disabled Item");
group.setItems("Option 1", "Option 2", "Option 3");
group.setItemEnabledProvider(item -> !"Option 2".equals(item));
```

```java
// Test
CheckboxGroupElement group = CheckboxGroupElement.getByLabel(page, "Disabled Item");
group.getCheckbox("Option 1").assertEnabled();
group.getCheckbox("Option 2").assertDisabled();

group.assertCheckboxCount(3);
List<CheckboxElement> all = group.getCheckboxes();  // DOM order
all.forEach(CheckboxElement::assertNotChecked);
all.get(1).check();
group.assertSelected("Option 2");
```

`getCheckboxCount()` returns the count once; prefer `assertCheckboxCount(int)`,
which retries.

### How item labels are matched

`getCheckbox(label)`, `selectByLabel`, `deselectByLabel` and `assertSelected`
look the checkbox up with `CheckboxElement.getByLabel(Locator, String)` scoped
to the group: the **whole** visible label, case-sensitively.
`selectByLabel("Option 1")` therefore leaves an `Option 10` checkbox alone, and
a partial label reaches nothing. Two checkboxes carrying the same label make the
lookup fail with a Playwright strict-mode error; reach those through
`getCheckboxes()` instead.

## Enabled and disabled

```java
// View
group.setEnabled(false);
```

```java
// Test
CheckboxGroupElement group = CheckboxGroupElement.getByLabel(page, "Disabled CheckboxGroup");
group.assertDisabled();
group.getCheckboxes().forEach(CheckboxElement::assertDisabled);
```

## Validation

```java
// View
CheckboxGroup<String> group = new CheckboxGroup<>();
group.setLabel("Required CheckboxGroup");
group.setItems("Option 1", "Option 2", "Option 3");
group.setRequiredIndicatorVisible(true);
group.setErrorMessage("Selection is required");
Button validate = new Button("Validate CheckboxGroup",
        event -> group.setInvalid(group.isEmpty()));
```

```java
// Test
CheckboxGroupElement group = CheckboxGroupElement.getByLabel(page, "Required CheckboxGroup");
group.assertValid();

ButtonElement.getByText(page, "Validate CheckboxGroup").click();
group.assertInvalid();
group.assertErrorMessage("Selection is required");

group.selectByLabel("Option 1");
ButtonElement.getByText(page, "Validate CheckboxGroup").click();
group.assertValid();
```

## Label, helper, tooltip, theme and style

```java
CheckboxGroupElement.getByLabel(page, "Basic CheckboxGroup").assertLabel("Basic CheckboxGroup");
CheckboxGroupElement.getByLabel(page, "Helper Text").assertHelperHasText("This is a helper text");
CheckboxGroupElement.getByLabel(page, "Tooltip CheckboxGroup").assertTooltipHasText("This is a tooltip");

// View: group.getElement().setAttribute("theme", "vertical"); group.addClassName("styled-group");
CheckboxGroupElement styled = CheckboxGroupElement.getByLabel(page, "Vertical CheckboxGroup");
styled.assertTheme("vertical");
styled.assertHasThemeVariant("vertical");
styled.assertCssClass("styled-group");
```

## Pitfalls

- The group's `value` holds server-generated keys, not item strings: always
  work with labels (`selectByLabel`, `assertSelected`, `getSelectedValues`),
  never with `getProperty("value")`.
- `assertSelected` is exhaustive: list every selected item, or call it with no
  argument for "nothing selected".
- Item labels match exactly and case-sensitively; a duplicate label is a
  strict-mode error, not "the first one".

## See also

- [Checkbox](CheckboxElement.md), [Radio Button Group](RadioButtonGroupElement.md),
  [Multi-Select Combo Box](MultiSelectComboBoxElement.md),
  [List Box](ListBoxElement.md)
- [Finding components](../guide/finding-components.md),
  [Assertions](../guide/assertions.md),
  [Actions and waiting](../guide/actions-and-waiting.md),
  [Core concepts](../guide/core-concepts.md)
- Full example: [CheckboxGroupViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/CheckboxGroupViewIT.java)
