# DateTimePickerElement

> Full API (methods, signatures, descriptions): see [api-reference.md](../../skills/vaadin-playwright-test/api-reference.md). This note covers only behaviour that isn't obvious from the signatures.

## Composite of two sub-fields

`DateTimePickerElement` internally composes a `DatePickerElement` and a `TimePickerElement`. This changes several inherited behaviours:

- **`getAriaLabel()`** returns the *date* picker's ARIA label; `assertAriaLabel(String)` asserts it on *both* sub-fields.
- **`isEnabled()` / `assertEnabled()` / `assertDisabled()`** require *both* sub-fields to be in that state.
- You can set/assert each part independently via `setDate` / `setTime` and `assertDateValue` / `assertTimeValue`.

The `String` value format is `dd/mm/yyyy hh:mm`.

```java
// Set date and time separately, then assert each part
meeting.setDate("15/05/2023");
meeting.setTime("14:30");
meeting.assertDateValue("15/05/2023");
meeting.assertTimeValue("14:30");
```
