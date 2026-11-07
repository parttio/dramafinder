package org.vaadin.addons.dramafinder.element;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.query.ElementQueries;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.query.InputFieldQuery;
import org.vaadin.addons.dramafinder.element.shared.FocusableElement;
import org.vaadin.addons.dramafinder.element.shared.HasAriaLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasClearButtonElement;
import org.vaadin.addons.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.addons.dramafinder.element.shared.HasHelperElement;
import org.vaadin.addons.dramafinder.element.shared.HasInputFieldElement;
import org.vaadin.addons.dramafinder.element.shared.HasLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasPlaceholderElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;
import org.vaadin.addons.dramafinder.element.shared.HasTooltipElement;
import org.vaadin.addons.dramafinder.element.shared.HasValidationPropertiesElement;
import org.vaadin.addons.dramafinder.element.utils.IdLocator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-date-picker>}.
 * <p>
 * Adds convenience methods for {@link LocalDate} values and lookup by label.
 */
@PlaywrightElement(DatePickerElement.FIELD_TAG_NAME)
public class DatePickerElement extends VaadinElement implements HasInputFieldElement, HasValidationPropertiesElement,
        HasClearButtonElement, HasPlaceholderElement, HasThemeElement, FocusableElement, HasAriaLabelElement,
        HasEnabledElement, HasTooltipElement, HasLabelElement, HasHelperElement,
        Findable<InputFieldQuery<DatePickerElement>> {

    public static final String FIELD_TAG_NAME = "vaadin-date-picker";

    /**
     * Create a new {@code DatePickerElement}.
     *
     * @param locator the locator for the {@code <vaadin-date-picker>} element
     */
    public DatePickerElement(Locator locator) {
        super(locator);
    }

    /**
     * Set the value using a {@link LocalDate} formatted as ISO-8601.
     *
     * @param date the date to set
     */
    public void setValue(LocalDate date) {
        String formattedDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        setProperty("value", formattedDate);
    }

    /**
     * Get the current value as a {@link LocalDate}.
     *
     * @return the parsed date or {@code null} when empty
     */
    public LocalDate getValueAsLocalDate() {
        String value = getValue();
        if (value == null || value.isEmpty()) {
            return null;
        }
        return LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    @Override
    public Locator getAriaLabelLocator() {
        return getInputLocator();
    }

    @Override
    public Locator getFocusLocator() {
        return getInputLocator();
    }


    @Override
    public Locator getEnabledLocator() {
        return getInputLocator();
    }

    /**
     * Set the value of the input.
     *
     * @param value value formatted as in the view dd/mm/yyyy.
     */
    @Override
    public void setValue(String value) {
        HasInputFieldElement.super.setValue(value);
        getLocator().press("Enter");
        getLocator().dispatchEvent("change");
        waitAfterAction();
    }


    /**
     * Assert that the input value equals the provided string.
     *
     * @param value formatted as in the view dd/mm/yyyy.
     */
    @Override
    public void assertValue(String value) {
        HasInputFieldElement.super.assertValue(value);
    }

    /**
     * Assert that the value equals the provided date.
     *
     * @param value expected {@link LocalDate} or {@code null} for empty
     */
    public void assertValue(LocalDate value) {
        if (value != null) {
            assertThat(getLocator()).hasJSProperty("value", value.format(DateTimeFormatter.ISO_LOCAL_DATE));
        } else {
            assertValue("");
        }
    }


    /**
     * Get the {@code DatePickerElement} whose visible label is exactly {@code label}, searching the entire page.
     * <p>
     * The label is the text of the component's own {@code label} slot, matched in
     * full and case-sensitively. An {@code aria-label} or a placeholder is not a
     * label: use {@code find(page, DatePickerElement.class).withAccessibleName(..)} or
     * {@code .withPlaceholder(..)}. No {@code .first()} is applied: two components
     * with that label fail with a strict-mode error. Same as
     * {@code find(page, DatePickerElement.class).withLabel(label)}; use
     * {@link #getByLabelContaining(Page, String)} to match part of the label.
     *
     * @param page  the Playwright page
     * @param label the full visible label
     * @return the matching {@code DatePickerElement}
     */
    public static DatePickerElement getByLabel(Page page, String label) {
        return new DatePickerElement(ElementQueries.find(page, DatePickerElement.class).withLabel(label).getLocator());
    }

    /**
     * Get the {@code DatePickerElement} whose visible label contains {@code text}, searching the entire page.
     * <p>
     * The label is matched case-insensitively as a substring; an {@code aria-label}
     * or a placeholder is not a label. No {@code .first()} is applied: several
     * matching components fail with a strict-mode error. Same as
     * {@code find(page, DatePickerElement.class).withLabelContaining(text)}.
     *
     * @param page  the Playwright page
     * @param text  part of the visible label
     * @return the matching {@code DatePickerElement}
     */
    public static DatePickerElement getByLabelContaining(Page page, String text) {
        return new DatePickerElement(ElementQueries.find(page, DatePickerElement.class).withLabelContaining(text).getLocator());
    }

    /**
     * Get the {@code DatePickerElement} whose visible label is exactly {@code label}, scoped to the given locator.
     * <p>
     * The label is the text of the component's own {@code label} slot, matched in
     * full and case-sensitively. An {@code aria-label} or a placeholder is not a
     * label: use {@code find(locator, DatePickerElement.class).withAccessibleName(..)} or
     * {@code .withPlaceholder(..)}. No {@code .first()} is applied: two components
     * with that label fail with a strict-mode error. Same as
     * {@code find(locator, DatePickerElement.class).withLabel(label)}; use
     * {@link #getByLabelContaining(Locator, String)} to match part of the label.
     *
     * @param locator the locator whose subtree is searched
     * @param label   the full visible label
     * @return the matching {@code DatePickerElement}
     */
    public static DatePickerElement getByLabel(Locator locator, String label) {
        return new DatePickerElement(ElementQueries.find(locator, DatePickerElement.class).withLabel(label).getLocator());
    }

    /**
     * Get the {@code DatePickerElement} whose visible label contains {@code text}, scoped to the given locator.
     * <p>
     * The label is matched case-insensitively as a substring; an {@code aria-label}
     * or a placeholder is not a label. No {@code .first()} is applied: several
     * matching components fail with a strict-mode error. Same as
     * {@code find(locator, DatePickerElement.class).withLabelContaining(text)}.
     *
     * @param locator the locator whose subtree is searched
     * @param text    part of the visible label
     * @return the matching {@code DatePickerElement}
     */
    public static DatePickerElement getByLabelContaining(Locator locator, String text) {
        return new DatePickerElement(ElementQueries.find(locator, DatePickerElement.class).withLabelContaining(text).getLocator());
    }

    // ── Lookup by id or test id ────────────────────────────────────────

    /**
     * Get a {@code DatePickerElement} by its {@code id}, searching the entire page.
     * <p>
     * A fallback for a component without a user-visible name: prefer the label or
     * text lookups when there is one. The id must match in full; two components
     * with that id fail with a strict-mode error.
     *
     * @param page the Playwright page
     * @param id   the full {@code id}
     * @return the matching {@code DatePickerElement}
     */
    public static DatePickerElement getById(Page page, String id) {
        return new DatePickerElement(IdLocator.byId(page, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code DatePickerElement} by its {@code id}, scoped to the given locator.
     * <p>
     * A fallback for a component without a user-visible name. The id must match in
     * full; two components with that id inside the scope fail with a strict-mode
     * error.
     *
     * @param locator the locator whose subtree is searched
     * @param id      the full {@code id}
     * @return the matching {@code DatePickerElement}
     */
    public static DatePickerElement getById(Locator locator, String id) {
        return new DatePickerElement(IdLocator.byId(locator, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code DatePickerElement} by its test id, searching the entire page.
     * <p>
     * The test id is Playwright's test id attribute, {@code data-testid} unless
     * reconfigured — the attribute Flow's {@code Component.setTestId(String)} sets.
     * A fallback for a component without a user-visible name; it must match in
     * full, and two components with that test id fail with a strict-mode error.
     *
     * @param page   the Playwright page
     * @param testId the full test id
     * @return the matching {@code DatePickerElement}
     */
    public static DatePickerElement getByTestId(Page page, String testId) {
        return new DatePickerElement(IdLocator.byTestId(page, FIELD_TAG_NAME, testId));
    }

    /**
     * Get a {@code DatePickerElement} by its test id, scoped to the given locator.
     * <p>
     * See {@link #getByTestId(Page, String)}; only components inside the scope are
     * considered.
     *
     * @param locator the locator whose subtree is searched
     * @param testId  the full test id
     * @return the matching {@code DatePickerElement}
     */
    public static DatePickerElement getByTestId(Locator locator, String testId) {
        return new DatePickerElement(IdLocator.byTestId(locator, FIELD_TAG_NAME, testId));
    }
}
