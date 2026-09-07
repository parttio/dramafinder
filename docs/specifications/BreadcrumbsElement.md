# BreadcrumbsElement

> Full API (methods, signatures, descriptions): see [api-reference.md](../../skills/vaadin-playwright-test/api-reference.md). This note covers only behaviour that isn't obvious from the signatures.

## The component is experimental

`Breadcrumbs` is experimental in Vaadin 25.3: it throws an
`ExperimentalFeatureException` on attach, and `vaadin-breadcrumbs` /
`vaadin-breadcrumbs-item` are not registered as custom elements, unless the
feature flag is enabled:

```properties
# src/test/resources/vaadin-featureflags.properties
com.vaadin.experimental.breadcrumbsComponent=true
```

A view under test that shows breadcrumbs will render nothing without it.

## Items collapse into an overflow overlay, but stay in the trail

When the trail does not fit, the component sets the `has-overflow` attribute,
reveals `[part='overflow-button']` and moves the collapsed items — closest to
the root first, the last item never collapses — to `slot="overlay"`. Those
items remain light-DOM children of the host, so `getItems()`,
`assertItemCount()` and `assertItemTexts()` see the whole trail, collapsed
items included, no matter how narrow the component is.

Only the visibility differs: a collapsed item is rendered inside the overlay
and is therefore not visible until the overlay is opened.

```java
BreadcrumbsElement breadcrumbs = BreadcrumbsElement.getByLabel(page, "Main breadcrumbs");

breadcrumbs.assertHasOverflow();
breadcrumbs.assertItemTexts("Continents", "South America", "Argentina", "Palermo");

breadcrumbs.openOverflow();
breadcrumbs.getItem("Continents").click(); // navigates
breadcrumbs.closeOverflow();
```

`openOverflow()` and `closeOverflow()` read `aria-expanded` on the overflow
button first, so calling them on an already open (or closed) overlay does
nothing instead of toggling it the wrong way. The open overlay captures
pointer events, so `closeOverflow()` presses `Escape` rather than clicking the
overflow button a second time.

The accessible name of the overflow button comes from
`BreadcrumbsI18n.moreItems` and defaults to `More items`; assert it with
`assertOverflowButtonAriaLabel(String)`.

## Current item, links and non-links

An item renders as `[part='link']` (an `<a>`) when it has a `path`, and as
`[part='nolink']` (a `<span>`) when it has none. The parent marks the last item
as the current page when that item has no path: the host gets the `current`
attribute and the inner `[part='nolink']` gets `aria-current="page"`.
`assertCurrent()` checks both.

`click()` targets the `[part='link']` anchor on a link item, so the click
navigates; on a non-link item it clicks the host instead.

## Lookup

`vaadin-breadcrumbs` renders with the ARIA role `navigation`, so
`getByLabel(page, label)` matches the value set by `Breadcrumbs.setAriaLabel`.
Item lookup by text (`getItem(String)`, `BreadcrumbsItemElement.getByText`)
matches the item's full text, so `getItem("Products")` does not match an item
reading `Products archive`.
