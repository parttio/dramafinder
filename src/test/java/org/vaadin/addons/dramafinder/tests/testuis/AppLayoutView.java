package org.vaadin.addons.dramafinder.tests.testuis;

import java.util.function.Consumer;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Exercises {@code AppLayoutElement} against the {@code AppLayout} of
 * {@link MainLayout}, which is the application shell of every test view.
 * <p>
 * The buttons mutate that surrounding layout, so each test needs its own page.
 */
@PageTitle("AppLayout Demo")
@Route(value = "applayout", layout = MainLayout.class)
public class AppLayoutView extends Main {

    /** Id assigned to the surrounding {@code AppLayout} when this view is attached. */
    public static final String APP_LAYOUT_ID = "demo-app-layout";

    public AppLayoutView() {
        Div content = new Div("App layout content");
        content.setId("applayout-content");

        Button primarySectionNavbar = new Button("Primary section navbar",
                event -> withAppLayout(layout -> layout.setPrimarySection(AppLayout.Section.NAVBAR)));
        primarySectionNavbar.setId("primary-section-navbar-button");

        Button primarySectionDrawer = new Button("Primary section drawer",
                event -> withAppLayout(layout -> layout.setPrimarySection(AppLayout.Section.DRAWER)));
        primarySectionDrawer.setId("primary-section-drawer-button");

        Button addTheme = new Button("Add theme",
                event -> withAppLayout(layout -> layout.getElement().setAttribute("theme", "demo-theme")));
        addTheme.setId("add-theme-button");

        Button addClassName = new Button("Add class name",
                event -> withAppLayout(layout -> layout.addClassName("custom-app-layout")));
        addClassName.setId("add-class-name-button");

        Button closeDrawer = new Button("Close drawer",
                event -> withAppLayout(layout -> layout.setDrawerOpened(false)));
        closeDrawer.setId("close-drawer-button");

        add(content, primarySectionNavbar, primarySectionDrawer, addTheme, addClassName, closeDrawer);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        withAppLayout(layout -> layout.setId(APP_LAYOUT_ID));
    }

    private void withAppLayout(Consumer<AppLayout> action) {
        getParent()
                .filter(AppLayout.class::isInstance)
                .map(AppLayout.class::cast)
                .ifPresent(action);
    }
}
