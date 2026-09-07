package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.BoundingBox;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-app-layout>}.
 * <p>
 * Exposes the three areas of an application shell — navbar, drawer and content —
 * as locators over the light DOM slotted into them, plus helpers to read and
 * change the drawer state through the {@code <vaadin-drawer-toggle>} placed in
 * the navbar.
 * <p>
 * Drawer state is read from the reflected {@code drawer-opened} attribute and
 * the layout mode from the reflected {@code overlay} attribute rather than from
 * measured geometry: the drawer animates open and closed, so positions and
 * sizes are transient while a transition runs.
 * <p>
 * In overlay mode (narrow viewports) an opened drawer is covered by a backdrop
 * that spans the whole viewport, so the drawer toggle cannot be clicked while
 * the drawer is open. Use {@link #closeDrawer()}, {@link #clickBackdrop()} or
 * {@link #closeDrawerWithEscape()} to close it.
 */
@PlaywrightElement(AppLayoutElement.FIELD_TAG_NAME)
public class AppLayoutElement extends VaadinElement implements HasStyleElement, HasThemeElement {

    public static final String FIELD_TAG_NAME = "vaadin-app-layout";

    /** Tag name of the drawer toggle button placed in the navbar. */
    public static final String DRAWER_TOGGLE_TAG_NAME = "vaadin-drawer-toggle";

    /** Value of the {@code primary-section} attribute when the navbar comes first. */
    public static final String PRIMARY_SECTION_NAVBAR = "navbar";
    /** Value of the {@code primary-section} attribute when the drawer comes first. */
    public static final String PRIMARY_SECTION_DRAWER = "drawer";

    /**
     * Create a new {@code AppLayoutElement}.
     *
     * @param locator the locator for the {@code <vaadin-app-layout>} element
     */
    public AppLayoutElement(Locator locator) {
        super(locator);
    }

    /**
     * Get the first {@code AppLayoutElement} on the page.
     *
     * @param page the Playwright page
     * @return the first matching {@code AppLayoutElement}
     */
    public static AppLayoutElement get(Page page) {
        return new AppLayoutElement(page.locator(FIELD_TAG_NAME).first());
    }

    /**
     * Get the first {@code AppLayoutElement} within a parent locator.
     *
     * @param parent the parent locator to search within
     * @return the first matching {@code AppLayoutElement}
     */
    public static AppLayoutElement get(Locator parent) {
        return new AppLayoutElement(parent.locator(FIELD_TAG_NAME).first());
    }

    /**
     * Get an {@code AppLayoutElement} by its {@code id} attribute.
     *
     * @param page the Playwright page
     * @param id   the element id
     * @return the matching {@code AppLayoutElement}
     */
    public static AppLayoutElement getById(Page page, String id) {
        return new AppLayoutElement(page.locator("#" + id));
    }

    // ── Areas ─────────────────────────────────────────────────────────

    /**
     * Locator for the content slotted into the top navbar.
     * <p>
     * Matches every child assigned to the {@code navbar} slot, so it may resolve
     * to more than one element; chain further locators to target a single one.
     *
     * @return the locator for the navbar content
     */
    public Locator getNavbarLocator() {
        return getLocator().locator("> [slot~='navbar']");
    }

    /**
     * Locator for the content slotted into the bottom navbar.
     * <p>
     * Components added as touch-optimized are moved to this slot by the
     * component itself, and only on touchscreen devices; on other devices the
     * locator resolves to nothing.
     *
     * @return the locator for the bottom navbar content
     */
    public Locator getBottomNavbarLocator() {
        return getLocator().locator("> [slot='navbar-bottom']");
    }

    /**
     * Locator for the content slotted into the drawer.
     * <p>
     * Matches every child assigned to the {@code drawer} slot, so it may resolve
     * to more than one element; chain further locators to target a single one.
     *
     * @return the locator for the drawer content
     */
    public Locator getDrawerLocator() {
        return getLocator().locator("> [slot='drawer']");
    }

    /**
     * Locator for the content area, that is, the children without a slot.
     * <p>
     * With Flow this is the component set as the layout content, typically the
     * currently routed view.
     *
     * @return the locator for the content area
     */
    public Locator getContentLocator() {
        // using xpath to not pierce the shadow dom
        return getLocator().locator("xpath=./*[not(@slot)]");
    }

    /**
     * Locator for the backdrop shown behind an opened drawer in overlay mode.
     *
     * @return the locator for the {@code backdrop} part
     */
    public Locator getBackdropLocator() {
        return getLocator().locator("[part='backdrop']");
    }

    // ── Drawer toggle ─────────────────────────────────────────────────

    /**
     * Get the drawer toggle button of this layout.
     *
     * @return the {@code <vaadin-drawer-toggle>} as a {@code ButtonElement}
     */
    public ButtonElement getDrawerToggle() {
        return new ButtonElement(getLocator().locator(DRAWER_TOGGLE_TAG_NAME).first());
    }

    /**
     * Click the drawer toggle, flipping the drawer state.
     * <p>
     * In overlay mode the backdrop of an opened drawer covers the navbar and
     * therefore the toggle; prefer {@link #openDrawer()} and
     * {@link #closeDrawer()}, which pick a usable target for the current mode.
     */
    public void toggleDrawer() {
        getDrawerToggle().click();
    }

    // ── Drawer state ──────────────────────────────────────────────────

    /**
     * Whether the drawer is opened.
     *
     * @return {@code true} when the {@code drawer-opened} attribute is present
     */
    public boolean isDrawerOpened() {
        return getLocator().getAttribute("drawer-opened") != null;
    }

    /**
     * Open the drawer by clicking the drawer toggle, unless it is already opened.
     */
    public void openDrawer() {
        if (!isDrawerOpened()) {
            toggleDrawer();
        }
        assertDrawerOpened();
    }

    /**
     * Close the drawer, unless it is already closed.
     * <p>
     * Presses {@code Escape} in overlay mode, where the backdrop covers the
     * drawer toggle, and clicks the drawer toggle otherwise.
     */
    public void closeDrawer() {
        if (isDrawerOpened()) {
            if (isOverlayMode()) {
                closeDrawerWithEscape();
            } else {
                toggleDrawer();
            }
        }
        assertDrawerClosed();
    }

    /**
     * Set the drawer state, opening or closing it as needed.
     *
     * @param opened {@code true} to open the drawer, {@code false} to close it
     */
    public void setDrawerOpened(boolean opened) {
        if (opened) {
            openDrawer();
        } else {
            closeDrawer();
        }
    }

    /**
     * Click the backdrop, which closes the drawer in overlay mode.
     * <p>
     * The backdrop spans the whole viewport, including the part of it covered by
     * the opened drawer, so the click is aimed at the side of the backdrop the
     * drawer leaves free. The backdrop only receives pointer events while the
     * drawer is opened in overlay mode; clicking it in any other state fails.
     */
    public void clickBackdrop() {
        Locator backdrop = getBackdropLocator();
        BoundingBox backdropBox = backdrop.boundingBox();
        BoundingBox drawerBox = getLocator().locator("[part='drawer']").boundingBox();
        if (backdropBox == null || drawerBox == null) {
            backdrop.click();
            return;
        }
        double spaceBeforeDrawer = drawerBox.x - backdropBox.x;
        double spaceAfterDrawer = backdropBox.x + backdropBox.width - (drawerBox.x + drawerBox.width);
        double x = spaceAfterDrawer >= spaceBeforeDrawer ? backdropBox.width - 1 : 1;
        backdrop.click(new Locator.ClickOptions()
                .setPosition(x, backdropBox.height / 2));
    }

    /**
     * Press {@code Escape}, which closes the drawer in overlay mode.
     */
    public void closeDrawerWithEscape() {
        getLocator().page().keyboard().press("Escape");
    }

    /**
     * Assert that the drawer is opened.
     */
    public void assertDrawerOpened() {
        assertThat(getLocator()).hasAttribute("drawer-opened", "");
    }

    /**
     * Assert that the drawer is closed.
     */
    public void assertDrawerClosed() {
        assertThat(getLocator()).not().hasAttribute("drawer-opened", "");
    }

    // ── Layout mode ───────────────────────────────────────────────────

    /**
     * Whether the drawer is displayed as an overlay on top of the content,
     * which the component enables on small viewports.
     *
     * @return {@code true} when the {@code overlay} attribute is present
     */
    public boolean isOverlayMode() {
        return getLocator().getAttribute("overlay") != null;
    }

    /**
     * Assert that the layout is in overlay mode.
     */
    public void assertOverlayMode() {
        assertThat(getLocator()).hasAttribute("overlay", "");
    }

    /**
     * Assert that the layout is not in overlay mode.
     */
    public void assertNotOverlayMode() {
        assertThat(getLocator()).not().hasAttribute("overlay", "");
    }

    /**
     * Get the primary section, that is, the area that comes first visually.
     *
     * @return {@link #PRIMARY_SECTION_NAVBAR} or {@link #PRIMARY_SECTION_DRAWER}
     */
    public String getPrimarySection() {
        return getLocator().getAttribute("primary-section");
    }

    /**
     * Assert the primary section of the layout.
     *
     * @param primarySection the expected value, {@link #PRIMARY_SECTION_NAVBAR}
     *                       or {@link #PRIMARY_SECTION_DRAWER}
     */
    public void assertPrimarySection(String primarySection) {
        assertThat(getLocator()).hasAttribute("primary-section", primarySection);
    }

    // ── Presence of the areas ─────────────────────────────────────────

    /**
     * Assert that the layout has content slotted into the drawer.
     */
    public void assertHasDrawer() {
        assertThat(getLocator()).hasAttribute("has-drawer", "");
    }

    /**
     * Assert that the layout has content slotted into the navbar.
     */
    public void assertHasNavbar() {
        assertThat(getLocator()).hasAttribute("has-navbar", "");
    }

}
