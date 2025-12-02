package org.vaadin.addons.dramafinder.tests.it;

import com.microsoft.playwright.Locator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.HasTestView;
import org.vaadin.addons.dramafinder.element.SplitLayoutElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SplitLayoutViewIT extends SpringPlaywrightIT implements HasTestView {

    @Override
    public String getView() {
        return "splitlayout";
    }

    @Test
    public void testDefaultHorizontal() {
        SplitLayoutElement split = SplitLayoutElement.get(page);
        assertThat(split.getLocator()).isVisible();
        split.assertHorizontal();

        assertThat(split.getPrimaryLocator().getByText("Primary 1")).isVisible();
        assertThat(split.getSecondaryLocator().getByText("Secondary 1")).isVisible();

    }

    @Test
    public void testVertical() {
        Locator verticalHost = page.locator(SplitLayoutElement.FIELD_TAG_NAME + "[orientation='vertical']").first();
        SplitLayoutElement vertical = new SplitLayoutElement(verticalHost);
        vertical.assertVertical();
        assertThat(vertical.getPrimaryLocator().getByText("Primary V")).isVisible();
        assertThat(vertical.getSecondaryLocator().getByText("Secondary V")).isVisible();
    }

    @Test
    public void testSplitterPositionSet() {
        page.setViewportSize(1600, 1400);
        Locator positionedHost = page.locator(SplitLayoutElement.FIELD_TAG_NAME).nth(2);
        SplitLayoutElement splitLayoutElement = new SplitLayoutElement(positionedHost);
        assertThat(splitLayoutElement.getLocator()).isVisible();
        assertWidth(splitLayoutElement.getPrimaryLocator(), 945, 5);
        splitLayoutElement.dragSplitterBy(-100, 0);
        assertWidth(splitLayoutElement.getPrimaryLocator(), 845, 5);
        splitLayoutElement.dragSplitterBy(-100, 0);
        assertWidth(splitLayoutElement.getPrimaryLocator(), 745, 5);
    }

    private static void assertWidth(Locator locator, double expectedWidth, double tolerance) {
        var box = locator.boundingBox();
        if (box == null) {
            throw new AssertionError("Element not found or not visible — cannot read width.");
        }

        double actual = box.width;

        if (Math.abs(actual - expectedWidth) > tolerance) {
            throw new AssertionError(
                    "Expected width: " + expectedWidth + "px but was: " + actual + "px"
            );
        }
    }

}
