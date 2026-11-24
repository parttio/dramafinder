# Components

One page per Vaadin component: how to find it, act on it and assert its state,
with the view code next to the test code. Every element also has the methods of
the [VaadinElement base class](VaadinElement.md); the
[API reference](../../skills/vaadin-playwright-test/api-reference.md) lists every
signature.

## Text input

| Component | Element | Typical lookup |
|---|---|---|
| [Text Field](TextFieldElement.md) | `TextFieldElement` | `find(page, TextFieldElement.class).withLabel("Name").single()` |
| [Email Field](EmailFieldElement.md) | `EmailFieldElement` | `find(page, EmailFieldElement.class).withLabel("Email").single()` |
| [Password Field](PasswordFieldElement.md) | `PasswordFieldElement` | `find(page, PasswordFieldElement.class).withLabel("Password").single()` |
| [Text Area](TextAreaElement.md) | `TextAreaElement` | `find(page, TextAreaElement.class).withLabel("Comment").single()` |
| [Number Field](NumberFieldElement.md) | `NumberFieldElement` | `find(page, NumberFieldElement.class).withLabel("Price").single()` |
| [Integer Field](IntegerFieldElement.md) | `IntegerFieldElement` | `find(page, IntegerFieldElement.class).withLabel("Quantity").single()` |
| [Big Decimal Field](BigDecimalFieldElement.md) | `BigDecimalFieldElement` | `find(page, BigDecimalFieldElement.class).withLabel("Amount").single()` |
| [Custom Field](CustomFieldElement.md) | `CustomFieldElement` | `find(page, CustomFieldElement.class).withLabel("Phone").single()` |

## Date and time

| Component | Element | Typical lookup |
|---|---|---|
| [Date Picker](DatePickerElement.md) | `DatePickerElement` | `find(page, DatePickerElement.class).withLabel("Birthday").single()` |
| [Time Picker](TimePickerElement.md) | `TimePickerElement` | `find(page, TimePickerElement.class).withLabel("Alarm").single()` |
| [Date Time Picker](DateTimePickerElement.md) | `DateTimePickerElement` | `find(page, DateTimePickerElement.class).withLabel("Meeting").single()` |

## Selection

| Component | Element | Typical lookup |
|---|---|---|
| [Combo Box](ComboBoxElement.md) | `ComboBoxElement` | `find(page, ComboBoxElement.class).withLabel("Country").single()` |
| [Multi-Select Combo Box](MultiSelectComboBoxElement.md) | `MultiSelectComboBoxElement` | `find(page, MultiSelectComboBoxElement.class).withLabel("Tags").single()` |
| [Select](SelectElement.md) | `SelectElement` | `find(page, SelectElement.class).withLabel("Size").single()` |
| [List Box](ListBoxElement.md) | `ListBoxElement` | `find(page, ListBoxElement.class).withAccessibleName("Fruits").single()` |
| [Checkbox](CheckboxElement.md) | `CheckboxElement` | `find(page, CheckboxElement.class).withLabel("I agree").single()` |
| [Checkbox Group](CheckboxGroupElement.md) | `CheckboxGroupElement` | `find(page, CheckboxGroupElement.class).withLabel("Toppings").single()` |
| [Radio Button Group](RadioButtonGroupElement.md) | `RadioButtonGroupElement` | `find(page, RadioButtonGroupElement.class).withLabel("Plan").single()` |
| [Switch](SwitchElement.md) | `SwitchElement` | `find(page, SwitchElement.class).withLabel("Notifications").single()` |
| [Slider](SliderElement.md) | `SliderElement` | `find(page, SliderElement.class).withLabel("Volume").single()` |
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
| [Side Navigation](SideNavigationElement.md) | `SideNavigationElement`, `SideNavigationItemElement` | `find(page, SideNavigationElement.class).withLabel("Menu").single()` |
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
