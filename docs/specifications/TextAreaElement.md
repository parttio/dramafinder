# Text Area

`TextAreaElement` wraps `<vaadin-text-area>` (Flow: `TextArea`), the multi-line
text field. It extends [`TextFieldElement`](TextFieldElement.md), so value, clear
button, placeholder, prefix and suffix, helper, validation, read-only, enabled,
tooltip, theme and focus work exactly as described there. The one difference is
the input: every value, focus and constraint operation targets the native
`<textarea>`.

| | |
|---|---|
| Web component | `<vaadin-text-area>` |
| Flow component | `com.vaadin.flow.component.textfield.TextArea` |
| Wrapper | `org.vaadin.addons.dramafinder.element.TextAreaElement` |
| Typed query | `find(page, TextAreaElement.class)` → `InputFieldQuery` |
| API | [TextAreaElement](../../skills/vaadin-playwright-test/api-reference.md#textareaelement) |

## Finding the component

```java
TextAreaElement area = TextAreaElement.getByLabel(page, "TextArea");
TextAreaElement validated = TextAreaElement.getByLabelContaining(page, "validated");
TextAreaElement scoped = TextAreaElement.getByLabel(page.locator("#container"), "My Label");
TextAreaElement byId = TextAreaElement.getById(page, "comments");
TextAreaElement byTestId = TextAreaElement.getByTestId(page, "comments");

// Placeholder or aria-label only: use the typed query
TextAreaElement byPlaceholder = find(page, TextAreaElement.class).withPlaceholder("Enter text here").single();
TextAreaElement byName = find(page, TextAreaElement.class).withAccessibleName("Invisible label").single();
```

`getByLabel` matches the whole visible label, case-sensitively;
`getByLabelContaining` a case-insensitive substring. Only `<vaadin-text-area>`
elements are considered: `TextFieldElement.getByLabel` does not find a text area.

## Find, set and assert

```java
// View
TextArea area = new TextArea("TextArea");
area.setHelperText("Helper text");
area.setRequired(true);
area.setI18n(new TextArea.TextAreaI18n().setRequiredErrorMessage("Field is required"));
```

```java
// Test
TextAreaElement area = TextAreaElement.getByLabel(page, "TextArea");
area.assertHelperHasText("Helper text");
area.assertValue("");

area.setValue("new value");
area.assertValue("new value");
area.assertValid();

area.setValue("");
area.assertInvalid();
area.assertErrorMessage("Field is required");
```

## Multi-line values

`setValue` fills the `<textarea>`, so line breaks are typed as part of the value:

```java
TextAreaElement area = TextAreaElement.getByLabel(page, "TextArea");
area.setValue("First line\nSecond line");
area.assertValue("First line\nSecond line");
```

## The input locator points at the textarea slot

`TextAreaElement` overrides `getInputLocator()` to return the `slot="textarea"`
element instead of the `slot="input"` one a text field has, so all inherited
value and field operations (value, focus, enabled, `aria-label`, min/max length,
pattern) target the multi-line textarea:

```java
public Locator getInputLocator() {
    return getLocator().locator("*[slot=\"textarea\"]").first();
}
```

Use it for Playwright assertions the wrapper does not offer:

```java
TextAreaElement validated = TextAreaElement.getByLabel(page, "Validated TextArea");
assertThat(validated.getInputLocator()).hasAttribute("maxlength", "7");
```

## Validation

The constraint helpers read the `<textarea>`'s attributes:

```java
// View
TextArea validated = new TextArea("Validated TextArea");
validated.setRequired(true);
validated.setAllowedCharPattern("[0-8]");
validated.setPattern("\\d{7}");
validated.setMinLength(6);
validated.setMaxLength(7);
```

```java
// Test
TextAreaElement validated = TextAreaElement.getByLabel(page, "Validated TextArea");
validated.assertMinLength(6);
validated.assertMaxLength(7);
validated.assertPattern("\\d{7}");
validated.assertAllowedCharPattern("[0-8]");

validated.setValue("1");
validated.assertInvalid();
validated.assertErrorMessage("Minimum length is 6 characters");
validated.setValue("1238456");
validated.assertValid();
```

## Pitfalls

- **Wrapping a raw locator.** Build a `TextAreaElement`, not a `TextFieldElement`,
  around a `<vaadin-text-area>`: a `TextFieldElement` looks for a `slot="input"`
  child, which a text area does not have. This matters for helper components too:
  `new TextAreaElement(area.getHelperLocator())` when the helper is a text area.
- See also the [Text Field pitfalls](TextFieldElement.md#pitfalls), which all apply.

## See also

- [Text Field](TextFieldElement.md) — the full feature reference for text-like fields
- [Email Field](EmailFieldElement.md), [Password Field](PasswordFieldElement.md),
  [Message Input](MessageInputElement.md)
- [Finding components](../guide/finding-components.md),
  [Assertions](../guide/assertions.md),
  [Actions and waiting](../guide/actions-and-waiting.md)
- Full example: [TextAreaViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/TextAreaViewIT.java)
