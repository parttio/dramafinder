package org.vaadin.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.dramafinder.element.shared.*;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@PlaywrightElement(TextFieldElement.FIELD_TAG_NAME)
public class TextFieldElement extends VaadinElement
        implements HasValidationPropertiesElement, HasInputFieldElement,
        HasPrefixElement, HasSuffixElement, HasClearButtonElement, HasPlaceholderElement, HasAllowedCharPatternElement,
        HasThemeElement, FocusableElement, HasAriaLabelElement, HasEnabledElement, HasTooltipElement {

    public static final String FIELD_TAG_NAME = "vaadin-text-field";
    public static final String MAXLENGTH_ATTRIBUTE = "maxlength";
    public static final String PATTERN_ATTRIBUTE = "pattern";
    public static final String MIN_LENGTH_ATTRIBUTE = "minLength";

    public TextFieldElement(Locator locator) {
        super(locator);
    }

    public Integer getMinLength() {
        String v = getInputLocator().getAttribute(MIN_LENGTH_ATTRIBUTE);
        return v == null ? null : Integer.valueOf(v);
    }

    public void setMinLength(int min) {
        getLocator().evaluate("(el, v) => el.minLength = v", min);
    }

    public void assertMinLength(Integer min) {
        if (min != null) {
            assertThat(getInputLocator()).hasAttribute(MIN_LENGTH_ATTRIBUTE, min + "");
        } else {
            assertThat(getInputLocator()).not().hasAttribute(MIN_LENGTH_ATTRIBUTE, "");
        }
    }

    public Integer getMaxLength() {
        String v = getInputLocator().getAttribute(MAXLENGTH_ATTRIBUTE);
        return v == null ? null : Integer.valueOf(v);
    }

    public void setMaxLength(int max) {
        getLocator().evaluate("(el, v) => el.maxLength = v", max);
    }

    public void assertMaxLength(Integer max) {
        if (max != null) {
            assertThat(getInputLocator()).hasAttribute(MAXLENGTH_ATTRIBUTE, max + "");
        } else {
            assertThat(getInputLocator()).not().hasAttribute(MAXLENGTH_ATTRIBUTE, "");
        }
    }

    public String getPattern() {
        return getInputLocator().getAttribute(PATTERN_ATTRIBUTE);
    }

    public void setPattern(String pattern) {
        getInputLocator().evaluate("(el, p) => el.pattern = p", pattern);
    }

    public void assertPattern(String pattern) {
        if (pattern != null) {
            assertThat(getInputLocator()).hasAttribute(PATTERN_ATTRIBUTE, pattern);
        } else {
            assertThat(getInputLocator()).not().hasAttribute(PATTERN_ATTRIBUTE, "");
        }
    }

    public static TextFieldElement getByLabel(Page page, String label) {
        return new TextFieldElement(
                page.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions()
                                .setHas(page.getByRole(AriaRole.TEXTBOX,
                                        new Page.GetByRoleOptions().setName(label)))
                        ).first());
    }

    public static TextFieldElement getByLabel(Locator locator, String label) {
        return new TextFieldElement(
                locator.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions().setHas(locator.getByLabel(label))
                        ).first());
    }

    @Override
    public Locator getFocusLocator() {
        return getInputLocator();
    }

    @Override
    public Locator getAriaLabelLocator() {
        return getInputLocator();
    }

    @Override
    public Locator getEnabledLocator() {
        return getInputLocator();
    }
}
