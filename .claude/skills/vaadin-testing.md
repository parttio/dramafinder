---
name: vaadin-testing
description: Write Playwright integration tests for Vaadin applications using the DramaFinder library. Use when creating *IT.java tests, testing Vaadin web components, or debugging UI test failures.
allowed-tools: Read, Grep, Glob, Bash(mvn *)
---

# Vaadin Testing with DramaFinder

Use this skill when writing Playwright integration tests for Vaadin applications using the DramaFinder library.

## When to Use

- Writing `*IT.java` integration tests for Vaadin views
- Testing Vaadin web components (text fields, buttons, dialogs, etc.)
- Debugging flaky or failing Vaadin UI tests
- Converting manual test cases to automated Playwright tests

## Core Principles

1. **Accessibility-first lookups** - Always locate elements by accessible names (label, text, ARIA) rather than CSS selectors or tag names
2. **Auto-retry assertions** - Use `assertX()` methods that auto-retry, not raw `getX()` checks
3. **Wait for Vaadin** - Tests should extend `AbstractBasePlaywrightIT` which handles Vaadin loading
4. **Component-specific APIs** - Use the typed element wrappers, not generic Playwright locators

## Quick Start Template

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MyViewIT extends AbstractBasePlaywrightIT {

    @LocalServerPort
    private int port;

    @Override
    public String getUrl() {
        return String.format("http://localhost:%d/my-view", port);
    }

    @Test
    public void testUserFlow() {
        // Find elements by accessible label
        TextFieldElement name = TextFieldElement.getByLabel(page, "Name");
        ButtonElement submit = ButtonElement.getByText(page, "Submit");

        // Interact
        name.setValue("John Doe");
        submit.click();

        // Assert with auto-retry
        NotificationElement.getFirst(page).assertHasText("Saved");
    }
}
```

## Element Lookup Patterns

### By Label (Most Common)
```java
TextFieldElement.getByLabel(page, "Email")
SelectElement.getByLabel(page, "Country")
CheckboxElement.getByLabel(page, "Accept Terms")
DatePickerElement.getByLabel(page, "Birth Date")
```

### By Text Content
```java
ButtonElement.getByText(page, "Save")
ButtonElement.getByText(page, "Cancel")
TabElement.getByText(page, "Details")
```

### By Specific Attributes
```java
DialogElement.getByHeaderText(page, "Confirm Delete")
NotificationElement.getFirst(page)
CardElement.getByTitle(page, "Product Info")
```

### Scoped Lookup (Within Container)
```java
Locator form = page.locator("#registration-form");
TextFieldElement.getByLabel(form, "Email")
ButtonElement.getByText(form, "Submit")
```

## ARIA Roles by Component

| Component | ARIA Role | Example |
|-----------|-----------|---------|
| TextField, TextArea, EmailField, PasswordField | `TEXTBOX` | `getByRole(TEXTBOX).setName("Email")` |
| NumberField, IntegerField | `SPINBUTTON` | `getByRole(SPINBUTTON).setName("Quantity")` |
| DatePicker, TimePicker, Select | `COMBOBOX` | `getByRole(COMBOBOX).setName("Date")` |
| Button | `BUTTON` | `getByRole(BUTTON).setName("Save")` |
| Checkbox | `CHECKBOX` | `getByRole(CHECKBOX).setName("Remember")` |
| RadioButton | `RADIO` | `getByRole(RADIO).setName("Option A")` |
| Dialog | `DIALOG` | `getByRole(DIALOG)` |
| ListBox | `LISTBOX` | `getByRole(LISTBOX)` |
| Menu, ContextMenu | `MENU` | `getByRole(MENU)` |
| MenuItem | `MENUITEM` | `getByRole(MENUITEM).setName("Edit")` |

## Common Assertions

### Value Assertions
```java
field.assertValue("expected value");
field.assertEmpty();
select.assertSelectedLabel("Option A");
checkbox.assertChecked();
checkbox.assertNotChecked();
```

### State Assertions
```java
element.assertVisible();
element.assertHidden();
element.assertEnabled();
element.assertDisabled();
element.assertRequired();
field.assertInvalid();
field.assertValid();
```

### Validation Assertions
```java
field.assertErrorMessage("Email is required");
field.assertHelperText("Enter your work email");
field.assertMinLength(5);
field.assertMaxLength(100);
field.assertPattern("[a-z]+@[a-z]+\\.[a-z]+");
```

### Overlay Assertions
```java
dialog.assertOpen();
dialog.assertClosed();
notification.assertHasText("Success");
popover.assertOpen();
```

## Input Field Operations

### Text Input
```java
// Set value (clears first, triggers change events)
field.setValue("new value");

// Get current value
String value = field.getValue();

// Clear the field
field.clickClearButton();  // If clear button enabled
field.setValue("");        // Alternative

// Focus operations
field.focus();
field.blur();
```

### Selection Components
```java
// Select dropdown
select.selectByLabel("Option A");
select.assertSelectedLabel("Option A");

// Checkbox
checkbox.setChecked(true);
checkbox.setChecked(false);
checkbox.assertChecked();

// Radio group
radioGroup.selectByLabel("Option B");
radioGroup.assertSelectedLabel("Option B");

// List box
listBox.selectByLabel("Item 1");
```

### Date/Time Pickers
```java
// Date picker
datePicker.setDate(LocalDate.of(2024, 1, 15));
datePicker.assertDate(LocalDate.of(2024, 1, 15));

// Time picker
timePicker.setTime(LocalTime.of(14, 30));

// DateTime picker
dateTimePicker.setDateTime(LocalDateTime.of(2024, 1, 15, 14, 30));
```

## Overlay Components

### Dialog
```java
DialogElement dialog = DialogElement.getByHeaderText(page, "Confirm");
dialog.assertOpen();

// Find elements within dialog
ButtonElement confirm = ButtonElement.getByText(dialog.getLocator(), "Confirm");
confirm.click();

dialog.assertClosed();
```

### Notification
```java
NotificationElement notification = NotificationElement.getFirst(page);
notification.assertHasText("Saved successfully");
notification.close();  // If closeable
```

### Context Menu
```java
// Right-click to open
element.getLocator().click(new Locator.ClickOptions().setButton(MouseButton.RIGHT));

// Find and click menu item
ContextMenuElement menu = ContextMenuElement.getOpened(page);
menu.clickItem("Delete");
```

### Menu Bar
```java
MenuBarElement menuBar = MenuBarElement.getFirst(page);
menuBar.openMenu("File");
menuBar.clickItem("Save As...");
```

## Layout Components

### Accordion
```java
AccordionElement accordion = AccordionElement.getFirst(page);
accordion.openPanel("Details");
accordion.closePanel("Details");
accordion.assertPanelOpen("Details");
```

### Tab Sheet
```java
TabSheetElement tabs = TabSheetElement.getFirst(page);
tabs.selectTab("Settings");
tabs.assertSelectedTab("Settings");
```

### Details (Collapsible)
```java
DetailsElement details = DetailsElement.getBySummary(page, "Advanced Options");
details.open();
details.assertOpen();
details.close();
```

## Common Pitfalls

### 1. Not Waiting for Vaadin
```java
// BAD: May execute before Vaadin loads
page.navigate(url);
TextFieldElement.getByLabel(page, "Name").setValue("John");

// GOOD: AbstractBasePlaywrightIT handles this automatically
// Just extend the base class and use getUrl()
```

### 2. Using Raw getAttribute Without Retry
```java
// BAD: No retry, may flake
assertTrue(field.getLocator().getAttribute("invalid") != null);

// GOOD: Auto-retries until timeout
field.assertInvalid();
```

### 3. Wrong Locator for Operation
```java
// BAD: Checking enabled on component root
getLocator().getAttribute("disabled");

// GOOD: Use the proper locator via element API
field.assertEnabled();  // Uses getEnabledLocator() internally
```

### 4. Overly Broad Locators
```java
// BAD: May match multiple buttons
page.locator("vaadin-button").click();

// GOOD: Specific by accessible name
ButtonElement.getByText(page, "Save").click();
```

### 5. Not Piercing Shadow DOM
```java
// BAD: XPath doesn't pierce shadow DOM
page.locator("//vaadin-text-field//input");

// GOOD: CSS selectors auto-pierce in Playwright
page.locator("vaadin-text-field input");
// Or better: use the element wrapper
TextFieldElement.getByLabel(page, "Email").getInputLocator();
```

## Build & Run Commands

```bash
# Run all integration tests
./mvnw -Pit verify

# Run specific test class
./mvnw -Dit.test=MyViewIT -Pit verify

# Run with visible browser (for debugging)
./mvnw -Pdebug-ui -Dit.test=MyViewIT verify
# or
./mvnw -Dit.test=MyViewIT -Dheadless=false verify

# Run all tests including unit tests
./mvnw clean install
```

## Dependency

Add to your `pom.xml`:
```xml
<dependency>
    <groupId>org.vaadin.addons</groupId>
    <artifactId>dramafinder</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
```

## Element Reference

| Element Class | Vaadin Component | Primary Lookup |
|---------------|------------------|----------------|
| `TextFieldElement` | `vaadin-text-field` | `getByLabel(page, "Label")` |
| `TextAreaElement` | `vaadin-text-area` | `getByLabel(page, "Label")` |
| `EmailFieldElement` | `vaadin-email-field` | `getByLabel(page, "Label")` |
| `PasswordFieldElement` | `vaadin-password-field` | `getByLabel(page, "Label")` |
| `NumberFieldElement` | `vaadin-number-field` | `getByLabel(page, "Label")` |
| `IntegerFieldElement` | `vaadin-integer-field` | `getByLabel(page, "Label")` |
| `BigDecimalFieldElement` | `vaadin-big-decimal-field` | `getByLabel(page, "Label")` |
| `DatePickerElement` | `vaadin-date-picker` | `getByLabel(page, "Label")` |
| `TimePickerElement` | `vaadin-time-picker` | `getByLabel(page, "Label")` |
| `DateTimePickerElement` | `vaadin-date-time-picker` | `getByLabel(page, "Label")` |
| `CheckboxElement` | `vaadin-checkbox` | `getByLabel(page, "Label")` |
| `RadioButtonGroupElement` | `vaadin-radio-group` | `getByLabel(page, "Label")` |
| `SelectElement` | `vaadin-select` | `getByLabel(page, "Label")` |
| `ListBoxElement` | `vaadin-list-box` | `getFirst(page)` |
| `ButtonElement` | `vaadin-button` | `getByText(page, "Text")` |
| `DialogElement` | `vaadin-dialog` | `getByHeaderText(page, "Header")` |
| `NotificationElement` | `vaadin-notification` | `getFirst(page)` |
| `PopoverElement` | `vaadin-popover` | `getFirst(page)` |
| `AccordionElement` | `vaadin-accordion` | `getFirst(page)` |
| `DetailsElement` | `vaadin-details` | `getBySummary(page, "Summary")` |
| `TabSheetElement` | `vaadin-tabsheet` | `getFirst(page)` |
| `CardElement` | `vaadin-card` | `getByTitle(page, "Title")` |
| `MenuBarElement` | `vaadin-menu-bar` | `getFirst(page)` |
| `ContextMenuElement` | `vaadin-context-menu` | `getOpened(page)` |
| `SideNavigationElement` | `vaadin-side-nav` | `getFirst(page)` |
| `UploadElement` | `vaadin-upload` | `getFirst(page)` |
| `ProgressBarElement` | `vaadin-progress-bar` | `getFirst(page)` |

## More Information

- [DramaFinder AGENTS.md](AGENTS.md) - Coding conventions and patterns
- [DramaFinder TESTING.md](TESTING.md) - Testing guidelines
- [Element Specifications](docs/specifications/) - Detailed API for each element
