package org.vaadin.addons.dramafinder.agent;

import java.util.List;

import com.microsoft.playwright.Page;

/**
 * Produces a compact, human- and agent-readable summary of the Vaadin
 * components currently present on a page.
 * <p>
 * Instead of dumping a full accessibility tree (which is large and noisy for
 * Vaadin's shadow DOM and teleported overlays), this walks the light DOM for
 * {@code vaadin-*} elements and emits one terse line per meaningful component,
 * e.g.:
 * <pre>
 * vaadin-grid — 12 rows, columns [Date, Amount, Status], 1 selected
 * vaadin-combo-box "Category" = "Travel"
 * notification: "Saved"
 * vaadin-dialog-overlay open — "Confirm"
 * </pre>
 * The result is orders of magnitude smaller than an accessibility-tree dump and
 * captures exactly the state an agent needs to reason about the UI. It is also
 * embedded in failure reports written by {@link AgentReporting}.
 */
public final class ComponentSnapshot {

    private ComponentSnapshot() {
    }

    // Walks the DOM in the browser and returns one string per component.
    // @formatter:off
    private static final String SNAPSHOT_SCRIPT = """
        () => {
          const out = [];
          // Structural / internal tags we never report on their own.
          const SKIP = new Set([
            'vaadin-grid-cell-content','vaadin-grid-column','vaadin-grid-column-group',
            'vaadin-grid-sorter','vaadin-grid-tree-toggle','vaadin-grid-flow-selection-column',
            'vaadin-grid-selection-column','vaadin-combo-box-item','vaadin-item',
            'vaadin-select-item','vaadin-list-box','vaadin-menu-bar-item','vaadin-menu-bar-button',
            'vaadin-context-menu-item','vaadin-tab','vaadin-notification-container',
            'vaadin-dev-tools','vaadin-connection-indicator','vaadin-overlay','vaadin-scroller',
            'vaadin-horizontal-layout','vaadin-vertical-layout','vaadin-form-layout',
            'vaadin-form-item','vaadin-app-layout','vaadin-split-layout','vaadin-icon',
            'vaadin-avatar-group'
          ]);
          // Overlays whose descendants are just list items, not real state.
          const ITEM_OVERLAYS = 'vaadin-combo-box-overlay,vaadin-multi-select-combo-box-overlay,'
            + 'vaadin-select-overlay,vaadin-menu-bar-overlay,vaadin-context-menu-overlay';

          const quote = (v) => (v === undefined || v === null || v === '') ? '' : ' = "' + v + '"';
          const labelOf = (el) => (el.label || el.getAttribute('aria-label') || '').trim();

          const els = Array.from(document.querySelectorAll('*'))
            .filter(e => e.tagName.toLowerCase().startsWith('vaadin-'));

          for (const el of els) {
            const tag = el.tagName.toLowerCase();
            if (SKIP.has(tag)) continue;
            // Skip items nested inside selection overlays.
            if (el.closest(ITEM_OVERLAYS) && !el.matches(ITEM_OVERLAYS)) continue;
            // Skip anything living inside a grid (cells, editors) except the grid itself.
            const grid = el.closest('vaadin-grid,vaadin-tree-grid');
            if (grid && grid !== el) continue;

            const label = labelOf(el);
            const named = label ? ' "' + label + '"' : '';

            if (tag === 'vaadin-grid' || tag === 'vaadin-tree-grid') {
              const rows = el._flatSize ?? el._effectiveSize ?? el.size ?? 0;
              const headers = [];
              const sr = el.shadowRoot;
              if (sr) {
                sr.querySelectorAll('thead th').forEach(th => {
                  const slot = th.querySelector('slot');
                  if (!slot || !slot.assignedNodes) return;
                  const txt = slot.assignedNodes().map(n => n.textContent || '').join('').trim();
                  if (txt) headers.push(txt);
                });
              }
              const selected = (el.selectedItems || []).length;
              out.push(tag + named + ' — ' + rows + ' rows, columns ['
                + headers.join(', ') + ']' + (selected ? ', ' + selected + ' selected' : ''));
              continue;
            }

            if (tag === 'vaadin-virtual-list') {
              const items = (el.items || []).length;
              out.push(tag + named + ' — ' + items + ' items');
              continue;
            }

            if (tag === 'vaadin-notification-card') {
              const txt = (el.textContent || '').replace(/\\s+/g, ' ').trim();
              if (txt) out.push('notification: "' + txt + '"');
              continue;
            }

            if (tag.endsWith('-overlay')) {
              const opened = el.opened || el.hasAttribute('opened');
              if (!opened) continue;
              const header = (el.headerTitle || '').trim()
                || (el.querySelector('[slot="title"]')?.textContent || '').trim();
              out.push(tag + ' open' + (header ? ' — "' + header + '"' : ''));
              continue;
            }

            const flags = [];
            if (el.disabled) flags.push('disabled');
            if (el.readonly) flags.push('read-only');
            if (el.required) flags.push('required');
            if (el.invalid) flags.push('invalid');
            const flagStr = flags.length ? ' (' + flags.join(', ') + ')' : '';

            if (tag === 'vaadin-checkbox') {
              out.push(tag + named + ' = ' + (el.checked ? 'checked' : 'unchecked') + flagStr);
              continue;
            }
            if (tag === 'vaadin-checkbox-group' || tag === 'vaadin-radio-group') {
              const val = Array.isArray(el.value) ? el.value.join(', ') : (el.value || '');
              out.push(tag + named + quote(val) + flagStr);
              continue;
            }

            if (tag === 'vaadin-button') {
              const txt = (el.textContent || '').replace(/\\s+/g, ' ').trim();
              if (txt) out.push('vaadin-button "' + txt + '"' + flagStr);
              continue;
            }

            // Generic field-like components expose a value / label.
            if ('value' in el || label) {
              let val = el.value;
              if (Array.isArray(val)) val = val.join(', ');
              out.push(tag + named + quote(val) + flagStr);
            }
          }
          return out;
        }
        """;
    // @formatter:on

    /**
     * Capture a compact snapshot of all meaningful Vaadin components on the
     * page.
     *
     * @param page the Playwright page to inspect
     * @return a multi-line summary, one component per line (empty string when
     *         no components are found)
     */
    @SuppressWarnings("unchecked")
    public static String capture(Page page) {
        Object result = page.evaluate(SNAPSHOT_SCRIPT);
        if (!(result instanceof List<?> list) || list.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Object line : list) {
            if (line != null) {
                sb.append(line).append(System.lineSeparator());
            }
        }
        return sb.toString().stripTrailing();
    }
}
