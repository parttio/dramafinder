package org.vaadin.addons.dramafinder.element;

import java.util.regex.Pattern;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.shared.HasDisabledAttributeElement;
import org.vaadin.addons.dramafinder.element.shared.HasPrefixElement;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-breadcrumbs-item>}, a single entry of a
 * {@link BreadcrumbsElement} trail.
 * <p>
 * An item renders one of two mutually exclusive shadow parts: {@code link} (an
 * {@code <a>}, when the item has a {@code path}) or {@code nolink} (a
 * {@code <span>} carrying {@code aria-current="page"} when the item is the
 * current page). The state attributes {@code current}, {@code disabled} and
 * {@code has-prefix} live on the host element.
 */
@PlaywrightElement(BreadcrumbsItemElement.FIELD_TAG_NAME)
public class BreadcrumbsItemElement extends VaadinElement
        implements HasDisabledAttributeElement, HasPrefixElement, HasStyleElement {

    public static final String FIELD_TAG_NAME = "vaadin-breadcrumbs-item";

    private static final String LINK_PART = "[part='link']";
    private static final String NOLINK_PART = "[part='nolink']";

    /**
     * Create a new {@code BreadcrumbsItemElement}.
     *
     * @param locator the locator for the {@code <vaadin-breadcrumbs-item>} element
     */
    public BreadcrumbsItemElement(Locator locator) {
        super(locator);
    }

    // --- Content ---

    /**
     * Assert that the item shows the expected text.
     *
     * @param text the expected text
     */
    public void assertText(String text) {
        assertThat(getLocator()).hasText(text);
    }

    /**
     * Get the path the item links to.
     *
     * @return the {@code path} attribute, or {@code null} when the item has no
     *         path and therefore renders as a non-link
     */
    public String getPath() {
        return getLocator().getAttribute("path");
    }

    /**
     * Assert the path the item links to.
     *
     * @param path the expected path, or {@code null} to assert that the item
     *             has no path
     */
    public void assertPath(String path) {
        if (path != null) {
            assertThat(getLocator()).hasAttribute("path", path);
        } else {
            assertThat(getLocator()).not().hasAttribute("path", Pattern.compile(".*"));
        }
    }

    // --- Link state ---

    /**
     * Locator for the {@code <a part="link">} rendered when the item has a path.
     *
     * @return locator for the link part, matching nothing on a non-link item
     */
    public Locator getLinkLocator() {
        return getLocator().locator(LINK_PART);
    }

    /**
     * Whether the item renders as a link, which is the case when it has a path.
     *
     * @return {@code true} when the item has a {@code [part='link']} anchor
     */
    public boolean isLink() {
        return getLinkLocator().count() > 0;
    }

    /**
     * Assert that the item renders as a link.
     */
    public void assertLink() {
        assertThat(getLinkLocator()).hasCount(1);
    }

    /**
     * Assert that the item renders as a non-link.
     */
    public void assertNotLink() {
        assertThat(getLinkLocator()).hasCount(0);
    }

    /**
     * Click the item. A link item is clicked on its {@code [part='link']}
     * anchor so that the click navigates; a non-link item is clicked on the
     * host element.
     */
    @Override
    public void click() {
        if (isLink()) {
            getLinkLocator().click();
        } else {
            super.click();
        }
    }

    // --- Current state ---

    /**
     * Whether the item represents the current page.
     *
     * @return {@code true} when the {@code current} attribute is set
     */
    public boolean isCurrent() {
        return getLocator().getAttribute("current") != null;
    }

    /**
     * Assert that the item represents the current page, both through the
     * {@code current} host attribute and {@code aria-current="page"} on its
     * {@code [part='nolink']} element.
     */
    public void assertCurrent() {
        assertThat(getLocator()).hasAttribute("current", "");
        assertThat(getLocator().locator(NOLINK_PART)).hasAttribute("aria-current", "page");
    }

    /**
     * Assert that the item does not represent the current page.
     */
    public void assertNotCurrent() {
        assertThat(getLocator()).not().hasAttribute("current", "");
    }

    // --- Prefix ---

    /**
     * Whether the item has content in its prefix slot.
     *
     * @return {@code true} when the {@code has-prefix} attribute is set
     */
    public boolean hasPrefix() {
        return getLocator().getAttribute("has-prefix") != null;
    }

    /**
     * Assert that the item has content in its prefix slot.
     */
    public void assertHasPrefix() {
        assertThat(getLocator()).hasAttribute("has-prefix", "");
    }

    /**
     * Assert that the item has no content in its prefix slot.
     */
    public void assertHasNoPrefix() {
        assertThat(getLocator()).not().hasAttribute("has-prefix", "");
    }

    // --- Factory methods ---

    /**
     * Get the first {@code <vaadin-breadcrumbs-item>} with the given text on the page.
     *
     * @param page the Playwright page
     * @param text the exact text of the item
     * @return the matching {@code BreadcrumbsItemElement}
     */
    public static BreadcrumbsItemElement getByText(Page page, String text) {
        return new BreadcrumbsItemElement(page.locator(FIELD_TAG_NAME)
                .and(page.getByText(text, new Page.GetByTextOptions().setExact(true))).first());
    }

    /**
     * Get the first {@code <vaadin-breadcrumbs-item>} with the given text within a
     * locator scope.
     *
     * @param locator the scope to search within
     * @param text    the exact text of the item
     * @return the matching {@code BreadcrumbsItemElement}
     */
    public static BreadcrumbsItemElement getByText(Locator locator, String text) {
        return new BreadcrumbsItemElement(locator.locator(FIELD_TAG_NAME)
                .and(locator.getByText(text, new Locator.GetByTextOptions().setExact(true))).first());
    }
}
