# MessageInputElement Specification

## Overview

`MessageInputElement` is a Playwright element wrapper for the `<vaadin-message-input>` web component. It provides access to the internal text area and send button, value manipulation, submit actions and i18n property accessors.

## Tag Name

```
vaadin-message-input
```

## Class Hierarchy

```
VaadinElement
    └── MessageInputElement
```

## Implemented Interfaces

| Interface | Description |
|-----------|-------------|
| `FocusableElement` | Focus operations (delegates to the native textarea input) |
| `HasEnabledElement` | Enabled/disabled state (delegates to the native textarea input) |
| `HasStyleElement` | Style attribute support |
| `HasThemeElement` | Theme variants support |
| `HasTooltipElement` | Tooltip support |

## Constants

| Constant | Value | Description |
|----------|-------|-------------|
| `FIELD_TAG_NAME` | `"vaadin-message-input"` | HTML tag name |

## API Methods

### Constructor

```java
MessageInputElement(Locator locator)
```

Creates a new `MessageInputElement` from a Playwright locator.

### Static Factory Methods

#### get(Page page)

Get the first `<vaadin-message-input>` on the page.

```java
MessageInputElement input = MessageInputElement.get(page);
```

#### get(Locator locator)

Get the first `<vaadin-message-input>` within a locator scope.

```java
MessageInputElement input = MessageInputElement.get(chatLayout);
```

### Locator Methods

| Method | Description |
|--------|-------------|
| `getTextAreaLocator()` | Locator for the internal `<vaadin-text-area>` |
| `getTextAreaInputLocator()` | Locator for the native textarea inside the text area (`slot="textarea"`) |
| `getSendButtonLocator()` | Locator for the internal send button (`<vaadin-message-input-button>`) |

### Value Methods

| Method | Description |
|--------|-------------|
| `getValue()` | Get the current text area value |
| `setValue(String)` | Set the message text (fills the textarea and syncs the component value) |
| `clear()` | Clear the text area |

### Action Methods

| Method | Description |
|--------|-------------|
| `submit()` | Click the send button to submit the message |
| `submitByEnter()` | Press Enter on the text area to submit |
| `typeAndSubmit(String)` | Set a value then click the send button |

### I18n Methods

| Method | Description |
|--------|-------------|
| `getMessagePlaceholder()` | Get the placeholder text on the text area |
| `getSendButtonText()` | Get the send button text content |

### Assertions

| Method | Description |
|--------|-------------|
| `assertValue(String)` | Assert the text area input has the expected value |
| `assertSendButtonVisible()` | Assert the send button is visible |
| `assertSendButtonHidden()` | Assert the send button is hidden |
| `assertSendButtonEnabled()` | Assert the send button is enabled |
| `assertSendButtonDisabled()` | Assert the send button is disabled |
| `assertMessagePlaceholder(String)` | Assert the text area placeholder matches |
| `assertSendButtonText(String)` | Assert the send button text matches |

## Usage Examples

### Basic Usage

```java
// Get first message input on the page
MessageInputElement input = MessageInputElement.get(page);

// Type a message and submit by clicking send
input.typeAndSubmit("Hello!");

// Or set value and submit with Enter
input.setValue("Hello again!");
input.submitByEnter();
```

### Verifying Submit Behavior

```java
MessageInputElement input = new MessageInputElement(
        page.locator("#my-message-input"));

input.setValue("Test message");
input.assertValue("Test message");
input.submit();

// The component clears the text area after submit
input.assertValue("");
```

### Checking Enabled/Disabled State

```java
MessageInputElement disabledInput = new MessageInputElement(
        page.locator("#disabled-input"));
disabledInput.assertDisabled();

MessageInputElement enabledInput = MessageInputElement.get(page);
enabledInput.assertEnabled();
```

### Custom I18n

```java
MessageInputElement input = new MessageInputElement(
        page.locator("#custom-i18n-input"));

// Verify custom placeholder and button text
input.assertMessagePlaceholder("Type your message here...");
input.assertSendButtonText("Submit");
```

### Accessing Internal Locators

```java
MessageInputElement input = MessageInputElement.get(page);

// Access the internal text area
Locator textArea = input.getTextAreaLocator();

// Access the native textarea for low-level interaction
Locator nativeTextarea = input.getTextAreaInputLocator();

// Access the send button
Locator sendBtn = input.getSendButtonLocator();
```

## Related Elements

- `TextAreaElement` - For standalone text areas
- `ButtonElement` - For standalone buttons
