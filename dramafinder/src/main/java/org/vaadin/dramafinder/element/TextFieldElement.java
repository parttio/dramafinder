package org.vaadin.dramafinder.element;

import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.dramafinder.element.common.HasInputFieldElement;
import org.vaadin.dramafinder.element.common.HasValidationPropertiesElement;

@PlaywrightElement("vaadin-text-field")
public class TextFieldElement extends VaadinElement
        implements HasValidationPropertiesElement, HasInputFieldElement {
	public TextFieldElement(Locator locator) {
		super(locator);
	}

	/** Set value via JavaScript (ensures the `value-changed` event is triggered) */
	public void setValue(String value) {
		locator.evaluate("(el, val) => el.value = val", value);
		locator.dispatchEvent("change");
	}

	public String getValue() {
		return locator.evaluate("el => el.value").toString();
	}

	/** Clear the input field */
	public void clear() {
		setValue("");
	}

    public Locator getInputLocator() {
        return getLocator().locator("*[slot=\"input\"]").first(); // slot="helper"
    }

    public static TextFieldElement getByLabel(Page page, String label) {
        return new TextFieldElement(
                page.locator("vaadin-text-field")
                .filter(new Locator.FilterOptions().setHas(page.getByLabel(label))
                ).first());
    }

    public static TextFieldElement getByLabel(Locator locator, String label) {
        return new TextFieldElement(
                locator.locator("vaadin-text-field")
                .filter(new Locator.FilterOptions().setHas(locator.getByLabel(label))
                ).first());
    }

}
