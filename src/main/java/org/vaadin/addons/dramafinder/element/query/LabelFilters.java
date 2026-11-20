package org.vaadin.addons.dramafinder.element.query;

import com.microsoft.playwright.Locator;

/**
 * Filters for components that render a visible label in their
 * {@code slot="label"} child, such as {@code vaadin-text-field} or
 * {@code vaadin-checkbox}.
 * <p>
 * A component with no label of its own placed in a {@code vaadin-form-item}
 * ({@code formLayout.addFormItem(field, "Name")}) is labelled by that form
 * item's label, the text shown next to it.
 *
 * @param <Q> the query type returned by each filter
 */
public interface LabelFilters<Q> extends Refinable<Q> {

    /**
     * Keep components whose visible label is exactly {@code label}.
     * <p>
     * The match is case-sensitive and ignores surrounding whitespace; an
     * {@code aria-label} or a placeholder does not count as a label (see
     * {@code withAccessibleName} and {@code withPlaceholder}). A component
     * without a label of its own matches by the label of the
     * {@code vaadin-form-item} it sits in.
     *
     * @param label the full label text
     * @return a new, narrowed query
     */
    default Q withLabel(String label) {
        return refine(QueryText.quote("with label", label),
                candidates -> QueryText.labelled(candidates,
                        new Locator.FilterOptions().setHasText(QueryText.exact(label))));
    }

    /**
     * Keep components whose visible label contains {@code text}, matched
     * case-insensitively as a substring; like {@link #withLabel(String)}, a
     * component without a label of its own is read by its form item's label.
     *
     * @param text part of the label text
     * @return a new, narrowed query
     */
    default Q withLabelContaining(String text) {
        return refine(QueryText.quote("with label containing", text),
                candidates -> QueryText.labelled(candidates,
                        new Locator.FilterOptions().setHasText(text)));
    }
}
