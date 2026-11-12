package org.vaadin.addons.dramafinder.element;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.query.ElementQueries;
import org.vaadin.addons.dramafinder.element.query.DateTimePickerQuery;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.shared.FocusableElement;
import org.vaadin.addons.dramafinder.element.shared.HasAriaLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasClearButtonElement;
import org.vaadin.addons.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.addons.dramafinder.element.shared.HasHelperElement;
import org.vaadin.addons.dramafinder.element.shared.HasInputFieldElement;
import org.vaadin.addons.dramafinder.element.shared.HasLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasPlaceholderElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;
import org.vaadin.addons.dramafinder.element.shared.HasValueElement;
import org.vaadin.addons.dramafinder.element.shared.HasTooltipElement;
import org.vaadin.addons.dramafinder.element.shared.HasValidationPropertiesElement;
import org.vaadin.addons.dramafinder.element.utils.IdLocator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

/**
 * PlaywrightElement for {@code <vaadin-date-time-picker>}.
 * <p>
 * Composes a {@link DatePickerElement} and {@link TimePickerElement} (see
 * {@link #getDatePicker()} and {@link #getTimePicker()}) and exposes helpers to
 * interact using {@link LocalDateTime}; {@link #typeText(String, String)} types
 * into both halves as a user does and {@link #clear()} empties both.
 */
@PlaywrightElement(DateTimePickerElement.FIELD_TAG_NAME)
public class DateTimePickerElement extends VaadinElement implements HasInputFieldElement, HasValidationPropertiesElement,
        HasClearButtonElement, HasPlaceholderElement, HasThemeElement, FocusableElement, HasAriaLabelElement,
        HasEnabledElement, HasTooltipElement, HasLabelElement, HasHelperElement,
        Findable<DateTimePickerQuery<DateTimePickerElement>> {

    private final DatePickerElement datePickerElement;
    private final TimePickerElement timePickerElement;
    public static final String FIELD_TAG_NAME = "vaadin-date-time-picker";
    public static final DateTimeFormatter ISO_LOCAL_DATE_TIME = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_DATE)
            .appendLiteral('T')
            .append(TimePickerElement.LOCAL_TIME)
            .toFormatter();

    /**
     * Create a new {@code DateTimePickerElement}.
     *
     * @param locator the locator for the {@code <vaadin-date-time-picker>} element
     */
    public DateTimePickerElement(Locator locator) {
        super(locator);
        datePickerElement = new DatePickerElement(locator.locator(DatePickerElement.FIELD_TAG_NAME));
        timePickerElement = new TimePickerElement(locator.locator(TimePickerElement.FIELD_TAG_NAME));
    }

    /**
     * Set the value using a {@link LocalDateTime}.
     *
     * @param date the date-time to set
     */
    public void setValue(LocalDateTime date) {
        datePickerElement.setValue(date.toLocalDate());
        timePickerElement.setValue(date.toLocalTime());
        getLocator().dispatchEvent("change");
        waitAfterAction();
    }

    /**
     * Get the current value as a {@link LocalDateTime}.
     *
     * @return the parsed value or {@code null} when empty
     */
    public LocalDateTime getValueAsLocalDateTime() {
        String value = getValue();
        if (value == null || value.isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(value, ISO_LOCAL_DATE_TIME);
    }

    @Override
    public String getAriaLabel() {
        return datePickerElement.getAriaLabel();
    }

    @Override
    public void assertAriaLabel(String ariaLabel) {
        datePickerElement.assertAriaLabel(ariaLabel);
        timePickerElement.assertAriaLabel(ariaLabel);
    }

    @Override
    public Locator getFocusLocator() {
        return datePickerElement.getInputLocator();
    }

    @Override
    public boolean isEnabled() {
        return datePickerElement.isEnabled() && timePickerElement.isEnabled();
    }

    @Override
    public void assertEnabled() {
        datePickerElement.assertEnabled();
        timePickerElement.assertEnabled();
    }

    @Override
    public void assertDisabled() {
        datePickerElement.assertDisabled();
        timePickerElement.assertDisabled();
    }

    /**
     * Fill the date half's input with {@code value} and dispatch a
     * {@code change} event on the host.
     * <p>
     * Only the date half is touched, so {@code setValue("")} leaves the time:
     * use {@link #typeText(String, String)} to type into both halves as a user
     * does, and {@link #clear()} to empty both.
     *
     * @param value the text for the date half's input
     */
    @Override
    public void setValue(String value) {
        HasInputFieldElement.super.setValue(value);
    }


    /**
     * Assert that the input value equals the provided string.
     *
     * @param value formatted as in the view dd/mm/yyyy hh:mm.
     */
    @Override
    public void assertValue(String value) {
        HasInputFieldElement.super.assertValue(value);
    }

    /**
     * Assert that the value equals the provided date-time.
     *
     * @param value expected {@link LocalDateTime} or {@code null} for empty
     */
    public void assertValue(LocalDateTime value) {
        if (value != null) {
            assertThat(getLocator()).hasJSProperty("value", value.format(ISO_LOCAL_DATE_TIME));
        } else {
            assertValue("");
        }
    }


    /**
     * Get the {@code DateTimePickerElement} whose visible label is exactly {@code label}, searching the entire page.
     * <p>
     * The label is the text of the component's own {@code label} slot, matched in
     * full and case-sensitively. An {@code aria-label} or a placeholder is not a
     * label: use {@code find(page, DateTimePickerElement.class).withAccessibleName(..)} or
     * {@code .withPlaceholder(..)}. No {@code .first()} is applied: two components
     * with that label fail with a strict-mode error. Same as
     * {@code find(page, DateTimePickerElement.class).withLabel(label)}; use
     * {@link #getByLabelContaining(Page, String)} to match part of the label.
     *
     * @param page  the Playwright page
     * @param label the full visible label
     * @return the matching {@code DateTimePickerElement}
     */
    public static DateTimePickerElement getByLabel(Page page, String label) {
        return new DateTimePickerElement(ElementQueries.find(page, DateTimePickerElement.class).withLabel(label).getLocator());
    }

    /**
     * Get the {@code DateTimePickerElement} whose visible label contains {@code text}, searching the entire page.
     * <p>
     * The label is matched case-insensitively as a substring; an {@code aria-label}
     * or a placeholder is not a label. No {@code .first()} is applied: several
     * matching components fail with a strict-mode error. Same as
     * {@code find(page, DateTimePickerElement.class).withLabelContaining(text)}.
     *
     * @param page  the Playwright page
     * @param text  part of the visible label
     * @return the matching {@code DateTimePickerElement}
     */
    public static DateTimePickerElement getByLabelContaining(Page page, String text) {
        return new DateTimePickerElement(ElementQueries.find(page, DateTimePickerElement.class).withLabelContaining(text).getLocator());
    }

    /**
     * Get the {@code DateTimePickerElement} whose visible label is exactly {@code label}, scoped to the given locator.
     * <p>
     * The label is the text of the component's own {@code label} slot, matched in
     * full and case-sensitively. An {@code aria-label} or a placeholder is not a
     * label: use {@code find(locator, DateTimePickerElement.class).withAccessibleName(..)} or
     * {@code .withPlaceholder(..)}. No {@code .first()} is applied: two components
     * with that label fail with a strict-mode error. Same as
     * {@code find(locator, DateTimePickerElement.class).withLabel(label)}; use
     * {@link #getByLabelContaining(Locator, String)} to match part of the label.
     *
     * @param locator the locator whose subtree is searched
     * @param label   the full visible label
     * @return the matching {@code DateTimePickerElement}
     */
    public static DateTimePickerElement getByLabel(Locator locator, String label) {
        return new DateTimePickerElement(ElementQueries.find(locator, DateTimePickerElement.class).withLabel(label).getLocator());
    }

    /**
     * Get the {@code DateTimePickerElement} whose visible label contains {@code text}, scoped to the given locator.
     * <p>
     * The label is matched case-insensitively as a substring; an {@code aria-label}
     * or a placeholder is not a label. No {@code .first()} is applied: several
     * matching components fail with a strict-mode error. Same as
     * {@code find(locator, DateTimePickerElement.class).withLabelContaining(text)}.
     *
     * @param locator the locator whose subtree is searched
     * @param text    part of the visible label
     * @return the matching {@code DateTimePickerElement}
     */
    public static DateTimePickerElement getByLabelContaining(Locator locator, String text) {
        return new DateTimePickerElement(ElementQueries.find(locator, DateTimePickerElement.class).withLabelContaining(text).getLocator());
    }

    /**
     * Type {@code date} into the date half, press Enter there and leave the
     * field, as a user does.
     * <p>
     * No synthetic {@code change} event is dispatched. Leaving the field makes
     * the picker check its halves, so while the time half is still empty the
     * field is invalid (incomplete) until {@link #setTime(String)} fills it; use
     * {@link #typeText(String, String)} to type both halves before leaving.
     * Waits for the server round-trip afterwards.
     *
     * @param date the date text in the locale's format (e.g. {@code dd/mm/yyyy});
     *             an empty string empties the date half
     */
    public void setDate(String date) {
        typeInto(datePickerElement, date);
        leave(datePickerElement);
    }

    /**
     * Type {@code time} into the time half, press Enter there and leave the
     * field, as a user does.
     * <p>
     * No synthetic {@code change} event is dispatched; see
     * {@link #setDate(String)} for what leaving the field checks. Waits for the
     * server round-trip afterwards.
     *
     * @param time the time text in the locale's format (e.g. {@code hh:mm});
     *             an empty string empties the time half
     */
    public void setTime(String time) {
        typeInto(timePickerElement, time);
        leave(timePickerElement);
    }

    /**
     * Type into both halves as a user does, then leave the field.
     * <p>
     * Fills the date input and presses Enter on it, does the same in the time
     * input, then blurs the time input. No synthetic {@code change} event is
     * dispatched; leaving the field makes the picker check its halves, so text
     * it cannot parse leaves the value empty and the field invalid (bad input),
     * and one empty half leaves it empty and invalid (incomplete). Waits for the
     * server round-trip afterwards.
     *
     * @param date the date text in the locale's format (e.g. {@code dd/mm/yyyy});
     *             an empty string leaves the date half empty
     * @param time the time text in the locale's format (e.g. {@code hh:mm});
     *             an empty string leaves the time half empty
     */
    public void typeText(String date, String time) {
        typeInto(datePickerElement, date);
        typeInto(timePickerElement, time);
        leave(timePickerElement);
    }

    /**
     * Empty both halves and leave the field, so the value becomes empty
     * ({@code null} on the server). Same as {@code typeText("", "")}.
     */
    @Override
    public void clear() {
        typeText("", "");
    }

    /**
     * Get the date half of this field.
     *
     * @return the nested {@code vaadin-date-picker} as a {@link DatePickerElement}
     */
    public DatePickerElement getDatePicker() {
        return datePickerElement;
    }

    /**
     * Get the time half of this field.
     *
     * @return the nested {@code vaadin-time-picker} as a {@link TimePickerElement}
     */
    public TimePickerElement getTimePicker() {
        return timePickerElement;
    }

    private void typeInto(HasValueElement half, String text) {
        Locator input = half.getInputLocator();
        input.fill(text);
        input.press("Enter");
        waitAfterAction();
    }

    private void leave(HasValueElement half) {
        half.getInputLocator().blur();
        waitAfterAction();
    }

    /**
     * Assert the date sub-field value equals the expected string.
     */
    public void assertDateValue(String date) {
        datePickerElement.assertValue(date);
    }

    /**
     * Assert the time sub-field value equals the expected string.
     */
    public void assertTimeValue(String time) {
        timePickerElement.assertValue(time);
    }

    // ── Lookup by id or test id ────────────────────────────────────────

    /**
     * Get a {@code DateTimePickerElement} by its {@code id}, searching the entire page.
     * <p>
     * A fallback for a component without a user-visible name: prefer the label or
     * text lookups when there is one. The id must match in full; two components
     * with that id fail with a strict-mode error.
     *
     * @param page the Playwright page
     * @param id   the full {@code id}
     * @return the matching {@code DateTimePickerElement}
     */
    public static DateTimePickerElement getById(Page page, String id) {
        return new DateTimePickerElement(IdLocator.byId(page, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code DateTimePickerElement} by its {@code id}, scoped to the given locator.
     * <p>
     * A fallback for a component without a user-visible name. The id must match in
     * full; two components with that id inside the scope fail with a strict-mode
     * error.
     *
     * @param locator the locator whose subtree is searched
     * @param id      the full {@code id}
     * @return the matching {@code DateTimePickerElement}
     */
    public static DateTimePickerElement getById(Locator locator, String id) {
        return new DateTimePickerElement(IdLocator.byId(locator, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code DateTimePickerElement} by its test id, searching the entire page.
     * <p>
     * The test id is Playwright's test id attribute, {@code data-testid} unless
     * reconfigured — the attribute Flow's {@code Component.setTestId(String)} sets.
     * A fallback for a component without a user-visible name; it must match in
     * full, and two components with that test id fail with a strict-mode error.
     *
     * @param page   the Playwright page
     * @param testId the full test id
     * @return the matching {@code DateTimePickerElement}
     */
    public static DateTimePickerElement getByTestId(Page page, String testId) {
        return new DateTimePickerElement(IdLocator.byTestId(page, FIELD_TAG_NAME, testId));
    }

    /**
     * Get a {@code DateTimePickerElement} by its test id, scoped to the given locator.
     * <p>
     * See {@link #getByTestId(Page, String)}; only components inside the scope are
     * considered.
     *
     * @param locator the locator whose subtree is searched
     * @param testId  the full test id
     * @return the matching {@code DateTimePickerElement}
     */
    public static DateTimePickerElement getByTestId(Locator locator, String testId) {
        return new DateTimePickerElement(IdLocator.byTestId(locator, FIELD_TAG_NAME, testId));
    }
}
