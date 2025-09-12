# Repository Guidelines

## Project Structure & Module Organization
- `dramafinder/` — Core Playwright helpers for Vaadin (`src/main/java/...`), resources under `src/main/resources`.
- `dramafinder-demo/` — Spring Boot Vaadin demo app; integration tests in `src/test/java` with `*IT.java`.
- `dramafinder-test-reporting/` — Utilities for test reporting/aggregation.
- Root `pom.xml` — Maven multi-module parent (Java 17).

## Build, Test, and Development Commands
- Build all modules + unit tests: `./mvnw clean install`
- Run demo locally: `./mvnw -pl dramafinder-demo spring-boot:run` (serves at http://localhost:8080)
- Run integration tests (Failsafe, includes `**/*IT.java`): `./mvnw -pl dramafinder-demo verify -Pit`
- Run a single test:
  - Unit: `./mvnw -Dtest=MyClassTest test`
  - IT: `./mvnw -pl dramafinder-demo -Dit.test=MyViewIT -Pit verify`

## Coding Style & Naming Conventions
- Java 17; 4-space indent; organize imports; no trailing whitespace.
- Packages: lowercase (`org.vaadin.dramafinder`); classes: `PascalCase`; methods/fields: `camelCase`; constants: `UPPER_SNAKE_CASE`.
- Public API in `dramafinder` should be small, cohesive, and documented with Javadoc.

## Testing Guidelines
- Frameworks: JUnit 5, Playwright (Java), Axe-core checks in demo.
- Unit tests live with their module; integration tests go in `dramafinder-demo/src/test/java/**/*IT.java`.
- Tests should be deterministic; avoid timing sleeps—prefer Playwright waits and assertions.

## Commit & Pull Request Guidelines
- Commits: short, imperative subject; optional scope prefix (e.g., `core:`, `demo:`). Example: `core: add VaadinQuery helper for nested locators`.
- PRs: describe intent and approach, link issues, list test coverage and manual steps; include screenshots/gifs for UI changes.
- Keep changes module-scoped; update README/AGENTS.md when commands or workflows change.

## Security & Configuration Tips
- Requires Java 17. Vaadin demo uses Node tooling; first runs may download frontend and Playwright browser binaries.
- Do not commit generated/build output: `target/`, `frontend/generated/`, `vite.generated.ts`.
- Prefer configuration via `application.properties` in each module’s `src/main/resources`.

## Agent-Specific Instructions
- Follow this file’s conventions for any edits. Keep patches minimal and focused.
- Add unit tests for new public APIs in `dramafinder`; use `*IT.java` only for end-to-end flows in `dramafinder-demo`.

