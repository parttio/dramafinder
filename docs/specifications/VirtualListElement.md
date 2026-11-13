# Virtual List

`VirtualListElement` wraps `<vaadin-virtual-list>` (Flow: `VirtualList`), a
scrollable list that renders only the items in view. It lets a test count items,
scroll, check which rows are in view and reach the components rendered inside an
item.

| | |
|---|---|
| Web component | `<vaadin-virtual-list>` |
| Flow component | `com.vaadin.flow.component.virtuallist.VirtualList` |
| Wrapper | `org.vaadin.addons.dramafinder.element.VirtualListElement` |
| Typed query | `find(page, VirtualListElement.class)` → `ComponentQuery` |
| API | [VirtualListElement](../../skills/vaadin-playwright-test/api-reference.md#virtuallistelement) |

## Finding the component

```java
VirtualListElement list = VirtualListElement.getById(page, "basic-virtual-list");
VirtualListElement first = VirtualListElement.get(page);        // first list on the page
VirtualListElement byTestId = VirtualListElement.getByTestId(page, "messages");
VirtualListElement scoped = VirtualListElement.getById(page.locator("#sidebar"), "contacts");
```

The typed query has the filters every component has:

```java
import static org.vaadin.addons.dramafinder.element.query.ElementQueries.find;

VirtualListElement styled = find(page, VirtualListElement.class)
        .withClassName("custom-virtual-list").single();
```

## Counting items

```java
// View
VirtualList<String> list = new VirtualList<>();
list.setId("basic-virtual-list");
list.setHeight("200px");
list.setItems(IntStream.rangeClosed(1, 100).mapToObj(i -> "Item " + i).toList());
list.setRenderer(new ComponentRenderer<>(item -> {
    Span span = new Span(item);
    span.addClassName("virtual-list-item");
    return span;
}));
```

```java
// Test
VirtualListElement list = VirtualListElement.getById(page, "basic-virtual-list");
list.assertRowCount(100);        // all items, rendered or not

VirtualListElement.getById(page, "empty-virtual-list").assertEmpty();
```

`getRowCount()` returns the same number, including items not yet rendered. A lazy
list backed by `DataProvider.fromCallbacks(...)` reports the count its count
callback returns (`assertRowCount(500)` for a 500-item provider).

## Scrolling and what is in view

```java
VirtualListElement list = VirtualListElement.getById(page, "basic-virtual-list");
list.assertFirstVisibleRow(0);
list.assertRowInView(0);
list.assertRowNotInView(50);

list.scrollToRow(50);
list.assertRowInView(50);
list.assertRowNotInView(0);

list.scrollToEnd();
list.assertRowInView(99);
list.scrollToStart();
list.assertFirstVisibleRow(0);
```

A row is "in view" when it is at least partially visible. The assertions retry;
`assertFirstVisibleRow` / `assertLastVisibleRow` use Playwright's `hasJSProperty`,
and the connector's 50 ms debouncer settles within the retry window. The getters
read the same state once, when the number itself is needed:

| Getter | Returns |
|--------|---------|
| `getFirstVisibleRowIndex()` | 0-based index of the first (partially) visible row |
| `getLastVisibleRowIndex()` | 0-based index of the last (partially) visible row |
| `getVisibleRowCount()` | Number of rows visible in the viewport |
| `isRowInView(int rowIndex)` | Whether the row is between the first and last visible index |

All of them flush the connector debouncer and read the value in a single browser
round trip.

```java
int pageSize = list.getVisibleRowCount();   // e.g. to page through the list
list.scrollToRow(list.getLastVisibleRowIndex() + 1);
```

## Reading rendered items

Items are only in the DOM while rendered, so scroll to an item before reading it:

```java
list.assertItemRendered("Item 1");

list.scrollToRow(50);
list.assertItemRendered("Item 51");

assertThat(list.getItemByIndex(50)).hasText("Item 51");   // virtual index, must be in view
assertThat(list.getItemByText("Item 51")).isVisible();
assertThat(list.getRenderedItems()).not().hasCount(0);   // every rendered item root
```

`getItemByText` and `assertItemRendered` match the text **case-insensitively as a
substring** and take the first match: an item renders an arbitrary component, so
there is no single label to match in full. Pass enough text to be unambiguous —
`"Item 1"` also matches `"Item 10"` to `"Item 19"` when they are rendered.

## Components inside items

When the renderer creates Vaadin components, get them as typed elements:

```java
// View
list.setRenderer(new ComponentRenderer<>(item -> {
    Span label = new Span(item);
    Button button = new Button("Action " + item);
    return new HorizontalLayout(label, button);
}));
```

```java
// Test
VirtualListElement list = VirtualListElement.getById(page, "component-virtual-list");

ButtonElement first = list.getItemComponent(0, ButtonElement.class);       // item at index 0
assertThat(first.getLocator()).hasText("Action Row 1");

ButtonElement third = list.getItemComponentByText("Row 3", ButtonElement.class);
third.click();

ButtonElement any = list.getComponent(ButtonElement.class);   // first in any rendered item

list.scrollToRow(20);
list.getItemComponentByText("Row 21", ButtonElement.class).assertVisible();
```

These methods read the tag from the `@PlaywrightElement` annotation on the target
class and need a public constructor taking a single `Locator`; they throw
`IllegalArgumentException` otherwise. They return the **first** matching
component inside the item, and `getItemComponentByText` finds the item the same
substring way as `getItemByText`.

## Styling and focus

```java
VirtualListElement list = VirtualListElement.getById(page, "styled-virtual-list");
list.assertCssClass("custom-virtual-list");   // list.addClassName("custom-virtual-list")
list.focus();
```

## Pitfalls

- **Only rendered items exist.** `getItemByIndex`, `getItemByText`,
  `assertItemRendered` and the component lookups work on items currently in the
  DOM. Scroll with `scrollToRow` first.
- **Substring text matching.** `"Row 1"` also matches `"Row 10"`; use enough text,
  or `getItemByIndex` / `getItemComponent` with an index.
- **First match wins.** The text and component lookups end in `.first()`; they do
  not fail on ambiguity.

## See also

- [Grid](GridElement.md) — a virtualized list with columns, selection and sorting
- [List Box](ListBoxElement.md) — a non-virtualized list with selection
- [Assertions](../guide/assertions.md)
- [Finding components](../guide/finding-components.md)
- [Actions and waiting](../guide/actions-and-waiting.md)
- [Core concepts](../guide/core-concepts.md)
- Full example: [VirtualListViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/VirtualListViewIT.java)
