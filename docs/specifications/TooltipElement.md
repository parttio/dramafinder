# Tooltip

`TooltipElement` wraps `<vaadin-tooltip>` (Flow: `Tooltip`, created through
`setTooltipText(...)`). It lets a test read a tooltip's text without opening it,
open it by hovering its target, and check its opened state and position.

| | |
|---|---|
| Web component | `<vaadin-tooltip>` |
| Flow component | `com.vaadin.flow.component.shared.Tooltip` |
| Wrapper | `org.vaadin.addons.dramafinder.element.TooltipElement` |
| Typed query | `find(page, TooltipElement.class)` → `TooltipQuery` |
| API | [TooltipElement](../../skills/vaadin-playwright-test/api-reference.md#tooltipelement) |

## Finding the component

Flow's `com.vaadin.flow.component.shared.Tooltip` has no constructor: a tooltip
is created for a component, and the resulting `<vaadin-tooltip slot="tooltip">`
is a light-DOM child of that component. So there is no "id of the tooltip" to
look it up by — scope the lookup to the target instead, or go through the
`HasTooltipElement` mixin:

```java
// View
Button save = new Button("Save");
save.setId("save-button");
save.setTooltipText("Saves the current draft");
```

```java
// Test
// through the mixin — the usual way
ButtonElement saveButton = ButtonElement.getByText(page, "Save");
saveButton.getTooltip().assertText("Saves the current draft");

// scoped to the target component
TooltipElement tooltip = TooltipElement.get(page.locator("#save-button"));

// by the tooltip's own text, when the target is not the interesting part
TooltipElement.getByText(page, "Saves the current draft");

// the first tooltip on the page
TooltipElement first = TooltipElement.get(page);
```

`TooltipElement.getByText` is one of the deliberate "contains" helpers: the text
is matched case-insensitively as a substring and the first match wins, so pass
enough text to be unambiguous. The typed query matches exactly, whether the
tooltip is showing or not:

```java
TooltipElement exact = find(page, TooltipElement.class)
        .withText("Saves the current draft").single();
find(page, TooltipElement.class).withTextContaining("saves");     // substring
find(page, TooltipElement.class).withAccessibleName("Saves the current draft"); // same as withText
```

There is no `getById` / `getByTestId`: Flow gives the tooltip no id of its own.

## The mixin and the element

Every element implementing `HasTooltipElement` (buttons, text fields, …) exposes
its tooltip:

```java
TextFieldElement field = TextFieldElement.getByLabel(page, "Dynamic");
field.assertTooltipHasText("Initial tooltip");
String text = field.getTooltipText();          // read once
Locator content = field.getTooltipLocator();   // the role="tooltip" node
TooltipElement tooltip = field.getTooltip();
```

`HasTooltipElement` is a thin facade over `TooltipElement`:
`assertTooltipHasText(...)` is `getTooltip().assertText(...)`. Use the mixin for
the text, and `getTooltip()` as soon as the test cares about the opened state,
the position, or triggering the tooltip.

## The text is readable while the tooltip is closed

The tooltip content lives in a `role="tooltip"` element that stays in the DOM
whether the tooltip is open or not, so text assertions need no interaction:

```java
// no hover needed
TooltipElement tooltip = ButtonElement.getByText(page, "Hover me").getTooltip();
tooltip.assertClosed();
tooltip.assertText("Tooltip for the hover button");
```

The text follows server-side updates; `assertText(...)` retries:

```java
// View: Button("Update tooltip", e -> dynamicTooltip.setText("Updated tooltip"))
TooltipElement tooltip = TextFieldElement.getByLabel(page, "Dynamic").getTooltip();
tooltip.assertText("Initial tooltip");
ButtonElement.getByText(page, "Update tooltip").click();
tooltip.assertText("Updated tooltip");
```

`assertText(null)` asserts that the tooltip shows nothing. A tooltip with empty
text keeps its overlay hidden, so absence is asserted on the content's
visibility rather than on the element being gone:

```java
// View: empty.setTooltipText("");
TooltipElement tooltip = ButtonElement.getByText(page, "No tooltip text").getTooltip();
tooltip.assertText(null);
```

`getText()` returns the rendered text (a Markdown tooltip without its markup),
an empty string for an empty tooltip. `getContentLocator()` is the
`role="tooltip"` node.

## Opening a tooltip is delayed

`hoverTarget()` moves the pointer to the centre of the element the tooltip
describes — the live `target` element reference, which no selector can express —
after scrolling it into view. Vaadin then waits out the tooltip's hover delay,
500 ms by default, before opening. Follow it with a retrying assertion, never
with the plain getter:

```java
TooltipElement tooltip = ButtonElement.getByText(page, "Hover me").getTooltip();
tooltip.hoverTarget();
tooltip.assertOpened();          // absorbs the hover delay
// tooltip.isOpened()            // would read false, too early

tooltip.closeWithEscape();
tooltip.assertClosed();
```

`hoverTarget()` throws `IllegalStateException` when the tooltip has no target
element.

`closeWithEscape()` presses `Escape` on the page, because the component listens
for the key on the document and the tooltip host cannot be focused. A tooltip in
manual mode ignores both hover and `Escape`; drive it from the server side and
assert the result:

```java
// View
Tooltip manualTooltip = manual.setTooltipText("Opened programmatically").withManual(true);
Button toggle = new Button("Toggle manual tooltip",
        e -> manualTooltip.setOpened(!manualTooltip.isOpened()));
```

```java
// Test
TooltipElement tooltip = TooltipElement.get(page.locator("#tooltip-manual-button"));
tooltip.assertClosed();
ButtonElement.getByText(page, "Toggle manual tooltip").click();
tooltip.assertOpened();
ButtonElement.getByText(page, "Toggle manual tooltip").click();
tooltip.assertClosed();
```

## Assert the opened state, not visibility

`<vaadin-tooltip>` is `display: contents` and renders nothing of its own — what
appears on screen is the `<vaadin-tooltip-overlay>` in its shadow DOM. So the
open/closed assertions read the `opened` attribute the component reflects, which
flips exactly when the tooltip opens and closes:

```java
tooltip.assertOpened();   // opened attribute present
tooltip.assertClosed();   // opened attribute absent
tooltip.isOpened();       // read once, no retry
```

When a test genuinely needs the rendered box — a screenshot, a bounding-box
assertion — reach for the overlay instead of the host:

```java
assertThat(tooltip.getOverlayLocator()).isVisible();
```

## The position is read from the overlay

`Tooltip.setPosition(...)` sets a DOM property on the host and nothing else; it
is the overlay that reflects the *effective* position as an attribute.
`getPosition()` and `assertPosition(...)` read it there, so they answer with the
configured position, or with `bottom` when none was configured:

```java
// no position configured
tooltip.assertPosition("bottom");

// View: positioned.setTooltipText("Shown above the button")
//           .setPosition(Tooltip.TooltipPosition.TOP_START);
TooltipElement.getByText(page, "Shown above the button").assertPosition("top-start");
```

There is no "absent" case, so `assertPosition(null)` is not supported.

## Theme and CSS classes

Flow's `Tooltip` has no API for theme names or classes; when a view sets them on
the `<vaadin-tooltip>` element, the `HasThemeElement` and `HasStyleElement`
assertions read them:

```java
TooltipElement tooltip = TextFieldElement.getByLabel(page, "Styled").getTooltip();
tooltip.assertTheme("custom-tooltip-theme");
tooltip.assertHasThemeVariant("custom-tooltip-theme");
tooltip.assertCssClass("custom-tooltip");
```

## Pitfalls

- **Assert `opened`, not visibility.** The host is `display: contents`;
  `assertVisible()` on it says nothing useful. Use `assertOpened()` /
  `assertClosed()`, or `getOverlayLocator()` for the rendered box.
- **Never follow `hoverTarget()` with `isOpened()`.** The hover delay has not
  elapsed yet; use `assertOpened()`.
- **Manual tooltips ignore hover and Escape.** Open and close them through the
  server.
- **`getByText` is a substring match with `.first()`.** Use
  `find(page, TooltipElement.class).withText(...)` for an exact match.
- **`get(page)` returns the first tooltip on the page.** Scope the lookup to the
  target, or go through the target's `getTooltip()`.
- **`assertPosition(null)` is not supported.** The effective position is never
  absent.

## See also

- [Button](ButtonElement.md), [Text Field](TextFieldElement.md), [Popover](PopoverElement.md)
- [Finding components](../guide/finding-components.md),
  [Assertions](../guide/assertions.md),
  [Actions and waiting](../guide/actions-and-waiting.md),
  [Core concepts](../guide/core-concepts.md)
- Full example: [TooltipViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/TooltipViewIT.java)
