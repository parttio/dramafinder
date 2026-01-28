package org.vaadin.addons.dramafinder.mcp.tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import org.vaadin.addons.dramafinder.element.*;
import org.vaadin.addons.dramafinder.element.shared.*;
import org.vaadin.addons.dramafinder.mcp.service.BrowserService;
import org.vaadin.addons.dramafinder.mcp.service.ElementRegistry;

import java.nio.file.Path;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * MCP Tools for Vaadin application testing using Drama Finder.
 * <p>
 * All methods annotated with @Tool are automatically exposed as MCP tools.
 */
@Service
public class VaadinMcpTools {

    private static final Logger log = LoggerFactory.getLogger(VaadinMcpTools.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final BrowserService browserService;
    private final ElementRegistry elementRegistry;

    public VaadinMcpTools(BrowserService browserService, ElementRegistry elementRegistry) {
        this.browserService = browserService;
        this.elementRegistry = elementRegistry;
    }

    @Tool(description = "Navigate to a Vaadin application URL. Optionally waits for Vaadin to finish loading.")
    public String vaadinNavigate(
            @ToolParam(description = "The URL of the Vaadin application to test") String url,
            @ToolParam(description = "Wait for Vaadin to finish loading (default: true)", required = false) Boolean waitForVaadin
    ) {
        try {
            boolean wait = waitForVaadin == null || waitForVaadin;

            if (!browserService.isRunning()) {
                browserService.start();
            }

            elementRegistry.clear();
            Page page = browserService.getPage();
            log.info("Navigating to: {}", url);

            page.navigate(url);

            if (wait) {
                waitForVaadinReady(page);
            }

            return toJson(Map.of(
                    "success", true,
                    "title", page.title(),
                    "url", page.url()
            ));
        } catch (Exception e) {
            log.error("Navigation failed", e);
            return errorJson("NAVIGATION_FAILED", "Failed to navigate: " + e.getMessage());
        }
    }

    @Tool(description = "Control the browser instance lifecycle: start, stop, restart, or check status.")
    public String vaadinBrowserControl(
            @ToolParam(description = "The action: start, stop, restart, or status") String action,
            @ToolParam(description = "Run in headless mode (default: true)", required = false) Boolean headless,
            @ToolParam(description = "Browser type: chromium, firefox, webkit (default: chromium)", required = false) String browserType
    ) {
        try {
            boolean isHeadless = headless == null || headless;
            String browser = browserType != null ? browserType : "chromium";

            return switch (action.toLowerCase()) {
                case "start" -> {
                    browserService.start(browser, isHeadless);
                    elementRegistry.clear();
                    yield toJson(Map.of(
                            "status", "running",
                            "browserType", browserService.getBrowserType(),
                            "headless", browserService.isHeadless()
                    ));
                }
                case "stop" -> {
                    browserService.stop();
                    elementRegistry.clear();
                    yield toJson(Map.of("status", "stopped"));
                }
                case "restart" -> {
                    browserService.restart();
                    elementRegistry.clear();
                    yield toJson(Map.of(
                            "status", "running",
                            "browserType", browserService.getBrowserType(),
                            "headless", browserService.isHeadless()
                    ));
                }
                case "status" -> {
                    Map<String, Object> result = new LinkedHashMap<>();
                    result.put("status", browserService.isRunning() ? "running" : "stopped");
                    if (browserService.isRunning()) {
                        result.put("browserType", browserService.getBrowserType());
                        result.put("headless", browserService.isHeadless());
                        result.put("currentUrl", browserService.getCurrentUrl());
                        result.put("pageTitle", browserService.getPageTitle());
                    }
                    yield toJson(result);
                }
                default -> errorJson("INVALID_PARAMETER", "Invalid action: " + action);
            };
        } catch (Exception e) {
            log.error("Browser control failed", e);
            return errorJson("BROWSER_CONTROL_FAILED", e.getMessage());
        }
    }

    @Tool(description = "Find a Vaadin component on the page using accessibility-first locators.")
    public String vaadinFindElement(
            @ToolParam(description = "Element type: TextField, Button, Select, DatePicker, etc.") String elementType,
            @ToolParam(description = "Locator strategy: label, text, placeholder, testId") String locatorType,
            @ToolParam(description = "The value to search for") String locatorValue,
            @ToolParam(description = "Optional CSS selector to scope the search", required = false) String scopeSelector
    ) {
        try {
            if (!browserService.isRunning()) {
                return errorJson("BROWSER_NOT_STARTED", "Browser is not started");
            }

            Page page = browserService.getPage();
            Locator scope = scopeSelector != null ? page.locator(scopeSelector) : page.locator("body");

            log.info("Finding {} with {} = '{}'", elementType, locatorType, locatorValue);

            VaadinElement element = findElement(page, scope, elementType, locatorType, locatorValue);

            if (element == null || !element.getLocator().isVisible()) {
                return errorJson("ELEMENT_NOT_FOUND",
                        String.format("Could not find %s with %s '%s'", elementType, locatorType, locatorValue));
            }

            String elementId = elementRegistry.register(element, elementType);

            return toJson(Map.of(
                    "found", true,
                    "elementId", elementId,
                    "properties", Map.of(
                            "visible", element.isVisible(),
                            "text", element.getText() != null ? element.getText() : ""
                    )
            ));
        } catch (Exception e) {
            log.error("Find element failed", e);
            return errorJson("ELEMENT_NOT_FOUND", "Failed to find element: " + e.getMessage());
        }
    }

    @Tool(description = "Perform an action on a previously found Vaadin element.")
    public String vaadinInteract(
            @ToolParam(description = "The element ID returned from vaadin_find_element") String elementId,
            @ToolParam(description = "Action: click, setValue, clear, focus, blur, open, close, selectItem, upload") String action,
            @ToolParam(description = "Value for actions that require input", required = false) String value
    ) {
        try {
            if (!browserService.isRunning()) {
                return errorJson("BROWSER_NOT_STARTED", "Browser is not started");
            }

            ElementRegistry.ElementEntry entry = elementRegistry.get(elementId);
            if (entry == null) {
                return errorJson("SESSION_EXPIRED", "Element not found: " + elementId);
            }

            VaadinElement element = entry.element();
            String elementType = entry.elementType();

            log.info("Performing {} on {} ({})", action, elementId, elementType);

            performAction(element, elementType, action, value);

            return toJson(Map.of("success", true));
        } catch (Exception e) {
            log.error("Interaction failed", e);
            return errorJson("ELEMENT_NOT_INTERACTABLE", "Failed to perform action: " + e.getMessage());
        }
    }

    @Tool(description = "Perform an assertion on a Vaadin element to verify its state.")
    public String vaadinAssert(
            @ToolParam(description = "The element ID to assert on") String elementId,
            @ToolParam(description = "Assertion: visible, hidden, enabled, disabled, hasValue, hasText, etc.") String assertion,
            @ToolParam(description = "Expected value for value-based assertions", required = false) String expectedValue
    ) {
        try {
            if (!browserService.isRunning()) {
                return errorJson("BROWSER_NOT_STARTED", "Browser is not started");
            }

            ElementRegistry.ElementEntry entry = elementRegistry.get(elementId);
            if (entry == null) {
                return errorJson("SESSION_EXPIRED", "Element not found: " + elementId);
            }

            VaadinElement element = entry.element();
            String elementType = entry.elementType();

            log.info("Asserting {} on {} ({})", assertion, elementId, elementType);

            AssertionResult result = performAssertion(element, elementType, assertion, expectedValue);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("passed", result.passed);
            if (result.actual != null) response.put("actual", result.actual);
            if (!result.passed && result.error != null) response.put("error", result.error);

            return toJson(response);
        } catch (Exception e) {
            log.error("Assertion failed", e);
            return errorJson("ASSERTION_FAILED", "Assertion error: " + e.getMessage());
        }
    }

    @Tool(description = "Get a property value from a Vaadin element.")
    public String vaadinGetProperty(
            @ToolParam(description = "The element ID") String elementId,
            @ToolParam(description = "Property name to retrieve") String property
    ) {
        try {
            if (!browserService.isRunning()) {
                return errorJson("BROWSER_NOT_STARTED", "Browser is not started");
            }

            ElementRegistry.ElementEntry entry = elementRegistry.get(elementId);
            if (entry == null) {
                return errorJson("SESSION_EXPIRED", "Element not found: " + elementId);
            }

            Object value = entry.element().getProperty(property);

            return toJson(Map.of(
                    "value", value != null ? value : "",
                    "type", value != null ? getTypeName(value) : "null"
            ));
        } catch (Exception e) {
            log.error("Get property failed", e);
            return errorJson("PROPERTY_ERROR", "Failed to get property: " + e.getMessage());
        }
    }

    @Tool(description = "Take a screenshot of the current page or a specific element.")
    public String vaadinScreenshot(
            @ToolParam(description = "Optional element ID for element-specific screenshot", required = false) String elementId,
            @ToolParam(description = "Capture full page (default: false)", required = false) Boolean fullPage
    ) {
        try {
            if (!browserService.isRunning()) {
                return errorJson("BROWSER_NOT_STARTED", "Browser is not started");
            }

            boolean isFullPage = fullPage != null && fullPage;
            byte[] imageBytes;
            int width, height;

            if (elementId != null) {
                ElementRegistry.ElementEntry entry = elementRegistry.get(elementId);
                if (entry == null) {
                    return errorJson("SESSION_EXPIRED", "Element not found: " + elementId);
                }

                Locator locator = entry.element().getLocator();
                imageBytes = locator.screenshot();
                var box = locator.boundingBox();
                width = box != null ? (int) box.width : 0;
                height = box != null ? (int) box.height : 0;
            } else {
                Page page = browserService.getPage();
                imageBytes = page.screenshot(new Page.ScreenshotOptions().setFullPage(isFullPage));
                var viewportSize = page.viewportSize();
                width = viewportSize != null ? viewportSize.width : 0;
                height = viewportSize != null ? viewportSize.height : 0;
            }

            return toJson(Map.of(
                    "imageData", Base64.getEncoder().encodeToString(imageBytes),
                    "width", width,
                    "height", height,
                    "mimeType", "image/png"
            ));
        } catch (Exception e) {
            log.error("Screenshot failed", e);
            return errorJson("SCREENSHOT_FAILED", "Failed to take screenshot: " + e.getMessage());
        }
    }

    // Helper methods

    private void waitForVaadinReady(Page page) {
        page.waitForFunction("""
                () => {
                    if (typeof Vaadin === 'undefined' || typeof Vaadin.Flow === 'undefined') {
                        return true;
                    }
                    const clients = Object.values(Vaadin.Flow.clients);
                    return clients.length === 0 || clients.every(client => !client.isActive());
                }
                """);
    }

    private VaadinElement findElement(Page page, Locator scope, String elementType,
                                       String locatorType, String locatorValue) {
        return switch (elementType) {
            case "TextField" -> findTextField(page, scope, locatorType, locatorValue);
            case "TextArea" -> findTextArea(page, scope, locatorType, locatorValue);
            case "EmailField" -> findEmailField(page, scope, locatorType, locatorValue);
            case "PasswordField" -> findPasswordField(page, scope, locatorType, locatorValue);
            case "NumberField" -> findNumberField(page, scope, locatorType, locatorValue);
            case "IntegerField" -> findIntegerField(page, scope, locatorType, locatorValue);
            case "DatePicker" -> findDatePicker(page, scope, locatorType, locatorValue);
            case "TimePicker" -> findTimePicker(page, scope, locatorType, locatorValue);
            case "Checkbox" -> findCheckbox(page, scope, locatorType, locatorValue);
            case "Select" -> findSelect(page, scope, locatorType, locatorValue);
            case "RadioButtonGroup" -> findRadioButtonGroup(page, scope, locatorType, locatorValue);
            case "Button" -> findButton(page, scope, locatorType, locatorValue);
            case "Dialog" -> findDialog(page, locatorType, locatorValue);
            case "Notification" -> findNotification(page, locatorType, locatorValue);
            case "Details" -> findDetails(page, locatorType, locatorValue);
            case "Tab" -> findTab(scope, locatorType, locatorValue);
            case "Upload" -> findUpload(scope, locatorType, locatorValue);
            default -> throw new IllegalArgumentException("Unsupported element type: " + elementType);
        };
    }

    // Element finders
    private TextFieldElement findTextField(Page page, Locator scope, String locatorType, String locatorValue) {
        return switch (locatorType) {
            case "label" -> TextFieldElement.getByLabel(page, locatorValue);
            case "placeholder" -> new TextFieldElement(scope.locator("vaadin-text-field")
                    .filter(new Locator.FilterOptions().setHas(page.getByPlaceholder(locatorValue))).first());
            case "testId" -> new TextFieldElement(scope.locator("vaadin-text-field[data-testid='" + locatorValue + "']").first());
            default -> throw new IllegalArgumentException("Unsupported locator: " + locatorType);
        };
    }

    private TextAreaElement findTextArea(Page page, Locator scope, String locatorType, String locatorValue) {
        return switch (locatorType) {
            case "label" -> TextAreaElement.getByLabel(page, locatorValue);
            case "testId" -> new TextAreaElement(scope.locator("vaadin-text-area[data-testid='" + locatorValue + "']").first());
            default -> throw new IllegalArgumentException("Unsupported locator: " + locatorType);
        };
    }

    private EmailFieldElement findEmailField(Page page, Locator scope, String locatorType, String locatorValue) {
        return switch (locatorType) {
            case "label" -> EmailFieldElement.getByLabel(page, locatorValue);
            case "testId" -> new EmailFieldElement(scope.locator("vaadin-email-field[data-testid='" + locatorValue + "']").first());
            default -> throw new IllegalArgumentException("Unsupported locator: " + locatorType);
        };
    }

    private PasswordFieldElement findPasswordField(Page page, Locator scope, String locatorType, String locatorValue) {
        return switch (locatorType) {
            case "label" -> PasswordFieldElement.getByLabel(page, locatorValue);
            case "testId" -> new PasswordFieldElement(scope.locator("vaadin-password-field[data-testid='" + locatorValue + "']").first());
            default -> throw new IllegalArgumentException("Unsupported locator: " + locatorType);
        };
    }

    private NumberFieldElement findNumberField(Page page, Locator scope, String locatorType, String locatorValue) {
        return switch (locatorType) {
            case "label" -> NumberFieldElement.getByLabel(page, locatorValue);
            case "testId" -> new NumberFieldElement(scope.locator("vaadin-number-field[data-testid='" + locatorValue + "']").first());
            default -> throw new IllegalArgumentException("Unsupported locator: " + locatorType);
        };
    }

    private IntegerFieldElement findIntegerField(Page page, Locator scope, String locatorType, String locatorValue) {
        return switch (locatorType) {
            case "label" -> IntegerFieldElement.getByLabel(page, locatorValue);
            case "testId" -> new IntegerFieldElement(scope.locator("vaadin-integer-field[data-testid='" + locatorValue + "']").first());
            default -> throw new IllegalArgumentException("Unsupported locator: " + locatorType);
        };
    }

    private DatePickerElement findDatePicker(Page page, Locator scope, String locatorType, String locatorValue) {
        return switch (locatorType) {
            case "label" -> DatePickerElement.getByLabel(page, locatorValue);
            case "testId" -> new DatePickerElement(scope.locator("vaadin-date-picker[data-testid='" + locatorValue + "']").first());
            default -> throw new IllegalArgumentException("Unsupported locator: " + locatorType);
        };
    }

    private TimePickerElement findTimePicker(Page page, Locator scope, String locatorType, String locatorValue) {
        return switch (locatorType) {
            case "label" -> TimePickerElement.getByLabel(page, locatorValue);
            case "testId" -> new TimePickerElement(scope.locator("vaadin-time-picker[data-testid='" + locatorValue + "']").first());
            default -> throw new IllegalArgumentException("Unsupported locator: " + locatorType);
        };
    }

    private CheckboxElement findCheckbox(Page page, Locator scope, String locatorType, String locatorValue) {
        return switch (locatorType) {
            case "label" -> CheckboxElement.getByLabel(page, locatorValue);
            case "testId" -> new CheckboxElement(scope.locator("vaadin-checkbox[data-testid='" + locatorValue + "']").first());
            default -> throw new IllegalArgumentException("Unsupported locator: " + locatorType);
        };
    }

    private SelectElement findSelect(Page page, Locator scope, String locatorType, String locatorValue) {
        return switch (locatorType) {
            case "label" -> SelectElement.getByLabel(page, locatorValue);
            case "testId" -> new SelectElement(scope.locator("vaadin-select[data-testid='" + locatorValue + "']").first());
            default -> throw new IllegalArgumentException("Unsupported locator: " + locatorType);
        };
    }

    private RadioButtonGroupElement findRadioButtonGroup(Page page, Locator scope, String locatorType, String locatorValue) {
        return switch (locatorType) {
            case "label" -> RadioButtonGroupElement.getByLabel(page, locatorValue);
            case "testId" -> new RadioButtonGroupElement(scope.locator("vaadin-radio-group[data-testid='" + locatorValue + "']").first());
            default -> throw new IllegalArgumentException("Unsupported locator: " + locatorType);
        };
    }

    private ButtonElement findButton(Page page, Locator scope, String locatorType, String locatorValue) {
        return switch (locatorType) {
            case "text" -> ButtonElement.getByText(page, locatorValue);
            case "testId" -> new ButtonElement(scope.locator("vaadin-button[data-testid='" + locatorValue + "']").first());
            default -> throw new IllegalArgumentException("Unsupported locator: " + locatorType);
        };
    }

    private DialogElement findDialog(Page page, String locatorType, String locatorValue) {
        return switch (locatorType) {
            case "text" -> DialogElement.getByHeaderText(page, locatorValue);
            case "testId" -> new DialogElement(page.locator("vaadin-dialog[data-testid='" + locatorValue + "']").first());
            default -> throw new IllegalArgumentException("Unsupported locator: " + locatorType);
        };
    }

    private NotificationElement findNotification(Page page, String locatorType, String locatorValue) {
        return switch (locatorType) {
            case "text" -> new NotificationElement(page.locator("vaadin-notification-card")
                    .filter(new Locator.FilterOptions().setHasText(locatorValue)).first());
            default -> throw new IllegalArgumentException("Unsupported locator: " + locatorType);
        };
    }

    private DetailsElement findDetails(Page page, String locatorType, String locatorValue) {
        return switch (locatorType) {
            case "text" -> DetailsElement.getBySummaryText(page, locatorValue);
            case "testId" -> new DetailsElement(page.locator("vaadin-details[data-testid='" + locatorValue + "']").first());
            default -> throw new IllegalArgumentException("Unsupported locator: " + locatorType);
        };
    }

    private TabElement findTab(Locator scope, String locatorType, String locatorValue) {
        return switch (locatorType) {
            case "text" -> TabElement.getTabByText(scope, locatorValue);
            case "testId" -> new TabElement(scope.locator("vaadin-tab[data-testid='" + locatorValue + "']").first());
            default -> throw new IllegalArgumentException("Unsupported locator: " + locatorType);
        };
    }

    private UploadElement findUpload(Locator scope, String locatorType, String locatorValue) {
        return switch (locatorType) {
            case "testId" -> new UploadElement(scope.locator("vaadin-upload[data-testid='" + locatorValue + "']").first());
            default -> throw new IllegalArgumentException("Unsupported locator: " + locatorType);
        };
    }

    // Action methods
    private void performAction(VaadinElement element, String elementType, String action, String value) {
        switch (action) {
            case "click" -> element.click();
            case "setValue" -> {
                if (element instanceof HasValueElement) {
                    ((HasValueElement) element).setValue(value);
                } else {
                    element.setProperty("value", value);
                }
            }
            case "clear" -> {
                if (element instanceof HasValueElement) {
                    ((HasValueElement) element).clear();
                }
            }
            case "focus" -> {
                if (element instanceof FocusableElement) {
                    ((FocusableElement) element).focus();
                }
            }
            case "blur" -> {
                if (element instanceof FocusableElement) {
                    ((FocusableElement) element).blur();
                }
            }
            case "open" -> {
                if (element instanceof DetailsElement) {
                    ((DetailsElement) element).setOpen(true);
                } else {
                    element.click();
                }
            }
            case "close" -> {
                if (element instanceof DetailsElement) {
                    ((DetailsElement) element).setOpen(false);
                } else if (element instanceof DialogElement) {
                    ((DialogElement) element).closeWithEscape();
                } else {
                    element.getLocator().press("Escape");
                }
            }
            case "selectItem" -> {
                if (element instanceof SelectElement) {
                    ((SelectElement) element).selectItem(value);
                } else if (element instanceof RadioButtonGroupElement) {
                    ((RadioButtonGroupElement) element).selectByLabel(value);
                } else if (element instanceof TabElement) {
                    ((TabElement) element).select();
                }
            }
            case "upload" -> {
                if (element instanceof UploadElement) {
                    ((UploadElement) element).uploadFiles(Path.of(value));
                }
            }
            default -> throw new IllegalArgumentException("Unsupported action: " + action);
        }
    }

    // Assertion methods
    private record AssertionResult(boolean passed, String actual, String error) {}

    private AssertionResult performAssertion(VaadinElement element, String elementType, String assertion, String expectedValue) {
        try {
            return switch (assertion) {
                case "visible" -> {
                    element.assertVisible();
                    yield new AssertionResult(true, null, null);
                }
                case "hidden" -> {
                    element.assertHidden();
                    yield new AssertionResult(true, null, null);
                }
                case "enabled" -> {
                    if (element instanceof HasEnabledElement e) {
                        e.assertEnabled();
                        yield new AssertionResult(true, null, null);
                    }
                    yield new AssertionResult(false, null, "Element does not support enabled state");
                }
                case "disabled" -> {
                    if (element instanceof HasEnabledElement e) {
                        e.assertDisabled();
                        yield new AssertionResult(true, null, null);
                    }
                    yield new AssertionResult(false, null, "Element does not support disabled state");
                }
                case "hasValue" -> {
                    if (element instanceof HasValueElement e) {
                        String actual = e.getValue();
                        if (expectedValue.equals(actual)) {
                            yield new AssertionResult(true, actual, null);
                        }
                        yield new AssertionResult(false, actual, "Value mismatch");
                    }
                    yield new AssertionResult(false, null, "Element does not have value");
                }
                case "hasText" -> {
                    String actual = element.getText();
                    if (actual != null && actual.contains(expectedValue)) {
                        yield new AssertionResult(true, actual, null);
                    }
                    yield new AssertionResult(false, actual, "Text not found");
                }
                case "hasLabel" -> {
                    if (element instanceof HasLabelElement e) {
                        String actual = e.getLabel();
                        if (expectedValue.equals(actual)) {
                            yield new AssertionResult(true, actual, null);
                        }
                        yield new AssertionResult(false, actual, "Label mismatch");
                    }
                    yield new AssertionResult(false, null, "Element does not have label");
                }
                case "open" -> {
                    if (element instanceof DetailsElement e) {
                        e.assertOpened();
                        yield new AssertionResult(true, null, null);
                    } else if (element instanceof DialogElement e) {
                        e.assertOpen();
                        yield new AssertionResult(true, null, null);
                    }
                    yield new AssertionResult(false, null, "Element does not support open state");
                }
                case "closed" -> {
                    if (element instanceof DetailsElement e) {
                        e.assertClosed();
                        yield new AssertionResult(true, null, null);
                    } else if (element instanceof DialogElement e) {
                        e.assertClosed();
                        yield new AssertionResult(true, null, null);
                    }
                    yield new AssertionResult(false, null, "Element does not support closed state");
                }
                default -> new AssertionResult(false, null, "Unknown assertion: " + assertion);
            };
        } catch (AssertionError e) {
            return new AssertionResult(false, null, e.getMessage());
        }
    }

    private String getTypeName(Object value) {
        if (value instanceof String) return "string";
        if (value instanceof Number) return "number";
        if (value instanceof Boolean) return "boolean";
        return "object";
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return "{\"error\": \"Serialization failed\"}";
        }
    }

    private String errorJson(String code, String message) {
        return toJson(Map.of(
                "success", false,
                "error", Map.of("code", code, "message", message)
        ));
    }
}
