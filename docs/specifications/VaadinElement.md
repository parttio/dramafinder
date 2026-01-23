# VaadinElement Specification

## Overview

`VaadinElement` is the abstract base class for all typed Playwright wrappers around Vaadin components. It exposes common helpers such as clicking, visibility assertions, text retrieval and generic DOM property access. Concrete components add component-specific APIs on top of this.

## Class Hierarchy

```
VaadinElement (abstract)
    ├── AbstractNumberFieldElement
    ├── AccordionElement
    ├── AccordionPanelElement
    ├── BigDecimalFieldElement
    ├── ButtonElement
    ├── CardElement
    ├── CheckboxElement
    ├── ContextMenuElement
    ├── DatePickerElement
    ├── DateTimePickerElement
    ├── DetailsElement
    ├── DialogElement
    ├── ListBoxElement
    ├── MenuBarElement
    ├── MenuElement
    ├── MenuItemElement
    ├── NotificationElement
    ├── PopoverElement
    ├── ProgressBarElement
    ├── RadioButtonElement
    ├── RadioButtonGroupElement
    ├── SelectElement
    ├── SideNavigationElement
    ├── SideNavigationItemElement
    ├── TabElement
    ├── TabSheetElement
    ├── TextFieldElement
    ├── TimePickerElement
    └── UploadElement
```

## Implemented Interfaces

| Interface | Description |
|-----------|-------------|
| `HasLocatorElement` | Provides access to underlying Playwright locator |

## API Methods

### Constructor

```java
VaadinElement(Locator locator)
```

Creates a new VaadinElement wrapper from a Playwright locator.

### Basic Actions

| Method | Description |
|--------|-------------|
| `click()` | Click the component root |
| `getText()` | Get textual content of component |

### Locator Access

| Method | Description |
|--------|-------------|
| `getLocator()` | Get the underlying Playwright Locator |

### Property Access

| Method | Description |
|--------|-------------|
| `setProperty(String name, Object value)` | Set a DOM property |
| `getProperty(String name)` | Get a DOM property value |

### Visibility

| Method | Description |
|--------|-------------|
| `isVisible()` | Whether component is visible |
| `isHidden()` | Whether component is hidden |
| `assertVisible()` | Assert component is visible |
| `assertHidden()` | Assert component is hidden |

## Usage Examples

### Basic Usage

```java
// All element classes extend VaadinElement
ButtonElement button = ButtonElement.getByText(page, "Save");

// Common methods available
button.click();
String text = button.getText();
```

### Visibility Checks

```java
DialogElement dialog = new DialogElement(page);

// Check visibility
if (dialog.isVisible()) {
    dialog.click();
}

// Assert visibility
dialog.assertVisible();
dialog.assertHidden();
```

### Property Access

```java
TextFieldElement field = TextFieldElement.getByLabel(page, "Name");

// Set DOM property
field.setProperty("value", "John");

// Get DOM property
Object disabled = field.getProperty("disabled");
```

### Locator Access

```java
CardElement card = CardElement.getByTitle(page, "Product");

// Get underlying locator for custom operations
Locator locator = card.getLocator();
locator.screenshot();
locator.evaluate("el => el.scrollIntoView()");
```

## Extending VaadinElement

To create a new element wrapper:

```java
@PlaywrightElement(MyElement.FIELD_TAG_NAME)
public class MyElement extends VaadinElement {

    public static final String FIELD_TAG_NAME = "my-component";

    public MyElement(Locator locator) {
        super(locator);
    }

    // Add component-specific methods
    public void doSomething() {
        getLocator().locator(".internal").click();
    }
}
```

## Related Interfaces

- `HasLocatorElement` - Base locator interface
- `HasInputFieldElement` - Input field operations
- `HasValidationPropertiesElement` - Validation support
- `FocusableElement` - Focus operations
- `HasEnabledElement` - Enabled/disabled state
- `HasThemeElement` - Theme variants
