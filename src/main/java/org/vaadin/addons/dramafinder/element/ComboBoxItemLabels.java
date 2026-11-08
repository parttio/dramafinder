package org.vaadin.addons.dramafinder.element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

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
 * until the page has arrived. Labels are kept as they are seen, because the component
 * drops pages it has scrolled away from.
 * <p>
 * Depends on the web component's private {@code _scroller} (the overlay's
 * {@code vaadin-combo-box-scroller}), as {@link GridElement} depends on the grid's
 * private scrolling API: both {@code vaadin-combo-box} and
 * {@code vaadin-multi-select-combo-box} hold the items and the scroller themselves.
 */
final class ComboBoxItemLabels {

    // One read of the component: whether it is loading, and the label of each item in
    // filteredItems. A placeholder for an item not loaded yet is an object with no own field
    // and reads as null; a loaded item without a label reads as "".
    private static final String STATE_JS = """
            el => {
                const path = el.itemLabelPath || 'label';
                const label = i => {
                    if (i === undefined || i === null) return null;
                    if (typeof i !== 'object') return String(i);
                    if (Object.keys(i).length === 0) return null;
                    const l = i[path];
                    return l === undefined || l === null ? '' : String(l);
                };
                return { loading: !!el.loading, labels: (el.filteredItems || []).map(label) };
            }""";

    // Scroll the overlay to an item, which makes the component request the page holding it
    private static final String SCROLL_JS = "(el, index) => el._scroller.scrollIntoView(index)";

    private record State(boolean loading, List<String> labels) {
    }

    private ComboBoxItemLabels() {
    }

    /**
     * Read every item label of a combo box, opening its overlay for the read if it is closed and
     * closing it again afterwards.
     *
     * @param locator  the combo box host
     * @param isOpened whether the overlay is open
     * @param open     opens the overlay
     * @param close    closes the overlay
     * @return the labels, in overlay order
     * @throws IllegalStateException if the items do not load before the timeout, or change while
     *                               they are read
     */
    static List<String> read(Locator locator, BooleanSupplier isOpened, Runnable open, Runnable close) {
        boolean wasOpened = isOpened.getAsBoolean();
        if (!wasOpened) {
            open.run();
        }
        try {
            return readAll(locator);
        } finally {
            if (!wasOpened) {
                close.run();
            }
        }
    }

    private static List<String> readAll(Locator locator) {
        var seen = new ArrayList<>(awaitState(locator, -1, "the combo box items did not load").labels());
        for (int missing = seen.indexOf(null); missing >= 0; missing = seen.indexOf(null)) {
            locator.evaluate(SCROLL_JS, missing);
            var current = awaitState(locator, missing, "combo box item " + missing + " did not load").labels();
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

    // Wait until the component is not loading and, for index >= 0, the item at index has loaded
    private static State awaitState(Locator locator, int index, String failure) {
        var last = new State[1];
        try {
            locator.page().waitForCondition(() -> {
                var state = state(locator);
                last[0] = state;
                return !state.loading()
                        && (index < 0 || (index < state.labels().size() && state.labels().get(index) != null));
            });
        } catch (TimeoutError e) {
            throw new IllegalStateException(failure + " before the timeout", e);
        }
        return last[0];
    }

    private static State state(Locator locator) {
        var result = (Map<?, ?>) locator.evaluate(STATE_JS);
        var labels = new ArrayList<String>();
        for (Object label : (List<?>) result.get("labels")) {
            labels.add(label == null ? null : label.toString());
        }
        return new State(Boolean.TRUE.equals(result.get("loading")), labels);
    }
}
