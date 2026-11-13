# List Box

`ListBoxElement` wraps `<vaadin-list-box>` (Flow: `ListBox` and
`MultiSelectListBox`). It lets a test select items by label in single and
multiple selection mode, assert the selection, and check whether the list box or
one of its items is enabled.

| | |
|---|---|
| Web component | `<vaadin-list-box>` (items: `<vaadin-item>`) |
| Flow component | `com.vaadin.flow.component.listbox.ListBox`, `com.vaadin.flow.component.listbox.MultiSelectListBox` |
| Wrapper | `org.vaadin.addons.dramafinder.element.ListBoxElement` |
| Typed query | `find(page, ListBoxElement.class)` → `ComponentQuery` |
| API | [ListBoxElement](../../skills/vaadin-playwright-test/api-reference.md#listboxelement) |

## Finding the component

A list box has no visible label, so find it by its accessible name (an
`aria-label`, or the text of the element named by `aria-labelledby`), by id or by
test id:

```java
// View
ListBox<String> listBox = new ListBox<>();
listBox.setItems("Most recent first", "Rating: high to low", "Rating: low to high");
listBox.setAriaLabel("Invisible label");
```

```java
// Test
ListBoxElement sort = find(page, ListBoxElement.class).withAccessibleName("Invisible label").single();
ListBoxElement labelledBy = find(page, ListBoxElement.class).withAccessibleName("Labelled by").single();
ListBoxElement partial = find(page, ListBoxElement.class).withAccessibleNameContaining("databean").single();

ListBoxElement byId = ListBoxElement.getById(page, "sort-order");
ListBoxElement byTestId = ListBoxElement.getByTestId(page, "sort-order");
ListBoxElement scoped = ListBoxElement.getById(page.locator("#container"), "sort-order");
```

`withAccessibleName` matches the whole name, case-sensitively;
`withAccessibleNameContaining` a case-insensitive substring.

!!! note "Deprecated `getByLabel`"

    `ListBoxElement.getByLabel(page, ..)` and `getByLabelContaining(page, ..)`
    match the accessible name (ARIA role `listbox`), not a visible label. They
    are deprecated since 2.0.0 and will be removed in 3.0: use
    `find(page, ListBoxElement.class).withAccessibleName(..)` /
    `.withAccessibleNameContaining(..)` instead.

## Single selection

```java
// View
listBox.setValue("Most recent first");
```

```java
// Test
ListBoxElement sort = find(page, ListBoxElement.class).withAccessibleName("Invisible label").single();
sort.assertSingle();
sort.assertSelectedValue("Most recent first");

sort.selectItem("Rating: high to low");
sort.assertSelectedValue("Rating: high to low");

sort.selectItem("Rating: high to low");      // clicking the selected item keeps it selected
sort.assertSelectedValue("Rating: high to low");
```

`selectItem(label)` clicks the item whose **whole** label is `label` and waits
for the server round-trip. `assertSelectedValue(..)` checks that exactly these
items carry the `selected` attribute. `getSingleSelectedValue()` returns the
selected item's text when the test needs the value itself.

## Multiple selection

```java
// View
MultiSelectListBox<String> listBox = new MultiSelectListBox<>();
listBox.setAriaLabel("Multiple list");
listBox.setItems("Most recent first", "Rating: high to low", "Rating: low to high");
listBox.setValue(Set.of("Most recent first"));
```

```java
// Test
ListBoxElement multi = find(page, ListBoxElement.class).withAccessibleName("Multiple list").single();
multi.assertMultiple();
multi.assertSelectedValue("Most recent first");

multi.selectItem("Rating: high to low");     // adds to the selection
multi.assertSelectedValue("Most recent first", "Rating: high to low");

multi.selectItem("Rating: high to low");     // toggles it off again
multi.assertSelectedValue("Most recent first");

multi.selectItem("Most recent first");
multi.assertSelectedValue();                 // no argument: nothing selected
```

In multiple mode `selectItem` toggles: clicking a selected item deselects it.
`getSelectedValue()` returns the labels of every selected item, and
`isMultiple()` reads the mode without waiting.

## Items rendered by components

With a `ComponentRenderer`, an item's label is the text of everything it renders,
concatenated as the browser reads it:

```java
// View
MultiSelectListBox<DataBean> listBox = new MultiSelectListBox<>();
listBox.setRenderer(new ComponentRenderer<Component, DataBean>(
        data -> new Div(new Span(data.name()), new Span(data.surname()))));
listBox.setAriaLabel("Multiple databean");
listBox.setItems(List.of(new DataBean("name", "surname"), new DataBean("John", "Doe")));
```

```java
// Test
ListBoxElement people = find(page, ListBoxElement.class).withAccessibleName("Multiple databean").single();
people.assertSelectedValue();
people.selectItem("JohnDoe");                // not "John", not "Johndoe"
people.selectItem("namesurname");
people.assertSelectedValue("JohnDoe", "namesurname");
```

## Enabled and disabled items

`assertEnabled()` / `assertDisabled()` read the `disabled` attribute of the list
box; `assertItemEnabled(label)` / `assertItemDisabled(label)` check one item. A
disabled list box disables every item.

```java
// View
ListBox<String> listBox = new ListBox<>();
listBox.setItems("Most recent first", "Rating: high to low", "Always disabled");
listBox.setItemEnabledProvider(item -> !"Always disabled".equals(item));
listBox.setAriaLabel("Enabled/Disabled Field");
listBox.setEnabled(false);
```

```java
// Test
ListBoxElement listBox = find(page, ListBoxElement.class).withAccessibleName("Enabled/Disabled Field").single();
listBox.assertDisabled();
listBox.assertItemDisabled("Most recent first");
listBox.assertItemDisabled("Always disabled");

ButtonElement.getByText(page, "Enable/Disable").click();

listBox.assertEnabled();
listBox.assertItemEnabled("Most recent first");
listBox.assertItemDisabled("Always disabled");   // still off through the item enabled provider
```

`isEnabled()` and `isEnabled(boolean)` read the same attribute without waiting.

## Accessible name, tooltip and style

```java
ListBoxElement sort = find(page, ListBoxElement.class).withAccessibleName("Invisible label").single();
sort.assertAriaLabel("Invisible label");
sort.assertTooltipHasText("Tooltip for listbox");
sort.assertCssClass("list-box");

// The name comes from aria-labelledby: no aria-label on the list box
find(page, ListBoxElement.class).withAccessibleName("Labelled by").single()
        .assertAriaLabel(null);
```

## Pitfalls

- **Single vs multiple behave differently on a second click.** Single mode keeps
  the item selected; multiple mode deselects it.
- **`getSingleSelectedValue()` expects one selected item.** On a multi-select
  list box with several selected items it fails with a strict-mode error; use
  `getSelectedValue()` or `assertSelectedValue(..)`.
- **Item labels match exactly**, case-sensitively, on the item's whole text.
  A component-rendered item's label is its concatenated text (`"JohnDoe"`).
- **The list box host has no form-control role**, so its enabled state is read
  from the `disabled` attribute (see `HasDisabledAttributeElement`), while items
  (role `option`) use Playwright's enabled check.

## See also

- [Select](SelectElement.md), [Combo Box](ComboBoxElement.md),
  [Multi-Select Combo Box](MultiSelectComboBoxElement.md),
  [Checkbox Group](CheckboxGroupElement.md)
- [Finding components](../guide/finding-components.md),
  [Assertions](../guide/assertions.md),
  [Actions and waiting](../guide/actions-and-waiting.md),
  [Core concepts](../guide/core-concepts.md)
- Full example: [ListBoxViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/ListBoxViewIT.java)
