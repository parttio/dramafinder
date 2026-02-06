# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is an MCP (Model Context Protocol) server that enables AI assistants to interact with and test Vaadin applications using the Drama Finder library. It exposes browser automation tools over MCP so that AI agents can navigate, find elements, interact, assert, and screenshot Vaadin apps.

**Stack:** Java 21, Spring Boot 4.0.0, Spring AI MCP Server WebFlux 2.0.0-M2, Playwright 1.55.0, Drama Finder 1.0.3-SNAPSHOT

## Build & Run

```bash
# Install parent dramafinder project first (required - this module depends on it)
cd .. && mvn install -DskipTests && cd mcp-server

# Build the MCP server
mvn clean package -DskipTests

# Run
java -jar target/dramafinder-mcp-server.jar

# Docker (multi-stage build, run from parent directory)
cd .. && docker build -f mcp-server/Dockerfile -t dramafinder-mcp-server:latest .
docker run -p 8080:8080 dramafinder-mcp-server:latest
```

There are currently no tests in this module. The parent project has tests.

## Architecture

The server runs on port 8080 and exposes MCP tools via SSE transport (`/mcp/sse`).

### Key Layers

- **`tools/VaadinMcpTools.java`** — Single class containing all 7 MCP tools, annotated with `@Tool`. This is the main entry point for all AI interactions. Tools are auto-discovered via `MethodToolCallbackProvider` in `McpConfiguration`.
- **`service/BrowserService.java`** — Thread-safe Playwright browser lifecycle management (start/stop/restart). Uses `ReentrantLock` for concurrency. Supports chromium, firefox, webkit.
- **`service/ElementRegistry.java`** — Tracks found Vaadin elements in a session with TTL-based expiration. Elements get IDs like `el_1`, `el_2` etc. Uses `ConcurrentHashMap` with scheduled cleanup.
- **`config/PlaywrightProperties.java`** — Java 21 record mapping `playwright.*` config properties (headless, browser type, timeouts, viewport).
- **`config/McpConfiguration.java`** — Spring config that wires `VaadinMcpTools` as a `ToolCallbackProvider`.

### MCP Tool Flow

1. AI calls `vaadinNavigate` → browser starts if needed, navigates, waits for Vaadin readiness
2. AI calls `vaadinFindElement` → uses Drama Finder element classes to locate Vaadin components → returns an `elementId`
3. AI calls `vaadinInteract`/`vaadinAssert`/`vaadinGetProperty`/`vaadinScreenshot` using the `elementId`

### Element Type System

Drama Finder provides typed element classes (`TextFieldElement`, `ButtonElement`, `SelectElement`, etc.) that wrap Playwright locators with Vaadin-specific behavior. The `findElement()` method in `VaadinMcpTools` dispatches to type-specific finders based on `elementType` string. Locator strategies are accessibility-first: `label`, `text`, `placeholder`, `testId`.

### Response Convention

All tool methods return JSON strings. Success responses vary by tool. Error responses follow: `{"success": false, "error": {"code": "ERROR_CODE", "message": "..."}}`.

## Configuration

Key environment variables: `PLAYWRIGHT_HEADLESS` (default: true), `PLAYWRIGHT_BROWSER` (default: chromium), `PLAYWRIGHT_TIMEOUT` (default: 30000ms). See `application.yml` for session management settings (max-elements, element-ttl, cleanup-interval).

## Important Patterns

- All tool methods are in a single class (`VaadinMcpTools`) — when adding new tools, add them here with `@Tool` annotation.
- Switch expressions are used throughout for type dispatch (element types, actions, assertions, locator strategies).
- Browser auto-starts on `vaadinNavigate` if not already running. Element registry clears on navigation and browser lifecycle changes.
- The `waitForVaadinReady()` helper checks `Vaadin.Flow.clients` to ensure the page is stable before interacting.
