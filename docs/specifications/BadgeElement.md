# Badge

`BadgeElement` wraps `<vaadin-badge>` (Flow: `Badge`, in the catalogue since
Vaadin 25.1). It reads and asserts the badge's text, number, icon and theme
variants.

| | |
|---|---|
| Web component | `<vaadin-badge>` |
| Flow component | `com.vaadin.flow.component.badge.Badge` |
| Wrapper | `org.vaadin.addons.dramafinder.element.BadgeElement` |
| Typed query | `find(page, BadgeElement.class)` → `TextQuery` |
| API | [BadgeElement](../../skills/vaadin-playwright-test/api-reference.md#badgeelement) |

!!! note "It targets the real `vaadin-badge`, not a themed `Span`"

    The older idiom of styling a `Span` with `theme="badge"` produces a `<span>`,
    not a `<vaadin-badge>`, so it is **not** matched by any `BadgeElement`
    factory. Locate those with a plain Playwright locator:

    ```java
    // vaadin-badge component
    BadgeElement badge = BadgeElement.getByText(page, "Completed");

    // legacy Span theme="badge" — not a BadgeElement
    assertThat(page.locator("span[theme~='badge']")).hasText("Completed");
    ```

## Finding the component

```java
// View
Badge scoped = new Badge("Scoped");
Div container = new Div(scoped);
container.setId("badge-container");
Badge draft = new Badge("Draft");
draft.setId("badge-contrast");
```

```java
// Test
BadgeElement draft = BadgeElement.getByText(page, "Draft");
BadgeElement scoped = BadgeElement.getByText(page.locator("#badge-container"), "Scoped");
BadgeElement byId = BadgeElement.getById(page, "badge-contrast");
BadgeElement byTestId = BadgeElement.getByTestId(page, "order-status");
BadgeElement first = BadgeElement.get(page);                                // first badge on the page
BadgeElement firstInContainer = BadgeElement.get(page.locator("#badge-container"));
```

`getByText` is one of the library's deliberate **"contains" helpers**: the text is
matched **case-insensitively as a substring** and the first match wins, so pass
enough text to be unambiguous.

The typed query reads the badge's **own** text only — no number, no icon — and
matches it exactly with `withText`, or as a case-insensitive substring with
`withTextContaining`:

```java
import static org.vaadin.addons.dramafinder.element.query.ElementQueries.find;

BadgeElement verified = find(page, BadgeElement.class).withText("Verified").single();
int newBadges = find(page, BadgeElement.class).withTextContaining("new").count();
```

### `getByText(...)` matches the number too

`getByText` filters with Playwright's `hasText`, which traverses the shadow DOM, so
the number the badge renders there is part of the matched text:

```java
// View: the numbered badge comes before the "5 minutes" one
Badge unread = new Badge("unread messages", 5);
unread.addThemeVariants(BadgeVariant.NUMBER_ONLY);
Badge minutes = new Badge("5 minutes");
```

```java
// Test
BadgeElement badge = BadgeElement.getByText(page, "5");   // both match; the first one wins
badge.assertText("unread messages");                       // it is the numbered badge
badge.assertNumber(5);

BadgeElement.getByText(page, "unread");                    // unambiguous
```

Pass some of the badge's own text to avoid matching a number by accident, or use
the typed query, whose text filters ignore the number.

## Text, number and icon

A badge can carry three separate pieces of content at once:

| Content  | Where it lives                     | Read with                        |
|----------|------------------------------------|----------------------------------|
| text     | default slot (light DOM)           | `getText()` / `assertText(...)`  |
| number   | `number` property, rendered in the component's shadow DOM | `getNumber()` / `assertNumber(...)` |
| icon     | `icon` slot (light DOM)            | `getIconLocator()`, `assertHasIcon()` / `assertHasNoIcon()` |

### Text

```java
// View
Badge plain = new Badge("Pending");
Badge withTextIcon = new Badge("Verified", new Span("✓"));   // host textContent is "Verified✓"
Badge dot = new Badge();
dot.addThemeVariants(BadgeVariant.DOT);
```

```java
// Test
BadgeElement.getById(page, "badge-plain").assertText("Pending");
BadgeElement.getById(page, "badge-with-text-icon").assertText("Verified");
BadgeElement.getById(page, "badge-dot").assertText(null);   // null: no text at all
```

`getText()` and `assertText(...)` read only the child nodes that carry no `slot`
attribute — the badge's own content. Neither the number (shadow DOM) nor an
icon's light-DOM text is included. `assertText` compares exactly, without
whitespace normalization, retries, and reports the expected and the actual text
on failure. `getText()` returns an empty string for a badge with no text.

!!! warning "Don't assert the text on the host locator"

    `assertThat(badge.getLocator()).hasText("unread messages")` fails on a
    numbered badge, because Playwright's text assertions traverse the shadow DOM
    and also pick up the rendered number. Use `badge.assertText(...)`.

### Number

```java
// View
Badge unread = new Badge("unread messages", 5);
```

```java
// Test
BadgeElement badge = BadgeElement.getById(page, "badge-with-number");
badge.assertNumber(5);
badge.assertText("unread messages");   // the number is not part of the text

BadgeElement.getById(page, "badge-plain").assertNumber(null);   // no number
```

`assertNumber(null)` asserts the `has-number` attribute is absent; the component
sets it exactly when the number is not `null`. `getNumber()` returns `null` when
there is no number.

### Icon

```java
// View
Badge withIcon = new Badge("Verified", VaadinIcon.CHECK.create());
```

```java
// Test
BadgeElement badge = BadgeElement.getById(page, "badge-with-icon");
badge.assertHasIcon();
badge.assertText("Verified");
assertThat(badge.getIconLocator()).hasCount(1);   // the slotted icon

BadgeElement.getById(page, "badge-without-icon").assertHasNoIcon();
```

`getIconLocator()` matches nothing when no icon is set.

## A badge updated by the server

The assertions retry, so they wait for the server round-trip:

```java
// View
Badge dynamic = new Badge("Initial");
Button update = new Button("Update badge", e -> {
    dynamic.setText("Updated");
    dynamic.setNumber(9);
    dynamic.addThemeVariants(BadgeVariant.SUCCESS);
});
```

```java
// Test
BadgeElement badge = BadgeElement.getById(page, "badge-dynamic");
badge.assertText("Initial");
badge.assertNumber(null);

ButtonElement.getByText(page, "Update badge").click();
badge.assertText("Updated");
badge.assertNumber(9);
badge.assertHasThemeVariant("success");
```

## Theme variants

`Badge.addThemeVariants(...)` appends to a single space-separated `theme`
attribute, so `assertTheme(...)` has to list every applied variant — though their
order does not matter, it compares them as an unordered set. Use
`assertHasThemeVariant(...)` to assert one variant regardless of the others.
Both live on `HasThemeElement`, so they are available on every themed element:

```java
// View
Badge synced = new Badge("Synced");
synced.addThemeVariants(BadgeVariant.SUCCESS, BadgeVariant.SMALL);
```

```java
// Test
BadgeElement badge = BadgeElement.getById(page, "badge-small-success");
badge.assertTheme("success small");        // whole attribute, any order
badge.assertHasThemeVariant("success");    // one variant among several
badge.assertHasNoThemeVariant("error");

BadgeElement.getById(page, "badge-plain").assertTheme(null);   // no theme attribute
```

The variants the component supports are `success`, `error`, `contrast`,
`warning`, `small`, `filled`, `dot`, `icon-only` and `number-only`.

### `dot`, `icon-only` and `number-only` only hide content visually

Those variants render the hidden content as screen-reader-only rather than
removing it, so the badge stays visible and the text and number assertions keep
working:

```java
BadgeElement dot = BadgeElement.getById(page, "badge-dot");   // new Badge() + BadgeVariant.DOT
dot.assertVisible();
dot.assertText(null);   // a dot badge genuinely has no text

BadgeElement unread = BadgeElement.getById(page, "badge-with-number");   // NUMBER_ONLY
unread.assertText("unread messages");                                    // still readable
```

## Style

```java
BadgeElement badge = BadgeElement.getById(page, "badge-with-class");   // addClassName("custom-badge")
badge.assertCssClass("custom-badge");
```

## Pitfalls

- **Legacy `Span` badges** (`theme="badge"`) are not `vaadin-badge` elements; use
  a plain locator.
- **`getByText` is a substring match that includes the number** and takes the
  first match. The typed query's `withText` / `withTextContaining` read the own
  text only.
- **Playwright `hasText` on the host includes the number.** Use `assertText`.
- **`assertTheme` needs every variant.** Use `assertHasThemeVariant` for one.

## See also

- [Avatar](AvatarElement.md)
- [Assertions](../guide/assertions.md)
- [Finding components](../guide/finding-components.md)
- [Core concepts](../guide/core-concepts.md)
- Full example: [BadgeViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/BadgeViewIT.java)
