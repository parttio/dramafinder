package org.vaadin.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.dramafinder.element.common.*;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@PlaywrightElement("vaadin-text-field")
public class TextFieldElement extends VaadinElement
        implements HasValidationPropertiesElement, HasInputFieldElement,
        HasPrefixAndSuffixElement, HasClearButtonElement, HasPlaceholderElement, HasAllowedCharPatternElement, HasThemeElement {
    public TextFieldElement(Locator locator) {
        super(locator);
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
