package org.vaadin.addons.dramafinder.element;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.shared.HasDisabledAttributeElement;
import org.vaadin.addons.dramafinder.element.shared.HasHelperElement;
import org.vaadin.addons.dramafinder.element.shared.HasLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;
import org.vaadin.addons.dramafinder.element.shared.HasTooltipElement;
import org.vaadin.addons.dramafinder.element.shared.HasValidationPropertiesElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-custom-field>}.
 * <p>
 * A custom field wraps several fields as one: it carries the label, helper
 * text, error message, required and invalid states, while the values live in
 * the wrapped fields. Reach those with {@link #getField(Class, String)} (by
 * their own accessible name) or {@link #getField(Class, int)} (by position)
 * and set or assert values on them. The host has the {@code group} ARIA role,
 * named by the custom field's label, which is what {@code getByLabel} matches.
 * <p>
 * The web component's own {@code value} is a tab-separated join of the inner
 * fields' client values (a select contributes its item key, not its label),
 * so this wrapper exposes no value getter; assert the inner fields, or the
 * server-side result, instead.
 */
@PlaywrightElement(CustomFieldElement.FIELD_TAG_NAME)
public class CustomFieldElement extends VaadinElement
        implements HasLabelElement, HasHelperElement, HasValidationPropertiesElement,
        HasDisabledAttributeElement, HasThemeElement, HasStyleElement, HasTooltipElement {

    public static final String FIELD_TAG_NAME = "vaadin-custom-field";

    /**
     * Create a {@code CustomFieldElement} from an existing locator.
     *
     * @param locator the locator for the {@code <vaadin-custom-field>} element
     */
    public CustomFieldElement(Locator locator) {
        super(locator);
    }

    /**
     * Get the first custom field on the page whose label matches, using the
     * {@code group} ARIA role.
     * <p>
     * The label is matched case-insensitively as a substring.
     *
     * @param page  the Playwright page
     * @param label the label, or part of it
     * @return the first matching {@code CustomFieldElement}
     */
    public static CustomFieldElement getByLabel(Page page, String label) {
        return new CustomFieldElement(page.locator(FIELD_TAG_NAME)
                .and(page.getByRole(AriaRole.GROUP, new Page.GetByRoleOptions().setName(label)))
                .first());
    }

    /**
     * Get the first custom field within a parent locator whose label matches,
     * using the {@code group} ARIA role.
     * <p>
     * The label is matched case-insensitively as a substring.
     *
     * @param parent the locator to search within
     * @param label  the label, or part of it
     * @return the first matching {@code CustomFieldElement}
     */
    public static CustomFieldElement getByLabel(Locator parent, String label) {
        return new CustomFieldElement(parent.locator(FIELD_TAG_NAME)
                .and(parent.page().getByRole(AriaRole.GROUP,
                        new Page.GetByRoleOptions().setName(label)))
                .first());
    }

    // ── Wrapped fields ─────────────────────────────────────────────────

    /**
     * Get a wrapped field by its own accessible name, using that element's
     * {@code getByLabel(Locator, String)} factory scoped to this custom field.
     * <p>
     * Inner fields usually have no visible label; an {@code aria-label} (Flow:
     * {@code setAriaLabel}) is what this matches. Matching follows the factory,
     * which for fields is case-insensitive and by substring.
     *
     * @param type  the element class of the wrapped field, e.g.
     *              {@code TextFieldElement.class}
     * @param label the wrapped field's accessible name
     * @param <T>   the element type
     * @return the wrapped field
     * @throws IllegalArgumentException if {@code type} has no static
     *                                  {@code getByLabel(Locator, String)}
     *                                  factory
     */
    public <T extends VaadinElement> T getField(Class<T> type, String label) {
        Method factory;
        try {
            factory = type.getMethod("getByLabel", Locator.class, String.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(type.getSimpleName()
                    + " has no getByLabel(Locator, String) factory; use getField(type, index)", e);
        }
        if (!Modifier.isStatic(factory.getModifiers())
                || !type.isAssignableFrom(factory.getReturnType())) {
            throw new IllegalArgumentException(type.getSimpleName()
                    + ".getByLabel(Locator, String) does not return a " + type.getSimpleName());
        }
        try {
            return type.cast(factory.invoke(null, getLocator(), label));
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(
                    "Cannot look up " + type.getSimpleName(), e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException("Cannot look up " + type.getSimpleName(), e);
        }
    }

    /**
     * Get a wrapped field by its position among the wrapped fields of the same
     * type, in DOM order.
     *
     * @param type  the element class of the wrapped field; it must be
     *              annotated with {@link PlaywrightElement}
     * @param index the 0-based position among fields of that type
     * @param <T>   the element type
     * @return the wrapped field
     * @throws IllegalArgumentException if {@code type} is not annotated with
     *                                  {@link PlaywrightElement}
     */
    public <T extends VaadinElement> T getField(Class<T> type, int index) {
        PlaywrightElement annotation = type.getAnnotation(PlaywrightElement.class);
        if (annotation == null) {
            throw new IllegalArgumentException(
                    type.getSimpleName() + " is not annotated with @PlaywrightElement");
        }
        Locator field = getLocator().locator("> " + annotation.value()).nth(index);
        try {
            return type.getConstructor(Locator.class).newInstance(field);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(
                    "Cannot instantiate " + type.getSimpleName(), e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException("Cannot instantiate " + type.getSimpleName(), e);
        }
    }

    // ── Own slots ──────────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     * <p>
     * Only the custom field's own helper: a wrapped field's helper text is not
     * matched.
     */
    @Override
    public Locator getHelperLocator() {
        return getLocator().locator("> [slot='helper']");
    }

    /**
     * {@inheritDoc}
     * <p>
     * Only the custom field's own tooltip: a wrapped field's tooltip is not
     * matched.
     */
    @Override
    public TooltipElement getTooltip() {
        return new TooltipElement(getLocator().locator("> " + TooltipElement.FIELD_TAG_NAME));
    }

    // ── States ─────────────────────────────────────────────────────────

    /** Assert that the field is marked required. */
    public void assertRequired() {
        assertThat(getLocator()).hasAttribute("required", "");
    }

    /** Assert that the field is not marked required. */
    public void assertNotRequired() {
        assertThat(getLocator()).not().hasAttribute("required", "");
    }

    /**
     * Whether the field is read-only.
     * <p>
     * Read from the {@code readonly} property: the web component does not
     * reflect it to an attribute, and Flow does not pass it on to the wrapped
     * fields, which keep their own read-only state.
     *
     * @return {@code true} if the custom field is read-only
     */
    public boolean isReadOnly() {
        return Boolean.TRUE.equals(getLocator().evaluate("el => !!el.readonly"));
    }

    /** Assert that the field is read-only (see {@link #isReadOnly()}). */
    public void assertReadOnly() {
        assertThat(getLocator()).hasJSProperty("readonly", true);
    }

    /** Assert that the field is not read-only (see {@link #isReadOnly()}). */
    public void assertNotReadOnly() {
        assertThat(getLocator()).not().hasJSProperty("readonly", true);
    }
}
