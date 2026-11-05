# ConfirmDialogElement

> Full API (methods, signatures, descriptions): see [api-reference.md](../../skills/vaadin-playwright-test/api-reference.md). This note covers only behaviour that isn't obvious from the signatures.

## Only open dialogs exist

A Flow `ConfirmDialog` is attached to the page when it opens and removed once it
is answered or closed with Escape. So:

- Look the dialog up **after** the action that opens it.
- `assertClosed()` / `assertHidden()` pass both while it is closing and once it
  is gone, so use them right after `confirm()`, `cancel()` or `reject()`.
- Opening the same dialog again creates a new element; look it up again instead
  of reusing the old wrapper.

```java
ButtonElement.getByText(page, "Delete").click();
ConfirmDialogElement dialog = ConfirmDialogElement.getByHeaderText(page, "Delete item");
dialog.assertMessage("Delete Report Q3? This cannot be undone.");
dialog.confirm();
dialog.assertClosed();
```

## Lookup

The host has the `alertdialog` role and takes the header as its accessible name.
`getByHeaderText(page, header)` matches that name case-insensitively as a
substring and is not narrowed with `first()`, so two open dialogs that both match
raise a strict-mode error. `get(page)` returns the open confirm dialog when there
is only one.

## Message vs header and buttons

The header and the buttons are slotted children (`header`, `confirm-button`,
`cancel-button`, `reject-button`); the message is everything else. `getMessage()`
and `assertMessage(...)` read only those unslotted children and collapse
whitespace, so a message set as a component (a paragraph with nested spans) reads
as one string, without the header or button texts.

## Optional buttons

The cancel and reject buttons are always in the DOM but hidden unless the dialog
is cancelable / rejectable. Check them with `assertCancelable()` /
`assertNotCancelable()` and `assertRejectable()` / `assertNotRejectable()`;
`cancel()` and `reject()` time out on a hidden button.

## Escape

`closeWithEscape()` fires the dialog's cancel event, like the cancel button. When
the dialog disables closing on Escape (`setCloseOnEsc(false)`), the dialog stays
open.
