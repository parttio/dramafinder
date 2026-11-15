package org.vaadin.addons.dramafinder.element;

import java.util.regex.Pattern;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.query.TextQuery;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;
import org.vaadin.addons.dramafinder.element.utils.IdLocator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.vaadin.addons.dramafinder.element.AssertionWaits.assertEventually;

/**
 * PlaywrightElement for {@code <vaadin-badge>}.
 * <p>
 * Wraps the real {@code vaadin-badge} component (server class
 * {@code com.vaadin.flow.component.badge.Badge}, available since Vaadin 25.1).
 * A badge rendered the legacy way — a {@code Span} carrying
 * {@code theme="badge"} — is <em>not</em> a {@code vaadin-badge} and is not
 * matched by this element; use a plain {@link com.microsoft.playwright.Locator}
 * for those.
 * <p>
 * A badge can carry three independent pieces of content:
 * <ul>
 *   <li>text or a component in the default slot — read with {@link #getText()},</li>
 *   <li>a {@code number} property rendered by the component itself — read with
 *       {@link #getNumber()},</li>
 *   <li>a component in the {@code icon} slot — located with
 *       {@link #getIconLocator()}.</li>
 * </ul>
 * {@link #getText()} reads only the badge's own, non-slotted content, so
 * neither the number (rendered in the shadow DOM) nor the icon (in the
 * {@code icon} slot) is part of it.
 * <p>
 * Theme variants ({@code success}, {@code error}, {@code contrast},
 * {@code warning}, {@code small}, {@code filled}, {@code dot},
 * {@code icon-only}, {@code number-only}) are combined into a single
 * space-separated {@code theme} attribute. Use
 * {@link HasThemeElement#assertHasThemeVariant(String)} to assert one variant
 * among several, or {@link HasThemeElement#assertTheme(String)} for the whole
 * attribute.
 * <p>
 * The {@code dot}, {@code icon-only} and {@code number-only} variants hide
 * content visually while keeping it in the DOM for screen readers, so the
 * text and number assertions keep working under those variants.
 */
@PlaywrightElement(BadgeElement.FIELD_TAG_NAME)
public class BadgeElement extends VaadinElement implements HasThemeElement, HasStyleElement,
        Findable<TextQuery<BadgeElement>> {

    public static final String FIELD_TAG_NAME = "vaadin-badge";

    /**
     * Concatenate the text of the child nodes that are not assigned to a named
     * slot, i.e. the badge's own content. Text nodes have no {@code slot}
     * attribute, so only slotted elements — the icon — are skipped.
     */
    private static final String OWN_TEXT_JS = """
            el => Array.from(el.childNodes)
                .filter(node => !(node.nodeType === Node.ELEMENT_NODE && node.hasAttribute('slot')))
                .map(node => node.textContent)
                .join('')""";

    /**
     * Create a new {@code BadgeElement}.
     *
     * @param locator the locator for the {@code <vaadin-badge>} element
     */
    public BadgeElement(Locator locator) {
        super(locator);
    }

    // ── Static Factory Methods ─────────────────────────────────────────

    /**
     * Get the first {@code BadgeElement} on the page.
     *
     * @param page the Playwright page
     * @return the first matching {@code BadgeElement}
     */
    public static BadgeElement get(Page page) {
        return new BadgeElement(page.locator(FIELD_TAG_NAME).first());
    }

    /**
     * Get the first {@code BadgeElement} within a scope.
     *
     * @param locator the scope containing the badge (not the badge itself)
     * @return the first matching {@code BadgeElement}
     */
    public static BadgeElement get(Locator locator) {
        return new BadgeElement(locator.locator(FIELD_TAG_NAME).first());
    }

    /**
     * Get a {@code BadgeElement} by its text content.
     * <p>
     * The match is a case-insensitive substring match on the badge's text, so
     * pass enough of the text to be unambiguous. Playwright's {@code hasText}
     * filter traverses the shadow DOM, so — unlike {@link #getText()} — the
     * matched text also contains the rendered {@code number}: on a page with
     * {@code new Badge("unread messages", 5)}, {@code getByText(page, "5")}
     * matches that badge. Pass some of the badge's own text to avoid matching a
     * number by accident.
     *
     * @param page the Playwright page
     * @param text the text the badge contains
     * @return the first matching {@code BadgeElement}
     */
    public static BadgeElement getByText(Page page, String text) {
        return new BadgeElement(page
                .locator(FIELD_TAG_NAME, new Page.LocatorOptions().setHasText(text))
                .first());
    }

    /**
     * Get a {@code BadgeElement} by its text content within a scope.
     * <p>
     * As with {@link #getByText(Page, String)}, the matched text includes the
     * {@code number} the component renders in its shadow DOM.
     *
     * @param locator the scope containing the badge
     * @param text    the text the badge contains
     * @return the first matching {@code BadgeElement}
     */
    public static BadgeElement getByText(Locator locator, String text) {
        return new BadgeElement(locator
                .locator(FIELD_TAG_NAME, new Locator.LocatorOptions().setHasText(text))
                .first());
    }

    // ── Locators ───────────────────────────────────────────────────────

    /**
     * Locator for the content of the {@code icon} slot.
     *
     * @return the icon slot locator; it matches nothing when no icon is set
     */
    public Locator getIconLocator() {
        return getLocator().locator("> [slot='icon']").first();
    }

    // ── Properties ─────────────────────────────────────────────────────

    /**
     * Get the badge's own text content.
     * <p>
     * Only child nodes that are not assigned to a named slot are read, so the
     * text of an icon that renders a glyph in the light DOM is excluded. The
     * {@code number} lives in the shadow DOM and is excluded as well; read it
     * with {@link #getNumber()}.
     *
     * @return the badge's own text content, or an empty string when it has none
     */
    @Override
    public String getText() {
        return (String) getLocator().evaluate(OWN_TEXT_JS);
    }

    /**
     * Get the number displayed by the badge.
     *
     * @return the number, or {@code null} when the badge has no number
     */
    public Integer getNumber() {
        Object value = getProperty("number");
        return value == null ? null : ((Number) value).intValue();
    }

    // ── Assertions ─────────────────────────────────────────────────────

    /**
     * Assert the badge's text content.
     * <p>
     * The comparison is exact (no whitespace normalization) and covers only the
     * text {@link #getText()} returns, so neither the {@code number} nor an
     * icon's light-DOM text is included. Playwright's {@code hasText} assertion
     * does traverse the shadow DOM, so asserting on {@link #getLocator()}
     * directly would also match the number.
     *
     * @param text the expected text, or {@code null} to assert the badge has no
     *             text content
     */
    public void assertText(String text) {
        String expected = text == null ? "" : text;
        assertEventually(locator.page(), "Badge text", this::getText, expected);
    }

    /**
     * Assert the badge's number.
     *
     * @param number the expected number, or {@code null} to assert the badge has
     *               no number. The component toggles the {@code has-number}
     *               attribute exactly when {@code number != null}, so absence is
     *               asserted on that attribute.
     */
    public void assertNumber(Integer number) {
        if (number != null) {
            assertThat(getLocator()).hasJSProperty("number", number);
        } else {
            assertThat(getLocator()).not().hasAttribute("has-number", Pattern.compile(".*"));
        }
    }

    /**
     * Assert that the badge has content in its {@code icon} slot.
     */
    public void assertHasIcon() {
        assertThat(getLocator()).hasAttribute("has-icon", "");
    }

    /**
     * Assert that the badge has no content in its {@code icon} slot.
     */
    public void assertHasNoIcon() {
        assertThat(getLocator()).not().hasAttribute("has-icon", Pattern.compile(".*"));
    }

    // ── Lookup by id or test id ────────────────────────────────────────

    /**
     * Get a {@code BadgeElement} by its {@code id}, searching the entire page.
     * <p>
     * A fallback for a component without a user-visible name: prefer the label or
     * text lookups when there is one. The id must match in full; two components
     * with that id fail with a strict-mode error.
     *
     * @param page the Playwright page
     * @param id   the full {@code id}
     * @return the matching {@code BadgeElement}
     */
    public static BadgeElement getById(Page page, String id) {
        return new BadgeElement(IdLocator.byId(page, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code BadgeElement} by its {@code id}, scoped to the given locator.
     * <p>
     * A fallback for a component without a user-visible name. The id must match in
     * full; two components with that id inside the scope fail with a strict-mode
     * error.
     *
     * @param locator the locator whose subtree is searched
     * @param id      the full {@code id}
     * @return the matching {@code BadgeElement}
     */
    public static BadgeElement getById(Locator locator, String id) {
        return new BadgeElement(IdLocator.byId(locator, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code BadgeElement} by its test id, searching the entire page.
     * <p>
     * The test id is Playwright's test id attribute, {@code data-testid} unless
     * reconfigured — the attribute Flow's {@code Component.setTestId(String)} sets.
     * A fallback for a component without a user-visible name; it must match in
     * full, and two components with that test id fail with a strict-mode error.
     *
     * @param page   the Playwright page
     * @param testId the full test id
     * @return the matching {@code BadgeElement}
     */
    public static BadgeElement getByTestId(Page page, String testId) {
        return new BadgeElement(IdLocator.byTestId(page, FIELD_TAG_NAME, testId));
    }

    /**
     * Get a {@code BadgeElement} by its test id, scoped to the given locator.
     * <p>
     * See {@link #getByTestId(Page, String)}; only components inside the scope are
     * considered.
     *
     * @param locator the locator whose subtree is searched
     * @param testId  the full test id
     * @return the matching {@code BadgeElement}
     */
    public static BadgeElement getByTestId(Locator locator, String testId) {
        return new BadgeElement(IdLocator.byTestId(locator, FIELD_TAG_NAME, testId));
    }
}
