---
name: DramaFinder Setup
description: Runbook for adding DramaFinder + Playwright to a Vaadin 25 project. Executed by Claude after the user confirms the setup plan.
---

# DramaFinder Setup Runbook

This file is a runbook for Claude to execute after the user confirms the setup
plan in SKILL.md Step 1. Do not relay it to the user as documentation — perform
the steps.

## Constants

- `KNOWN_LATEST = 1.1.1` — fallback version if Maven Central lookup fails. Bump
  when the library releases.

## Step 1 — Resolve the latest version

Run:

```bash
curl -s "https://repo1.maven.org/maven2/org/vaadin/addons/dramafinder/maven-metadata.xml" \
  | grep -o '<release>[^<]*</release>' | sed 's/<[^>]*>//g'
```

If the command returns a non-empty version string, use it. Otherwise, use
`KNOWN_LATEST`.

## Step 2 — Edit `pom.xml`

Two edits, both in `pom.xml`:

### 2a. Add the version property

Inside `<properties>`, add:

```xml

<dramafinder.version>RESOLVED_VERSION</dramafinder.version>
```

Replace `RESOLVED_VERSION` with the value from Step 1. If a
`<dramafinder.version>` property already exists, leave it alone.

### 2b. Add the dependencies

Inside `<dependencies>`, add (skip whichever is already present):

```xml

<dependency>
    <groupId>com.microsoft.playwright</groupId>
    <artifactId>playwright</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
<groupId>org.vaadin.addons</groupId>
<artifactId>dramafinder</artifactId>
<version>${dramafinder.version}</version>
<scope>test</scope>
</dependency>
```

Note the groupId is `org.vaadin.addons` (not `org.vaadin.addons.dramafinder`).

## Step 3 — Copy `SpringPlaywrightIT` (Spring Boot projects only)

Skip this step entirely if the project is not a Spring Boot project.

If `find src/test/java -name SpringPlaywrightIT.java` already returns a result,
skip — the file exists and will be used as-is.

Otherwise:

1. **Find the base package** by locating the class annotated with
   `@SpringBootApplication` under `src/main/java`. Take its package (e.g.,
   `com.example.app`).
2. **Target package** is `<basePackage>.it.support` (e.g.,
   `com.example.app.it.support`).
3. **Read** `templates/SpringPlaywrightIT.java.tmpl` from this skill directory.
4. **Substitute** `{{PACKAGE}}` with the target package.
5. **Write** the result to
   `src/test/java/<basePackage-as-path>/it/support/SpringPlaywrightIT.java`.

## Step 4 — Confirm dependencies resolve

Optional but recommended: run `mvn -q dependency:resolve` to surface any pom
syntax errors before generating tests. Skip if the user wants to proceed without
compile-time verification.

## Debugging with a visible browser (informational)

To run tests with a visible browser, pass `-Dheadless=false`:

```bash
mvn -Dit.test=MyViewIT -Dheadless=false verify
```

A `debug-ui` profile can be added to `pom.xml` for shorthand `-Pdebug-ui`.
Mention this only if the user asks about debugging.