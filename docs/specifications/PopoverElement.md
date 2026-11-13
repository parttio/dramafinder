# Popover

`PopoverElement` wraps `<vaadin-popover>` (Flow: `Popover`). It lets a test find
an open popover by its accessible name, check that it opens and closes, and
reach the components inside it.

| | |
|---|---|
| Web component | `<vaadin-popover>` |
| Flow component | `com.vaadin.flow.component.popover.Popover` |
| Wrapper | `org.vaadin.addons.dramafinder.element.PopoverElement` |
| Typed query | `find(page, PopoverElement.class)` → `ComponentQuery` |
| API | [PopoverElement](../../skills/vaadin-playwright-test/api-reference.md#popoverelement) |

## Finding the component

A popover has no visible label. It carries the `dialog` role, and
`Popover.setAriaLabel(...)` gives it an accessible name, which is the key to
find it by:

```java
// View
Button button = new Button("Open Popover");
Popover popover = new Popover();
popover.setAriaLabel("Basic Popover");
popover.add(new Div("This is a popover"));
popover.setTarget(button);
```

```java
// Test
ButtonElement.getByText(page, "Open Popover").click();

// by accessible name: exact, case-sensitive, no .first()
PopoverElement popover = find(page, PopoverElement.class)
        .withAccessibleName("Basic Popover").single();

// the single open popover, found by its dialog role
PopoverElement onlyPopover = new PopoverElement(page);

// fallbacks (each also has a Locator overload)
PopoverElement byId = PopoverElement.getById(page, "help-popover");
PopoverElement byTestId = PopoverElement.getByTestId(page, "help-popover");
```

`withAccessibleNameContaining(...)` matches a case-insensitive part of the name.

!!! note "Deprecated: `getByLabel`"

    `PopoverElement.getByLabel(page, name)` and `getByLabelContaining(page, text)`
    match the accessible name too, despite their name. They are deprecated and
    will be removed in 3.0; use `find(page, PopoverElement.class).withAccessibleName(...)`
    (or `...Containing`) instead.

## Opening and closing

By default a popover opens when its target is clicked, and closes when the
target is clicked again or the user clicks outside it.

```java
ButtonElement openPopover = ButtonElement.getByText(page, "Open Popover");
openPopover.click();

PopoverElement popover = find(page, PopoverElement.class)
        .withAccessibleName("Basic Popover").single();
popover.assertOpen();

openPopover.click();
popover.assertClosed();
```

`assertOpen()` checks the `opened` attribute; `assertClosed()` checks that the
popover is hidden. `isOpen()` reads the visibility once, without waiting — use
it only when the test branches on the state.

A popover with `setCloseOnOutsideClick(false)` stays open while the user
interacts with the rest of the page, so several can be open at once:

```java
// View: stayOpen.setCloseOnOutsideClick(false);
ButtonElement.getByText(page, "Open stay opened Popover").click();
PopoverElement stayOpen = find(page, PopoverElement.class)
        .withAccessibleName("Stay opened Popover").single();
stayOpen.assertOpen();

ButtonElement.getByText(page, "Open component Popover").click();
PopoverElement other = find(page, PopoverElement.class)
        .withAccessibleName("Stay component Popover").single();
other.assertOpen();
stayOpen.assertOpen();
```

## Content and components inside the popover

`getContentLocator()` is the popover itself, so text assertions see everything
it holds. Components inside it are reached with `popover.find(...)` or a factory
scoped to the content locator:

```java
// View
Button close = new Button("Close component Popover");
Popover popover = new Popover();
popover.setAriaLabel("Stay component Popover");
popover.add(new HorizontalLayout(new Span("This is a popover"), close));
popover.setTarget(openPopover);
close.addClickListener(e -> popover.close());
```

```java
// Test
ButtonElement.getByText(page, "Open component Popover").click();
PopoverElement popover = find(page, PopoverElement.class)
        .withAccessibleName("Stay component Popover").single();

assertThat(popover.getContentLocator()).hasText("This is a popoverClose component Popover");

popover.find(ButtonElement.class).withText("Close component Popover").single().click();
// or: ButtonElement.getByText(popover.getContentLocator(), "Close component Popover").click();
popover.assertClosed();
```

## Accessible name, theme and CSS classes

```java
// View
popover.setAriaLabel("Stay component Popover");
popover.addThemeVariants(PopoverVariant.ARROW);
popover.addClassName("component-popover");
```

```java
// Test
popover.assertAriaLabel("Stay component Popover");
popover.assertTheme("arrow");
popover.assertHasThemeVariant("arrow");
popover.assertCssClass("component-popover");
```

`assertAriaLabel(null)` asserts that the popover has no `aria-label`.

## Pitfalls

- **Look the popover up after opening it**, and give it an accessible name
  (`setAriaLabel`) so the lookup is unambiguous: `new PopoverElement(page)`
  resolves any element with the `dialog` role that is a `vaadin-popover`, and
  fails with a strict-mode error when several match.
- **No `closeWithEscape()`.** Unlike `DialogElement`, the wrapper has no Escape
  helper; close a popover through its target, an outside click or a button
  inside it.
- **The content text is joined.** `getContentLocator()` is the whole popover, so
  the texts of several components read as one string without separators
  (`"This is a popoverClose component Popover"`); assert with `containsText` or
  on a nested component when that gets in the way.
- **Clicking the target toggles.** A second `click()` on the target closes the
  popover; don't click it again "to be sure".
- **Opening another popover's target is an outside click.** It closes every open
  popover that keeps the default `closeOnOutsideClick`.

## See also

- [Dialog](DialogElement.md), [Tooltip](TooltipElement.md), [Button](ButtonElement.md)
- [Finding components](../guide/finding-components.md),
  [Assertions](../guide/assertions.md),
  [Actions and waiting](../guide/actions-and-waiting.md),
  [Core concepts](../guide/core-concepts.md)
- Full example: [PopoverViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/PopoverViewIT.java)
