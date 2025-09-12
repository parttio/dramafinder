# PR Title

Short, imperative summary (e.g., "core: add VaadinQuery helper").

## Summary
- What problem does this solve? Why now?

## Changes
- Key changes in bullet points.
- Note module(s) touched: `dramafinder`, `dramafinder-demo`, `dramafinder-test-reporting`.

## Screenshots / Logs (optional)
- If UI or Playwright behavior changed, add screenshots/gifs or relevant logs.

## How to Test
1. Build all modules: `./mvnw clean install`
2. Run demo locally: `./mvnw -pl dramafinder-demo spring-boot:run` (http://localhost:8080)
3. Run integration tests: `./mvnw -pl dramafinder-demo verify -Pit`
4. Run a specific test if needed:
   - Unit: `./mvnw -Dtest=MyClassTest test`
   - IT: `./mvnw -pl dramafinder-demo -Dit.test=MyViewIT -Pit verify`

## Checklist
- [ ] Tests added/updated for new/changed behavior
- [ ] Docs updated (README or AGENTS.md) if commands/APIs changed
- [ ] No generated/build artifacts committed (`target/`, `frontend/generated/`, `vite.generated.ts`)
- [ ] Java 17 compatible; imports organized; no trailing whitespace
- [ ] CI passes (build, tests)

## Related Issues
Closes #<id> / Relates to #<id>

## Breaking Changes
- [ ] None
- [ ] Describe migration steps:

## Release Notes
One-line, user-facing summary for CHANGELOG.

