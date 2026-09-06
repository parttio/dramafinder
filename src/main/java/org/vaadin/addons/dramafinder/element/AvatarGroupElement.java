package org.vaadin.addons.dramafinder.element;

import java.util.ArrayList;
import java.util.List;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-avatar-group>}.
 * <p>
 * The group renders one {@code <vaadin-avatar>} per visible item as a direct
 * light-DOM child, plus a single overflow avatar carrying {@code slot="overflow"}
 * once {@code maxItemsVisible} (or the available width) hides some of them.
 * Clicking the overflow avatar opens an overlay holding a
 * {@code <vaadin-avatar-group-menu>} whose {@code <vaadin-avatar-group-menu-item>}
 * children each wrap the avatar of one hidden item. Those avatars are in the DOM
 * as soon as the group overflows, but are only visible while the overlay is open.
 * <p>
 * Individual avatars — visible or overflowing — are exposed as
 * {@link AvatarElement} instances.
 */
@PlaywrightElement(AvatarGroupElement.FIELD_TAG_NAME)
public class AvatarGroupElement extends VaadinElement
        implements HasStyleElement, HasThemeElement {

    public static final String FIELD_TAG_NAME = "vaadin-avatar-group";
    public static final String FIELD_MENU_TAG_NAME = "vaadin-avatar-group-menu";
    public static final String FIELD_MENU_ITEM_TAG_NAME = "vaadin-avatar-group-menu-item";

    /**
     * Selector for the visible avatars: direct {@code <vaadin-avatar>} children
     * without a {@code slot} attribute. The explicit xpath does not pierce the
     * shadow DOM, which keeps the overflow avatar ({@code slot="overflow"}) and
     * the avatars nested in the overlay menu out of the match.
     */
    private static final String VISIBLE_AVATARS_XPATH =
            "xpath=./" + AvatarElement.FIELD_TAG_NAME + "[not(@slot)]";

    private static final String OVERFLOW_AVATAR_SELECTOR =
            AvatarElement.FIELD_TAG_NAME + "[slot='overflow']";

    /**
     * Create a new {@code AvatarGroupElement}.
     *
     * @param locator the locator for the {@code <vaadin-avatar-group>} element
     */
    public AvatarGroupElement(Locator locator) {
        super(locator);
    }

    // ── Static Factory Methods ─────────────────────────────────────────

    /**
     * Get the first {@code AvatarGroupElement} on the page.
     *
     * @param page the Playwright page
     * @return the first matching {@code AvatarGroupElement}
     */
    public static AvatarGroupElement get(Page page) {
        return new AvatarGroupElement(page.locator(FIELD_TAG_NAME).first());
    }

    /**
     * Get the first {@code AvatarGroupElement} within a scope.
     *
     * @param locator the scope containing the group (not the group itself)
     * @return the first matching {@code AvatarGroupElement}
     */
    public static AvatarGroupElement get(Locator locator) {
        return new AvatarGroupElement(locator.locator(FIELD_TAG_NAME).first());
    }

    // ── Visible avatars ────────────────────────────────────────────────

    /**
     * Locator matching every visible avatar, in rendering order. The overflow
     * avatar is not included.
     *
     * @return locator for the visible {@code <vaadin-avatar>} children
     */
    public Locator getAvatarsLocator() {
        return getLocator().locator(VISIBLE_AVATARS_XPATH);
    }

    /**
     * Get the visible avatars, in rendering order.
     *
     * @return the visible avatars, empty when the group has no items
     */
    public List<AvatarElement> getAvatars() {
        return toAvatarElements(getAvatarsLocator());
    }

    /**
     * Get the avatar at the given position among the visible avatars.
     *
     * @param index zero-based index
     * @return the avatar at that position
     */
    public AvatarElement getAvatar(int index) {
        return new AvatarElement(getAvatarsLocator().nth(index));
    }

    /**
     * Get the number of visible avatars, excluding the overflow avatar.
     *
     * @return the visible avatar count
     */
    public int getVisibleCount() {
        return getAvatarsLocator().count();
    }

    /**
     * Get the names of the visible avatars, in rendering order.
     *
     * @return the names; an entry is {@code null} when that avatar has no name
     */
    public List<String> getNames() {
        return toNames(getAvatarsLocator());
    }

    /**
     * Get the maximum number of avatars the group displays before overflowing.
     *
     * @return the {@code maxItemsVisible} property, or {@code null} when unset
     */
    public Integer getMaxItemsVisible() {
        Object value = getProperty("maxItemsVisible");
        return value == null ? null : ((Number) value).intValue();
    }

    /**
     * Set the maximum number of avatars to display before overflowing.
     *
     * @param maxItemsVisible the maximum number of visible avatars
     */
    public void setMaxItemsVisible(int maxItemsVisible) {
        setProperty("maxItemsVisible", maxItemsVisible);
    }

    // ── Overflow ───────────────────────────────────────────────────────

    /**
     * Get the overflow avatar, the one summarising the hidden items as
     * {@code +N}. It is present even when nothing overflows, in which case it
     * is hidden.
     *
     * @return the overflow avatar
     */
    public AvatarElement getOverflowAvatar() {
        return new AvatarElement(getLocator().locator(OVERFLOW_AVATAR_SELECTOR).first());
    }

    /**
     * Whether some avatars are hidden behind the overflow avatar.
     *
     * @return {@code true} when the group has an overflow
     */
    public boolean hasOverflow() {
        return getLocator().getAttribute("has-overflow") != null;
    }

    /**
     * Open the overflow overlay by clicking the overflow avatar, and wait until
     * it is open. Does nothing when the overlay is already open — clicking the
     * overflow avatar toggles the overlay, so an unconditional click would
     * close it again.
     */
    public void openOverflow() {
        if (!isOverflowOpen()) {
            getOverflowAvatar().click();
        }
        assertOverflowOpen();
    }

    /**
     * Close the overflow overlay with the {@code Escape} key, and wait until it
     * is closed.
     */
    public void closeOverflow() {
        getLocator().page().keyboard().press("Escape");
        assertOverflowClosed();
    }

    /**
     * Whether the overflow overlay is currently open.
     *
     * @return {@code true} when the overlay is open
     */
    public boolean isOverflowOpen() {
        return "true".equals(getOverflowAvatar().getLocator().getAttribute("aria-expanded"));
    }

    /**
     * Locator for the overflow overlay menu, which holds one menu item per
     * hidden avatar.
     *
     * @return locator for {@code <vaadin-avatar-group-menu>}
     */
    public Locator getOverflowMenuLocator() {
        return getLocator().locator(FIELD_MENU_TAG_NAME).first();
    }

    /**
     * Locator matching every avatar inside the overflow overlay, in rendering
     * order.
     *
     * @return locator for the overflowing {@code <vaadin-avatar>} elements
     */
    public Locator getOverflowAvatarsLocator() {
        return getOverflowMenuLocator()
                .locator(FIELD_MENU_ITEM_TAG_NAME + " " + AvatarElement.FIELD_TAG_NAME);
    }

    /**
     * Get the avatars hidden behind the overflow avatar, in rendering order.
     * They are in the DOM as soon as the group overflows, but only become
     * visible once {@link #openOverflow()} has opened the overlay.
     *
     * @return the overflowing avatars, empty when nothing overflows
     */
    public List<AvatarElement> getOverflowAvatars() {
        return toAvatarElements(getOverflowAvatarsLocator());
    }

    /**
     * Get the names of the avatars hidden behind the overflow avatar, in
     * rendering order.
     *
     * @return the names; an entry is {@code null} when that avatar has no name
     */
    public List<String> getOverflowNames() {
        return toNames(getOverflowAvatarsLocator());
    }

    // ── Assertions ─────────────────────────────────────────────────────

    /**
     * Assert the names of the visible avatars, in order.
     *
     * @param names the expected names
     */
    public void assertNames(String... names) {
        assertNames(getAvatarsLocator(), names);
    }

    /**
     * Assert the names of the avatars hidden behind the overflow avatar, in
     * order.
     *
     * @param names the expected names
     */
    public void assertOverflowNames(String... names) {
        assertNames(getOverflowAvatarsLocator(), names);
    }

    /**
     * Assert the number of visible avatars, excluding the overflow avatar.
     *
     * @param count the expected visible avatar count
     */
    public void assertVisibleCount(int count) {
        assertThat(getAvatarsLocator()).hasCount(count);
    }

    /**
     * Assert that some avatars are hidden behind the overflow avatar.
     */
    public void assertHasOverflow() {
        assertThat(getLocator()).hasAttribute("has-overflow", "");
    }

    /**
     * Assert that no avatar is hidden behind the overflow avatar.
     */
    public void assertHasNoOverflow() {
        assertThat(getLocator()).not().hasAttribute("has-overflow", "");
    }

    /**
     * Assert that the overflow overlay is open.
     */
    public void assertOverflowOpen() {
        assertThat(getOverflowAvatar().getLocator()).hasAttribute("aria-expanded", "true");
    }

    /**
     * Assert that the overflow overlay is closed.
     */
    public void assertOverflowClosed() {
        assertThat(getOverflowAvatar().getLocator()).hasAttribute("aria-expanded", "false");
    }

    private static void assertNames(Locator avatars, String... names) {
        assertThat(avatars).hasCount(names.length);
        for (int i = 0; i < names.length; i++) {
            assertThat(avatars.nth(i)).hasJSProperty("name", names[i]);
        }
    }

    private static List<AvatarElement> toAvatarElements(Locator avatars) {
        List<AvatarElement> elements = new ArrayList<>();
        for (Locator avatar : avatars.all()) {
            elements.add(new AvatarElement(avatar));
        }
        return elements;
    }

    /**
     * Read the {@code name} property of every matched avatar in a single
     * round-trip to the browser.
     */
    private static List<String> toNames(Locator avatars) {
        Object evaluated = avatars
                .evaluateAll("avatars => avatars.map(avatar => avatar.name ?? null)");
        List<String> names = new ArrayList<>();
        for (Object name : (List<?>) evaluated) {
            names.add((String) name);
        }
        return names;
    }

}
