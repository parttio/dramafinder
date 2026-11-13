# Password Field

`PasswordFieldElement` wraps `<vaadin-password-field>` (Flow: `PasswordField`). It
extends [`TextFieldElement`](TextFieldElement.md), so value, clear button,
placeholder, prefix and suffix, helper, validation (required, min/max length,
pattern, allowed characters), read-only, enabled, tooltip, theme and focus work
exactly as described there. This page covers what differs.

| | |
|---|---|
| Web component | `<vaadin-password-field>` |
| Flow component | `com.vaadin.flow.component.textfield.PasswordField` |
| Wrapper | `org.vaadin.addons.dramafinder.element.PasswordFieldElement` |
| Typed query | `find(page, PasswordFieldElement.class)` → `InputFieldQuery` |
| API | [PasswordFieldElement](../../skills/vaadin-playwright-test/api-reference.md#passwordfieldelement) |

## Finding the component

```java
PasswordFieldElement password = PasswordFieldElement.getByLabel(page, "PasswordField");
PasswordFieldElement validated = PasswordFieldElement.getByLabelContaining(page, "validated");
PasswordFieldElement scoped = PasswordFieldElement.getByLabel(page.locator("#container"), "My Label");
PasswordFieldElement byId = PasswordFieldElement.getById(page, "new-password");
PasswordFieldElement byTestId = PasswordFieldElement.getByTestId(page, "new-password");

// Placeholder or aria-label only: use the typed query
PasswordFieldElement byPlaceholder = find(page, PasswordFieldElement.class).withPlaceholder("Enter text here").single();
PasswordFieldElement byName = find(page, PasswordFieldElement.class).withAccessibleName("Invisible label").single();
```

`getByLabel` matches the whole visible label, case-sensitively;
`getByLabelContaining` a case-insensitive substring. Only
`<vaadin-password-field>` elements are considered.

## Find, set and assert

```java
// View
PasswordField password = new PasswordField("PasswordField");
password.setRequired(true);
password.setHelperText("Helper text");
password.setI18n(new PasswordField.PasswordFieldI18n()
        .setRequiredErrorMessage("Field is required"));
```

```java
// Test
PasswordFieldElement password = PasswordFieldElement.getByLabel(page, "PasswordField");
password.assertHelperHasText("Helper text");
password.assertValue("");

password.setValue("new value");
password.assertValue("new value");   // the input value is readable although masked
password.assertValid();

password.setValue("");
password.assertInvalid();
password.assertErrorMessage("Field is required");
```

## Password rules

Password policies are usually expressed with the same constraints as a text field:

```java
// View
PasswordField validated = new PasswordField("Validated PasswordField");
validated.setRequired(true);
validated.setAllowedCharPattern("[0-8]");
validated.setPattern("\\d{7}");
validated.setMinLength(6);
validated.setMaxLength(7);
validated.setI18n(new PasswordField.PasswordFieldI18n()
        .setMinLengthErrorMessage("Minimum length is 6 characters")
        .setPatternErrorMessage("Invalid code format"));
```

```java
// Test
PasswordFieldElement validated = PasswordFieldElement.getByLabel(page, "Validated PasswordField");
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

## Masking and the reveal button

The value is masked because the native input has `type="password"`. The wrapper
has no method for the reveal (eye) button; assert the masking with Playwright on
the input locator:

```java
PasswordFieldElement password = PasswordFieldElement.getByLabel(page, "PasswordField");
assertThat(password.getInputLocator()).hasAttribute("type", "password");
```

To exercise the reveal button, click the web component's `slot="reveal"` child with
plain Playwright, then check the input type. This is not a wrapper action, so it
does not wait for the server; the retrying assertion covers the client-side
change:

```java
password.getLocator().locator("[slot='reveal']").click();
assertThat(password.getInputLocator()).hasAttribute("type", "text");
```

## Pitfalls

- **No reveal-button helper.** Use plain Playwright as above; the repository's ITs
  do not cover the reveal button.
- **A helper component of the same type.** `getHelperLocator()` returns the helper
  slot content; when the helper is itself a password field, wrap it:
  `new PasswordFieldElement(password.getHelperLocator())`.
- See also the [Text Field pitfalls](TextFieldElement.md#pitfalls), which all apply.

## See also

- [Text Field](TextFieldElement.md) — the full feature reference for text-like fields
- [Email Field](EmailFieldElement.md), [Login](LoginElement.md)
- [Finding components](../guide/finding-components.md),
  [Assertions](../guide/assertions.md),
  [Actions and waiting](../guide/actions-and-waiting.md)
- Full example: [PasswordFieldViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/PasswordFieldViewIT.java)
