# Message List

`MessageListElement` wraps `<vaadin-message-list>` (Flow: `MessageList`). It
counts messages and reads or asserts each message's text, author and time.

| | |
|---|---|
| Web component | `<vaadin-message-list>` (messages are `<vaadin-message>` children) |
| Flow component | `com.vaadin.flow.component.messages.MessageList` |
| Wrapper | `org.vaadin.addons.dramafinder.element.MessageListElement` |
| Typed query | `find(page, MessageListElement.class)` → `ComponentQuery` |
| API | [MessageListElement](../../skills/vaadin-playwright-test/api-reference.md#messagelistelement) |

Messages are rendered as `<vaadin-message>` children in the light DOM. Each
message shows its author in the shadow part `name`, its time in the part `time`,
and its text in the light DOM, projected into the part `message`.

## Finding the component

```java
MessageListElement chat = MessageListElement.getById(page, "chat");
MessageListElement first = MessageListElement.get(page);                    // first list on the page
MessageListElement scoped = MessageListElement.get(page.locator("#support-panel"));
MessageListElement byTestId = MessageListElement.getByTestId(page, "chat-history");
```

The typed query has the filters every component has:

```java
import static org.vaadin.addons.dramafinder.element.query.ElementQueries.find;

MessageListElement chat = find(page, MessageListElement.class).withClassName("chat-history").single();
```

Without an id, several lists on one page can be addressed by position:
`new MessageListElement(page.locator(MessageListElement.FIELD_TAG_NAME).nth(1))`.

## Counting messages

```java
// View
MessageListItem msg1 = new MessageListItem("Hello everyone!", timestamp1, "Alice");
msg1.setUserAbbreviation("AL");
MessageListItem msg2 = new MessageListItem("Hi Alice, welcome!", timestamp2, "Bob");
MessageListItem msg3 = new MessageListItem("Thanks Bob!", timestamp3, "Alice");

MessageList messageList = new MessageList();
messageList.setItems(List.of(msg1, msg2, msg3));
add(messageList, new MessageList());   // the second one is empty
```

```java
// Test
MessageListElement list = MessageListElement.get(page);
list.assertMessageCount(3);

MessageListElement empty = new MessageListElement(page.locator(MessageListElement.FIELD_TAG_NAME).nth(1));
empty.assertEmpty();
```

`getMessages()` is the locator over every `<vaadin-message>`, e.g. for
`assertThat(list.getMessages()).hasCount(3)`.

## Reading a message

Messages are addressed by zero-based index, in display order:

```java
list.assertMessageText(0, "Hello everyone!");
list.assertMessageUserName(0, "Alice");
list.assertMessageText(1, "Hi Alice, welcome!");
list.assertMessageUserName(1, "Bob");
list.assertMessageTime(0, expectedTime);   // the text the message shows in its time part
```

The getters return the same values when they are needed:

```java
String text = list.getMessageText(2);       // "Thanks Bob!"
String author = list.getMessageUserName(2); // "Alice"
String time = list.getMessageTime(2);       // as rendered by the component
```

`getMessageText` reads the nodes assigned to the message slot, so the avatar, the
name and the time are not part of the text, and trims the result.
`getMessageUserName` and `getMessageTime` return the `textContent` of the `name`
and `time` parts.

The time is formatted by the web component, so the expected string depends on the
browser's locale and time zone; read it once with `getMessageTime` before
hard-coding it.

## A message by its author

`getMessageByUserName` returns a locator for the **first** message whose author
name matches **exactly** — `"Ann"` does not resolve to a message from `"Anna"`:

```java
Locator bobMessage = list.getMessageByUserName("Bob");
assertThat(bobMessage).isVisible();
assertThat(bobMessage.locator("[part='name']")).hasText("Bob");
assertThat(bobMessage).containsText("Hi Alice, welcome!");
```

It is the documented exception among item lookups: it keeps `.first()`, because
several messages from one author is normal data, not an ambiguity. Use
`getMessage(int)` to reach a later message of the same author:

```java
Locator alicesSecond = list.getMessage(2);
```

## Messages added by the server

`assertMessageCount` retries, so wait for the new message with it before reading
the message:

```java
// View: a MessageInput appends what the user sends
input.addSubmitListener(e -> {
    var items = new ArrayList<>(messageList.getItems());
    items.add(new MessageListItem(e.getValue(), Instant.now(), "Me"));
    messageList.setItems(items);
});
```

```java
// Test
MessageInputElement.get(page).typeAndSubmit("Hello there");
MessageListElement list = MessageListElement.get(page);
list.assertMessageCount(4);                  // retries until the new message arrives
list.assertMessageText(3, "Hello there");
list.assertMessageUserName(3, "Me");
```

## Theme and style

```java
list.assertCssClass("chat-history");   // messageList.addClassName("chat-history")
list.assertTheme(null);                // no theme attribute
```

## Pitfalls

- **`assertMessageText` does not retry.** It reads the text once and fails at
  once on a mismatch (`assertMessageUserName` and `assertMessageTime` do retry).
  After an action that adds or changes messages, first wait with
  `assertMessageCount(...)`, or assert with
  `assertThat(list.getMessage(i)).containsText(...)`.
- **`getMessageByUserName` takes the first match** of the exact author name.
- **Time strings depend on the browser locale and time zone.**

## See also

- [Message Input](MessageInputElement.md) — the input usually paired with a list
- [Avatar](AvatarElement.md)
- [Markdown](MarkdownElement.md)
- [Assertions](../guide/assertions.md)
- [Finding components](../guide/finding-components.md)
- [Actions and waiting](../guide/actions-and-waiting.md)
- Full example: [MessageListViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/MessageListViewIT.java)
