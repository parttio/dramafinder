package org.vaadin.addons.dramafinder.element;

import java.time.LocalTime;
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
 * PlaywrightElement for {@code <vaadin-time-picker>}.
 * <p>
 * Adds convenience methods for {@link LocalTime} values and lookup by label.
 */
@PlaywrightElement(TimePickerElement.FIELD_TAG_NAME)
public class TimePickerElement extends VaadinElement implements HasInputFieldElement, HasValidationPropertiesElement,
        HasClearButtonElement, HasPlaceholderElement, HasThemeElement, FocusableElement, HasAriaLabelElement,
        HasEnabledElement, HasTooltipElement, HasLabelElement, HasHelperElement,
        Findable<InputFieldQuery<TimePickerElement>> {

    public static final String FIELD_TAG_NAME = "vaadin-time-picker";
    public static final DateTimeFormatter LOCAL_TIME = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Create a new {@code TimePickerElement}.
     *
     * @param locator the locator for the {@code <vaadin-time-picker>} element
     */
    public TimePickerElement(Locator locator) {
        super(locator);
    }

    /**
     * Set the value using a {@link LocalTime} formatted as HH:mm.
     *
     * @param time the time to set
     */
    public void setValue(LocalTime time) {
        String formattedTime = time.format(LOCAL_TIME);
        setProperty("value", formattedTime);
    }

    /**
     * Get the current value as a {@link LocalTime}.
     *
     * @return the parsed time or {@code null} when empty
     */
    public LocalTime getValueAsLocalTime() {
        String value = getValue();
        if (value == null || value.isEmpty()) {
            return null;
        }
        return LocalTime.parse(value, LOCAL_TIME);
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
     * Assert that the value equals the provided time.
     *
     * @param value expected {@link LocalTime} or {@code null} for empty
     */
    public void assertValue(LocalTime value) {
        if (value != null) {
            assertThat(getLocator()).hasJSProperty("value", value.format(LOCAL_TIME));
        } else {
            assertValue("");
        }
    }

    /**
     * Get the {@code TimePickerElement} whose visible label is exactly {@code label}, searching the entire page.
     * <p>
     * The label is the text of the component's own {@code label} slot, matched in
     * full and case-sensitively. An {@code aria-label} or a placeholder is not a
     * label: use {@code find(page, TimePickerElement.class).withAccessibleName(..)} or
     * {@code .withPlaceholder(..)}. No {@code .first()} is applied: two components
     * with that label fail with a strict-mode error. Same as
     * {@code find(page, TimePickerElement.class).withLabel(label)}; use
     * {@link #getByLabelContaining(Page, String)} to match part of the label.
     *
     * @param page  the Playwright page
     * @param label the full visible label
     * @return the matching {@code TimePickerElement}
     */
    public static TimePickerElement getByLabel(Page page, String label) {
        return new TimePickerElement(ElementQueries.find(page, TimePickerElement.class).withLabel(label).getLocator());
    }

    /**
     * Get the {@code TimePickerElement} whose visible label contains {@code text}, searching the entire page.
     * <p>
     * The label is matched case-insensitively as a substring; an {@code aria-label}
     * or a placeholder is not a label. No {@code .first()} is applied: several
     * matching components fail with a strict-mode error. Same as
     * {@code find(page, TimePickerElement.class).withLabelContaining(text)}.
     *
     * @param page  the Playwright page
     * @param text  part of the visible label
     * @return the matching {@code TimePickerElement}
     */
    public static TimePickerElement getByLabelContaining(Page page, String text) {
        return new TimePickerElement(ElementQueries.find(page, TimePickerElement.class).withLabelContaining(text).getLocator());
    }

    /**
     * Get the {@code TimePickerElement} whose visible label is exactly {@code label}, scoped to the given locator.
     * <p>
     * The label is the text of the component's own {@code label} slot, matched in
     * full and case-sensitively. An {@code aria-label} or a placeholder is not a
     * label: use {@code find(locator, TimePickerElement.class).withAccessibleName(..)} or
     * {@code .withPlaceholder(..)}. No {@code .first()} is applied: two components
     * with that label fail with a strict-mode error. Same as
     * {@code find(locator, TimePickerElement.class).withLabel(label)}; use
     * {@link #getByLabelContaining(Locator, String)} to match part of the label.
     *
     * @param locator the locator whose subtree is searched
     * @param label   the full visible label
     * @return the matching {@code TimePickerElement}
     */
    public static TimePickerElement getByLabel(Locator locator, String label) {
        return new TimePickerElement(ElementQueries.find(locator, TimePickerElement.class).withLabel(label).getLocator());
    }

    /**
     * Get the {@code TimePickerElement} whose visible label contains {@code text}, scoped to the given locator.
     * <p>
     * The label is matched case-insensitively as a substring; an {@code aria-label}
     * or a placeholder is not a label. No {@code .first()} is applied: several
     * matching components fail with a strict-mode error. Same as
     * {@code find(locator, TimePickerElement.class).withLabelContaining(text)}.
     *
     * @param locator the locator whose subtree is searched
     * @param text    part of the visible label
     * @return the matching {@code TimePickerElement}
     */
    public static TimePickerElement getByLabelContaining(Locator locator, String text) {
        return new TimePickerElement(ElementQueries.find(locator, TimePickerElement.class).withLabelContaining(text).getLocator());
    }

    // ── Lookup by id or test id ────────────────────────────────────────

    /**
     * Get a {@code TimePickerElement} by its {@code id}, searching the entire page.
     * <p>
     * A fallback for a component without a user-visible name: prefer the label or
     * text lookups when there is one. The id must match in full; two components
     * with that id fail with a strict-mode error.
     *
     * @param page the Playwright page
     * @param id   the full {@code id}
     * @return the matching {@code TimePickerElement}
     */
    public static TimePickerElement getById(Page page, String id) {
        return new TimePickerElement(IdLocator.byId(page, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code TimePickerElement} by its {@code id}, scoped to the given locator.
     * <p>
     * A fallback for a component without a user-visible name. The id must match in
     * full; two components with that id inside the scope fail with a strict-mode
     * error.
     *
     * @param locator the locator whose subtree is searched
     * @param id      the full {@code id}
     * @return the matching {@code TimePickerElement}
     */
    public static TimePickerElement getById(Locator locator, String id) {
        return new TimePickerElement(IdLocator.byId(locator, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code TimePickerElement} by its test id, searching the entire page.
     * <p>
     * The test id is Playwright's test id attribute, {@code data-testid} unless
     * reconfigured — the attribute Flow's {@code Component.setTestId(String)} sets.
     * A fallback for a component without a user-visible name; it must match in
     * full, and two components with that test id fail with a strict-mode error.
     *
     * @param page   the Playwright page
     * @param testId the full test id
     * @return the matching {@code TimePickerElement}
     */
    public static TimePickerElement getByTestId(Page page, String testId) {
        return new TimePickerElement(IdLocator.byTestId(page, FIELD_TAG_NAME, testId));
    }

    /**
     * Get a {@code TimePickerElement} by its test id, scoped to the given locator.
     * <p>
     * See {@link #getByTestId(Page, String)}; only components inside the scope are
     * considered.
     *
     * @param locator the locator whose subtree is searched
     * @param testId  the full test id
     * @return the matching {@code TimePickerElement}
     */
    public static TimePickerElement getByTestId(Locator locator, String testId) {
        return new TimePickerElement(IdLocator.byTestId(locator, FIELD_TAG_NAME, testId));
    }
}
