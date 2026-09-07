# SliderElement

> Full API (methods, signatures, descriptions): see [api-reference.md](../../skills/vaadin-playwright-test/api-reference.md). This note covers only behaviour that isn't obvious from the signatures.

## One element for both Flow sliders

`IntegerSlider` and `DecimalSlider` both render `<vaadin-slider>`, so
`SliderElement` covers both. Values are always handled as `double`; for an
`IntegerSlider` the values are whole numbers.

```java
SliderElement volume = SliderElement.getByLabel(page, "Volume");
volume.assertValue(50);
```

The range variants (`IntegerRangeSlider` / `DecimalRangeSlider`, tag
`<vaadin-range-slider>`) are a different component and are not covered by this
element.

## Value, constraints, and focus live on the native input

The slider renders a native `<input type="range">` in its light DOM. That input
carries the ARIA role `slider`, the accessible name, and the `value`, `min`,
`max` and `step` constraints, and it is also what receives focus — so
`getByLabel` matches on role `slider`, and `focus()` / `assertIsFocused()`
target the input rather than the component root.

`setValue(double)` writes to that input and dispatches `input` and `change`,
which is the same path a user interaction takes; the component then snaps the
value to `step` and clamps it to `min`/`max`. Asking for a value that is not on
the step grid therefore leaves the slider on the nearest allowed value:

```java
SliderElement measurement = SliderElement.getByLabel(page, "Measurement");
// min 0.5, max 10, step 0.5
measurement.setValue(5.7);
measurement.assertValue(5.5);
```

## Change the value with the keyboard, not the mouse

There is deliberately no drag helper: dragging the thumb is pixel-based and
flaky in Playwright. Use the keyboard helpers instead — they drive the same
code path a keyboard user takes.

```java
volume.increment();      // ArrowRight, one step up
volume.increment(4);     // four steps up
volume.decrement();      // ArrowLeft, one step down
volume.moveToMin();      // Home
volume.moveToMax();      // End
```

A read-only slider keeps the focus and the keyboard events but ignores them, so
its value stays put:

```java
SliderElement readOnly = SliderElement.getByLabel(page, "Read-only slider");
readOnly.assertReadOnly();
readOnly.increment();
readOnly.assertValue(30);
```
