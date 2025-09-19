package org.vaadin.dramafinder.element;

import java.util.regex.Pattern;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.dramafinder.element.shared.HasAriaLabelElement;
import org.vaadin.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.dramafinder.element.shared.HasStyleElement;
import org.vaadin.dramafinder.element.shared.HasTooltipElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@PlaywrightElement(ListBoxElement.FIELD_TAG_NAME)
public class ListBoxElement extends VaadinElement
        implements HasAriaLabelElement, HasStyleElement, HasTooltipElement,
        HasEnabledElement {

    public static final String FIELD_TAG_NAME = "vaadin-list-box";
    public static final String FIELD_ITEM_TAG_NAME = "vaadin-item";

    public ListBoxElement(Locator locator) {
        super(locator);
    }

    public void selectItem(String item) {
        getLocator().locator("vaadin-item:has-text(\"" + item + "\")").click();
    }

    public String getSelectedValue() {
        return getLocator().locator("vaadin-item[selected]").innerText();
    }

    public void assertSelectedValue(String expected) {
        if (expected == null) {
            throw new AssertionError("Expected value cannot be null");
        }
        String actual = getSelectedValue();
        if (!expected.equals(actual)) {
            throw new AssertionError(String.format("Expected value to be '%s' but was '%s'", expected, actual));
        }
    }

    @Override
    public boolean isEnabled() {
        return getEnabledLocator().isEnabled();
    }

    @Override
    public void assertEnabled() {
        assertThat(getLocator()).not().hasAttribute("disabled", Pattern.compile(".*"));
    }

    @Override
    public void assertDisabled() {
        assertThat(getLocator()).hasAttribute("disabled", "");
    }

    public void assertItemEnabled(String item) {
        assertThat(getItem(locator, item)).isEnabled();
    }

    public void assertItemDisabled(String item) {
        assertThat(getItem(locator, item)).isDisabled();
    }

    private static Locator getItem(Locator locator, String label) {
        return locator.locator(FIELD_ITEM_TAG_NAME)
                .filter(new Locator.FilterOptions()
                        .setHasText(label)).first();
    }


    public static ListBoxElement getByLabel(Page page, String label) {
        return new ListBoxElement(
                page.locator(FIELD_TAG_NAME)
                        .and(page.getByRole(AriaRole.LISTBOX,
                                new Page.GetByRoleOptions().setName(label))
                        ).first());
    }
}
