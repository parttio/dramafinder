package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-card>}.
 * <p>
 * Exposes slot-aware accessors (title, subtitle, header/footer, media) and
 * lookup helpers based on the Card's ARIA {@code region} name (title).
 */
@PlaywrightElement(CardElement.FIELD_TAG_NAME)
public class CardElement extends VaadinElement implements HasThemeElement, HasStyleElement {

    public static final String FIELD_TAG_NAME = "vaadin-card";

    /**
     * Create a new {@code CardElement}.
     *
     * @param locator the locator for the {@code <vaadin-card>} element
     */
    public CardElement(Locator locator) {
        super(locator);
    }

    /**
     * Get a card by title
     *
     * @param page  the Playwright page
     * @param title the card's accessible name or title text
     * @return the matching {@code CardElement}
     */
    public static CardElement getByTitle(Page page, String title) {
        return new CardElement(
                page.locator(FIELD_TAG_NAME).filter(
                        new Locator.FilterOptions().setHas(
                                page.locator("[slot='title']", new Page.LocatorOptions().setHasText(title))
                        ))
        );
    }

    /**
     * Get a card by title
     *
     * @param locator the scope to search within
     * @param title   the card's accessible name or title text
     * @return the matching {@code CardElement}
     */
    public static CardElement getByTitle(Locator locator, String title) {
        return new CardElement(
                locator.locator(FIELD_TAG_NAME).filter(
                        new Locator.FilterOptions().setHas(
                                locator.page().locator("[slot='title']", new Page.LocatorOptions().setHasText(title))
                        ))
        );
    }

    /**
     * Locator for the title slot.
     */
    public Locator getTitleLocator() {
        return getLocator().locator("> [slot='title']");
    }

    /**
     * Locator for the subtitle slot.
     */
    public Locator getSubtitleLocator() {
        return getLocator().locator("> [slot='subtitle']");
    }

    /**
     * Locator for the header slot.
     */
    public Locator getHeaderLocator() {
        return getLocator().locator("> [slot='header']");
    }

    /**
     * Locator for the header prefix slot.
     */
    public Locator getHeaderPrefixLocator() {
        return getLocator().locator("> [slot='header-prefix']");
    }

    /**
     * Locator for the header suffix slot.
     */
    public Locator getHeaderSuffixLocator() {
        return getLocator().locator("> [slot='header-suffix']");
    }

    /**
     * Locator for the media slot.
     */
    public Locator getMediaLocator() {
        return getLocator().locator("> [slot='media']");
    }

    /**
     * Locator for the footer slot.
     */
    public Locator getFooterLocator() {
        return getLocator().locator(" > [slot='footer']");
    }

    /**
     * Locator for the default (content) slot.
     */
    public Locator getContentLocator() {
        return getLocator().locator("xpath=./*[not(@slot)][1]");
    }

    /**
     * Assert the card title text, or absence when {@code null}.
     */
    public void assertTitle(String title) {
        if (title != null) {
            assertThat(getTitleLocator()).hasText(title);
        } else {
            assertThat(getTitleLocator()).hasCount(0);
        }
    }

    /**
     * Assert the card subtitle text, or absence when {@code null}.
     */
    public void assertSubtitle(String subtitle) {
        if (subtitle != null) {
            assertThat(getSubtitleLocator()).hasText(subtitle);
        } else {
            assertThat(getSubtitleLocator()).hasCount(0);
        }
    }
}
