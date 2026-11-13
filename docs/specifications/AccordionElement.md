# Accordion

`AccordionElement` wraps `<vaadin-accordion>` (Flow: `Accordion`) and
`AccordionPanelElement` wraps each of its `<vaadin-accordion-panel>` children
(Flow: `AccordionPanel`). Together they let a test open and close panels by
their summary, check which panel is open, read a panel's content and check
whether a panel is disabled.

| | |
|---|---|
| Web component | `<vaadin-accordion>`, `<vaadin-accordion-panel>` |
| Flow component | `com.vaadin.flow.component.accordion.Accordion`, `AccordionPanel` |
| Wrapper | `org.vaadin.addons.dramafinder.element.AccordionElement`, `AccordionPanelElement` |
| Typed query | `find(page, AccordionElement.class)` / `find(page, AccordionPanelElement.class)` → `ComponentQuery` |
| API | [AccordionElement](../../skills/vaadin-playwright-test/api-reference.md#accordionelement), [AccordionPanelElement](../../skills/vaadin-playwright-test/api-reference.md#accordionpanelelement) |

## Finding the component

An accordion has no label of its own. Find it by id, test id, class name, or by
wrapping a locator:

```java
AccordionElement accordion = AccordionElement.getById(page, "faq");
AccordionElement byTestId = AccordionElement.getByTestId(page, "faq");
AccordionElement scoped = AccordionElement.getById(dialog.getLocator(), "faq");

// Typed query: generic filters only (id, test id, attribute, class name, accessible name)
AccordionElement byClass = find(page, AccordionElement.class).withClassName("custom-css").single();

// Or wrap any locator
AccordionElement wrapped = new AccordionElement(page.locator(".custom-css"));
```

Panels are found through the accordion, by their **full** summary text:

```java
AccordionPanelElement panel = accordion.getPanel("Panel 1");

// The same lookups as static factories, scoped to any locator
AccordionPanelElement same = AccordionPanelElement.getAccordionPanelBySummary(accordion.getLocator(), "Panel 1");
AccordionPanelElement opened = AccordionPanelElement.getOpenedAccordionPanel(accordion.getLocator());

// By id or test id
AccordionPanelElement shipping = AccordionPanelElement.getById(page, "shipping-panel");
AccordionPanelElement billing = AccordionPanelElement.getByTestId(accordion.getLocator(), "billing-panel");
```

The summary is matched exactly and case-sensitively: `getPanel("Panel 1")` never
resolves to `"Panel 10"`, and two panels with the same summary fail with a
Playwright strict-mode error instead of picking the first one.

## Opening and closing panels

```java
// View
Accordion accordion = new Accordion();
accordion.addClassName("custom-css");
accordion.add("Panel 1", new Span("Content 1"));
accordion.add("Panel 2", new Span("Content 2"));
accordion.add("Panel 3", new Span("Content 3"));
```

```java
// Test
AccordionElement accordion = new AccordionElement(page.locator(".custom-css"));
accordion.assertPanelCount(3);

accordion.assertPanelOpened("Panel 1");   // Flow opens the first panel by default
accordion.assertPanelClosed("Panel 2");

accordion.openPanel("Panel 2");
accordion.assertPanelOpened("Panel 2");
accordion.assertPanelClosed("Panel 1");   // only one panel is open at a time

accordion.closePanel("Panel 2");
accordion.assertPanelClosed("Panel 2");
```

`openPanel` / `closePanel` delegate to `AccordionPanelElement.setOpen(boolean)`,
which clicks the summary only when the panel is not already in the requested
state, then waits for the server round-trip. The same methods are on the panel:

```java
AccordionPanelElement panel = accordion.getPanel("Panel 3");
panel.setOpen(true);
panel.assertOpened();
panel.setOpen(false);
panel.assertClosed();
```

`isPanelOpened(summary)` and `panel.isOpen()` read the `opened` attribute once,
without retrying — use them for branching, and the `assert…` methods to verify.

## The opened panel

`getOpenedPanel()` returns the panel whose heading carries `opened`:

```java
accordion.openPanel("Panel 2");
AccordionPanelElement opened = accordion.getOpenedPanel();
assertThat(opened.getSummaryLocator()).hasText("Panel 2");
```

When every panel is closed, its locator matches nothing; check that with
`assertThat(accordion.getOpenedPanel().getLocator()).hasCount(0)`.

## Summary and content

```java
AccordionPanelElement panel = accordion.getPanel("Panel 1");

panel.assertContentVisible();
assertThat(panel.getContentLocator()).hasText("Content 1");

accordion.openPanel("Panel 2");
panel.assertContentNotVisible();

assertThat(panel.getSummaryLocator()).hasText("Panel 1");
String summary = panel.getSummaryText(); // raw textContent of the heading
```

- `getSummaryLocator()` is the `<vaadin-accordion-heading>` of the panel.
- `getContentLocator()` is the panel's **first** child without a `slot`
  attribute — the component Flow added as the panel content. Chain from it to
  reach fields inside:

```java
TextFieldElement street = TextFieldElement.getByLabel(panel.getContentLocator(), "Street");
```

## Disabled panels

`AccordionPanelElement` implements `HasDisabledAttributeElement`: enablement is
read from the `disabled` attribute of the panel host, because Playwright does
not treat a disabled `vaadin-accordion-panel` as disabled.

```java
// View
AccordionPanel disabledPanel = accordion.add("Disabled Panel", new Span("No content"));
disabledPanel.setEnabled(false);
```

```java
// Test
AccordionPanelElement disabled = accordion.getPanel("Disabled Panel");
disabled.assertDisabled();
disabled.assertClosed();

disabled.getSummaryLocator().click();  // a click on a disabled panel does nothing
disabled.assertClosed();

accordion.getPanel("Panel 2").assertEnabled();
boolean enabled = disabled.isEnabled();       // false
boolean matches = disabled.isEnabled(false);  // true
```

## CSS classes

`AccordionElement` implements `HasStyleElement`:

```java
accordion.assertCssClass("custom-css");
```

## Pitfalls

- `openPanel` on a disabled panel clicks the summary, which does nothing, and
  returns without an error. Follow it with `assertPanelOpened(…)` when the
  outcome matters.
- `getContentLocator()` only returns the first unslotted child. A panel whose
  content is several sibling components exposes only the first; wrap them in a
  layout, or locate the others from `panel.getLocator()`.
- `getPanel` and the summary factories match the heading's whole text: a
  heading made of a component with extra text (an icon label, a badge) has to be
  matched by that whole text, or found by id/test id.

## See also

- [Details](DetailsElement.md) — a single collapsible section
- [Tabs](TabsElement.md), [Tab Sheet](TabSheetElement.md)
- [Finding components](../guide/finding-components.md),
  [Assertions](../guide/assertions.md),
  [Actions and waiting](../guide/actions-and-waiting.md),
  [Core concepts](../guide/core-concepts.md)
- Full example: [AccordionViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/AccordionViewIT.java)
