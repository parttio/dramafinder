package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.BigDecimalFieldElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class BigDecimalFieldViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "bigdecimal";
    }

    @Test
    public void testTitle() {
        assertThat(page).hasTitle("BigDecimalField Demo");
        assertThat(page.getByText("BigDecimalField Demo")).isVisible();
    }

    @Test
    public void testBasicBigDecimalField() {
        BigDecimalFieldElement bigDecimalField = BigDecimalFieldElement.getByLabel(page, "Amount");
        bigDecimalField.assertVisible();
        bigDecimalField.assertHelperHasText("Enter a precise decimal value");

        // Check initial value
        bigDecimalField.assertValue("123.456789");

        // Set a new value
        String newValue = "98765.4321";
        bigDecimalField.setValue(newValue);
        bigDecimalField.assertValue(newValue);
    }
}
