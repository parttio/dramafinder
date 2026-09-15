package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.HasTestView;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.addons.dramafinder.element.TextFieldElement;
import org.vaadin.addons.dramafinder.element.TooltipElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TooltipViewIT extends SpringPlaywrightIT implements HasTestView {

    @Override
    public String getView() {
        return "tooltip";
    }

    private TooltipElement tooltipOf(String componentId) {
        return TooltipElement.get(page.locator("#" + componentId));
    }

    // ── Page title ────────────────────────────────────────────────────

    @Test
    public void testTitle() {
        assertThat(page).hasTitle("Tooltip Demo");
    }

    // ── Factory methods ───────────────────────────────────────────────

    @Test
    public void testGetByPage() {
        TooltipElement tooltip = TooltipElement.get(page);
        tooltip.assertText("Tooltip for the hover button");
    }

    @Test
    public void testGetByLocator() {
        TooltipElement tooltip = TooltipElement.get(page.locator("#tooltip-positioned-button"));
        tooltip.assertText("Shown above the button");
    }

    @Test
    public void testGetByText() {
        TooltipElement tooltip = TooltipElement.getByText(page, "Shown above the button");
        tooltip.assertText("Shown above the button");
        tooltip.assertPosition("top-start");
    }

    // ── Text ──────────────────────────────────────────────────────────

    @Test
    public void testText() {
        TooltipElement tooltip = tooltipOf("tooltip-hover-button");
        tooltip.assertText("Tooltip for the hover button");
        assertEquals("Tooltip for the hover button", tooltip.getText());
    }

    @Test
    public void testTextIsReadableWhileClosed() {
        TooltipElement tooltip = tooltipOf("tooltip-hover-button");
        tooltip.assertClosed();
        tooltip.assertText("Tooltip for the hover button");
    }

    @Test
    public void testNoText() {
        TooltipElement tooltip = tooltipOf("tooltip-empty-button");
        assertThat(tooltip.getLocator()).hasCount(1);
        tooltip.assertText(null);
        assertEquals("", tooltip.getText());
    }

    @Test
    public void testTextIsUpdated() {
        TooltipElement tooltip = tooltipOf("tooltip-dynamic-field");
        tooltip.assertText("Initial tooltip");

        ButtonElement.getByText(page, "Update tooltip").click();

        tooltip.assertText("Updated tooltip");
    }

    // ── Opened state ──────────────────────────────────────────────────

    @Test
    public void testClosedByDefault() {
        TooltipElement tooltip = tooltipOf("tooltip-hover-button");
        tooltip.assertClosed();
        assertFalse(tooltip.isOpened());
    }

    @Test
    public void testHoverTargetOpensTooltip() {
        TooltipElement tooltip = tooltipOf("tooltip-instant-button");
        tooltip.assertClosed();

        tooltip.hoverTarget();

        tooltip.assertOpened();
        assertTrue(tooltip.isOpened());
        assertThat(tooltip.getOverlayLocator()).isVisible();
    }

    @Test
    public void testOverlayVisibilityFollowsOpenedState() {
        TooltipElement tooltip = tooltipOf("tooltip-instant-button");
        assertThat(tooltip.getOverlayLocator()).isHidden();

        tooltip.hoverTarget();

        tooltip.assertOpened();
        assertThat(tooltip.getOverlayLocator()).isVisible();
    }

    @Test
    public void testHoverTargetOpensTooltipAfterHoverDelay() {
        TooltipElement tooltip = tooltipOf("tooltip-hover-button");

        tooltip.hoverTarget();

        // The default 500 ms hover delay is absorbed by the retrying assertion.
        tooltip.assertOpened();
    }

    @Test
    public void testCloseWithEscape() {
        TooltipElement tooltip = tooltipOf("tooltip-instant-button");
        tooltip.hoverTarget();
        tooltip.assertOpened();

        tooltip.closeWithEscape();

        tooltip.assertClosed();
    }

    @Test
    public void testManualTooltip() {
        TooltipElement tooltip = tooltipOf("tooltip-manual-button");
        tooltip.assertClosed();

        ButtonElement.getByText(page, "Toggle manual tooltip").click();
        tooltip.assertOpened();

        ButtonElement.getByText(page, "Toggle manual tooltip").click();
        tooltip.assertClosed();
    }

    // ── Position ──────────────────────────────────────────────────────

    @Test
    public void testDefaultPosition() {
        TooltipElement tooltip = tooltipOf("tooltip-hover-button");
        tooltip.assertPosition("bottom");
        assertEquals("bottom", tooltip.getPosition());
    }

    @Test
    public void testConfiguredPosition() {
        TooltipElement tooltip = tooltipOf("tooltip-positioned-button");
        tooltip.assertPosition("top-start");
        assertEquals("top-start", tooltip.getPosition());
    }

    // ── Interface mixins ──────────────────────────────────────────────

    @Test
    public void testTheme() {
        TooltipElement tooltip = tooltipOf("tooltip-styled-field");
        tooltip.assertTheme("custom-tooltip-theme");
        tooltip.assertHasThemeVariant("custom-tooltip-theme");
        assertEquals("custom-tooltip-theme", tooltip.getTheme());
    }

    @Test
    public void testCssClass() {
        TooltipElement tooltip = tooltipOf("tooltip-styled-field");
        tooltip.assertCssClass("custom-tooltip");
        assertEquals("custom-tooltip", tooltip.getCssClass());
    }

    // ── HasTooltipElement mixin ───────────────────────────────────────

    @Test
    public void testMixinReturnsTooltipElement() {
        ButtonElement button = ButtonElement.getByText(page, "Hover me");
        TooltipElement tooltip = button.getTooltip();

        tooltip.assertText("Tooltip for the hover button");
        tooltip.assertClosed();
        tooltip.assertPosition("bottom");
    }

    @Test
    public void testMixinTextHelpers() {
        TextFieldElement field = TextFieldElement.getByLabel(page, "Dynamic");

        field.assertTooltipHasText("Initial tooltip");
        assertEquals("Initial tooltip", field.getTooltipText());
        assertThat(field.getTooltipLocator()).hasCount(1);
    }

    @Test
    public void testMixinHoverOpensTooltip() {
        ButtonElement button = ButtonElement.getByText(page, "Instant");

        button.getTooltip().hoverTarget();

        button.getTooltip().assertOpened();
    }
}
