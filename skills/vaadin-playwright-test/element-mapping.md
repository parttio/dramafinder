# DramaFinder Component → Element Class Mapping

Use this table to map Vaadin component class names found in view source to the corresponding DramaFinder element wrapper.

Also scan `src/main/java` for any `*Element.java` files not listed here (custom extensions).

> The `Key Methods` column below is verified against the bundled
> [api-reference.md](api-reference.md) but abbreviated — it lists the most
> common factories and methods, not everything. The auto-generated **Element
> index** at the top of [api-reference.md](api-reference.md) (derived directly
> from source, verified in CI) is the authoritative list of factories, and each
> element's section there is the authoritative list of methods. Never use a
> method that isn't in api-reference.md.

| Vaadin Component | DramaFinder Element Class | Key Methods |
|-----------------|--------------------------|-------------|
| `TextField` | `TextFieldElement` | `getByLabel(page, label)`, `setValue()`, `assertValue()`, `assertValid()`, `assertInvalid()`, `assertErrorMessage()`, `assertLabel()`, `assertPlaceholder()`, `assertHelperHasText()`, `assertMinLength()`, `assertMaxLength()`, `assertPattern()`, `clickClearButton()` |
| `TextArea` | `TextAreaElement` | `getByLabel(page, label)`, `setValue()`, `assertValue()` — extends `TextFieldElement` |
| `EmailField` | `EmailFieldElement` | `getByLabel(page, label)`, `setValue()`, `assertValue()` — extends `TextFieldElement` |
| `PasswordField` | `PasswordFieldElement` | `getByLabel(page, label)`, `setValue()`, `assertValue()` — extends `TextFieldElement` |
| `NumberField` | `NumberFieldElement` | `getByLabel(page, label)`, `setValue()`, `assertValue()`, `assertMin()`, `assertMax()`, `assertStep()`, `clickIncreaseButton()`, `clickDecreaseButton()` |
| `IntegerField` | `IntegerFieldElement` | `getByLabel(page, label)`, `setValue()`, `assertValue()`, `assertMin()`, `assertMax()`, `assertStep()` |
| `BigDecimalField` | `BigDecimalFieldElement` | `getByLabel(page, label)`, `setValue()`, `assertValue()` |
| `Button` | `ButtonElement` | `getByText(page, text)`, `click()`, `assertVisible()`, `assertEnabled()`, `assertDisabled()` |
| `Checkbox` | `CheckboxElement` | `getByLabel(page, label)`, `check()`, `uncheck()`, `assertChecked()`, `assertNotChecked()`, `assertIndeterminate()` |
| `RadioButtonGroup` | `RadioButtonGroupElement` | `getByLabel(page, label)`, `selectByLabel()`, `selectByValue()`, `assertValue()` |
| `ComboBox` | `ComboBoxElement` | `getByLabel(page, label)`, `selectItem()`, `filterAndSelectItem()`, `assertValue()`, `open()`, `assertItemCount()` |
| `MultiSelectComboBox` | `MultiSelectComboBoxElement` | `getByLabel(page, label)`, `selectItem()`, `selectItems()`, `deselectItem()`, `assertSelectedItems()`, `assertSelectedCount()` |
| `Select` | `SelectElement` | `getByLabel(page, label)`, `selectItem()`, `assertValue()` |
| `ListBox` | `ListBoxElement` | `getByLabel(page, label)`, `selectItem()`, `assertSelectedValue()`, `assertItemEnabled()`, `assertItemDisabled()` |
| `DatePicker` | `DatePickerElement` | `getByLabel(page, label)`, `setValue(LocalDate)`, `assertValue(LocalDate)`, `assertValid()`, `assertInvalid()` |
| `TimePicker` | `TimePickerElement` | `getByLabel(page, label)`, `setValue(LocalTime)`, `assertValue(LocalTime)` |
| `DateTimePicker` | `DateTimePickerElement` | `getByLabel(page, label)`, `setValue(LocalDateTime)`, `assertValue(LocalDateTime)`, `setDate()`, `setTime()` |
| `Grid` | `GridElement` | `get(page)`, `getById(page, id)`, `assertRowCount()`, `assertCellContent(row, col, text)`, `assertHeaderCellContents()`, `select(rowIndex)`, `assertRowSelected()`, `scrollToRow()`, `findRow()`, `findCell()`; sorting via `findHeaderCellByText(text)` → `HeaderCellElement.clickSort()` / `assertSortAscending()` |
| `TreeGrid` | `TreeGridElement` | `get(page)`, `expandRow()`, `collapseRow()`, `assertRowCount()`, `assertCellContent()` — extends `GridElement` |
| `VirtualList` | `VirtualListElement` | `get(page)`, `assertRowCount()`, `assertItemRendered()`, `scrollToRow()` |
| `Dialog` | `DialogElement` | `getByHeaderText(page, text)` or `new DialogElement(page)`, `assertOpen()`, `assertClosed()`, `assertHeaderText()`, `closeWithEscape()`, `getContentLocator()` |
| `Notification` | `NotificationElement` | `getByText(page, text)`, `assertOpen()`, `assertClosed()`, `assertContent()` |
| `Tabs` / `Tab` | `TabElement` | `getTabByText(tabsLocator, text)`, `getSelectedTab(tabsLocator)`, `select()`, `assertSelected()` |
| `TabSheet` | `TabSheetElement` | `get(page)`, `selectTab(label)`, `getSelectedTab()`, `assertTabsCount()` |
| `Accordion` | `AccordionElement` | `new AccordionElement(locator)`, `openPanel(summary)`, `closePanel(summary)`, `assertPanelOpened()`, `assertPanelClosed()` |
| `AccordionPanel` | `AccordionPanelElement` | `getAccordionPanelBySummary(locator, summary)`, `assertOpened()`, `assertClosed()` |
| `Details` | `DetailsElement` | `getBySummaryText(page, summary)`, `setOpen(boolean)`, `assertOpened()`, `assertClosed()` |
| `MenuBar` | `MenuBarElement` | `getByLabel(page, label)` or `new MenuBarElement(page)`, `getMenuItemElement(name)`, `openSubMenu(name)` |
| `ContextMenu` | `ContextMenuElement` | `ContextMenuElement.openOn(target)` then `new ContextMenuElement(page)`, `selectItem()`, `assertOpen()`, `assertClosed()` |
| `SplitLayout` | `SplitLayoutElement` | `get(page)`, `assertHorizontal()`, `assertVertical()`, `dragSplitterBy()` |
| `Upload` | `UploadElement` | `getByButtonText(page, text)`, `uploadFiles(Path...)`, `assertHasFile()`, `assertFileComplete()` |
| `ProgressBar` | `ProgressBarElement` | `new ProgressBarElement(locator)`, `assertValue()`, `assertIndeterminate()` |
| `Avatar` | `AvatarElement` | `get(page)`, `getByName(page, name)`, `assertName()`, `assertAbbreviation()` |
| `MessageInput` | `MessageInputElement` | `get(page)`, `typeAndSubmit()`, `submit()`, `assertValue()` |
| `MessageList` | `MessageListElement` | `get(page)`, `assertMessageCount()`, `assertMessageText(index, text)`, `assertMessageUserName(index, name)` |
| `Popover` | `PopoverElement` | `getByLabel(page, label)` or `new PopoverElement(page)`, `assertOpen()`, `assertClosed()` |
| `SideNavigation` | `SideNavigationElement` | `getByLabel(page, label)`, `clickItem(label)`, `getItem(label)`, `assertCollapsed()`, `assertExpanded()` |
| `Card` | `CardElement` | `getByTitle(page, title)`, `assertTitle()`, `assertSubtitle()` |

## Factory method conventions

Factory methods are **not uniform** across elements — do not assume every
element has `get`, `getByLabel`, and `getById`. The **Element index** in
[api-reference.md](api-reference.md) lists the exact factories each element
provides. The common shapes are:

```java
// Fields (TextField, ComboBox, DatePicker, ...) — by aria-label or visible label
TextFieldElement field = TextFieldElement.getByLabel(page, "My Label");

// Containers (Grid, VirtualList, TabSheet, ...) — first matching element on the page
GridElement grid = GridElement.get(page);

// By DOM id — only on a few elements (e.g. GridElement, TreeGridElement)
GridElement grid = GridElement.getById(page, "my-grid-id");

// Some elements have a bespoke factory (check the index), e.g.
ButtonElement save = ButtonElement.getByText(page, "Save");
NotificationElement toast = NotificationElement.getByText(page, "Saved");
DialogElement dialog = DialogElement.getByHeaderText(page, "Confirm");

// A few elements have no static factory at all (AccordionElement,
// ProgressBarElement, SideNavigationItemElement, ...) — construct them
// from a Locator, which is always available on every element:
// (also useful for elements inside dialogs, grid cells, etc.)
TextFieldElement field = new TextFieldElement(dialog.getLocator().locator("vaadin-text-field"));
```

## Common shared assertions (available on most elements)

Provided by `VaadinElement` and the shared mixins — an element only has a
mixin's methods if it implements that mixin (see each element's **Implements**
line in [api-reference.md](api-reference.md)).

```java
element.assertVisible();
element.assertHidden();
element.assertEnabled();
element.assertDisabled();
element.assertCssClass("my-class");
element.assertTheme("small");
element.assertLabel("Expected label");
element.assertHelperHasText("Helper text");
element.assertTooltipHasText("Tooltip text");
element.assertAriaLabel("Aria label");
element.assertIsFocused();
element.assertIsNotFocused();
element.focus();
element.click();
element.getText();
element.getLocator(); // raw Playwright Locator for custom assertions
```
