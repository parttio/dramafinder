package org.vaadin.addons.dramafinder.element.shared;

import com.microsoft.playwright.Locator;

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

    /** Locator for the native input element inside the component. */
    default Locator getInputLocator() {
        return getLocator().locator("*[slot=\"input\"]").first();
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

    /** Check the component. */
    default void check() {
        getInputLocator().check();
    }

    /** Uncheck the component. */
    default void uncheck() {
        getInputLocator().uncheck();
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
