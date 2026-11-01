# CustomFieldElement

> Full API (methods, signatures, descriptions): see [api-reference.md](../../skills/vaadin-playwright-test/api-reference.md). This note covers only behaviour that isn't obvious from the signatures.

## The value lives in the wrapped fields

A `vaadin-custom-field` wraps several fields and carries the label, helper text,
error message, required and invalid states for them as a whole. Its client-side
`value` is a tab-separated join of the wrapped fields' client values — a
`vaadin-select` contributes its item *key* (`"2"`), not the label (`"+49"`) — so
it says little about what the user sees, and `CustomFieldElement` has no value
getter. Set and assert values on the wrapped fields, and assert the server-side
result through whatever the view renders from it.

```java
CustomFieldElement phone = CustomFieldElement.getByLabel(page, "Phone");
phone.getField(SelectElement.class, "Country code").selectItem("+358");
phone.getField(TextFieldElement.class, "Number").setValue("40 1234567");
```

## Reaching a wrapped field

- `getField(type, label)` calls the type's own `getByLabel(Locator, String)`
  factory scoped to the custom field. Wrapped fields rarely have a visible label;
  give them an `aria-label` (Flow: `setAriaLabel`) and pass that. Matching follows
  the factory: case-insensitive, by substring. A type without that factory
  (e.g. `ButtonElement`) throws `IllegalArgumentException`.
- `getField(type, index)` takes the n-th direct child of that type, for fields
  with no accessible name. The type must carry `@PlaywrightElement`.

`ViewOutline` lists a custom field's wrapped fields under it, with their names,
as `fields (getField(Type.class, label or index)): ...`.

## Own slots only

`getHelperLocator()`, `getErrorMessageLocator()`, `getLabelLocator()` and
`getTooltip()` read the custom field's own slotted elements, never those of a
wrapped field.

## States

- **Disabled** is the host's `disabled` attribute (the host has the `group` role,
  which Playwright does not treat as disableable). Flow also disables every
  wrapped field.
- **Read-only** is a property the web component does not reflect, so
  `isReadOnly()` / `assertReadOnly()` read the property. Flow does not pass
  read-only on to the wrapped fields; check them separately if it matters.
- **Required** is the host's `required` attribute (Flow:
  `setRequiredIndicatorVisible(true)`).
