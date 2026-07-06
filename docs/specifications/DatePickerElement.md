# DatePickerElement

> Full API (methods, signatures, descriptions): see [api-reference.md](../../skills/vaadin-playwright-test/api-reference.md). This note covers only behaviour that isn't obvious from the signatures.

## String values use the localized format, LocalDate uses ISO

`setValue(String)` / `assertValue(String)` expect the localized display format `dd/mm/yyyy` (e.g. `"15/05/2023"`), whereas `setValue(LocalDate)` uses ISO-8601. Pick the overload that matches the format you're working with.

## Asserting a cleared value

After clearing, assert emptiness with the `LocalDate` overload passing an explicit null cast to disambiguate:

```java
date.clickClearButton();
date.assertValue((LocalDate) null);
```
