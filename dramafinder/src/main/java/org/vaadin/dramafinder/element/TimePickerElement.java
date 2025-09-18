package org.vaadin.dramafinder.element;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.dramafinder.element.shared.FocusableElement;
import org.vaadin.dramafinder.element.shared.HasAriaLabelElement;
import org.vaadin.dramafinder.element.shared.HasClearButtonElement;
import org.vaadin.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.dramafinder.element.shared.HasHelperElement;
import org.vaadin.dramafinder.element.shared.HasInputFieldElement;
import org.vaadin.dramafinder.element.shared.HasLabelElement;
import org.vaadin.dramafinder.element.shared.HasPlaceholderElement;
import org.vaadin.dramafinder.element.shared.HasThemeElement;
import org.vaadin.dramafinder.element.shared.HasTooltipElement;
import org.vaadin.dramafinder.element.shared.HasValidationPropertiesElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@PlaywrightElement(TimePickerElement.FIELD_TAG_NAME)
public class TimePickerElement extends VaadinElement implements HasInputFieldElement, HasValidationPropertiesElement,
        HasClearButtonElement, HasPlaceholderElement, HasThemeElement, FocusableElement, HasAriaLabelElement,
        HasEnabledElement, HasTooltipElement, HasLabelElement, HasHelperElement {

    public static final String FIELD_TAG_NAME = "vaadin-time-picker";
    public static final DateTimeFormatter LOCAL_TIME = DateTimeFormatter.ofPattern("HH:mm");

    public TimePickerElement(Locator locator) {
        super(locator);
    }

    public void setValue(LocalTime time) {
        String formattedTime = time.format(LOCAL_TIME);
        setProperty("value", formattedTime);
    }

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
     * Check if the value equals to the parameter
     *
     * @param value
     */
    public void assertValue(LocalTime value) {
        if (value != null) {
            assertThat(getLocator()).hasJSProperty("value", value.format(LOCAL_TIME));
        } else {
            assertValue("");
        }
    }

    public static TimePickerElement getByLabel(Page page, String label) {
        return new TimePickerElement(
                page.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions()
                                .setHas(page.getByRole(AriaRole.COMBOBOX,
                                        new Page.GetByRoleOptions().setName(label)))
                        ).first());
    }

    public static TimePickerElement getByLabel(Locator locator, String label) {
        return new TimePickerElement(
                locator.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions().setHas(locator.getByLabel(label)))
                        .first());
    }
}
