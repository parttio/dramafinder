package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.shared.HasAriaLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-popover>}.
 */
public class PopoverElement extends VaadinElement implements HasThemeElement, HasStyleElement, HasAriaLabelElement {

    public static final String FIELD_TAG_NAME = "vaadin-popover";

    /** Create a {@code PopoverElement} by resolving the dialog with ARIA role. */
    public PopoverElement(Page page) {
        super(
                page.getByRole(AriaRole.DIALOG)
                        .and(page.locator(FIELD_TAG_NAME)));
    }

    /** Create a {@code PopoverElement} from an existing locator. */
    public PopoverElement(Locator locator) {
        super(locator);
    }

    /** Whether the popover is open (visible). */
    public boolean isOpen() {
        return getLocator().isVisible();
    }

    /** Assert that the popover is open. */
    public void assertOpen() {
        assertThat(getLocator()).hasAttribute("opened", "");
    }

    /** Assert that the popover is closed (hidden). */
    public void assertClosed() {
        assertThat(getLocator()).isHidden();
    }

    /** Locator for the popover content. */
    public Locator getContentLocator() {
        return getLocator();
    }


    /** Get a popover by its accessible label. */
    public static PopoverElement getByLabel(Page page, String label) {
        return new PopoverElement(
                page.getByRole(AriaRole.DIALOG, new Page.GetByRoleOptions().setName(label))
                        .and(page.locator(FIELD_TAG_NAME))
        );
    }

}
