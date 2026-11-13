# DramaFinder

**Playwright element wrappers for testing Vaadin applications in Java.**

📖 [Documentation](https://parttio.github.io/dramafinder/) ·
[Getting started](docs/guide/getting-started.md) ·
[Components](docs/specifications/index.md) ·
[API reference](skills/vaadin-playwright-test/api-reference.md) ·
[Javadoc](https://parttio.github.io/dramafinder/javadoc/)

DramaFinder gives every Vaadin component a typed Java wrapper — an *element* —
that finds the component the way a user does, acts on it like a user, waits for
the server, and asserts what the user sees:

```java
public class ContactViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "contact";
    }

    @Test
    void savesAContact() {
        TextFieldElement.getByLabel(page, "Name").setValue("Ada Lovelace");
        ComboBoxElement.getByLabel(page, "Country").selectItem("United Kingdom");
        DatePickerElement.getByLabel(page, "Birthday").setValue(LocalDate.of(1815, 12, 10));
        ButtonElement.getByText(page, "Save").click();

        NotificationElement.getByText(page, "Contact saved").assertOpen();
        GridElement.get(page).assertCellContent(0, "Name", "Ada Lovelace");
    }
}
```

- **Find components by what the user sees** — the visible label, the text on a
  button, the header of a dialog — matched exactly, so a test never silently
  picks the wrong field.
- **Every action waits for the server round-trip**, so there is no
  `Thread.sleep` in your tests.
- **Every state has a retrying assertion**: `assertValue`, `assertInvalid`,
  `assertErrorMessage`, `assertRowCount`, `assertSelected`, `assertOpen`...
- **Covers the Vaadin component set**: fields, pickers, combo boxes, grids and
  tree grids, dialogs, menus, tabs, side navigation, upload and more.
- **Test base classes** for any server or Spring Boot, with navigation by route
  class, a Spring Security sign-in through `@WithMockUser`, and a failure report
  with screenshot and component snapshot.
- Works with **plain Playwright** too: an element only needs a `Page`.

## Installation

```xml
<dependency>
    <groupId>org.vaadin.addons</groupId>
    <artifactId>dramafinder</artifactId>
    <version>2.0.2</version>
    <scope>test</scope>
</dependency>
```

DramaFinder targets Vaadin 25 and Java 21, and follows semantic versioning.
Continue with [Getting started](docs/guide/getting-started.md).

## Documentation

| | |
|---|---|
| [Getting started](docs/guide/getting-started.md) | dependency, Failsafe, the first test, base classes |
| [Core concepts](docs/guide/core-concepts.md) | elements, factories, actions, assertions, locators, mixins |
| [Finding components](docs/guide/finding-components.md) | labels, text, ids, items and the typed query builder |
| [Assertions](docs/guide/assertions.md) | retrying assertions, `null` semantics, timeouts |
| [Actions and waiting](docs/guide/actions-and-waiting.md) | automatic waiting for the server, in-flight states |
| [Navigation](docs/guide/navigation.md) | `navigate(View.class)`, `assertAt`, query parameters |
| [Spring Boot and Spring Security](docs/guide/spring.md) | `SpringPlaywrightIT`, `@WithMockUser`, several users |
| [Recipes](docs/guide/recipes.md) | forms, grids, dialogs, uploads, menus end to end |
| [Debugging failed tests](docs/guide/debugging.md) | failure report, view outline, traces, client-side errors |
| [Writing your own element](docs/guide/extending.md) | wrap add-ons and your own components |
| [Components](docs/specifications/index.md) | one page per component, with examples |
| [API reference](skills/vaadin-playwright-test/api-reference.md) | every element and signature, generated from source |

Upgrading? See [Migrating to 2.0](src/main/resources/META-INF/dramafinder/MIGRATION-2.0.md)
and [Migrating from Mopo](src/main/resources/META-INF/dramafinder/MIGRATION-FROM-MOPO.md).

## AI coding agents

The API reference, a short guide (`START-HERE.md`) and the migration guides ship
**inside the jar**, so an agent can read them offline instead of decompiling:

```bash
JAR=$(ls ~/.m2/repository/org/vaadin/addons/dramafinder/*/dramafinder-*.jar | tail -1)
unzip -p "$JAR" META-INF/dramafinder/START-HERE.md
unzip -p "$JAR" META-INF/dramafinder/api-reference.md
```

The repository also ships two Claude Code skills:

```text
/plugin marketplace add parttio/dramafinder
/plugin install vaadin-playwright-test@dramafinder
/plugin install vaadin-playwright-screenshot@dramafinder
```

- **vaadin-playwright-test** generates Playwright integration tests for Vaadin
  views with DramaFinder.
- **vaadin-playwright-screenshot** captures screenshots of UI changes with the
  `org.vaadin.addons.dramafinder.agent` helpers.

Run `/plugin marketplace update dramafinder` to get later updates.

## Contributing

Missing a component or a method? [Open an issue](https://github.com/parttio/dramafinder/issues)
or a pull request. [Development](docs/development.md) explains how to build,
test and release, and `AGENTS.md` holds the coding conventions.
