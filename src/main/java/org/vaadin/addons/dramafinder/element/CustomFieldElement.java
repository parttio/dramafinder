package org.vaadin.addons.dramafinder.element;

import java.lang.reflect.InvocationTargetException;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.query.ElementQueries;
import org.vaadin.addons.dramafinder.element.query.ElementQuery;
import org.vaadin.addons.dramafinder.element.query.FieldQuery;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.shared.HasDisabledAttributeElement;
import org.vaadin.addons.dramafinder.element.shared.HasHelperElement;
import org.vaadin.addons.dramafinder.element.shared.HasLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;
import org.vaadin.addons.dramafinder.element.shared.HasTooltipElement;
import org.vaadin.addons.dramafinder.element.shared.HasValidationPropertiesElement;
import org.vaadin.addons.dramafinder.element.utils.IdLocator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-custom-field>}.
 * <p>
 * A custom field wraps several fields as one: it carries the label, helper
 * text, error message, required and invalid states, while the values live in
 * the wrapped fields. Reach those with {@link #getField(Class, String)} (by
 * their own accessible name) or {@link #getField(Class, int)} (by position)
 * and set or assert values on them. The host has the {@code group} ARIA role,
 * named by the custom field's label.
 * <p>
 * The web component's own {@code value} is a tab-separated join of the inner
 * fields' client values (a select contributes its item key, not its label),
 * so this wrapper exposes no value getter; assert the inner fields, or the
 * server-side result, instead.
 */
@PlaywrightElement(CustomFieldElement.FIELD_TAG_NAME)
public class CustomFieldElement extends VaadinElement
        implements HasLabelElement, HasHelperElement, HasValidationPropertiesElement,
        HasDisabledAttributeElement, HasThemeElement, HasStyleElement, HasTooltipElement,
        Findable<FieldQuery<CustomFieldElement>> {

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
     * Get the {@code CustomFieldElement} whose visible label is exactly {@code label}, searching the entire page.
     * <p>
     * The label is the text of the component's own {@code label} slot, matched in
     * full and case-sensitively. An {@code aria-label} or a placeholder is not a
     * label: use {@code find(page, CustomFieldElement.class).withAccessibleName(..)} or
     * {@code .withPlaceholder(..)}. No {@code .first()} is applied: two components
     * with that label fail with a strict-mode error. Same as
     * {@code find(page, CustomFieldElement.class).withLabel(label)}; use
     * {@link #getByLabelContaining(Page, String)} to match part of the label.
     *
     * @param page  the Playwright page
     * @param label the full visible label
     * @return the matching {@code CustomFieldElement}
     */
    public static CustomFieldElement getByLabel(Page page, String label) {
        return new CustomFieldElement(ElementQueries.find(page, CustomFieldElement.class).withLabel(label).getLocator());
    }

    /**
     * Get the {@code CustomFieldElement} whose visible label contains {@code text}, searching the entire page.
     * <p>
     * The label is matched case-insensitively as a substring; an {@code aria-label}
     * or a placeholder is not a label. No {@code .first()} is applied: several
     * matching components fail with a strict-mode error. Same as
     * {@code find(page, CustomFieldElement.class).withLabelContaining(text)}.
     *
     * @param page  the Playwright page
     * @param text  part of the visible label
     * @return the matching {@code CustomFieldElement}
     */
    public static CustomFieldElement getByLabelContaining(Page page, String text) {
        return new CustomFieldElement(ElementQueries.find(page, CustomFieldElement.class).withLabelContaining(text).getLocator());
    }

    /**
     * Get the {@code CustomFieldElement} whose visible label is exactly {@code label}, scoped to the given locator.
     * <p>
     * The label is the text of the component's own {@code label} slot, matched in
     * full and case-sensitively. An {@code aria-label} or a placeholder is not a
     * label: use {@code find(locator, CustomFieldElement.class).withAccessibleName(..)} or
     * {@code .withPlaceholder(..)}. No {@code .first()} is applied: two components
     * with that label fail with a strict-mode error. Same as
     * {@code find(locator, CustomFieldElement.class).withLabel(label)}; use
     * {@link #getByLabelContaining(Locator, String)} to match part of the label.
     *
     * @param locator the locator whose subtree is searched
     * @param label   the full visible label
     * @return the matching {@code CustomFieldElement}
     */
    public static CustomFieldElement getByLabel(Locator locator, String label) {
        return new CustomFieldElement(ElementQueries.find(locator, CustomFieldElement.class).withLabel(label).getLocator());
    }

    /**
     * Get the {@code CustomFieldElement} whose visible label contains {@code text}, scoped to the given locator.
     * <p>
     * The label is matched case-insensitively as a substring; an {@code aria-label}
     * or a placeholder is not a label. No {@code .first()} is applied: several
     * matching components fail with a strict-mode error. Same as
     * {@code find(locator, CustomFieldElement.class).withLabelContaining(text)}.
     *
     * @param locator the locator whose subtree is searched
     * @param text    part of the visible label
     * @return the matching {@code CustomFieldElement}
     */
    public static CustomFieldElement getByLabelContaining(Locator locator, String text) {
        return new CustomFieldElement(ElementQueries.find(locator, CustomFieldElement.class).withLabelContaining(text).getLocator());
    }

    // ── Wrapped fields ─────────────────────────────────────────────────

    /**
     * Get a wrapped field by its own accessible name, searched inside this
     * custom field. Same as {@code find(getLocator(), type).withAccessibleName(name).single()}.
     * <p>
     * Inner fields usually have no visible label; an {@code aria-label} (Flow:
     * {@code setAriaLabel}) is what names them. The name must match in full and
     * case-sensitively, and two wrapped fields sharing it fail with a
     * strict-mode error.
     *
     * @param type the element class of the wrapped field, e.g.
     *             {@code TextFieldElement.class}; it must be findable
     *             (see {@link org.vaadin.addons.dramafinder.element.query.Findable})
     * @param name the wrapped field's full accessible name
     * @param <T>  the element type
     * @return the wrapped field
     * @throws IllegalArgumentException if {@code type} cannot be used with {@code find}
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T extends VaadinElement> T getField(Class<T> type, String name) {
        if (!Findable.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException(type.getSimpleName()
                    + " cannot be found by accessible name; use getField(type, index)");
        }
        ElementQuery<?, ?> query = (ElementQuery<?, ?>) ElementQueries.find(getLocator(), (Class) type);
        return type.cast(query.withAccessibleName(name).single());
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

    // ── Lookup by id or test id ────────────────────────────────────────

    /**
     * Get a {@code CustomFieldElement} by its {@code id}, searching the entire page.
     * <p>
     * A fallback for a component without a user-visible name: prefer the label or
     * text lookups when there is one. The id must match in full; two components
     * with that id fail with a strict-mode error.
     *
     * @param page the Playwright page
     * @param id   the full {@code id}
     * @return the matching {@code CustomFieldElement}
     */
    public static CustomFieldElement getById(Page page, String id) {
        return new CustomFieldElement(IdLocator.byId(page, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code CustomFieldElement} by its {@code id}, scoped to the given locator.
     * <p>
     * A fallback for a component without a user-visible name. The id must match in
     * full; two components with that id inside the scope fail with a strict-mode
     * error.
     *
     * @param locator the locator whose subtree is searched
     * @param id      the full {@code id}
     * @return the matching {@code CustomFieldElement}
     */
    public static CustomFieldElement getById(Locator locator, String id) {
        return new CustomFieldElement(IdLocator.byId(locator, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code CustomFieldElement} by its test id, searching the entire page.
     * <p>
     * The test id is Playwright's test id attribute, {@code data-testid} unless
     * reconfigured — the attribute Flow's {@code Component.setTestId(String)} sets.
     * A fallback for a component without a user-visible name; it must match in
     * full, and two components with that test id fail with a strict-mode error.
     *
     * @param page   the Playwright page
     * @param testId the full test id
     * @return the matching {@code CustomFieldElement}
     */
    public static CustomFieldElement getByTestId(Page page, String testId) {
        return new CustomFieldElement(IdLocator.byTestId(page, FIELD_TAG_NAME, testId));
    }

    /**
     * Get a {@code CustomFieldElement} by its test id, scoped to the given locator.
     * <p>
     * See {@link #getByTestId(Page, String)}; only components inside the scope are
     * considered.
     *
     * @param locator the locator whose subtree is searched
     * @param testId  the full test id
     * @return the matching {@code CustomFieldElement}
     */
    public static CustomFieldElement getByTestId(Locator locator, String testId) {
        return new CustomFieldElement(IdLocator.byTestId(locator, FIELD_TAG_NAME, testId));
    }
}
