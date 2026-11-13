# App Layout

`AppLayoutElement` wraps `<vaadin-app-layout>` (Flow: `AppLayout`), the
application shell with a navbar, a drawer and a content area. It lets a test
reach the content of each area, open and close the drawer in desktop and overlay
mode, and assert the layout's state: drawer opened, overlay mode, primary
section.

| | |
|---|---|
| Web component | `<vaadin-app-layout>` |
| Flow component | `com.vaadin.flow.component.applayout.AppLayout` |
| Wrapper | `org.vaadin.addons.dramafinder.element.AppLayoutElement` |
| Typed query | `find(page, AppLayoutElement.class)` → `ComponentQuery` |
| API | [AppLayoutElement](../../skills/vaadin-playwright-test/api-reference.md#applayoutelement) |

The examples use a typical `RouterLayout`:

```java
// View
public class MainLayout extends AppLayout {
    MainLayout() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");
        addToNavbar(true, toggle, new Header(new H1("AppLayout Demo")));

        setPrimarySection(Section.DRAWER);
        addToDrawer(new Div(new Span("Dramas")), new Scroller(new SideNav()));
    }
}
```

## Finding the component

An application normally has a single app layout:

```java
AppLayoutElement appLayout = AppLayoutElement.get(page);                        // first on the page
AppLayoutElement inBody = AppLayoutElement.get(page.locator("body"));           // first in a scope
AppLayoutElement byId = AppLayoutElement.getById(page, "demo-app-layout");
AppLayoutElement byTestId = AppLayoutElement.getByTestId(page, "demo-app-layout");

// Typed query: generic filters only
AppLayoutElement styled = find(page, AppLayoutElement.class).withClassName("custom-app-layout").single();
```

## The navbar, drawer and content areas

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
AppLayoutElement appLayout = AppLayoutElement.get(page);

assertThat(appLayout.getNavbarLocator()).hasCount(2);   // the toggle and the header
assertThat(appLayout.getNavbarLocator().getByText("AppLayout Demo")).isVisible();
assertThat(appLayout.getDrawerLocator().getByText("Dramas")).isVisible();
assertThat(appLayout.getContentLocator()).containsText("App layout content");

appLayout.assertHasNavbar();   // has-navbar: something is slotted into the navbar
appLayout.assertHasDrawer();   // has-drawer: something is slotted into the drawer
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

Components in an area are found by passing its locator to their factories:

```java
SideNavigationElement nav = SideNavigationElement.getByLabel(appLayout.getDrawerLocator(), "My App");
ButtonElement logout = ButtonElement.getByText(appLayout.getNavbarLocator(), "Log out");
```

## Opening and closing the drawer

```java
appLayout.assertDrawerOpened();       // opened by default on desktop

appLayout.toggleDrawer();             // clicks the toggle, whatever the state
appLayout.assertDrawerClosed();

appLayout.openDrawer();               // only acts when closed
appLayout.closeDrawer();              // only acts when open
appLayout.setDrawerOpened(true);      // either way; keeps the state when already there
appLayout.assertDrawerOpened();

boolean opened = appLayout.isDrawerOpened();   // read once
```

The drawer toggle is a `ButtonElement`:

```java
ButtonElement toggle = appLayout.getDrawerToggle();
toggle.assertVisible();
toggle.assertAriaLabel("Menu toggle");
```

A drawer closed by the server is observed with the same assertion:

```java
// View: new Button("Close drawer", e -> layout.setDrawerOpened(false))
ButtonElement.getByText(page, "Close drawer").click();
appLayout.assertDrawerClosed();
```

### Assert on the state attributes, not on geometry

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
auto-retrying `assertThat(…).isHidden()` rather than reading `isVisible()`:

```java
appLayout.closeDrawer();
appLayout.assertDrawerClosed();
assertThat(appLayout.getDrawerLocator().first()).isHidden();
```

## Overlay mode

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
restores what was saved:

```java
appLayout.assertDrawerOpened();
page.setViewportSize(500, 900);
appLayout.assertDrawerClosed();

page.setViewportSize(1280, 720);
appLayout.assertNotOverlayMode();
appLayout.assertDrawerOpened();
```

A drawer opened while in overlay mode is covered by a backdrop that spans the
whole viewport at a higher stacking level than the navbar, so **the drawer
toggle cannot be clicked while the drawer is open**:

- `toggleDrawer()` clicks the toggle unconditionally and thus fails in that
  state.
- `closeDrawer()` and `setDrawerOpened(false)` press `Escape` in overlay mode
  and click the toggle otherwise, so they work in both modes.
- `clickBackdrop()` aims at the side of the backdrop the drawer leaves free —
  a plain click would land in the drawer, since the backdrop is centred on the
  viewport and the drawer sits on top of it.

```java
page.setViewportSize(500, 900);
appLayout.assertOverlayMode();

appLayout.openDrawer();
appLayout.assertDrawerOpened();
appLayout.closeDrawer();              // presses Escape here
appLayout.assertDrawerClosed();

appLayout.openDrawer();
appLayout.closeDrawerWithEscape();    // explicitly
appLayout.assertDrawerClosed();

appLayout.openDrawer();
appLayout.clickBackdrop();            // like a user tapping beside the drawer
appLayout.assertDrawerClosed();
```

## Primary section

The primary section is the area that comes first visually: the navbar spans the
full width (`navbar`), or the drawer spans the full height (`drawer`).

```java
// View: setPrimarySection(AppLayout.Section.DRAWER)
appLayout.assertPrimarySection(AppLayoutElement.PRIMARY_SECTION_DRAWER);

ButtonElement.getByText(page, "Primary section navbar").click(); // calls setPrimarySection(NAVBAR)
appLayout.assertPrimarySection(AppLayoutElement.PRIMARY_SECTION_NAVBAR);

String section = appLayout.getPrimarySection();   // "navbar" or "drawer", read once
```

## Theme and CSS classes

```java
appLayout.assertTheme(null);              // null: no theme attribute
ButtonElement.getByText(page, "Add theme").click();
appLayout.assertTheme("demo-theme");

appLayout.assertCssClass("custom-app-layout");
```

## Pitfalls

- **Touch-optimized navbar components move between slots.** Components added
  with `addToNavbar(true, …)` start out in the `navbar` slot and are moved by the
  component itself to `navbar-bottom` only on touchscreen devices
  (`pointer: coarse` and a small viewport). A desktop browser — including
  headless Chromium in the ITs — keeps them in the top navbar, so
  `getBottomNavbarLocator()` matches nothing there:

    ```java
    assertThat(appLayout.getBottomNavbarLocator()).hasCount(0);
    ```

- `toggleDrawer()` fails in overlay mode while the drawer is open (the backdrop
  covers the toggle); use `closeDrawer()`, `setDrawerOpened(false)`,
  `closeDrawerWithEscape()` or `clickBackdrop()`.
- Don't read `isVisible()` on the drawer right after closing it; see
  [Assert on the state attributes](#assert-on-the-state-attributes-not-on-geometry).
- The app layout is usually the `RouterLayout` around every view, so a test that
  changes it (theme, primary section, viewport size) changes the shell for the
  rest of that page. Don't share a page between such tests.

## See also

- [Side Navigation](SideNavigationElement.md) — the usual drawer content
- [Split Layout](SplitLayoutElement.md), [Form Layout](FormLayoutElement.md)
- [Finding components](../guide/finding-components.md),
  [Assertions](../guide/assertions.md),
  [Actions and waiting](../guide/actions-and-waiting.md),
  [Core concepts](../guide/core-concepts.md)
- Full example: [AppLayoutViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/AppLayoutViewIT.java)
