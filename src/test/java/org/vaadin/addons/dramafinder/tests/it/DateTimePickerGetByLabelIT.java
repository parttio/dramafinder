package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.DateTimePickerElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class DateTimePickerGetByLabelIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "datetimepicker-getbylabel";
    }

    @Test
    public void getByLabel_page_placeholder() {
        DateTimePickerElement field = DateTimePickerElement.getByLabel(page, "My Placeholder");
        assertThat(field.getLocator()).isVisible();
    }

    @Test
    public void getByLabel_page_ariaLabel() {
        DateTimePickerElement field = DateTimePickerElement.getByLabel(page, "My AriaLabel");
        assertThat(field.getLocator()).isVisible();
    }

    @Test
    public void getByLabel_locator_doesNotFindOutsideContainer() {
        DateTimePickerElement field = DateTimePickerElement.getByLabel(page.locator("#container"), "Outside Label");
        assertThat(field.getLocator()).isHidden();
    }
}
