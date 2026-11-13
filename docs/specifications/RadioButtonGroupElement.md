# Radio Button Group

`RadioButtonGroupElement` wraps `<vaadin-radio-group>` (Flow:
`RadioButtonGroup`). It selects a radio button by its label, asserts which one
is selected, lists the item labels, and covers the group's label, helper,
validation and enabled state.

| | |
|---|---|
| Web component | `<vaadin-radio-group>` (items: `<vaadin-radio-button>`) |
| Flow component | `com.vaadin.flow.component.radiobutton.RadioButtonGroup` |
| Wrapper | `org.vaadin.addons.dramafinder.element.RadioButtonGroupElement` |
| Typed query | `find(page, RadioButtonGroupElement.class)` → `FieldQuery` |
| API | [RadioButtonGroupElement](../../skills/vaadin-playwright-test/api-reference.md#radiobuttongroupelement) |

## Finding the component

```java
// View
RadioButtonGroup<String> group = new RadioButtonGroup<>();
group.setLabel("Basic RadioButtonGroup");
group.setItems("Option 1", "Option 2", "Option 3");
group.setId("basic-radio");
group.setTestId("plan");
```

```java
// Test
RadioButtonGroupElement group = RadioButtonGroupElement.getByLabel(page, "Basic RadioButtonGroup");
RadioButtonGroupElement partial = RadioButtonGroupElement.getByLabelContaining(page, "basic");
RadioButtonGroupElement byId = RadioButtonGroupElement.getById(page, "basic-radio");
RadioButtonGroupElement byTestId = RadioButtonGroupElement.getByTestId(page, "plan");

// Scoped, and the typed query
RadioButtonGroupElement scoped = RadioButtonGroupElement.getByLabel(page.locator("main"), "Basic RadioButtonGroup");
RadioButtonGroupElement viaQuery = find(page, RadioButtonGroupElement.class)
        .withLabel("Basic RadioButtonGroup").single();
```

`getByLabel` / `withLabel` match the group's visible label in full,
case-sensitively; the `...Containing` variants match a case-insensitive
substring. Several matches fail with a strict-mode error.

## Selecting a radio button

Select by the label the user sees. `selectByLabel` checks the radio button and
waits for the server round-trip; `setValue(String)` does the same (its argument
is a **label**, despite the name).

```java
RadioButtonGroupElement group = RadioButtonGroupElement.getByLabel(page, "Basic RadioButtonGroup");
group.assertValue(null);           // nothing selected yet

group.selectByLabel("Option 2");
group.assertValue("Option 2");

group.setValue("Option 3");        // same as selectByLabel("Option 3")
group.assertValue("Option 3");
```

The radio button is matched by its **whole** accessible name (ARIA role
`radio`), case-sensitively: `selectByLabel("Option 1")` never picks
`Option 10`, and two radios sharing a label inside the group fail with a
Playwright strict-mode error rather than resolving to the first one in the DOM.

### selectByValue sets the client-side key

`selectByValue(String)` writes the web component's `value` property directly.
For a Flow `RadioButtonGroup` that value is a server-generated item key
(`"1"`, `"2"`, …), not the item string, so `selectByValue("Option 2")` selects
nothing. Use it only for a client-side `<vaadin-radio-group>` whose radio
buttons carry explicit `value` attributes; with Flow, use `selectByLabel`.

## Asserting the selection

Two assertions read the selection; both retry.

```java
// View
group.setLabel("Pre-selected Value");
group.setValue("Option 2");
```

```java
// Test
RadioButtonGroupElement group = RadioButtonGroupElement.getByLabel(page, "Pre-selected Value");
group.assertValue("Option 2");          // exactly one radio checked, and it is "Option 2"
group.assertSelectedLabel("Option 2");  // the label text of the checked radio
```

- `assertValue(label)` asserts that exactly one radio button is checked and that
  it is the one whose accessible name is `label`. `null` or `""` asserts that no
  radio button is checked.
- `assertSelectedLabel(label)` compares the text of the checked radio's `label`
  slot (whitespace collapsed) with `label`, exactly. `null` asserts that nothing
  is selected.

## Reading labels

When the test needs the values, two getters read them once, without retrying:

```java
List<String> labels = group.getItemLabels();   // ["Option 1", "Option 2", "Option 3"]
String selected = group.getSelectedLabel();    // null when nothing is selected
```

`getItemLabels()` returns the text the user sees, in order — for Flow, the item
label, not the item key held in `value`.

## Individual radio buttons

`getRadioButtonByLabel(String)` returns the `<vaadin-radio-button>` wrapper for
one item. Its class, `RadioButtonElement`, is **package-private**: it is meant
for internal use by the group, its factory is package-private too, and the
checked-state methods it inherits from `HasCheckedElement` cannot be called on
the `RadioButtonElement` type from a test. Drive radio buttons through the
group. When a test really needs one radio, hold it as the public
`HasCheckedElement` mixin:

```java
RadioButtonGroupElement group = RadioButtonGroupElement.getByLabel(page, "Basic RadioButtonGroup");
group.selectByLabel("Option 1");

HasCheckedElement option1 = group.getRadioButtonByLabel("Option 1");
option1.assertChecked();
option1.assertEnabled();
```

## Enabled and disabled

```java
// View
group.setLabel("Disabled RadioButtonGroup");
group.setEnabled(false);
```

```java
// Test
RadioButtonGroupElement.getByLabel(page, "Disabled RadioButtonGroup").assertDisabled();
RadioButtonGroupElement.getByLabel(page, "Basic RadioButtonGroup").assertEnabled();
```

## Label, helper and validation

```java
// View
RadioButtonGroup<String> group = new RadioButtonGroup<>();
group.setLabel("Helper Text");
group.setItems("Option 1", "Option 2", "Option 3");
group.setHelperText("This is a helper text");
```

```java
// Test
RadioButtonGroupElement group = RadioButtonGroupElement.getByLabel(page, "Helper Text");
group.assertLabel("Helper Text");
group.assertHelperHasText("This is a helper text");

group.assertValid();
// after the server marks the group invalid (setInvalid / a binder):
// group.assertInvalid();
// group.assertErrorMessage("Pick one option");
```

## Pitfalls

- `setValue(String)` and `assertValue(String)` take a **label**, while
  `selectByValue(String)` takes the client-side **value** — a Flow item key, not
  the item string.
- Item labels match exactly and case-sensitively; a partial label reaches
  nothing.
- `RadioButtonElement` is package-private. Prefer group-level methods; if you
  need one radio, assign `getRadioButtonByLabel(...)` to `HasCheckedElement`.
- The group has no theme, CSS-class or tooltip helpers; use `getLocator()` with
  Playwright's `assertThat` if you need them.

## See also

- [Checkbox Group](CheckboxGroupElement.md), [Checkbox](CheckboxElement.md),
  [Select](SelectElement.md), [List Box](ListBoxElement.md)
- [Finding components](../guide/finding-components.md),
  [Assertions](../guide/assertions.md),
  [Actions and waiting](../guide/actions-and-waiting.md),
  [Core concepts](../guide/core-concepts.md)
- Full example: [RadioButtonGroupViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/RadioButtonGroupViewIT.java)
