# MessageInputElement

> Full API (methods, signatures, descriptions): see [api-reference.md](../../skills/vaadin-playwright-test/api-reference.md). This note covers only behaviour that isn't obvious from the signatures.

## The component clears the text area after submit

Submitting (via `submit()`, `submitByEnter()`, or `typeAndSubmit(...)`) causes the component to clear its text area, so `getValue()` / `assertValue()` return empty afterwards. `submit()` clicks the send button; `submitByEnter()` presses Enter on the text area.

```java
input.setValue("Test message");
input.assertValue("Test message");
input.submit();

// The component clears the text area after submit
input.assertValue("");
```

## Focus/enabled delegate to the native textarea

`FocusableElement` and `HasEnabledElement` operations delegate to the internal native textarea input, not the outer `<vaadin-message-input>` host.
