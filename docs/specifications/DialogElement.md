# Dialog

`DialogElement` wraps `<vaadin-dialog>` (Flow: `Dialog`). It lets a test find an
open dialog by its header, reach the buttons and fields inside it, read its
header, content and footer, check whether it is modal, and close it.

| | |
|---|---|
| Web component | `<vaadin-dialog>` |
| Flow component | `com.vaadin.flow.component.dialog.Dialog` |
| Wrapper | `org.vaadin.addons.dramafinder.element.DialogElement` |
| Typed query | `find(page, DialogElement.class)` → `ComponentQuery` (open dialogs only) |
| API | [DialogElement](../../skills/vaadin-playwright-test/api-reference.md#dialogelement) |

## Finding the component

Look the dialog up **after** the action that opens it. The header title set
with `Dialog.setHeaderTitle(...)` is the dialog's accessible name, so it is the
natural key:

```java
// View
Dialog dialog = new Dialog();
dialog.setHeaderTitle("My Dialog");
dialog.add(new Span("This is the content of the dialog."));
dialog.getFooter().add(new Button("Close", e -> dialog.close()));
Button open = new Button("Open dialog", e -> dialog.open());
```

```java
// Test
ButtonElement.getByText(page, "Open dialog").click();

// by header title: exact, case-sensitive, no .first()
DialogElement dialog = DialogElement.getByHeaderText(page, "My Dialog");

// the single open dialog, found by its dialog role
DialogElement onlyDialog = new DialogElement(page);

// fallbacks for a dialog without a header title
DialogElement byId = DialogElement.getById(page, "edit-dialog");
DialogElement byTestId = DialogElement.getByTestId(page, "edit-dialog");
```

`getById` and `getByTestId` also have a `Locator` overload that searches inside
a scope, and they match a dialog **whether it is open or not**.

The typed query searches **open** dialogs only (`vaadin-dialog[opened]`): a
closed dialog kept in the view, or one of the dev tools' dialogs, is never part
of the result. `withAccessibleName` reads the header title; it never matches the
label of a field inside the dialog.

```java
DialogElement dialog = find(page, DialogElement.class)
        .withAccessibleName("Delete account?")
        .single();

// only one dialog is open: no filter needed
DialogElement open = find(page, DialogElement.class).single();

// no dialog open any more (retries until the count matches)
assertThat(find(page, DialogElement.class).getLocator()).hasCount(0);
```

`getByHeaderText` and `withAccessibleName` match the whole header. Use
`withAccessibleNameContaining(...)` for a case-insensitive part of it. Two open
dialogs with the same header fail with a strict-mode error instead of one being
picked silently.

## Finding components inside the dialog

A dialog usually holds the very buttons and fields the page also has — a
"Delete" button on the page and a "Delete" button in the confirmation dialog.
Scope the lookup to the dialog with `dialog.find(...)`:

```java
// View
Dialog dialog = new Dialog();
dialog.setHeaderTitle("Delete account?");
dialog.add(new TextField("Reason"));
dialog.getFooter().add(
        new Button("Delete", e -> dialog.close()),
        new Button("Cancel", e -> dialog.close()));
add(new Button("Open dialog", e -> dialog.open()), new Button("Delete"));
```

```java
// Test
find(page, ButtonElement.class).withText("Open dialog").single().click();

DialogElement dialog = find(page, DialogElement.class)
        .withAccessibleName("Delete account?").single();

dialog.find(TextFieldElement.class).withLabel("Reason").single().setValue("Moving away");
dialog.find(ButtonElement.class).withText("Delete").single().click();
dialog.assertClosed();
```

The element factories take a `Locator` scope as well. Pass `dialog.getLocator()`
for the whole dialog, or one of its slot locators to narrow it further:

```java
TextFieldElement reason = TextFieldElement.getByLabel(dialog.getLocator(), "Reason");
ButtonElement close = ButtonElement.getByText(dialog.getFooterLocator(), "Close");
close.click();
```

## Header, content and footer

A Flow dialog has four places that hold content, each with its own locator:

| Flow API | DramaFinder |
|---|---|
| `setHeaderTitle("...")` | `getHeaderText()`, `assertHeaderText(...)` |
| `getHeader().add(...)` | `getHeaderLocator()` |
| `add(...)` | `getContentLocator()` (the first unslotted child) |
| `getFooter().add(...)` | `getFooterLocator()` |

```java
// View
Dialog dialog = new Dialog();
dialog.setHeaderTitle("Header title");
dialog.getHeader().add(new Span("This is the header of the dialog."));
dialog.add(new Span("This is the content of the dialog."));
dialog.getFooter().add(new Button("Close", e -> dialog.close()));
```

```java
// Test
DialogElement dialog = DialogElement.getByHeaderText(page, "Header title");
dialog.assertHeaderText("Header title");
assertThat(dialog.getHeaderLocator()).hasText("This is the header of the dialog.");
assertThat(dialog.getContentLocator()).hasText("This is the content of the dialog.");
assertThat(dialog.getFooterLocator()).containsText("Close");
```

`getHeaderText()` reads the header title once, without waiting; use it only
when the test needs the value itself.

## Opening and closing

`assertOpen()` checks the `opened` attribute; `assertVisible()` checks that the
overlay shows. `assertClosed()` checks both that `opened` is gone and that the
overlay is hidden, so it waits out the closing animation, and it also passes
when Flow has removed the dialog from the page.

```java
ButtonElement.getByText(page, "Open dialog").click();
DialogElement dialog = DialogElement.getByHeaderText(page, "My Dialog");
dialog.assertOpen();
dialog.assertVisible();

// close with a button of the dialog
dialog.find(ButtonElement.class).withText("Close").single().click();
dialog.assertClosed();
```

Pressing Escape closes a dialog unless the view called
`setCloseOnEsc(false)`:

```java
dialog.closeWithEscape();
dialog.assertClosed();
```

`isOpen()` reads the `opened` attribute once, without waiting, and returns
`false` when the dialog is no longer in the page. `isVisible()` reads the
overlay's visibility once. `assertHidden()` asserts that the overlay is hidden.

## Modal and modeless dialogs

A Flow `Dialog` is modal by default. `setModal(false)` makes it modeless, which
renders the `modeless` attribute:

```java
// View
Dialog dialog1 = new Dialog();
dialog1.setHeaderTitle("My non-modal Dialog 1");
dialog1.setModal(false);
```

```java
// Test
ButtonElement.getByText(page, "Open non-modal 1").click();
DialogElement dialog1 = DialogElement.getByHeaderText(page, "My non-modal Dialog 1");
dialog1.assertModeless();

// the page stays usable: open a second modeless dialog
ButtonElement.getByText(page, "Open non-modal 2").click();
DialogElement dialog2 = DialogElement.getByHeaderText(page, "My non-modal Dialog 2");
dialog2.assertModeless();

ButtonElement.getByText(dialog2.getFooterLocator(), "Close").click();
dialog2.assertClosed();
dialog1.assertOpen();
```

```java
DialogElement modal = DialogElement.getByHeaderText(page, "My Dialog");
modal.assertModal();
boolean isModal = modal.isModal(); // read once, no retry
```

## Theme variants and CSS classes

```java
// View
dialog.addClassName("custom-dialog");
dialog.addThemeVariants(DialogVariant.LUMO_NO_PADDING);
```

```java
// Test
dialog.assertCssClass("custom-dialog");
dialog.assertTheme("no-padding");
dialog.assertHasThemeVariant("no-padding");
```

## Pitfalls

- **Header text vs header locator.** `getHeaderText()` / `assertHeaderText(...)`
  read the header **title** (`setHeaderTitle`, the `title` slot);
  `getHeaderLocator()` is the header **content** (`getHeader().add(...)`, the
  `header-content` slot). They are different elements.
- **`getContentLocator()` is the first unslotted child only.** A dialog with
  several components added through `add(...)` exposes only the first one there;
  reach the others with `dialog.find(...)` or a factory scoped to
  `dialog.getLocator()`.
- **The host is never visible.** `<vaadin-dialog>` has a zero-size box; what
  shows is the `<vaadin-dialog-overlay>` in its shadow DOM. `isVisible()`,
  `assertVisible()` and `assertHidden()` are redefined to use the overlay
  (`getOverlayLocator()`). Use that locator, not `getLocator()`, for a
  screenshot or a bounding-box check.
- **A closing dialog is still visible for a moment.** The overlay animates out,
  so assert with `assertClosed()`, which retries, rather than with `isOpen()`.
- **A modal dialog hides the rest of the page from role lookups.** While it is
  open, Vaadin marks the content behind it `aria-hidden`, so a role-based
  factory such as `ButtonElement.getByText(page, ...)` does not find a page
  button. Close the dialog first, or make it modeless.
- **A Flow dialog that was not added to a layout only exists while it is open.**
  Look it up again after reopening instead of keeping an old wrapper around.
- **The typed query skips closed dialogs; `getById` / `getByTestId` do not.**
  `find(page, DialogElement.class).withId(...)` returns nothing for a closed
  dialog, while `DialogElement.getById(page, ...)` still resolves it.

## See also

- [Confirm Dialog](ConfirmDialogElement.md) for `ConfirmDialog`
- [Popover](PopoverElement.md), [Login](LoginElement.md) (login overlay)
- [Button](ButtonElement.md), [Text Field](TextFieldElement.md)
- [Finding components](../guide/finding-components.md),
  [Assertions](../guide/assertions.md),
  [Actions and waiting](../guide/actions-and-waiting.md),
  [Core concepts](../guide/core-concepts.md)
- Full examples: [DialogViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/DialogViewIT.java),
  [ElementQueryIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/ElementQueryIT.java)
