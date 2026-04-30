package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.PasswordFieldElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PasswordFieldGetByLabelIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "passwordfield-getbylabel";
    }

    @Test
    public void getByLabel_page_placeholder() {
        PasswordFieldElement field = PasswordFieldElement.getByLabel(page, "My Placeholder");
        assertThat(field.getLocator()).isVisible();
    }

    @Test
    public void getByLabel_page_ariaLabel() {
        PasswordFieldElement field = PasswordFieldElement.getByLabel(page, "My AriaLabel");
        assertThat(field.getLocator()).isVisible();
    }
}
