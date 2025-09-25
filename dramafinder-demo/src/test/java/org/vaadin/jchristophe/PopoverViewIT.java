package org.vaadin.jchristophe;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.dramafinder.HasTestView;
import org.vaadin.dramafinder.element.ButtonElement;
import org.vaadin.dramafinder.element.PopoverElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PopoverViewIT extends SpringPlaywrightIT implements HasTestView {

    @Override
    public String getView() {
        return "popover";
    }

    @Test
    public void testPopover() {
        ButtonElement openPopover = ButtonElement.getByText(page, "Open Popover");
        openPopover.click();
        PopoverElement popoverElement = PopoverElement.getByLabel(page, "Basic Popover");
        popoverElement.assertOpen();

        assertThat(popoverElement.getContentLocator()).hasText("This is a popover");
        openPopover.click();
        popoverElement.assertClosed();
    }

    @Test
    public void testPopoverComponent() {
        ButtonElement openComponentPopover = ButtonElement.getByText(page, "Open component Popover");
        openComponentPopover.click();

        PopoverElement popoverElement = PopoverElement.getByLabel(page, "Component Popover");
        popoverElement.assertOpen();

        ButtonElement closePopover = ButtonElement.getByText(popoverElement.getContentLocator(), "Close");
        closePopover.click();
        popoverElement.assertClosed();
    }

    @Test
    public void testStayOpenedPopoverComponent() {
        ButtonElement openStayOpenedPopover = ButtonElement.getByText(page, "Open stay opened Popover");
        openStayOpenedPopover.click();

        PopoverElement popoverElement = PopoverElement.getByLabel(page, "Stay opened Popover");
        popoverElement.assertOpen();

        ButtonElement openComponentPopover = ButtonElement.getByText(page, "Open component Popover");
        openComponentPopover.click();

        PopoverElement componentPopover = PopoverElement.getByLabel(page, "Component Popover");
        componentPopover.assertOpen();
        assertThat(popoverElement.getContentLocator()).hasText("This is a popover");
        assertThat(componentPopover.getContentLocator()).hasText("This is a popoverClose component Popover");

        ButtonElement closePopover = ButtonElement.getByText(componentPopover.getContentLocator(), "Close");
        closePopover.click();
        componentPopover.assertClosed();
    }


    @Test
    public void testHasClass() {
        ButtonElement openComponentPopover = ButtonElement.getByText(page, "Open component Popover");
        openComponentPopover.click();

        PopoverElement popoverElement = PopoverElement.getByLabel(page, "Component Popover");
        popoverElement.assertCssClass("component-popover");
    }

    @Test
    public void testTheme() {
        ButtonElement openComponentPopover = ButtonElement.getByText(page, "Open component Popover");
        openComponentPopover.click();

        PopoverElement popoverElement = PopoverElement.getByLabel(page, "Component Popover");
        popoverElement.assertTheme("arrow");
    }
}
