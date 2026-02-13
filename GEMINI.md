# Drama Finder Project Overview

This document provides an overview of the Drama Finder project, its purpose, and key development aspects relevant for automated agents.

## Project Purpose

Drama Finder is a set of helper classes designed to facilitate testing of Vaadin applications using Playwright. It offers a collection of assertions and actions for Vaadin components, simplifying the creation of robust integration tests.

## Key Technologies

*   **Vaadin:** Frontend framework for web applications.
*   **Playwright:** A Node.js library to automate Chromium, Firefox and WebKit with a single API. Used here for browser automation and testing.
*   **Spring Boot:** Used for the test/demo server and integration tests.
*   **Maven:** Project build automation tool.
*   **JUnit:** Testing framework.
*   **Axe-core:** Used for accessibility testing within integration tests.

## Project Structure Highlights

*   `src/main/java/`: Contains the core Java helper classes for Vaadin and Playwright integration.
*   `src/test/java/`: Contains unit and integration tests for the Drama Finder library itself, as well as demo application tests.
*   `pom.xml`: Maven project configuration, including dependencies and build profiles.

## Development & Testing

### Starting the Demo Server

To run the demo application (used for testing the Drama Finder library):

```bash
mvn spring-boot:run
```

The demo will be available at `http://localhost:8080`.

### Running Unit Tests

```bash
mvn test
```

### Running Integration Tests

Integration tests are located in `src/test/java/.../it/` and are run with the `it` Maven profile.

```bash
mvn -Pit verify
```

## Available Element Classes

Each element class wraps a Playwright `Locator` and provides typed helpers (getters, actions, assertions) for a specific Vaadin component. They all extend `VaadinElement` and are annotated with `@PlaywrightElement`.

| Element | Vaadin Component | Tag |
|---------|-----------------|-----|
| `AccordionElement` | Accordion | `vaadin-accordion` |
| `AccordionPanelElement` | AccordionPanel | `vaadin-accordion-panel` |
| `BigDecimalFieldElement` | BigDecimalField | `vaadin-big-decimal-field` |
| `ButtonElement` | Button | `vaadin-button` |
| `CardElement` | Card | `vaadin-card` |
| `CheckboxElement` | Checkbox | `vaadin-checkbox` |
| `ContextMenuElement` | ContextMenu | `vaadin-context-menu` |
| `DatePickerElement` | DatePicker | `vaadin-date-picker` |
| `DateTimePickerElement` | DateTimePicker | `vaadin-date-time-picker` |
| `DetailsElement` | Details | `vaadin-details` |
| `DialogElement` | Dialog | `vaadin-dialog` |
| `EmailFieldElement` | EmailField | `vaadin-email-field` |
| `IntegerFieldElement` | IntegerField | `vaadin-integer-field` |
| `ListBoxElement` | ListBox | `vaadin-list-box` |
| `MenuBarElement` | MenuBar | `vaadin-menu-bar` |
| `NotificationElement` | Notification | `vaadin-notification-card` |
| `NumberFieldElement` | NumberField | `vaadin-number-field` |
| `PasswordFieldElement` | PasswordField | `vaadin-password-field` |
| `PopoverElement` | Popover | `vaadin-popover` |
| `ProgressBarElement` | ProgressBar | `vaadin-progress-bar` |
| `RadioButtonGroupElement` | RadioButtonGroup | `vaadin-radio-group` |
| `SelectElement` | Select | `vaadin-select` |
| `SideNavigationElement` | SideNav | `vaadin-side-nav` |
| `TabSheetElement` | TabSheet | `vaadin-tabsheet` |
| `TextAreaElement` | TextArea | `vaadin-text-area` |
| `TextFieldElement` | TextField | `vaadin-text-field` |
| `TimePickerElement` | TimePicker | `vaadin-time-picker` |
| `UploadElement` | Upload | `vaadin-upload` |
| `VirtualListElement` | VirtualList | `vaadin-virtual-list` |

### VirtualListElement

`VirtualListElement` wraps `<vaadin-virtual-list>`, a virtualized scrollable list that lazily renders items. It implements `FocusableElement` and `HasStyleElement`.

**Scrolling:**

```java
VirtualListElement list = new VirtualListElement(page.locator("#my-list"));
list.scrollToRow(50);       // scroll to index 50
list.scrollToStart();       // scroll to the top
list.scrollToEnd();         // scroll to the bottom
```

**Querying items:**

```java
list.getRowCount();                    // total item count (including non-rendered)
list.getFirstVisibleRowIndex();        // first visible row index
list.getLastVisibleRowIndex();         // last visible row index
list.getVisibleRowCount();             // number of visible rows
list.isRowInView(50);                  // true if row 50 is visible

list.getRenderedItems();               // Locator for all rendered child elements
list.getItemByIndex(0);                // Locator for item at virtual index
list.getItemByText("Item 1");          // Locator for item containing text
```

**Getting typed components inside items:**

```java
// Get a ButtonElement from item at index 0
ButtonElement btn = list.getItemComponent(0, ButtonElement.class);

// Get a ButtonElement from item matching text
ButtonElement btn = list.getItemComponentByText("Row 1", ButtonElement.class);

// Get the first ButtonElement in any rendered item
ButtonElement btn = list.getComponent(ButtonElement.class);
```

These methods read the tag from the `@PlaywrightElement` annotation and work with any element class.

**Assertions (all auto-retry via Playwright):**

```java
list.assertRowCount(100);              // total items
list.assertEmpty();                    // zero items
list.assertRowInView(0);              // row is visible
list.assertRowNotInView(99);          // row is not visible
list.assertFirstVisibleRow(0);        // first visible index (hasJSProperty)
list.assertLastVisibleRow(10);        // last visible index (hasJSProperty)
list.assertItemRendered("Item 1");    // text is visible in DOM
```

### Shared Mixins

Element classes implement shared interfaces for cross-cutting concerns:

| Interface | Provides |
|-----------|----------|
| `FocusableElement` | `focus()`, `blur()`, `getTabIndex()`, `assertIsFocused()` |
| `HasAriaLabelElement` | `getAriaLabel()`, `assertAriaLabel()` |
| `HasClearButtonElement` | `assertClearButton()` |
| `HasEnabledElement` | `isEnabled()`, `assertEnabled()`, `assertDisabled()` |
| `HasHelperElement` | `getHelper()`, `assertHelper()` |
| `HasLabelElement` | `getLabel()`, `assertLabel()` |
| `HasPlaceholderElement` | `assertPlaceholder()` |
| `HasPrefixElement` | `getPrefixLocator()` |
| `HasStyleElement` | `getCssClass()`, `assertCssClass()` |
| `HasSuffixElement` | `getSuffixLocator()` |
| `HasThemeElement` | `getTheme()`, `assertTheme()` |
| `HasTooltipElement` | `assertTooltipHasText()` |
| `HasValidationPropertiesElement` | `assertRequired()`, `assertErrorMessage()` |
| `HasValueElement` | `getValue()`, `assertValue()` |

## How to Use Drama Finder

To incorporate Drama Finder into a Vaadin project, add it as a test dependency in your `pom.xml`:

```xml
<dependency>
    <groupId>org.vaadin.addons</groupId>
    <artifactId>dramafinder</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
```

Example of an integration test:

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SimpleExampleViewIT extends SpringPlaywrightIT {
    @Test
    public void testTooltip() {
        TextFieldElement textfield = TextFieldElement.getByLabel(page, "Textfield");
        textfield.assertVisible();
        textfield.assertTooltipHasText("Tooltip for textfield");
    }
}
```
