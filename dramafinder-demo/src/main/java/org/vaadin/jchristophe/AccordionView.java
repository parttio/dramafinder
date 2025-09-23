package org.vaadin.jchristophe;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Accordion Demo")
@Route(value = "accordion", layout = MainLayout.class)
public class AccordionView extends Main {

    public AccordionView() {
        createBasicExample();
        createComponentExample();
    }

    private void createBasicExample() {
        Accordion accordion = new Accordion();
        accordion.addClassName("custom-css");
        accordion.add("Panel 1", new Span("Content 1"));
        accordion.add("Panel 2", new Span("Content 2"));
        AccordionPanel disabledPanel = accordion.add("Disabled Panel", new Span("No content"));
        disabledPanel.setEnabled(false);

        accordion.add("Panel 3", new Span("Content 3"));


        addExample("Basic example", accordion);
    }

    private void createComponentExample() {
        Accordion accordion = new Accordion();
        accordion.addClassName("custom-accordion");

        accordion.add("Component 1", new Span("Content 1"));
        accordion.add("Panel 2", new Span("Content 2"));
        addExample("Component example", accordion);
    }


    private void addExample(String title, Component component) {
        add(new H2(title), component);
    }
}
