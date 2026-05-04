package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.MultiSelectComboBoxElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MultiSelectComboBoxGetByLabelIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "multiselectcombobox-getbylabel";
    }

    @Test
    public void getByLabel_page_placeholder() {
        MultiSelectComboBoxElement field = MultiSelectComboBoxElement.getByLabel(page, "My Placeholder");
        assertThat(field.getLocator()).isVisible();
    }

    @Test
    public void getByLabel_page_ariaLabel() {
        MultiSelectComboBoxElement field = MultiSelectComboBoxElement.getByLabel(page, "My AriaLabel");
        assertThat(field.getLocator()).isVisible();
    }

    @Test
    public void getByLabel_locator_doesNotFindOutsideContainer() {
        MultiSelectComboBoxElement field = MultiSelectComboBoxElement.getByLabel(page.locator("#container"), "Outside Label");
        assertThat(field.getLocator()).isHidden();
    }
}
