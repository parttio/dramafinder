# Notification

`NotificationElement` wraps an open `<vaadin-notification-card>` (Flow:
`Notification`). It lets a test find a notification by its text, check its
content, theme and class, click components inside it, and wait for it to close.

| | |
|---|---|
| Web component | `<vaadin-notification>` (the wrapper targets its `<vaadin-notification-card>`) |
| Flow component | `com.vaadin.flow.component.notification.Notification` |
| Wrapper | `org.vaadin.addons.dramafinder.element.NotificationElement` |
| Typed query | `find(page, NotificationElement.class)` → `TextQuery` (open cards only) |
| API | [NotificationElement](../../skills/vaadin-playwright-test/api-reference.md#notificationelement) |

## Finding the component

```java
// View
Notification notification = new Notification("This is a notification.");
notification.setDuration(400);
add(new Button("Show notification", e -> notification.open()));
```

```java
// Test
ButtonElement.getByText(page, "Show notification").click();

// the single open notification
NotificationElement notification = new NotificationElement(page);
notification.assertOpen();

// by (a substring of) its text, case-insensitive
NotificationElement byText = NotificationElement.getByText(page, "notification");

// typed query: exact text, case-sensitive
NotificationElement exact = find(page, NotificationElement.class)
        .withText("This is a notification.").single();
```

The constructor scopes its locator to open cards only
(`vaadin-notification-card[slot]`), so closed and never-opened cards are
excluded. It targets the single open notification; when several are open at
once, use `getByText` or the typed query to disambiguate.

`getByText` is one of the deliberate "contains" helpers: it matches a
case-insensitive substring, because notification text is free-form content
rather than a label. Using the element still fails in strict mode when several
open notifications contain the text. For an exact match, use
`find(page, NotificationElement.class).withText(text)`.

There is no `getById` / `getByTestId`: Flow puts the id and test id on
`<vaadin-notification>`, not on the card.

## Several open notifications

```java
// View
Notification first = new Notification("First notification");
first.setDuration(0);
first.open();
Notification second = new Notification("Second notification");
second.setDuration(0);
second.open();
```

```java
// Test
NotificationElement first = NotificationElement.getByText(page, "First notification");
first.assertOpen();
first.assertContent("First notification");

NotificationElement second = find(page, NotificationElement.class)
        .withText("Second notification").single();
second.assertOpen();

// how many are open (retrying)
assertThat(find(page, NotificationElement.class).getLocator()).hasCount(2);
```

## Typed query

`find(page, NotificationElement.class)` searches the same open cards. `withText`
matches the card's whole text exactly (case-sensitive), `withTextContaining` a
case-insensitive substring, and both combine with the common filters — e.g. the
error notification among several with the same text:

```java
// View: three notifications open at once
//   "Upload finished" (LUMO_ERROR), "Upload finished" (LUMO_SUCCESS),
//   "Upload finished with warnings"
```

```java
// Test
NotificationElement error = find(page, NotificationElement.class)
        .withText("Upload finished")
        .withAttribute("theme", "error")
        .single();
error.assertTheme("error");

assertThat(find(page, NotificationElement.class).withText("Upload finished").getLocator())
        .hasCount(2);
assertThat(find(page, NotificationElement.class).withTextContaining("upload FINISHED").getLocator())
        .hasCount(3);
```

The text is the card's light-DOM text. A notification built from components
reads as their texts joined without separators (a `Span` "Saved" and a
`Button` "Undo" give `"SavedUndo"`), so prefer `withTextContaining` there.
`withClassName` sees the classes Flow copies onto the card; `withId` and
`withTestId` do not match, because Flow puts both on `vaadin-notification`, not
on the card.

## Checking the content

`assertContent(...)` asserts that the card **contains** the text (a
case-sensitive substring, retrying). For an exact match, assert on
`getContentLocator()`, which is the card itself:

```java
notification.assertContent("This is a notification.");
assertThat(notification.getContentLocator()).hasText("This is a notification.");
```

## Clicking components inside a notification

A notification built from components (a close button, an "Undo" action) is
interacted with by scoping a factory to the content locator, or with
`notification.find(...)`:

```java
// View
Button close = new Button("Close");
Notification notification = new Notification(
        new HorizontalLayout(new Span("This is a notification."), close));
notification.setDuration(0);
close.addClickListener(e -> notification.close());
```

```java
// Test
NotificationElement notification = find(page, NotificationElement.class)
        .withTextContaining("This is a notification.")
        .single();
ButtonElement.getByText(notification.getContentLocator(), "Close").click();
// or: notification.find(ButtonElement.class).withText("Close").single().click();
notification.assertClosed();
assertThat(find(page, NotificationElement.class).getLocator()).hasCount(0);
```

## Waiting for a notification to close

A notification with a duration closes on its own; `assertClosed()` retries until
it has:

```java
// View: notification.setDuration(400);
NotificationElement notification = new NotificationElement(page);
notification.assertOpen();
notification.assertClosed();   // passes once the 400 ms have elapsed
```

`isOpen()` reads the visibility once, without waiting.

## Theme variants and CSS classes

```java
// View
notification.addClassName("custom-notification");
notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
```

```java
// Test
notification.assertCssClass("custom-notification");
notification.assertTheme("error");
notification.assertHasThemeVariant("error");
```

## Pitfalls

- **Look the notification up after it opened.** Closed and never-opened cards
  have no `slot` attribute and are excluded from every lookup.
- **`new NotificationElement(page)` is ambiguous with several open.** It fails
  with a strict-mode error; narrow with `getByText` or `find(...).withText(...)`.
- **`getByText` is a substring match.** `"Upload finished"` also matches
  `"Upload finished with warnings"`; use `withText` for an exact match.
- **Component notifications read as joined text.** Prefer `withTextContaining`
  over `withText`.
- **A long duration can outlast the assertion timeout.** `assertClosed()` only
  retries up to Playwright's assertion timeout; close the notification from the
  test (a close button) when its duration is longer.
- **No id lookups.** `withId` / `withTestId` never match a notification card.

## See also

- [Confirm Dialog](ConfirmDialogElement.md), [Dialog](DialogElement.md), [Button](ButtonElement.md)
- [Finding components](../guide/finding-components.md),
  [Assertions](../guide/assertions.md),
  [Actions and waiting](../guide/actions-and-waiting.md),
  [Core concepts](../guide/core-concepts.md)
- Full example: [NotificationViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/NotificationViewIT.java)
