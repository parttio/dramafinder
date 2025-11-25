package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Details Demo")
@Route(value = "details", layout = MainLayout.class)
public class DetailsView extends Main {

    public DetailsView() {
        createBasicDetails();
        createOpenedDetails();
        createDisabledDetails();
        createSummaryComponent();
    }

    private void createBasicDetails() {
        Details details = new Details("Basic Details", new Span("Some content."));
        details.setTooltipText("Tooltip for component");
        details.addClassName("custom-css");
        details.addThemeVariants(DetailsVariant.SMALL);
        addExample("Basic Details", details);
    }

    private void createOpenedDetails() {
        Details details = new Details("Opened Details", new Span("Some content."));
        details.setOpened(true);
        addExample("Opened Details", details);
    }

    private void createDisabledDetails() {
        Details details = new Details("Disabled Details", new Span("Some content."));
        details.setEnabled(false);
        addExample("Disabled Details", details);
    }

    public void createSummaryComponent() {

        Button button = new Button("test", e -> Notification.show("Clicked"));
        Details details = new Details(
                new HorizontalLayout(new Span("Summary Component"), button)
                , new Span("Some content."));
        addExample("Summary Component", details);
    }

    private void addExample(String title, Component component) {
        add(new H2(title), component);
    }
}
