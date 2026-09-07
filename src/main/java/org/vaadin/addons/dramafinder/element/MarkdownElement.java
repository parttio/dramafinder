package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-markdown>}.
 * <p>
 * The component parses its {@code content} property as Markdown and renders the
 * resulting HTML into its own light DOM, which is then projected through the
 * default slot. This wrapper therefore works on the <em>rendered</em> output —
 * headings, links and code blocks are real DOM elements — and never on the
 * Markdown source. Headings and links are located by their ARIA roles
 * ({@code heading} and {@code link}); code blocks have no role and are located
 * as {@code pre > code}.
 */
@PlaywrightElement(MarkdownElement.FIELD_TAG_NAME)
public class MarkdownElement extends VaadinElement implements HasThemeElement, HasStyleElement {

    public static final String FIELD_TAG_NAME = "vaadin-markdown";

    /**
     * Create a new {@code MarkdownElement}.
     *
     * @param locator the locator for the {@code <vaadin-markdown>} element
     */
    public MarkdownElement(Locator locator) {
        super(locator);
    }

    // --- Rendered content ---

    /**
     * Locator for the root of the rendered Markdown output.
     * <p>
     * The rendered HTML is written into the light DOM of
     * {@code <vaadin-markdown>} itself, so the rendered root is the component
     * root. Use it as the scope for custom queries against the rendered
     * output.
     *
     * @return the locator scoping the rendered Markdown
     */
    public Locator getRenderedLocator() {
        return getLocator();
    }

    /**
     * Get the text of the rendered Markdown, with leading and trailing
     * whitespace removed.
     * <p>
     * This is the rendered text, not the Markdown source: for
     * {@code # Title} it returns {@code Title}, not {@code # Title}.
     *
     * @return the rendered text, or {@code null} if the element has no text
     */
    @Override
    public String getText() {
        String text = getRenderedLocator().textContent();
        return text == null ? null : text.trim();
    }

    /**
     * Assert that the rendered Markdown contains the given text.
     * <p>
     * The expected text is matched against the rendered output, so Markdown
     * syntax characters (such as {@code #} or {@code **}) are not part of it.
     *
     * @param expected the text expected to appear in the rendered output
     */
    public void assertContainsText(String expected) {
        assertThat(getRenderedLocator()).containsText(expected);
    }

    /**
     * Assert the full text of the rendered Markdown. Whitespace is normalized
     * before comparison.
     *
     * @param expected the expected rendered text
     */
    public void assertText(String expected) {
        assertThat(getRenderedLocator()).hasText(expected);
    }

    // --- Headings ---

    /**
     * Locator for every rendered heading ({@code h1}–{@code h6}), in document
     * order.
     *
     * @return locator matching all headings
     */
    public Locator getHeadings() {
        return getRenderedLocator().getByRole(AriaRole.HEADING);
    }

    /**
     * Locator for the rendered headings of a single level, in document order.
     *
     * @param level the heading level, 1 for {@code h1} through 6 for {@code h6}
     * @return locator matching the headings of that level
     */
    public Locator getHeadings(int level) {
        return getRenderedLocator().getByRole(AriaRole.HEADING,
                new Locator.GetByRoleOptions().setLevel(level));
    }

    /**
     * Locator for the rendered heading at the given index.
     *
     * @param index zero-based index among all headings
     * @return locator for that heading
     */
    public Locator getHeading(int index) {
        return getHeadings().nth(index);
    }

    /**
     * Assert that the rendered output contains exactly the expected number of
     * headings.
     *
     * @param count expected heading count
     */
    public void assertHeadingCount(int count) {
        assertThat(getHeadings()).hasCount(count);
    }

    /**
     * Assert the text of the rendered heading at the given index.
     *
     * @param index    zero-based index among all headings
     * @param expected expected heading text
     */
    public void assertHeading(int index, String expected) {
        assertThat(getHeading(index)).hasText(expected);
    }

    // --- Links ---

    /**
     * Locator for every rendered link, in document order.
     *
     * @return locator matching all links
     */
    public Locator getLinks() {
        return getRenderedLocator().getByRole(AriaRole.LINK);
    }

    /**
     * Locator for the rendered link with the given accessible name. The name is
     * matched exactly, so {@code getLink("docs")} does not match a link named
     * {@code API docs}.
     * <p>
     * The locator is strict: if several links share the accessible name, using
     * it fails instead of picking one of them. Use {@link #getLinks()} with
     * {@code nth(int)} to address one of a set of same-named links.
     *
     * @param text the link text, matched exactly
     * @return locator for the matching link
     */
    public Locator getLink(String text) {
        return getRenderedLocator().getByRole(AriaRole.LINK,
                new Locator.GetByRoleOptions().setName(text).setExact(true));
    }

    /**
     * Assert that the rendered output contains exactly the expected number of
     * links.
     *
     * @param count expected link count
     */
    public void assertLinkCount(int count) {
        assertThat(getLinks()).hasCount(count);
    }

    /**
     * Assert that the rendered link with the given text points to the given
     * target. The link text is matched exactly and must be unique within the
     * rendered output, see {@link #getLink(String)}.
     *
     * @param text the link text, matched exactly
     * @param href the expected {@code href} attribute value
     */
    public void assertLink(String text, String href) {
        assertThat(getLink(text)).hasAttribute("href", href);
    }

    // --- Code blocks ---

    /**
     * Locator for every rendered fenced or indented code block, in document
     * order. Inline code ({@code `code`}) is not matched, only
     * {@code pre > code}.
     *
     * @return locator matching all code blocks
     */
    public Locator getCodeBlocks() {
        return getRenderedLocator().locator("pre > code");
    }

    /**
     * Locator for the rendered code block at the given index.
     *
     * @param index zero-based index among all code blocks
     * @return locator for that code block
     */
    public Locator getCodeBlock(int index) {
        return getCodeBlocks().nth(index);
    }

    /**
     * Assert that the rendered output contains exactly the expected number of
     * code blocks.
     *
     * @param count expected code block count
     */
    public void assertCodeBlockCount(int count) {
        assertThat(getCodeBlocks()).hasCount(count);
    }

    /**
     * Assert the text of the rendered code block at the given index.
     * Whitespace is normalized before comparison.
     *
     * @param index    zero-based index among all code blocks
     * @param expected expected code block text
     */
    public void assertCodeBlock(int index, String expected) {
        assertThat(getCodeBlock(index)).hasText(expected);
    }

    /**
     * Assert the language of the rendered code block at the given index. A
     * fenced block such as {@code ```java} is rendered with the class
     * {@code language-java}.
     *
     * @param index    zero-based index among all code blocks
     * @param language expected language, without the {@code language-} prefix
     */
    public void assertCodeBlockLanguage(int index, String language) {
        assertThat(getCodeBlock(index)).hasClass("language-" + language);
    }

    // --- Factory methods ---

    /**
     * Get the first {@code <vaadin-markdown>} on the page.
     *
     * @param page the Playwright page
     * @return the first {@code MarkdownElement}
     */
    public static MarkdownElement get(Page page) {
        return new MarkdownElement(page.locator(FIELD_TAG_NAME).first());
    }

    /**
     * Get the first {@code <vaadin-markdown>} within a locator scope.
     *
     * @param locator the scope to search within
     * @return the first {@code MarkdownElement}
     */
    public static MarkdownElement get(Locator locator) {
        return new MarkdownElement(locator.locator(FIELD_TAG_NAME).first());
    }
}
