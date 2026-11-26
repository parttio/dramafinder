package org.vaadin.addons.dramafinder.element.query;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Filter by accessible name, available on every query.
 * <p>
 * By default the name is the one given by {@code aria-label},
 * {@code aria-labelledby} or an associated {@code <label>}, read on the
 * component itself or on its own input (the {@code input}, {@code textarea} or
 * {@code value} slotted directly into a field), or on the internal table of a
 * {@code vaadin-grid}, where the grid forwards its {@code accessibleName}.
 * Components nested inside it are not considered, so a dialog is not matched
 * by the name of a field it holds.
 * Queries for components named by their content (buttons, tabs) refine this.
 *
 * @param <Q> the query type returned by the filter
 */
public interface AccessibleNameFilters<Q> extends Refinable<Q> {

    /**
     * Keep components whose accessible name is exactly {@code name}
     * (case-sensitive, whitespace-normalized).
     * <p>
     * Unlike {@code withLabel} this also finds a component named only by
     * {@code aria-label}. A field's placeholder is not its name. From Vaadin
     * 25.4 this holds for {@code vaadin-select} too: a select with no label,
     * {@code accessibleName} or {@code accessibleNameRef} has no name. On 25.3
     * the select labels its value button with its placeholder or selected
     * item, so that text is part of its name there.
     *
     * @param name the full accessible name
     * @return a new, narrowed query
     */
    default Q withAccessibleName(String name) {
        return refine(QueryText.quote("with accessible name", name), candidates -> named(candidates, name, true));
    }

    /**
     * Keep components whose accessible name — as read by
     * {@link #withAccessibleName(String)} — contains {@code text}, matched
     * case-insensitively as a substring.
     * <p>
     * The closest equivalent of the 1.x {@code getByLabel}, which matched the
     * label or {@code aria-label} this way; unlike it, several matches are not
     * reduced to the first one.
     *
     * @param text part of the accessible name
     * @return a new, narrowed query
     */
    default Q withAccessibleNameContaining(String text) {
        return refine(QueryText.quote("with accessible name containing", text),
                candidates -> named(candidates, text, false));
    }

    private static Locator named(Locator candidates, String name, boolean exact) {
        // One pass of Playwright's label engine: every labelled element, plus the component
        // that a labelled input, textarea or value is slotted directly into. Matching the
        // own input with `has` instead ran the engine again over each candidate's subtree.
        Page page = candidates.page();
        Locator named = page.getByLabel(name, new Page.GetByLabelOptions().setExact(exact));
        Locator labelled = candidates.and(named.locator(QueryText.SELF_OR_OWNING_FIELD));
        // A grid's name is on the table in its shadow root, which the XPath above cannot leave.
        Locator namedGrid = candidates.and(page.locator(QueryText.GRID_TAGS)).filter(new Locator.FilterOptions()
                .setHas(page.locator(QueryText.OWN_GRID_TABLE).and(named)));
        return labelled.or(namedGrid);
    }
}
