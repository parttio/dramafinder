# Form Layout

`FormLayoutElement` wraps `<vaadin-form-layout>` (Flow: `FormLayout`). The
component is purely structural: it has no value, no selection and no state a
user can change. What is worth asserting is the outcome of its layout algorithm
— how many columns it renders at the current width, where it puts the labels,
and which fields it is positioning. That is the whole surface.

| | |
|---|---|
| Web component | `<vaadin-form-layout>` (with `<vaadin-form-item>`, `<vaadin-form-row>`) |
| Flow component | `com.vaadin.flow.component.formlayout.FormLayout` |
| Wrapper | `org.vaadin.addons.dramafinder.element.FormLayoutElement` |
| Typed query | `find(page, FormLayoutElement.class)` → `ComponentQuery` |
| API | [FormLayoutElement](../../skills/vaadin-playwright-test/api-reference.md#formlayoutelement) |

## Finding the component

```java
FormLayoutElement layout = FormLayoutElement.get(page);                    // first on the page
FormLayoutElement inDialog = FormLayoutElement.get(dialog.getLocator());   // first in a scope
FormLayoutElement address = FormLayoutElement.getById(page, "address-form");
FormLayoutElement byTestId = FormLayoutElement.getByTestId(page, "address-form");
FormLayoutElement scoped = FormLayoutElement.getById(dialog.getLocator(), "address-form");

// Typed query: generic filters only
FormLayoutElement demo = find(page, FormLayoutElement.class).withClassName("demo-form").single();
```

To work with the fields themselves, find them with their own elements — the
layout locator is a convenient scope:

```java
TextFieldElement email = TextFieldElement.getByLabel(layout.getLocator(), "Email");
```

## Two layout modes, one API

`FormLayout` lays out in one of two modes, and `getColumnCount()` /
`getLabelPosition()` answer for whichever one is active:

| Mode | Enabled by | Columns come from | Labels are aside when |
|------|------------|-------------------|-----------------------|
| responsive steps (default) | nothing — it is the default | the `responsiveSteps` entry matching the layout's width | that step's `labelsPosition` is not `top` |
| auto-responsive | `setAutoResponsive(true)`, or the `defaultAutoResponsiveFormLayout` feature flag | the CSS grid columns the layout actually created | `setLabelsAside(true)` *and* the layout is wide enough for them |

In auto-responsive mode `responsiveSteps` is ignored by the component, and so is
ignored here.

## Columns and label position

```java
// View: default responsive steps in an 800px container
FormLayout layout = new FormLayout(new TextField("First name"),
        new TextField("Last name"), new TextField("Email"));
layout.setId("default-form");
```

```java
// Test
FormLayoutElement layout = FormLayoutElement.getById(page, "default-form");
layout.assertColumnCount(2);
layout.assertLabelPosition(FormLayoutElement.LABEL_POSITION_ASIDE);

int columns = layout.getColumnCount();        // read once
String position = layout.getLabelPosition();  // "aside" or "top"
```

### Column count is derived, not read off a property

The component exposes no property or attribute holding the current column
count, so `getColumnCount()` recomputes it the same way the component does:
walking `responsiveSteps` and keeping the last one whose `minWidth` still fits
the layout, or counting the rendered CSS grid tracks in auto-responsive mode.

Two consequences:

- The value tracks the layout's **own** width (`offsetWidth`), not the viewport.
  A layout inside a narrow container stays at one column no matter how wide the
  browser window is.
- With responsive steps the value is derived from the steps directly, so it is
  correct even before the component has finished reacting to a resize.
  `assertColumnCount(int)` and `assertLabelPosition(String)` still retry, which
  is what makes them safe right after `page.setViewportSize(...)` in
  auto-responsive mode, where the answer does depend on the rendered grid.

```java
// View: custom steps, in a container sized relative to the viewport
FormLayout layout = new FormLayout(/* six text fields */);
layout.setResponsiveSteps(new ResponsiveStep("0px", 1, LabelsPosition.TOP),
        new ResponsiveStep("400px", 2), new ResponsiveStep("700px", 3));
Div container = new Div(layout);
container.setWidth("40vw");
```

```java
// Test
FormLayoutElement layout = FormLayoutElement.getById(page, "responsive-form");

page.setViewportSize(600, 900);
layout.assertColumnCount(1);
layout.assertLabelPosition(FormLayoutElement.LABEL_POSITION_TOP);

page.setViewportSize(1200, 900);
layout.assertColumnCount(2);
layout.assertLabelPosition(FormLayoutElement.LABEL_POSITION_ASIDE);

page.setViewportSize(2000, 900);
layout.assertColumnCount(3);
```

To make a test deterministic, give the layout a container whose width depends
only on the viewport (`40vw`, say) rather than relying on whatever width the
surrounding application layout happens to leave.

### Auto-responsive mode

```java
// View
FormLayout layout = new FormLayout(new TextField("One"), new TextField("Two"),
        new TextField("Three"), new TextField("Four"));
layout.setAutoResponsive(true);
layout.setAutoRows(true);
layout.setColumnWidth("10em");
layout.setMaxColumns(3);
```

```java
// Test: four fields, but the layout is capped at three columns
FormLayoutElement layout = FormLayoutElement.getById(page, "auto-responsive-form");
layout.assertFieldCount(4);
layout.assertColumnCount(3);
layout.assertLabelPosition(FormLayoutElement.LABEL_POSITION_TOP);
```

### `getLabelPosition()` answers for the layout, not for one field

It reports where the layout *tells* its form items to put their labels. A
layout built from plain fields rather than `FormItem`s still answers — the
fields simply ignore the instruction, since only `vaadin-form-item` renders a
label the layout controls.

With responsive steps the component reflects the same decision onto each form
item as a `label-position="top"` attribute (absent when labels are aside), so
the two agree:

```java
// View: layout.addFormItem(new TextField(), "First name"); in a 200px container
FormLayoutElement narrow = FormLayoutElement.getById(page, "narrow-form");
narrow.assertColumnCount(1);
narrow.assertLabelPosition(FormLayoutElement.LABEL_POSITION_TOP);
assertThat(narrow.getFormItems().first()).hasAttribute("label-position", "top");
```

In auto-responsive mode there is no such attribute — the position is driven by
CSS custom properties — so `getLabelPosition()` is the only way to read it:

```java
// View: setAutoResponsive(true); setLabelsAside(true); setMaxColumns(2), wide container
FormLayoutElement rows = FormLayoutElement.getById(page, "form-row-form");
rows.assertColumnCount(2);
rows.assertLabelPosition(FormLayoutElement.LABEL_POSITION_ASIDE);
```

## Fields and form items

```java
FormLayoutElement layout = FormLayoutElement.getById(page, "default-form");
layout.assertFieldCount(3);
int count = layout.getFieldCount();                    // read once
assertThat(layout.getFields()).hasCount(3);
assertThat(layout.getField(0)).hasText("First name");  // zero-based
assertThat(layout.getField(2)).hasText("Email");
assertThat(layout.getFormItems()).hasCount(0);         // plain fields, no form items
```

### `getFields()` flattens rows and skips line breaks

A field is whatever occupies one cell of the layout: a plain component, or a
`vaadin-form-item` wrapping a label and a field. Two kinds of child are not
fields and are filtered out:

- `<br>`, which only forces a row break.
- `<vaadin-form-row>`, an explicit row grouping in auto-responsive mode. Its
  children are returned instead, so `addFormRow(a, b)` twice yields four fields,
  in document order.

```java
// View: layout.add(new TextField("Email")); layout.add(<br>); layout.add(new TextField("Confirm email"));
FormLayoutElement withBreak = FormLayoutElement.getById(page, "line-break-form");
withBreak.assertFieldCount(2);
assertThat(withBreak.getField(1)).hasText("Confirm email");

// View: layout.addFormRow(street, number); layout.addFormRow(city, country);
FormLayoutElement rows = FormLayoutElement.getById(page, "form-row-form");
rows.assertFieldCount(4);
assertThat(rows.getFormItems()).hasCount(4);
assertThat(rows.getField(0)).hasText("Street");
assertThat(rows.getField(3)).hasText("Country");
```

The lookup uses an XPath union rather than a CSS child selector: Playwright's
CSS pierces the shadow DOM, so `> *` would also match the layout's internal
wrapper element and inflate every count by one.

`getFormItems()`, by contrast, matches `vaadin-form-item` anywhere inside the
layout, whether or not the items sit in rows.

## Theme variants and CSS classes

```java
// View
layout.getElement().setAttribute("theme", "small");
layout.addClassName("demo-form");
```

```java
// Test
layout.assertTheme("small");
layout.assertHasThemeVariant("small");
layout.assertHasNoThemeVariant("large");
layout.assertCssClass("demo-form");
```

## Pitfalls

- The column count follows the layout's own width, not the viewport: size the
  container from the viewport (`40vw`) when a test resizes the page.
- `getColumnCount()` and `getLabelPosition()` read once; after
  `page.setViewportSize(...)` use `assertColumnCount` / `assertLabelPosition`,
  which retry.
- `getLabelPosition()` reports the layout's instruction even when no field obeys
  it (plain fields instead of form items).
- `getFormItems()` also matches form items of a form layout nested inside this
  one; `getFields()` only sees this layout's own cells.

## See also

- [Text Field](TextFieldElement.md) and the other field pages
- [App Layout](AppLayoutElement.md), [Split Layout](SplitLayoutElement.md)
- [Finding components](../guide/finding-components.md),
  [Assertions](../guide/assertions.md),
  [Core concepts](../guide/core-concepts.md)
- Full example: [FormLayoutViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/FormLayoutViewIT.java)
