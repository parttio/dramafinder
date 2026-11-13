# Details

`DetailsElement` wraps `<vaadin-details>` (Flow: `Details`), a collapsible
section with a summary and content. It lets a test find the section by its
summary, open and close it, and check its content, tooltip, theme and enabled
state.

| | |
|---|---|
| Web component | `<vaadin-details>` |
| Flow component | `com.vaadin.flow.component.details.Details` |
| Wrapper | `org.vaadin.addons.dramafinder.element.DetailsElement` |
| Typed query | `find(page, DetailsElement.class)` → `ComponentQuery` |
| API | [DetailsElement](../../skills/vaadin-playwright-test/api-reference.md#detailselement) |

## Finding the component

```java
DetailsElement details = DetailsElement.getBySummaryText(page, "Basic Details");
DetailsElement byId = DetailsElement.getById(page, "contact-details");
DetailsElement byTestId = DetailsElement.getByTestId(page, "contact-details");

// Scoped
DetailsElement inForm = DetailsElement.getById(form.getLocator(), "contact-details");
DetailsElement inDialog = dialog.find(DetailsElement.class).withId("advanced").single();

// Typed query: generic filters only
DetailsElement styled = find(page, DetailsElement.class).withClassName("custom-css").single();
```

`getBySummaryText` matches the accessible name of the component's own summary —
its whole text, including the text of any component placed in it —
case-sensitively. Two details with that summary fail with a strict-mode error.
It has no `Locator` overload; use `getById(Locator, …)` or
`element.find(DetailsElement.class)` to search inside a container.

## Opening and closing

```java
// View
Details details = new Details("Basic Details", new Span("Some content."));

Details opened = new Details("Opened Details", new Span("Some content."));
opened.setOpened(true);
```

```java
// Test
DetailsElement details = DetailsElement.getBySummaryText(page, "Basic Details");
details.assertClosed();
details.assertContentNotVisible();

details.setOpen(true);          // clicks the summary only if needed
details.assertOpened();
details.assertContentVisible();

details.toggle();               // always clicks the summary
details.assertClosed();
```

`setOpen(boolean)` is idempotent; `toggle()` flips the state unconditionally.
Both click the summary and wait for the server round-trip. `isOpen()` reads the
`opened` attribute once — prefer `assertOpened()` / `assertClosed()`, which
retry.

## Summary and content

```java
assertThat(details.getContentLocator()).containsText("Some content.");
assertThat(details.getSummaryLocator()).hasText("Basic Details");
String summary = details.getSummaryText(); // textContent of the summary
```

- `getSummaryLocator()` is this component's own `vaadin-details-summary`
  (`:scope > vaadin-details-summary`).
- `getContentLocator()` is the direct child that is neither the summary nor
  slotted (a tooltip is slotted). It resolves whether the details are open or
  closed, so `assertContentNotVisible()` really checks that a closed
  component's content is hidden.

Find fields inside the content through that locator:

```java
details.setOpen(true);
TextFieldElement phone = TextFieldElement.getByLabel(details.getContentLocator(), "Phone");
```

### Components in the summary

A summary can hold components. `getBySummaryText` then matches the summary's
whole accessible name, and the components are reachable through
`getSummaryLocator()`:

```java
// View
Button button = new Button("test", e -> Notification.show("Clicked"));
Details details = new Details(new HorizontalLayout(new Span("Summary Component"), button),
        new Span("Some content."));
```

```java
// Test
DetailsElement details = DetailsElement.getBySummaryText(page, "Summary Component test");
ButtonElement button = ButtonElement.getByText(details.getSummaryLocator(), "test");

details.assertClosed();
button.click();
details.assertOpened(); // the click reached the summary too
```

## Nested details

Summary and content lookups only look at the component's **direct** children,
so details nested in the content do not interfere, and each keeps its own state:

```java
// View
Details inner = new Details("Inner Details", new Span("Inner content."));
Details outer = new Details("Outer Details", new Span("Outer content."), inner);
```

```java
// Test
DetailsElement outer = DetailsElement.getBySummaryText(page, "Outer Details");
outer.setOpen(true);
assertThat(outer.getContentLocator()).hasCount(1);
assertThat(outer.getContentLocator()).containsText("Outer content.");

DetailsElement inner = DetailsElement.getBySummaryText(page, "Inner Details");
inner.toggle();
inner.assertOpened();

outer.setOpen(false);
outer.assertContentNotVisible();
outer.toggle();
inner.assertOpened(); // kept its state while hidden
```

## Disabled state

`DetailsElement` implements `HasDisabledAttributeElement`: enablement is read
from the host's `disabled` attribute, since Playwright does not treat a
disabled `vaadin-details` as disabled.

```java
// View
Details details = new Details("Disabled Details", new Span("Some content."));
details.setEnabled(false);
```

```java
// Test
DetailsElement disabled = DetailsElement.getBySummaryText(page, "Disabled Details");
disabled.assertDisabled();
disabled.assertClosed();

DetailsElement.getBySummaryText(page, "Basic Details").assertEnabled();
```

## Tooltip, theme variants and CSS classes

```java
// View
Details details = new Details("Basic Details", new Span("Some content."));
details.setTooltipText("Tooltip for component");
details.addThemeVariants(DetailsVariant.SMALL);
details.addClassName("custom-css");
```

```java
// Test
DetailsElement details = DetailsElement.getBySummaryText(page, "Basic Details");
details.assertTooltipHasText("Tooltip for component");
details.assertTheme("small");              // the whole theme attribute
details.assertHasThemeVariant("small");    // one variant among several
details.assertCssClass("custom-css");
```

## Pitfalls

- A click on any component placed in the summary also toggles the details, as
  in the browser. Assert the open state after clicking such a component.
- `getBySummaryText` reads the summary's whole accessible name, so with
  components in the summary the text is the concatenation of their texts
  (`"Summary Component test"` above), not the leading label alone.
- `setOpen(true)` on a disabled details clicks the summary, which does nothing,
  and returns without an error. Check the outcome with `assertOpened()`.

## See also

- [Accordion](AccordionElement.md) — several panels, one open at a time
- [Tooltip](TooltipElement.md)
- [Finding components](../guide/finding-components.md),
  [Assertions](../guide/assertions.md),
  [Actions and waiting](../guide/actions-and-waiting.md),
  [Core concepts](../guide/core-concepts.md)
- Full example: [DetailsViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/DetailsViewIT.java)
