# Email Field

`EmailFieldElement` wraps `<vaadin-email-field>` (Flow: `EmailField`). It extends
[`TextFieldElement`](TextFieldElement.md), so value, clear button, placeholder,
prefix and suffix, helper, validation, read-only, enabled, tooltip, theme and
focus work exactly as described there. This page covers what is specific to
email fields.

| | |
|---|---|
| Web component | `<vaadin-email-field>` |
| Flow component | `com.vaadin.flow.component.textfield.EmailField` |
| Wrapper | `org.vaadin.addons.dramafinder.element.EmailFieldElement` |
| Typed query | `find(page, EmailFieldElement.class)` → `InputFieldQuery` |
| API | [EmailFieldElement](../../skills/vaadin-playwright-test/api-reference.md#emailfieldelement) |

## Finding the component

```java
EmailFieldElement email = EmailFieldElement.getByLabel(page, "Email");          // whole label
EmailFieldElement validated = EmailFieldElement.getByLabelContaining(page, "validated");
EmailFieldElement scoped = EmailFieldElement.getByLabel(page.locator("#container"), "My Label");
EmailFieldElement byId = EmailFieldElement.getById(page, "contact-email");
EmailFieldElement byTestId = EmailFieldElement.getByTestId(page, "contact-email");

// Placeholder or aria-label only: use the typed query
EmailFieldElement byPlaceholder = find(page, EmailFieldElement.class).withPlaceholder("your@email.com").single();
EmailFieldElement byName = find(page, EmailFieldElement.class).withAccessibleName("Work email").single();
```

`getByLabel` matches the whole visible label, case-sensitively;
`getByLabelContaining` a case-insensitive substring. The factories only consider
`<vaadin-email-field>`: `TextFieldElement.getByLabel(page, "Email")` does not find
an email field.

## Find, set and assert

```java
// View
EmailField email = new EmailField("Email");
email.setTooltipText("Tooltip for email field");
email.setPlaceholder("your@email.com");
email.setClearButtonVisible(true);
email.setPrefixComponent(new Span("@"));
```

```java
// Test
EmailFieldElement email = EmailFieldElement.getByLabel(page, "Email");
email.assertPlaceholder("your@email.com");
email.assertPrefixHasText("@");
email.assertTooltipHasText("Tooltip for email field");

email.assertClearButtonNotVisible();
email.setValue("test@vaadin.com");
email.assertValue("test@vaadin.com");
email.assertClearButtonVisible();
email.clickClearButton();
email.assertValue("");
```

## Email validation

The web component checks the address format in the browser, and Flow validates it
on the server. A malformed address makes the field invalid and shows the error
message set with `setErrorMessage`.

```java
// View
EmailField email = new EmailField("Validated Email");
email.setHelperText("This field is required.");
email.setRequired(true);
email.setErrorMessage("Please enter a valid email address");
```

```java
// Test
EmailFieldElement email = EmailFieldElement.getByLabel(page, "Validated Email");
email.assertHelperHasText("This field is required.");

email.setValue("not-an-email");
email.assertInvalid();
email.assertErrorMessage("Please enter a valid email address");

email.setValue("valid@email.com");
email.assertValid();

email.clear();            // required and now empty
email.assertInvalid();
email.assertErrorMessage("Please enter a valid email address");
```

Length and pattern constraints (`assertMinLength`, `assertMaxLength`,
`assertPattern`, `assertAllowedCharPattern`) work as on
[Text Field](TextFieldElement.md#validation).

## Pitfalls

- **One error message for every failure.** `setErrorMessage(...)` replaces the
  per-constraint messages: the field above shows the same text for a malformed
  address and for an empty required field. Assert `assertInvalid()` together with
  the message to tell the states apart.
- See also the [Text Field pitfalls](TextFieldElement.md#pitfalls), which all apply.

## See also

- [Text Field](TextFieldElement.md) — the full feature reference for text-like fields
- [Password Field](PasswordFieldElement.md), [Text Area](TextAreaElement.md)
- [Finding components](../guide/finding-components.md),
  [Assertions](../guide/assertions.md),
  [Actions and waiting](../guide/actions-and-waiting.md)
- Full example: [EmailFieldViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/EmailFieldViewIT.java)
