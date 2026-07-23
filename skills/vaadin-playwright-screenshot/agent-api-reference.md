# DramaFinder Agent Helpers API Reference

> **Auto-generated from source — do not edit by hand.** Regenerate with `jbang tools/generate-agent-api-reference.java`.
> DramaFinder 1.1.6-SNAPSHOT — 5 agent helper types.

Public API of the `org.vaadin.addons.dramafinder.agent` helpers used by the **vaadin-playwright-screenshot** skill. Method one-liners come from Javadoc.

**Do not download or unzip the DramaFinder jar to discover this API — it is all here.**

A temp verification test extends `VisualVerificationTest`; the other types are its supporting cast (the failure-report writer, the JUnit extension, and the semantic component snapshot).

## Types

[VisualVerificationTest](#visualverificationtest) · [AgentReport](#agentreport) · [AgentReportProvider](#agentreportprovider) · [AgentReporting](#agentreporting) · [ComponentSnapshot](#componentsnapshot)

## VisualVerificationTest

Base class for temporary, agent-driven visual verification tests that run against an <strong>already running</strong> application.

*abstract class* · **implements** AgentReportProvider

**Methods:**

- `String baseUrl()` — The base URL the test connects to.
- `void open(String path)` — Navigate to a path relative to the configured base URL and wait for Vaadin to finish loading.
- `void shot(String name)` — Capture a numbered screenshot into the report directory.

## AgentReport

Writes agent-oriented verification artifacts for a single test into a report directory (target/agent-report/ for Maven, build/agent-report/ for Gradle).

**Constructors:**

- `AgentReport(Page page)` — Create a report writing into the build-tool default directory.
- `AgentReport(Page page, Path directory)` — Create a report writing into the given directory.

**Static methods:**

- `Path defaultReportDirectory()` — Resolve the default report directory for the current build tool: build/agent-report when a Gradle build file is present, otherwise target/agent-report (Maven).

**Methods:**

- `void captureFailure(Throwable error)` — Write the failure bundle for a failed test: failure.txt (message + trimmed stack trace + page URL), failure.png (full-page screenshot), and component-snapshot.txt (semantic snapshot).
- `Path directory()` — The directory this report writes to.
- `Path shot(String name)` — Capture a numbered screenshot of the current page state.

## AgentReportProvider

Implemented by tests that want the AgentReporting extension to write a failure bundle using a specific page and report instance.

*interface*

**Methods:**

- `Page agentPage()` — The page the extension should screenshot and inspect on failure.
- `AgentReport agentReport()` — The report the extension should write the failure bundle into.

## AgentReporting

JUnit 5 extension that, on test failure, writes an agent-oriented report to target/agent-report/ (Maven) or build/agent-report/ (Gradle): a failure.txt with the assertion message and a trimmed stack trace, a screenshot of the page at failure time, and a semantic ComponentSnapshot component snapshot.

**implements** AfterTestExecutionCallback

## ComponentSnapshot

Produces a compact, human- and agent-readable summary of the Vaadin components currently present on a page.

**Static methods:**

- `String capture(Page page)` — Capture a compact snapshot of all meaningful Vaadin components on the page.

