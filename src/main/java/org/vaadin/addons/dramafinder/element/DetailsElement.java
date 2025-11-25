package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;
import org.vaadin.addons.dramafinder.element.shared.HasTooltipElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@PlaywrightElement(DetailsElement.FIELD_TAG_NAME)
public class DetailsElement extends VaadinElement implements HasStyleElement, HasThemeElement,
        HasTooltipElement {

    public static final String FIELD_TAG_NAME = "vaadin-details";

    public DetailsElement(Locator locator) {
        super(locator);
    }

    public void assertEnabled() {
        assertThat(getLocator()).not().hasAttribute("disabled", "");
    }

    public void assertDisabled() {
        assertThat(getLocator()).hasAttribute("disabled", "");
    }

    public void assertOpened() {
        assertThat(getLocator()).hasAttribute("opened", "");
    }

    public void assertClosed() {
        assertThat(getLocator()).not().hasAttribute("opened", "");
    }

    public boolean isOpen() {
        return getLocator().getAttribute("opened") != null;
    }

    public void setOpen(boolean open) {
        if (isOpen() != open) {
            getSummaryLocator().click();
        }
    }

    public Locator getSummaryLocator() {
        return getLocator().locator("vaadin-details-summary");
    }

    public String getSummaryText() {
        return getSummaryLocator().textContent();
    }


    public Locator getContentLocator() {
        return getLocator().locator("> div[aria-hidden='false']");
    }

    public static DetailsElement getBySummaryText(Page page, String summary) {
        return new DetailsElement(
                page.locator(FIELD_TAG_NAME).filter(
                        new Locator.FilterOptions().setHas(
                                page.locator("vaadin-details-summary", new Page.LocatorOptions().setHasText(summary))
                        )
                )
        );
    }

    public void assertContentVisible() {
        assertThat(getContentLocator()).isVisible();
    }

    public void assertContentNotVisible() {
        assertThat(getContentLocator()).not().isVisible();
    }
}
