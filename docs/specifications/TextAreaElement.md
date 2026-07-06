# TextAreaElement

> Full API (methods, signatures, descriptions): see [api-reference.md](../../skills/vaadin-playwright-test/api-reference.md). This note covers only behaviour that isn't obvious from the signatures.

## Input locator points at the textarea slot

`TextAreaElement` extends `TextFieldElement` but overrides `getInputLocator()` to return the `slot="textarea"` element instead of a plain `input`, so all inherited value/field operations target the multi-line textarea.

```java
public Locator getInputLocator() {
    return getLocator().locator("*[slot=\"textarea\"]").first();
}
```
