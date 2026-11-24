# Radio Button Group

`RadioButtonGroupElement` wraps `<vaadin-radio-group>` (Flow:
`RadioButtonGroup`). It selects a radio button by its label, asserts which one
is selected, lists the item labels, and covers the group's label, helper,
validation and enabled state.

| | |
|---|---|
| Web component | `<vaadin-radio-group>` (items: `<vaadin-radio-button>`, wrapped by `RadioButtonElement`) |
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
RadioButtonGroupElement group = find(page, RadioButtonGroupElement.class).withLabel("Basic RadioButtonGroup").single();
RadioButtonGroupElement partial = find(page, RadioButtonGroupElement.class).withLabelContaining("basic").single();
RadioButtonGroupElement byId = RadioButtonGroupElement.getById(page, "basic-radio");
RadioButtonGroupElement byTestId = RadioButtonGroupElement.getByTestId(page, "plan");

// Scoped, and the typed query
RadioButtonGroupElement scoped = find(page.locator("main"), RadioButtonGroupElement.class).withLabel("Basic RadioButtonGroup").single();
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
RadioButtonGroupElement group = find(page, RadioButtonGroupElement.class).withLabel("Basic RadioButtonGroup").single();
group.assertValue(null);           // nothing selected yet

group.selectByLabel("Option 2");
group.assertValue("Option 2");

group.setValue("Option 3");        // same as selectByLabel("Option 3")
group.assertValue("Option 3");
```

The radio button is matched by its **whole** visible label,
case-sensitively: `selectByLabel("Option 1")` never picks
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
RadioButtonGroupElement group = find(page, RadioButtonGroupElement.class).withLabel("Pre-selected Value").single();
group.assertValue("Option 2");          // exactly one radio checked, and it is "Option 2"
group.assertSelectedLabel("Option 2");  // the label text of the checked radio
```

- `assertValue(label)` asserts that exactly one radio button is checked and that
  it is the one whose visible label is `label`. `null` or `""` asserts that no
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

`getRadioButtonByLabel(String)` returns the `RadioButtonElement` for one item,
the wrapper of `<vaadin-radio-button>`. It has the checked-state, label,
helper, focus and enabled-state API of the other check controls:

```java
RadioButtonGroupElement group = find(page, RadioButtonGroupElement.class).withLabel("Basic RadioButtonGroup").single();
RadioButtonElement option2 = group.getRadioButtonByLabel("Option 2");
option2.assertNotChecked();
option2.check();
option2.assertChecked();
group.assertValue("Option 2");
```

`RadioButtonElement` also has the usual factories (`getByLabel`,
`getByLabelContaining`, `getById`, `getByTestId`, each with a `Page` and a
`Locator` overload) and a typed query, `find(page, RadioButtonElement.class)`
→ `FieldQuery`. Flow has no standalone radio button, so scope these to the
group: `group.find(RadioButtonElement.class).withLabel("Option 3").single()`.

## Enabled and disabled

```java
// View
group.setLabel("Disabled RadioButtonGroup");
group.setEnabled(false);
```

```java
// Test
find(page, RadioButtonGroupElement.class).withLabel("Disabled RadioButtonGroup").single().assertDisabled();
find(page, RadioButtonGroupElement.class).withLabel("Basic RadioButtonGroup").single().assertEnabled();
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
RadioButtonGroupElement group = find(page, RadioButtonGroupElement.class).withLabel("Helper Text").single();
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
