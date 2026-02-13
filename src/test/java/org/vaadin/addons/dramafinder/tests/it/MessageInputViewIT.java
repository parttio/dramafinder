package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.MessageInputElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MessageInputViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "message-input";
    }

    @Test
    public void testSetAndGetValue() {
        MessageInputElement messageInput = new MessageInputElement(
                page.locator("#basic-message-input"));
        messageInput.setValue("Hello World");
        messageInput.assertValue("Hello World");
        assertEquals("Hello World", messageInput.getValue());
    }

    @Test
    public void testSubmitByButton() {
        MessageInputElement messageInput = new MessageInputElement(
                page.locator("#basic-message-input"));
        messageInput.typeAndSubmit("Test message");
        assertThat(page.locator("#submit-output")).hasText("Test message");
    }

    @Test
    public void testSubmitByEnter() {
        MessageInputElement messageInput = new MessageInputElement(
                page.locator("#basic-message-input"));
        messageInput.setValue("Enter message");
        messageInput.submitByEnter();
        assertThat(page.locator("#submit-output")).hasText("Enter message");
    }

    @Test
    public void testClear() {
        MessageInputElement messageInput = new MessageInputElement(
                page.locator("#basic-message-input"));
        messageInput.setValue("Some text");
        messageInput.assertValue("Some text");
        messageInput.clear();
        messageInput.assertValue("");
    }

    @Test
    public void testDisabled() {
        MessageInputElement messageInput = new MessageInputElement(
                page.locator("#disabled-message-input"));
        messageInput.assertDisabled();
    }

    @Test
    public void testEnabled() {
        MessageInputElement messageInput = new MessageInputElement(
                page.locator("#basic-message-input"));
        messageInput.assertEnabled();
    }

    @Test
    public void testCustomI18n() {
        MessageInputElement messageInput = new MessageInputElement(
                page.locator("#i18n-message-input"));
        messageInput.assertMessagePlaceholder("Type your message here...");
        assertEquals("Type your message here...", messageInput.getMessagePlaceholder());
        messageInput.assertSendButtonText("Submit");
        assertEquals("Submit", messageInput.getSendButtonText());
    }

    @Test
    public void testValueClearedAfterSubmit() {
        MessageInputElement messageInput = new MessageInputElement(
                page.locator("#basic-message-input"));
        messageInput.typeAndSubmit("Will be cleared");
        assertThat(page.locator("#submit-output")).hasText("Will be cleared");
        messageInput.assertValue("");
    }
}
