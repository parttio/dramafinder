package org.vaadin.addons.dramafinder.tests.it;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.PlaywrightException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.HasTestView;
import org.vaadin.addons.dramafinder.element.MarkdownElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MarkdownViewIT extends SpringPlaywrightIT implements HasTestView {

    @Override
    public String getView() {
        return "markdown";
    }

    private MarkdownElement basicMarkdown() {
        return MarkdownElement.get(page);
    }

    private MarkdownElement overlappingLinkNamesMarkdown() {
        return new MarkdownElement(page.locator(MarkdownElement.FIELD_TAG_NAME).nth(1));
    }

    private MarkdownElement lineBreaksMarkdown() {
        return new MarkdownElement(page.locator(MarkdownElement.FIELD_TAG_NAME).nth(2));
    }

    private MarkdownElement emptyMarkdown() {
        return new MarkdownElement(page.locator(MarkdownElement.FIELD_TAG_NAME).nth(3));
    }

    @Test
    public void testRenderedTextIsNotTheMarkdownSource() {
        MarkdownElement markdown = basicMarkdown();
        markdown.assertVisible();

        String text = markdown.getText();
        assertTrue(text.startsWith("Release notes"),
                "Expected the rendered text to start with the heading text but was: " + text);
        assertFalse(text.contains("#"),
                "Expected the Markdown syntax to be rendered away but was: " + text);
        assertFalse(text.contains("**"),
                "Expected the Markdown syntax to be rendered away but was: " + text);

        markdown.assertContainsText("Getting started");
    }

    @Test
    public void testRenderedLocatorScopesTheOutput() {
        MarkdownElement markdown = basicMarkdown();
        // The H2 example title lives outside the component and must not match.
        assertThat(markdown.getRenderedLocator().locator("h2")).hasCount(1);
        assertThat(markdown.getRenderedLocator().locator("h2")).hasText("Getting started");
    }

    @Test
    public void testHeadings() {
        MarkdownElement markdown = basicMarkdown();

        markdown.assertHeadingCount(3);
        markdown.assertHeading(0, "Release notes");
        markdown.assertHeading(1, "Getting started");
        markdown.assertHeading(2, "Notes");

        assertThat(markdown.getHeadings(1)).hasText("Release notes");
        assertThat(markdown.getHeadings(2)).hasText("Getting started");
        assertThat(markdown.getHeadings(3)).hasText("Notes");
    }

    @Test
    public void testLinks() {
        MarkdownElement markdown = basicMarkdown();

        markdown.assertLinkCount(2);
        markdown.assertLink("documentation", "https://vaadin.com/docs");
        markdown.assertLink("add-on page", "https://vaadin.com/directory");
        assertThat(markdown.getLink("documentation")).hasText("documentation");
    }

    @Test
    public void testLinkNameIsMatchedExactly() {
        MarkdownElement markdown = overlappingLinkNamesMarkdown();

        // "docs" is a substring of "API docs", so a substring match would
        // resolve both names to the same, wrong link.
        markdown.assertLink("docs", "https://vaadin.com/docs");
        markdown.assertLink("API docs", "https://vaadin.com/api");
        assertThat(markdown.getLink("docs")).hasText("docs");
        assertThat(markdown.getLink("docs")).hasCount(1);

        // A name that is only a substring of a link's name matches nothing.
        assertThat(markdown.getLink("API")).hasCount(0);
    }

    @Test
    public void testAmbiguousLinkNameFailsInsteadOfPickingOne() {
        MarkdownElement markdown = overlappingLinkNamesMarkdown();

        markdown.assertLinkCount(4);

        // Both links named "guide" are matched: the ambiguity is not hidden
        // behind an arbitrary first match.
        Locator guides = markdown.getLink("guide");
        assertThat(guides).hasCount(2);

        // Acting on the ambiguous locator fails loudly (strict mode) instead of
        // reporting the state of one arbitrarily chosen link.
        assertThrows(PlaywrightException.class, () -> guides.getAttribute("href"));

        // Same-named links are addressed by index instead.
        assertThat(guides.nth(0)).hasAttribute("href", "https://vaadin.com/guide/a");
        assertThat(guides.nth(1)).hasAttribute("href", "https://vaadin.com/guide/b");
    }

    @Test
    public void testCodeBlocks() {
        MarkdownElement markdown = basicMarkdown();

        // Inline code is not a code block, only the fenced block counts.
        markdown.assertCodeBlockCount(1);
        markdown.assertCodeBlock(0, "ButtonElement.getByText(page, \"Save\").click();");
        markdown.assertCodeBlockLanguage(0, "java");
    }

    @Test
    public void testLineBreaks() {
        MarkdownElement markdown = lineBreaksMarkdown();

        markdown.assertContainsText("First line");
        markdown.assertContainsText("Second line");
        assertThat(markdown.getRenderedLocator().locator("br")).hasCount(1);
    }

    @Test
    public void testEmptyMarkdown() {
        MarkdownElement markdown = emptyMarkdown();

        // Without content the component renders nothing and therefore has no
        // size, so it is attached but not visible.
        assertThat(markdown.getLocator()).isAttached();
        markdown.assertText("");
        markdown.assertHeadingCount(0);
        markdown.assertLinkCount(0);
        markdown.assertCodeBlockCount(0);
    }
}
