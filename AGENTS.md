# Repository Guidelines

## Project Structure & Module Organization

- Root `pom.xml` — Core Playwright helpers for Vaadin (`src/main/java/...`),
  resources under `src/main/resources`.

## Build, Test, and Development Commands

- Build all modules + unit tests: `./mvnw clean install`
- Run demo locally: `./mvnw -pl dramafinder-demo spring-boot:run` (serves
  at http://localhost:8080)
- Run integration tests (Failsafe, includes `**/*IT.java`):
  `./mvnw -B verify --file pom.xml`
- Run a single test:
    - IT: `./mvnw -Dit.test=MyViewIT -Pit verify`

## Coding Style & Naming Conventions

- Java 21; 4-space indent; organize imports; no trailing whitespace.
- Packages: lowercase (`org.vaadin.dramafinder`); classes: `PascalCase`;
  methods/fields: `camelCase`; constants: `UPPER_SNAKE_CASE`.
- Public API in `dramafinder` should be small, cohesive, and documented with
  Javadoc.
- Prefer aria role for internal locators

### Javadoc Conventions

- Add a class-level Javadoc for each element class and shared mixin:
  - Identify the Vaadin tag using inline code (e.g., `vaadin-text-field`).
  - One–two sentence summary of responsibilities and notable behaviors.
  - For factory helpers (e.g., `getByLabel`), mention the ARIA role used.
- For public methods, document parameters, return values, and null semantics
  (especially for assertion helpers where `null` implies absence).
- Use `{@inheritDoc}` on simple overrides (e.g., locator accessors) to avoid
  duplication.
- Keep Javadoc concise and consistent; prefer present tense and active voice.

## Testing Guidelines

- Frameworks: JUnit 5, Playwright (Java), Axe-core checks in demo.
- Unit tests live with their module; integration tests go in
  `src/test/java/**/*IT.java`.
- Tests should be deterministic; avoid timing sleeps—prefer Playwright waits and
  assertions.

## Commit & Pull Request Guidelines

- Commits: short, imperative subject; optional scope prefix (e.g., `core:`,
  `demo:`). Example: `core: add TestFieldElement to test a Vaadin Textfield`.
- PRs: describe intent and approach, link issues, list test coverage and manual
  steps; include screenshots/gifs for UI changes.
- Keep changes module-scoped; update README/AGENTS.md when commands or workflows
  change.

## Security & Configuration Tips

- Requires Java 21. Vaadin demo uses Node tooling; first runs may download
  frontend and Playwright browser binaries.
- Do not commit generated/build output: `target/`, `frontend/generated/`,
  `vite.generated.ts`.
- Prefer configuration via `application.properties` in each module’s
  `src/main/resources`.

## Agent-Specific Instructions

- Follow this file’s conventions for any edits. Keep patches minimal and
  focused.
- Use `*IT.java` only for end-to-end tests executed by Failsafe.
