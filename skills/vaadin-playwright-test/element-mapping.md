# DramaFinder Component → Element Class Mapping

Use this table to map Vaadin component class names found in view source to the corresponding DramaFinder element wrapper.

Also scan `src/main/java` for any `*Element.java` files not listed here (custom extensions).

> **The `Key Methods` column is illustrative, not authoritative.** Factory
> methods are **not** uniform across elements — the factory name shown here may
> be out of date. For the exact, always-current factory signatures of each
> element, use the auto-generated **Element index** at the top of
> [api-reference.md](api-reference.md) (derived directly from source, verified in
> CI). Never assume a factory that isn't listed there.

| Vaadin Component | DramaFinder Element Class | Key Methods |
|-----------------|--------------------------|-------------|
| `TextField` | `TextFieldElement` | `getByLabel(page, label)`, `setValue()`, `assertValue()`, `assertValid()`, `assertInvalid()`, `assertErrorMessage()`, `assertLabel()`, `assertPlaceholder()`, `assertHelperHasText()`, `assertPrefixHasText()`, `assertSuffixHasText()`, `assertClearButtonVisible()`, `clickClearButton()`, `assertTheme()`, `assertAllowedCharPattern()`, `assertMinLength()`, `assertMaxLength()`, `assertPattern()`, `assertTooltipHasText()`, `assertAriaLabel()`, `assertIsFocused()`, `assertEnabled()`, `assertDisabled()` |
| `TextArea` | `TextAreaElement` | `getByLabel(page, label)`, `setValue()`, `assertValue()`, `assertValid()`, `assertInvalid()` |
| `EmailField` | `EmailFieldElement` | `getByLabel(page, label)`, `setValue()`, `assertValue()`, `assertValid()`, `assertInvalid()` |
| `PasswordField` | `PasswordFieldElement` | `getByLabel(page, label)`, `setValue()`, `assertValue()` |
| `NumberField` | `NumberFieldElement` | `getByLabel(page, label)`, `setValue()`, `assertValue()` |
| `IntegerField` | `IntegerFieldElement` | `getByLabel(page, label)`, `setValue()`, `assertValue()` |
| `BigDecimalField` | `BigDecimalFieldElement` | `getByLabel(page, label)`, `setValue()`, `assertValue()` |
| `Button` | `ButtonElement` | `getByText(page, text)`, `get(page)`, `click()`, `assertVisible()`, `assertEnabled()`, `assertDisabled()` |
| `Checkbox` | `CheckboxElement` | `getByLabel(page, label)`, `check()`, `uncheck()`, `assertChecked()`, `assertUnchecked()` |
| `RadioButtonGroup` | `RadioButtonGroupElement` | `getByLabel(page, label)`, `selectByText()`, `assertSelectedValue()` |
| `ComboBox` | `ComboBoxElement` | `getByLabel(page, label)`, `selectByText()`, `assertValue()`, `openDropdown()` |
| `MultiSelectComboBox` | `MultiSelectComboBoxElement` | `getByLabel(page, label)`, `selectByText()`, `assertSelectedValues()` |
| `Select` | `SelectElement` | `getByLabel(page, label)`, `selectByText()`, `assertValue()` |
| `ListBox` | `ListBoxElement` | `get(page)`, `selectByText()`, `assertSelectedValue()` |
| `DatePicker` | `DatePickerElement` | `getByLabel(page, label)`, `setValue()`, `assertValue()`, `assertValid()`, `assertInvalid()` |
| `TimePicker` | `TimePickerElement` | `getByLabel(page, label)`, `setValue()`, `assertValue()` |
| `DateTimePicker` | `DateTimePickerElement` | `getByLabel(page, label)`, `setValue()`, `assertValue()` |
| `Grid` | `GridElement` | `get(page)`, `assertRowCount()`, `assertCellContent(row, col, text)`, `clickRow()`, `sortByColumn()`, `selectRow()`, `assertRowSelected()` |
| `TreeGrid` | `TreeGridElement` | `get(page)`, `expandRow()`, `collapseRow()`, `assertRowCount()`, `assertCellContent()` |
| `VirtualList` | `VirtualListElement` | `get(page)`, `assertItemCount()` |
| `Dialog` | `DialogElement` | `get(page)`, `assertOpen()`, `assertClosed()`, `getLocator()` |
| `Notification` | `NotificationElement` | `get(page)`, `assertVisible()`, `assertText()` |
| `Tabs` / `Tab` | `TabElement` | `getByLabel(page, label)`, `click()`, `assertSelected()` |
| `TabSheet` | `TabSheetElement` | `get(page)`, `selectTabByLabel()`, `assertTabSelected()` |
| `Accordion` | `AccordionElement` | `get(page)`, `openPanel()`, `closePanel()`, `assertPanelOpen()` |
| `AccordionPanel` | `AccordionPanelElement` | `get(page)`, `assertOpen()`, `assertClosed()` |
| `Details` | `DetailsElement` | `get(page)`, `open()`, `close()`, `assertOpen()`, `assertClosed()` |
| `MenuBar` | `MenuBarElement` | `get(page)`, `clickItem()` |
| `ContextMenu` | `ContextMenuElement` | `get(page)`, `open()`, `clickItem()` |
| `SplitLayout` | `SplitLayoutElement` | `get(page)`, `assertOrientation()` |
| `Upload` | `UploadElement` | `get(page)`, `uploadFile()`, `assertFileUploaded()` |
| `ProgressBar` | `ProgressBarElement` | `get(page)`, `assertValue()`, `assertIndeterminate()` |
| `Avatar` | `AvatarElement` | `get(page)`, `assertName()`, `assertAbbreviation()` |
| `MessageInput` | `MessageInputElement` | `get(page)`, `sendMessage()` |
| `MessageList` | `MessageListElement` | `get(page)`, `assertMessageCount()`, `assertMessageText()` |
| `Popover` | `PopoverElement` | `get(page)`, `assertOpen()`, `assertClosed()` |
| `SideNavigation` | `SideNavigationElement` | `get(page)`, `clickItem()`, `assertItemSelected()` |
| `Card` | `CardElement` | `get(page)`, `assertVisible()`, `getText()` |

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

// From a Locator — always available via the constructor
// (useful for elements inside dialogs, grid cells, etc.)
TextFieldElement field = new TextFieldElement(dialog.getLocator().locator("vaadin-text-field"));
```

## Common shared assertions (available on most elements)

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
