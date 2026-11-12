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
| `NumberField` | `NumberFieldElement` | `getByLabel(page, label)`, `setValue()` (`String` or `Double`; `null` clears), `getValueAsDouble()`, `assertValue()` (`String` or `Double`), `assertMin()`, `assertMax()`, `assertStep()`, `clickIncreaseButton()`, `clickDecreaseButton()` |
| `IntegerField` | `IntegerFieldElement` | `getByLabel(page, label)`, `setValue()` (`String` or `Integer`; `null` clears), `getValueAsInteger()`, `assertValue()` (`String` or `Integer`), `assertMin()`, `assertMax()`, `assertStep()` |
| `BigDecimalField` | `BigDecimalFieldElement` | `getByLabel(page, label)`, `setValue()`, `assertValue()` |
| `Button` | `ButtonElement` | `getByText(page, text)`, `click()`, `assertVisible()`, `assertEnabled()`, `assertDisabled()` |
| `Checkbox` | `CheckboxElement` | `getByLabel(page, label)`, `check()`, `uncheck()`, `setChecked(boolean)`, `isChecked()`, `assertChecked()`, `assertNotChecked()`, `assertIndeterminate()` |
| `Switch` (experimental, Vaadin 25.3) | `SwitchElement` | `getByLabel(page, label)`, `check()`, `uncheck()`, `setChecked(boolean)`, `isChecked()`, `assertChecked()`, `assertNotChecked()` |
| `CheckboxGroup` | `CheckboxGroupElement` | `getByLabel(page, label)`, `getCheckboxes()`, `getCheckbox(label)`, `selectByLabel()`, `deselectByLabel()`, `deselectAll()`, `getSelectedValues()`, `assertSelected()` |
| `RadioButtonGroup` | `RadioButtonGroupElement` | `getByLabel(page, label)`, `selectByLabel()`, `selectByValue()`, `assertValue()`, `getItemLabels()`, `getSelectedLabel()`, `assertSelectedLabel()` |
| `ComboBox` | `ComboBoxElement` | `getByLabel(page, label)`, `selectItem()`, `filterAndSelectItem()`, `assertValue()`, `clear()` (clear button or empty input), `open()`, `assertItemCount()`, `getItemLabels()` (all items, lazy pages loaded), `getSelectedItemLabel()` |
| `MultiSelectComboBox` | `MultiSelectComboBoxElement` | `getByLabel(page, label)`, `selectItem()`, `selectItems()`, `deselectItem()`, `clear()` (deselects all), `assertSelectedItems()`, `assertSelectedCount()`, `getSelectedItems()`, `getItemLabels()` (all items, lazy pages loaded) |
| `Select` | `SelectElement` | `getByLabel(page, label)`, `selectItem()`, `assertValue()` |
| `ListBox` | `ListBoxElement` | `find(page, ListBoxElement.class).withAccessibleName(name)`, `selectItem()`, `assertSelectedValue()`, `assertItemEnabled()`, `assertItemDisabled()` |
| `DatePicker` | `DatePickerElement` | `getByLabel(page, label)`, `setValue(LocalDate)`, `setValue(String)` (types + Enter), `assertValue(LocalDate)`, `assertValid()`, `assertInvalid()` |
| `TimePicker` | `TimePickerElement` | `getByLabel(page, label)`, `setValue(LocalTime)`, `assertValue(LocalTime)` |
| `DateTimePicker` | `DateTimePickerElement` | `getByLabel(page, label)`, `setValue(LocalDateTime)`, `assertValue(LocalDateTime)`, `typeText(date, time)`, `setDate()`, `setTime()`, `clear()` (both halves), `getDatePicker()`, `getTimePicker()` |
| `Grid` | `GridElement` | `get(page)`, `getById(page, id)`, `assertRowCount()`, `assertCellContent(row, col, text)`, `assertHeaderCellContents()`, `select(rowIndex)`, `assertRowSelected()`, `scrollToRow()`, `findRow()`, `findCell()`; rows: `findRow(index)` → `RowElement.click()`, `doubleClick()`, `openDetails()`; sorting via `findHeaderCellByText(text)` → `HeaderCellElement.sort(GridElement.SortDirection)` / `assertSortDirection()`; header rows (filters) via `getHeaderCells(columnIndex)` |
| `TreeGrid` | `TreeGridElement` | `get(page)`, `expandRow()`, `collapseRow()`, `assertRowCount()`, `assertCellContent()` — extends `GridElement` |
| `VirtualList` | `VirtualListElement` | `get(page)`, `assertRowCount()`, `assertItemRendered()`, `scrollToRow()` |
| `ConfirmDialog` | `ConfirmDialogElement` | `getByHeaderText(page, header)` or `get(page)`, `assertMessage()`, `assertHeaderText()`, `confirm()`, `cancel()`, `reject()`, `closeWithEscape()`, `assertConfirmText()`, `assertCancelable()`, `assertRejectable()`, `assertClosed()` |
| `CustomField` | `CustomFieldElement` | `getByLabel(page, label)`, `getField(TextFieldElement.class, "aria label")` / `getField(SelectElement.class, 0)` to reach the wrapped fields, `assertLabel()`, `assertHelperHasText()`, `assertRequired()`, `assertInvalid()`, `assertErrorMessage()`, `assertDisabled()`, `assertReadOnly()` — no value getter: assert the wrapped fields |
| `Dialog` | `DialogElement` | `getByHeaderText(page, text)` or `new DialogElement(page)`; `find(page, DialogElement.class)` matches open dialogs only; `isOpen()`, `assertOpen()`, `assertClosed()`, `assertHeaderText()`, `closeWithEscape()`, `getContentLocator()` |
| `LoginForm` | `LoginFormElement` | `get(page)` or `get(locator)`, `login(username, password)`, `getUsernameField()`, `getPasswordField()`, `getSubmitButton()`, `getForgotPasswordButton()`, `assertTitle()`, `assertErrorVisible()`, `assertErrorTitle()`, `assertErrorMessage()`, `assertNoError()`, `isDisabled()` |
| `LoginOverlay` | `LoginOverlayElement` | `get(page)`, `getByTitle(page, title)` or `new LoginOverlayElement(page)`, `login(username, password)`, `assertOpen()`, `assertClosed()`, `assertHeaderTitle()`, `assertDescription()`, `assertTitle()`, `assertErrorTitle()`, `assertErrorMessage()` |
| `Notification` | `NotificationElement` | `getByText(page, text)` or `find(page, NotificationElement.class).withText(text)` (exact; combine with `withAttribute("theme", "error")`, `withClassName(..)`), `assertOpen()`, `assertClosed()`, `assertContent()` |
| `Tabs` | `TabsElement` | `get(page)`, `getById(page, id)`, `getTab(label)` (own text; `aria-label` for an icon-only tab), `getTab(index)`, `getTabs()`, `getTabCount()`, `getTabLabels()`, `selectTab(label)`, `selectTab(index)` (fails at once on a disabled tab), `getSelectedTab()`, `getSelectedIndex()`, `assertSelectedTab()`, `assertTabCount()`, `assertTabLabels(..)`, `getOrientation()`, `assertOrientation()` |
| `Tab` | `TabElement` | `getTabByText(tabsLocator, text)`, `getSelectedTab(tabsLocator)`, `select()`, `getLabel()`, `assertSelected()`, `assertNotSelected()`, `isEnabled()`, `assertEnabled()`, `assertDisabled()` |
| `TabSheet` | `TabSheetElement` | `get(page)`, `getTabsElement()`, `getTab(label)`, `selectTab(label)`, `selectTab(index)`, `getSelectedTab()`, `assertSelectedTab(label)`, `assertTabsCount()`, `getTabLabels()`, `assertTabLabels(..)`, `getContentLocator()` |
| `Accordion` | `AccordionElement` | `new AccordionElement(locator)`, `openPanel(summary)`, `closePanel(summary)`, `assertPanelOpened()`, `assertPanelClosed()` |
| `AccordionPanel` | `AccordionPanelElement` | `getAccordionPanelBySummary(locator, summary)`, `assertOpened()`, `assertClosed()` |
| `Details` | `DetailsElement` | `getBySummaryText(page, summary)`, `setOpen(boolean)`, `toggle()`, `assertOpened()`, `assertClosed()`, `getSummaryLocator()`, `getContentLocator()` (open or closed), `assertContentVisible()`, `assertContentNotVisible()` |
| `MenuBar` | `MenuBarElement` | `find(page, MenuBarElement.class).withAccessibleName(name)` or `new MenuBarElement(page)`, `getMenuItemElement(name)` (text, or `aria-label` for icon-only items), `getItemLabels()`, `openSubMenu(name)`, `close()` (waits until the sub-menus are gone) |
| `MenuBar` sub-menu | `MenuElement` | returned by `menuBar.openSubMenu(name)`, scoped to that menu bar and level (`new MenuElement(page)` matches every level), `getMenuItemElement(name)`, `getItemLabels()`, `openSubMenu(name)` for a nested sub-menu |
| `MenuBar` item | `MenuItemElement` | returned by `menuBar.getMenuItemElement(name)` / `menu.getMenuItemElement(name)`, or `find(page, MenuItemElement.class).withText(text)` (buttons and sub-menu items), `click()`, `assertExpanded()`, `assertCollapsed()`, `isEnabled()`, `assertEnabled()`, `assertDisabled()` |
| `ContextMenu` | `ContextMenuElement` | `ContextMenuElement.openOn(target)` then `new ContextMenuElement(page)`, `selectItem()`, `assertOpen()`, `assertClosed()` |
| `SplitLayout` | `SplitLayoutElement` | `get(page)`, `assertHorizontal()`, `assertVertical()`, `dragSplitterBy()` |
| `FormLayout` | `FormLayoutElement` | `get(page)`, `get(locator)`, `getById(page, id)`, `getFields()`, `getField(index)`, `getFieldCount()`, `getFormItems()`, `getColumnCount()`, `getLabelPosition()`, `assertColumnCount(int)`, `assertLabelPosition(String)`, `assertFieldCount(int)` |
| `AppLayout` | `AppLayoutElement` | `get(page)`, `get(locator)`, `getById(page, id)`, `getNavbarLocator()`, `getDrawerLocator()`, `getContentLocator()`, `getDrawerToggle()`, `toggleDrawer()`, `openDrawer()`, `closeDrawer()`, `setDrawerOpened(boolean)`, `isDrawerOpened()`, `assertDrawerOpened()`, `assertDrawerClosed()`, `isOverlayMode()`, `assertOverlayMode()`, `assertPrimarySection()` |
| `Upload` | `UploadElement` | `getByButtonText(page, text)`, `uploadFiles(Path...)`, `uploadFiles(FilePayload...)`, `assertHasFile()`, `assertFileComplete()` |
| `IntegerSlider` / `DecimalSlider` | `SliderElement` | `getByLabel(page, label)`, `getValue()`, `setValue(double)`, `assertValue()`, `assertMin()`, `assertMax()`, `assertStep()`, `increment()`, `decrement()`, `moveToMin()`, `moveToMax()` |
| `ProgressBar` | `ProgressBarElement` | `new ProgressBarElement(locator)`, `assertValue()`, `assertIndeterminate()` |
| `Avatar` | `AvatarElement` | `get(page)`, `getByName(page, name)`, `assertName()`, `assertAbbreviation()` |
| `AvatarGroup` | `AvatarGroupElement` | `get(page)`, `getAvatars()`, `getAvatar(index)`, `getVisibleCount()`, `assertNames()`, `getOverflowAvatar()`, `assertHasOverflow()`, `openOverflow()`, `getOverflowAvatars()`, `assertOverflowNames()` |
| `MessageInput` | `MessageInputElement` | `get(page)`, `typeAndSubmit()`, `submit()`, `assertValue()` |
| `MessageList` | `MessageListElement` | `get(page)`, `assertMessageCount()`, `assertMessageText(index, text)`, `assertMessageUserName(index, name)` |
| `Popover` | `PopoverElement` | `find(page, PopoverElement.class).withAccessibleName(name)` or `new PopoverElement(page)`, `assertOpen()`, `assertClosed()` |
| `Tooltip` | `TooltipElement` | `get(page)`, `get(locator)` scoped to the target component, `getByText(page, text)`, or `someElement.getTooltip()`; `getText()`, `assertText()`, `isOpened()`, `assertOpened()`, `assertClosed()`, `getPosition()`, `assertPosition()`, `hoverTarget()`, `closeWithEscape()` |
| `SideNavigation` | `SideNavigationElement` | `getByLabel(page, label)`, `clickItem(label)`, `getItem(label)`, `assertCollapsed()`, `assertExpanded()` |
| `SideNavItem` | `SideNavigationItemElement` | returned by `sideNav.getItem(label)`, or `getByLabel(page, label)`; `navigate()`, `toggle()`, `isExpanded()`, `assertExpanded()`, `assertCollapsed()`, `assertCurrent()`, `assertNotCurrent()` |
| `Card` | `CardElement` | `getByTitle(page, title)`, `assertTitle()`, `assertSubtitle()` |
| `Markdown` | `MarkdownElement` | `get(page)`, `getRenderedLocator()`, `getText()`, `assertContainsText()`, `getHeadings()`, `getLinks()`, `getCodeBlocks()`, `assertHeading(index, text)`, `assertLink(text, href)`, `assertCodeBlockLanguage(index, language)` |
| `Badge` | `BadgeElement` | `get(page)`, `getByText(page, text)`, `getText()`, `assertText()`, `getNumber()`, `assertNumber()`, `assertHasIcon()`, `assertHasThemeVariant()` |
| `Breadcrumbs` | `BreadcrumbsElement` | `get(page)`, `find(page, BreadcrumbsElement.class).withAccessibleName(name)`, `getItems()`, `getItem(text)`, `getCurrentItem()`, `assertItemTexts()`, `assertItemCount()`, `hasOverflow()`, `openOverflow()`, `getOverflowItems()` |
| `BreadcrumbsItem` | `BreadcrumbsItemElement` | `getByText(page, text)`, `getText()`, `getPath()`, `isLink()`, `click()`, `isCurrent()`, `assertCurrent()`, `assertHasPrefix()`, `assertDisabled()` |

The abstract bases `AbstractNumberFieldElement` (of `IntegerFieldElement` and
`NumberFieldElement`) and `AbstractLoginElement` (of `LoginFormElement` and
`LoginOverlayElement`) hold their shared methods; use the concrete classes.

## Factory method conventions

Factory methods are **not uniform** across elements — do not assume every
element has `get`, `getByLabel`, and `getById`. The **Element index** in
[api-reference.md](api-reference.md) lists the exact factories each element
provides. The common shapes are:

```java
// Fields (TextField, ComboBox, DatePicker, ...) — by their visible label
TextFieldElement field = TextFieldElement.getByLabel(page, "My Label");
// …named only by an aria-label, or known only by a placeholder: the query builder
TextFieldElement search = find(page, TextFieldElement.class).withAccessibleName("Search").single();
TextFieldElement street = find(page, TextFieldElement.class).withPlaceholder("Street").single();

// Containers (Grid, VirtualList, TabSheet, ...) — first matching element on the page
GridElement grid = GridElement.get(page);

// By DOM id — only on a few elements (e.g. GridElement, TreeGridElement)
GridElement grid = GridElement.getById(page, "my-grid-id");

// Some elements have a bespoke factory (check the index), e.g.
ButtonElement save = ButtonElement.getByText(page, "Save");
NotificationElement toast = NotificationElement.getByText(page, "Saved");
DialogElement dialog = DialogElement.getByHeaderText(page, "Edit person");
ConfirmDialogElement confirm = ConfirmDialogElement.getByHeaderText(page, "Unsaved changes");

// A few elements have no static factory at all (AccordionElement,
// ProgressBarElement, ...) — construct them
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
element.getTooltip(); // TooltipElement: opened state, position, hoverTarget()
element.assertAriaLabel("Aria label");
element.assertIsFocused();
element.assertIsNotFocused();
element.focus();
element.click();
element.getText();
element.getLocator(); // raw Playwright Locator for custom assertions
```
