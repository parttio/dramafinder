# Markdown

`MarkdownElement` wraps `<vaadin-markdown>` (Flow: `Markdown`). It asserts the
**rendered** output: text, headings, links and code blocks.

| | |
|---|---|
| Web component | `<vaadin-markdown>` |
| Flow component | `com.vaadin.flow.component.markdown.Markdown` |
| Wrapper | `org.vaadin.addons.dramafinder.element.MarkdownElement` |
| Typed query | `find(page, MarkdownElement.class)` → `ComponentQuery` |
| API | [MarkdownElement](../../skills/vaadin-playwright-test/api-reference.md#markdownelement) |

The component parses its `content` property as Markdown and writes the resulting
HTML into its **own light DOM**, which is projected through the default slot in
the shadow root. Everything this wrapper exposes therefore describes the
**rendered output** — `# Title` is asserted as the text `Title` inside an `<h1>`,
never as the source string `# Title`. There is no accessor for the Markdown
source.

Headings and links are located by their ARIA roles (`heading` and `link`). Code
blocks have no ARIA role and are located structurally as `pre > code`, which
deliberately excludes inline code (`` `code` ``).

## Finding the component

```java
MarkdownElement notes = MarkdownElement.getById(page, "release-notes");
MarkdownElement first = MarkdownElement.get(page);                    // first on the page
MarkdownElement scoped = MarkdownElement.get(page.locator("#help-panel"));
MarkdownElement byTestId = MarkdownElement.getByTestId(page, "changelog");
```

The typed query has the filters every component has:

```java
import static org.vaadin.addons.dramafinder.element.query.ElementQueries.find;

MarkdownElement help = find(page, MarkdownElement.class).withClassName("help-text").single();
```

Without an id or test id, a page with several Markdown components can still be
addressed by position: `new MarkdownElement(page.locator(MarkdownElement.FIELD_TAG_NAME).nth(1))`.

## Rendered text

````java
// View
Markdown markdown = new Markdown("""
        # Release notes

        DramaFinder is a **Playwright** helper library for Vaadin.
        See the [documentation](https://vaadin.com/docs) and the
        [add-on page](https://vaadin.com/directory) for details.

        ## Getting started

        ```java
        ButtonElement.getByText(page, "Save").click();
        ```

        ### Notes

        Use `assertContainsText` for partial matches.
        """);
````

```java
// Test
MarkdownElement markdown = MarkdownElement.get(page);
markdown.assertContainsText("Getting started");                 // not "## Getting started"
markdown.assertContainsText("DramaFinder is a Playwright helper");  // no ** in the output

String text = markdown.getText();   // "Release notes\n...", trimmed; null without text
```

`assertContainsText` checks a part of the rendered text; `assertText` the whole
of it, with whitespace normalized. Both retry. `getText()` overrides
`VaadinElement.getText()`, which returns the raw `textContent`, to return the
rendered text with leading and trailing whitespace removed.

## Headings

```java
markdown.assertHeadingCount(3);
markdown.assertHeading(0, "Release notes");
markdown.assertHeading(1, "Getting started");
markdown.assertHeading(2, "Notes");

assertThat(markdown.getHeadings(2)).hasText("Getting started");   // all h2s
assertThat(markdown.getHeading(0)).hasText("Release notes");
assertThat(markdown.getHeadings()).hasCount(3);                    // h1–h6, in order
```

`getHeadings(int level)` takes a level from 1 to 6.

## Links

```java
markdown.assertLinkCount(2);
markdown.assertLink("documentation", "https://vaadin.com/docs");
markdown.assertLink("add-on page", "https://vaadin.com/directory");

assertThat(markdown.getLink("documentation")).hasText("documentation");
markdown.getLink("documentation").click();
```

The link name is matched **exactly**, so `getLink("docs")` does not match a link
named `API docs`, and it is not disambiguated with `.first()`: several links with
the same accessible name is an ambiguity in the test, and Playwright's strict mode
reports it instead of picking an arbitrary one. To address one of a set of
same-named links, use `getLinks().nth(int)`:

```java
// View
Markdown markdown = new Markdown("""
        Read the [API docs](https://vaadin.com/api) or just the
        [docs](https://vaadin.com/docs).

        Two links share a name: [guide](https://vaadin.com/guide/a)
        and [guide](https://vaadin.com/guide/b).
        """);
```

```java
// Test
markdown.assertLink("docs", "https://vaadin.com/docs");      // not "API docs"
markdown.assertLink("API docs", "https://vaadin.com/api");
assertThat(markdown.getLink("API")).hasCount(0);             // a fragment matches nothing

Locator guides = markdown.getLink("guide");                   // matches two links
assertThat(guides).hasCount(2);
assertThat(guides.nth(1)).hasAttribute("href", "https://vaadin.com/guide/b");
// markdown.assertLink("guide", ...) would fail with a strict-mode error
```

## Code blocks

```java
markdown.assertCodeBlockCount(1);    // the inline `assertContainsText` is not counted
markdown.assertCodeBlock(0, "ButtonElement.getByText(page, \"Save\").click();");
markdown.assertCodeBlockLanguage(0, "java");   // the ```java fence → class "language-java"

assertThat(markdown.getCodeBlock(0)).containsText("getByText");
assertThat(markdown.getCodeBlocks()).hasCount(1);
```

`assertCodeBlock` normalizes whitespace before comparing. `getCodeBlocks()`
matches fenced and indented blocks (`pre > code`), never inline code.

## Custom queries

The rendered Markdown is plain HTML; use `getRenderedLocator()` as the scope for
anything the wrapper doesn't cover. It is the component root, because the
rendered HTML lives in its light DOM — so a heading placed next to the component
in the view is not matched:

```java
assertThat(markdown.getRenderedLocator().locator("h2")).hasText("Getting started");
assertThat(markdown.getRenderedLocator().locator("strong")).hasText("Playwright");
```

## Line breaks

With `setLineBreaks(true)` on the server side, single newlines render as `<br>`;
by default they collapse into a space:

```java
// View
Markdown markdown = new Markdown("""
        First line
        Second line
        """);
markdown.setLineBreaks(true);
```

```java
// Test
markdown.assertContainsText("First line");
markdown.assertContainsText("Second line");
assertThat(markdown.getRenderedLocator().locator("br")).hasCount(1);
```

## Empty content

A `<vaadin-markdown>` without content renders nothing, so it has no size and
Playwright reports it as hidden. Assert that it is attached and empty instead of
calling `assertVisible()`:

```java
// View
add(new Markdown());
```

```java
// Test
assertThat(markdown.getLocator()).isAttached();
markdown.assertText("");
markdown.assertHeadingCount(0);
markdown.assertLinkCount(0);
markdown.assertCodeBlockCount(0);
```

## Theme and style

```java
markdown.assertCssClass("help-text");     // markdown.addClassName("help-text")
markdown.assertTheme(null);               // no theme attribute
```

## Pitfalls

- **Rendered, not source.** Assertions are made against the DOM the component
  produced; Markdown syntax characters are never part of the text.
- **Empty content is not visible.** Use `isAttached()` plus `assertText("")`.
- **Soft line breaks** collapse into a space unless `setLineBreaks(true)`.
- **Inline code is not a code block.** `getCodeBlocks()` matches `pre > code` only.
- **Link names are exact and unique.** A name that is a substring of another
  link's name does not resolve to the wrong link; two links with the same name
  make the locator fail. Use `getLinks().nth(int)` there.

## See also

- [Message List](MessageListElement.md)
- [Card](CardElement.md) — slot-based content container
- [Assertions](../guide/assertions.md)
- [Finding components](../guide/finding-components.md)
- [Core concepts](../guide/core-concepts.md)
- Full example: [MarkdownViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/MarkdownViewIT.java)
