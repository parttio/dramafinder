package org.vaadin.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class DialogElement extends VaadinElement {

    public static final String FIELD_TAG_NAME = "vaadin-dialog-overlay";

    public DialogElement(Page page) {
        super(page.locator(FIELD_TAG_NAME));
    }

    public DialogElement(Locator locator) {
        super(locator);
    }

    public void closeWithEscape() {
        getLocator().press("Escape");
    }

    public boolean isOpen() {
        return getLocator().isVisible();
    }

    public void assertOpen() {
        assertThat(getLocator()).hasAttribute("opened", "");
    }

    public boolean isModal() {
        return getLocator().getAttribute("modeless") == null;
    }

    public void assertModal() {
        assertThat(getLocator()).not().hasAttribute("modeless", "");
    }

    public void assertModeless() {
        assertThat(getLocator()).hasAttribute("modeless", "");
    }

    public void assertClosed() {
        assertThat(getLocator()).isHidden();
    }

    public String getHeaderText() {
        return getLocator().locator("> [slot='title']").textContent();
    }

    public void assertHeaderText(String headerText) {
        assertThat(getLocator().locator("> [slot='title']")).hasText(headerText);
    }

    public Locator getHeaderLocator() {
        return getLocator().locator("> [slot='header-content']");
    }

    public Locator getContentLocator() {
        // using xpath to not pierce the shadow dom
        return getLocator().locator("xpath=./*[not(@slot)][1]");
    }

    public Locator getFooterLocator() {
        return getLocator().locator("> [slot='footer']");
    }

    public static DialogElement getByHeaderText(Page page, String summary) {
        return new DialogElement(
                page.getByRole(AriaRole.DIALOG, new Page.GetByRoleOptions().setName(summary))
                        .and(page.locator(FIELD_TAG_NAME))
        );
    }

}
