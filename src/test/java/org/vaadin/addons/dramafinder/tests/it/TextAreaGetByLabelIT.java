package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.TextAreaElement;
import org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.vaadin.addons.dramafinder.element.query.ElementQueries.find;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TextAreaGetByLabelIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "textarea-getbylabel";
    }

    @Test
    public void getByLabel_page_placeholderIsNotALabel() {
        // a placeholder is not a label: withPlaceholder is the replacement
        assertThat(TextAreaElement.getByLabel(page, "My Placeholder").getLocator()).hasCount(0);
        assertThat(find(page, TextAreaElement.class).withPlaceholder("My Placeholder").getLocator()).isVisible();
    }

    @Test
    public void getByLabel_page_ariaLabelIsNotALabel() {
        // an aria-label is not a label: withAccessibleName is the replacement
        assertThat(TextAreaElement.getByLabel(page, "My AriaLabel").getLocator()).hasCount(0);
        assertThat(find(page, TextAreaElement.class).withAccessibleName("My AriaLabel").getLocator()).isVisible();
    }
}
