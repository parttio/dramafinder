# Avatar Group

`AvatarGroupElement` wraps `<vaadin-avatar-group>` (Flow: `AvatarGroup`). It
reads the visible avatars, the `+N` overflow avatar and the avatars hidden behind
it, and opens and closes the overflow menu.

| | |
|---|---|
| Web component | `<vaadin-avatar-group>` |
| Flow component | `com.vaadin.flow.component.avatar.AvatarGroup` |
| Wrapper | `org.vaadin.addons.dramafinder.element.AvatarGroupElement` |
| Typed query | `find(page, AvatarGroupElement.class)` → `ComponentQuery` |
| API | [AvatarGroupElement](../../skills/vaadin-playwright-test/api-reference.md#avatargroupelement) |

## Finding the component

```java
AvatarGroupElement group = AvatarGroupElement.getById(page, "avatar-group-basic");
AvatarGroupElement first = AvatarGroupElement.get(page);                       // first group on the page
AvatarGroupElement scoped = AvatarGroupElement.get(page.locator("#avatar-group-container"));
AvatarGroupElement byTestId = AvatarGroupElement.getByTestId(page, "participants");
```

The typed query has the filters every component has:

```java
import static org.vaadin.addons.dramafinder.element.query.ElementQueries.find;

AvatarGroupElement styled = find(page, AvatarGroupElement.class)
        .withClassName("custom-avatar-group").single();
```

## Visible avatars

```java
// View
AvatarGroup basic = new AvatarGroup(
        new AvatarGroupItem("Jane Smith"),
        new AvatarGroupItem("John Doe"),
        new AvatarGroupItem("Alice Cooper"));
basic.setId("avatar-group-basic");
basic.setWidth("600px");
```

```java
// Test
AvatarGroupElement group = AvatarGroupElement.getById(page, "avatar-group-basic");
group.assertNames("Jane Smith", "John Doe", "Alice Cooper");   // in order
group.assertVisibleCount(3);                                   // overflow avatar not counted

group.getAvatar(1).assertName("John Doe");                     // AvatarElement, zero-based
group.getAvatars().get(2).assertName("Alice Cooper");
```

Each avatar is an [`AvatarElement`](AvatarElement.md), so its name, abbreviation,
image and color index are asserted there. When the values themselves are needed:
`getNames()`, `getVisibleCount()`, `getAvatars()`, and `getAvatarsLocator()` for a
Playwright assertion such as `assertThat(group.getAvatarsLocator()).hasCount(3)`.

## Three kinds of avatar live inside one group

`<vaadin-avatar-group>` renders every avatar client-side, from its `items`
property, into three distinct places:

| Avatar | Where it lives | Reached with |
| --- | --- | --- |
| A visible one | direct `<vaadin-avatar>` child, no `slot` attribute | `getAvatars()`, `getAvatar(index)` |
| The overflow summary (`+N`) | `<vaadin-avatar slot="overflow">` child | `getOverflowAvatar()` |
| A hidden one | `<vaadin-avatar>` inside a `<vaadin-avatar-group-menu-item>` in the overlay menu | `getOverflowAvatars()` |

All three match a naive `vaadin-avatar` CSS lookup, because Playwright's CSS
selectors pierce the shadow DOM and the overlay menu is a light-DOM child of the
group. `getAvatarsLocator()` therefore uses an explicit
`xpath=./vaadin-avatar[not(@slot)]`, which matches direct children only and does
not pierce — so the overflow avatar and the menu avatars stay out of
`getAvatars()`, `getVisibleCount()` and `assertNames()`.

## Overflow

```java
// View: five items, at most three slots
AvatarGroupItem xavier = new AvatarGroupItem("Xavier Young");
xavier.setAbbreviation("XY");
AvatarGroupItem erin = new AvatarGroupItem("Erin Fisher");
erin.setColorIndex(3);

AvatarGroup overflow = new AvatarGroup(
        new AvatarGroupItem("Bob Ross"),
        new AvatarGroupItem("Carol Danvers"),
        new AvatarGroupItem("Dana Scully"),
        erin, xavier);
overflow.setId("avatar-group-overflow");
overflow.setMaxItemsVisible(3);
overflow.setWidth("600px");
```

```java
// Test
AvatarGroupElement group = AvatarGroupElement.getById(page, "avatar-group-overflow");
group.assertHasOverflow();
group.assertVisibleCount(2);
group.assertNames("Bob Ross", "Carol Danvers");
group.getOverflowAvatar().assertAbbreviation("+3");
group.assertOverflowNames("Dana Scully", "Erin Fisher", "Xavier Young");

AvatarGroupElement.getById(page, "avatar-group-basic").assertHasNoOverflow();
```

`hasOverflow()` and `getOverflowNames()` are the getters.

### The overflow avatar is always present

It exists even when nothing overflows, and is then marked `hidden`
(`group.getOverflowAvatar().assertHidden()`). Use `assertHasOverflow()` /
`assertHasNoOverflow()` (the `has-overflow` attribute on the group) to assert
whether anything is actually hidden, rather than testing the overflow avatar's
presence.

### How many avatars stay visible

`maxItemsVisible` is a budget for the whole row, overflow avatar included, so a
group of five items with `setMaxItemsVisible(3)` shows **two** avatars plus the
`+3` overflow avatar. Values below 2 are clamped: the group always keeps at
least two slots.

```java
AvatarGroupElement group = AvatarGroupElement.getById(page, "avatar-group-basic");
group.getMaxItemsVisible();        // null: not set
group.assertHasNoOverflow();
group.setMaxItemsVisible(2);       // writes the property in the browser
group.assertHasOverflow();
```

The count also depends on the available width — the group hides further avatars
when they no longer fit. Give the group an explicit width in test views if you
want to assert an exact visible count.

## Opening the overflow menu

The hidden avatars are in the DOM as soon as the group overflows, so
`assertOverflowNames(...)` works either way, but they are only *visible* while
the overlay is open. Like `ContextMenuElement`, the overlay is driven through
the UI rather than a property:

```java
AvatarGroupElement group = AvatarGroupElement.getById(page, "avatar-group-overflow");
group.assertOverflowClosed();

group.openOverflow();                       // clicks the +N avatar, waits for open
group.assertOverflowOpen();
assertThat(group.getOverflowMenuLocator()).isVisible();

List<AvatarElement> hidden = group.getOverflowAvatars();
hidden.get(0).assertName("Dana Scully");
hidden.get(2).assertAbbreviation("XY");

group.closeOverflow();                      // Escape, waits for closed
group.assertOverflowClosed();
```

Open state (`isOverflowOpen()`, `assertOverflowOpen()`) is read from the overflow
avatar's `aria-expanded` attribute, which the component keeps in sync with the
overlay. Clicking the `+N` avatar *toggles* the overlay, so `openOverflow()`
clicks only when the overlay is closed — calling it twice leaves the overlay open
rather than closing it again. `getOverflowAvatarsLocator()` is the locator over
the menu avatars.

## Theme and style

```java
// View
styled.addClassName("custom-avatar-group");
styled.addThemeVariants(AvatarGroupVariant.LUMO_SMALL);
```

```java
// Test
AvatarGroupElement group = AvatarGroupElement.getById(page, "avatar-group-styled");
group.assertTheme("small");
group.assertCssClass("custom-avatar-group");
```

## Pitfalls

- **Don't count `vaadin-avatar` yourself.** A CSS lookup also matches the
  overflow avatar and the menu avatars; use `getAvatars()` / `getAvatarsLocator()`.
- **The overflow avatar exists without overflow.** Assert with
  `assertHasOverflow()` / `assertHasNoOverflow()`, not on its presence.
- **`maxItemsVisible` includes the `+N` slot** and is clamped to at least 2; the
  visible count also shrinks when the group is too narrow.
- **`openOverflow()` is idempotent**, but a raw click on the overflow avatar
  toggles the menu.

## See also

- [Avatar](AvatarElement.md)
- [Context Menu](ContextMenuElement.md) — another overlay driven through the UI
- [Assertions](../guide/assertions.md)
- [Finding components](../guide/finding-components.md)
- [Actions and waiting](../guide/actions-and-waiting.md)
- Full example: [AvatarGroupViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/AvatarGroupViewIT.java)
