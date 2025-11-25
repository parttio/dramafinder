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

@PlaywrightElement(CheckboxElement.FIELD_TAG_NAME)
public class CheckboxElement extends VaadinElement
        implements FocusableElement, HasAriaLabelElement, HasEnabledElement,
        HasHelperElement, HasValueElement, HasStyleElement, HasLabelElement, HasValidationPropertiesElement {

    public static final String FIELD_TAG_NAME = "vaadin-checkbox";

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

    public boolean isChecked() {
        return getInputLocator().isChecked();
    }

    public void assertChecked() {
        assertThat(getInputLocator()).isChecked();
    }

    public void assertNotChecked() {
        assertThat(getInputLocator()).not().isChecked();
    }

    public void check() {
        getInputLocator().check();
    }

    public void uncheck() {
        getInputLocator().uncheck();
    }

    public boolean isIndeterminate() {
        return getLocator().getAttribute("indeterminate") != null;
    }

    public void assertIndeterminate() {
        assertThat(getLocator()).hasAttribute("indeterminate", "");
    }

    public void assertNotIndeterminate() {
        assertThat(getLocator()).not().hasAttribute("indeterminate", "");
    }

    public void setIndeterminate(boolean indeterminate) {
        getLocator().evaluate("(el, val) => el.indeterminate = val", indeterminate);
    }

    public static CheckboxElement getByLabel(Page page, String label) {
        return new CheckboxElement(
                page.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions()
                                .setHas(page.getByRole(AriaRole.CHECKBOX,
                                        new Page.GetByRoleOptions().setName(label)))
                        ).first());
    }
}
