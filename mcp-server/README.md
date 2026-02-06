# Drama Finder MCP Server

MCP (Model Context Protocol) server that enables AI assistants to interact with
and test Vaadin applications using the Drama Finder library.

## Overview

This server provides AI assistants with tools to:

- Navigate to Vaadin applications
- Find and interact with Vaadin components
- Perform assertions on element state
- Take screenshots
- Control browser lifecycle

## Project Structure

```
mcp-server/
├── pom.xml                     # Maven config (Spring Boot 4, Spring AI MCP, Playwright)
├── Dockerfile                  # Docker image
├── docker-compose.yml          # Development compose
└── src/main/
    ├── java/org/vaadin/addons/dramafinder/mcp/
    │   ├── DramaFinderMcpApplication.java
    │   ├── config/
    │   │   ├── McpConfiguration.java
    │   │   └── PlaywrightProperties.java
    │   ├── service/
    │   │   ├── BrowserService.java
    │   │   └── ElementRegistry.java
    │   └── tools/
    │       └── VaadinMcpTools.java    # All MCP tools with @Tool annotations
    └── resources/
        └── application.yml
```

## Building

```bash
# First, install the parent dramafinder project
cd ..
mvn install -DskipTests

# Then build the MCP server
cd mcp-server
mvn clean package -DskipTests
```

## Running

### Direct Java Execution

```bash
java -jar target/dramafinder-mcp-server.jar
```

### Docker

Build the image:

```bash
docker build -t dramafinder-mcp-server:latest .
```

Run with Docker:

```bash
docker run -p 8080:8080 dramafinder-mcp-server:latest
```

Run with Docker Compose:

```bash
docker-compose up
```

## MCP Client Configuration

### Claude Desktop (stdio transport)

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

### SSE Transport

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

## Available Tools

| Tool                   | Description                                               |
|------------------------|-----------------------------------------------------------|
| `vaadinNavigate`       | Navigate to a Vaadin application URL                      |
| `vaadinBrowserControl` | Control browser lifecycle (start/stop/restart/status)     |
| `vaadinFindElement`    | Find Vaadin components using accessibility-first locators |
| `vaadinInteract`       | Perform actions on found elements (click, setValue, etc.) |
| `vaadinAssert`         | Assert element state (visible, enabled, hasValue, etc.)   |
| `vaadinGetProperty`    | Get property values from elements                         |
| `vaadinScreenshot`     | Take page or element screenshots                          |

## Supported Element Types

- **Input Fields**: TextField, TextArea, EmailField, PasswordField, NumberField,
  IntegerField
- **Date/Time**: DatePicker, TimePicker
- **Selection**: Checkbox, Select, RadioButtonGroup
- **Navigation**: Tab
- **Overlays**: Dialog, Notification, Details
- **Other**: Button, Upload

## Configuration

Environment variables:

| Variable              | Description                                   | Default    |
|-----------------------|-----------------------------------------------|------------|
| `PLAYWRIGHT_HEADLESS` | Run browser headlessly                        | `true`     |
| `PLAYWRIGHT_BROWSER`  | Browser type: `chromium`, `firefox`, `webkit` | `chromium` |
| `PLAYWRIGHT_TIMEOUT`  | Default timeout in ms                         | `30000`    |

## Dependencies

- Spring Boot 4.0.0
- Spring AI MCP 1.1.0-M1-PLATFORM-2
- Playwright 1.55.0
- Drama Finder 1.0.3-SNAPSHOT
