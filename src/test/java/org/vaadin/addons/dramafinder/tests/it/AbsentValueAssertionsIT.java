package org.vaadin.addons.dramafinder.tests.it;

import com.microsoft.playwright.assertions.PlaywrightAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.vaadin.addons.dramafinder.element.IntegerFieldElement;
import org.vaadin.addons.dramafinder.element.ProgressBarElement;
import org.vaadin.addons.dramafinder.element.SelectElement;
import org.vaadin.addons.dramafinder.element.TextFieldElement;
import org.vaadin.addons.dramafinder.element.query.ElementQueries;
import org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@code null} passed to an assertion helper asserts that the value is absent
 * (issue #257): it passes on a component without the value and fails on one
 * that has it.
 * <p>
 * Isolated from the other test classes, which run in parallel: the assertion
 * timeout shortened here is JVM-wide, so their retrying assertions would give
 * up after a second too.
 */
@Isolated
public class AbsentValueAssertionsIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "label-lookup";
    }

    @BeforeEach
    public void shortTimeouts() {
        page.setDefaultTimeout(1000);
        PlaywrightAssertions.setDefaultAssertionTimeout(1000);
    }

    @AfterEach
    public void restoreTimeouts() {
        PlaywrightAssertions.setDefaultAssertionTimeout(5000);
    }

    @Test
    public void placeholder() {
        TextFieldElement.getById(page, "TextField-label").assertPlaceholder(null);
        TextFieldElement withPlaceholder = TextFieldElement.getById(page, "TextField-placeholder");
        withPlaceholder.assertPlaceholder("placeholder TextField");
        assertThrows(AssertionError.class, () -> withPlaceholder.assertPlaceholder(null));
    }

    @Test
    public void selectPlaceholder() {
        SelectElement.getById(page, "Select-label").assertPlaceholder(null);
        assertNull(SelectElement.getById(page, "Select-label").getPlaceholder());
        SelectElement withPlaceholder = SelectElement.getById(page, "Select-placeholder");
        withPlaceholder.assertPlaceholder("placeholder Select");
        assertThrows(AssertionError.class, () -> withPlaceholder.assertPlaceholder(null));
    }

    @Test
    public void allowedCharPattern() {
        TextFieldElement field = TextFieldElement.getById(page, "TextField-label");
        assertNull(field.getAllowedCharPattern());
        field.assertAllowedCharPattern(null);
        // an integer field restricts its input without a pattern being set
        IntegerFieldElement integerField = IntegerFieldElement.getById(page, "IntegerField-label");
        assertThrows(AssertionError.class, () -> integerField.assertAllowedCharPattern(null));
    }

    @Test
    public void integerFieldMinAndMax() {
        IntegerFieldElement field = IntegerFieldElement.getById(page, "IntegerField-label");
        assertNull(field.getMin());
        assertNull(field.getMax());
        field.assertMin(null);
        field.assertMax(null);

        field.setMin(1);
        field.setMax(5);
        field.assertMin(1);
        field.assertMax(5);
        assertThrows(AssertionError.class, () -> field.assertMin(null));
        assertThrows(AssertionError.class, () -> field.assertMax(null));
    }

    @Test
    public void progressBarValue() {
        page.navigate(getUrl() + "progress-bar");
        ProgressBarElement bar = ElementQueries.find(page, ProgressBarElement.class).first();
        bar.assertValue(0.5);
        assertThrows(AssertionError.class, () -> bar.assertValue(null));
    }
}
