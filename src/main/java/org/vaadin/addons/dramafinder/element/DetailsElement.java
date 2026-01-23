package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;
import org.vaadin.addons.dramafinder.element.shared.HasTooltipElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-details>}.
 * <p>
 * Provides helpers to open/close and access the summary/content.
 */
@PlaywrightElement(DetailsElement.FIELD_TAG_NAME)
public class DetailsElement extends VaadinElement implements HasStyleElement, HasThemeElement,
        HasTooltipElement {

    /** The HTML tag name for this Vaadin component. */
    public static final String FIELD_TAG_NAME = "vaadin-details";

    /**
     * Create a new {@code DetailsElement}.
     *
     * @param locator the locator for the {@code <vaadin-details>} element
     */
    public DetailsElement(Locator locator) {
        super(locator);
    }

    /** Assert that the component is enabled. */
    public void assertEnabled() {
        assertThat(getLocator()).not().hasAttribute("disabled", "");
    }

    /** Assert that the component is disabled. */
    public void assertDisabled() {
        assertThat(getLocator()).hasAttribute("disabled", "");
    }

    /** Assert that the details is opened. */
    public void assertOpened() {
        assertThat(getLocator()).hasAttribute("opened", "");
    }

    /** Assert that the details is closed. */
    public void assertClosed() {
        assertThat(getLocator()).not().hasAttribute("opened", "");
    }

    /**
     * Whether the details is opened.
     *
     * @return {@code true} if the details is open
     */
    public boolean isOpen() {
        return getLocator().getAttribute("opened") != null;
    }

    /**
     * Set the opened state by clicking the summary when necessary.
     *
     * @param open {@code true} to open, {@code false} to close
     */
    public void setOpen(boolean open) {
        if (isOpen() != open) {
            getSummaryLocator().click();
        }
    }

    /**
     * Locator for the summary element.
     *
     * @return the summary locator
     */
    public Locator getSummaryLocator() {
        return getLocator().locator("vaadin-details-summary");
    }

    /**
     * Text of the summary element.
     *
     * @return the summary text
     */
    public String getSummaryText() {
        return getSummaryLocator().textContent();
    }


    /**
     * Locator for the currently visible content container.
     *
     * @return the content locator
     */
    public Locator getContentLocator() {
        return getLocator().locator("> div[aria-hidden='false']");
    }

    /**
     * Get a details component by its summary text.
     *
     * @param page    the Playwright page
     * @param summary the summary text to match
     * @return the matching {@code DetailsElement}
     */
    public static DetailsElement getBySummaryText(Page page, String summary) {
        return new DetailsElement(
                page.locator(FIELD_TAG_NAME).filter(
                        new Locator.FilterOptions().setHas(
                                page.locator("vaadin-details-summary", new Page.LocatorOptions().setHasText(summary))
                        )
                )
        );
    }

    /** Assert that the content is visible. */
    public void assertContentVisible() {
        assertThat(getContentLocator()).isVisible();
    }

    /** Assert that the content is not visible. */
    public void assertContentNotVisible() {
        assertThat(getContentLocator()).not().isVisible();
    }
}
