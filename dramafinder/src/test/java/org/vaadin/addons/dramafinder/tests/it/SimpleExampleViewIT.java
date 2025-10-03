package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.TextFieldElement;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SimpleExampleViewIT extends SpringPlaywrightIT {
    @Test
    public void testTooltip() {
        // get a text that has an accessible name (label or aria label,...) equals to text field
        TextFieldElement textfield = TextFieldElement.getByLabel(page, "Textfield");
        // assert that is visible
        textfield.assertVisible();
        // assert that the textfield has a tooltip
        textfield.assertTooltipHasText("Tooltip for textfield");
    }
}
