package org.vaadin.dramafinder.element;

import java.util.List;
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
    public static final String MULTIPLE_ATTRIBUTE = "multiple";

    public ListBoxElement(Locator locator) {
        super(locator);
    }

    /**
     * select the item based on the text.
     * In multiple mode, if the item is already selected unselect it
     *
     * @param item
     */
    public void selectItem(String item) {
        getItem(item).click();
    }

    public String getSingleSelectedValue() {
        return getLocatorValue().innerText();
    }

    public List<String> getSelectedValue() {
        return getLocatorValue().allTextContents();
    }

    private Locator getLocatorValue() {
        return getLocator().locator("vaadin-item[selected]");
    }

    public void assertSelectedValue(String... expected) {
        int length = expected.length;
        assertThat(getLocatorValue()).hasCount(length);
        // check that
        for (String value : expected) {
            assertThat(getItem(value)).hasAttribute("selected", "");
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
        assertThat(getItem(item)).isEnabled();
    }

    public void assertItemDisabled(String item) {
        assertThat(getItem(item)).isDisabled();
    }

    public boolean isMultiple() {
        return getLocator().getAttribute(MULTIPLE_ATTRIBUTE) != null;
    }

    public void assertMultiple() {
        assertThat(getLocator()).hasAttribute(MULTIPLE_ATTRIBUTE, "");
    }

    public void assertSingle() {
        assertThat(getLocator()).not().hasAttribute(MULTIPLE_ATTRIBUTE, "");
    }

    private Locator getItem(String label) {
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
