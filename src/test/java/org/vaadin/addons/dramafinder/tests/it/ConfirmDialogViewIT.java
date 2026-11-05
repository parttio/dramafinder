package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.addons.dramafinder.element.ConfirmDialogElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ConfirmDialogViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "confirm-dialog";
    }

    private ConfirmDialogElement openUnsavedChanges() {
        ButtonElement.getByText(page, "Open unsaved changes").click();
        ConfirmDialogElement dialog = ConfirmDialogElement.getByHeaderText(page, "Unsaved changes");
        dialog.assertOpen();
        return dialog;
    }

    private void assertResult(String expected) {
        assertThat(page.locator("#result")).hasText(expected);
    }

    @Test
    public void readsHeaderMessageAndButtons() {
        ConfirmDialogElement dialog = openUnsavedChanges();
        dialog.assertVisible();
        dialog.assertHeaderText("Unsaved changes");
        assertEquals("Unsaved changes", dialog.getHeaderText());
        dialog.assertMessage("There are unsaved changes. Do you want to discard or save them?");
        dialog.assertConfirmText("Save");
        dialog.assertRejectText("Discard");
        dialog.assertCancelText("Cancel");
        dialog.assertCancelable();
        dialog.assertRejectable();
        assertTrue(dialog.isOpen());
    }

    @Test
    public void confirmFiresConfirmAndClosesTheDialog() {
        ConfirmDialogElement dialog = openUnsavedChanges();
        dialog.confirm();
        assertResult("confirm");
        dialog.assertClosed();
        dialog.assertHidden();
        assertFalse(dialog.isOpen());
    }

    @Test
    public void rejectFiresReject() {
        openUnsavedChanges().reject();
        assertResult("reject");
    }

    @Test
    public void cancelFiresCancel() {
        openUnsavedChanges().cancel();
        assertResult("cancel");
    }

    @Test
    public void escapeCancels() {
        ConfirmDialogElement dialog = openUnsavedChanges();
        dialog.closeWithEscape();
        assertResult("cancel");
        dialog.assertClosed();
    }

    @Test
    public void headerMatchesAsCaseInsensitiveSubstring() {
        ButtonElement.getByText(page, "Open unsaved changes").click();
        ConfirmDialogElement.getByHeaderText(page, "unsaved").assertOpen();
    }

    @Test
    public void getFindsTheOpenDialog() {
        ButtonElement.getByText(page, "Open alert").click();
        ConfirmDialogElement dialog = ConfirmDialogElement.get(page);
        dialog.assertHeaderText("Export failed");
        dialog.assertMessage("Your report could not be exported.");
        dialog.assertConfirmText("OK");
        dialog.assertNotCancelable();
        dialog.assertNotRejectable();
        dialog.confirm();
        assertResult("acknowledged");
    }

    @Test
    public void readsMessageSetAsComponent() {
        ButtonElement.getByText(page, "Open delete").click();
        ConfirmDialogElement dialog = ConfirmDialogElement.getByHeaderText(page, "Delete item");
        dialog.assertMessage("Delete Report Q3? This cannot be undone.");
        assertEquals("Delete Report Q3? This cannot be undone.", dialog.getMessage());
        dialog.getConfirmButton().assertTheme("error primary");
        dialog.assertNotRejectable();
        dialog.confirm();
        assertResult("deleted");
    }

    @Test
    public void escapeDoesNothingWhenDisabled() {
        ButtonElement.getByText(page, "Open terms").click();
        ConfirmDialogElement dialog = ConfirmDialogElement.getByHeaderText(page, "Accept terms");
        dialog.assertOpen();
        dialog.closeWithEscape();
        dialog.assertOpen();
        dialog.assertVisible();
        assertResult("none");
        dialog.confirm();
        assertResult("accepted");
    }

    @Test
    public void messageMismatchReportsExpectedAndActual() {
        page.setDefaultTimeout(1000);
        ConfirmDialogElement dialog = openUnsavedChanges();
        AssertionError error = assertThrows(AssertionError.class,
                () -> dialog.assertMessage("Discard everything?"));
        assertTrue(error.getMessage().contains("Discard everything?"), error.getMessage());
        assertTrue(error.getMessage().contains("There are unsaved changes."), error.getMessage());
    }
}
