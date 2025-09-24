package org.vaadin.jchristophe;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Dialog Demo")
@Route(value = "dialog", layout = MainLayout.class)
public class DialogView extends Main {

    public DialogView() {
        createModalDialog();
        createNonModalDialogs();
    }

    private void createModalDialog() {
        Button openDialogButton = new Button("Open dialog", e ->
                createDialog("My Dialog", true,
                        new Span("This is the content of the dialog.")).open());
        addExample("Dialog example", openDialogButton);
    }

    private void createNonModalDialogs() {
        Dialog dialog1 = createDialog("My non-modal Dialog 1", false,
                new Div("This is the content of the dialog 1."));
        dialog1.setLeft("0px");
        Button openDialogButton1 = new Button("Open non-modal 1",
                e -> dialog1.open());
        Dialog dialog2 = createDialog("My non-modal Dialog 2", false,
                new Div("This is the content of the dialog 2."));
        dialog2.setTop("0px");
        Button openDialogButton2 = new Button("Open non-modal 2",
                e -> dialog2.open());
        addExample("Dialog example", new HorizontalLayout(openDialogButton1, openDialogButton2));
    }

    private Dialog createDialog(String title, boolean modal, Component content) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(title);
        dialog.setModal(modal);
        dialog.add(content);
        Button closeButton = new Button("Close", e -> dialog.close());
        dialog.getFooter().add(closeButton);
        return dialog;
    }

    private void addExample(String title, Component component) {
        add(new H2(title), component);
    }
}
