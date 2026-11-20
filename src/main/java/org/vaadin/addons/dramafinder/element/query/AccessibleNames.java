package org.vaadin.addons.dramafinder.element.query;

import com.microsoft.playwright.Locator;

/**
 * Reads a component's accessible name the way the
 * {@link AccessibleNameFilters#withAccessibleName(String) withAccessibleName}
 * filters match it; behind {@code VaadinElement.getAccessibleName()}.
 * <p>
 * Playwright has no call that returns the names its {@code getByLabel} engine
 * matches, so this script follows that engine's rules: the texts of the
 * {@code aria-labelledby} targets, else a non-blank {@code aria-label}, else
 * the native {@code <label>}s of a labelable element. A label's text includes
 * its shadow DOM, as Playwright reads it. {@code AccessibleNameIT} looks up each
 * name it reads with {@code withAccessibleName} to keep the two in step.
 */
public final class AccessibleNames {

    // Playwright's getElementLabels: every label text of one element, whitespace-normalized.
    private static final String LABELS_JS = """
            const text = node => {
                let full = '';
                for (let child = node.firstChild; child; child = child.nextSibling) {
                    if (child.nodeType === Node.TEXT_NODE) {
                        full += child.nodeValue || '';
                    } else if (child.nodeType === Node.ELEMENT_NODE
                            && !['SCRIPT', 'NOSCRIPT', 'STYLE'].includes(child.nodeName)) {
                        full += child instanceof HTMLInputElement
                                && ['submit', 'button', 'reset'].includes(child.type) ? child.value : text(child);
                    }
                }
                if (node.shadowRoot) {
                    full += text(node.shadowRoot);
                }
                return full;
            };
            const labels = el => {
                const ref = el.getAttribute('aria-labelledby');
                if (ref !== null) {
                    const root = el.getRootNode();
                    const targets = [];
                    for (const id of ref.split(' ').filter(id => !!id)) {
                        const target = root.querySelector('#' + CSS.escape(id));
                        if (target && !targets.includes(target)) {
                            targets.push(target);
                        }
                    }
                    if (targets.length) {
                        return targets.map(text);
                    }
                }
                const ariaLabel = el.getAttribute('aria-label');
                if (ariaLabel !== null && ariaLabel.trim()) {
                    return [ariaLabel];
                }
                const labelable = ['BUTTON', 'METER', 'OUTPUT', 'PROGRESS', 'SELECT', 'TEXTAREA'].includes(el.nodeName)
                        || (el.nodeName === 'INPUT' && el.type !== 'hidden');
                return labelable && el.labels ? [...el.labels].map(text) : [];
            };
            const join = texts => texts.map(t => t.replace(/\\s+/g, ' ').trim()).filter(t => t).join(' ') || null;
            """;

    // The host, then the input, textarea or value button slotted directly into it.
    private static final String LABELLED_JS = "el => {" + LABELS_JS + """
                const own = [...el.children].find(c => ['input', 'textarea', 'value'].includes(c.getAttribute('slot')));
                return join(labels(el)) ?? (own ? join(labels(own)) : null);
            }""";

    // QueryText.labelledOrOwnText: the host's labels when it has aria-label or aria-labelledby,
    // otherwise its own light-DOM text without named-slot children (QueryText.ownTextIs).
    private static final String LABELLED_OR_OWN_TEXT_JS = "el => {" + LABELS_JS + """
                if (el.hasAttribute('aria-label') || el.hasAttribute('aria-labelledby')) {
                    return join(labels(el));
                }
                const children = [...el.childNodes];
                const hasText = node => !!(node.textContent || '').trim();
                if (!children.some(c => c.nodeType === Node.ELEMENT_NODE && c.hasAttribute('slot') && hasText(c))) {
                    return join([el.textContent || '']);
                }
                return join(children
                        .filter(c => c.nodeType === Node.TEXT_NODE
                                || (c.nodeType === Node.ELEMENT_NODE && !c.hasAttribute('slot')))
                        .map(c => c.textContent || ''));
            }""";

    private AccessibleNames() {
    }

    /**
     * Read the accessible name of a component named by its label: the name
     * given by {@code aria-labelledby}, {@code aria-label} or a native
     * {@code <label>} on the host, or else on its own input (the
     * {@code input}, {@code textarea} or {@code value} slotted directly into
     * it). Nested components are not read.
     * <p>
     * With several {@code aria-labelledby} targets the texts are joined with a
     * space, as a screen reader announces them; {@code withAccessibleName}
     * matches each target's text on its own.
     *
     * @param component the component's locator
     * @return the whitespace-normalized name, or {@code null} when it has none
     */
    public static String labelled(Locator component) {
        return (String) component.evaluate(LABELLED_JS);
    }

    /**
     * Read the accessible name of a component named by its content, such as a
     * button or a tab: the name given by {@code aria-labelledby} or
     * {@code aria-label} on the host when either attribute is set, otherwise
     * the component's own text — its light-DOM text without the children
     * assigned to a named slot, so neither an icon nor a slotted tooltip is
     * part of it.
     * <p>
     * When a slotted child holds text, the own text chunks (text nodes and
     * unslotted children) are joined with a space.
     *
     * @param component the component's locator
     * @return the whitespace-normalized name, or {@code null} when it has none
     */
    public static String labelledOrOwnText(Locator component) {
        return (String) component.evaluate(LABELLED_OR_OWN_TEXT_JS);
    }
}
