# AppLayoutElement

> Full API (methods, signatures, descriptions): see [api-reference.md](../../skills/vaadin-playwright-test/api-reference.md). This note covers only behaviour that isn't obvious from the signatures.

## The three areas are light DOM, the backdrop is a shadow part

`<vaadin-app-layout>` renders five containers in its shadow root — `navbar`
(`navbar-top`), `navbar-bottom`, `drawer`, `content` and `backdrop` — and each
of them holds a `<slot>`. The components you add to the layout stay in the light
DOM as children of the host, so they are *not* descendants of those shadow
containers, even though Playwright's CSS selectors pierce shadow DOM. A lookup
scoped to `[part='drawer']` therefore finds nothing of the drawer's content.

`getNavbarLocator()`, `getBottomNavbarLocator()`, `getDrawerLocator()` and
`getContentLocator()` consequently select the slotted light-DOM children, which
is what assertions are usually about:

```java
assertThat(appLayout.getDrawerLocator().getByText("Dramas")).isVisible();
assertThat(appLayout.getContentLocator()).containsText("App layout content");
```

Two consequences:

- A navbar or drawer normally holds **several** components, so those locators
  resolve to more than one element. Chain a further locator (as above) instead
  of asserting on them directly — a strict-mode assertion on a multi-element
  locator fails.
- `getBackdropLocator()` is the exception: the backdrop has no slot, so it
  returns the shadow part itself.

`getContentLocator()` matches the children **without** a slot attribute, which
with Flow is the component passed to `AppLayout.setContent(…)` — the routed
view when the layout is used as a `RouterLayout`.

## Assert on the state attributes, not on geometry

The drawer opens and closes with a CSS transition (300 ms unless
`prefers-reduced-motion` is set), and the component measures the drawer and
navbar asynchronously — with a `ResizeObserver` plus a `requestAnimationFrame`
since [web-components#11493](https://github.com/vaadin/web-components/pull/11493).
Positions, widths and the offset custom properties are therefore transient right
after a toggle or a viewport change.

Everything this element asserts is read from a reflected attribute on the host
instead, and through auto-retrying Playwright assertions:

| State | Attribute | Helpers |
| --- | --- | --- |
| Drawer opened | `drawer-opened` | `isDrawerOpened()`, `assertDrawerOpened()`, `assertDrawerClosed()` |
| Overlay mode | `overlay` | `isOverlayMode()`, `assertOverlayMode()`, `assertNotOverlayMode()` |
| Area order | `primary-section` | `getPrimarySection()`, `assertPrimarySection(…)` |
| Slotted content present | `has-navbar`, `has-drawer` | `assertHasNavbar()`, `assertHasDrawer()` |

Visibility is a good illustration: closing the drawer sets the attribute
immediately, but the drawer is animated out of the viewport and only turns
`visibility: hidden` when that transition ends. `getDrawerLocator().isVisible()`
therefore still returns `true` right after `closeDrawer()`. Assert on the state
with `assertDrawerClosed()`, and if you do assert on visibility, use the
auto-retrying `assertThat(…).isHidden()` rather than reading `isVisible()`.

## Overlay mode is decided by the viewport, and it moves the drawer

The component switches to overlay mode from CSS, when
`--vaadin-app-layout-drawer-overlay` computes to `true` — which the bundled
styles do below 800 px of width or 600 px of height. So overlay mode is entered
in a test by resizing the page, not by setting a property:

```java
page.setViewportSize(500, 900); // narrow enough for overlay mode
appLayout.assertOverlayMode();
appLayout.assertDrawerClosed(); // entering overlay mode closes the drawer
```

Entering overlay mode saves the drawer state and closes the drawer; leaving it
restores what was saved. A drawer opened while in overlay mode is covered by a
backdrop that spans the whole viewport at a higher stacking level than the
navbar, so **the drawer toggle cannot be clicked while the drawer is open**:

- `toggleDrawer()` clicks the toggle unconditionally and thus fails in that
  state.
- `closeDrawer()` and `setDrawerOpened(false)` press `Escape` in overlay mode
  and click the toggle otherwise, so they work in both modes.
- `clickBackdrop()` aims at the side of the backdrop the drawer leaves free —
  a plain click would land in the drawer, since the backdrop is centred on the
  viewport and the drawer sits on top of it.

## Touch-optimized navbar components move between slots

Components added with `addToNavbar(true, …)` start out in the `navbar` slot and
are moved by the component itself to `navbar-bottom` only on touchscreen
devices (`pointer: coarse` and a small viewport). A desktop browser — including
headless Chromium in the ITs — keeps them in the top navbar, so
`getBottomNavbarLocator()` matches nothing there.
