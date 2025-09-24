package org.vaadin.jchristophe;

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


    private void addExample(String title, Component component) {
        add(new H2(title), component);
    }
}
