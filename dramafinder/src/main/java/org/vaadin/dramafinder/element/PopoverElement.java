package org.vaadin.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.dramafinder.element.shared.HasAriaLabelElement;
import org.vaadin.dramafinder.element.shared.HasStyleElement;
import org.vaadin.dramafinder.element.shared.HasThemeElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class PopoverElement extends VaadinElement implements HasThemeElement, HasStyleElement, HasAriaLabelElement {

    public static final String FIELD_TAG_NAME = "vaadin-popover-overlay";

    public PopoverElement(Page page) {
        // use xpath to exclude the shadow dom
        super(page.locator("//" + FIELD_TAG_NAME));
    }

    public PopoverElement(Locator locator) {
        super(locator);
    }

    public boolean isOpen() {
        return getLocator().isVisible();
    }

    public void assertOpen() {
        assertThat(getLocator()).hasAttribute("opened", "");
    }

    public void assertClosed() {
        assertThat(getLocator()).isHidden();
    }

    public Locator getContentLocator() {
        return getLocator();
    }


    public static PopoverElement getByLabel(Page page, String label) {
        return new PopoverElement(
                page.getByRole(AriaRole.DIALOG, new Page.GetByRoleOptions().setName(label))
                        .and(page.locator(FIELD_TAG_NAME))
        );
    }

}
