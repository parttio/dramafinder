package org.vaadin.addons.dramafinder.element.shared;

import com.microsoft.playwright.Locator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Mixin for components that expose a read-only state through the reflected
 * {@code readonly} attribute.
 */
public interface HasReadOnlyElement extends HasLocatorElement {

    /** Attribute reflecting the read-only state. */
    String READONLY_ATTRIBUTE = "readonly";

    /** Locator used to check the read-only state. Defaults to root. */
    default Locator getReadOnlyLocator() {
        return getLocator();
    }

    /** Whether the component is read-only. */
    default boolean isReadOnly() {
        return getReadOnlyLocator().getAttribute(READONLY_ATTRIBUTE) != null;
    }

    /** Whether the component's read-only state matches the expected value. */
    default boolean isReadOnly(boolean readOnly) {
        return isReadOnly() == readOnly;
    }

    /** Assert that the component is read-only. */
    default void assertReadOnly() {
        assertThat(getReadOnlyLocator()).hasAttribute(READONLY_ATTRIBUTE, "");
    }

    /** Assert that the component is read-only (true) or not read-only (false). */
    default void assertReadOnly(boolean readOnly) {
        if (readOnly) {
            assertReadOnly();
        } else {
            assertNotReadOnly();
        }
    }

    /** Assert that the component is not read-only. */
    default void assertNotReadOnly() {
        assertThat(getReadOnlyLocator()).not().hasAttribute(READONLY_ATTRIBUTE, "");
    }
}
