package org.vaadin.addons.dramafinder.agent;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.AccordionElement;
import org.vaadin.addons.dramafinder.element.AccordionPanelElement;
import org.vaadin.addons.dramafinder.element.AppLayoutElement;
import org.vaadin.addons.dramafinder.element.AvatarElement;
import org.vaadin.addons.dramafinder.element.AvatarGroupElement;
import org.vaadin.addons.dramafinder.element.BadgeElement;
import org.vaadin.addons.dramafinder.element.BigDecimalFieldElement;
import org.vaadin.addons.dramafinder.element.BreadcrumbsElement;
import org.vaadin.addons.dramafinder.element.BreadcrumbsItemElement;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.addons.dramafinder.element.CardElement;
import org.vaadin.addons.dramafinder.element.CheckboxElement;
import org.vaadin.addons.dramafinder.element.CheckboxGroupElement;
import org.vaadin.addons.dramafinder.element.ComboBoxElement;
import org.vaadin.addons.dramafinder.element.ConfirmDialogElement;
import org.vaadin.addons.dramafinder.element.ContextMenuElement;
import org.vaadin.addons.dramafinder.element.CustomFieldElement;
import org.vaadin.addons.dramafinder.element.DatePickerElement;
import org.vaadin.addons.dramafinder.element.DateTimePickerElement;
import org.vaadin.addons.dramafinder.element.DetailsElement;
import org.vaadin.addons.dramafinder.element.DialogElement;
import org.vaadin.addons.dramafinder.element.EmailFieldElement;
import org.vaadin.addons.dramafinder.element.FormLayoutElement;
import org.vaadin.addons.dramafinder.element.GridElement;
import org.vaadin.addons.dramafinder.element.IntegerFieldElement;
import org.vaadin.addons.dramafinder.element.ListBoxElement;
import org.vaadin.addons.dramafinder.element.LoginFormElement;
import org.vaadin.addons.dramafinder.element.LoginOverlayElement;
import org.vaadin.addons.dramafinder.element.MarkdownElement;
import org.vaadin.addons.dramafinder.element.MenuBarElement;
import org.vaadin.addons.dramafinder.element.MenuElement;
import org.vaadin.addons.dramafinder.element.MenuItemElement;
import org.vaadin.addons.dramafinder.element.MessageInputElement;
import org.vaadin.addons.dramafinder.element.MessageListElement;
import org.vaadin.addons.dramafinder.element.MultiSelectComboBoxElement;
import org.vaadin.addons.dramafinder.element.NotificationElement;
import org.vaadin.addons.dramafinder.element.NumberFieldElement;
import org.vaadin.addons.dramafinder.element.PasswordFieldElement;
import org.vaadin.addons.dramafinder.element.PopoverElement;
import org.vaadin.addons.dramafinder.element.ProgressBarElement;
import org.vaadin.addons.dramafinder.element.RadioButtonGroupElement;
import org.vaadin.addons.dramafinder.element.SelectElement;
import org.vaadin.addons.dramafinder.element.SideNavigationElement;
import org.vaadin.addons.dramafinder.element.SideNavigationItemElement;
import org.vaadin.addons.dramafinder.element.SliderElement;
import org.vaadin.addons.dramafinder.element.SplitLayoutElement;
import org.vaadin.addons.dramafinder.element.SwitchElement;
import org.vaadin.addons.dramafinder.element.TabElement;
import org.vaadin.addons.dramafinder.element.TabSheetElement;
import org.vaadin.addons.dramafinder.element.TabsElement;
import org.vaadin.addons.dramafinder.element.TextAreaElement;
import org.vaadin.addons.dramafinder.element.TextFieldElement;
import org.vaadin.addons.dramafinder.element.TimePickerElement;
import org.vaadin.addons.dramafinder.element.TreeGridElement;
import org.vaadin.addons.dramafinder.element.UploadElement;
import org.vaadin.addons.dramafinder.element.VaadinElement;
import org.vaadin.addons.dramafinder.element.VirtualListElement;

/**
 * Lists the Vaadin components of the page that is open in a {@link Page}, each
 * with the DramaFinder lookup a test should use to reach it.
 * <p>
 * Meant to be read by an agent <em>before</em> it writes a test: instead of
 * guessing labels from a view's Java source, it opens the view in a throwaway
 * test, calls {@link #write(Page)}, and reads {@code view-outline.txt}:
 * <pre>
 * vaadin-text-field "Name" = "ada" (required)
 *     TextFieldElement.getByLabel(page, "Name")
 * vaadin-grid #people — 100 rows, columns [First Name, Last Name]
 *     GridElement.getById(page, "people")
 * vaadin-tabs
 *     TabsElement.get(page)
 *     items (full label, case-sensitive): "Details", "Payment"
 * </pre>
 * Every suggested lookup is <strong>verified</strong>: the outline calls the
 * factory method on the live page and keeps it only when it resolves to exactly
 * that component. A component no factory can single out (two fields with the
 * same label, a second grid without an id) gets a constructor over a positional
 * locator instead, which is a hint to scope the lookup to a container or give the
 * component an id. Items of a container (tabs, side-nav items, checkboxes of a
 * group, …) are listed under the container, because a test reaches them through
 * the container's API by their full label.
 * <p>
 * Components DramaFinder has no wrapper for are summarised on the last line. The
 * outline is also part of the failure bundle {@link AgentReport} writes.
 */
public final class ViewOutline {

    /** The file {@link #write(Page)} writes into the report directory. */
    public static final String FILE_NAME = "view-outline.txt";

    // Items listed per container before the rest is summarised as a count.
    private static final int MAX_ITEMS = 15;

    // Attribute stamped on each listed component while the lookups are verified.
    private static final String MARKER = "data-dramafinder-outline";

    // Every wrapper the outline can suggest. When two share a tag the first one
    // listed is the default: GridElement for vaadin-grid, with TreeGridElement
    // picked instead when the grid renders tree toggles.
    private static final List<Class<? extends VaadinElement>> ELEMENTS = List.of(
            AccordionElement.class, AccordionPanelElement.class, AppLayoutElement.class,
            AvatarElement.class, AvatarGroupElement.class, BadgeElement.class,
            BigDecimalFieldElement.class, BreadcrumbsElement.class, BreadcrumbsItemElement.class,
            ButtonElement.class, CardElement.class, CheckboxElement.class,
            CheckboxGroupElement.class, ComboBoxElement.class, ConfirmDialogElement.class,
            ContextMenuElement.class, CustomFieldElement.class,
            DatePickerElement.class, DateTimePickerElement.class, DetailsElement.class,
            DialogElement.class, EmailFieldElement.class, FormLayoutElement.class,
            GridElement.class, IntegerFieldElement.class, ListBoxElement.class,
            LoginFormElement.class, LoginOverlayElement.class, MarkdownElement.class,
            MenuBarElement.class, MenuElement.class, MenuItemElement.class,
            MessageInputElement.class, MessageListElement.class,
            MultiSelectComboBoxElement.class, NotificationElement.class,
            NumberFieldElement.class, PasswordFieldElement.class, PopoverElement.class,
            ProgressBarElement.class, RadioButtonGroupElement.class,
            SelectElement.class, SideNavigationElement.class, SideNavigationItemElement.class,
            SliderElement.class, SplitLayoutElement.class, SwitchElement.class,
            TabElement.class, TabSheetElement.class, TabsElement.class,
            TextAreaElement.class, TextFieldElement.class, TimePickerElement.class, TreeGridElement.class,
            UploadElement.class, VirtualListElement.class);

    // Factory methods to try, most user-facing first, each with the keys of the
    // component data its String argument may come from.
    private static final Map<String, List<String>> FACTORIES = new LinkedHashMap<>();

    static {
        FACTORIES.put("getByLabel", List.of("label", "ariaLabel", "placeholder"));
        FACTORIES.put("getByText", List.of("text", "label", "ariaLabel"));
        FACTORIES.put("getByHeaderText", List.of("header"));
        FACTORIES.put("getByTitle", List.of("title"));
        FACTORIES.put("getBySummaryText", List.of("summary"));
        FACTORIES.put("getByName", List.of("name"));
        FACTORIES.put("getByButtonText", List.of("buttonText"));
        FACTORIES.put("getById", List.of("id"));
        FACTORIES.put("get", List.of());
    }

    // @formatter:off
    private static final String COLLECT_SCRIPT = """
        ([known, marker]) => {
          const KNOWN = new Set(known);
          // Items are reached through their container's API; list them there.
          const OWNERS = {
            'vaadin-tab': 'vaadin-tabs', 'vaadin-side-nav-item': 'vaadin-side-nav',
            'vaadin-breadcrumbs-item': 'vaadin-breadcrumbs',
            'vaadin-accordion-panel': 'vaadin-accordion',
            'vaadin-checkbox': 'vaadin-checkbox-group', 'vaadin-radio-button': 'vaadin-radio-group',
            'vaadin-avatar': 'vaadin-avatar-group', 'vaadin-menu-bar-button': 'vaadin-menu-bar'
          };
          const ITEMS = {};
          for (const [item, owner] of Object.entries(OWNERS)) ITEMS[owner] = item;
          ITEMS['vaadin-list-box'] = 'vaadin-item';
          // Overlays that only exist while open.
          // Components whose own text is their name.
          const TEXT_TAGS = new Set(['vaadin-button', 'vaadin-badge', 'vaadin-breadcrumbs-item']);
          const OPENABLE = new Set(['vaadin-dialog', 'vaadin-confirm-dialog', 'vaadin-popover',
            'vaadin-login-overlay']);
          const ITEM_OVERLAYS = 'vaadin-combo-box-overlay,vaadin-multi-select-combo-box-overlay,'
            + 'vaadin-select-overlay,vaadin-menu-bar-overlay,vaadin-context-menu-overlay';
          // Structural or internal tags that are not worth reporting as missing a wrapper.
          const INTERNAL = /^vaadin-(horizontal-layout|vertical-layout|scroller|icon|item|form-item|form-row|dev-tools|connection-indicator|tooltip|drawer-toggle|grid-.*|.*-(overlay|container|content|item|chip|summary|heading|toggle|column|sorter|list-box|button|wrapper|card|header|footer|layout|scroller|submenu|icon|file|file-list))$/;
          // Composite components whose inner components are driven through the composite's API.
          const COMPOSITES = 'vaadin-upload,vaadin-login-form,vaadin-login-overlay,'
            + 'vaadin-message-input,vaadin-date-time-picker,vaadin-avatar-group,vaadin-confirm-dialog,'
            + 'vaadin-custom-field';

          const clean = (s) => (s || '').toString().replace(/\\s+/g, ' ').trim();
          const ownText = (el) => clean(Array.from(el.childNodes)
            .filter(n => n.nodeType === 3 || (n.nodeType === 1 && !n.hasAttribute('slot')))
            .map(n => n.textContent).join(' '));
          const visible = (el) => el.checkVisibility ? el.checkVisibility() : true;

          document.querySelectorAll('[' + marker + ']').forEach(e => e.removeAttribute(marker));
          const out = [];
          const other = {};
          const all = Array.from(document.querySelectorAll('*'))
            .filter(e => e.localName.startsWith('vaadin-'));
          for (const el of all) {
            const tag = el.localName;
            if (el.closest(ITEM_OVERLAYS) && !el.matches(ITEM_OVERLAYS)) continue;
            const grid = el.closest('vaadin-grid');
            if (grid && grid !== el) continue;
            if (!KNOWN.has(tag)) {
              if (!INTERNAL.test(tag)) other[tag] = (other[tag] || 0) + 1;
              continue;
            }
            if (OWNERS[tag] && el.parentElement && el.parentElement.closest(OWNERS[tag])) continue;
            if (el.parentElement && el.parentElement.closest(COMPOSITES)) continue;
            if (OPENABLE.has(tag) && !el.opened) continue;

            const index = out.length;
            el.setAttribute(marker, String(index));
            const flags = [];
            if (!visible(el)) flags.push('hidden');
            if (el.disabled) flags.push('disabled');
            if (el.readonly) flags.push('read-only');
            if (el.required) flags.push('required');
            if (el.invalid) flags.push('invalid');

            let state = '';
            let tree = false;
            if (tag === 'vaadin-grid') {
              const rows = el._flatSize ?? el._effectiveSize ?? el.size ?? 0;
              const headers = [];
              el.shadowRoot?.querySelectorAll('thead th').forEach(th => {
                const slot = th.querySelector('slot');
                const txt = slot?.assignedNodes ? clean(slot.assignedNodes().map(n => n.textContent).join('')) : '';
                if (txt) headers.push(txt);
              });
              tree = !!el.querySelector('vaadin-grid-tree-toggle');
              state = ' — ' + rows + ' rows, columns [' + headers.join(', ') + ']';
            } else if (tag === 'vaadin-virtual-list') {
              state = ' — ' + (el.items || []).length + ' items';
            } else if (tag === 'vaadin-checkbox' || tag === 'vaadin-switch') {
              state = ' = ' + (el.checked ? 'checked' : 'unchecked');
            } else if (tag === 'vaadin-details' || tag === 'vaadin-accordion-panel') {
              state = el.opened ? ' (opened)' : ' (closed)';
            } else if ('value' in el && !(el.value instanceof Object && !Array.isArray(el.value))) {
              const v = Array.isArray(el.value) ? el.value.join(', ') : el.value;
              if (v !== undefined && v !== null && v !== '') state = ' = "' + v + '"';
            }

            let items = [];
            if (ITEMS[tag]) {
              items = Array.from(el.querySelectorAll(ITEMS[tag]))
                .filter(i => i.closest(tag) === el)
                .map(i => clean(i.label) || clean(i.getAttribute('aria-label'))
                  || clean(i.querySelector('vaadin-accordion-heading')?.textContent) || ownText(i))
                .filter(t => t);
            }

            // A custom field's wrapped fields, reached through getField(type, label | index).
            let fields = [];
            if (tag === 'vaadin-custom-field') {
              fields = Array.from(el.children)
                .filter(f => f.localName.startsWith('vaadin-') && !f.hasAttribute('slot'))
                .map(f => ({ tag: f.localName,
                  name: clean(f.label) || clean(f.accessibleName) || clean(f.getAttribute('aria-label'))
                    || clean(f.querySelector(':scope > [slot="sr-label"]')?.textContent)
                    || clean(f.querySelector(':scope > input')?.getAttribute('aria-label')) }));
            }

            out.push({
              index, tag, tree, items, fields, flags, state,
              id: el.id || '',
              label: clean(el.label),
              ariaLabel: clean(el.getAttribute('aria-label')) || clean(el.accessibleName),
              placeholder: clean(el.placeholder),
              text: tag === 'vaadin-notification-card' ? clean(el.textContent)
                : TEXT_TAGS.has(tag) ? ownText(el) : '',
              header: tag === 'vaadin-dialog' || tag === 'vaadin-confirm-dialog'
                ? clean(el.headerTitle) || clean(el.getAttribute('aria-label')) : '',
              title: tag === 'vaadin-card'
                ? clean(el.cardTitle) || clean(el.querySelector(':scope > [slot="title"]')?.textContent)
                : tag === 'vaadin-login-overlay' ? clean(el.title) : '',
              summary: tag === 'vaadin-details'
                ? clean(el.querySelector(':scope > vaadin-details-summary, :scope > [slot="summary"]')?.textContent)
                  || clean(typeof el.summary === 'string' ? el.summary : '')
                : '',
              name: tag === 'vaadin-avatar' ? clean(el.getAttribute('name')) : '',
              buttonText: tag === 'vaadin-upload'
                ? clean(el.querySelector('[slot="add-button"]')?.textContent) : ''
            });
          }
          return { components: out, other };
        }
        """;
    // @formatter:on

    private ViewOutline() {
    }

    /**
     * Capture the outline of the page as text: one entry per component, with the
     * verified lookup on the line below it.
     *
     * @param page the page to outline, already navigated to the view
     * @return the outline; never {@code null}
     */
    @SuppressWarnings("unchecked")
    public static String capture(Page page) {
        Map<String, Object> result = (Map<String, Object>) page.evaluate(COLLECT_SCRIPT,
                List.of(tagToClass().keySet().stream().toList(), MARKER));
        try {
            List<Map<String, Object>> components =
                    (List<Map<String, Object>>) result.get("components");
            Map<String, Object> other = (Map<String, Object>) result.get("other");
            return render(page, components, other);
        } finally {
            page.evaluate("m => document.querySelectorAll('[' + m + ']')"
                    + ".forEach(e => e.removeAttribute(m))", MARKER);
        }
    }

    /**
     * Capture the outline and write it to {@value #FILE_NAME} in the default
     * report directory ({@code target/agent-report/}, or {@code build/} under
     * Gradle).
     *
     * @param page the page to outline, already navigated to the view
     * @return the written file
     */
    public static Path write(Page page) {
        return write(page, AgentReport.defaultReportDirectory());
    }

    /**
     * Capture the outline and write it to {@value #FILE_NAME} in the given
     * directory, creating the directory if needed.
     *
     * @param page      the page to outline, already navigated to the view
     * @param directory the directory to write into
     * @return the written file
     */
    public static Path write(Page page, Path directory) {
        Path file = directory.resolve(FILE_NAME);
        try {
            Files.createDirectories(directory);
            Files.writeString(file, capture(page) + System.lineSeparator(),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not write " + file, e);
        }
        return file;
    }

    private static String render(Page page, List<Map<String, Object>> components,
            Map<String, Object> other) {
        Map<String, Class<? extends VaadinElement>> classes = tagToClass();
        StringBuilder sb = new StringBuilder();
        sb.append("View outline of ").append(safeUrl(page)).append(" — ")
                .append(components.size()).append(" components").append(nl());
        sb.append("Lookups below were run against this page and resolve to the listed "
                + "component.").append(nl());
        for (Map<String, Object> c : components) {
            String tag = str(c, "tag");
            Class<? extends VaadinElement> type = Boolean.TRUE.equals(c.get("tree"))
                    ? TreeGridElement.class : classes.get(tag);
            sb.append(nl()).append(describe(c)).append(nl());
            sb.append("    ").append(lookup(page, type, c, tag)).append(nl());
            List<?> items = (List<?>) c.get("items");
            if (items != null && !items.isEmpty()) {
                sb.append("    items (full label, case-sensitive): ");
                List<String> quoted = new ArrayList<>();
                for (Object item : items.subList(0, Math.min(items.size(), MAX_ITEMS))) {
                    quoted.add(quote(String.valueOf(item)));
                }
                sb.append(String.join(", ", quoted));
                if (items.size() > MAX_ITEMS) {
                    sb.append(", … (").append(items.size() - MAX_ITEMS).append(" more)");
                }
                sb.append(nl());
            }
            List<?> fields = (List<?>) c.get("fields");
            if (fields != null && !fields.isEmpty()) {
                sb.append("    fields (getField(Type.class, label or index)): ")
                        .append(describeFields(fields, classes)).append(nl());
            }
        }
        if (other != null && !other.isEmpty()) {
            List<String> tags = new ArrayList<>();
            other.forEach((tag, count) -> tags.add(tag + " (" + ((Number) count).intValue() + ")"));
            sb.append(nl()).append("No DramaFinder wrapper: ").append(String.join(", ", tags))
                    .append(" — use a Playwright locator or extend VaadinElement.").append(nl());
        }
        return sb.toString().stripTrailing();
    }

    // "SelectElement \"Country code\", TextFieldElement \"Number\"", with the
    // position among fields of the same type when a field has no name.
    @SuppressWarnings("unchecked")
    private static String describeFields(List<?> fields,
            Map<String, Class<? extends VaadinElement>> classes) {
        Map<String, Integer> seen = new LinkedHashMap<>();
        List<String> parts = new ArrayList<>();
        for (Object field : fields) {
            Map<String, Object> f = (Map<String, Object>) field;
            String tag = str(f, "tag");
            int position = seen.merge(tag, 1, Integer::sum) - 1;
            Class<? extends VaadinElement> type = classes.get(tag);
            String typeName = type != null ? type.getSimpleName() : tag;
            String name = str(f, "name");
            parts.add(typeName + " " + (name.isEmpty() ? "#" + position : quote(name)));
        }
        return String.join(", ", parts);
    }

    private static String describe(Map<String, Object> c) {
        StringBuilder sb = new StringBuilder(str(c, "tag"));
        String name = firstNonEmpty(c, "label", "ariaLabel", "header", "summary", "title", "text");
        if (!name.isEmpty()) {
            sb.append(' ').append(quote(name));
        }
        if (!str(c, "id").isEmpty()) {
            sb.append(" #").append(str(c, "id"));
        }
        sb.append(str(c, "state"));
        List<?> flags = (List<?>) c.get("flags");
        if (flags != null && !flags.isEmpty()) {
            List<String> names = new ArrayList<>();
            flags.forEach(f -> names.add(String.valueOf(f)));
            sb.append(" (").append(String.join(", ", names)).append(')');
        }
        return sb.toString();
    }

    // The first factory call that resolves to exactly this component, or a
    // constructor over a locator when none does.
    private static String lookup(Page page, Class<? extends VaadinElement> type,
            Map<String, Object> c, String tag) {
        String index = String.valueOf(((Number) c.get("index")).intValue());
        String simpleName = type.getSimpleName();
        String misdirected = null;
        for (Map.Entry<String, List<String>> factory : FACTORIES.entrySet()) {
            String methodName = factory.getKey();
            if (factory.getValue().isEmpty()) {
                Method method = findFactory(type, methodName, Page.class);
                if (method != null && matchCount(page, method, index) == 0) {
                    return simpleName + "." + methodName + "(page)";
                }
                continue;
            }
            Method method = findFactory(type, methodName, Page.class, String.class);
            if (method == null) {
                continue;
            }
            for (String value : candidates(c, factory.getValue())) {
                String call = simpleName + "." + methodName + "(page, " + quote(value) + ")";
                int matches = matchCount(page, method, index, value);
                if (matches == 0) {
                    return call;
                }
                if (matches > 0 && misdirected == null) {
                    misdirected = call + (matches == 1 ? " finds another component first"
                            : " matches " + matches + " components")
                            + " (labels match as substrings)";
                }
            }
        }
        String id = str(c, "id");
        String locator = !id.isEmpty() && isUniqueId(page, id)
                ? "page.locator(" + quote("#" + id) + ")"
                : "page.locator(" + quote(tag) + ").nth(" + nthOf(page, tag, index) + ")";
        String reason = misdirected != null ? misdirected : "no factory singles it out";
        return "new " + simpleName + "(" + locator + ")  // " + reason
                + ": scope the lookup to a container or give the component an id";
    }

    private static List<String> candidates(Map<String, Object> c, List<String> keys) {
        List<String> values = new ArrayList<>();
        for (String key : keys) {
            String value = str(c, key);
            if (!value.isEmpty() && !values.contains(value)) {
                values.add(value);
            }
        }
        return values;
    }

    private static Method findFactory(Class<?> type, String name, Class<?>... parameters) {
        try {
            Method method = type.getMethod(name, parameters);
            if (Modifier.isStatic(method.getModifiers())
                    && type.isAssignableFrom(method.getReturnType())) {
                return method;
            }
        } catch (NoSuchMethodException e) {
            // This wrapper has no such factory.
        }
        return null;
    }

    // Calls the factory and reports what its locator resolves to: 0 when it is
    // exactly this component, the number of elements it matches otherwise
    // (1 being a single other component), or -1 when it matches nothing or the
    // call fails.
    private static int matchCount(Page page, Method factory, String index, Object... args) {
        try {
            Object[] callArgs = new Object[args.length + 1];
            callArgs[0] = page;
            System.arraycopy(args, 0, callArgs, 1, args.length);
            VaadinElement element = (VaadinElement) factory.invoke(null, callArgs);
            Object markers = element.getLocator().evaluateAll(
                    "(els, m) => els.map(e => e.getAttribute(m))", MARKER);
            if (!(markers instanceof List<?> list) || list.isEmpty()) {
                return -1;
            }
            return list.size() == 1 && index.equals(list.get(0)) ? 0 : list.size();
        } catch (ReflectiveOperationException | RuntimeException e) {
            return -1;
        }
    }

    // Position of the component among everything page.locator(tag) matches,
    // which also counts the components the outline does not list.
    private static int nthOf(Page page, String tag, String index) {
        Object position = page.locator(tag).evaluateAll(
                "(els, [m, i]) => els.findIndex(e => e.getAttribute(m) === i)",
                List.of(MARKER, index));
        return position instanceof Number n ? n.intValue() : 0;
    }

    private static boolean isUniqueId(Page page, String id) {
        Object count = page.evaluate("id => document.querySelectorAll('[id=\"' + CSS.escape(id) + '\"]').length", id);
        return count instanceof Number n && n.intValue() == 1;
    }

    private static Map<String, Class<? extends VaadinElement>> tagToClass() {
        Map<String, Class<? extends VaadinElement>> map = new LinkedHashMap<>();
        for (Class<? extends VaadinElement> type : ELEMENTS) {
            String tag = tagOf(type);
            if (tag != null) {
                map.putIfAbsent(tag, type);
            }
        }
        return map;
    }

    // The FIELD_TAG_NAME constant every concrete wrapper declares.
    static String tagOf(Class<?> type) {
        try {
            return (String) type.getField("FIELD_TAG_NAME").get(null);
        } catch (ReflectiveOperationException | ClassCastException e) {
            return null;
        }
    }

    // The wrappers the outline knows, exposed for the registry completeness test.
    static List<Class<? extends VaadinElement>> elements() {
        return ELEMENTS;
    }

    private static String firstNonEmpty(Map<String, Object> c, String... keys) {
        for (String key : keys) {
            String value = str(c, key);
            if (!value.isEmpty()) {
                return value;
            }
        }
        return "";
    }

    private static String str(Map<String, Object> c, String key) {
        return Objects.toString(c.get(key), "");
    }

    private static String quote(String value) {
        return '"' + value.replace("\\", "\\\\").replace("\"", "\\\"") + '"';
    }

    private static String safeUrl(Page page) {
        try {
            return page.url();
        } catch (RuntimeException e) {
            return "(unknown)";
        }
    }

    private static String nl() {
        return System.lineSeparator();
    }
}
