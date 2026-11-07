package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.query.ElementQueries;
import org.vaadin.addons.dramafinder.element.query.FieldQuery;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.addons.dramafinder.element.shared.HasHelperElement;
import org.vaadin.addons.dramafinder.element.shared.HasLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasValidationPropertiesElement;
import org.vaadin.addons.dramafinder.element.utils.IdLocator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-radio-group>}.
 * <p>
 * Provides helpers to select by label/value and assert selected state.
 */
@PlaywrightElement(RadioButtonGroupElement.FIELD_TAG_NAME)
public class RadioButtonGroupElement extends VaadinElement
        implements HasLabelElement, HasEnabledElement, HasHelperElement, HasValidationPropertiesElement,
        Findable<FieldQuery<RadioButtonGroupElement>> {

    public static final String FIELD_TAG_NAME = "vaadin-radio-group";

    /**
     * Create a new {@code RadioButtonGroupElement}.
     *
     * @param locator the locator for the {@code <vaadin-radio-group>} element
     */
    public RadioButtonGroupElement(Locator locator) {
        super(locator);
    }

    /**
     * Select a radio by its label text.
     */
    public void selectByLabel(String label) {
        getRadioButtonByLabel(label).check();
        waitAfterAction();
    }

    /**
     * Select a radio by its value.
     */
    public void selectByValue(String value) {
        getLocator().evaluate("(el, value) => el.value = value", value);
    }

    /**
     * Get a specific radio by its label within the group.
     */
    public RadioButtonElement getRadioButtonByLabel(String label) {
        return RadioButtonElement.getByLabel(getLocator(), label);
    }

    /**
     * Get the {@code RadioButtonGroupElement} whose visible label is exactly {@code label}, searching the entire page.
     * <p>
     * The label is the text of the component's own {@code label} slot, matched in
     * full and case-sensitively. An {@code aria-label} or a placeholder is not a
     * label: use {@code find(page, RadioButtonGroupElement.class).withAccessibleName(..)} or
     * {@code .withPlaceholder(..)}. No {@code .first()} is applied: two components
     * with that label fail with a strict-mode error. Same as
     * {@code find(page, RadioButtonGroupElement.class).withLabel(label)}; use
     * {@link #getByLabelContaining(Page, String)} to match part of the label.
     *
     * @param page  the Playwright page
     * @param label the full visible label
     * @return the matching {@code RadioButtonGroupElement}
     */
    public static RadioButtonGroupElement getByLabel(Page page, String label) {
        return new RadioButtonGroupElement(ElementQueries.find(page, RadioButtonGroupElement.class).withLabel(label).getLocator());
    }

    /**
     * Get the {@code RadioButtonGroupElement} whose visible label contains {@code text}, searching the entire page.
     * <p>
     * The label is matched case-insensitively as a substring; an {@code aria-label}
     * or a placeholder is not a label. No {@code .first()} is applied: several
     * matching components fail with a strict-mode error. Same as
     * {@code find(page, RadioButtonGroupElement.class).withLabelContaining(text)}.
     *
     * @param page  the Playwright page
     * @param text  part of the visible label
     * @return the matching {@code RadioButtonGroupElement}
     */
    public static RadioButtonGroupElement getByLabelContaining(Page page, String text) {
        return new RadioButtonGroupElement(ElementQueries.find(page, RadioButtonGroupElement.class).withLabelContaining(text).getLocator());
    }

    /**
     * Get the {@code RadioButtonGroupElement} whose visible label is exactly {@code label}, scoped to the given locator.
     * <p>
     * The label is the text of the component's own {@code label} slot, matched in
     * full and case-sensitively. An {@code aria-label} or a placeholder is not a
     * label: use {@code find(locator, RadioButtonGroupElement.class).withAccessibleName(..)} or
     * {@code .withPlaceholder(..)}. No {@code .first()} is applied: two components
     * with that label fail with a strict-mode error. Same as
     * {@code find(locator, RadioButtonGroupElement.class).withLabel(label)}; use
     * {@link #getByLabelContaining(Locator, String)} to match part of the label.
     *
     * @param locator the locator whose subtree is searched
     * @param label   the full visible label
     * @return the matching {@code RadioButtonGroupElement}
     */
    public static RadioButtonGroupElement getByLabel(Locator locator, String label) {
        return new RadioButtonGroupElement(ElementQueries.find(locator, RadioButtonGroupElement.class).withLabel(label).getLocator());
    }

    /**
     * Get the {@code RadioButtonGroupElement} whose visible label contains {@code text}, scoped to the given locator.
     * <p>
     * The label is matched case-insensitively as a substring; an {@code aria-label}
     * or a placeholder is not a label. No {@code .first()} is applied: several
     * matching components fail with a strict-mode error. Same as
     * {@code find(locator, RadioButtonGroupElement.class).withLabelContaining(text)}.
     *
     * @param locator the locator whose subtree is searched
     * @param text    part of the visible label
     * @return the matching {@code RadioButtonGroupElement}
     */
    public static RadioButtonGroupElement getByLabelContaining(Locator locator, String text) {
        return new RadioButtonGroupElement(ElementQueries.find(locator, RadioButtonGroupElement.class).withLabelContaining(text).getLocator());
    }

    /**
     * Set the selected value by label.
     */
    public void setValue(String value) {
        RadioButtonElement.getByLabel(getLocator(), value).check();
        waitAfterAction();
    }

    /**
     * Assert the selected value by label.
     */
    public void assertValue(String value) {
        if (value != null && !value.isEmpty()) {
            assertThat(getLocator().locator("vaadin-radio-button input:checked"))
                    .hasCount(1);
            RadioButtonElement.getByLabel(getLocator(), value).assertChecked();
        } else {
            assertThat(getLocator().locator("vaadin-radio-button input:checked"))
                    .hasCount(0);
        }
    }

    // ── Lookup by id or test id ────────────────────────────────────────

    /**
     * Get a {@code RadioButtonGroupElement} by its {@code id}, searching the entire page.
     * <p>
     * A fallback for a component without a user-visible name: prefer the label or
     * text lookups when there is one. The id must match in full; two components
     * with that id fail with a strict-mode error.
     *
     * @param page the Playwright page
     * @param id   the full {@code id}
     * @return the matching {@code RadioButtonGroupElement}
     */
    public static RadioButtonGroupElement getById(Page page, String id) {
        return new RadioButtonGroupElement(IdLocator.byId(page, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code RadioButtonGroupElement} by its {@code id}, scoped to the given locator.
     * <p>
     * A fallback for a component without a user-visible name. The id must match in
     * full; two components with that id inside the scope fail with a strict-mode
     * error.
     *
     * @param locator the locator whose subtree is searched
     * @param id      the full {@code id}
     * @return the matching {@code RadioButtonGroupElement}
     */
    public static RadioButtonGroupElement getById(Locator locator, String id) {
        return new RadioButtonGroupElement(IdLocator.byId(locator, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code RadioButtonGroupElement} by its test id, searching the entire page.
     * <p>
     * The test id is Playwright's test id attribute, {@code data-testid} unless
     * reconfigured — the attribute Flow's {@code Component.setTestId(String)} sets.
     * A fallback for a component without a user-visible name; it must match in
     * full, and two components with that test id fail with a strict-mode error.
     *
     * @param page   the Playwright page
     * @param testId the full test id
     * @return the matching {@code RadioButtonGroupElement}
     */
    public static RadioButtonGroupElement getByTestId(Page page, String testId) {
        return new RadioButtonGroupElement(IdLocator.byTestId(page, FIELD_TAG_NAME, testId));
    }

    /**
     * Get a {@code RadioButtonGroupElement} by its test id, scoped to the given locator.
     * <p>
     * See {@link #getByTestId(Page, String)}; only components inside the scope are
     * considered.
     *
     * @param locator the locator whose subtree is searched
     * @param testId  the full test id
     * @return the matching {@code RadioButtonGroupElement}
     */
    public static RadioButtonGroupElement getByTestId(Locator locator, String testId) {
        return new RadioButtonGroupElement(IdLocator.byTestId(locator, FIELD_TAG_NAME, testId));
    }
}
