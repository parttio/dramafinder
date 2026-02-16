package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-message-list>}.
 * <p>
 * Provides helpers to access individual messages, read their content
 * (text, user name, time) and assert message count. Messages are rendered
 * as {@code <vaadin-message>} children in light DOM with shadow-DOM parts
 * {@code name}, {@code time} and {@code message} for content access.
 */
@PlaywrightElement(MessageListElement.FIELD_TAG_NAME)
public class MessageListElement extends VaadinElement implements HasStyleElement, HasThemeElement {

    public static final String FIELD_TAG_NAME = "vaadin-message-list";
    public static final String FIELD_MESSAGE_TAG_NAME = "vaadin-message";

    /**
     * Create a new {@code MessageListElement}.
     *
     * @param locator the locator for the {@code <vaadin-message-list>} element
     */
    public MessageListElement(Locator locator) {
        super(locator);
    }

    // --- Message access ---

    /**
     * Locator for all {@code <vaadin-message>} children.
     *
     * @return locator matching every message in the list
     */
    public Locator getMessages() {
        return getLocator().locator(FIELD_MESSAGE_TAG_NAME);
    }

    /**
     * Locator for a single message by index.
     *
     * @param index zero-based index
     * @return locator for the message at the given index
     */
    public Locator getMessage(int index) {
        return getMessages().nth(index);
    }

    /**
     * Locator for the first message whose author name contains the given text.
     *
     * @param userName the user name to search for
     * @return locator for the matching message
     */
    public Locator getMessageByUserName(String userName) {
        return getMessages().filter(new Locator.FilterOptions()
                .setHas(getLocator().page().locator("[part='name']")
                        .filter(new Locator.FilterOptions().setHasText(userName)))).first();
    }

    // --- Message content accessors ---

    /**
     * Get the text content of the message at the given index.
     * The text lives in the light DOM of {@code <vaadin-message>} and is
     * projected into the shadow-DOM default slot inside {@code [part='message']}.
     * It is read via the slot's {@code assignedNodes()} to isolate it from
     * other shadow-DOM content (avatar, name, time).
     *
     * @param index zero-based index
     * @return the message text
     */
    public String getMessageText(int index) {
        return getMessage(index).evaluate(
                "el => el.shadowRoot.querySelector('[part=\"message\"] slot')"
                        + ".assignedNodes().map(n => n.textContent).join('').trim()"
        ).toString();
    }

    /**
     * Get the user name of the message at the given index.
     *
     * @param index zero-based index
     * @return the user name
     */
    public String getMessageUserName(int index) {
        return getMessage(index).locator("[part='name']").textContent();
    }

    /**
     * Get the time of the message at the given index.
     *
     * @param index zero-based index
     * @return the time text
     */
    public String getMessageTime(int index) {
        return getMessage(index).locator("[part='time']").textContent();
    }

    // --- Assertions ---

    /**
     * Assert that the list contains exactly the expected number of messages.
     *
     * @param count expected message count
     */
    public void assertMessageCount(int count) {
        assertThat(getMessages()).hasCount(count);
    }

    /**
     * Assert that the list contains no messages.
     */
    public void assertEmpty() {
        assertMessageCount(0);
    }

    /**
     * Assert that the message at the given index has the expected text.
     *
     * @param index    zero-based index
     * @param expected expected message text
     */
    public void assertMessageText(int index, String expected) {
        String actual = getMessageText(index);
        if (!actual.equals(expected)) {
            throw new AssertionError(
                    "Expected message text: \"" + expected + "\" but was: \"" + actual + "\"");
        }
    }

    /**
     * Assert that the message at the given index has the expected user name.
     *
     * @param index    zero-based index
     * @param expected expected user name
     */
    public void assertMessageUserName(int index, String expected) {
        assertThat(getMessage(index).locator("[part='name']")).hasText(expected);
    }

    /**
     * Assert that the message at the given index has the expected time.
     *
     * @param index    zero-based index
     * @param expected expected time text
     */
    public void assertMessageTime(int index, String expected) {
        assertThat(getMessage(index).locator("[part='time']")).hasText(expected);
    }

    // --- Factory methods ---

    /**
     * Get the first {@code <vaadin-message-list>} on the page.
     *
     * @param page the Playwright page
     * @return the first {@code MessageListElement}
     */
    public static MessageListElement get(Page page) {
        return new MessageListElement(page.locator(FIELD_TAG_NAME).first());
    }

    /**
     * Get the first {@code <vaadin-message-list>} within a locator scope.
     *
     * @param locator the scope to search within
     * @return the first {@code MessageListElement}
     */
    public static MessageListElement get(Locator locator) {
        return new MessageListElement(locator.locator(FIELD_TAG_NAME).first());
    }
}
