package org.vaadin.addons.dramafinder.element;

import java.util.Map;
import java.util.regex.Pattern;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;
import org.vaadin.addons.dramafinder.element.shared.HasTooltipElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-tooltip>}.
 * <p>
 * A tooltip is never a top-level component: Flow's
 * {@code com.vaadin.flow.component.shared.Tooltip} slots a
 * {@code <vaadin-tooltip slot="tooltip">} into the component it describes, so
 * the natural way to reach one is either
 * {@link HasTooltipElement#getTooltip()} on the wrapper of that component, or
 * {@link #get(Locator)} scoped to the target.
 * <p>
 * The host element is styled {@code display: contents} and renders nothing of
 * its own: what becomes visible is the {@code <vaadin-tooltip-overlay>} in its
 * shadow DOM, reachable through {@link #getOverlayLocator()}. The open/closed
 * state is therefore asserted on the {@code opened} attribute the component
 * reflects, which is the precise signal, rather than on visibility. The
 * tooltip text lives in a {@code role="tooltip"} element that stays in the DOM
 * while the tooltip is closed, which is why {@link #getText()} and
 * {@link #assertText(String)} work without opening the tooltip first.
 * <p>
 * By default a tooltip opens after a hover or focus delay of 500&nbsp;ms.
 * {@link #hoverTarget()} triggers it; the assertions auto-retry, so
 * {@code assertOpened()} right after it waits for the delay to elapse.
 */
@PlaywrightElement(TooltipElement.FIELD_TAG_NAME)
public class TooltipElement extends VaadinElement implements HasThemeElement, HasStyleElement {

    public static final String FIELD_TAG_NAME = "vaadin-tooltip";

    /** Tag name of the overlay the tooltip renders in its shadow DOM. */
    private static final String OVERLAY_TAG_NAME = "vaadin-tooltip-overlay";

    /**
     * Scroll the tooltip's target into view and return the viewport
     * coordinates of its centre, or {@code null} when the tooltip has no
     * target. The target is a live element reference on the {@code target}
     * property, so it cannot be expressed as a selector.
     */
    private static final String TARGET_CENTER_JS = """
            el => {
                const target = el.target;
                if (!target) {
                    return null;
                }
                target.scrollIntoView({ block: 'center', inline: 'center' });
                const rect = target.getBoundingClientRect();
                return { x: rect.x + rect.width / 2, y: rect.y + rect.height / 2 };
            }""";

    /**
     * Create a new {@code TooltipElement}.
     *
     * @param locator the locator for the {@code <vaadin-tooltip>} element
     */
    public TooltipElement(Locator locator) {
        super(locator);
    }

    // ── Static Factory Methods ─────────────────────────────────────────

    /**
     * Get the first {@code TooltipElement} on the page.
     *
     * @param page the Playwright page
     * @return the first matching {@code TooltipElement}
     */
    public static TooltipElement get(Page page) {
        return new TooltipElement(page.locator(FIELD_TAG_NAME).first());
    }

    /**
     * Get the first {@code TooltipElement} within a scope, typically the
     * component the tooltip is attached to.
     *
     * @param locator the scope containing the tooltip (not the tooltip itself)
     * @return the first matching {@code TooltipElement}
     */
    public static TooltipElement get(Locator locator) {
        return new TooltipElement(locator.locator(FIELD_TAG_NAME).first());
    }

    /**
     * Get a {@code TooltipElement} by its text.
     * <p>
     * The tooltip is matched through the accessible name of its
     * {@code role="tooltip"} content element, which is present whether the
     * tooltip is open or closed. The match is a case-insensitive substring
     * match, so pass enough of the text to be unambiguous.
     *
     * @param page the Playwright page
     * @param text the text the tooltip contains
     * @return the first matching {@code TooltipElement}
     */
    public static TooltipElement getByText(Page page, String text) {
        return new TooltipElement(page.locator(FIELD_TAG_NAME)
                .filter(new Locator.FilterOptions().setHas(
                        page.getByRole(AriaRole.TOOLTIP, new Page.GetByRoleOptions()
                                .setName(text)
                                .setIncludeHidden(true))))
                .first());
    }

    // ── Locators ───────────────────────────────────────────────────────

    /**
     * Locator for the element carrying the tooltip content, i.e. the
     * {@code role="tooltip"} node. It stays in the DOM while the tooltip is
     * closed, so it is matched with hidden elements included.
     *
     * @return the tooltip content locator
     */
    public Locator getContentLocator() {
        return getLocator().getByRole(AriaRole.TOOLTIP,
                new Locator.GetByRoleOptions().setIncludeHidden(true)).first();
    }

    /**
     * Locator for the {@code <vaadin-tooltip-overlay>} the tooltip renders in
     * its shadow DOM. The overlay exists from the first render on and is the
     * element that becomes visible when the tooltip opens.
     *
     * @return the overlay locator
     */
    public Locator getOverlayLocator() {
        return getLocator().locator(OVERLAY_TAG_NAME).first();
    }

    // ── Content ────────────────────────────────────────────────────────

    /**
     * Get the tooltip text.
     * <p>
     * Read from the {@code role="tooltip"} content node rather than from the
     * host, so it is available whether the tooltip is open or closed. When the
     * tooltip renders Markdown, this is the rendered text without the markup.
     *
     * @return the tooltip text, or {@code null} when the content node has none
     */
    @Override
    public String getText() {
        return getContentLocator().textContent();
    }

    /**
     * Assert the tooltip text.
     *
     * @param text the expected text, or {@code null} to assert the tooltip
     *             shows no content — an empty tooltip keeps its overlay
     *             hidden, so absence is asserted on the content's visibility
     */
    public void assertText(String text) {
        if (text != null) {
            assertThat(getContentLocator()).hasText(text,
                    new LocatorAssertions.HasTextOptions().setUseInnerText(true));
        } else {
            assertThat(getContentLocator()).not().isVisible();
        }
    }

    // ── Opened state ───────────────────────────────────────────────────

    /**
     * Whether the tooltip is open.
     * <p>
     * Based on the {@code opened} attribute the component reflects, which flips
     * exactly when the tooltip opens and closes. Visibility is a looser signal:
     * the host renders nothing of its own, and the overlay only reaches its
     * final state once the opening has gone through.
     *
     * @return {@code true} when the tooltip is open
     */
    public boolean isOpened() {
        return getLocator().getAttribute("opened") != null;
    }

    /** Assert that the tooltip is open. */
    public void assertOpened() {
        assertThat(getLocator()).hasAttribute("opened", "");
    }

    /** Assert that the tooltip is closed. */
    public void assertClosed() {
        assertThat(getLocator()).not().hasAttribute("opened", Pattern.compile(".*"));
    }

    // ── Position ───────────────────────────────────────────────────────

    /**
     * Get the effective position of the tooltip relative to its target, e.g.
     * {@code top-start} or {@code bottom}.
     * <p>
     * The host holds the position as a DOM property only; the overlay reflects
     * the effective value — the configured position, or the {@code bottom}
     * default — as an attribute, so it is read from there.
     *
     * @return the effective position, never {@code null} for a rendered tooltip
     */
    public String getPosition() {
        return getOverlayLocator().getAttribute("position");
    }

    /**
     * Assert the effective position of the tooltip relative to its target.
     *
     * @param position the expected position, e.g. {@code top-start}. A tooltip
     *                 with no configured position is {@code bottom}, so there
     *                 is no "absent" case to assert
     */
    public void assertPosition(String position) {
        assertThat(getOverlayLocator()).hasAttribute("position", position);
    }

    // ── Triggering ─────────────────────────────────────────────────────

    /**
     * Hover the element the tooltip describes, which opens the tooltip unless
     * it is in manual mode.
     * <p>
     * The target is scrolled into view first. Opening is delayed by the
     * tooltip's hover delay (500&nbsp;ms by default), so follow this with
     * {@link #assertOpened()} or {@link #assertText(String)}, which auto-retry,
     * rather than with {@link #isOpened()}.
     *
     * @throws IllegalStateException when the tooltip has no target element
     */
    @SuppressWarnings("unchecked")
    public void hoverTarget() {
        Object center = getLocator().evaluate(TARGET_CENTER_JS);
        if (center == null) {
            throw new IllegalStateException(
                    "The tooltip has no target element, so it cannot be hovered.");
        }
        Map<String, Object> point = (Map<String, Object>) center;
        getLocator().page().mouse().move(
                ((Number) point.get("x")).doubleValue(),
                ((Number) point.get("y")).doubleValue());
    }

    /**
     * Close the tooltip by pressing {@code Escape}.
     * <p>
     * The component listens for the key on the document, so the press is sent
     * to the page rather than to the tooltip, whose host cannot be focused. A
     * tooltip in manual mode ignores {@code Escape} and stays open.
     */
    public void closeWithEscape() {
        getLocator().page().keyboard().press("Escape");
    }
}
