# MarkdownElement Specification

> For the authoritative method list and signatures, see the auto-generated
> [api-reference.md](../../skills/vaadin-playwright-test/api-reference.md). This
> file focuses on behaviour and usage that signatures alone don't convey.

## Overview

`MarkdownElement` is a Playwright element wrapper for the `<vaadin-markdown>` web component.

The component parses its `content` property as Markdown and writes the resulting HTML
into its **own light DOM**, which is projected through the default slot in the shadow
root. Everything this wrapper exposes therefore describes the **rendered output** —
`# Title` is asserted as the text `Title` inside an `<h1>`, never as the source
string `# Title`.

Headings and links are located by their ARIA roles (`heading` and `link`), following
the repository's preference for role-based locators. Code blocks have no ARIA role and
are located structurally as `pre > code`, which deliberately excludes inline code
(`` `code` ``).

## Tag Name

```
vaadin-markdown
```

## Class Hierarchy

```
VaadinElement
    └── MarkdownElement
```

## Implemented Interfaces

| Interface | Description |
|-----------|-------------|
| `HasThemeElement` | `theme` attribute support |
| `HasStyleElement` | Style/class attribute support |

## Constants

| Constant | Value | Description |
|----------|-------|-------------|
| `FIELD_TAG_NAME` | `"vaadin-markdown"` | HTML tag name |

## API Methods

### Constructor

```java
MarkdownElement(Locator locator)
```

### Static Factory Methods

| Method | Description |
|--------|-------------|
| `get(Page page)` | First `<vaadin-markdown>` on the page |
| `get(Locator locator)` | First `<vaadin-markdown>` inside the given scope |

For a page with several Markdown components, construct the wrapper from a locator:

```java
MarkdownElement second = new MarkdownElement(
        page.locator(MarkdownElement.FIELD_TAG_NAME).nth(1));
```

### Rendered Content

| Method | Description |
|--------|-------------|
| `getRenderedLocator()` | Locator scoping the rendered output — the component root, because the rendered HTML lives in its light DOM |
| `getText()` | Rendered text, trimmed. Overrides `VaadinElement.getText()`, which returns the raw `textContent` |
| `assertContainsText(String expected)` | Assert the rendered output contains the text |
| `assertText(String expected)` | Assert the full rendered text (whitespace normalized) |

### Headings

| Method | Description |
|--------|-------------|
| `getHeadings()` | Locator for all `h1`–`h6`, in document order |
| `getHeadings(int level)` | Locator for the headings of one level (1–6) |
| `getHeading(int index)` | Locator for the heading at the given index |
| `assertHeadingCount(int count)` | Assert the number of headings |
| `assertHeading(int index, String expected)` | Assert a heading's text |

### Links

| Method | Description |
|--------|-------------|
| `getLinks()` | Locator for all rendered links, in document order |
| `getLink(String text)` | Locator for the link with the given accessible name, matched **exactly**. Strict: fails if several links share the name |
| `assertLinkCount(int count)` | Assert the number of links |
| `assertLink(String text, String href)` | Assert a link's target. The text is matched exactly and must be unique |

The link name is matched exactly, so `getLink("docs")` does not match a link named
`API docs`, and it is not disambiguated with `.first()`: several links with the same
accessible name is an ambiguity in the test, and Playwright's strict mode reports it
instead of picking an arbitrary one. To address one of a set of same-named links, use
`getLinks().nth(int)`.

### Code Blocks

| Method | Description |
|--------|-------------|
| `getCodeBlocks()` | Locator for all `pre > code` blocks — inline code is not matched |
| `getCodeBlock(int index)` | Locator for the code block at the given index |
| `assertCodeBlockCount(int count)` | Assert the number of code blocks |
| `assertCodeBlock(int index, String expected)` | Assert a code block's text (whitespace normalized) |
| `assertCodeBlockLanguage(int index, String language)` | Assert the fence language — rendered as the class `language-<language>` |

## Usage Examples

### Asserting rendered text

```java
MarkdownElement markdown = MarkdownElement.get(page);

markdown.assertContainsText("Getting started"); // not "## Getting started"
String text = markdown.getText();               // rendered text, trimmed
```

### Structure

```java
MarkdownElement markdown = MarkdownElement.get(page);

markdown.assertHeadingCount(3);
markdown.assertHeading(0, "Release notes");
assertThat(markdown.getHeadings(2)).hasText("Getting started");

markdown.assertLinkCount(2);
markdown.assertLink("documentation", "https://vaadin.com/docs");

markdown.assertCodeBlockCount(1);
markdown.assertCodeBlockLanguage(0, "java");
markdown.assertCodeBlock(0, "ButtonElement.getByText(page, \"Save\").click();");
```

### Custom queries

Use `getRenderedLocator()` as the scope for anything the wrapper doesn't cover — the
rendered Markdown is plain HTML:

```java
MarkdownElement markdown = MarkdownElement.get(page);

assertThat(markdown.getRenderedLocator().locator("ul > li")).hasCount(3);
assertThat(markdown.getRenderedLocator().locator("br")).hasCount(1);
```

## Notes and Pitfalls

- **Rendered, not source.** There is no accessor for the Markdown source; assertions
  are made against the DOM the component produced.
- **Empty content is not visible.** A `<vaadin-markdown>` without content renders
  nothing, so it has no size and Playwright reports it as hidden. Assert
  `isAttached()` plus `assertText("")` instead of `assertVisible()`.
- **Soft line breaks.** With `setLineBreaks(true)` on the server side, single newlines
  render as `<br>`; by default they collapse into a space.
- **Inline code is not a code block.** `getCodeBlocks()` matches `pre > code` only.
- **Link names are exact and unique.** `getLink(String)` matches the accessible name
  exactly and does not pick a first match, so a name that is a substring of another
  link's name no longer resolves to the wrong link. Two links with the same name make
  the locator fail; use `getLinks().nth(int)` there.

## Related Elements

- `MessageListElement` — message content rendering
- `CardElement` — slot-based content container
