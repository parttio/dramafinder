# Breadcrumbs

`BreadcrumbsElement` wraps `<vaadin-breadcrumbs>` (Flow: `Breadcrumbs`) and
`BreadcrumbsItemElement` wraps each `<vaadin-breadcrumbs-item>` (Flow:
`BreadcrumbsItem`). A test reads the trail, follows its links, checks the
current page and the disabled items, and opens the overflow overlay holding the
items that do not fit.

| | |
|---|---|
| Web component | `<vaadin-breadcrumbs>`, `<vaadin-breadcrumbs-item>` |
| Flow component | `com.vaadin.flow.component.breadcrumbs.Breadcrumbs`, `BreadcrumbsItem` |
| Wrapper | `org.vaadin.addons.dramafinder.element.BreadcrumbsElement`, `BreadcrumbsItemElement` |
| Typed query | `find(page, BreadcrumbsElement.class)` → `ComponentQuery`; `find(page, BreadcrumbsItemElement.class)` → `TextQuery` |
| API | [BreadcrumbsElement](../../skills/vaadin-playwright-test/api-reference.md#breadcrumbselement), [BreadcrumbsItemElement](../../skills/vaadin-playwright-test/api-reference.md#breadcrumbsitemelement) |

!!! warning "The component is experimental"
    `Breadcrumbs` is experimental in Vaadin 25.3: it throws an
    `ExperimentalFeatureException` on attach, and `vaadin-breadcrumbs` /
    `vaadin-breadcrumbs-item` are not registered as custom elements, unless the
    feature flag is enabled:

    ```properties
    # src/test/resources/vaadin-featureflags.properties
    com.vaadin.experimental.breadcrumbsComponent=true
    ```

    A view under test that shows breadcrumbs will render nothing without it.

The examples use this view:

```java
// View
Breadcrumbs breadcrumbs = new Breadcrumbs(Breadcrumbs.Mode.MANUAL);
breadcrumbs.setAriaLabel("Main breadcrumbs");
breadcrumbs.addClassName("main-breadcrumbs");
breadcrumbs.addThemeVariants(BreadcrumbsVariant.SLASH);

BreadcrumbsItem home = new BreadcrumbsItem("Home", "card");
home.setPrefixComponent(VaadinIcon.HOME.create());
BreadcrumbsItem movies = new BreadcrumbsItem("Movies", "grid-basic");
BreadcrumbsItem archive = new BreadcrumbsItem("Archive", "grid-sorting");
archive.setEnabled(false);
BreadcrumbsItem drama = new BreadcrumbsItem("Drama");   // no path: the current page

breadcrumbs.add(home, movies, archive, drama);
```

## Finding the component

`vaadin-breadcrumbs` renders with the ARIA role `navigation` and has no visible
label: it is named by `Breadcrumbs.setAriaLabel`. Find it by that accessible
name with the typed query:

```java
BreadcrumbsElement trail = find(page, BreadcrumbsElement.class)
        .withAccessibleName("Main breadcrumbs").single();
BreadcrumbsElement narrow = find(page, BreadcrumbsElement.class)
        .withAccessibleNameContaining("narrow").single();

BreadcrumbsElement first = BreadcrumbsElement.get(page);             // first on the page
BreadcrumbsElement inHeader = BreadcrumbsElement.get(header.getLocator());
BreadcrumbsElement byId = BreadcrumbsElement.getById(page, "trail");
BreadcrumbsElement byTestId = BreadcrumbsElement.getByTestId(page, "trail");
```

!!! note
    `BreadcrumbsElement.getByLabel(page, label)` and `getByLabelContaining` still
    exist, but are deprecated (to be removed in 3.0): they match the accessible
    name — the value set by `Breadcrumbs.setAriaLabel` — because the component has
    no visible label. `withAccessibleName` / `withAccessibleNameContaining` are
    the replacements.

Items are found through the trail:

```java
BreadcrumbsItemElement movies = trail.getItem("Movies");   // by full text
BreadcrumbsItemElement second = trail.getItem(1);          // by zero-based position
BreadcrumbsItemElement current = trail.getCurrentItem();
List<BreadcrumbsItemElement> items = trail.getItems();     // in trail order

// Or directly
BreadcrumbsItemElement byText = BreadcrumbsItemElement.getByText(page, "Movies");
BreadcrumbsItemElement scoped = BreadcrumbsItemElement.getByText(trail.getLocator(), "Archive");
BreadcrumbsItemElement typed = trail.find(BreadcrumbsItemElement.class).withText("Archive").single();
BreadcrumbsItemElement itemById = BreadcrumbsItemElement.getById(page, "home-crumb");
BreadcrumbsItemElement itemByTestId = BreadcrumbsItemElement.getByTestId(trail.getLocator(), "home-crumb");
```

Item lookup by text (`getItem(String)`, `BreadcrumbsItemElement.getByText`)
matches the item's full text, so `getItem("Products")` does not match an item
reading `Products archive`. When no item has the text, the returned element's
locator matches nothing.

## Reading the trail

```java
trail.assertItemCount(4);
trail.assertItemTexts("Home", "Movies", "Archive", "Drama");   // exact, in trail order
trail.getItem(1).assertText("Movies");

assertThat(trail.getItemsLocator()).hasCount(4);
String text = trail.getItems().get(0).getText();                // read once
```

## Links and paths

An item renders as `[part='link']` (an `<a>`) when it has a `path`, and as
`[part='nolink']` (a `<span>`) when it has none.

```java
BreadcrumbsItemElement home = trail.getItem("Home");
home.assertLink();
home.assertPath("card");
assertThat(home.getLinkLocator()).hasAttribute("href", "card");

BreadcrumbsItemElement drama = trail.getItem("Drama");
drama.assertNotLink();
drama.assertPath(null);         // null: the item has no path

boolean isLink = home.isLink(); // read once
String path = home.getPath();   // null when the item has no path
```

`click()` targets the `[part='link']` anchor on a link item, so the click
navigates; on a non-link item it clicks the host instead.

```java
trail.getItem("Home").click();
assertThat(page).hasURL(getUrl() + "card");
```

## The current item

The parent marks the last item as the current page when that item has no path:
the host gets the `current` attribute and the inner `[part='nolink']` gets
`aria-current="page"`. `assertCurrent()` checks both.

```java
BreadcrumbsItemElement current = trail.getCurrentItem();
current.assertText("Drama");
current.assertCurrent();

trail.getItem("Home").assertNotCurrent();
boolean isCurrent = current.isCurrent();   // read once
```

`getCurrentItem()` matches nothing when the trail has no current item (its last
item has a path).

## Disabled items and prefixes

`BreadcrumbsItemElement` implements `HasDisabledAttributeElement`, so the
enabled state is read from the item's `disabled` attribute:

```java
trail.getItem("Archive").assertDisabled();
trail.getItem("Movies").assertEnabled();
boolean enabled = trail.getItem("Archive").isEnabled();   // false
```

An icon prefix has no text; check its presence:

```java
BreadcrumbsItemElement home = trail.getItem("Home");
home.assertHasPrefix();
assertThat(home.getPrefixLocator()).isVisible();
trail.getItem("Movies").assertHasNoPrefix();
boolean hasPrefix = home.hasPrefix();   // read once
```

## Overflow

When the trail does not fit, the component sets the `has-overflow` attribute,
reveals `[part='overflow-button']` and moves the collapsed items — closest to
the root first, the last item never collapses — to `slot="overlay"`. Those
items remain light-DOM children of the host, so `getItems()`,
`assertItemCount()` and `assertItemTexts()` see the whole trail, collapsed
items included, no matter how narrow the component is.

Only the visibility differs: a collapsed item is rendered inside the overlay
and is therefore not visible until the overlay is opened.

```java
// View
Breadcrumbs narrow = new Breadcrumbs(Breadcrumbs.Mode.MANUAL);
narrow.setAriaLabel("Narrow breadcrumbs");
narrow.setWidth("220px");
narrow.add(new BreadcrumbsItem("Continents", "card"), new BreadcrumbsItem("South America", "card"),
        new BreadcrumbsItem("Argentina", "card"), new BreadcrumbsItem("Buenos Aires", "card"),
        new BreadcrumbsItem("Palermo"));
```

```java
// Test
BreadcrumbsElement breadcrumbs = find(page, BreadcrumbsElement.class)
        .withAccessibleName("Narrow breadcrumbs").single();

breadcrumbs.assertHasOverflow();
breadcrumbs.assertItemCount(5);   // collapsed items stay part of the trail
breadcrumbs.assertItemTexts("Continents", "South America", "Argentina", "Buenos Aires", "Palermo");
assertThat(breadcrumbs.getOverflowItemsLocator().first()).not().isVisible();

breadcrumbs.openOverflow();
breadcrumbs.assertOverflowOpen();
breadcrumbs.getItem("Continents").click(); // navigates
```

```java
breadcrumbs.openOverflow();
breadcrumbs.closeOverflow();
breadcrumbs.assertOverflowClosed();

List<BreadcrumbsItemElement> collapsed = breadcrumbs.getOverflowItems(); // in trail order
boolean overflowing = breadcrumbs.hasOverflow();                         // read once
boolean open = breadcrumbs.isOverflowOpen();                             // read once
assertThat(breadcrumbs.getOverflowButtonLocator()).isVisible();

// A trail that fits
trail.assertHasNoOverflow();
```

`openOverflow()` and `closeOverflow()` read `aria-expanded` on the overflow
button first, so calling them on an already open (or closed) overlay does
nothing instead of toggling it the wrong way. The open overlay captures
pointer events, so `closeOverflow()` presses `Escape` rather than clicking the
overflow button a second time.

The accessible name of the overflow button comes from
`BreadcrumbsI18n.moreItems` and defaults to `More items`; assert it with
`assertOverflowButtonAriaLabel(String)`:

```java
// View: narrow.setI18n(new Breadcrumbs.BreadcrumbsI18n().setMoreItems("Show more items"));
breadcrumbs.assertOverflowButtonAriaLabel("Show more items");
```

## Accessible name, theme and CSS classes

```java
trail.assertAriaLabel("Main breadcrumbs");
trail.assertTheme("slash");
trail.assertCssClass("main-breadcrumbs");
```

## Pitfalls

- Without the `breadcrumbsComponent` feature flag the view renders nothing, and
  every lookup times out.
- How many items collapse depends on the available width; assert on the trail
  (`assertItemTexts`) rather than on an exact `getOverflowItems().size()`. The
  items closest to the root collapse first and the last item never does.
- A collapsed item is not visible until the overflow is opened: open it before
  clicking such an item.
- `getItem(String)` and `BreadcrumbsItemElement.getByText` keep `.first()`, so a
  trail with two items of the same text resolves to the first one rather than
  failing.

## See also

- [Side Navigation](SideNavigationElement.md), [App Layout](AppLayoutElement.md)
- [Finding components](../guide/finding-components.md),
  [Assertions](../guide/assertions.md),
  [Actions and waiting](../guide/actions-and-waiting.md),
  [Core concepts](../guide/core-concepts.md)
- Full example: [BreadcrumbsViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/BreadcrumbsViewIT.java)
