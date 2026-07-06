# RadioButtonElement

> Full API (methods, signatures, descriptions): see [api-reference.md](../../skills/vaadin-playwright-test/api-reference.md). This note covers only behaviour that isn't obvious from the signatures.

## Package-private — use through RadioButtonGroupElement

`RadioButtonElement` is package-private and intended for internal use by `RadioButtonGroupElement`; its factory and checked-state methods are also package-private. Drive radio buttons through the group rather than constructing them directly.

```java
RadioButtonGroupElement group = RadioButtonGroupElement.getByLabel(page, "Gender");
group.selectByLabel("Male");

RadioButtonElement maleRadio = group.getRadioButtonByLabel("Male");
maleRadio.assertChecked();
```
