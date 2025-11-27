package org.vaadin.addons.dramafinder.element;

import java.util.regex.Pattern;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.shared.FocusableElement;
import org.vaadin.addons.dramafinder.element.shared.HasAllowedCharPatternElement;
import org.vaadin.addons.dramafinder.element.shared.HasAriaLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasClearButtonElement;
import org.vaadin.addons.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.addons.dramafinder.element.shared.HasInputFieldElement;
import org.vaadin.addons.dramafinder.element.shared.HasPlaceholderElement;
import org.vaadin.addons.dramafinder.element.shared.HasPrefixElement;
import org.vaadin.addons.dramafinder.element.shared.HasSuffixElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;
import org.vaadin.addons.dramafinder.element.shared.HasTooltipElement;
import org.vaadin.addons.dramafinder.element.shared.HasValidationPropertiesElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-text-field>}
 */
@PlaywrightElement(TextFieldElement.FIELD_TAG_NAME)
public class TextFieldElement extends VaadinElement
        implements HasValidationPropertiesElement, HasInputFieldElement,
        HasPrefixElement, HasSuffixElement, HasClearButtonElement, HasPlaceholderElement, HasAllowedCharPatternElement,
        HasThemeElement, FocusableElement, HasAriaLabelElement, HasEnabledElement, HasTooltipElement {

    public static final String FIELD_TAG_NAME = "vaadin-text-field";
    public static final String MAXLENGTH_ATTRIBUTE = "maxlength";
    public static final String PATTERN_ATTRIBUTE = "pattern";
    public static final String MIN_LENGTH_ATTRIBUTE = "minLength";

    /**
     * Creates a new {@code TextFieldElement}
     *
     * @param locator the locator for the {@code <vaadin-text-field>} element
     */
    public TextFieldElement(Locator locator) {
        super(locator);
    }

    /**
     * Get the current minimum length of the text field.
     *
     * @return the minimum length or {@code null} if not set
     */
    public Integer getMinLength() {
        String v = getInputLocator().getAttribute(MIN_LENGTH_ATTRIBUTE);
        return v == null ? null : Integer.valueOf(v);
    }

    /**
     * Set the minimum length for the text field.
     *
     * @param min the minimum length
     */
    public void setMinLength(int min) {
        getLocator().evaluate("(el, v) => el.minLength = v", min);
    }

    /**
     * Assert that the minimum length of the text field is as expected.
     *
     * @param min the expected minimum length or {@code null} if no minimum length is expected
     */
    public void assertMinLength(Integer min) {
        if (min != null) {
            assertThat(getInputLocator()).hasAttribute(MIN_LENGTH_ATTRIBUTE, min + "");
        } else {
            assertThat(getInputLocator()).not().hasAttribute(MIN_LENGTH_ATTRIBUTE, Pattern.compile(".*"));
        }
    }

    /**
     * Get the current maximum length of the text field.
     *
     * @return the maximum length or {@code null} if not set
     */
    public Integer getMaxLength() {
        String v = getInputLocator().getAttribute(MAXLENGTH_ATTRIBUTE);
        return v == null ? null : Integer.valueOf(v);
    }

    /**
     * Set the maximum length for the text field.
     *
     * @param max the maximum length
     */
    public void setMaxLength(int max) {
        getLocator().evaluate("(el, v) => el.maxLength = v", max);
    }

    /**
     * Assert that the maximum length of the text field is as expected.
     *
     * @param max the expected maximum length or {@code null} if no maximum length is expected
     */
    public void assertMaxLength(Integer max) {
        if (max != null) {
            assertThat(getInputLocator()).hasAttribute(MAXLENGTH_ATTRIBUTE, max + "");
        } else {
            assertThat(getInputLocator()).not().hasAttribute(MAXLENGTH_ATTRIBUTE, Pattern.compile(".*"));
        }
    }

    /**
     * Get the current pattern of the text field.
     *
     * @return the pattern or {@code null} if not set
     */
    public String getPattern() {
        return getInputLocator().getAttribute(PATTERN_ATTRIBUTE);
    }

    /**
     * Set the pattern for the text field.
     *
     * @param pattern the pattern
     */
    public void setPattern(String pattern) {
        getInputLocator().evaluate("(el, p) => el.pattern = p", pattern);
    }

    /**
     * Assert that the pattern of the text field is as expected.
     *
     * @param pattern the expected pattern or {@code null} if no pattern is expected
     */
    public void assertPattern(String pattern) {
        if (pattern != null) {
            assertThat(getInputLocator()).hasAttribute(PATTERN_ATTRIBUTE, pattern);
        } else {
            assertThat(getInputLocator()).not().hasAttribute(PATTERN_ATTRIBUTE, Pattern.compile(".*"));
        }
    }

    /**
     * Get the {@code TextFieldElement} by its label.
     *
     * @param page  the Playwright page
     * @param label the label of the text field
     * @return the {@code TextFieldElement}
     */
    public static TextFieldElement getByLabel(Page page, String label) {
        return new TextFieldElement(
                page.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions()
                                .setHas(page.getByRole(AriaRole.TEXTBOX,
                                        new Page.GetByRoleOptions().setName(label)))
                        ).first());
    }

    /**
     * Get the {@code TextFieldElement} by its label.
     *
     * @param locator the locator to search within
     * @param label   the label of the text field
     * @return the {@code TextFieldElement}
     */
    public static TextFieldElement getByLabel(Locator locator, String label) {
        return new TextFieldElement(
                locator.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions().setHas(locator.getByLabel(label))
                        ).first());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Locator getFocusLocator() {
        return getInputLocator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Locator getAriaLabelLocator() {
        return getInputLocator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Locator getEnabledLocator() {
        return getInputLocator();
    }
}
