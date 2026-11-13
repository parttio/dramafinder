# Split Layout

`SplitLayoutElement` wraps `<vaadin-split-layout>` (Flow: `SplitLayout`). It
gives a test the primary and secondary areas, the splitter handle, the
orientation, and a way to drag the splitter like a user does.

| | |
|---|---|
| Web component | `<vaadin-split-layout>` |
| Flow component | `com.vaadin.flow.component.splitlayout.SplitLayout` |
| Wrapper | `org.vaadin.addons.dramafinder.element.SplitLayoutElement` |
| Typed query | `find(page, SplitLayoutElement.class)` → `ComponentQuery` |
| API | [SplitLayoutElement](../../skills/vaadin-playwright-test/api-reference.md#splitlayoutelement) |

## Finding the component

A split layout has no label or text of its own, so it is found by position, id
or test id:

```java
SplitLayoutElement split = SplitLayoutElement.get(page);               // first on the page
SplitLayoutElement editor = SplitLayoutElement.getById(page, "editor-split");
SplitLayoutElement byTestId = SplitLayoutElement.getByTestId(page, "editor-split");

// Scoped to a container
SplitLayoutElement inDialog = SplitLayoutElement.getById(dialog.getLocator(), "preview-split");
```

The typed query offers the generic filters (`withId`, `withTestId`,
`withAttribute`, `withClassName`, `withAccessibleName`). `withAttribute` is
handy to pick a layout by orientation:

```java
SplitLayoutElement vertical = find(page, SplitLayoutElement.class)
        .withAttribute("orientation", "vertical")
        .first();
```

`get(Page)` takes the first layout in DOM order; there is no `get(Locator)`
overload, so use `getById(Locator, …)` / `getByTestId(Locator, …)` or
`element.find(SplitLayoutElement.class)` for a scoped lookup.

## Primary and secondary content

`getPrimaryLocator()` and `getSecondaryLocator()` select the components Flow put
in `slot="primary"` and `slot="secondary"`. Chain a further locator, or wrap a
component found inside them:

```java
// View
SplitLayout layout = new SplitLayout(new Div(new Div("Primary 1")), new Div(new Div("Secondary 1")));
```

```java
// Test
SplitLayoutElement split = SplitLayoutElement.get(page);
assertThat(split.getPrimaryLocator().getByText("Primary 1")).isVisible();
assertThat(split.getSecondaryLocator().getByText("Secondary 1")).isVisible();

GridElement grid = GridElement.get(split.getPrimaryLocator());
```

## Orientation

```java
// View
SplitLayout vertical = new SplitLayout(new Div("Primary V"), new Div("Secondary V"));
vertical.setOrientation(SplitLayout.Orientation.VERTICAL);
```

```java
// Test
split.assertVertical();     // orientation="vertical"
other.assertHorizontal();   // no orientation attribute, or anything but "vertical"
```

## Moving the splitter

`dragSplitterBy(deltaX, deltaY)` presses the mouse on the centre of the handle,
moves it by the given number of pixels in ten steps and releases it, then waits
for the server round-trip it triggers (a drag-end listener, for instance). Positive X
moves right, positive Y moves down. Use the X delta for a horizontal layout and
the Y delta for a vertical one.

```java
// View
SplitLayout positioned = new SplitLayout(new Div("Primary P"), new Div("Secondary P"));
positioned.setSplitterPosition(70); // percent
```

```java
// Test
SplitLayoutElement split = SplitLayoutElement.getById(page, "positioned-split");
double before = split.getPrimaryLocator().boundingBox().width;

split.dragSplitterBy(-100, 0);

double after = split.getPrimaryLocator().boundingBox().width;
assertEquals(before - 100, after, 5); // allow a few pixels of rounding
```

There is no splitter-position getter: measure the primary area with
`boundingBox()` as above, or assert on what the application does with the new
position. The handle itself is available as `getHandleLocator()` (the
`[part='handle']` shadow part), e.g. to check it is visible:

```java
assertThat(split.getHandleLocator()).isVisible();
```

## Theme variants and CSS classes

```java
// View
layout.addThemeVariants(SplitLayoutVariant.SMALL);
layout.addClassName("editor-split");
```

```java
// Test
split.assertHasThemeVariant("small");
split.assertCssClass("editor-split");
```

## Pitfalls

- `dragSplitterBy` does nothing when the handle has no bounding box (the layout
  is hidden or not rendered yet) — it neither drags nor fails. Assert the layout
  is visible first (`split.assertVisible()`) when that can happen.
- The drag lands where the browser lets it: the component clamps the splitter to
  the minimum and maximum sizes of the two areas, so a large delta may move it
  less than asked. Compare widths with a tolerance.
- Geometry is read without retrying. Give the page a fixed viewport
  (`page.setViewportSize(…)`) before measuring, so the layout's width does not
  depend on the window.
- `getPrimaryLocator()`, `getSecondaryLocator()` and `getHandleLocator()` search
  the whole subtree. With a split layout nested inside another one, the outer
  element's locators also match the inner layout's areas and handle; target the
  inner layout directly instead (by id, or `split.find(SplitLayoutElement.class)`).

## See also

- [App Layout](AppLayoutElement.md), [Form Layout](FormLayoutElement.md)
- [Finding components](../guide/finding-components.md),
  [Actions and waiting](../guide/actions-and-waiting.md),
  [Assertions](../guide/assertions.md), [Core concepts](../guide/core-concepts.md)
- Full example: [SplitLayoutViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/SplitLayoutViewIT.java)
