# Side Navigation

`SideNavigationElement` wraps `<vaadin-side-nav>` (Flow: `SideNav`) and
`SideNavigationItemElement` wraps each `<vaadin-side-nav-item>` (Flow:
`SideNavItem`). A test finds items by their own label at any nesting depth,
navigates with them, expands and collapses parents and the whole navigation,
and asserts which item is current and which are disabled.

| | |
|---|---|
| Web component | `<vaadin-side-nav>`, `<vaadin-side-nav-item>` |
| Flow component | `com.vaadin.flow.component.sidenav.SideNav`, `SideNavItem` |
| Wrapper | `org.vaadin.addons.dramafinder.element.SideNavigationElement`, `SideNavigationItemElement` |
| Typed query | `find(page, SideNavigationElement.class)` → `FieldQuery`; `find(page, SideNavigationItemElement.class)` → `SideNavItemQuery` |
| API | [SideNavigationElement](../../skills/vaadin-playwright-test/api-reference.md#sidenavigationelement), [SideNavigationItemElement](../../skills/vaadin-playwright-test/api-reference.md#sidenavigationitemelement) |

The examples use this view:

```java
// View
SideNav nav = new SideNav("My App");
nav.setCollapsible(true);
nav.setId("default-nav");

SideNavItem dashboard = new SideNavItem("Dashboard");
dashboard.setPath("");
SideNavItem messages = new SideNavItem("Messages");
messages.setPath("side-navigation-element-view");   // the current route

SideNavItem admin = new SideNavItem("Admin");
admin.setPrefixComponent(VaadinIcon.COG.create());
admin.setSuffixComponent(VaadinIcon.ACCORDION_MENU.create());
admin.addItem(new SideNavItem("Users", "admin/users"), new SideNavItem("Roles", "admin/roles"));

SideNavItem reports = new SideNavItem("Reports");
reports.setEnabled(false);

nav.addItem(dashboard, messages, admin, reports);
```

## Finding the component

The navigation is found by its visible label (the text passed to
`new SideNav("My App")`, rendered in the `label` slot):

```java
SideNavigationElement nav = SideNavigationElement.getByLabel(page, "My App");
SideNavigationElement partial = SideNavigationElement.getByLabelContaining(page, "app");
SideNavigationElement inDrawer = SideNavigationElement.getByLabel(appLayout.getDrawerLocator(), "My App");
SideNavigationElement byId = SideNavigationElement.getById(page, "default-nav");
SideNavigationElement byTestId = SideNavigationElement.getByTestId(page, "default-nav");

// Typed query
SideNavigationElement same = find(page, SideNavigationElement.class).withLabel("My App").single();
// A navigation without a visible label, named with setAriaLabel("Main nav")
SideNavigationElement main = find(page, SideNavigationElement.class).withAccessibleName("Main nav").single();
```

`getByLabel` / `withLabel` match the whole label, case-sensitively, and never
an `aria-label`; `getByLabelContaining` / `withLabelContaining` are the
case-insensitive substring variants. Neither applies `.first()`: two matching
navigations fail with a strict-mode error.

Items are found by their **own** label — at any depth:

```java
SideNavigationItemElement dashboard = nav.getItem("Dashboard");
SideNavigationItemElement users = nav.getItem("Users");      // nested under "Admin"

// Static factories and typed query, page-wide or scoped
SideNavigationItemElement roles = SideNavigationItemElement.getByLabel(page, "Roles");
SideNavigationItemElement inNav = SideNavigationItemElement.getByLabel(nav.getLocator(), "Roles");
SideNavigationItemElement rep = SideNavigationItemElement.getByLabelContaining(nav.getLocator(), "report");
SideNavigationItemElement byId = SideNavigationItemElement.getById(page, "reports-item");
SideNavigationItemElement byTestId = SideNavigationItemElement.getByTestId(nav.getLocator(), "reports-item");
SideNavigationItemElement admin = nav.find(SideNavigationItemElement.class).withLabel("Admin").single();
```

### Exact own-text matching

An item's own label is its own text, **without** its prefix, its suffix and
the labels of the items nested in it. The match is exact (case-sensitive,
whitespace-normalized):

- `nav.getItem("Admin")` resolves to the parent, never to one of its children,
  even though the parent's subtree text reads `"Admin Users Roles"`.
- `nav.getItem("Item 1")` does not resolve to `"Item 10"`.
- Two items sharing a label fail with a strict-mode error instead of resolving
  to whichever comes first.

```java
// A parent "Section" with children "Section 1" and "Section 2", expanded
SideNavigationItemElement section = nav.getItem("Section");   // the parent
section.assertExpanded();
nav.getItem("Section 1").assertLabel("Section 1");             // its child
```

`withLabelContaining("dash")` matches every item whose own label contains
`dash`, ignoring case — use `.first()`, `.all()` or `.count()` when several can
match.

## Navigating

```java
// Any of these follows the "Dashboard" link (path "")
nav.getItem("Dashboard").navigate();  // clicks the item's link
nav.clickItem("Dashboard");           // clicks the item, found by label
nav.getItem("Dashboard").click();     // the same, on the element

assertThat(page).hasURL(getUrl());
```

All three wait for the server round-trip. `navigate()` clicks the first link
inside the item; `click()` / `clickItem()` click the item host, which follows
the link on an item with a path and expands or collapses an item without one.

## The current item

The web component marks the item whose path matches the current URL with the
`current` attribute:

```java
nav.getItem("Messages").assertCurrent();
nav.getItem("Reports").assertNotCurrent();
```

## Expanding and collapsing items

A parent item is collapsed until expanded. `toggle()` clicks the item's toggle
button; on a parent without a path a plain `click()` does the same:

```java
SideNavigationItemElement admin = nav.getItem("Admin");
admin.assertCollapsed();

admin.toggle();
admin.assertExpanded();
nav.getItem("Users").assertVisible();

admin.toggle();
admin.assertCollapsed();

admin.click();                        // no path: the click expands it
admin.assertExpanded();
```

`isExpanded()` reads the `expanded` attribute once; use it for branching:

```java
if (!admin.isExpanded()) {
    admin.toggle();
}
```

!!! note
    `getItem` finds items at any depth, but an action on a child needs the child
    to be visible: expand its parent first.

## Collapsing the whole navigation

A collapsible navigation (`setCollapsible(true)`) shows its label as a toggle.
`SideNavigationElement.toggle()` clicks that label:

```java
nav.assertCollapsible();
nav.assertExpanded();

nav.toggle();
nav.assertCollapsed();
boolean collapsed = nav.isCollapsed();   // read once

nav.toggle();
nav.assertExpanded();

SideNavigationElement.getById(page, "static-nav").assertNotCollapsible();
```

## Labels

```java
nav.assertLabel("My App");
nav.getItem("Dashboard").assertLabel("Dashboard");

String navLabel = nav.getLabel();
String itemLabel = nav.getItem("Dashboard").getLabel();
```

`assertLabel(null)` asserts the label is hidden (or absent).

## Prefix and suffix

Items implement `HasPrefixElement` and `HasSuffixElement`. An icon has no text,
so check its presence with the locator:

```java
SideNavigationItemElement admin = nav.getItem("Admin");
assertThat(admin.getPrefixLocator()).isVisible();
assertThat(admin.getSuffixLocator()).isVisible();

// A text suffix, e.g. item.setSuffixComponent(new Span("12"))
nav.getItem("Inbox").assertSuffixHasText("12");
nav.getItem("Dashboard").assertPrefixHasText(null);   // null: no visible prefix
```

## Disabled items

`SideNavigationItemElement` implements `HasDisabledAttributeElement`. The item
host has the `listitem` role, which Playwright never reports as disabled, so
`isEnabled()` and the assertions read the `disabled` attribute instead:

```java
SideNavigationItemElement reports = nav.getItem("Reports");
reports.assertDisabled();
boolean enabled = reports.isEnabled();        // false
boolean matches = reports.isEnabled(false);   // true

nav.getItem("Dashboard").assertEnabled();
```

## Pitfalls

- `getItem` and the item label lookups match the item's own **text nodes**. An
  item whose label is wrapped in an element instead of being plain text (a
  custom web component, a `<span>` you added) is not found by label; use its id
  or test id.
- On an item, `getLabelLocator()` is the item host itself, so `getLabel()` and
  `assertLabel(…)` read the item's whole rendered text — a text suffix or
  visible child items become part of it. Check a parent's label by finding it
  with `getItem(label)` (an exact own-text match), and keep `assertLabel` for
  leaf items without text in their prefix/suffix.
- `getPrefixLocator()` / `getSuffixLocator()` take the first matching slotted
  element in the item's subtree. On a parent without a prefix of its own, that
  can be the prefix of one of its expanded children.
- `SideNavigationElement.toggle()` clicks the navigation's label: it only works
  on a navigation that has a label and is collapsible.
- `isExpanded()`, `isCollapsed()` and `isEnabled()` read once; use the
  `assert…` methods after an action.

## See also

- [App Layout](AppLayoutElement.md) — the drawer that usually holds the navigation
- [Breadcrumbs](BreadcrumbsElement.md), [Tabs](TabsElement.md)
- [Finding components](../guide/finding-components.md),
  [Assertions](../guide/assertions.md),
  [Actions and waiting](../guide/actions-and-waiting.md),
  [Core concepts](../guide/core-concepts.md)
- Full example: [SideNavigationElementViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/SideNavigationElementViewIT.java)
