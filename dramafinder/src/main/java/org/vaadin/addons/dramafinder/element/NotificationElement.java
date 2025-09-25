package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class NotificationElement extends VaadinElement implements HasThemeElement, HasStyleElement {

    public static final String FIELD_TAG_NAME = "vaadin-notification-card";

    public NotificationElement(Page page) {
        super(page.locator(FIELD_TAG_NAME));
    }

    public NotificationElement(Locator locator) {
        super(locator);
    }

    public boolean isOpen() {
        return getLocator().isVisible();
    }

    public void assertOpen() {
        assertThat(getLocator()).isVisible();
    }

    public void assertClosed() {
        assertThat(getLocator()).isHidden();
    }

    public Locator getContentLocator() {
        return getLocator();
    }
}
