package org.vaadin.jchristophe;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.dramafinder.HasTestView;
import org.vaadin.dramafinder.element.ButtonElement;
import org.vaadin.dramafinder.element.DialogElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class DialogViewIT extends SpringPlaywrightIT implements HasTestView {

    @Override
    public String getView() {
        return "dialog";
    }

    @Test
    public void testModalDialog() {
        ButtonElement openDialogButton = ButtonElement.getByText(page, "Open dialog");
        openDialogButton.click();

        DialogElement dialog = new DialogElement(page);
        dialog.assertClosed();
        dialog.assertOpen();
        dialog.assertModal();
        assertThat(dialog.getHeaderText()).hasText("My Dialog");

        assertThat(dialog.getContent()).hasText("This is the content of the dialog.");

        ButtonElement closeButton = ButtonElement.getByText(dialog.getFooter(), "Close");
        closeButton.click();

        dialog.assertClosed();
    }


    @Test
    public void testNonModalDialogs() {
        ButtonElement openDialog1Button = ButtonElement.getByText(page, "Open non-modal 1");
        openDialog1Button.click();

        // the main part is aria-hidden so it's not accessible anymore
        ButtonElement openDialog2Button = new ButtonElement(
                page.getByRole(
                        AriaRole.BUTTON,
                        new Page.GetByRoleOptions().setName("Open non-modal 2")
                                .setIncludeHidden(true)
                ).and(page.locator(ButtonElement.FIELD_TAG_NAME))
        );
        openDialog2Button.assertVisible();
        DialogElement dialog1Element = DialogElement.getByHeaderText(page, "My non-modal Dialog 1");
        dialog1Element.assertOpen();
        dialog1Element.assertModeless();

        openDialog2Button.click();

        DialogElement dialog2Element = DialogElement.getByHeaderText(page, "My non-modal Dialog 2");
        dialog2Element.assertOpen();
        dialog2Element.assertModeless();

        assertThat(dialog2Element.getContent()).hasText("This is the content of the dialog 2.");

        ButtonElement close2Button = ButtonElement.getByText(dialog2Element.getFooter(), "Close");
        close2Button.click();

        dialog2Element.assertClosed();
        dialog1Element.assertOpen();

        assertThat(dialog1Element.getContent()).hasText("This is the content of the dialog 1.");

        ButtonElement closeButton = ButtonElement.getByText(dialog1Element.getFooter(), "Close");
        closeButton.click();

        dialog1Element.assertClosed();
        dialog1Element.assertClosed();
    }


}
