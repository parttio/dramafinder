package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageInputI18n;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("MessageInput Demo")
@Route(value = "message-input", layout = MainLayout.class)
public class MessageInputView extends Main {

    public MessageInputView() {
        createBasicExample();
        createDisabledExample();
        createCustomI18nExample();
    }

    private void createBasicExample() {
        MessageInput messageInput = new MessageInput();
        messageInput.setId("basic-message-input");

        Span submitOutput = new Span();
        submitOutput.setId("submit-output");

        messageInput.addSubmitListener(event -> submitOutput.setText(event.getValue()));

        addExample("Basic Example", messageInput, submitOutput);
    }

    private void createDisabledExample() {
        MessageInput messageInput = new MessageInput();
        messageInput.setId("disabled-message-input");
        messageInput.setEnabled(false);

        addExample("Disabled Example", messageInput);
    }

    private void createCustomI18nExample() {
        MessageInput messageInput = new MessageInput();
        messageInput.setId("i18n-message-input");
        messageInput.setI18n(new MessageInputI18n()
                .setMessage("Type your message here...")
                .setSend("Submit"));

        addExample("Custom I18n Example", messageInput);
    }

    private void addExample(String title, Component... components) {
        add(new H2(title));
        for (Component component : components) {
            add(component);
        }
    }
}
