# Components

One page per Vaadin component: how to find it, act on it and assert its state,
with the view code next to the test code. Every element also has the methods of
the [VaadinElement base class](VaadinElement.md); the
[API reference](../../skills/vaadin-playwright-test/api-reference.md) lists every
signature.

## Text input

| Component | Element | Typical lookup |
|---|---|---|
| [Text Field](TextFieldElement.md) | `TextFieldElement` | `TextFieldElement.getByLabel(page, "Name")` |
| [Email Field](EmailFieldElement.md) | `EmailFieldElement` | `EmailFieldElement.getByLabel(page, "Email")` |
| [Password Field](PasswordFieldElement.md) | `PasswordFieldElement` | `PasswordFieldElement.getByLabel(page, "Password")` |
| [Text Area](TextAreaElement.md) | `TextAreaElement` | `TextAreaElement.getByLabel(page, "Comment")` |
| [Number Field](NumberFieldElement.md) | `NumberFieldElement` | `NumberFieldElement.getByLabel(page, "Price")` |
| [Integer Field](IntegerFieldElement.md) | `IntegerFieldElement` | `IntegerFieldElement.getByLabel(page, "Quantity")` |
| [Big Decimal Field](BigDecimalFieldElement.md) | `BigDecimalFieldElement` | `BigDecimalFieldElement.getByLabel(page, "Amount")` |
| [Custom Field](CustomFieldElement.md) | `CustomFieldElement` | `CustomFieldElement.getByLabel(page, "Phone")` |

## Date and time

| Component | Element | Typical lookup |
|---|---|---|
| [Date Picker](DatePickerElement.md) | `DatePickerElement` | `DatePickerElement.getByLabel(page, "Birthday")` |
| [Time Picker](TimePickerElement.md) | `TimePickerElement` | `TimePickerElement.getByLabel(page, "Alarm")` |
| [Date Time Picker](DateTimePickerElement.md) | `DateTimePickerElement` | `DateTimePickerElement.getByLabel(page, "Meeting")` |

## Selection

| Component | Element | Typical lookup |
|---|---|---|
| [Combo Box](ComboBoxElement.md) | `ComboBoxElement` | `ComboBoxElement.getByLabel(page, "Country")` |
| [Multi-Select Combo Box](MultiSelectComboBoxElement.md) | `MultiSelectComboBoxElement` | `MultiSelectComboBoxElement.getByLabel(page, "Tags")` |
| [Select](SelectElement.md) | `SelectElement` | `SelectElement.getByLabel(page, "Size")` |
| [List Box](ListBoxElement.md) | `ListBoxElement` | `find(page, ListBoxElement.class).withAccessibleName("Fruits").single()` |
| [Checkbox](CheckboxElement.md) | `CheckboxElement` | `CheckboxElement.getByLabel(page, "I agree")` |
| [Checkbox Group](CheckboxGroupElement.md) | `CheckboxGroupElement` | `CheckboxGroupElement.getByLabel(page, "Toppings")` |
| [Radio Button Group](RadioButtonGroupElement.md) | `RadioButtonGroupElement` | `RadioButtonGroupElement.getByLabel(page, "Plan")` |
| [Switch](SwitchElement.md) | `SwitchElement` | `SwitchElement.getByLabel(page, "Notifications")` |
| [Slider](SliderElement.md) | `SliderElement` | `SliderElement.getByLabel(page, "Volume")` |
| [Upload](UploadElement.md) | `UploadElement` | `UploadElement.getByButtonText(page, "Upload file...")` |

## Actions and menus

| Component | Element | Typical lookup |
|---|---|---|
| [Button](ButtonElement.md) | `ButtonElement` | `ButtonElement.getByText(page, "Save")` |
| [Menu Bar](MenuBarElement.md) | `MenuBarElement`, `MenuElement`, `MenuItemElement` | `MenuBarElement.getById(page, "main-menu")` |
| [Context Menu](ContextMenuElement.md) | `ContextMenuElement` | `new ContextMenuElement(page)` |

## Overlays and feedback

| Component | Element | Typical lookup |
|---|---|---|
| [Dialog](DialogElement.md) | `DialogElement` | `DialogElement.getByHeaderText(page, "Edit person")` |
| [Confirm Dialog](ConfirmDialogElement.md) | `ConfirmDialogElement` | `ConfirmDialogElement.getByHeaderText(page, "Delete?")` |
| [Notification](NotificationElement.md) | `NotificationElement` | `NotificationElement.getByText(page, "Saved")` |
| [Popover](PopoverElement.md) | `PopoverElement` | `find(page, PopoverElement.class).withAccessibleName("Details").single()` |
| [Tooltip](TooltipElement.md) | `TooltipElement` | `field.getTooltip()` |
| [Progress Bar](ProgressBarElement.md) | `ProgressBarElement` | `ProgressBarElement.getById(page, "progress")` |
| [Login](LoginElement.md) | `LoginFormElement`, `LoginOverlayElement` | `LoginFormElement.get(page)` |

## Layout

| Component | Element | Typical lookup |
|---|---|---|
| [App Layout](AppLayoutElement.md) | `AppLayoutElement` | `AppLayoutElement.get(page)` |
| [Form Layout](FormLayoutElement.md) | `FormLayoutElement` | `FormLayoutElement.get(page)` |
| [Split Layout](SplitLayoutElement.md) | `SplitLayoutElement` | `SplitLayoutElement.get(page)` |
| [Accordion](AccordionElement.md) | `AccordionElement`, `AccordionPanelElement` | `AccordionElement.getById(page, "faq")` |
| [Details](DetailsElement.md) | `DetailsElement` | `DetailsElement.getBySummaryText(page, "Advanced")` |
| [Card](CardElement.md) | `CardElement` | `CardElement.getByTitle(page, "Revenue")` |

## Navigation

| Component | Element | Typical lookup |
|---|---|---|
| [Tabs](TabsElement.md) | `TabsElement`, `TabElement` | `TabsElement.get(page)` |
| [Tab Sheet](TabSheetElement.md) | `TabSheetElement` | `TabSheetElement.get(page)` |
| [Side Navigation](SideNavigationElement.md) | `SideNavigationElement`, `SideNavigationItemElement` | `SideNavigationElement.getByLabel(page, "Menu")` |
| [Breadcrumbs](BreadcrumbsElement.md) | `BreadcrumbsElement`, `BreadcrumbsItemElement` | `BreadcrumbsElement.get(page)` |

## Data

| Component | Element | Typical lookup |
|---|---|---|
| [Grid](GridElement.md) | `GridElement` | `GridElement.get(page)` |
| [Tree Grid](TreeGridElement.md) | `TreeGridElement` | `TreeGridElement.get(page)` |
| [Virtual List](VirtualListElement.md) | `VirtualListElement` | `VirtualListElement.get(page)` |

## Display

| Component | Element | Typical lookup |
|---|---|---|
| [Avatar](AvatarElement.md) | `AvatarElement` | `AvatarElement.getByName(page, "Ada Lovelace")` |
| [Avatar Group](AvatarGroupElement.md) | `AvatarGroupElement` | `AvatarGroupElement.get(page)` |
| [Badge](BadgeElement.md) | `BadgeElement` | `BadgeElement.getByText(page, "Completed")` |
| [Markdown](MarkdownElement.md) | `MarkdownElement` | `MarkdownElement.get(page)` |
| [Message List](MessageListElement.md) | `MessageListElement` | `MessageListElement.get(page)` |
| [Message Input](MessageInputElement.md) | `MessageInputElement` | `MessageInputElement.get(page)` |

## Not listed?

A component without a page has no wrapper yet. Use a plain Playwright locator,
or [write your own element](../guide/extending.md).
