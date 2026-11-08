package org.vaadin.addons.dramafinder.element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.TimeoutError;

/**
 * Reads the item labels a combo box offers, for {@link ComboBoxElement} and
 * {@link MultiSelectComboBoxElement}.
 * <p>
 * A Flow combo box loads its items a page at a time, and the web component keeps a
 * placeholder for every item it has not loaded yet. The labels are read from the
 * component's {@code filteredItems}; while a placeholder remains, the overlay's
 * scroller is moved to it so the component requests that page, and the read waits
 * until the page has arrived.
 */
final class ComboBoxItemLabels {

    // The labels of filteredItems; null for a placeholder of an item not loaded yet, which has
    // no label. vaadin-combo-box and vaadin-multi-select-combo-box both hold the items themselves.
    private static final String STATE_JS = """
            el => {
                const path = el.itemLabelPath || 'label';
                return {
                    loading: !!el.loading,
                    labels: (el.filteredItems || []).map(i => i == null ? null
                        : typeof i === 'string' ? i
                        : (i[path] === undefined || i[path] === null ? null : String(i[path])))
                };
            }""";

    // Scroll the overlay to an item, which makes the component request the page holding it
    private static final String SCROLL_JS = "(el, index) => el._scroller.scrollIntoView(index)";

    private ComboBoxItemLabels() {
    }

    /**
     * Read every item label of the combo box at {@code locator}, loading the pages not loaded yet.
     * The overlay must be open.
     *
     * @param locator the combo box host
     * @return the labels, in overlay order
     * @throws IllegalStateException if a page does not load before the timeout
     */
    static List<String> readAll(Locator locator) {
        var page = locator.page();
        page.waitForCondition(() -> !loading(locator));
        // Labels are kept as they are seen: the component may drop pages it scrolled away from
        var seen = new ArrayList<>(labels(locator));
        for (int missing = seen.indexOf(null); missing >= 0; missing = seen.indexOf(null)) {
            int index = missing;
            locator.evaluate(SCROLL_JS, index);
            try {
                page.waitForCondition(() -> {
                    var current = labels(locator);
                    return !loading(locator) && index < current.size() && current.get(index) != null;
                });
            } catch (TimeoutError e) {
                throw new IllegalStateException("Combo box item " + index + " did not load", e);
            }
            var current = labels(locator);
            if (current.size() != seen.size()) {
                throw new IllegalStateException("The combo box items changed while they were read: "
                        + seen.size() + " items, then " + current.size());
            }
            for (int i = 0; i < current.size(); i++) {
                if (current.get(i) != null) {
                    seen.set(i, current.get(i));
                }
            }
        }
        return seen;
    }

    private static boolean loading(Locator locator) {
        return Boolean.TRUE.equals(state(locator).get("loading"));
    }

    private static List<String> labels(Locator locator) {
        var labels = new ArrayList<String>();
        for (Object label : (List<?>) state(locator).get("labels")) {
            labels.add(label == null ? null : label.toString());
        }
        return labels;
    }

    private static Map<?, ?> state(Locator locator) {
        return (Map<?, ?>) locator.evaluate(STATE_JS);
    }
}
