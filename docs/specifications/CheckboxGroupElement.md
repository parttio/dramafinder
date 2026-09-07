# CheckboxGroupElement

> Full API (methods, signatures, descriptions): see [api-reference.md](../../skills/vaadin-playwright-test/api-reference.md). This note covers only behaviour that isn't obvious from the signatures.

## Items are addressed by label, not by value

`CheckboxGroup`'s client-side `value` is an array of server-generated item
keys (`"1"`, `"2"`, …), not of the item strings a test knows about. Every
selection method therefore takes the checkbox's **visible label**, and
`getSelectedValues()` returns labels as well.

```java
CheckboxGroupElement group = CheckboxGroupElement.getByLabel(page, "Toppings");

group.selectByLabel("Cheese", "Ham");
group.assertSelected("Cheese", "Ham");

group.deselectByLabel("Ham");
assertEquals(List.of("Cheese"), group.getSelectedValues());

group.deselectAll();
group.assertSelected(); // no argument asserts an empty selection
```

Labels are matched by accessible name (ARIA role `checkbox`), the same way
`CheckboxElement.getByLabel` matches, so the match is case-insensitive and
partial. Use distinct labels when a group mixes items such as `Option 1` and
`Option 10`.

## assertSelected is exhaustive

`assertSelected(String...)` asserts the *complete* selection: the number of
checked checkboxes must equal the number of labels passed, and each label must
be checked. Calling it with no argument (or `null`) asserts that nothing is
selected.

## Individual checkboxes

Items are exposed as regular `CheckboxElement` instances, so the whole checkbox
API — including per-item enablement from `setItemEnabledProvider` — is
available:

```java
group.getCheckbox("Ham").assertDisabled();
group.getCheckboxes().forEach(CheckboxElement::assertNotChecked);
```
