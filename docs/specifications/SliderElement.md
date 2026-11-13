# Slider

`SliderElement` wraps `<vaadin-slider>` (Flow: `IntegerSlider` and
`DecimalSlider`). It sets the value directly or with the keyboard, asserts the
value and the `min` / `max` / `step` constraints, and covers label, helper,
enabled and read-only state, focus, theme and tooltip.

| | |
|---|---|
| Web component | `<vaadin-slider>` |
| Flow component | `com.vaadin.flow.component.slider.IntegerSlider`, `com.vaadin.flow.component.slider.DecimalSlider` |
| Wrapper | `org.vaadin.addons.dramafinder.element.SliderElement` |
| Typed query | `find(page, SliderElement.class)` → `FieldQuery` |
| API | [SliderElement](../../skills/vaadin-playwright-test/api-reference.md#sliderelement) |

## One element for both Flow sliders

`IntegerSlider` and `DecimalSlider` both render `<vaadin-slider>`, so
`SliderElement` covers both. Values are always handled as `double`; for an
`IntegerSlider` the values are whole numbers.

The range variants (`IntegerRangeSlider` / `DecimalRangeSlider`, tag
`<vaadin-range-slider>`) are a different component and are not covered by this
element.

## Finding the component

```java
// View
IntegerSlider volume = new IntegerSlider("Volume", 0, 100);
volume.setId("volume");
volume.setTestId("volume-slider");
```

```java
// Test
SliderElement volume = SliderElement.getByLabel(page, "Volume");
SliderElement partial = SliderElement.getByLabelContaining(page, "vol");
SliderElement byId = SliderElement.getById(page, "volume");
SliderElement byTestId = SliderElement.getByTestId(page, "volume-slider");

// Scoped, and the typed query
SliderElement scoped = SliderElement.getByLabel(page.locator("main"), "Volume");
SliderElement viaQuery = find(page, SliderElement.class).withLabel("Volume").single();
```

`getByLabel` / `withLabel` match the slider's visible label (its `label` slot)
in full, case-sensitively; the `...Containing` variants match a
case-insensitive substring. Several matches fail with a strict-mode error.

## Value, constraints, and focus live on the native input

The slider renders a native `<input type="range">` in its light DOM. That input
carries the ARIA role `slider`, the accessible name, and the `value`, `min`,
`max` and `step` constraints, and it is also what receives focus and carries
the disabled state — so `getValue()`, the constraint getters and assertions,
`focus()` / `assertIsFocused()` and `assertEnabled()` / `assertDisabled()` all
target the input rather than the component root. `getInputLocator()` returns
it for raw Playwright assertions.

```java
// View
IntegerSlider slider = new IntegerSlider("Volume", 0, 100);
slider.setHelperText("Pick a value between 0 and 100");
slider.setValue(50);
```

```java
// Test
SliderElement volume = SliderElement.getByLabel(page, "Volume");
volume.assertLabel("Volume");
volume.assertHelperHasText("Pick a value between 0 and 100");
volume.assertValue(50);
volume.assertMin(0);
volume.assertMax(100);
volume.assertStep(1);
```

`getValue()` returns the current value as a `double`; `getMin()`, `getMax()`
and `getStep()` return a `Double` that is `null` when the input exposes no such
attribute. They read once, without retrying — prefer the `assert...` methods.

## Setting the value

`setValue(double)` writes to the input and dispatches `input` and `change`,
which is the same path a user interaction takes; the component then snaps the
value to `step` and clamps it to `min`/`max`, and the server's value-change
listener runs before the method returns.

```java
// View
Span selected = new Span("Selected: 50");
selected.setId("volume-value");
slider.addValueChangeListener(e -> selected.setText("Selected: " + e.getValue()));
```

```java
// Test
SliderElement volume = SliderElement.getByLabel(page, "Volume");
volume.setValue(75);
volume.assertValue(75);
assertThat(page.locator("#volume-value")).hasText("Selected: 75");
```

Asking for a value that is not on the step grid leaves the slider on the
nearest allowed value:

```java
// View
DecimalSlider slider = new DecimalSlider("Measurement", 0.5, 10);
slider.setStep(0.5);
slider.setValue(1.5);
```

```java
// Test
SliderElement measurement = SliderElement.getByLabel(page, "Measurement");
measurement.assertMin(0.5);
measurement.assertMax(10);
measurement.assertStep(0.5);

measurement.setValue(5.7);
measurement.assertValue(5.5);   // snapped to the step grid
```

## Change the value with the keyboard, not the mouse

There is deliberately no drag helper: dragging the thumb is pixel-based and
flaky in Playwright. Use the keyboard helpers instead — they drive the same
code path a keyboard user takes, and each key press waits for the server.

```java
SliderElement volume = SliderElement.getByLabel(page, "Volume"); // value 50
volume.increment();      // ArrowRight, one step up       -> 51
volume.increment(4);     // four steps up                 -> 55
volume.decrement();      // ArrowLeft, one step down      -> 54
volume.decrement(4);     // four steps down               -> 50
volume.moveToMax();      // End                           -> 100
volume.moveToMin();      // Home                          -> 0
volume.assertValue(0);
```

`increment(int)` and `decrement(int)` throw `IllegalArgumentException` for a
negative number of steps.

## Disabled and read-only sliders

```java
// View
IntegerSlider disabled = new IntegerSlider("Disabled slider", 0, 100);
disabled.setValue(20);
disabled.setEnabled(false);

IntegerSlider readOnly = new IntegerSlider("Read-only slider", 0, 100);
readOnly.setValue(30);
readOnly.setReadOnly(true);
```

```java
// Test
SliderElement disabled = SliderElement.getByLabel(page, "Disabled slider");
disabled.assertDisabled();
assertThrows(IllegalStateException.class, () -> disabled.setValue(70));
disabled.assertValue(20);

SliderElement readOnly = SliderElement.getByLabel(page, "Read-only slider");
readOnly.assertReadOnly();
readOnly.increment();            // keeps focus and key events, but ignores them
readOnly.assertValue(30);
assertThrows(IllegalStateException.class, () -> readOnly.setValue(50));
```

A script write is not gated by the input's state the way a user is, so
`setValue` refuses a disabled or read-only slider up front with an
`IllegalStateException` rather than leaving it showing a value the server never
accepted. The keyboard helpers behave like a user: a read-only slider keeps the
focus and the keyboard events but ignores them, so its value stays put.

`assertNotReadOnly()`, `assertReadOnly(boolean)` and `isReadOnly()` complete
the read-only API; `assertEnabled()` / `isEnabled()` the enabled one.

## Focus, theme, style and tooltip

```java
// View
IntegerSlider slider = new IntegerSlider("Styled slider", 0, 10);
slider.setTooltipText("Drag to change the rating");
slider.getElement().getThemeList().add("small");
slider.addClassName("rating-slider");
```

```java
// Test
SliderElement styled = SliderElement.getByLabel(page, "Styled slider");
styled.assertTheme("small");
styled.assertHasThemeVariant("small");
styled.assertCssClass("rating-slider");
styled.assertTooltipHasText("Drag to change the rating");

styled.focus();                  // focuses the native range input
styled.assertIsFocused();
```

## Pitfalls

- `setValue` snaps to `step` and clamps to `min`/`max`; assert the value the
  component settles on, not the one you asked for.
- `setValue` throws `IllegalStateException` on a disabled or read-only slider.
  To check that a user cannot change it, use the keyboard helpers and assert the
  value is unchanged.
- `<vaadin-range-slider>` (two thumbs) is not supported by this element.

## See also

- [Integer Field](IntegerFieldElement.md), [Number Field](NumberFieldElement.md),
  [Progress Bar](ProgressBarElement.md), [Tooltip](TooltipElement.md)
- [Finding components](../guide/finding-components.md),
  [Assertions](../guide/assertions.md),
  [Actions and waiting](../guide/actions-and-waiting.md),
  [Core concepts](../guide/core-concepts.md)
- Full example: [SliderViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/SliderViewIT.java)
