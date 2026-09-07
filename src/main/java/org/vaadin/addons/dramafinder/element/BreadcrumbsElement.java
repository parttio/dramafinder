package org.vaadin.addons.dramafinder.element;

import java.util.List;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.shared.HasAriaLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-breadcrumbs>}.
 * <p>
 * The trail is made of {@code <vaadin-breadcrumbs-item>} light-DOM children,
 * exposed as {@link BreadcrumbsItemElement}. When the trail does not fit, the
 * component sets the {@code has-overflow} attribute, reveals an
 * {@code [part='overflow-button']} and moves the collapsed items to
 * {@code slot="overlay"}; those items stay children of the host, so
 * {@link #getItems()} covers both the inline and the overflowing set.
 * <p>
 * The component is rendered with the ARIA role {@code navigation}, which
 * {@link #getByLabel(Page, String)} uses for lookup.
 */
@PlaywrightElement(BreadcrumbsElement.FIELD_TAG_NAME)
public class BreadcrumbsElement extends VaadinElement
        implements HasAriaLabelElement, HasStyleElement, HasThemeElement {

    public static final String FIELD_TAG_NAME = "vaadin-breadcrumbs";

    private static final String OVERFLOW_BUTTON_PART = "[part='overflow-button']";

    /**
     * Create a new {@code BreadcrumbsElement}.
     *
     * @param locator the locator for the {@code <vaadin-breadcrumbs>} element
     */
    public BreadcrumbsElement(Locator locator) {
        super(locator);
    }

    // --- Item access ---

    /**
     * Locator for every {@code <vaadin-breadcrumbs-item>} of the trail,
     * including the items collapsed into the overflow overlay.
     *
     * @return locator matching all items of the trail
     */
    public Locator getItemsLocator() {
        return getLocator().locator(BreadcrumbsItemElement.FIELD_TAG_NAME);
    }

    /**
     * Get every item of the trail, in trail order, including the items
     * collapsed into the overflow overlay.
     *
     * @return the items of the trail, empty when there are none
     */
    public List<BreadcrumbsItemElement> getItems() {
        return getItemsLocator().all().stream().map(BreadcrumbsItemElement::new).toList();
    }

    /**
     * Get the item at the given position in the trail.
     *
     * @param index zero-based index
     * @return the item at that index
     */
    public BreadcrumbsItemElement getItem(int index) {
        return new BreadcrumbsItemElement(getItemsLocator().nth(index));
    }

    /**
     * Get the item showing the given text. The text is matched in full, so
     * {@code "Products"} does not match an item reading {@code "Products archive"}.
     *
     * @param text the exact text of the item
     * @return the first matching item; its locator matches nothing when no item
     *         has that text
     */
    public BreadcrumbsItemElement getItem(String text) {
        return new BreadcrumbsItemElement(getItemsLocator()
                .and(getLocator().getByText(text, new Locator.GetByTextOptions().setExact(true)))
                .first());
    }

    /**
     * Get the item marked as the current page, that is, the last item of the
     * trail when it has no path.
     *
     * @return the current item; its locator matches nothing when the trail has
     *         no current item
     */
    public BreadcrumbsItemElement getCurrentItem() {
        return new BreadcrumbsItemElement(getLocator()
                .locator(BreadcrumbsItemElement.FIELD_TAG_NAME + "[current]").first());
    }

    /**
     * Assert that the trail holds exactly the expected number of items.
     *
     * @param count the expected item count
     */
    public void assertItemCount(int count) {
        assertThat(getItemsLocator()).hasCount(count);
    }

    /**
     * Assert that the trail shows exactly the given texts, in trail order.
     *
     * @param texts the expected item texts
     */
    public void assertItemTexts(String... texts) {
        assertThat(getItemsLocator()).hasText(texts);
    }

    // --- Overflow ---

    /**
     * Locator for the button that reveals the collapsed items. The button is
     * hidden unless the trail overflows.
     *
     * @return locator for the overflow button
     */
    public Locator getOverflowButtonLocator() {
        return getLocator().locator(OVERFLOW_BUTTON_PART);
    }

    /**
     * Whether one or more items are collapsed into the overflow overlay.
     *
     * @return {@code true} when the {@code has-overflow} attribute is set
     */
    public boolean hasOverflow() {
        return getLocator().getAttribute("has-overflow") != null;
    }

    /**
     * Assert that items are collapsed into the overflow overlay.
     */
    public void assertHasOverflow() {
        assertThat(getLocator()).hasAttribute("has-overflow", "");
    }

    /**
     * Assert that the whole trail fits and nothing is collapsed.
     */
    public void assertHasNoOverflow() {
        assertThat(getLocator()).not().hasAttribute("has-overflow", "");
    }

    /**
     * Locator for the items collapsed into the overflow overlay.
     *
     * @return locator matching the overflowing items, empty when the trail fits
     */
    public Locator getOverflowItemsLocator() {
        return getLocator().locator(
                BreadcrumbsItemElement.FIELD_TAG_NAME + "[slot='overlay']");
    }

    /**
     * Get the items collapsed into the overflow overlay, in trail order.
     *
     * @return the overflowing items, empty when the trail fits
     */
    public List<BreadcrumbsItemElement> getOverflowItems() {
        return getOverflowItemsLocator().all().stream().map(BreadcrumbsItemElement::new).toList();
    }

    /**
     * Whether the overflow overlay is open.
     *
     * @return {@code true} when the overflow button reports {@code aria-expanded="true"}
     */
    public boolean isOverflowOpen() {
        return "true".equals(getOverflowButtonLocator().getAttribute("aria-expanded"));
    }

    /**
     * Open the overflow overlay, revealing the collapsed items. Does nothing
     * when the overlay is already open.
     */
    public void openOverflow() {
        if (!isOverflowOpen()) {
            getOverflowButtonLocator().click();
        }
    }

    /**
     * Close the overflow overlay by pressing {@code Escape}. Does nothing when
     * the overlay is already closed.
     * <p>
     * The open overlay captures pointer events, so the overlay cannot be closed
     * by clicking the overflow button a second time.
     */
    public void closeOverflow() {
        if (isOverflowOpen()) {
            getLocator().page().keyboard().press("Escape");
        }
    }

    /**
     * Assert that the overflow overlay is open.
     */
    public void assertOverflowOpen() {
        assertThat(getOverflowButtonLocator()).hasAttribute("aria-expanded", "true");
    }

    /**
     * Assert that the overflow overlay is closed.
     */
    public void assertOverflowClosed() {
        assertThat(getOverflowButtonLocator()).hasAttribute("aria-expanded", "false");
    }

    /**
     * Assert the accessible name of the overflow button, which comes from
     * {@code BreadcrumbsI18n.moreItems}.
     *
     * @param label the expected accessible name
     */
    public void assertOverflowButtonAriaLabel(String label) {
        assertThat(getOverflowButtonLocator()).hasAttribute("aria-label", label);
    }

    // --- Factory methods ---

    /**
     * Get the first {@code <vaadin-breadcrumbs>} on the page.
     *
     * @param page the Playwright page
     * @return the first {@code BreadcrumbsElement}
     */
    public static BreadcrumbsElement get(Page page) {
        return new BreadcrumbsElement(page.locator(FIELD_TAG_NAME).first());
    }

    /**
     * Get the first {@code <vaadin-breadcrumbs>} within a locator scope.
     *
     * @param locator the scope to search within
     * @return the first {@code BreadcrumbsElement}
     */
    public static BreadcrumbsElement get(Locator locator) {
        return new BreadcrumbsElement(locator.locator(FIELD_TAG_NAME).first());
    }

    /**
     * Get the {@code <vaadin-breadcrumbs>} by its accessible name, using the
     * ARIA role {@code navigation}.
     *
     * @param page  the Playwright page
     * @param label the accessible label of the breadcrumbs
     * @return the matching {@code BreadcrumbsElement}
     */
    public static BreadcrumbsElement getByLabel(Page page, String label) {
        return new BreadcrumbsElement(
                page.getByRole(AriaRole.NAVIGATION, new Page.GetByRoleOptions().setName(label))
                        .and(page.locator(FIELD_TAG_NAME)).first());
    }
}
