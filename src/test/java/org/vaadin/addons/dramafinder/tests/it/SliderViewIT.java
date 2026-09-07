package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.SliderElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SliderViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "slider";
    }

    @Test
    public void testTitle() {
        assertThat(page).hasTitle("Slider Demo");
        assertThat(page.getByText("Slider Demo")).isVisible();
    }

    @Test
    public void testBasicSlider() {
        SliderElement slider = SliderElement.getByLabel(page, "Volume");
        slider.assertVisible();
        slider.assertLabel("Volume");
        slider.assertHelperHasText("Pick a value between 0 and 100");
        slider.assertEnabled();
        slider.assertNotReadOnly();

        slider.assertValue(50);
        slider.assertMin(0);
        slider.assertMax(100);
        slider.assertStep(1);
        assertEquals(50d, slider.getValue());
        assertEquals(0d, slider.getMin());
        assertEquals(100d, slider.getMax());
        assertEquals(1d, slider.getStep());
    }

    @Test
    public void testSetValueUpdatesTheServerSideValue() {
        SliderElement slider = SliderElement.getByLabel(page, "Volume");
        slider.setValue(75);
        slider.assertValue(75);
        assertThat(page.locator("#volume-value")).hasText("Selected: 75");
    }

    @Test
    public void testKeyboardIncrementAndDecrement() {
        SliderElement slider = SliderElement.getByLabel(page, "Volume");
        slider.focus();
        slider.assertIsFocused();

        slider.increment();
        slider.assertValue(51);

        slider.increment(4);
        slider.assertValue(55);

        slider.decrement();
        slider.assertValue(54);

        slider.decrement(4);
        slider.assertValue(50);

        assertThat(page.locator("#volume-value")).hasText("Selected: 50");
    }

    @Test
    public void testKeyboardMoveToMinAndMax() {
        SliderElement slider = SliderElement.getByLabel(page, "Volume");
        slider.moveToMax();
        slider.assertValue(100);
        assertThat(page.locator("#volume-value")).hasText("Selected: 100");

        slider.moveToMin();
        slider.assertValue(0);
        assertThat(page.locator("#volume-value")).hasText("Selected: 0");
    }

    @Test
    public void testMinMaxStep() {
        SliderElement slider = SliderElement.getByLabel(page, "Measurement");
        slider.assertVisible();
        slider.assertHelperHasText("Value between 0.5 and 10, in steps of 0.5");
        slider.assertMin(0.5);
        slider.assertMax(10);
        slider.assertStep(0.5);
        slider.assertValue(1.5);

        slider.setValue(5.5);
        slider.assertValue(5.5);

        slider.increment();
        slider.assertValue(6);

        // A value that is not on the step grid is snapped to the closest step
        slider.setValue(5.7);
        slider.assertValue(5.5);
    }

    @Test
    public void testDisabledSlider() {
        SliderElement slider = SliderElement.getByLabel(page, "Disabled slider");
        slider.assertVisible();
        slider.assertDisabled();
        slider.assertValue(20);
    }

    @Test
    public void testReadOnlySlider() {
        SliderElement slider = SliderElement.getByLabel(page, "Read-only slider");
        slider.assertVisible();
        slider.assertReadOnly();
        slider.assertValue(30);

        // A read-only slider ignores the keyboard
        slider.increment();
        slider.assertValue(30);
    }

    @Test
    public void testThemeStyleAndTooltip() {
        SliderElement slider = SliderElement.getByLabel(page, "Styled slider");
        slider.assertVisible();
        slider.assertTheme("small");
        slider.assertCssClass("rating-slider");
        slider.assertTooltipHasText("Drag to change the rating");
        slider.assertMax(10);
        slider.assertValue(4);
    }
}
