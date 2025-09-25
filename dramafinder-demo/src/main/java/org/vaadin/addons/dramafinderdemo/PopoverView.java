package org.vaadin.addons.dramafinderdemo;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.popover.PopoverVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Popover Demo")
@Route(value = "popover", layout = MainLayout.class)
public class PopoverView extends Main {

    public PopoverView() {
        createBasicExample();
        createComponentExample();
        createStayOpenedExample();
    }

    private void createBasicExample() {
        Button button = new Button("Open Popover");
        Popover popover = new Popover();
        popover.setAriaLabel("Basic Popover");
        popover.add(new Div("This is a popover"));
        popover.setTarget(button);
        addExample("Popover example", button);
    }

    private void createComponentExample() {
        Button button = new Button("Close component Popover");
        HorizontalLayout horizontalLayout = new HorizontalLayout(
                new Span("This is a popover"),
                button);
        Button openPopover = new Button("Open component Popover");
        Popover popover = new Popover();
        popover.addThemeVariants(PopoverVariant.ARROW);
        popover.add(horizontalLayout);
        popover.setTarget(openPopover);
        popover.addClassName("component-popover");
        popover.setAriaLabel("Stay component Popover");
        button.addClickListener(e -> popover.close());
        addExample("Popover example", openPopover);
    }

    private void createStayOpenedExample() {
        Button button = new Button("Open stay opened Popover");
        Popover popover = new Popover();
        popover.setAriaLabel("Stay opened Popover");
        popover.setCloseOnOutsideClick(false);
        popover.add(new Div("This is a popover"));
        popover.setTarget(button);
        addExample("Popover example", button);
    }

    private void addExample(String title, Component component) {
        add(new H2(title), component);
    }
}
