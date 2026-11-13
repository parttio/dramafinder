# Tab Sheet

`TabSheetElement` wraps `<vaadin-tabsheet>` (Flow: `TabSheet`): a tab strip plus
the content panel of the selected tab. It lets a test select tabs, assert the
selection and the labels, and reach the visible panel. Tab lookup and selection
are delegated to the embedded [`TabsElement`](TabsElement.md).

| | |
|---|---|
| Web component | `<vaadin-tabsheet>` |
| Flow component | `com.vaadin.flow.component.tabs.TabSheet` |
| Wrapper | `org.vaadin.addons.dramafinder.element.TabSheetElement` |
| Typed query | `find(page, TabSheetElement.class)` → `ComponentQuery` |
| API | [TabSheetElement](../../skills/vaadin-playwright-test/api-reference.md#tabsheetelement) |

## Finding the component

```java
TabSheetElement sheet = TabSheetElement.get(page);                 // first on the page
TabSheetElement byId = TabSheetElement.getById(page, "disabled-tabsheet");
TabSheetElement byTestId = TabSheetElement.getByTestId(page, "settings-sheet");
TabSheetElement scoped = TabSheetElement.getById(dialog.getLocator(), "settings-sheet");

// Typed query: generic filters only
TabSheetElement inner = outer.find(TabSheetElement.class).withId("inner-tabsheet").single();
```

Tabs are found by their exact label, as on [Tabs](TabsElement.md#tab-labels-and-count):

```java
TabElement tab = sheet.getTab("Tab 2");
TabElement selected = sheet.getSelectedTab();
```

## Selecting tabs and checking the content

```java
// View
TabSheet tabSheet = new TabSheet();
tabSheet.add("Tab 1", new Div(new Text("This is page 1")));
tabSheet.add("Tab 2", new Div(new Text("This is page 2")));
tabSheet.add("Tab 3", new Div(new Text("This is page 3")));
```

```java
// Test
TabSheetElement sheet = TabSheetElement.get(page);
sheet.assertTabsCount(3);
sheet.assertSelectedTab("Tab 1");
assertThat(sheet.getContentLocator()).hasText("This is page 1");

sheet.selectTab("Tab 2");
sheet.assertSelectedTab("Tab 2");
sheet.getTab("Tab 1").assertNotSelected();
assertThat(sheet.getContentLocator()).hasText("This is page 2");

sheet.selectTab(2);   // zero-based index
sheet.assertSelectedTab("Tab 3");
```

`getContentLocator()` is the visible panel (`:scope > [tab]:not([hidden])`).
Find components inside it through that locator:

```java
sheet.selectTab("Tab 2");
TextFieldElement name = TextFieldElement.getByLabel(sheet.getContentLocator(), "Name");
```

## Tab labels

```java
sheet.assertTabLabels("Tab 1", "Tab 2", "Tab 3");    // exact, in order, retried
List<String> labels = sheet.getTabLabels();           // read once
```

Labels are read as [`TabsElement.getTabLabels()`](TabsElement.md#tab-labels-and-count)
does: own text, trimmed, or the `aria-label` of an icon-only tab.

## The embedded tab strip

`getTabsElement()` returns the [`TabsElement`](TabsElement.md) of the sheet's
own `<vaadin-tabs>`, for everything `TabSheetElement` does not forward — index
assertions, the selected index, theme variants:

```java
TabsElement strip = sheet.getTabsElement();
strip.assertSelectedTab(0);
int index = strip.getSelectedIndex();
strip.assertHasThemeVariant("minimal");
```

## Disabled tabs

```java
// View
TabSheet withDisabled = new TabSheet();
withDisabled.setId("disabled-tabsheet");
withDisabled.add("Open", new Div(new Text("Open page")));
withDisabled.add("Locked", new Div(new Text("Locked page"))).setEnabled(false);
```

```java
// Test
TabSheetElement sheet = TabSheetElement.getById(page, "disabled-tabsheet");
sheet.getTab("Locked").assertDisabled();

assertThrows(IllegalStateException.class, () -> sheet.selectTab("Locked"));
sheet.assertSelectedTab("Open");
assertThat(sheet.getContentLocator()).hasText("Open page");
```

`selectTab` throws `IllegalStateException` at once on a disabled tab, without
waiting for Playwright's action timeout.

## Nested tab sheets

The strip and the panel are the sheet's **direct** children, so a tab sheet
nested in a panel does not interfere with the outer one:

```java
// View
TabSheet inner = new TabSheet();
inner.setId("inner-tabsheet");
inner.add("Inner 1", new Div(new Text("Inner page 1")));
inner.add("Inner 2", new Div(new Text("Inner page 2")));
TabSheet outer = new TabSheet();
outer.setId("outer-tabsheet");
outer.add("Outer 1", new Div(new Text("Outer page 1"), inner));
outer.add("Outer 2", new Div(new Text("Outer page 2")));
```

```java
// Test
TabSheetElement outer = TabSheetElement.getById(page, "outer-tabsheet");
outer.assertTabLabels("Outer 1", "Outer 2");
assertThat(outer.getContentLocator()).hasCount(1);
assertThat(outer.getContentLocator()).containsText("Outer page 1");

TabSheetElement inner = TabSheetElement.getById(page, "inner-tabsheet");
inner.selectTab("Inner 2");
assertThat(inner.getContentLocator()).hasText("Inner page 2");
outer.assertSelectedTab("Outer 1");
```

## Pitfalls

- The count assertion is named `assertTabsCount` here and `assertTabCount` on
  `TabsElement`.
- `TabSheetElement` has no theme, class-name or enabled helpers of its own; use
  `getTabsElement()` for the strip, or Playwright assertions on `getLocator()`.
- `TabSheetElement.get(page)` takes the first sheet in DOM order — with nested
  sheets that is the outer one. Use ids for anything but a single sheet.

## See also

- [Tabs](TabsElement.md) — labels, orientation, disabled tabs in detail
- [Accordion](AccordionElement.md)
- [Finding components](../guide/finding-components.md),
  [Assertions](../guide/assertions.md),
  [Actions and waiting](../guide/actions-and-waiting.md),
  [Core concepts](../guide/core-concepts.md)
- Full example: [TabSheetViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/TabSheetViewIT.java)
