package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for notification cards {@code <vaadin-notification-card>}.
 */
public class NotificationElement extends VaadinElement implements HasThemeElement, HasStyleElement {

    public static final String FIELD_TAG_NAME = "vaadin-notification-card";

    /** Create a {@code NotificationElement} from the page. */
    public NotificationElement(Page page) {
        super(page.locator(FIELD_TAG_NAME));
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
}
