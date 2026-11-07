package org.vaadin.addons.dramafinder.element;

import java.util.regex.Pattern;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.query.ElementQueries;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.query.InputFieldQuery;
import org.vaadin.addons.dramafinder.element.shared.FocusableElement;
import org.vaadin.addons.dramafinder.element.shared.HasAllowedCharPatternElement;
import org.vaadin.addons.dramafinder.element.shared.HasAriaLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasClearButtonElement;
import org.vaadin.addons.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.addons.dramafinder.element.shared.HasInputFieldElement;
import org.vaadin.addons.dramafinder.element.shared.HasPlaceholderElement;
import org.vaadin.addons.dramafinder.element.shared.HasPrefixElement;
import org.vaadin.addons.dramafinder.element.shared.HasReadOnlyElement;
import org.vaadin.addons.dramafinder.element.shared.HasSuffixElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;
import org.vaadin.addons.dramafinder.element.shared.HasTooltipElement;
import org.vaadin.addons.dramafinder.element.shared.HasValidationPropertiesElement;
import org.vaadin.addons.dramafinder.element.utils.IdLocator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-text-field>}
 */
@PlaywrightElement(TextFieldElement.FIELD_TAG_NAME)
public class TextFieldElement extends VaadinElement
        implements HasValidationPropertiesElement, HasInputFieldElement,
        HasPrefixElement, HasSuffixElement, HasClearButtonElement, HasPlaceholderElement, HasAllowedCharPatternElement,
        HasThemeElement, FocusableElement, HasAriaLabelElement, HasEnabledElement, HasReadOnlyElement, HasTooltipElement,
        Findable<InputFieldQuery<TextFieldElement>> {

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
     * Get the {@code TextFieldElement} whose visible label is exactly {@code label}, searching the entire page.
     * <p>
     * The label is the text of the component's own {@code label} slot, matched in
     * full and case-sensitively. An {@code aria-label} or a placeholder is not a
     * label: use {@code find(page, TextFieldElement.class).withAccessibleName(..)} or
     * {@code .withPlaceholder(..)}. No {@code .first()} is applied: two components
     * with that label fail with a strict-mode error. Same as
     * {@code find(page, TextFieldElement.class).withLabel(label)}; use
     * {@link #getByLabelContaining(Page, String)} to match part of the label.
     *
     * @param page  the Playwright page
     * @param label the full visible label
     * @return the matching {@code TextFieldElement}
     */
    public static TextFieldElement getByLabel(Page page, String label) {
        return new TextFieldElement(ElementQueries.find(page, TextFieldElement.class).withLabel(label).getLocator());
    }

    /**
     * Get the {@code TextFieldElement} whose visible label contains {@code text}, searching the entire page.
     * <p>
     * The label is matched case-insensitively as a substring; an {@code aria-label}
     * or a placeholder is not a label. No {@code .first()} is applied: several
     * matching components fail with a strict-mode error. Same as
     * {@code find(page, TextFieldElement.class).withLabelContaining(text)}.
     *
     * @param page  the Playwright page
     * @param text  part of the visible label
     * @return the matching {@code TextFieldElement}
     */
    public static TextFieldElement getByLabelContaining(Page page, String text) {
        return new TextFieldElement(ElementQueries.find(page, TextFieldElement.class).withLabelContaining(text).getLocator());
    }

    /**
     * Get the {@code TextFieldElement} whose visible label is exactly {@code label}, scoped to the given locator.
     * <p>
     * The label is the text of the component's own {@code label} slot, matched in
     * full and case-sensitively. An {@code aria-label} or a placeholder is not a
     * label: use {@code find(locator, TextFieldElement.class).withAccessibleName(..)} or
     * {@code .withPlaceholder(..)}. No {@code .first()} is applied: two components
     * with that label fail with a strict-mode error. Same as
     * {@code find(locator, TextFieldElement.class).withLabel(label)}; use
     * {@link #getByLabelContaining(Locator, String)} to match part of the label.
     *
     * @param locator the locator whose subtree is searched
     * @param label   the full visible label
     * @return the matching {@code TextFieldElement}
     */
    public static TextFieldElement getByLabel(Locator locator, String label) {
        return new TextFieldElement(ElementQueries.find(locator, TextFieldElement.class).withLabel(label).getLocator());
    }

    /**
     * Get the {@code TextFieldElement} whose visible label contains {@code text}, scoped to the given locator.
     * <p>
     * The label is matched case-insensitively as a substring; an {@code aria-label}
     * or a placeholder is not a label. No {@code .first()} is applied: several
     * matching components fail with a strict-mode error. Same as
     * {@code find(locator, TextFieldElement.class).withLabelContaining(text)}.
     *
     * @param locator the locator whose subtree is searched
     * @param text    part of the visible label
     * @return the matching {@code TextFieldElement}
     */
    public static TextFieldElement getByLabelContaining(Locator locator, String text) {
        return new TextFieldElement(ElementQueries.find(locator, TextFieldElement.class).withLabelContaining(text).getLocator());
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

    // ── Lookup by id or test id ────────────────────────────────────────

    /**
     * Get a {@code TextFieldElement} by its {@code id}, searching the entire page.
     * <p>
     * A fallback for a component without a user-visible name: prefer the label or
     * text lookups when there is one. The id must match in full; two components
     * with that id fail with a strict-mode error.
     *
     * @param page the Playwright page
     * @param id   the full {@code id}
     * @return the matching {@code TextFieldElement}
     */
    public static TextFieldElement getById(Page page, String id) {
        return new TextFieldElement(IdLocator.byId(page, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code TextFieldElement} by its {@code id}, scoped to the given locator.
     * <p>
     * A fallback for a component without a user-visible name. The id must match in
     * full; two components with that id inside the scope fail with a strict-mode
     * error.
     *
     * @param locator the locator whose subtree is searched
     * @param id      the full {@code id}
     * @return the matching {@code TextFieldElement}
     */
    public static TextFieldElement getById(Locator locator, String id) {
        return new TextFieldElement(IdLocator.byId(locator, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code TextFieldElement} by its test id, searching the entire page.
     * <p>
     * The test id is Playwright's test id attribute, {@code data-testid} unless
     * reconfigured — the attribute Flow's {@code Component.setTestId(String)} sets.
     * A fallback for a component without a user-visible name; it must match in
     * full, and two components with that test id fail with a strict-mode error.
     *
     * @param page   the Playwright page
     * @param testId the full test id
     * @return the matching {@code TextFieldElement}
     */
    public static TextFieldElement getByTestId(Page page, String testId) {
        return new TextFieldElement(IdLocator.byTestId(page, FIELD_TAG_NAME, testId));
    }

    /**
     * Get a {@code TextFieldElement} by its test id, scoped to the given locator.
     * <p>
     * See {@link #getByTestId(Page, String)}; only components inside the scope are
     * considered.
     *
     * @param locator the locator whose subtree is searched
     * @param testId  the full test id
     * @return the matching {@code TextFieldElement}
     */
    public static TextFieldElement getByTestId(Locator locator, String testId) {
        return new TextFieldElement(IdLocator.byTestId(locator, FIELD_TAG_NAME, testId));
    }
}
