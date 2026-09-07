package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.badge.Badge;
import com.vaadin.flow.component.badge.BadgeVariant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Badge Demo")
@Route(value = "badge", layout = MainLayout.class)
public class BadgeView extends Main {

    public BadgeView() {
        Badge scoped = new Badge("Scoped");
        scoped.setId("badge-scoped");

        Div container = new Div();
        container.setId("badge-container");
        container.add(scoped);

        Badge plain = new Badge("Pending");
        plain.setId("badge-plain");

        Badge success = new Badge("Completed");
        success.setId("badge-success");
        success.addThemeVariants(BadgeVariant.SUCCESS);

        Badge error = new Badge("Failed");
        error.setId("badge-error");
        error.addThemeVariants(BadgeVariant.ERROR);

        Badge contrast = new Badge("Draft");
        contrast.setId("badge-contrast");
        contrast.addThemeVariants(BadgeVariant.CONTRAST);

        Badge warning = new Badge("Expiring");
        warning.setId("badge-warning");
        warning.addThemeVariants(BadgeVariant.WARNING);

        Badge smallSuccess = new Badge("Synced");
        smallSuccess.setId("badge-small-success");
        smallSuccess.addThemeVariants(BadgeVariant.SUCCESS, BadgeVariant.SMALL);

        Badge withIcon = new Badge("Verified", VaadinIcon.CHECK.create());
        withIcon.setId("badge-with-icon");

        Badge withoutIcon = new Badge("No icon");
        withoutIcon.setId("badge-without-icon");

        Badge withNumber = new Badge("unread messages", 5);
        withNumber.setId("badge-with-number");
        withNumber.addThemeVariants(BadgeVariant.NUMBER_ONLY);

        Badge dot = new Badge();
        dot.setId("badge-dot");
        dot.addThemeVariants(BadgeVariant.DOT);

        Badge withClass = new Badge("Styled");
        withClass.setId("badge-with-class");
        withClass.addClassName("custom-badge");

        Badge dynamic = new Badge("Initial");
        dynamic.setId("badge-dynamic");
        Button update = new Button("Update badge", event -> {
            dynamic.setText("Updated");
            dynamic.setNumber(9);
            dynamic.addThemeVariants(BadgeVariant.SUCCESS);
        });
        update.setId("badge-update-button");

        add(container, plain, success, error, contrast, warning, smallSuccess,
                withIcon, withoutIcon, withNumber, dot, withClass, dynamic, update);
    }
}
