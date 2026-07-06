# TimePickerElement

> Full API (methods, signatures, descriptions): see [api-reference.md](../../skills/vaadin-playwright-test/api-reference.md). This note covers only behaviour that isn't obvious from the signatures.

## String values use HH:mm, LocalTime is set directly

`setValue(String)` (inherited from `HasInputFieldElement`) / `assertValue(String)` expect the `HH:mm` display format (e.g. `"09:00"`), while `setValue(LocalTime)` sets the value directly. Pick the overload matching your format.

## Asserting a cleared value

After clearing, assert emptiness with the `LocalTime` overload passing an explicit null cast:

```java
time.clickClearButton();
time.assertValue((LocalTime) null);
```
