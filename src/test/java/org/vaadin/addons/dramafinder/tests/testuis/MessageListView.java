package org.vaadin.addons.dramafinder.tests.testuis;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("MessageList Demo")
@Route(value = "messagelist", layout = MainLayout.class)
public class MessageListView extends Main {

    public MessageListView() {
        createBasicExample();
        createEmptyExample();
    }

    private void createBasicExample() {
        Instant timestamp1 = LocalDateTime.of(2025, 3, 1, 10, 15, 0)
                .toInstant(ZoneOffset.UTC);
        Instant timestamp2 = LocalDateTime.of(2025, 3, 1, 10, 18, 0)
                .toInstant(ZoneOffset.UTC);
        Instant timestamp3 = LocalDateTime.of(2025, 3, 1, 10, 22, 0)
                .toInstant(ZoneOffset.UTC);

        MessageListItem msg1 = new MessageListItem(
                "Hello everyone!", timestamp1, "Alice");
        msg1.setUserAbbreviation("AL");
        msg1.setUserColorIndex(1);

        MessageListItem msg2 = new MessageListItem(
                "Hi Alice, welcome!", timestamp2, "Bob");
        msg2.setUserAbbreviation("BO");
        msg2.setUserColorIndex(2);

        MessageListItem msg3 = new MessageListItem(
                "Thanks Bob!", timestamp3, "Alice");
        msg3.setUserAbbreviation("AL");
        msg3.setUserColorIndex(1);

        MessageList messageList = new MessageList();
        messageList.setItems(List.of(msg1, msg2, msg3));

        addExample("Basic Example", messageList);
    }

    private void createEmptyExample() {
        MessageList emptyList = new MessageList();
        addExample("Empty Example", emptyList);
    }

    private void addExample(String title, Component... components) {
        add(new H2(title));
        for (Component component : components) {
            add(component);
        }
    }
}
