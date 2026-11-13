# Development

How to build, test and release DramaFinder itself. To *use* DramaFinder, start
with [Getting started](guide/getting-started.md).

## Repository layout

- `src/main/java/org/vaadin/addons/dramafinder/` — the library: elements
  (`element/`), mixins (`element/shared/`), typed queries (`element/query/`),
  test base classes, Spring support (`spring/`) and agent helpers (`agent/`).
- `src/test/java/.../tests/testuis/` — the test-only views that exercise the
  library. There is no standalone demo application: the views are served by the
  Spring Boot test harness when the `*IT.java` tests run.
- `src/test/java/.../tests/it/` — the integration tests, one per view.
- `docs/` — the guide, the component pages, ADRs and release notes.
- `skills/` — the Claude Code skills and the generated API references.

## Build and test

```bash
./mvnw clean install        # build + unit tests
./mvnw -Pit verify          # integration tests (Failsafe, **/*IT.java)
./mvnw -Pit verify -Dit.test=ContextMenuViewIT
```

Watch an integration test in a visible browser:

```bash
./mvnw -Pdebug-ui -Dit.test=ContextMenuViewIT verify
```

The integration tests here exercise the API, not best practice: they call
getters such as `getMinLength()` directly to test them, where an application
test should use the retrying `assertMinLength(..)`.

## Generated references

`skills/vaadin-playwright-test/api-reference.md` and
`skills/vaadin-playwright-screenshot/agent-api-reference.md` are generated from
the sources. Never edit them by hand; regenerate after changing a public API:

```bash
jbang tools/generate-api-reference.java
jbang tools/generate-agent-api-reference.java
```

CI fails when they are stale.

## Documentation site

The `Documentation site` workflow publishes the Markdown documentation and the
Javadoc to GitHub Pages on every push to `master`. `site/build.py` collects the
published files (listed in its `NAV`) without moving them. To preview:

```bash
pip install -r site/requirements.txt
python3 site/build.py && mkdocs serve -f target/docs-site/mkdocs.yml
```

- A guide page goes in `docs/guide/` and needs an entry in `NAV`.
- A component page goes in `docs/specifications/<Name>Element.md` and needs an
  entry in the "Components" section of `NAV` (the build fails otherwise) and a
  row in `docs/specifications/index.md`. Follow the structure of the existing
  pages: a summary table, "Finding the component", task-oriented sections with
  view and test code, pitfalls, see also.
- ADRs and release notes are picked up automatically.

## Javadoc

Public APIs are documented with concise Javadoc: element classes name the
Vaadin tag they wrap (e.g. `vaadin-text-field`) and their notable behaviour;
public methods document parameters, return values and `null` semantics. See
the repository guidelines (`AGENTS.md`) for the full conventions.

## Cutting a release

Make sure the build passes locally and in the GitHub Actions verification
build, then:

```bash
mvn release:prepare release:clean
```

The defaults are usually fine. `release:perform` is not needed: a GitHub Action
builds and pushes the release to Maven Central. Vaadin Directory picks up new
releases within about half an hour; if browser or Vaadin version support
changed, adjust the metadata in the Directory UI.
