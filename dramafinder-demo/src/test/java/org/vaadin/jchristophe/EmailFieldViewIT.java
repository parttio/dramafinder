package org.vaadin.jchristophe;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.dramafinder.element.EmailFieldElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class EmailFieldViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "email";
    }


    @Test
    public void testTitle() {
        assertThat(page).hasTitle("EmailField Demo");
        assertThat(page.getByText("EmailField Demo")).isVisible();
    }

    @Test
    public void testBasicEmailField() {
        EmailFieldElement emailField = EmailFieldElement.getByLabel(page, "Email");
        emailField.assertVisible();
        emailField.assertTooltipHasText("Tooltip for email field");
        emailField.assertPlaceholder("your@email.com");
        emailField.assertPrefixHasText("@");

        // Test clear button
        emailField.assertClearButtonNotVisible();
        emailField.setValue("test@vaadin.com");
        emailField.assertValue("test@vaadin.com");
        emailField.assertClearButtonVisible();
        emailField.clickClearButton();
        emailField.assertValue("");
    }

    @Test
    public void testValidation() {
        EmailFieldElement emailField = EmailFieldElement.getByLabel(page, "Validated Email");
        emailField.assertVisible();
        emailField.assertHelperHasText("This field is required.");

        // Enter an invalid email
        emailField.setValue("not-an-email");
        emailField.assertInvalid();
        emailField.assertErrorMessage("Please enter a valid email address");

        // Enter a valid email
        emailField.setValue("valid@email.com");
        emailField.assertValid();

        emailField.clear();

        // Field is required, so it should be invalid initially
        emailField.assertInvalid();
        emailField.assertErrorMessage("Please enter a valid email address");

    }
}
