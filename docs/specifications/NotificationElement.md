# NotificationElement

> Full API (methods, signatures, descriptions): see [api-reference.md](../../skills/vaadin-playwright-test/api-reference.md). This note covers only behaviour that isn't obvious from the signatures.

## Constructor scopes to open cards only

The constructor scopes its locator to open cards only (`vaadin-notification-card[slot]`), so closed and never-opened cards are excluded. It targets the single open notification; when several are open at once, use `getByText` to disambiguate.

```java
// Get a specific open notification by (a substring of) its text
NotificationElement errorNotification = NotificationElement.getByText(page, "Error");
errorNotification.assertOpen();
errorNotification.assertContent("Error");
```

## Typed query

`find(page, NotificationElement.class)` searches the same open cards. `withText`
matches the card's whole text exactly (case-sensitive), `withTextContaining` a
case-insensitive substring, and both combine with the common filters — e.g. the
error notification among several with the same text:

```java
NotificationElement error = find(page, NotificationElement.class)
        .withText("Upload finished")
        .withAttribute("theme", "error")
        .single();
int open = find(page, NotificationElement.class).count();
```

The text is the card's light-DOM text. A notification built from components
reads as their texts joined without separators (a `Span` "Saved" and a
`Button` "Undo" give `"SavedUndo"`), so prefer `withTextContaining` there.
`withClassName` sees the classes Flow copies onto the card; `withId` and
`withTestId` do not match, because Flow puts both on `vaadin-notification`, not
on the card.
