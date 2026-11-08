package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.query.TextQuery;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for notification cards {@code <vaadin-notification-card>}.
 * <p>
 * Only cards that are currently shown carry a {@code slot} attribute: when a
 * notification opens, Vaadin moves its card into the
 * {@code <vaadin-notification-container>} and sets {@code card.slot}; closed and
 * never-opened cards have no slot (and live in their host shadow root or are
 * detached). The locator is therefore scoped to {@code [slot]} so that closed
 * notifications are excluded.
 * <p>
 * The no-arg-content {@link #NotificationElement(Page) constructor} targets the
 * single open notification. When several notifications are open at once, use
 * {@link #getByText(Page, String)} to disambiguate by text, or the typed query
 * {@code find(page, NotificationElement.class)}, which searches the same open
 * cards and combines {@code withText} / {@code withTextContaining} (the card's
 * text) with the common filters, such as {@code withClassName} or
 * {@code withAttribute("theme", "error")}. Flow puts the notification's id and
 * test id on {@code vaadin-notification}, not on the card, so {@code withId}
 * and {@code withTestId} do not match a notification.
 */
@PlaywrightElement(NotificationElement.OPEN_CARD_SELECTOR)
public class NotificationElement extends VaadinElement implements HasThemeElement, HasStyleElement,
        Findable<TextQuery<NotificationElement>> {

    public static final String FIELD_TAG_NAME = "vaadin-notification-card";

    /**
     * CSS selector matching only currently-shown notification cards; also the
     * selector the typed query searches. A literal, so the API reference
     * generator can read it from the annotation.
     */
    static final String OPEN_CARD_SELECTOR = "vaadin-notification-card[slot]";

    /** Create a {@code NotificationElement} for the open notification on the page. */
    public NotificationElement(Page page) {
        super(page.locator(OPEN_CARD_SELECTOR));
    }

    /** Create a {@code NotificationElement} from an existing locator. */
    public NotificationElement(Locator locator) {
        super(locator);
    }

    /** Whether the notification is open (visible). */
    public boolean isOpen() {
        return getLocator().isVisible();
    }

    /** Assert that the notification is open. */
    public void assertOpen() {
        assertThat(getLocator()).isVisible();
    }

    /** Assert that the notification is closed. */
    public void assertClosed() {
        assertThat(getLocator()).isHidden();
    }

    /** Locator for the notification content. */
    public Locator getContentLocator() {
        return getLocator();
    }

    /** Assert that the notification contains the given text. */
    public void assertContent(String content) {
        assertThat(getContentLocator()).containsText(content);
    }

    /**
     * Get an open notification by its text, matched case-insensitively as a
     * substring. Using the element fails in strict mode when several open
     * notifications contain {@code text}. For an exact match, use
     * {@code find(page, NotificationElement.class).withText(text)}.
     *
     * @param page the Playwright page
     * @param text part of the notification text
     * @return the open notification containing {@code text}
     */
    public static NotificationElement getByText(Page page, String text) {
        return new NotificationElement(
                page.locator(OPEN_CARD_SELECTOR)
                        .filter(new Locator.FilterOptions().setHasText(text))
        );
    }
}
