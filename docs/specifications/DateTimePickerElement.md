# DateTimePickerElement

> Full API (methods, signatures, descriptions): see [api-reference.md](../../skills/vaadin-playwright-test/api-reference.md). This note covers only behaviour that isn't obvious from the signatures.

## Composite of two sub-fields

`DateTimePickerElement` internally composes a `DatePickerElement` and a `TimePickerElement`, exposed by `getDatePicker()` and `getTimePicker()`. This changes several inherited behaviours:

- **`getAriaLabel()`** returns the *date* picker's ARIA label; `assertAriaLabel(String)` asserts it on *both* sub-fields.
- **`isEnabled()` / `assertEnabled()` / `assertDisabled()`** require *both* sub-fields to be in that state.
- **`setValue(String)` / `assertValue(String)`** touch the *date* half's input only. Use `typeText(date, time)` to type into both halves and `clear()` to empty both.
- You can set/assert each part independently via `setDate` / `setTime` and `assertDateValue` / `assertTimeValue`.

## Typing as a user does

`typeText(date, time)`, `setDate` and `setTime` fill a half's input and press Enter on it, then leave the field — no synthetic `change` event. The picker checks its halves when focus leaves it, so:

- an unparsable half (`typeText("dsdds", "10:00")`) leaves the value empty and the field invalid (bad input);
- one empty half (`typeText("", "10:00")`) leaves the value empty and the field invalid (incomplete);
- `setDate` alone leaves the field invalid (incomplete) until `setTime` fills the other half.

Each half uses its locale's format (`dd/mm/yyyy` and `hh:mm` in the test views).

```java
meeting.typeText("15/05/2023", "14:30");
meeting.assertDateValue("15/05/2023");
meeting.assertTimeValue("14:30");
meeting.assertValue(LocalDateTime.of(2023, 5, 15, 14, 30));

meeting.clear();                       // empties both halves
meeting.assertValue((LocalDateTime) null);
```
