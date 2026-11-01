package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Confirm Dialog Demo")
@Route(value = "confirm-dialog", layout = MainLayout.class)
public class ConfirmDialogView extends Main {

    private final Span result = new Span("none");

    public ConfirmDialogView() {
        result.setId("result");
        add(new Paragraph(new Span("Last action: "), result));
        createUnsavedChangesDialog();
        createAlertDialog();
        createRichMessageDialog();
        createNoEscapeDialog();
    }

    private void createUnsavedChangesDialog() {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Unsaved changes");
        dialog.setText("There are unsaved changes. Do you want to discard or save them?");
        dialog.setCancelable(true);
        dialog.addCancelListener(e -> result.setText("cancel"));
        dialog.setRejectable(true);
        dialog.setRejectText("Discard");
        dialog.addRejectListener(e -> result.setText("reject"));
        dialog.setConfirmText("Save");
        dialog.addConfirmListener(e -> result.setText("confirm"));
        addExample("Confirm, reject and cancel", new Button("Open unsaved changes", e -> dialog.open()));
    }

    private void createAlertDialog() {
        ConfirmDialog dialog = new ConfirmDialog("Export failed",
                "Your report could not be exported.", "OK",
                e -> result.setText("acknowledged"));
        addExample("Alert", new Button("Open alert", e -> dialog.open()));
    }

    private void createRichMessageDialog() {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Delete item");
        Paragraph message = new Paragraph("Delete ");
        message.add(new Span("Report Q3"));
        message.add("? This cannot be undone.");
        dialog.setText(message);
        dialog.setCancelable(true);
        dialog.setConfirmText("Delete");
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(e -> result.setText("deleted"));
        addExample("Rich message", new Button("Open delete", e -> dialog.open()));
    }

    private void createNoEscapeDialog() {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Accept terms");
        dialog.setText("You must accept the terms to continue.");
        dialog.setCloseOnEsc(false);
        dialog.setConfirmText("Accept");
        dialog.addConfirmListener(e -> result.setText("accepted"));
        addExample("No close on Escape", new Button("Open terms", e -> dialog.open()));
    }

    private void addExample(String title, Component component) {
        add(new H2(title), component);
    }
}
