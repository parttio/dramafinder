package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.HasTestView;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.addons.dramafinder.element.DetailsElement;
import org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class DetailsViewIT extends SpringPlaywrightIT implements HasTestView {

    @Override
    public String getView() {
        return "details";
    }

    @Test
    public void testBasicDetails() {
        DetailsElement details = DetailsElement.getBySummaryText(page, "Basic Details");
        details.assertClosed();
        details.assertContentNotVisible();
        details.setOpen(true);
        details.assertOpened();
        details.assertContentVisible();
        details.getContentLocator().isVisible();
    }

    @Test
    public void testHasClass() {
        DetailsElement details = DetailsElement.getBySummaryText(page, "Basic Details");
        details.assertCssClass("custom-css");
    }

    @Test
    public void testTheme() {
        DetailsElement details = DetailsElement.getBySummaryText(page, "Basic Details");
        details.assertVisible();
        details.assertTheme("small");
    }

    @Test
    public void testTooltip() {
        DetailsElement details = DetailsElement.getBySummaryText(page, "Basic Details");
        details.assertVisible();
        details.assertTooltipHasText("Tooltip for component");
    }

    @Test
    public void testOpenedDetails() {
        DetailsElement details = DetailsElement.getBySummaryText(page, "Opened Details");
        details.assertOpened();
        details.setOpen(false);
        details.assertClosed();
    }

    @Test
    public void testDisabledDetails() {
        DetailsElement details = DetailsElement.getBySummaryText(page, "Disabled Details");
        details.assertDisabled();
        assertFalse(details.isEnabled());
        assertTrue(details.isEnabled(false));
        details.assertClosed();

        DetailsElement enabled = DetailsElement.getBySummaryText(page, "Basic Details");
        enabled.assertEnabled();
        assertTrue(enabled.isEnabled());
    }

    @Test
    public void testButtonInSummary() {
        DetailsElement details = DetailsElement.getBySummaryText(page, "Summary Component test");
        ButtonElement buttonElement = ButtonElement.getByText(details.getSummaryLocator(), "test");
        buttonElement.isVisible();
        details.assertClosed();
        buttonElement.click();
        details.assertOpened();
    }

    @Test
    public void testNestedDetails() {
        DetailsElement outer = DetailsElement.getBySummaryText(page, "Outer Details");
        outer.assertClosed();
        assertThat(outer.getContentLocator()).hasCount(1);
        outer.assertContentNotVisible();
        assertThat(outer.getSummaryLocator()).hasCount(1);

        outer.setOpen(true);
        outer.assertOpened();
        outer.assertContentVisible();
        assertThat(outer.getContentLocator()).containsText("Outer content.");

        DetailsElement inner = DetailsElement.getBySummaryText(page, "Inner Details");
        inner.assertClosed();
        inner.assertContentNotVisible();
        inner.toggle();
        inner.assertOpened();
        inner.assertContentVisible();
        assertEquals("Inner Details", inner.getSummaryText());

        outer.setOpen(false);
        outer.assertClosed();
        outer.assertContentNotVisible();

        outer.toggle();
        outer.assertOpened();
        // the inner details kept their state while hidden
        inner.assertOpened();
    }
}
