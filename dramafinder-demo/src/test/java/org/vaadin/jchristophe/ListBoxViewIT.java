package org.vaadin.jchristophe;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.dramafinder.element.ListBoxElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ListBoxViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "list-box";
    }

    @Test
    public void testTitle() {
        assertThat(page).hasTitle("ListBox Demo");
    }

    @Test
    public void testValue() {
        ListBoxElement listBox = ListBoxElement.getByLabel(page, "Invisible label");
        listBox.assertVisible();
        listBox.assertSelectedValue("Most recent first");
        listBox.selectItem("Rating: high to low");
        listBox.assertSelectedValue("Rating: high to low");
    }

    @Test
    public void testHasClass() {
        ListBoxElement listBox = ListBoxElement.getByLabel(page, "Invisible label");
        listBox.assertCssClass("list-box");
    }

    @Test
    public void testAriaLabel() {
        ListBoxElement listBox = ListBoxElement.getByLabel(page, "Invisible label");
        listBox.assertVisible();
        listBox.assertAriaLabel("Invisible label");
    }

    @Test
    public void testAriaLabelledBy() {
        ListBoxElement listBox = ListBoxElement.getByLabel(page, "Labelled by");
        listBox.assertVisible();
        listBox.assertAriaLabel(null);
    }

    @Test
    public void testEnabledDisabled() {
        ListBoxElement listBox = ListBoxElement.getByLabel(page, "Enabled/Disabled Field");
        listBox.assertDisabled();
        listBox.assertItemDisabled("Most recent first");
        listBox.assertItemDisabled("Always disabled");

        page.locator("#enable-disable-button").click();

        listBox.assertEnabled();
        listBox.assertItemEnabled("Most recent first");
        listBox.assertItemDisabled("Always disabled");
    }

    @Test
    public void testTooltip() {
        ListBoxElement listBoxElement = ListBoxElement.getByLabel(page, "Invisible label");
        listBoxElement.assertVisible();
        listBoxElement.assertTooltipHasText("Tooltip for listbox");
    }
}
