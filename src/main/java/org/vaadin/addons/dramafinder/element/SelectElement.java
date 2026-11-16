package org.vaadin.addons.dramafinder.element;

import java.util.Objects;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.query.ElementQueries;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.query.SelectQuery;
import org.vaadin.addons.dramafinder.element.shared.FocusableElement;
import org.vaadin.addons.dramafinder.element.utils.IdLocator;
import org.vaadin.addons.dramafinder.element.utils.ItemLocator;
import org.vaadin.addons.dramafinder.element.shared.HasAriaLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.addons.dramafinder.element.shared.HasInputFieldElement;
import org.vaadin.addons.dramafinder.element.shared.HasPlaceholderElement;
import org.vaadin.addons.dramafinder.element.shared.HasPrefixElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;
import org.vaadin.addons.dramafinder.element.shared.HasTooltipElement;
import org.vaadin.addons.dramafinder.element.shared.HasValidationPropertiesElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.vaadin.addons.dramafinder.element.AssertionWaits.assertEventually;

/**
 * PlaywrightElement for {@code <vaadin-select>}.
 * <p>
 * Provides helpers to open the overlay and pick items by visible text,
 * along with aria/placeholder/validation mixins.
 */
@PlaywrightElement(SelectElement.FIELD_TAG_NAME)
public class SelectElement extends VaadinElement
        implements FocusableElement, HasAriaLabelElement, HasInputFieldElement,
        HasPrefixElement, HasThemeElement, HasPlaceholderElement,
        HasEnabledElement, HasTooltipElement, HasValidationPropertiesElement,
        Findable<SelectQuery<SelectElement>> {

    public static final String FIELD_TAG_NAME = "vaadin-select";
    public static final String FIELD_ITEM_TAG_NAME = "vaadin-select-item";
    public static final String FIELD_OVERLAY_TAG_NAME = "vaadin-select-list-box";

    @Override
    public Locator getInputLocator() {
        return getLocator().locator("*[slot=\"value\"]").first();
    }

    /**
     * Create a new {@code SelectElement}.
     *
     * @param locator the locator for the {@code <vaadin-select>} element
     */
    public SelectElement(Locator locator) {
        super(locator);
    }

    /**
     * Select an item by its visible label.
     *
     * @param item label of the item
     */
    public void selectItem(String item) {
        getLocator().click();
        getSelectItem(getLocator().getByRole(AriaRole.LISTBOX), item).click();
        waitAfterAction();
    }

    /**
     * Get the selected value label for single-select.
     *
     * @return the label of the value
     */
    @Override
    public String getValue() {
        return getInputLocator().innerText();
    }

    /**
     * The value button, which carries the {@code aria-label} from Vaadin 25.4 on.
     * Up to 25.3 the select renders its accessible name as a screen-reader-only
     * label ({@code slot="sr-label"}) instead; {@link #getAriaLabel()} and
     * {@link #assertAriaLabel(String)} read either.
     *
     * @return the value button locator
     */
    @Override
    public Locator getAriaLabelLocator() {
        return getInputLocator();
    }

    /**
     * Read the accessible name set with {@code setAriaLabel(..)}: the value
     * button's {@code aria-label} (Vaadin 25.4+), else the text of the
     * screen-reader-only {@code sr-label} (up to 25.3).
     *
     * @return the ARIA label, or {@code null} when none is set
     */
    @Override
    public String getAriaLabel() {
        String ariaLabel = getAriaLabelLocator().getAttribute("aria-label");
        if (ariaLabel != null) {
            return ariaLabel;
        }
        Locator srLabel = getLocator().locator(":scope > label[slot=\"sr-label\"]");
        if (srLabel.count() == 0) {
            return null;
        }
        String text = srLabel.first().textContent();
        return text == null || text.isEmpty() ? null : text;
    }

    /**
     * Assert the accessible name set with {@code setAriaLabel(..)}, read as
     * {@link #getAriaLabel()} does, retrying until it matches.
     *
     * @param ariaLabel expected label, or {@code null} to assert none is set
     *                  (e.g. with {@code setAriaLabelledBy(..)})
     */
    @Override
    public void assertAriaLabel(String ariaLabel) {
        assertEventually(getLocator().page(), "Select aria-label", this::getAriaLabel, ariaLabel);
    }

    @Override
    public Locator getFocusLocator() {
        return getInputLocator();
    }

    @Override
    public Locator getEnabledLocator() {
        return getInputLocator();
    }

    /**
     * Assert the placeholder shown by the value button.
     * <p>
     * {@code vaadin-select} does not reflect {@code placeholder} on its host: the
     * value button shows it while nothing is selected. A non-null placeholder
     * asserts that the button currently shows it; {@code null} asserts that the
     * select has no placeholder at all (its {@code placeholder} property is unset
     * or empty), selected item or not.
     *
     * @param placeholder the expected placeholder, or {@code null} for none
     */
    @Override
    public void assertPlaceholder(String placeholder) {
        if (placeholder != null) {
            assertThat(getInputLocator()).hasAttribute("placeholder", "");
            assertThat(getInputLocator()).hasText(placeholder);
        } else {
            assertEventually(getLocator().page(), "Select placeholder", this::getPlaceholder,
                    actual -> actual == null || actual.isEmpty(), "none");
        }
    }

    /**
     * Assert the selected value label equals the expected string.
     *
     * @param expected expected label or empty for no selection
     */
    @Override
    public void assertValue(String expected) {
        assertThat(getInputLocator()).hasText(Objects.requireNonNullElse(expected, ""));
    }

    /**
     * Locator for an overlay item, matched on its full label. {@code "Option 1"}
     * does not resolve to {@code "Option 10"}, and two items sharing the label
     * fail with a Playwright strict-mode error.
     */
    private static Locator getSelectItem(Locator locator, String label) {
        return ItemLocator.byExactText(locator, FIELD_ITEM_TAG_NAME, label);
    }

    /**
     * Get the {@code SelectElement} whose visible label is exactly {@code label}, searching the entire page.
     * <p>
     * The label is the text of the component's own {@code label} slot, matched in
     * full and case-sensitively. An {@code aria-label} or a placeholder is not a
     * label: use {@code find(page, SelectElement.class).withAccessibleName(..)} or
     * {@code .withPlaceholder(..)}. No {@code .first()} is applied: two components
     * with that label fail with a strict-mode error. Same as
     * {@code find(page, SelectElement.class).withLabel(label)}; use
     * {@link #getByLabelContaining(Page, String)} to match part of the label.
     *
     * @param page  the Playwright page
     * @param label the full visible label
     * @return the matching {@code SelectElement}
     */
    public static SelectElement getByLabel(Page page, String label) {
        return new SelectElement(ElementQueries.find(page, SelectElement.class).withLabel(label).getLocator());
    }

    /**
     * Get the {@code SelectElement} whose visible label contains {@code text}, searching the entire page.
     * <p>
     * The label is matched case-insensitively as a substring; an {@code aria-label}
     * or a placeholder is not a label. No {@code .first()} is applied: several
     * matching components fail with a strict-mode error. Same as
     * {@code find(page, SelectElement.class).withLabelContaining(text)}.
     *
     * @param page  the Playwright page
     * @param text  part of the visible label
     * @return the matching {@code SelectElement}
     */
    public static SelectElement getByLabelContaining(Page page, String text) {
        return new SelectElement(ElementQueries.find(page, SelectElement.class).withLabelContaining(text).getLocator());
    }

    /**
     * Get the {@code SelectElement} whose visible label is exactly {@code label}, scoped to the given locator.
     * <p>
     * The label is the text of the component's own {@code label} slot, matched in
     * full and case-sensitively. An {@code aria-label} or a placeholder is not a
     * label: use {@code find(locator, SelectElement.class).withAccessibleName(..)} or
     * {@code .withPlaceholder(..)}. No {@code .first()} is applied: two components
     * with that label fail with a strict-mode error. Same as
     * {@code find(locator, SelectElement.class).withLabel(label)}; use
     * {@link #getByLabelContaining(Locator, String)} to match part of the label.
     *
     * @param locator the locator whose subtree is searched
     * @param label   the full visible label
     * @return the matching {@code SelectElement}
     */
    public static SelectElement getByLabel(Locator locator, String label) {
        return new SelectElement(ElementQueries.find(locator, SelectElement.class).withLabel(label).getLocator());
    }

    /**
     * Get the {@code SelectElement} whose visible label contains {@code text}, scoped to the given locator.
     * <p>
     * The label is matched case-insensitively as a substring; an {@code aria-label}
     * or a placeholder is not a label. No {@code .first()} is applied: several
     * matching components fail with a strict-mode error. Same as
     * {@code find(locator, SelectElement.class).withLabelContaining(text)}.
     *
     * @param locator the locator whose subtree is searched
     * @param text    part of the visible label
     * @return the matching {@code SelectElement}
     */
    public static SelectElement getByLabelContaining(Locator locator, String text) {
        return new SelectElement(ElementQueries.find(locator, SelectElement.class).withLabelContaining(text).getLocator());
    }


    // ── Lookup by id or test id ────────────────────────────────────────

    /**
     * Get a {@code SelectElement} by its {@code id}, searching the entire page.
     * <p>
     * A fallback for a component without a user-visible name: prefer the label or
     * text lookups when there is one. The id must match in full; two components
     * with that id fail with a strict-mode error.
     *
     * @param page the Playwright page
     * @param id   the full {@code id}
     * @return the matching {@code SelectElement}
     */
    public static SelectElement getById(Page page, String id) {
        return new SelectElement(IdLocator.byId(page, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code SelectElement} by its {@code id}, scoped to the given locator.
     * <p>
     * A fallback for a component without a user-visible name. The id must match in
     * full; two components with that id inside the scope fail with a strict-mode
     * error.
     *
     * @param locator the locator whose subtree is searched
     * @param id      the full {@code id}
     * @return the matching {@code SelectElement}
     */
    public static SelectElement getById(Locator locator, String id) {
        return new SelectElement(IdLocator.byId(locator, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code SelectElement} by its test id, searching the entire page.
     * <p>
     * The test id is Playwright's test id attribute, {@code data-testid} unless
     * reconfigured — the attribute Flow's {@code Component.setTestId(String)} sets.
     * A fallback for a component without a user-visible name; it must match in
     * full, and two components with that test id fail with a strict-mode error.
     *
     * @param page   the Playwright page
     * @param testId the full test id
     * @return the matching {@code SelectElement}
     */
    public static SelectElement getByTestId(Page page, String testId) {
        return new SelectElement(IdLocator.byTestId(page, FIELD_TAG_NAME, testId));
    }

    /**
     * Get a {@code SelectElement} by its test id, scoped to the given locator.
     * <p>
     * See {@link #getByTestId(Page, String)}; only components inside the scope are
     * considered.
     *
     * @param locator the locator whose subtree is searched
     * @param testId  the full test id
     * @return the matching {@code SelectElement}
     */
    public static SelectElement getByTestId(Locator locator, String testId) {
        return new SelectElement(IdLocator.byTestId(locator, FIELD_TAG_NAME, testId));
    }
}
