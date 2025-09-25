package org.vaadin.addons.dramafinderdemo;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Button Demo")
@Route(value = "button", layout = MainLayout.class)
public class ButtonView extends Main {

    public ButtonView() {
        createBasicButtons();
        createDisabledButton();
        createToggleButton();
        createClickableButton();
        createIconButton();
        createTooltipButton();
    }

    private void createBasicButtons() {
        Button button = new Button("Enabled Button");
        button.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        button.addClassName("custom-button");
        button.focus();
        addExample("Enabled Button", button);
    }

    private void createDisabledButton() {
        Button button = new Button("Disabled Button");
        button.setEnabled(false);
        addExample("Disabled Button", button);
    }

    private void createToggleButton() {
        Button button = new Button("Toggle Button");
        button.addClickListener(e -> button.setEnabled(!button.isEnabled()));
        addExample("Toggle Button", button);
    }

    private void createClickableButton() {
        Button button = new Button("Click me");
        button.addClickListener(e -> Notification.show("Button clicked!"));
        addExample("Clickable Button", button);
    }

    private void createIconButton() {
        Button button = new Button();
        button.setAriaLabel("Icon Button");
        button.setIcon(VaadinIcon.BUTTON.create());
        button.setPrefixComponent(new Span("Prefix"));
        button.setSuffixComponent(new Span("Suffix"));
        addExample("Icon Button", button);
    }


    private void createTooltipButton() {
        Button button = new Button("Tooltip Button");
        button.setTooltipText("Tooltip Text");
        addExample("Tooltip Button", button);
    }


    private void addExample(String title, Component component) {
        add(new H2(title), component);
    }
}
