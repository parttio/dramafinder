package org.vaadin.addons.dramafinder.element.shared;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Mixin for components whose enablement has to be read from the {@code disabled}
 * attribute of the host element instead of Playwright's enablement check.
 * <p>
 * Playwright considers an element disabled only when it is a native form
 * control carrying {@code disabled}, or when it has {@code aria-disabled="true"}
 * on a role that supports it ({@code button}, {@code link}, {@code menuitem},
 * {@code tab}, {@code option}, …). Vaadin's {@code DisabledMixin} sets both
 * {@code disabled} and {@code aria-disabled="true"} on the host custom element,
 * so for hosts with another role — {@code listitem}, {@code list},
 * {@code navigation}, {@code group}, plain containers — Playwright reports the
 * component as enabled even while it is disabled.
 * <p>
 * This mixin redefines the whole trio ({@link #isEnabled()},
 * {@link #assertEnabled()} and {@link #assertDisabled()}) in terms of the
 * {@code disabled} attribute of {@link #getEnabledLocator()}, keeping the getter
 * and the assertions consistent with each other. Prefer plain
 * {@link HasEnabledElement} whenever the enablement locator is a native form
 * control or has a role Playwright understands, since its checks also cover
 * {@code aria-disabled} and inherited {@code fieldset} state.
 */
public interface HasDisabledAttributeElement extends HasEnabledElement {

    /** {@inheritDoc} */
    @Override
    default boolean isEnabled() {
        return getEnabledLocator().getAttribute("disabled") == null;
    }

    /** {@inheritDoc} */
    @Override
    default void assertEnabled() {
        assertThat(getEnabledLocator()).not().hasAttribute("disabled", Pattern.compile(".*"));
    }

    /** {@inheritDoc} */
    @Override
    default void assertDisabled() {
        assertThat(getEnabledLocator()).hasAttribute("disabled", Pattern.compile(".*"));
    }
}
