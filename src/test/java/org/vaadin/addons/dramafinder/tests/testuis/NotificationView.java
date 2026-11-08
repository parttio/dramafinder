package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Notification Demo")
@Route(value = "notification", layout = MainLayout.class)
public class NotificationView extends Main {

    public NotificationView() {
        createBasicExample();
        createComponentExample();
        createMultipleExample();
        createSameTextExample();
    }

    private void createBasicExample() {
        Notification notification = new Notification("This is a notification.");
        notification.setDuration(400);
        Button openNotificationButton = new Button("Show notification", e -> notification.open());
        addExample("Notification example", openNotificationButton);
    }

    private void createComponentExample() {
        Button button = new Button("Close");
        Notification notification = new Notification(new HorizontalLayout(
                new Span("This is a notification."),
                button));
        notification.addClassName("custom-notification");
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        button.addClickListener(e -> notification.close());
        notification.setDuration(0);
        Button openNotificationButton = new Button("Show component notification", e -> notification.open());
        addExample("Notification component example", openNotificationButton);
    }


    private void createMultipleExample() {
        Button openNotificationsButton = new Button("Show multiple notifications", e -> {
            Notification first = new Notification("First notification");
            first.setDuration(0);
            first.open();
            Notification second = new Notification("Second notification");
            second.setDuration(0);
            second.open();
        });
        addExample("Multiple notifications example", openNotificationsButton);
    }

    private void createSameTextExample() {
        Button openNotificationsButton = new Button("Show same-text notifications", e -> {
            Notification error = new Notification("Upload finished");
            error.addThemeVariants(NotificationVariant.LUMO_ERROR);
            error.setDuration(0);
            error.open();
            Notification success = new Notification("Upload finished");
            success.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            success.setDuration(0);
            success.open();
            Notification other = new Notification("Upload finished with warnings");
            other.setDuration(0);
            other.open();
        });
        addExample("Same-text notifications example", openNotificationsButton);
    }

    private void addExample(String title, Component component) {
        add(new H2(title), component);
    }
}
