package org.vaadin.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.dramafinder.element.common.HasInputFieldElement;
import org.vaadin.dramafinder.element.common.HasPrefixAndSuffixElement;
import org.vaadin.dramafinder.element.common.HasValidationPropertiesElement;

import org.vaadin.dramafinder.element.common.HasClearButtonElement;
import org.vaadin.dramafinder.element.common.HasPlaceholderElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@PlaywrightElement("vaadin-text-field")
public class TextFieldElement extends VaadinElement
        implements HasValidationPropertiesElement, HasInputFieldElement,
        HasPrefixAndSuffixElement, HasClearButtonElement, HasPlaceholderElement {
    public TextFieldElement(Locator locator) {
        super(locator);
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

    public Integer getMinLength() {
        String v = getInputLocator().getAttribute("minLength");
        return v == null ? null : Integer.valueOf(v);
    }

    public void setMinLength(int min) {
        getLocator().evaluate("(el, v) => el.minLength = v", min);
    }

    public void assertMinLength(Integer min) {
        if (min != null) {
            assertThat(getInputLocator()).hasAttribute("minlength", min + "");
        } else {
            assertThat(getInputLocator()).not().hasAttribute("minlength", "");
        }
    }

    public Integer getMaxLength() {
        String v = getInputLocator().getAttribute("maxlength");
        return v == null ? null : Integer.valueOf(v);
    }

    public void setMaxLength(int max) {
        getLocator().evaluate("(el, v) => el.maxLength = v", max);
    }

    public void assertMaxLength(Integer max) {
        if (max != null) {
            assertThat(getInputLocator()).hasAttribute("maxlength", max + "");
        } else {
            assertThat(getInputLocator()).not().hasAttribute("maxlength", "");
        }
    }

    public String getAllowedCharPattern() {
        return getLocator().evaluate("el => el.allowedCharPattern").toString();
    }

    public void setAllowedCharPattern(String pattern) {
        getLocator().evaluate("(el, p) => el.allowedCharPattern = p", pattern);
    }

    public void assertAllowedCharPattern(String pattern) {
        if (pattern != null) {
            assertThat(getLocator()).hasJSProperty("allowedCharPattern", pattern);
        } else {
            assertThat(getLocator()).not().hasAttribute("allowedCharPattern", "");
        }
    }

    public String getPattern() {
        return getLocator().getAttribute("pattern");
    }

    public void setPattern(String pattern) {
        getLocator().evaluate("(el, p) => el.pattern = p", pattern);
    }

    public void assertPattern(String pattern) {
        if (pattern != null) {
            assertThat(getInputLocator()).hasAttribute("pattern", pattern);
        } else {
            assertThat(getInputLocator()).not().hasAttribute("pattern", "");
        }
    }

    public String getValue() {
        return locator.evaluate("el => el.value").toString();
    }

    /**
     * Set value via JavaScript (ensures the `value-changed` event is triggered)
     */
    public void setValue(String value) {
        getInputLocator().fill(value);
        locator.dispatchEvent("change");
    }

    /**
     * Clear the input field
     */
    public void clear() {
        setValue("");
    }

    public Locator getInputLocator() {
        return getLocator().locator("*[slot=\"input\"]").first(); // slot="helper"
    }

}
