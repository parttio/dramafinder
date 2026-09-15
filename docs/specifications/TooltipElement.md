# TooltipElement

> Full API (methods, signatures, descriptions): see [api-reference.md](../../skills/vaadin-playwright-test/api-reference.md). This note covers only behaviour that isn't obvious from the signatures.

## A tooltip is always reached through the component it describes

Flow's `com.vaadin.flow.component.shared.Tooltip` has no constructor: a tooltip
is created for a component, and the resulting `<vaadin-tooltip slot="tooltip">`
is a light-DOM child of that component. So there is no "id of the tooltip" to
look it up by — scope the lookup to the target instead, or go through the
`HasTooltipElement` mixin:

```java
// through the mixin — the usual way
ButtonElement save = ButtonElement.getByText(page, "Save");
save.getTooltip().assertText("Saves the current draft");

// scoped to the target component
TooltipElement tooltip = TooltipElement.get(page.locator("#save-button"));

// by the tooltip's own text, when the target is not the interesting part
TooltipElement.getByText(page, "Saves the current draft");
```

`HasTooltipElement` is a thin facade over `TooltipElement`:
`assertTooltipHasText(...)` is `getTooltip().assertText(...)`. Use the mixin for
the text, and `getTooltip()` as soon as the test cares about the opened state,
the position, or triggering the tooltip.

## Assert the opened state, not visibility

`<vaadin-tooltip>` is `display: contents` and renders nothing of its own — what
appears on screen is the `<vaadin-tooltip-overlay>` in its shadow DOM. So the
open/closed assertions read the `opened` attribute the component reflects, which
flips exactly when the tooltip opens and closes:

```java
tooltip.assertOpened();   // opened attribute present
tooltip.assertClosed();   // opened attribute absent
tooltip.isOpened();
```

When a test genuinely needs the rendered box — a screenshot, a bounding-box
assertion — reach for the overlay instead of the host:

```java
assertThat(tooltip.getOverlayLocator()).isVisible();
```

## The text is readable while the tooltip is closed

The tooltip content lives in a `role="tooltip"` element that stays in the DOM
whether the tooltip is open or not, so text assertions need no interaction:

```java
// no hover needed
field.assertTooltipHasText("Tooltip for the field");
```

`assertText(null)` asserts that the tooltip shows nothing. A tooltip with empty
text keeps its overlay hidden, so absence is asserted on the content's
visibility rather than on the element being gone.

## Opening a tooltip is delayed

`hoverTarget()` moves the pointer to the centre of the element the tooltip
describes — the live `target` element reference, which no selector can express —
after scrolling it into view. Vaadin then waits out the tooltip's hover delay,
500 ms by default, before opening. Follow it with a retrying assertion, never
with the plain getter:

```java
tooltip.hoverTarget();
tooltip.assertOpened();          // absorbs the hover delay
// tooltip.isOpened()            // would read false, too early

tooltip.closeWithEscape();
tooltip.assertClosed();
```

`closeWithEscape()` presses `Escape` on the page, because the component listens
for the key on the document and the tooltip host cannot be focused. A tooltip in
manual mode ignores both hover and `Escape`; drive it from the server side and
assert the result:

```java
// tooltip.setManual(true) on the server
ButtonElement.getByText(page, "Show tooltip").click();
tooltip.assertOpened();
```

## The position is read from the overlay

`Tooltip.setPosition(...)` sets a DOM property on the host and nothing else; it
is the overlay that reflects the *effective* position as an attribute.
`getPosition()` and `assertPosition(...)` read it there, so they answer with the
configured position, or with `bottom` when none was configured:

```java
// no position configured
tooltip.assertPosition("bottom");

// Tooltip.TooltipPosition.TOP_START
tooltip.assertPosition("top-start");
```

There is no "absent" case, so `assertPosition(null)` is not supported.
