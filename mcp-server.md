# Drama Finder MCP Server Specification

## Overview

This document specifies an MCP (Model Context Protocol) server that enables AI
assistants to interact with and test Vaadin applications using the Drama Finder
library. The server will be implemented as a Spring Boot application and
distributed as a Docker container.

## Purpose

The MCP server provides AI assistants with:

1. **Tools** to execute Playwright-based UI tests on Vaadin applications
2. **Resources** to access component documentation and specifications
3. **Prompts** to guide test creation and debugging workflows

## Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    Docker Container                      │
│  ┌───────────────────────────────────────────────────┐  │
│  │              Spring Boot Application               │  │
│  │  ┌─────────────────────────────────────────────┐  │  │
│  │  │            MCP Server (stdio/SSE)           │  │  │
│  │  │  ┌─────────┐ ┌──────────┐ ┌─────────────┐  │  │  │
│  │  │  │  Tools  │ │Resources │ │   Prompts   │  │  │  │
│  │  │  └────┬────┘ └────┬─────┘ └──────┬──────┘  │  │  │
│  │  └───────┼───────────┼──────────────┼─────────┘  │  │
│  │          │           │              │            │  │
│  │  ┌───────▼───────────▼──────────────▼─────────┐  │  │
│  │  │          Drama Finder Library              │  │  │
│  │  │  (Playwright + Vaadin Element Wrappers)    │  │  │
│  │  └─────────────────────────────────────────────┘  │  │
│  └───────────────────────────────────────────────────┘  │
│                           │                              │
│                     Playwright                           │
│                     (Chromium)                           │
└─────────────────────────────────────────────────────────┘
```

## Technology Stack

| Component          | Technology                    | Version |
|--------------------|-------------------------------|---------|
| Runtime            | Java                          | 21+     |
| Framework          | Spring Boot                   | 4.0.x   |
| MCP SDK            | mcp-spring-sdk                | latest  |
| Browser Automation | Playwright                    | 1.55.0  |
| Build System       | Maven                         | 3.9+    |
| Container          | Docker                        | 24+     |
| Base Image         | eclipse-temurin:21-jre-alpine | latest  |

## MCP Capabilities

### Tools

#### 1. `vaadin_navigate`

Navigate to a Vaadin application URL.

**Parameters:**
| Name | Type | Required | Description |
|------|------|----------|-------------|
| url | string | yes | The URL of the Vaadin application to test |
| waitForVaadin | boolean | no | Wait for Vaadin to finish loading (default:
true) |

**Returns:** `{ success: boolean, title: string, url: string }`

---

#### 2. `vaadin_find_element`

Find a Vaadin component on the page using accessibility-first locators.

**Parameters:**
| Name | Type | Required | Description |
|------|------|----------|-------------|
| elementType | string | yes | The Drama Finder element type (e.g., "
TextField", "Button", "Select") |
| locatorType | string | yes | Locator strategy: "label", "text", "
placeholder", "testId" |
| locatorValue | string | yes | The value to search for |
| scopeSelector | string | no | Optional CSS selector to scope the search |

**Returns:** `{ found: boolean, elementId: string, properties: object }`

**Supported Element Types:**

- Input: `TextField`, `TextArea`, `EmailField`, `PasswordField`, `NumberField`,
  `IntegerField`, `BigDecimalField`
- Date/Time: `DatePicker`, `TimePicker`, `DateTimePicker`
- Selection: `Checkbox`, `RadioButton`, `RadioButtonGroup`, `Select`, `ListBox`,
  `ComboBox`, `MultiSelectComboBox`
- Navigation: `MenuBar`, `Menu`, `MenuItem`, `ContextMenu`, `SideNavigation`,
  `SideNavigationItem`
- Layout: `Card`, `Accordion`, `AccordionPanel`, `Details`, `TabSheet`, `Tab`
- Overlays: `Dialog`, `Notification`, `Popover`
- Other: `Button`, `Upload`, `ProgressBar`, `Grid`, `Avatar`

---

#### 3. `vaadin_interact`

Perform an action on a previously found element.

**Parameters:**
| Name | Type | Required | Description |
|------|------|----------|-------------|
| elementId | string | yes | The element ID returned from`vaadin_find_element` |
| action | string | yes | The action to perform |
| value | string | no | Value for actions that require input |

**Supported Actions:**
| Action | Description | Requires Value |
|--------|-------------|----------------|
| `click` | Click the element | no |
| `setValue` | Set the element's value | yes |
| `clear` | Clear the element's value | no |
| `focus` | Focus the element | no |
| `blur` | Remove focus from element | no |
| `open` | Open overlay/dropdown | no |
| `close` | Close overlay/dropdown | no |
| `selectItem` | Select item by text | yes |
| `upload` | Upload file(s) | yes (file path) |

**Returns:** `{ success: boolean, error?: string }`

---

#### 4. `vaadin_assert`

Perform an assertion on an element.

**Parameters:**
| Name | Type | Required | Description |
|------|------|----------|-------------|
| elementId | string | yes | The element ID to assert on |
| assertion | string | yes | The assertion type |
| expectedValue | string | no | Expected value for value-based assertions |
| timeout | number | no | Timeout in milliseconds (default: 5000) |

**Supported Assertions:**
| Assertion | Description | Requires Expected Value |
|-----------|-------------|-------------------------|
| `visible` | Element is visible | no |
| `hidden` | Element is hidden | no |
| `enabled` | Element is enabled | no |
| `disabled` | Element is disabled | no |
| `required` | Element is required | no |
| `valid` | Element passes validation | no |
| `invalid` | Element fails validation | no |
| `hasValue` | Element has specific value | yes |
| `hasText` | Element contains text | yes |
| `hasLabel` | Element has specific label | yes |
| `hasPlaceholder` | Element has specific placeholder | yes |
| `hasErrorMessage` | Element shows error message | yes |
| `hasTooltip` | Element has tooltip text | yes |
| `open` | Overlay is open | no |
| `closed` | Overlay is closed | no |

**Returns:** `{ passed: boolean, actual?: string, error?: string }`

---

#### 5. `vaadin_get_property`

Get a property value from an element.

**Parameters:**
| Name | Type | Required | Description |
|------|------|----------|-------------|
| elementId | string | yes | The element ID |
| property | string | yes | Property name to retrieve |

**Returns:** `{ value: any, type: string }`

---

#### 6. `vaadin_screenshot`

Take a screenshot of the current page or a specific element.

**Parameters:**
| Name | Type | Required | Description |
|------|------|----------|-------------|
| elementId | string | no | Optional element ID for element screenshot |
| fullPage | boolean | no | Capture full page (default: false) |

**Returns:** `{ imageData: string (base64), width: number, height: number }`

---

#### 7. `vaadin_execute_test`

Execute a complete test scenario defined as a sequence of steps.

**Parameters:**
| Name | Type | Required | Description |
|------|------|----------|-------------|
| testName | string | yes | Name of the test |
| steps | array | yes | Array of test step objects |

**Step Object Schema:**

```json
{
  "action": "navigate|find|interact|assert|wait",
  "params": {
    /* action-specific parameters */
  }
}
```

**Returns:**
`{ passed: boolean, stepsExecuted: number, failedStep?: number, error?: string }`

---

#### 8. `vaadin_browser_control`

Control the browser instance lifecycle.

**Parameters:**
| Name | Type | Required | Description |
|------|------|----------|-------------|
| action | string | yes | "start", "stop", "restart", "status" |
| headless | boolean | no | Run in headless mode (default: true) |
| browserType | string | no | "chromium", "firefox", "webkit" (default: "
chromium") |

**Returns:** `{ status: string, browserType: string, headless: boolean }`

---

### Resources

#### 1. `dramafinder://elements`

List all available Drama Finder element types with brief descriptions.

**URI Template:** `dramafinder://elements`

**Returns:** JSON array of element types and their capabilities.

---

#### 2. `dramafinder://element/{elementType}`

Get detailed specification for a specific element type.

**URI Template:** `dramafinder://element/{elementType}`

**Parameters:**
| Name | Type | Description |
|------|------|-------------|
| elementType | string | The element type name (e.g., "TextField") |

**Returns:** Markdown documentation including:

- ARIA roles used
- Available factory methods
- Supported actions
- Assertion methods
- Code examples

---

#### 3. `dramafinder://patterns`

Common testing patterns and best practices.

**URI Template:** `dramafinder://patterns`

**Returns:** Documentation on:

- Form testing patterns
- Navigation testing
- Dialog/overlay handling
- Grid/table testing
- File upload testing
- Validation testing

---

#### 4. `dramafinder://session`

Get current browser session state.

**URI Template:** `dramafinder://session`

**Returns:**

```json
{
  "active": true,
  "currentUrl": "https://...",
  "pageTitle": "...",
  "elementsFound": 5,
  "browserType": "chromium",
  "headless": true
}
```

---

### Prompts

#### 1. `create_test`

Guide the AI to create a test for a specific user flow.

**Arguments:**
| Name | Type | Required | Description |
|------|------|----------|-------------|
| applicationUrl | string | yes | URL of the application to test |
| userFlow | string | yes | Description of the user flow to test |

**Returns:** A structured prompt that guides test creation including:

- Navigation setup
- Element discovery strategy
- Interaction sequence
- Assertions to verify behavior

---

#### 2. `debug_test`

Help diagnose why a test is failing.

**Arguments:**
| Name | Type | Required | Description |
|------|------|----------|-------------|
| errorMessage | string | yes | The error message from the failed test |
| lastAction | string | no | The last action attempted |

**Returns:** Debugging guidance including:

- Common causes for the error
- Diagnostic steps to try
- Alternative locator strategies

---

#### 3. `element_discovery`

Help find the right element on a page.

**Arguments:**
| Name | Type | Required | Description |
|------|------|----------|-------------|
| componentDescription | string | yes | Description of the component to find |
| pageContext | string | no | Context about the current page |

**Returns:** Suggestions for:

- Appropriate element type
- Locator strategies to try
- How to verify the element was found

---

## Docker Configuration

### Dockerfile

```dockerfile
FROM eclipse-temurin:21-jre-alpine

# Install Playwright dependencies
RUN apk add --no-cache \
    chromium \
    nss \
    freetype \
    harfbuzz \
    ca-certificates \
    ttf-freefont

# Set Playwright to use system Chromium
ENV PLAYWRIGHT_CHROMIUM_EXECUTABLE_PATH=/usr/bin/chromium-browser
ENV PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=1

# Create app directory
WORKDIR /app

# Copy application
COPY target/dramafinder-mcp-server.jar app.jar

# Copy element specifications
COPY docs/specifications /app/specifications

# Expose MCP server port (for SSE transport)
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD wget -q --spider http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Docker Compose (for development)

```yaml
version: '3.8'
services:
  dramafinder-mcp:
    build: .
    image: dramafinder-mcp-server:latest
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - MCP_TRANSPORT=sse
      - PLAYWRIGHT_HEADLESS=true
    volumes:
      - ./test-results:/app/test-results
    networks:
      - test-network

networks:
  test-network:
    driver: bridge
```

---

## Configuration

### Application Properties

```yaml
# application.yml
spring:
  application:
    name: dramafinder-mcp-server

mcp:
  server:
    name: dramafinder
    version: 1.0.0
    transport: ${MCP_TRANSPORT:stdio}  # stdio or sse

playwright:
  headless: ${PLAYWRIGHT_HEADLESS:true}
  browser: ${PLAYWRIGHT_BROWSER:chromium}
  timeout:
    default: 30000
    assertion: 5000
  viewport:
    width: 1280
    height: 720

dramafinder:
  specifications:
    path: ${SPECS_PATH:classpath:specifications/}
  session:
    max-elements: 100
    element-ttl: 300000  # 5 minutes
```

### Environment Variables

| Variable              | Description                      | Default                     |
|-----------------------|----------------------------------|-----------------------------|
| `MCP_TRANSPORT`       | Transport type: `stdio` or `sse` | `stdio`                     |
| `PLAYWRIGHT_HEADLESS` | Run browser in headless mode     | `true`                      |
| `PLAYWRIGHT_BROWSER`  | Browser type                     | `chromium`                  |
| `PLAYWRIGHT_TIMEOUT`  | Default timeout in ms            | `30000`                     |
| `SPECS_PATH`          | Path to element specifications   | `classpath:specifications/` |

---

## MCP Client Configuration

### Claude Desktop Configuration

```json
{
  "mcpServers": {
    "dramafinder": {
      "command": "docker",
      "args": [
        "run",
        "-i",
        "--rm",
        "--network",
        "host",
        "dramafinder-mcp-server:latest"
      ]
    }
  }
}
```

### SSE Transport Configuration

```json
{
  "mcpServers": {
    "dramafinder": {
      "url": "http://localhost:8080/sse",
      "transport": "sse"
    }
  }
}
```

---

## Security Considerations

1. **Network Isolation**: The MCP server should only connect to trusted Vaadin
   applications
2. **No Arbitrary Code Execution**: Tests are defined declaratively, not as
   arbitrary code
3. **Resource Limits**: Browser instances have memory and CPU limits
4. **Session Cleanup**: Browser sessions are automatically cleaned up after
   inactivity
5. **URL Allowlist**: Optional configuration to restrict which URLs can be
   tested

---

## Error Handling

All tools return structured error responses:

```json
{
  "success": false,
  "error": {
    "code": "ELEMENT_NOT_FOUND",
    "message": "Could not find TextField with label 'Email'",
    "suggestion": "Try using placeholder or testId locator instead"
  }
}
```

### Error Codes

| Code                       | Description                                 |
|----------------------------|---------------------------------------------|
| `BROWSER_NOT_STARTED`      | Browser session not active                  |
| `NAVIGATION_FAILED`        | Failed to navigate to URL                   |
| `ELEMENT_NOT_FOUND`        | Element could not be located                |
| `ELEMENT_NOT_INTERACTABLE` | Element found but cannot be interacted with |
| `ASSERTION_FAILED`         | Assertion did not pass                      |
| `TIMEOUT`                  | Operation timed out                         |
| `INVALID_PARAMETER`        | Invalid parameter value                     |
| `SESSION_EXPIRED`          | Element reference expired                   |

---

## Implementation Phases

### Phase 1: Core Infrastructure

- [ ] Spring Boot application setup with MCP SDK
- [ ] Playwright integration and browser lifecycle management
- [ ] Basic tool implementations: `vaadin_navigate`, `vaadin_browser_control`
- [ ] Docker container setup

### Phase 2: Element Interaction

- [ ] `vaadin_find_element` with all element types
- [ ] `vaadin_interact` with common actions
- [ ] `vaadin_get_property`
- [ ] Element reference management

### Phase 3: Assertions & Testing

- [ ] `vaadin_assert` with all assertion types
- [ ] `vaadin_screenshot`
- [ ] `vaadin_execute_test` for batch execution

### Phase 4: Resources & Prompts

- [ ] Resource endpoints for documentation
- [ ] Prompt templates
- [ ] Session state resource

### Phase 5: Polish & Documentation

- [ ] Error handling improvements
- [ ] Performance optimization
- [ ] Usage documentation
- [ ] Example test scenarios

---

## Usage Examples

### Example 1: Login Form Test

```
AI: I'll test the login form on your Vaadin application.

1. Navigate to the application
   → vaadin_navigate(url: "https://app.example.com/login")

2. Find the username field
   → vaadin_find_element(elementType: "TextField", locatorType: "label", locatorValue: "Username")

3. Enter username
   → vaadin_interact(elementId: "el_1", action: "setValue", value: "testuser")

4. Find password field
   → vaadin_find_element(elementType: "PasswordField", locatorType: "label", locatorValue: "Password")

5. Enter password
   → vaadin_interact(elementId: "el_2", action: "setValue", value: "secret123")

6. Find and click login button
   → vaadin_find_element(elementType: "Button", locatorType: "text", locatorValue: "Login")
   → vaadin_interact(elementId: "el_3", action: "click")

7. Verify navigation to dashboard
   → vaadin_assert(elementId: "el_4", assertion: "visible")
```

### Example 2: Form Validation Test

```
AI: I'll verify the form validation is working correctly.

1. Navigate to the form
2. Find the email field and enter invalid email
   → vaadin_find_element(elementType: "EmailField", locatorType: "label", locatorValue: "Email")
   → vaadin_interact(elementId: "el_1", action: "setValue", value: "not-an-email")
   → vaadin_interact(elementId: "el_1", action: "blur")

3. Assert validation error appears
   → vaadin_assert(elementId: "el_1", assertion: "invalid")
   → vaadin_assert(elementId: "el_1", assertion: "hasErrorMessage", expectedValue: "Please enter a valid email")

4. Enter valid email
   → vaadin_interact(elementId: "el_1", action: "setValue", value: "user@example.com")

5. Assert validation passes
   → vaadin_assert(elementId: "el_1", assertion: "valid")
```

---

## References

- [Model Context Protocol Specification](https://modelcontextprotocol.io/)
- [MCP Spring SDK](https://github.com/modelcontextprotocol/java-sdk)
- [Drama Finder Documentation](./README.md)
- [Playwright for Java](https://playwright.dev/java/)
- [Vaadin Web Components](https://vaadin.com/docs/latest/components)
