package org.vaadin.addons.dramafinder.tests.it;

import com.microsoft.playwright.Locator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.HasTestView;
import org.vaadin.addons.dramafinder.element.MessageListElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MessageListViewIT extends SpringPlaywrightIT implements HasTestView {

    @Override
    public String getView() {
        return "messagelist";
    }

    @Test
    public void testMessageCount() {
        MessageListElement list = MessageListElement.get(page);
        assertThat(list.getLocator()).isVisible();
        list.assertMessageCount(3);
    }

    @Test
    public void testMessageContent() {
        MessageListElement list = MessageListElement.get(page);

        list.assertMessageText(0, "Hello everyone!");
        list.assertMessageUserName(0, "Alice");

        list.assertMessageText(1, "Hi Alice, welcome!");
        list.assertMessageUserName(1, "Bob");

        list.assertMessageText(2, "Thanks Bob!");
        list.assertMessageUserName(2, "Alice");
    }

    @Test
    public void testEmptyList() {
        Locator secondList = page.locator(MessageListElement.FIELD_TAG_NAME).nth(1);
        MessageListElement emptyList = new MessageListElement(secondList);
        assertThat(emptyList.getLocator()).isVisible();
        emptyList.assertEmpty();
    }

    @Test
    public void testMessageByUserName() {
        MessageListElement list = MessageListElement.get(page);
        Locator bobMessage = list.getMessageByUserName("Bob");
        assertThat(bobMessage).isVisible();
        assertThat(bobMessage.locator("[part='name']")).hasText("Bob");
        String text = bobMessage.evaluate(
                "el => el.shadowRoot.querySelector('[part=\"message\"] slot')"
                        + ".assignedNodes().map(n => n.textContent).join('').trim()"
        ).toString();
        assert text.equals("Hi Alice, welcome!") : "Expected 'Hi Alice, welcome!' but was '" + text + "'";
    }
}
