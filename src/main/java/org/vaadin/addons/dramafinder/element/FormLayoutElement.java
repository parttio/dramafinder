package org.vaadin.addons.dramafinder.element;

import java.util.Map;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;

/**
 * PlaywrightElement for {@code <vaadin-form-layout>}.
 * <p>
 * The component is purely structural, so the surface is deliberately small: it
 * exposes the laid out fields and the two things a responsive form is worth
 * asserting on — how many columns the layout currently renders and where the
 * labels sit. Both are resolved for the layout's <em>current</em> width, so a
 * test can resize the viewport and assert the effect of the responsive steps.
 * <p>
 * Both layout modes are supported. With responsive steps (the default) the
 * step matching the layout's width decides the column count and the label
 * position; in auto-responsive mode the columns actually rendered by the CSS
 * grid are counted, and labels are aside only while they fit.
 */
@PlaywrightElement(FormLayoutElement.FIELD_TAG_NAME)
public class FormLayoutElement extends VaadinElement implements HasThemeElement, HasStyleElement {

    public static final String FIELD_TAG_NAME = "vaadin-form-layout";

    /** Tag name of a form item, the label + field wrapper. */
    public static final String FORM_ITEM_TAG_NAME = "vaadin-form-item";

    /** Tag name of a form row, the explicit row grouping used in auto-responsive mode. */
    public static final String FORM_ROW_TAG_NAME = "vaadin-form-row";

    /** Label position placing labels next to their field. */
    public static final String LABEL_POSITION_ASIDE = "aside";

    /** Label position placing labels above their field. */
    public static final String LABEL_POSITION_TOP = "top";

    /**
     * Resolves the column count and the label position for the layout's current
     * width, mirroring the component's own layout algorithm.
     * <p>
     * In auto-responsive mode the rendered CSS grid tracks are counted, skipping
     * the {@code 0px} auto columns the component uses as padding; labels are
     * aside exactly when the component decided they fit. Otherwise the
     * responsive steps are walked in order and the last one whose
     * {@code minWidth} still fits the layout wins — {@code minWidth} is resolved
     * to pixels by parking it in an unrelated length property, which is how the
     * component converts {@code em} thresholds itself.
     */
    private static final String LAYOUT_STATE_SCRIPT = """
            el => {
              const layout = el.shadowRoot.querySelector('#layout');
              if (el.hasAttribute('auto-responsive')) {
                return {
                  columns: getComputedStyle(layout).gridTemplateColumns
                      .split(' ').filter(track => track !== '0px' && track !== 'none').length,
                  labelsPosition: layout.hasAttribute('fits-labels-aside') ? 'aside' : 'top'
                };
              }
              const probe = 'background-position';
              let selected = null;
              (el.responsiveSteps || []).forEach(step => {
                layout.style.setProperty(probe, String(step.minWidth === undefined ? 0 : step.minWidth));
                const minWidth = parseFloat(getComputedStyle(layout).getPropertyValue(probe));
                if (!Number.isNaN(minWidth) && minWidth <= el.offsetWidth) {
                  selected = step;
                }
              });
              layout.style.removeProperty(probe);
              return {
                columns: selected ? selected.columns : 1,
                labelsPosition: selected && selected.labelsPosition === 'top' ? 'top' : 'aside'
              };
            }
            """;

    /**
     * Create a new {@code FormLayoutElement}.
     *
     * @param locator the locator for the {@code <vaadin-form-layout>} element
     */
    public FormLayoutElement(Locator locator) {
        super(locator);
    }

    /**
     * Get the first {@code FormLayoutElement} on the page.
     *
     * @param page the Playwright page
     * @return the first matching {@code FormLayoutElement}
     */
    public static FormLayoutElement get(Page page) {
        return new FormLayoutElement(page.locator(FIELD_TAG_NAME).first());
    }

    /**
     * Get the first {@code FormLayoutElement} within a parent locator.
     *
     * @param parent the parent locator to search within
     * @return the first matching {@code FormLayoutElement}
     */
    public static FormLayoutElement get(Locator parent) {
        return new FormLayoutElement(parent.locator(FIELD_TAG_NAME).first());
    }

    /**
     * Get a {@code FormLayoutElement} by its {@code id} attribute.
     *
     * @param page the Playwright page
     * @param id   the element id
     * @return the matching {@code FormLayoutElement}
     */
    public static FormLayoutElement getById(Page page, String id) {
        return new FormLayoutElement(page.locator("#" + id));
    }

    // ── Fields ─────────────────────────────────────────────────────────

    /**
     * Locator matching every field the layout positions, in DOM order.
     * <p>
     * A field is whatever the layout treats as one item of the grid: a plain
     * component, or a {@code <vaadin-form-item>} wrapping a label and a field.
     * {@code <vaadin-form-row>} groups are flattened to the fields they contain,
     * and the {@code <br>} elements used to force a row break are skipped, so
     * the count matches what the user sees rather than the raw child list.
     * <p>
     * The lookup is an XPath union because a CSS child selector would pierce the
     * shadow DOM and pick up the layout's internal wrapper as a child too.
     *
     * @return a locator resolving to all fields of this layout
     */
    public Locator getFields() {
        return getLocator().locator("xpath=./*[not(self::br)][not(self::" + FORM_ROW_TAG_NAME + ")]"
                + " | ./" + FORM_ROW_TAG_NAME + "/*[not(self::br)]");
    }

    /**
     * Get a field by its zero-based position in the layout.
     *
     * @param index the zero-based field index
     * @return a locator for that field
     */
    public Locator getField(int index) {
        return getFields().nth(index);
    }

    /**
     * Get the number of fields the layout positions.
     *
     * @return the field count, as counted by {@link #getFields()}
     */
    public int getFieldCount() {
        return getFields().count();
    }

    /**
     * Locator matching every {@code <vaadin-form-item>} of this layout, in DOM
     * order. Empty for a layout built from plain fields.
     *
     * @return a locator resolving to all form items of this layout
     */
    public Locator getFormItems() {
        return getLocator().locator(FORM_ITEM_TAG_NAME);
    }

    // ── Layout ─────────────────────────────────────────────────────────

    /**
     * Get the number of columns the layout renders at its current width.
     * <p>
     * With responsive steps this is the {@code columns} value of the step that
     * matches the layout's width; in auto-responsive mode it is the number of
     * columns the layout actually created for the available space.
     *
     * @return the current column count, at least {@code 1}
     */
    public int getColumnCount() {
        return ((Number) getLayoutState().get("columns")).intValue();
    }

    /**
     * Get where the layout places the labels of its form items at its current
     * width.
     * <p>
     * With responsive steps this is the {@code labelsPosition} of the matching
     * step; in auto-responsive mode labels are {@code aside} only when the
     * layout enabled aside labels and is wide enough for them.
     *
     * @return {@link #LABEL_POSITION_ASIDE} or {@link #LABEL_POSITION_TOP},
     *         never {@code null}
     */
    public String getLabelPosition() {
        return (String) getLayoutState().get("labelsPosition");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getLayoutState() {
        return (Map<String, Object>) locator.evaluate(LAYOUT_STATE_SCRIPT);
    }

    // ── Assertions ─────────────────────────────────────────────────────

    /**
     * Assert that the layout renders the given number of columns. Retries until
     * the layout settles, so it is safe to call right after a viewport resize.
     *
     * @param expected the expected column count
     */
    public void assertColumnCount(int expected) {
        locator.page().waitForCondition(() -> getColumnCount() == expected);
    }

    /**
     * Assert where the layout places the labels of its form items. Retries until
     * the layout settles, so it is safe to call right after a viewport resize.
     *
     * @param expected {@link #LABEL_POSITION_ASIDE} or {@link #LABEL_POSITION_TOP}
     */
    public void assertLabelPosition(String expected) {
        locator.page().waitForCondition(() -> getLabelPosition().equals(expected));
    }

    /**
     * Assert that the layout positions the given number of fields.
     *
     * @param expected the expected field count
     */
    public void assertFieldCount(int expected) {
        locator.page().waitForCondition(() -> getFieldCount() == expected);
    }

}
