package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.shared.FocusableElement;
import org.vaadin.addons.dramafinder.element.shared.HasAriaLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.addons.dramafinder.element.shared.HasHelperElement;
import org.vaadin.addons.dramafinder.element.shared.HasLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasValidationPropertiesElement;
import org.vaadin.addons.dramafinder.element.shared.HasValueElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-checkbox>}.
 * <p>
 * Provides helpers to read and modify checked/indeterminate state and
 * access common mixins for label, helper, validation and enablement.
 */
@PlaywrightElement(CheckboxElement.FIELD_TAG_NAME)
public class CheckboxElement extends VaadinElement
        implements FocusableElement, HasAriaLabelElement, HasEnabledElement,
        HasHelperElement, HasValueElement, HasStyleElement, HasLabelElement, HasValidationPropertiesElement {

    public static final String FIELD_TAG_NAME = "vaadin-checkbox";

    /**
     * Create a new {@code CheckboxElement}.
     *
     * @param locator the locator for the {@code <vaadin-checkbox>} element
     */
    public CheckboxElement(Locator locator) {
        super(locator);
    }

    @Override
    public Locator getEnabledLocator() {
        return getInputLocator();
    }

    @Override
    public Locator getAriaLabelLocator() {
        return getInputLocator();
    }

    @Override
    public Locator getFocusLocator() {
        return getInputLocator();
    }

    /**
     * Whether the checkbox is currently checked.
     *
     * @return {@code true} if checked
     */
    public boolean isChecked() {
        return getInputLocator().isChecked();
    }

    /**
     * Assert that the checkbox is checked.
     */
    public void assertChecked() {
        assertThat(getInputLocator()).isChecked();
    }

    /**
     * Assert that the checkbox is not checked.
     */
    public void assertNotChecked() {
        assertThat(getInputLocator()).not().isChecked();
    }

    /**
     * Check the checkbox.
     */
    public void check() {
        getInputLocator().check();
    }

    /**
     * Uncheck the checkbox.
     */
    public void uncheck() {
        getInputLocator().uncheck();
    }

    /**
     * Whether the checkbox is in indeterminate state.
     *
     * @return {@code true} when indeterminate
     */
    public boolean isIndeterminate() {
        return getLocator().getAttribute("indeterminate") != null;
    }

    /**
     * Assert that the checkbox is indeterminate.
     */
    public void assertIndeterminate() {
        assertThat(getLocator()).hasAttribute("indeterminate", "");
    }

    /**
     * Assert that the checkbox is not indeterminate.
     */
    public void assertNotIndeterminate() {
        assertThat(getLocator()).not().hasAttribute("indeterminate", "");
    }

    /**
     * Set the indeterminate state.
     *
     * @param indeterminate {@code true} to set indeterminate
     */
    public void setIndeterminate(boolean indeterminate) {
        getLocator().evaluate("(el, val) => el.indeterminate = val", indeterminate);
    }

    /**
     * Get a {@code CheckboxElement} by its accessible label.
     *
     * @param page  the Playwright page
     * @param label the accessible label of the checkbox
     * @return the matching {@code CheckboxElement}
     */
    public static CheckboxElement getByLabel(Page page, String label) {
        return new CheckboxElement(
                page.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions()
                                .setHas(page.getByRole(AriaRole.CHECKBOX,
                                        new Page.GetByRoleOptions().setName(label)))
                        ).first());
    }
}
