package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.addons.dramafinder.element.CustomFieldElement;
import org.vaadin.addons.dramafinder.element.SelectElement;
import org.vaadin.addons.dramafinder.element.TextFieldElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CustomFieldViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "custom-field";
    }

    @Test
    public void readsLabelHelperAndStates() {
        CustomFieldElement phone = CustomFieldElement.getByLabel(page, "Phone");
        phone.assertVisible();
        phone.assertLabel("Phone");
        assertEquals("Phone", phone.getLabel());
        phone.assertHelperHasText("Country code and number");
        phone.assertRequired();
        phone.assertValid();
        phone.assertEnabled();
        phone.assertNotReadOnly();
        assertFalse(phone.isReadOnly());
    }

    @Test
    public void setsValueThroughWrappedFields() {
        CustomFieldElement phone = CustomFieldElement.getByLabel(page, "Phone");
        phone.getField(SelectElement.class, "Country code").selectItem("+358");
        TextFieldElement number = phone.getField(TextFieldElement.class, "Number");
        number.setValue("40 1234567");
        number.assertValue("40 1234567");
        assertThat(page.locator("#phone-value")).hasText("+358 40 1234567");
    }

    @Test
    public void readsPrefilledValueByPosition() {
        CustomFieldElement office = CustomFieldElement.getByLabel(page, "Office phone");
        office.getField(SelectElement.class, 0).assertValue("+49");
        office.getField(TextFieldElement.class, 0).assertValue("30 1234567");
    }

    @Test
    public void labelMatchesAsCaseInsensitiveSubstring() {
        CustomFieldElement.getByLabel(page, "office").assertLabel("Office phone");
    }

    @Test
    public void scopedLookupStaysInsideTheParent() {
        CustomFieldElement fax = CustomFieldElement.getByLabel(page.locator("main"), "Fax");
        fax.assertLabel("Fax");
    }

    @Test
    public void reportsInvalidStateAndErrorMessage() {
        CustomFieldElement mobile = CustomFieldElement.getByLabel(page, "Mobile phone");
        mobile.assertValid();
        ButtonElement.getByText(page, "Mark mobile invalid").click();
        mobile.assertInvalid();
        mobile.assertErrorMessage("Enter a valid number");
        mobile.assertNotRequired();
    }

    @Test
    public void disabledFieldDisablesWrappedFields() {
        CustomFieldElement fax = CustomFieldElement.getByLabel(page, "Fax");
        fax.assertDisabled();
        assertFalse(fax.isEnabled());
        fax.getField(TextFieldElement.class, "Number").assertDisabled();
    }

    @Test
    public void readOnlyIsReadFromTheProperty() {
        CustomFieldElement home = CustomFieldElement.getByLabel(page, "Home phone");
        home.assertReadOnly();
        assertTrue(home.isReadOnly());
    }

    @Test
    public void tooltipAndClassName() {
        CustomFieldElement work = CustomFieldElement.getByLabel(page, "Work phone");
        work.assertTooltipHasText("Your direct line");
        work.assertCssClass("work-phone");
    }

    @Test
    public void fieldByLabelNeedsAScopedFactory() {
        CustomFieldElement phone = CustomFieldElement.getByLabel(page, "Phone");
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> phone.getField(ButtonElement.class, "Country code"));
        assertTrue(error.getMessage().contains("getByLabel(Locator, String)"), error.getMessage());
    }
}
