# Confirm Dialog

`ConfirmDialogElement` wraps `<vaadin-confirm-dialog>` (Flow: `ConfirmDialog`).
It lets a test find the open confirm dialog, read its header, message and button
captions, and answer it with confirm, reject, cancel or Escape.

| | |
|---|---|
| Web component | `<vaadin-confirm-dialog>` |
| Flow component | `com.vaadin.flow.component.confirmdialog.ConfirmDialog` |
| Wrapper | `org.vaadin.addons.dramafinder.element.ConfirmDialogElement` |
| Typed query | `find(page, ConfirmDialogElement.class)` → `ComponentQuery` |
| API | [ConfirmDialogElement](../../skills/vaadin-playwright-test/api-reference.md#confirmdialogelement) |

## Finding the component

The host has the `alertdialog` role and takes the header as its accessible name:

```java
// View
ConfirmDialog dialog = new ConfirmDialog();
dialog.setHeader("Unsaved changes");
dialog.setText("There are unsaved changes. Do you want to discard or save them?");
add(new Button("Open unsaved changes", e -> dialog.open()));
```

```java
// Test
ButtonElement.getByText(page, "Open unsaved changes").click();

// by header: exact, case-sensitive, no .first()
ConfirmDialogElement dialog = ConfirmDialogElement.getByHeaderText(page, "Unsaved changes");

// the one open confirm dialog, whatever its header
ConfirmDialogElement open = ConfirmDialogElement.get(page);

// typed query, same accessible name
ConfirmDialogElement byName = find(page, ConfirmDialogElement.class)
        .withAccessibleName("Unsaved changes").single();

// fallbacks (each also has a Locator overload)
ConfirmDialogElement byId = ConfirmDialogElement.getById(page, "unsaved-dialog");
ConfirmDialogElement byTestId = ConfirmDialogElement.getByTestId(page, "unsaved-dialog");
```

`getByHeaderText(page, header)` matches that name in full, case-sensitively, and
is not narrowed with `first()`, so two open dialogs with that header raise a
strict-mode error. A partial header finds nothing:

```java
assertThat(ConfirmDialogElement.getByHeaderText(page, "unsaved").getLocator()).hasCount(0);
```

`get(page)` returns the open confirm dialog when there is only one; with several
open, any action on it fails with a strict-mode error and `getByHeaderText`
picks one. For a partial header, use
`find(page, ConfirmDialogElement.class).withAccessibleNameContaining(...)`.

## Only open dialogs exist

A Flow `ConfirmDialog` is attached to the page when it opens and removed once it
is answered or closed with Escape. So:

- Look the dialog up **after** the action that opens it.
- `assertClosed()` / `assertHidden()` pass both while it is closing and once it
  is gone, so use them right after `confirm()`, `cancel()` or `reject()`.
- Opening the same dialog again creates a new element; look it up again instead
  of reusing the old wrapper.

```java
ButtonElement.getByText(page, "Open delete").click();
ConfirmDialogElement dialog = ConfirmDialogElement.getByHeaderText(page, "Delete item");
dialog.assertOpen();
dialog.assertVisible();
dialog.assertMessage("Delete Report Q3? This cannot be undone.");
dialog.confirm();
dialog.assertClosed();
```

`isOpen()` and `isVisible()` read the overlay's visibility once, without
waiting; prefer `assertOpen()` / `assertClosed()`.

## Reading the header and message

```java
dialog.assertHeaderText("Unsaved changes");
dialog.assertMessage("There are unsaved changes. Do you want to discard or save them?");

// when the value itself is needed
String header = dialog.getHeaderText();
String message = dialog.getMessage();
```

The header and the buttons are slotted children (`header`, `confirm-button`,
`cancel-button`, `reject-button`); the message is everything else. `getMessage()`
and `assertMessage(...)` read only those unslotted children and collapse
whitespace, so a message set as a component (a paragraph with nested spans)
reads as one string, without the header or button texts:

```java
// View
Paragraph message = new Paragraph("Delete ");
message.add(new Span("Report Q3"));
message.add("? This cannot be undone.");
dialog.setText(message);
```

```java
// Test
dialog.assertMessage("Delete Report Q3? This cannot be undone.");
```

`assertMessage(...)` retries until the message matches, and on failure reports
both the expected and the actual message. `assertMessage(null)` asserts that the
dialog has no message. For assertions of your own, `getHeaderLocator()` and
`getMessageLocator()` return the header and the unslotted message children.

## Answering the dialog

Each answer clicks the matching button and waits for the server round-trip it
triggers:

```java
// View
ConfirmDialog dialog = new ConfirmDialog();
dialog.setHeader("Unsaved changes");
dialog.setCancelable(true);
dialog.addCancelListener(e -> result.setText("cancel"));
dialog.setRejectable(true);
dialog.setRejectText("Discard");
dialog.addRejectListener(e -> result.setText("reject"));
dialog.setConfirmText("Save");
dialog.addConfirmListener(e -> result.setText("confirm"));
```

```java
// Test
ConfirmDialogElement dialog = ConfirmDialogElement.getByHeaderText(page, "Unsaved changes");
dialog.confirm();   // or dialog.reject(); or dialog.cancel();
assertThat(page.locator("#result")).hasText("confirm");
dialog.assertClosed();
```

## Button captions and optional buttons

```java
dialog.assertConfirmText("Save");
dialog.assertRejectText("Discard");
dialog.assertCancelText("Cancel");

dialog.assertCancelable();
dialog.assertRejectable();
```

The cancel and reject buttons are always in the DOM but hidden unless the dialog
is cancelable / rejectable. Check them with `assertCancelable()` /
`assertNotCancelable()` and `assertRejectable()` / `assertNotRejectable()`;
`cancel()` and `reject()` time out on a hidden button.

```java
// View: new ConfirmDialog("Export failed", "Your report could not be exported.", "OK", listener)
ConfirmDialogElement alert = ConfirmDialogElement.get(page);
alert.assertConfirmText("OK");
alert.assertNotCancelable();
alert.assertNotRejectable();
alert.confirm();
```

`getConfirmButton()`, `getCancelButton()` and `getRejectButton()` return the
buttons as [`ButtonElement`](ButtonElement.md)s, for anything the shortcuts
don't cover, such as the button theme:

```java
// View: dialog.setConfirmButtonTheme("error primary");
dialog.getConfirmButton().assertTheme("error primary");
dialog.getConfirmButton().assertHasThemeVariant("error");
```

## Escape

`closeWithEscape()` fires the dialog's cancel event, like the cancel button:

```java
dialog.closeWithEscape();
assertThat(page.locator("#result")).hasText("cancel");
dialog.assertClosed();
```

When the dialog disables closing on Escape (`setCloseOnEsc(false)`), the dialog
stays open:

```java
// View: dialog.setCloseOnEsc(false);
ConfirmDialogElement terms = ConfirmDialogElement.getByHeaderText(page, "Accept terms");
terms.closeWithEscape();
terms.assertOpen();
terms.confirm();
```

## Theme and CSS classes

The element implements `HasThemeElement` and `HasStyleElement`, so
`assertTheme(...)`, `assertHasThemeVariant(...)` and `assertCssClass(...)` work
on the dialog host.

## Pitfalls

- The dialog only exists while it is open: look it up after opening it, and
  again after reopening it.
- `cancel()` and `reject()` time out when the dialog is not cancelable /
  rejectable, because the button is hidden.
- The host has a zero-size box; `isVisible()`, `assertVisible()`,
  `assertHidden()`, `isOpen()` and `assertClosed()` all go through the overlay
  (`getOverlayLocator()`), while `assertOpen()` checks the `opened` attribute.
- `getByHeaderText` is exact: a partial or differently cased header finds
  nothing.

## See also

- [Dialog](DialogElement.md), [Button](ButtonElement.md), [Notification](NotificationElement.md)
- [Finding components](../guide/finding-components.md),
  [Assertions](../guide/assertions.md),
  [Actions and waiting](../guide/actions-and-waiting.md),
  [Core concepts](../guide/core-concepts.md)
- Full example: [ConfirmDialogViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/ConfirmDialogViewIT.java)
