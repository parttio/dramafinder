package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
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
 * {@link #getByText(Page, String)} to disambiguate by text.
 */
public class NotificationElement extends VaadinElement implements HasThemeElement, HasStyleElement {

    public static final String FIELD_TAG_NAME = "vaadin-notification-card";

    /** CSS selector matching only currently-shown notification cards. */
    private static final String OPEN_CARD_SELECTOR = FIELD_TAG_NAME + "[slot]";

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

    /** Get an open notification by (a substring of) its text. */
    public static NotificationElement getByText(Page page, String text) {
        return new NotificationElement(
                page.locator(OPEN_CARD_SELECTOR)
                        .filter(new Locator.FilterOptions().setHasText(text))
        );
    }
}
