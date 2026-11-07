package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.query.ElementQueries;
import org.vaadin.addons.dramafinder.element.query.FieldQuery;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.shared.FocusableElement;
import org.vaadin.addons.dramafinder.element.shared.HasAriaLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasCheckedElement;
import org.vaadin.addons.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.addons.dramafinder.element.shared.HasHelperElement;
import org.vaadin.addons.dramafinder.element.shared.HasLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasValidationPropertiesElement;
import org.vaadin.addons.dramafinder.element.utils.IdLocator;

/**
 * PlaywrightElement for {@code <vaadin-switch>}.
 * <p>
 * A switch is functionally equivalent to a checkbox, but is presented as an
 * on/off toggle and has no indeterminate state. The checked-state API comes
 * from {@link HasCheckedElement}; the other mixins cover label, helper,
 * validation and enablement.
 * <p>
 * The underlying {@code com.vaadin.flow.component.checkbox.Switch} component is
 * experimental and requires the {@code com.vaadin.experimental.switchComponent}
 * feature flag to be enabled in the application under test.
 */
@PlaywrightElement(SwitchElement.FIELD_TAG_NAME)
public class SwitchElement extends VaadinElement
        implements FocusableElement, HasAriaLabelElement, HasCheckedElement, HasEnabledElement,
        HasHelperElement, HasStyleElement, HasLabelElement, HasValidationPropertiesElement,
        Findable<FieldQuery<SwitchElement>> {

    public static final String FIELD_TAG_NAME = "vaadin-switch";

    /**
     * Create a new {@code SwitchElement}.
     *
     * @param locator the locator for the {@code <vaadin-switch>} element
     */
    public SwitchElement(Locator locator) {
        super(locator);
    }

    /**
     * Get the {@code SwitchElement} whose visible label is exactly {@code label}, searching the entire page.
     * <p>
     * The label is the text of the component's own {@code label} slot, matched in
     * full and case-sensitively. An {@code aria-label} is not a label: use
     * {@code find(page, SwitchElement.class).withAccessibleName(..)}. No {@code .first()} is applied: two components
     * with that label fail with a strict-mode error. Same as
     * {@code find(page, SwitchElement.class).withLabel(label)}; use
     * {@link #getByLabelContaining(Page, String)} to match part of the label.
     *
     * @param page  the Playwright page
     * @param label the full visible label
     * @return the matching {@code SwitchElement}
     */
    public static SwitchElement getByLabel(Page page, String label) {
        return new SwitchElement(ElementQueries.find(page, SwitchElement.class).withLabel(label).getLocator());
    }

    /**
     * Get the {@code SwitchElement} whose visible label contains {@code text}, searching the entire page.
     * <p>
     * The label is matched case-insensitively as a substring; an {@code aria-label}
     * is not a label. No {@code .first()} is applied: several
     * matching components fail with a strict-mode error. Same as
     * {@code find(page, SwitchElement.class).withLabelContaining(text)}.
     *
     * @param page  the Playwright page
     * @param text  part of the visible label
     * @return the matching {@code SwitchElement}
     */
    public static SwitchElement getByLabelContaining(Page page, String text) {
        return new SwitchElement(ElementQueries.find(page, SwitchElement.class).withLabelContaining(text).getLocator());
    }

    /**
     * Get the {@code SwitchElement} whose visible label is exactly {@code label}, scoped to the given locator.
     * <p>
     * The label is the text of the component's own {@code label} slot, matched in
     * full and case-sensitively. An {@code aria-label} is not a label: use
     * {@code find(locator, SwitchElement.class).withAccessibleName(..)}. No {@code .first()} is applied: two components
     * with that label fail with a strict-mode error. Same as
     * {@code find(locator, SwitchElement.class).withLabel(label)}; use
     * {@link #getByLabelContaining(Locator, String)} to match part of the label.
     *
     * @param locator the locator whose subtree is searched
     * @param label   the full visible label
     * @return the matching {@code SwitchElement}
     */
    public static SwitchElement getByLabel(Locator locator, String label) {
        return new SwitchElement(ElementQueries.find(locator, SwitchElement.class).withLabel(label).getLocator());
    }

    /**
     * Get the {@code SwitchElement} whose visible label contains {@code text}, scoped to the given locator.
     * <p>
     * The label is matched case-insensitively as a substring; an {@code aria-label}
     * is not a label. No {@code .first()} is applied: several
     * matching components fail with a strict-mode error. Same as
     * {@code find(locator, SwitchElement.class).withLabelContaining(text)}.
     *
     * @param locator the locator whose subtree is searched
     * @param text    part of the visible label
     * @return the matching {@code SwitchElement}
     */
    public static SwitchElement getByLabelContaining(Locator locator, String text) {
        return new SwitchElement(ElementQueries.find(locator, SwitchElement.class).withLabelContaining(text).getLocator());
    }

    // ── Lookup by id or test id ────────────────────────────────────────

    /**
     * Get a {@code SwitchElement} by its {@code id}, searching the entire page.
     * <p>
     * A fallback for a component without a user-visible name: prefer the label or
     * text lookups when there is one. The id must match in full; two components
     * with that id fail with a strict-mode error.
     *
     * @param page the Playwright page
     * @param id   the full {@code id}
     * @return the matching {@code SwitchElement}
     */
    public static SwitchElement getById(Page page, String id) {
        return new SwitchElement(IdLocator.byId(page, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code SwitchElement} by its {@code id}, scoped to the given locator.
     * <p>
     * A fallback for a component without a user-visible name. The id must match in
     * full; two components with that id inside the scope fail with a strict-mode
     * error.
     *
     * @param locator the locator whose subtree is searched
     * @param id      the full {@code id}
     * @return the matching {@code SwitchElement}
     */
    public static SwitchElement getById(Locator locator, String id) {
        return new SwitchElement(IdLocator.byId(locator, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code SwitchElement} by its test id, searching the entire page.
     * <p>
     * The test id is Playwright's test id attribute, {@code data-testid} unless
     * reconfigured — the attribute Flow's {@code Component.setTestId(String)} sets.
     * A fallback for a component without a user-visible name; it must match in
     * full, and two components with that test id fail with a strict-mode error.
     *
     * @param page   the Playwright page
     * @param testId the full test id
     * @return the matching {@code SwitchElement}
     */
    public static SwitchElement getByTestId(Page page, String testId) {
        return new SwitchElement(IdLocator.byTestId(page, FIELD_TAG_NAME, testId));
    }

    /**
     * Get a {@code SwitchElement} by its test id, scoped to the given locator.
     * <p>
     * See {@link #getByTestId(Page, String)}; only components inside the scope are
     * considered.
     *
     * @param locator the locator whose subtree is searched
     * @param testId  the full test id
     * @return the matching {@code SwitchElement}
     */
    public static SwitchElement getByTestId(Locator locator, String testId) {
        return new SwitchElement(IdLocator.byTestId(locator, FIELD_TAG_NAME, testId));
    }
}
