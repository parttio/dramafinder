package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.TextFieldElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TextFieldGetByLabelIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "textfield-getbylabel";
    }

    @Test
    public void getByLabel_page_label() {
        TextFieldElement field = TextFieldElement.getByLabel(page, "My Label");
        assertThat(field.getLocator()).isVisible();
    }

    @Test
    public void getByLabel_page_placeholder() {
        TextFieldElement field = TextFieldElement.getByLabel(page, "My Placeholder");
        assertThat(field.getLocator()).isVisible();
    }

    @Test
    public void getByLabel_page_ariaLabel() {
        TextFieldElement field = TextFieldElement.getByLabel(page, "My AriaLabel");
        assertThat(field.getLocator()).isVisible();
    }

    @Test
    public void getByLabel_locator_label() {
        TextFieldElement field = TextFieldElement.getByLabel(page.locator("#container"), "My Label");
        assertThat(field.getLocator()).isVisible();
    }

    @Test
    public void getByLabel_locator_placeholder() {
        TextFieldElement field = TextFieldElement.getByLabel(page.locator("#container"), "My Placeholder");
        assertThat(field.getLocator()).isVisible();
    }

    @Test
    public void getByLabel_locator_ariaLabel() {
        TextFieldElement field = TextFieldElement.getByLabel(page.locator("#container"), "My AriaLabel");
        assertThat(field.getLocator()).isVisible();
    }

    @Test
    public void getByLabel_page_findsOutsideContainer() {
        TextFieldElement field = TextFieldElement.getByLabel(page, "Outside Label");
        assertThat(field.getLocator()).isVisible();
    }

    @Test
    public void getByLabel_locator_doesNotFindOutsideContainer() {
        TextFieldElement field = TextFieldElement.getByLabel(page.locator("#container"), "Outside Label");
        assertThat(field.getLocator()).isHidden();
    }
}
