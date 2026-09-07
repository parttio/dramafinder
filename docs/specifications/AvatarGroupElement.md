# AvatarGroupElement

> Full API (methods, signatures, descriptions): see [api-reference.md](../../skills/vaadin-playwright-test/api-reference.md). This note covers only behaviour that isn't obvious from the signatures.

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
group. `getAvatarsLocator()` therefore uses an explicit `xpath=./vaadin-avatar[not(@slot)]`,
which matches direct children only and does not pierce — so the overflow avatar
and the menu avatars stay out of `getAvatars()`, `getVisibleCount()` and
`assertNames()`.

## The overflow avatar is always present

It exists even when nothing overflows, and is then marked `hidden`. Use
`assertHasOverflow()` / `assertHasNoOverflow()` (the `has-overflow` attribute on
the group) to assert whether anything is actually hidden, rather than testing
the overflow avatar's presence.

## How many avatars stay visible

`maxItemsVisible` is a budget for the whole row, overflow avatar included, so a
group of five items with `setMaxItemsVisible(3)` shows **two** avatars plus the
`+3` overflow avatar. Values below 2 are clamped: the group always keeps at
least two slots.

The count also depends on the available width — the group hides further avatars
when they no longer fit. Give the group an explicit width in test views if you
want to assert an exact `getVisibleCount()`.

## Opening the overflow

The hidden avatars are in the DOM as soon as the group overflows, so
`assertOverflowNames(...)` works either way, but they are only *visible* while
the overlay is open. Like `ContextMenuElement`, the overlay is driven through
the UI rather than a property:

```java
AvatarGroupElement group = AvatarGroupElement.get(page);
group.assertNames("Bob Ross", "Carol Danvers");
group.assertHasOverflow();

group.openOverflow();                       // clicks the +N avatar, waits for open
group.assertOverflowNames("Dana Scully", "Erin Fisher", "Xavier Young");
group.getOverflowAvatars().get(0).assertName("Dana Scully");
group.closeOverflow();                      // Escape, waits for closed
```

Open state is read from the overflow avatar's `aria-expanded` attribute, which
the component keeps in sync with the overlay. Clicking the `+N` avatar *toggles*
the overlay, so `openOverflow()` clicks only when the overlay is closed — calling
it twice leaves the overlay open rather than closing it again.
