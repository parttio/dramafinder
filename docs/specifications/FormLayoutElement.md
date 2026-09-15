# FormLayoutElement

> Full API (methods, signatures, descriptions): see [api-reference.md](../../skills/vaadin-playwright-test/api-reference.md). This note covers only behaviour that isn't obvious from the signatures.

`vaadin-form-layout` is purely structural: it has no value, no selection and no
state a user can change. What is worth asserting is the outcome of its layout
algorithm — how many columns it renders at the current width, where it puts the
labels, and which fields it is positioning. That is the whole surface.

## Two layout modes, one API

`FormLayout` lays out in one of two modes, and `getColumnCount()` /
`getLabelPosition()` answer for whichever one is active:

| Mode | Enabled by | Columns come from | Labels are aside when |
|------|------------|-------------------|-----------------------|
| responsive steps (default) | nothing — it is the default | the `responsiveSteps` entry matching the layout's width | that step's `labelsPosition` is not `top` |
| auto-responsive | `setAutoResponsive(true)`, or the `defaultAutoResponsiveFormLayout` feature flag | the CSS grid columns the layout actually created | `setLabelsAside(true)` *and* the layout is wide enough for them |

In auto-responsive mode `responsiveSteps` is ignored by the component, and so is
ignored here.

```java
FormLayoutElement layout = FormLayoutElement.getById(page, "address-form");
layout.assertColumnCount(2);
layout.assertLabelPosition(FormLayoutElement.LABEL_POSITION_ASIDE);
```

## Column count is derived, not read off a property

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
page.setViewportSize(1600, 900);
layout.assertColumnCount(3);

page.setViewportSize(600, 900);
layout.assertColumnCount(1);
layout.assertLabelPosition(FormLayoutElement.LABEL_POSITION_TOP);
```

To make a test deterministic, give the layout a container whose width depends
only on the viewport (`40vw`, say) rather than relying on whatever width the
surrounding application layout happens to leave.

## `getLabelPosition()` answers for the layout, not for one field

It reports where the layout *tells* its form items to put their labels. A
layout built from plain fields rather than `FormItem`s still answers — the
fields simply ignore the instruction, since only `vaadin-form-item` renders a
label the layout controls.

With responsive steps the component reflects the same decision onto each form
item as a `label-position="top"` attribute (absent when labels are aside), so
the two agree:

```java
layout.assertLabelPosition(FormLayoutElement.LABEL_POSITION_TOP);
assertThat(layout.getFormItems().first()).hasAttribute("label-position", "top");
```

In auto-responsive mode there is no such attribute — the position is driven by
CSS custom properties — so `getLabelPosition()` is the only way to read it.

## `getFields()` flattens rows and skips line breaks

A field is whatever occupies one cell of the layout: a plain component, or a
`vaadin-form-item` wrapping a label and a field. Two kinds of child are not
fields and are filtered out:

- `<br>`, which only forces a row break.
- `<vaadin-form-row>`, an explicit row grouping in auto-responsive mode. Its
  children are returned instead, so `addFormRow(a, b)` twice yields four fields,
  in document order.

```java
// layout.addFormRow(street, number); layout.addFormRow(city, country);
layout.assertFieldCount(4);
assertThat(layout.getField(0)).hasText("Street");
```

The lookup uses an XPath union rather than a CSS child selector: Playwright's
CSS pierces the shadow DOM, so `> *` would also match the layout's internal
wrapper element and inflate every count by one.

`getFormItems()`, by contrast, matches `vaadin-form-item` anywhere inside the
layout, whether or not the items sit in rows.
