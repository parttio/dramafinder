package org.vaadin.addons.dramafinder.element;

import java.util.Objects;
import java.util.regex.Pattern;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

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
 * The number lives in the component's shadow DOM, so it is <strong>not</strong>
 * part of {@link #getText()}.
 * <p>
 * Theme variants ({@code success}, {@code error}, {@code contrast},
 * {@code warning}, {@code small}, {@code filled}, {@code dot},
 * {@code icon-only}, {@code number-only}) are combined into a single
 * space-separated {@code theme} attribute. Use
 * {@link #assertHasThemeVariant(String)} to assert one variant among several,
 * or {@link HasThemeElement#assertTheme(String)} for the whole attribute.
 * <p>
 * The {@code dot}, {@code icon-only} and {@code number-only} variants hide
 * content visually while keeping it in the DOM for screen readers, so the
 * text and number assertions keep working under those variants.
 */
@PlaywrightElement(BadgeElement.FIELD_TAG_NAME)
public class BadgeElement extends VaadinElement implements HasThemeElement, HasStyleElement {

    public static final String FIELD_TAG_NAME = "vaadin-badge";

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
     * pass enough of the text to be unambiguous. The {@code number} is rendered
     * in the shadow DOM and is not part of the matched text.
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
     * badge's own text content, so the {@code number} — which the component
     * renders in its shadow DOM — is excluded. Playwright's
     * {@code hasText} assertion does traverse the shadow DOM, so asserting on
     * {@link #getLocator()} directly would also match the number.
     *
     * @param text the expected text, or {@code null} to assert the badge has no
     *             text content
     */
    public void assertText(String text) {
        assertThat(getLocator()).hasJSProperty("textContent", text == null ? "" : text);
    }

    /**
     * Assert the badge's number.
     *
     * @param number the expected number, or {@code null} to assert the badge has
     *               no number
     */
    public void assertNumber(Integer number) {
        locator.page().waitForCondition(() -> Objects.equals(number, getNumber()));
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

    /**
     * Assert that the badge's {@code theme} attribute contains the given
     * variant, ignoring any other variants that are also applied.
     *
     * @param variant the theme variant name, e.g. {@code success}
     */
    public void assertHasThemeVariant(String variant) {
        assertThat(getLocator()).hasAttribute("theme", themeVariantPattern(variant));
    }

    /**
     * Assert that the badge's {@code theme} attribute does not contain the given
     * variant.
     *
     * @param variant the theme variant name, e.g. {@code success}
     */
    public void assertHasNoThemeVariant(String variant) {
        assertThat(getLocator()).not().hasAttribute("theme", themeVariantPattern(variant));
    }

    /**
     * Build a pattern matching {@code variant} as a whole token inside a
     * space-separated attribute value. The escaping is done by hand because
     * Playwright compiles the pattern into a JavaScript {@code RegExp}, which
     * does not understand {@link Pattern#quote(String)}'s {@code \Q...\E}.
     */
    private static Pattern themeVariantPattern(String variant) {
        String quoted = variant.replaceAll("[\\\\^$.|?*+()\\[\\]{}]", "\\\\$0");
        return Pattern.compile("(^|\\s)" + quoted + "($|\\s)");
    }
}
