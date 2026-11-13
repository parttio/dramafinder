# Message Input

`MessageInputElement` wraps `<vaadin-message-input>` (Flow: `MessageInput`), a
text area with a send button. It types and submits messages, and asserts the
value, the send button and the i18n texts.

| | |
|---|---|
| Web component | `<vaadin-message-input>` (an inner `<vaadin-text-area>` and `<vaadin-message-input-button>`) |
| Flow component | `com.vaadin.flow.component.messages.MessageInput` |
| Wrapper | `org.vaadin.addons.dramafinder.element.MessageInputElement` |
| Typed query | `find(page, MessageInputElement.class)` → `ComponentQuery` |
| API | [MessageInputElement](../../skills/vaadin-playwright-test/api-reference.md#messageinputelement) |

## Finding the component

```java
MessageInputElement input = MessageInputElement.getById(page, "basic-message-input");
MessageInputElement first = MessageInputElement.get(page);                     // first on the page
MessageInputElement scoped = MessageInputElement.get(page.locator("#chat-panel"));
MessageInputElement byTestId = MessageInputElement.getByTestId(page, "chat-input");
```

The typed query has the filters every component has:

```java
import static org.vaadin.addons.dramafinder.element.query.ElementQueries.find;

MessageInputElement input = chatPanel.find(MessageInputElement.class).single();
```

## Typing and submitting

```java
// View
MessageInput messageInput = new MessageInput();
messageInput.setId("basic-message-input");
Span submitOutput = new Span();
submitOutput.setId("submit-output");
messageInput.addSubmitListener(event -> submitOutput.setText(event.getValue()));
```

```java
// Test
MessageInputElement input = MessageInputElement.getById(page, "basic-message-input");

input.typeAndSubmit("Test message");          // setValue + click the send button
assertThat(page.locator("#submit-output")).hasText("Test message");

input.setValue("Enter message");
input.submitByEnter();                         // press Enter in the text area
assertThat(page.locator("#submit-output")).hasText("Enter message");

input.setValue("Draft");
input.submit();                                // click the send button
```

Each action waits for the server round-trip the submit triggers.

## The value

```java
input.setValue("Hello World");
input.assertValue("Hello World");
String draft = input.getValue();   // "Hello World", when the value itself is needed

input.clear();
input.assertValue("");
```

`setValue` fills the native textarea and also syncs the value to the inner
`<vaadin-text-area>`, so the `<vaadin-message-input>` updates its send button
state. `assertValue` retries on the native textarea's value.

### The component clears the text area after submit

Submitting (via `submit()`, `submitByEnter()`, or `typeAndSubmit(...)`) makes the
component clear its text area, so `getValue()` / `assertValue()` return empty
afterwards:

```java
input.typeAndSubmit("Will be cleared");
assertThat(page.locator("#submit-output")).hasText("Will be cleared");
input.assertValue("");
```

## Enabled state and the send button

```java
// View
MessageInput disabled = new MessageInput();
disabled.setId("disabled-message-input");
disabled.setEnabled(false);
```

```java
// Test
MessageInputElement.getById(page, "basic-message-input").assertEnabled();

MessageInputElement off = MessageInputElement.getById(page, "disabled-message-input");
off.assertDisabled();
```

`HasEnabledElement` and `FocusableElement` delegate to the internal **native
textarea**, not the outer `<vaadin-message-input>` host:

```java
input.focus();
input.assertIsFocused();     // the textarea has focus
input.blur();
input.assertIsNotFocused();
```

The send button has its own assertions:

```java
input.assertSendButtonVisible();
input.assertSendButtonEnabled();
off.assertSendButtonDisabled();      // e.g. while the component is disabled
input.assertSendButtonHidden();      // when the button is not shown at all
```

## Placeholder and send button text (i18n)

```java
// View
MessageInput messageInput = new MessageInput();
messageInput.setId("i18n-message-input");
messageInput.setI18n(new MessageInputI18n()
        .setMessage("Type your message here...")
        .setSend("Submit"));
```

```java
// Test
MessageInputElement input = MessageInputElement.getById(page, "i18n-message-input");
input.assertMessagePlaceholder("Type your message here...");
input.assertSendButtonText("Submit");

String placeholder = input.getMessagePlaceholder();   // getters, when the text is needed
String send = input.getSendButtonText();
```

## Internal locators

For anything the wrapper doesn't cover, use the inner parts:

| Locator | Points at |
|---------|-----------|
| `getTextAreaLocator()` | the inner `<vaadin-text-area>` |
| `getTextAreaInputLocator()` | the native textarea inside it (`slot="textarea"`) |
| `getSendButtonLocator()` | the `<vaadin-message-input-button>` |

```java
assertThat(input.getTextAreaInputLocator()).isEditable();
input.getTextAreaInputLocator().press("Shift+Enter");   // a new line instead of a submit
```

## Tooltip, theme and style

```java
// View
messageInput.setTooltipText("Press Enter to send");
messageInput.addClassName("chat-input");
```

```java
// Test
input.assertTooltipHasText("Press Enter to send");
input.assertCssClass("chat-input");
```

## Pitfalls

- **Submit clears the value.** Assert what was sent on the receiving side (a
  listener's output, a [Message List](MessageListElement.md)), not with
  `assertValue` after submitting.
- **Focus and enabled state live on the native textarea**, not on the host.
- **`Enter` submits.** Use `Shift+Enter` on `getTextAreaInputLocator()` for a
  line break.

## See also

- [Message List](MessageListElement.md) — where submitted messages usually appear
- [Text Area](TextAreaElement.md)
- [Assertions](../guide/assertions.md)
- [Finding components](../guide/finding-components.md)
- [Actions and waiting](../guide/actions-and-waiting.md)
- Full example: [MessageInputViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/MessageInputViewIT.java)
