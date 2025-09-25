package org.vaadin.addons.dramafinderdemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.HasTestView;
import org.vaadin.addons.dramafinder.element.AccordionElement;
import org.vaadin.addons.dramafinder.element.AccordionPanelElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AccordionViewIT extends SpringPlaywrightIT implements HasTestView {

    @Override
    public String getView() {
        return "accordion";
    }

    @Test
    public void testAccordion() {
        AccordionElement accordion = new AccordionElement(page.locator(".custom-css"));
        accordion.assertPanelCount(4);

        AccordionPanelElement panel1 = accordion.getPanel("Panel 1");
        panel1.assertOpened();
        panel1.assertContentVisible();

        AccordionPanelElement panel2 = accordion.getPanel("Panel 2");
        panel2.assertClosed();
        accordion.openPanel("Panel 2");
        panel1.assertClosed();
        panel2.assertOpened();

        AccordionPanelElement disabledPanel = accordion.getPanel("Disabled Panel");
        disabledPanel.assertDisabled();
        disabledPanel.assertClosed();
        disabledPanel.getSummaryLocator().click();
        disabledPanel.assertClosed();

        AccordionPanelElement openedPanel = accordion.getOpenedPanel();
        assertEquals("Panel 2", openedPanel.getSummaryText());
    }

    @Test
    public void testHasClass() {
        AccordionElement accordion = new AccordionElement(page.locator(".custom-css"));
        accordion.assertCssClass("custom-css");
    }


    @Test
    public void testContent() {
        AccordionElement accordion = new AccordionElement(page.locator(".custom-css"));

        AccordionPanelElement panel1 = accordion.getPanel("Panel 1");
        panel1.assertOpened();
        panel1.assertContentVisible();
        assertThat(panel1.getContentLocator()).hasText("Content 1");
    }
}
