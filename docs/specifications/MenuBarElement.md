# Menu Bar

`MenuBarElement` wraps `<vaadin-menu-bar>` (Flow: `MenuBar`). With its two
helpers — `MenuElement` for an open sub-menu and `MenuItemElement` for one item —
it lets a test click top-level items, open sub-menus at any depth, click nested
items, and check disabled, expanded and checked items.

| | |
|---|---|
| Web component | `<vaadin-menu-bar>`; sub-menus `<vaadin-menu-bar-list-box>`; items `<vaadin-menu-bar-button>` (top level) and `<vaadin-menu-bar-item>` (sub-menu) |
| Flow component | `com.vaadin.flow.component.menubar.MenuBar` (items: `com.vaadin.flow.component.contextmenu.MenuItem`) |
| Wrappers | `org.vaadin.addons.dramafinder.element.MenuBarElement`, `MenuElement`, `MenuItemElement` |
| Typed query | `find(page, MenuBarElement.class)` → `ComponentQuery`; `MenuElement` → `ComponentQuery`; `MenuItemElement` → `TextQuery` |
| API | [MenuBarElement](../../skills/vaadin-playwright-test/api-reference.md#menubarelement), [MenuElement](../../skills/vaadin-playwright-test/api-reference.md#menuelement), [MenuItemElement](../../skills/vaadin-playwright-test/api-reference.md#menuitemelement) |

The examples below use this view:

```java
// View
MenuBar menuBar = new MenuBar();
menuBar.setId("main-menu-bar");
menuBar.addItem("View", listener);
menuBar.addItem("Edit", listener);

MenuItem share = menuBar.addItem("Share");
MenuItem onSocialMedia = share.getSubMenu().addItem("On social media");
onSocialMedia.getSubMenu().addItem("Facebook", listener);
onSocialMedia.getSubMenu().addItem("Twitter", listener);
share.getSubMenu().addItem("By email", listener);

MenuItem file = menuBar.addItem("File");
file.getSubMenu().addItem("New", listener);
MenuItem export = file.getSubMenu().addItem("Export");
export.getSubMenu().addItem("PDF", listener);
export.getSubMenu().addItem("CSV", listener);
file.getSubMenu().addItem("Print", listener).setEnabled(false);
file.getSubMenu().addItem("Close", listener).setAriaLabel("Close the document");

menuBar.addItem("Archive", listener).setEnabled(false);

// icon-only item, named by its aria-label
MenuItem more = menuBar.addItem(VaadinIcon.ELLIPSIS_DOTS_V.create());
more.setAriaLabel("More actions");
more.getSubMenu().addItem("Settings", listener);
```

## Finding the component

A menu bar has no visible label. Find it by id, test id, or by the accessible
name an `aria-label` gives it:

```java
MenuBarElement menuBar = MenuBarElement.getById(page, "main-menu-bar");
MenuBarElement byTestId = MenuBarElement.getByTestId(page, "main-menu-bar");

// View: menuBar.getElement().setAttribute("aria-label", "Main menu");
MenuBarElement byName = find(page, MenuBarElement.class)
        .withAccessibleName("Main menu").single();

// the only menu bar on the page
MenuBarElement only = new MenuBarElement(page);
```

`getById` and `getByTestId` also have a `Locator` overload that searches inside a
scope. `new MenuBarElement(page)` matches every menu bar on the page, so with
several it fails with a strict-mode error.

!!! note "Deprecated: `getByLabel`"

    `MenuBarElement.getByLabel` / `getByLabelContaining`, `MenuElement.getByLabel` /
    `getByLabelContaining` and `MenuItemElement.getByLabel(Locator, String)` match the
    accessible name, not a visible label. They are deprecated and will be removed
    in 3.0; use `find(...).withAccessibleName(...)` (or `...Containing`) instead.

## Clicking a top-level item

`getMenuItemElement(name)` returns a top-level item; `click()` clicks it and
waits for the server round-trip:

```java
MenuBarElement menuBar = MenuBarElement.getById(page, "main-menu-bar");
menuBar.getMenuItemElement("View").click();
assertThat(page.locator("#text")).hasText("Clicked item: View");
```

The name is the item's own text, matched in full and case-sensitively. An item is
also found by its accessible name, which is all an icon-only item with an
`aria-label` has. Items of open sub-menus and the hidden overflow button are not
considered; two items sharing the label fail with a strict-mode error.

## Opening sub-menus and clicking nested items

`openSubMenu(name)` clicks a top-level item, waits until its sub-menu is visible
and returns it as a `MenuElement`. `MenuElement.openSubMenu(name)` does the same
one level down, so a chain follows the path a user clicks:

```java
MenuElement share = menuBar.openSubMenu("Share");
share.getMenuItemElement("By email").click();

// two levels deep
menuBar.openSubMenu("Share")
        .openSubMenu("On social media")
        .getMenuItemElement("Facebook")
        .click();
assertThat(page.locator("#text")).hasText("Clicked item: Facebook");

// icon-only top-level item, by its aria-label
menuBar.openSubMenu("More actions").getMenuItemElement("Settings").click();
```

The returned menu is scoped to its menu bar and its level, so it never matches
another menu bar's sub-menu or a nested level — even when both are open, or a
second menu bar on the page has the same labels:

```java
MenuElement file = menuBar.openSubMenu("File");
MenuElement export = file.openSubMenu("Export");
assertThat(file.getLocator()).isVisible();
assertThat(export.getLocator()).isVisible();

MenuBarElement.getById(page, "other-menu-bar")
        .openSubMenu("File").openSubMenu("Export").getMenuItemElement("PDF").click();
```

An item whose `aria-label` differs from its text is found by either:

```java
MenuElement file = menuBar.openSubMenu("File");
file.getMenuItemElement("Close").click();              // its text
file.getMenuItemElement("Close the document");         // its aria-label
```

## Closing the sub-menus

`close()` closes every open sub-menu of the menu bar and returns once none is
visible, so the same menu can be opened again right away. It does nothing when
no sub-menu is open.

```java
MenuElement file = menuBar.openSubMenu("File");
MenuElement export = file.openSubMenu("Export");
menuBar.close();
file.assertHidden();
export.assertHidden();
menuBar.getMenuItemElement("File").assertCollapsed();
```

## Expanded and collapsed items

An item with an open sub-menu is expanded:

```java
MenuItemElement fileItem = menuBar.getMenuItemElement("File");
fileItem.assertCollapsed();
menuBar.openSubMenu("File");
fileItem.assertExpanded();
```

`openSubMenu` already asserts this before returning.

## Disabled items

Both kinds of item carry the `menuitem` role and `aria-disabled="true"` when
disabled, so `MenuItemElement` uses the regular `HasEnabledElement` checks:

```java
menuBar.getMenuItemElement("Archive").assertDisabled();
menuBar.getMenuItemElement("File").assertEnabled();

MenuElement file = menuBar.openSubMenu("File");
file.getMenuItemElement("Print").assertDisabled();
file.getMenuItemElement("New").assertEnabled(true);
boolean enabled = file.getMenuItemElement("New").isEnabled(); // read once
```

## Checkable items

`MenuItemElement` has no checked assertion of its own. A checkable item
(`MenuItem.setCheckable(true)`) is toggled by clicking it, and its state is the
`menu-item-checked` attribute — the same one
[`ContextMenuElement.assertItemChecked`](ContextMenuElement.md#checkable-items)
reads — so assert it on the item's locator:

```java
// View
MenuItem options = menuBar.addItem("Options");
MenuItem wrap = options.getSubMenu().addItem("Word wrap");
wrap.setCheckable(true);
```

```java
// Test
MenuElement optionsMenu = menuBar.openSubMenu("Options");
MenuItemElement wrap = optionsMenu.getMenuItemElement("Word wrap");
assertThat(wrap.getLocator()).not().hasAttribute("menu-item-checked", "");
wrap.click();

MenuItemElement reopened = menuBar.openSubMenu("Options").getMenuItemElement("Word wrap");
assertThat(reopened.getLocator()).hasAttribute("menu-item-checked", "");
```

Clicking an item closes the sub-menu, so open it again before checking the
state. The menu bar test view has no checkable item, so this pattern is not
covered by an IT of its own.

## Reading the item labels

`getItemLabels()` returns each item's own text — or its `aria-label` when it has
no text — in display order. Nested items and the hidden overflow button are left
out. Use it when the test asserts the menu's structure:

```java
assertEquals(List.of("View", "Edit", "Share", "File", "Archive", "More actions"),
        menuBar.getItemLabels());
assertEquals(List.of("New", "Export", "Print", "Close"),
        menuBar.openSubMenu("File").getItemLabels());
```

## Typed queries

`menuBar.find(MenuItemElement.class)` searches every item of the menu bar —
top-level buttons and the items of open sub-menus — leaving out the hidden
overflow button. Its `TextQuery` filters read the item's own text:

```java
assertEquals(6, menuBar.find(MenuItemElement.class).count());

menuBar.openSubMenu("File").openSubMenu("Export");
menuBar.find(MenuItemElement.class).withText("Print").single().assertDisabled();
menuBar.find(MenuItemElement.class).withText("PDF").single().click();

// by accessible name (aria-label, else the text)
find(page, MenuItemElement.class).withAccessibleName("Edit").single();
```

`find(page, MenuElement.class)` finds every sub-menu list box rendered on the
page, including those of closed menus; it is handy right after opening a single
menu, but the `MenuElement` returned by `openSubMenu` is the reliable handle:

```java
menuBar.openSubMenu("Share");
MenuElement share = find(page, MenuElement.class).single();
share.getMenuItemElement("By email").click();
```

## Accessible name, theme and CSS classes

All three wrappers implement `HasAriaLabelElement`, `HasThemeElement` and
`HasStyleElement`:

```java
// View
menuBar.addClassName("custom-menu-bar");
menuBar.addThemeVariants(MenuBarVariant.LUMO_CONTRAST);
```

```java
// Test
menuBar.assertCssClass("custom-menu-bar");
menuBar.assertTheme("contrast");
menuBar.assertHasThemeVariant("contrast");

// View: menuBar.getElement().setAttribute("aria-label", "Main menu");
menuBar.assertAriaLabel("Main menu");
```

`assertAriaLabel(null)` asserts that there is no `aria-label`.

## Pitfalls

- **Labels match exactly.** `getMenuItemElement("Save")` does not resolve to
  "Save as…", and is case-sensitive; two items with the same label fail with a
  strict-mode error.
- **Prefer the `MenuElement` from `openSubMenu`.** `new MenuElement(page)` and
  `find(page, MenuElement.class)` match every list box on the page, so several
  open levels or another menu bar make them ambiguous.
- **`getMenuItemElement` on a menu does not see nested levels.** Open the nested
  sub-menu with `openSubMenu` and look the item up on the menu it returns.
- **A closing sub-menu stays visible while it animates out.** Use `close()`,
  which waits until it is gone, before reopening the same menu.
- **Clicking a leaf item closes the sub-menus.** Reopen the menu to check the
  state of a checkable item.

## See also

- [Context Menu](ContextMenuElement.md), [Button](ButtonElement.md)
- [Finding components](../guide/finding-components.md),
  [Assertions](../guide/assertions.md),
  [Actions and waiting](../guide/actions-and-waiting.md),
  [Core concepts](../guide/core-concepts.md)
- Full example: [MenuBarViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/MenuBarViewIT.java)
