# BadgeElement

> Full API (methods, signatures, descriptions): see [api-reference.md](../../skills/vaadin-playwright-test/api-reference.md). This note covers only behaviour that isn't obvious from the signatures.

## It targets the real `vaadin-badge`, not a `Span` with `theme="badge"`

`BadgeElement` wraps the `vaadin-badge` component (server class
`com.vaadin.flow.component.badge.Badge`, in the catalogue since Vaadin 25.1).
The older idiom of styling a `Span` with `theme="badge"` produces a `<span>`,
not a `<vaadin-badge>`, so it is **not** matched by any `BadgeElement` factory.
Locate those with a plain Playwright locator instead:

```java
// vaadin-badge component
BadgeElement badge = BadgeElement.getByText(page, "Completed");

// legacy Span theme="badge" — not a BadgeElement
assertThat(page.locator("span[theme~='badge']")).hasText("Completed");
```

## Text, number and icon are three separate pieces of content

A badge can carry all three at once:

| Content  | Where it lives                     | Read with                        |
|----------|------------------------------------|----------------------------------|
| text     | default slot (light DOM)           | `getText()` / `assertText(...)`  |
| number   | `number` property, rendered in the component's shadow DOM | `getNumber()` / `assertNumber(...)` |
| icon     | `icon` slot (light DOM)            | `getIconLocator()`, `assertHasIcon()` |

`getText()` and `assertText(...)` read the badge's own text content, so the
number is **not** included:

```java
// new Badge("unread messages", 5) with BadgeVariant.NUMBER_ONLY
badge.assertText("unread messages");
badge.assertNumber(5);
```

Note that a raw Playwright text assertion on the host element behaves
differently: `assertThat(badge.getLocator()).hasText("unread messages")` fails,
because Playwright's text assertions traverse the shadow DOM and therefore also
pick up the rendered number. Use `assertText(...)` — it compares the badge's
text content exactly, without whitespace normalization.

## Theme variants combine into one attribute

`Badge.addThemeVariants(...)` appends to a single space-separated `theme`
attribute, so `assertTheme(...)` (from `HasThemeElement`) has to match the whole
value. Use `assertHasThemeVariant(...)` to assert one variant regardless of the
others:

```java
// badge.addThemeVariants(BadgeVariant.SUCCESS, BadgeVariant.SMALL)
badge.assertTheme("success small");        // whole attribute
badge.assertHasThemeVariant("success");    // one variant among several
badge.assertHasNoThemeVariant("error");
```

The variants the component supports are `success`, `error`, `contrast`,
`warning`, `small`, `filled`, `dot`, `icon-only` and `number-only`.

## The `dot`, `icon-only` and `number-only` variants only hide content visually

Those variants render the hidden content as screen-reader-only rather than
removing it, so the badge stays visible and the text/number assertions keep
working:

```java
// new Badge() with BadgeVariant.DOT
badge.assertVisible();
badge.assertText(null);   // a dot badge genuinely has no text
```
