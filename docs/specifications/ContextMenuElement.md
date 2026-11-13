# Context Menu

`ContextMenuElement` wraps an open `<vaadin-context-menu>` (Flow: `ContextMenu`).
It lets a test open the menu with a right-click on its target, select items,
open sub-menus, and check disabled and checkable items.

| | |
|---|---|
| Web component | `<vaadin-context-menu>` (items in `<vaadin-context-menu-list-box>`) |
| Flow component | `com.vaadin.flow.component.contextmenu.ContextMenu` |
| Wrapper | `org.vaadin.addons.dramafinder.element.ContextMenuElement` |
| Typed query | none — not `Findable` |
| API | [ContextMenuElement](../../skills/vaadin-playwright-test/api-reference.md#contextmenuelement) |

The examples below use this view:

```java
// View
Div target = new Div(new Text("Right click to open the file menu"));
target.setId("file-target");
Span selection = new Span();
selection.setId("file-selection");

ContextMenu contextMenu = new ContextMenu(target);
contextMenu.addClassName("file-context-menu");
contextMenu.addItem("Open", e -> selection.setText("Open"));
contextMenu.addItem("Rename", e -> selection.setText("Rename"));
contextMenu.addItem("Delete", e -> selection.setText("Delete"));
MenuItem share = contextMenu.addItem("Share");
share.getSubMenu().addItem("Copy link", e -> selection.setText("Copy link"));
share.getSubMenu().addItem("Send via email", e -> selection.setText("Send via email"));
```

## Opening the menu

A context menu has no factory methods: create the wrapper with
`new ContextMenuElement(page)`, which resolves the open menu
(`vaadin-context-menu[opened]`), and open it with the static
`ContextMenuElement.openOn(target)`, which right-clicks the target. The locator
is lazy, so the wrapper can be created before the menu opens:

```java
ContextMenuElement contextMenu = new ContextMenuElement(page);
ContextMenuElement.openOn(page.locator("#file-target"));
contextMenu.assertOpen();
```

`openOn` takes any Playwright `Locator`, so the target can also come from an
element wrapper, e.g. `GridElement.get(page).getLocator()`.

`openOn` waits for Flow to go idle before the right-click, waits for the menu's
list box to show up, and right-clicks again (up to three attempts) when the menu
was lost. That makes it safe to reopen the menu right after a selection whose
round-trip is still in flight:

```java
contextMenu.selectItem("Security alerts");
// no wait needed: openOn absorbs the pending round-trip
ContextMenuElement.openOn(page.locator("#checkable-target"));
contextMenu.assertItemChecked("Security alerts");
```

## Selecting an item

`selectItem(label)` clicks the item and waits for the server round-trip. The
menu closes afterwards:

```java
ContextMenuElement contextMenu = new ContextMenuElement(page);
ContextMenuElement.openOn(page.locator("#file-target"));

contextMenu.selectItem("Rename");
assertThat(page.locator("#file-selection")).hasText("Rename");
contextMenu.assertClosed();
```

The label is matched against the item's **whole** accessible name,
case-sensitively: `selectItem("Delete")` does not pick a "Delete all" item that
comes first, and two items with the same label fail with a strict-mode error.
An item with an `aria-label` is selected by that `aria-label`.

## Sub-menus

`openSubMenu(label)` hovers the parent item and returns the sub-menu as a new
`ContextMenuElement`; use that one for the nested items:

```java
ContextMenuElement contextMenu = new ContextMenuElement(page);
ContextMenuElement.openOn(page.locator("#file-target"));

ContextMenuElement shareMenu = contextMenu.openSubMenu("Share");
shareMenu.selectItem("Copy link");

assertThat(page.locator("#file-selection")).hasText("Copy link");
shareMenu.assertClosed();
```

## Disabled items

```java
// View
ContextMenu statusMenu = new ContextMenu(statusTarget);
statusMenu.addItem("Refresh", e -> status.setText("Refresh"));
statusMenu.addItem("Disabled action", e -> status.setText("Disabled")).setEnabled(false);
```

```java
// Test
ContextMenuElement statusMenu = new ContextMenuElement(page);
ContextMenuElement.openOn(page.locator("#status-target"));
statusMenu.assertItemDisabled("Disabled action");
statusMenu.assertItemEnabled("Refresh");
```

## Checkable items

`assertItemChecked` / `assertItemNotChecked` read the item's
`menu-item-checked` attribute. Selecting a checkable item toggles it and closes
the menu, so reopen the menu before checking the new state:

```java
// View
ContextMenu checkableMenu = new ContextMenu(checkableTarget);
MenuItem news = checkableMenu.addItem("Project news");
MenuItem security = checkableMenu.addItem("Security alerts");
news.setCheckable(true);
security.setCheckable(true);
news.setChecked(true);
```

```java
// Test
ContextMenuElement contextMenu = new ContextMenuElement(page);
ContextMenuElement.openOn(page.locator("#checkable-target"));
contextMenu.assertItemChecked("Project news");
contextMenu.assertItemNotChecked("Security alerts");

contextMenu.selectItem("Security alerts");

ContextMenuElement.openOn(page.locator("#checkable-target"));
contextMenu.assertItemChecked("Security alerts");
```

## The list box and CSS classes

`getListBoxLocator()` returns the menu's `<vaadin-context-menu-list-box>`, for
assertions the wrapper does not cover, such as a screenshot of the open menu.
The element implements `HasStyleElement`:

```java
// View: contextMenu.addClassName("file-context-menu");
contextMenu.assertCssClass("file-context-menu");

assertThat(contextMenu.getListBoxLocator()).isVisible();
```

## Pitfalls

- **Open the menu with `openOn`, not a raw right-click.** Flow renders the
  `vaadin-context-menu` element on the server, so a round-trip still in flight
  — the one the previous selection triggered, for instance — can replace it and
  silently discard a menu opened meanwhile. `openOn` waits for Flow to go idle
  first (even when automatic waiting is switched off with `AutoWait`) and
  retries.
- **Use the element returned by `openSubMenu` for nested items.** The top-level
  wrapper's item lookup covers its own list box.
- **Labels match exactly**, against the accessible name, case-sensitively.
- **No typed query, no id lookups.** `ContextMenuElement` is not `Findable` and
  has no `getById` / `getByTestId`; reach it through `new ContextMenuElement(page)`.
- **Selecting closes the menu.** Reopen it with `openOn` before the next
  assertion on its items.

## See also

- [Menu Bar](MenuBarElement.md) (which shares Flow's `MenuItem`), [Grid](GridElement.md)
- [Finding components](../guide/finding-components.md),
  [Assertions](../guide/assertions.md),
  [Actions and waiting](../guide/actions-and-waiting.md),
  [Core concepts](../guide/core-concepts.md)
- Full example: [ContextMenuViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/ContextMenuViewIT.java)
