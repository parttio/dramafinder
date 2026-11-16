package org.vaadin.addons.dramafinder.element.shared;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.WaitForSelectorState;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Mixin for components backed by a checkbox-like native input, i.e. one whose
 * state is a boolean {@code checked} flag rather than a textual value
 * ({@code vaadin-checkbox}, {@code vaadin-radio-button}, {@code vaadin-switch}).
 * <p>
 * The checked state, and the enablement, ARIA label and focus handling, all live
 * on the inner input rather than on the component root, so this mixin redirects
 * {@link #getEnabledLocator()}, {@link #getAriaLabelLocator()} and
 * {@link #getFocusLocator()} to {@link #getInputLocator()}.
 * <p>
 * Deliberately unrelated to {@link HasValueElement}: filling a checkbox-type
 * input throws, and its {@code value} is the constant {@code "on"}, so the
 * textual value API does not apply here. A class implementing both mixins has to
 * override {@link #getInputLocator()} to resolve the clash between the two
 * defaults.
 */
public interface HasCheckedElement extends HasEnabledElement, HasAriaLabelElement, FocusableElement {

    /**
     * Locator for the native input element inside the component.
     * <p>
     * Deliberately not narrowed with {@code .first()}: a checkbox-like component
     * holds exactly one input, so the only ambiguity this could hide is an
     * ambiguous component locator — and a lookup that matched two checkboxes
     * should fail with a strict-mode error rather than quietly act on the first.
     */
    default Locator getInputLocator() {
        return getLocator().locator("*[slot=\"input\"]");
    }

    /** {@inheritDoc} */
    @Override
    default Locator getEnabledLocator() {
        return getInputLocator();
    }

    /** {@inheritDoc} */
    @Override
    default Locator getAriaLabelLocator() {
        return getInputLocator();
    }

    /** {@inheritDoc} */
    @Override
    default Locator getFocusLocator() {
        return getInputLocator();
    }

    /**
     * Whether the component is currently checked.
     *
     * @return {@code true} if checked
     */
    default boolean isChecked() {
        return getInputLocator().isChecked();
    }

    /** Assert that the component is checked. */
    default void assertChecked() {
        assertThat(getInputLocator()).isChecked();
    }

    /** Assert that the component is not checked. */
    default void assertNotChecked() {
        assertThat(getInputLocator()).not().isChecked();
    }

    /**
     * Assert the component's checked state.
     *
     * @param checked expected checked state
     */
    default void assertChecked(boolean checked) {
        if (checked) {
            assertChecked();
        } else {
            assertNotChecked();
        }
    }

    /**
     * Check the component by clicking its visible label, or the input when it
     * has none.
     */
    default void check() {
        clickTarget().check();
        waitAfterAction();
    }

    /**
     * Uncheck the component by clicking its visible label, or the input when it
     * has none.
     */
    default void uncheck() {
        clickTarget().uncheck();
        waitAfterAction();
    }

    /**
     * What {@link #check()} and {@link #uncheck()} click. From Vaadin 25.4 the
     * native input stretches under the slotted label, which is painted on top,
     * so a click aimed at the input lands on the label and Playwright rejects it
     * as intercepted. Playwright accepts a {@code <label>} for {@code check()}
     * and reads the state from the control it labels.
     */
    private Locator clickTarget() {
        Locator input = getInputLocator();
        input.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED));
        Locator label = getLocator().locator(":scope > label[slot=\"label\"]");
        return label.isVisible() ? label : input;
    }

    /**
     * Check or uncheck the component.
     *
     * @param checked {@code true} to check, {@code false} to uncheck
     */
    default void setChecked(boolean checked) {
        if (checked) {
            check();
        } else {
            uncheck();
        }
    }
}
