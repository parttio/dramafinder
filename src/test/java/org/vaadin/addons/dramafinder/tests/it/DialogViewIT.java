package org.vaadin.addons.dramafinder.tests.it;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.HasTestView;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.addons.dramafinder.element.DialogElement;
import org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.vaadin.addons.dramafinder.element.query.ElementQueries.find;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class DialogViewIT extends SpringPlaywrightIT implements HasTestView {

    @Override
    public String getView() {
        return "dialog";
    }

    @Test
    public void testModalDialog() {
        ButtonElement openDialogButton = ButtonElement.getByText(page,
                new Page.GetByRoleOptions()
                        .setName("Open dialog").setExact(true));
        openDialogButton.click();

        DialogElement dialog = new DialogElement(page);
        dialog.assertOpen();
        dialog.assertVisible();
        dialog.assertModal();
        dialog.assertHeaderText("My Dialog");

        assertThat(dialog.getContentLocator()).hasText("This is the content of the dialog.");

        ButtonElement closeButton = ButtonElement.getByText(dialog.getFooterLocator(), "Close");
        closeButton.click();

        dialog.assertClosed();
    }


    @Test
    public void testNonModalDialogs() {
        ButtonElement openDialog1Button = ButtonElement.getByText(page, "Open non-modal 1");
        openDialog1Button.click();

        // the main part is aria-hidden so it's not accessible anymore
        ButtonElement openDialog2Button = ButtonElement.getByText(page,
                new Page.GetByRoleOptions()
                        .setName("Open non-modal 2"));
        openDialog2Button.assertVisible();
        DialogElement dialog1Element = DialogElement.getByHeaderText(page, "My non-modal Dialog 1");
        dialog1Element.assertOpen();
        dialog1Element.assertModeless();

        openDialog2Button.click();

        DialogElement dialog2Element = DialogElement.getByHeaderText(page, "My non-modal Dialog 2");
        dialog2Element.assertOpen();
        dialog2Element.assertModeless();

        assertThat(dialog2Element.getContentLocator()).hasText("This is the content of the dialog 2.");

        ButtonElement close2Button = ButtonElement.getByText(dialog2Element.getFooterLocator(), "Close");
        close2Button.click();

        dialog2Element.assertClosed();
        dialog1Element.assertOpen();

        assertThat(dialog1Element.getContentLocator()).hasText("This is the content of the dialog 1.");

        ButtonElement closeButton = ButtonElement.getByText(dialog1Element.getFooterLocator(), "Close");
        closeButton.click();

        dialog1Element.assertClosed();
        dialog1Element.assertClosed();
    }


    @Test
    public void testDialogWithHeaderComponent() {
        ButtonElement openDialogButton = ButtonElement.getByText(page, "Open dialog with header component");
        openDialogButton.click();

        DialogElement dialog = DialogElement.getByHeaderText(page, "Header title");
        dialog.assertOpen();
        dialog.assertVisible();
        dialog.assertModal();
        dialog.assertHeaderText("Header title");
        assertThat(dialog.getHeaderLocator()).hasText("This is the header of the dialog.");
        assertThat(dialog.getContentLocator()).hasText("This is the content of the dialog.");

        ButtonElement closeButton = ButtonElement.getByText(dialog.getFooterLocator(), "Close");
        closeButton.click();

        dialog.assertClosed();
    }

    @Test
    public void testHasClass() {
        ButtonElement openDialogButton = ButtonElement.getByText(page, "Open dialog with header component");
        openDialogButton.click();
        DialogElement dialog = DialogElement.getByHeaderText(page, "Header title");
        dialog.assertCssClass("custom-dialog");
    }

    @Test
    public void testTheme() {
        ButtonElement openDialogButton = ButtonElement.getByText(page, "Open dialog with header component");
        openDialogButton.click();
        DialogElement dialog = DialogElement.getByHeaderText(page, "Header title");
        dialog.assertTheme("no-padding");
    }


    @Test
    public void testQueryMatchesOpenDialogsOnly() {
        ButtonElement.getByText(page, "Open kept dialog").click();
        DialogElement kept = DialogElement.getByHeaderText(page, "Kept dialog");
        kept.assertOpen();
        assertTrue(kept.isOpen());
        ButtonElement.getByText(kept.getFooterLocator(), "Close").click();
        kept.assertClosed();
        assertFalse(kept.isOpen());

        // the closed dialog is still in the page
        DialogElement keptById = new DialogElement(page.locator("vaadin-dialog").filter(
                new Locator.FilterOptions().setHas(page.getByText("Kept in the page."))));
        assertThat(keptById.getLocator()).hasCount(1);
        assertFalse(keptById.isOpen());
        keptById.assertClosed();

        ButtonElement.getByText(page, "Open other dialog").click();
        DialogElement open = find(page, DialogElement.class).single();
        open.assertOpen();
        assertTrue(open.isOpen());
        open.assertHeaderText("Other dialog");

        ButtonElement.getByText(open.getFooterLocator(), "Close").click();
        open.assertClosed();
        assertFalse(open.isOpen());
        assertEquals(0, find(page, DialogElement.class).count());
    }
}
