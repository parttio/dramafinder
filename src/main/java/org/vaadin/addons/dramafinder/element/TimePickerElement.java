package org.vaadin.addons.dramafinder.element;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
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

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-time-picker>}.
 * <p>
 * Adds convenience methods for {@link LocalTime} values and lookup by label.
 */
@PlaywrightElement(TimePickerElement.FIELD_TAG_NAME)
public class TimePickerElement extends VaadinElement implements HasInputFieldElement, HasValidationPropertiesElement,
        HasClearButtonElement, HasPlaceholderElement, HasThemeElement, FocusableElement, HasAriaLabelElement,
        HasEnabledElement, HasTooltipElement, HasLabelElement, HasHelperElement {

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
     * Get the {@code TimePickerElement} by its label.
     *
     * @param page  the Playwright page
     * @param label the accessible label of the field
     * @return the matching {@code TimePickerElement}
     */
    public static TimePickerElement getByLabel(Page page, String label) {
        return new TimePickerElement(
                page.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions()
                                .setHas(page.getByRole(AriaRole.COMBOBOX,
                                        new Page.GetByRoleOptions().setName(label)))
                        ).first());
    }

    /**
     * Get the {@code TimePickerElement} by its label within a given scope.
     *
     * @param locator the locator to search within
     * @param label   the accessible label of the field
     * @return the matching {@code TimePickerElement}
     */
    public static TimePickerElement getByLabel(Locator locator, String label) {
        return new TimePickerElement(
                locator.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions().setHas(locator.getByLabel(label)))
                        .first());
    }
}
