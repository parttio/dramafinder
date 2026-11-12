# DatePickerElement

> Full API (methods, signatures, descriptions): see [api-reference.md](../../skills/vaadin-playwright-test/api-reference.md). This note covers only behaviour that isn't obvious from the signatures.

## String values use the localized format, LocalDate uses ISO

`setValue(String)` / `assertValue(String)` expect the localized display format `dd/mm/yyyy` (e.g. `"15/05/2023"`), whereas `setValue(LocalDate)` uses ISO-8601. Pick the overload that matches the format you're working with.

## `setValue(String)` types as a user does

`setValue(String)` fills the input and presses Enter on it — no synthetic `change` event — so the picker parses and commits the text itself, exactly as in a browser. That makes the bad-input path testable: text the picker cannot parse leaves the value empty and the field invalid, while the text stays in the input.

```java
date.setValue("dsdds");
date.assertInvalid();
date.assertValue("dsdds");                     // the input keeps the text
assertThat(date.getLocator()).hasJSProperty("value", ""); // the value is empty
```

`setValue(LocalDate)` instead sets the `value` property directly and skips parsing.

## Asserting a cleared value

After clearing, assert emptiness with the `LocalDate` overload passing an explicit null cast to disambiguate:

```java
date.clickClearButton();
date.assertValue((LocalDate) null);
```
