# Button

`ButtonElement` wraps `<vaadin-button>` (Flow: `Button`). It finds a button by
the text or accessible name a user sees, clicks it and waits for the server, and
asserts its enabled state, theme variants, prefix/suffix, focus and tooltip.

| | |
|---|---|
| Web component | `<vaadin-button>` |
| Flow component | `com.vaadin.flow.component.button.Button` |
| Wrapper | `org.vaadin.addons.dramafinder.element.ButtonElement` |
| Typed query | `find(page, ButtonElement.class)` → `ButtonQuery` |
| API | [ButtonElement](../../skills/vaadin-playwright-test/api-reference.md#buttonelement) |

## Finding the component

A button has no visible label, so it is found by its **text** (or, for an
icon-only button, its `aria-label`), not with `getByLabel`.

```java
// View
Button save = new Button("Save");
Button saveDraft = new Button("Save draft");
Button close = new Button(VaadinIcon.CLOSE.create());
close.setAriaLabel("Close");
save.setId("save");
save.setTestId("save-btn");
```

```java
// Test
ButtonElement save = ButtonElement.getByText(page, "Save");          // exact: not "Save draft"
ButtonElement draft = ButtonElement.getByTextContaining(page, "draft"); // substring, case-insensitive
ButtonElement close = ButtonElement.getByText(page, "Close");        // the aria-label of an icon button

ButtonElement byId = ButtonElement.getById(page, "save");
ButtonElement byTestId = ButtonElement.getByTestId(page, "save-btn");

// Scoped: only buttons inside the dialog footer
DialogElement dialog = new DialogElement(page);
ButtonElement cancel = ButtonElement.getByText(dialog.getFooterLocator(), "Cancel");
```

What each text lookup matches:

| Factory | Matches | Case | Several matches |
|---|---|---|---|
| `getByText(page, text)` | the `aria-label` when set, otherwise the button's **own** text, in full | sensitive | strict-mode error |
| `getByTextContaining(page, text)` | the same name, as a substring | insensitive (ASCII) | strict-mode error |
| `getByText(page, Page.GetByRoleOptions)` | Playwright's `getByRole(BUTTON, options)`, restricted to `vaadin-button` | as set in the options | Playwright default (strict on actions) |
| `getByLabel` / `getByLabelContaining` | *deprecated* aliases of `getByText` / `getByTextContaining`, kept from 1.x and removed in 3.0 | | |

Every factory has a `Locator` overload for a scoped lookup. The button's
"own" text excludes slotted children, so the prefix, suffix and a tooltip never
become part of the name: `getByText(page, "Tooltip Button")` keeps working while
the tooltip is open.

### Role options

The `GetByRoleOptions` overloads hand the options straight to Playwright's
`getByRole(AriaRole.BUTTON, ...)`. Use them when you need a role-lookup feature
the text factories don't have, such as a regular-expression name or
`setIncludeHidden(true)`. They follow Playwright's rules, not DramaFinder's:
the name is a case-insensitive substring unless you call `setExact(true)`, and
elements under an `aria-hidden` ancestor are skipped.

```java
ButtonElement open = ButtonElement.getByText(page,
        new Page.GetByRoleOptions().setName("Open dialog").setExact(true));

ButtonElement retry = ButtonElement.getByText(page,
        new Page.GetByRoleOptions().setName(Pattern.compile("^Retry \\(\\d+\\)\\?$")));

// Scoped variant takes Locator.GetByRoleOptions
ButtonElement close = ButtonElement.getByText(dialog.getLocator(),
        new Locator.GetByRoleOptions().setName("Close").setExact(true));
```

!!! warning

    Playwright's accessible name of a button includes the text of its slotted
    tooltip while the tooltip is open. A `GetByRoleOptions` lookup with
    `setExact(true)` can therefore stop matching after a hover. The
    `getByText(page, String)` factory reads the button's own text and is not
    affected.

### Typed query

`ButtonQuery` has the text filters and an accessible-name filter that falls back
to the button's own text:

```java
ButtonElement save = find(page, ButtonElement.class).withText("Save").single();
int saves = find(page, ButtonElement.class).withTextContaining("save").count(); // "Save", "Save draft"
ButtonElement close = find(page, ButtonElement.class).withAccessibleName("Close").single();

// Combine with the filters every query has
ButtonElement primarySave = find(page, ButtonElement.class)
        .withAttribute("theme", "primary")
        .withText("Save")
        .single();

// Search inside another component
DialogElement dialog = find(page, DialogElement.class).withAccessibleName("Delete account?").single();
dialog.find(ButtonElement.class).withText("Delete").single().click();
```

`withText` reads only the button's own text; `withAccessibleName` reads the
`aria-label` (or `aria-labelledby`) first, so it also finds icon-only buttons.
`find(page, ButtonElement.class).withLabel(...)` does not compile: a button has
no label.

## Clicking

`click()` clicks the button and returns once the server round-trip it triggers
has finished, so the listener has already run.

```java
// View
Button button = new Button("Click me");
button.addClickListener(e -> Notification.show("Button clicked!"));
```

```java
// Test
ButtonElement.getByText(page, "Click me").click();
NotificationElement.getByText(page, "Button clicked!").assertOpen();
```

The wait covers the server half only. Client-side rendering after the response
(an overlay opening, a list re-rendering) still needs a retrying assertion such
as `assertOpen()` or `assertVisible()`.

## Enabled and disabled

```java
// View
Button toggle = new Button("Toggle Button");
toggle.addClickListener(e -> toggle.setEnabled(!toggle.isEnabled()));
```

```java
// Test
ButtonElement toggle = ButtonElement.getByText(page, "Toggle Button");
toggle.assertEnabled();
toggle.click();
toggle.assertDisabled();
toggle.assertEnabled(false); // same, with the expected state as a parameter
```

`isEnabled()` and `isEnabled(boolean)` return the current state without
retrying; use them only when the test branches on the value.

## Visibility

```java
// View
Button target = new Button("Visibility Target Button");
Button toggle = new Button("Toggle Visibility Button",
        e -> target.setVisible(!target.isVisible()));
```

```java
// Test
ButtonElement target = ButtonElement.getByText(page, "Visibility Target Button");
target.assertVisible();
ButtonElement.getByText(page, "Toggle Visibility Button").click();
target.assertHidden(); // or target.assertVisible(false)
```

## Theme variants and CSS classes

Vaadin writes every variant into one space-separated `theme` attribute.
`assertTheme` checks the whole attribute (in any order);
`assertHasThemeVariant` checks one variant among several.

```java
// View
Button button = new Button("Combined Variants Button");
button.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
button.addClassName("custom-button");
```

```java
// Test
ButtonElement button = ButtonElement.getByText(page, "Combined Variants Button");
button.assertTheme("primary small");          // every variant, any order
button.assertHasThemeVariant("primary");      // one variant, others ignored
button.assertHasNoThemeVariant("error");
button.assertCssClass("custom-button");       // exactly these classes
```

`assertTheme(null)` asserts that the button has no `theme` attribute, and
`assertCssClass(null)` that it has no CSS class.

## Icon, prefix, suffix and aria-label

```java
// View
Button button = new Button();
button.setAriaLabel("Icon Button");
button.setIcon(VaadinIcon.BUTTON.create());
button.setPrefixComponent(new Span("Prefix"));
button.setSuffixComponent(new Span("Suffix"));
```

```java
// Test
ButtonElement icon = ButtonElement.getByText(page, "Icon Button");
icon.assertAriaLabel("Icon Button");
icon.assertPrefixHasText("Prefix");
icon.assertSuffixHasText("Suffix");
```

`assertPrefixHasText(null)` / `assertSuffixHasText(null)` assert that the slot
is empty, and `assertAriaLabel(null)` that there is no `aria-label`. For
anything else inside the slots, use `getPrefixLocator()` / `getSuffixLocator()`
with Playwright's `assertThat`.

## Focus

```java
// View
Button button = new Button("Enabled Button");
button.focus();
```

```java
// Test
ButtonElement.getByText(page, "Enabled Button").assertIsFocused();

ButtonElement next = ButtonElement.getByText(page, "Toggle Button");
next.assertIsNotFocused();
next.focus();
next.assertIsFocused();
next.blur();
```

## Tooltip

The tooltip text is read from the tooltip's content node, so no hover is
needed.

```java
// View
Button button = new Button("Tooltip Button");
button.setTooltipText("Tooltip Text");
```

```java
// Test
ButtonElement button = ButtonElement.getByText(page, "Tooltip Button");
button.assertTooltipHasText("Tooltip Text");

TooltipElement tooltip = button.getTooltip(); // the full tooltip API
```

`assertTooltipHasText(null)` asserts that the button shows no tooltip content.

## Pitfalls

- `getByText(page, String)` matches the **whole** name, case-sensitively:
  `getByText(page, "Save")` never returns "Save draft", and `"save"` finds
  nothing. Use `getByTextContaining` for a fragment; it still fails on several
  matches instead of picking the first.
- Two buttons with the same text fail with a strict-mode error. Scope the lookup
  (`getByText(dialog.getLocator(), "Delete")`, `dialog.find(...)`) instead of
  reaching for `.first()`.
- An `aria-label` replaces the text as the name: a button with text "X" and
  `aria-label` "Close" is found with `"Close"`, not `"X"`.
- The `GetByRoleOptions` overloads keep Playwright's substring default and its
  tooltip-in-name behaviour; pass `setExact(true)` and prefer the `String`
  factories.
- `getByLabel` on a button is a deprecated alias of `getByText`; it does not
  look for a label.

## See also

- [Tooltip](TooltipElement.md), [Dialog](DialogElement.md),
  [Notification](NotificationElement.md)
- [Finding components](../guide/finding-components.md),
  [Assertions](../guide/assertions.md),
  [Actions and waiting](../guide/actions-and-waiting.md),
  [Core concepts](../guide/core-concepts.md)
- Full example: [ButtonViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/ButtonViewIT.java),
  [ElementQueryIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/ElementQueryIT.java)
