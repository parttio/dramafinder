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
