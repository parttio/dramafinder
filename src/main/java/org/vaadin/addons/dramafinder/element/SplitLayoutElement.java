package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Mouse;
import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.query.ComponentQuery;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;
import org.vaadin.addons.dramafinder.element.utils.IdLocator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code vaadin-split-layout}.
 * <p>
 * Provides helpers to read/change orientation, access primary/secondary slotted
 * content and control splitter position. Primary/secondary content is exposed
 * via the {@code slot} attribute, and the splitter is moved by property or drag.
 */
@PlaywrightElement(SplitLayoutElement.FIELD_TAG_NAME)
public class SplitLayoutElement extends VaadinElement implements HasStyleElement, HasThemeElement,
        Findable<ComponentQuery<SplitLayoutElement>> {

    public static final String FIELD_TAG_NAME = "vaadin-split-layout";

    /**
     * Create a new {@code SplitLayoutElement}.
     */
    public SplitLayoutElement(Locator locator) {
        super(locator);
    }

    /**
     * Get the first split layout on the page.
     */
    public static SplitLayoutElement get(Page page) {
        return new SplitLayoutElement(page.locator(FIELD_TAG_NAME).first());
    }

    /**
     * Locator for the primary content (slot="primary").
     */
    public Locator getPrimaryLocator() {
        return getLocator().locator("[slot='primary']");
    }

    /**
     * Locator for the secondary content (slot="secondary").
     */
    public Locator getSecondaryLocator() {
        return getLocator().locator("[slot='secondary']");
    }

    /**
     * Locator for the splitter handle (shadow part="handle").
     */
    public Locator getHandleLocator() {
        return getLocator().locator("[part='handle']");
    }

    /**
     * Assert that the layout orientation is vertical.
     */
    public void assertVertical() {
        assertThat(getLocator()).hasAttribute("orientation", "vertical");
    }

    /**
     * Assert that the layout orientation is horizontal.
     */
    public void assertHorizontal() {
        assertThat(getLocator()).not().hasAttribute("orientation", "vertical");
    }

    /**
     * Drag the splitter by a delta offset in pixels.
     * Positive X moves to the right, positive Y moves down.
     */
    public void dragSplitterBy(double deltaX, double deltaY) {
        Locator splitter = getHandleLocator();
        var box = splitter.boundingBox();
        if (box == null) {
            return;
        }
        double centerX = box.x + box.width / 2;
        double centerY = box.y + box.height / 2;
        Page page = splitter.page();
        page.mouse().move(centerX, centerY);
        page.mouse().down();
        page.mouse().move(centerX + deltaX, centerY + deltaY,
                new Mouse.MoveOptions().setSteps(10));
        page.mouse().up();
        waitAfterAction();
    }

    // ── Lookup by id or test id ────────────────────────────────────────

    /**
     * Get a {@code SplitLayoutElement} by its {@code id}, searching the entire page.
     * <p>
     * A fallback for a component without a user-visible name: prefer the label or
     * text lookups when there is one. The id must match in full; two components
     * with that id fail with a strict-mode error.
     *
     * @param page the Playwright page
     * @param id   the full {@code id}
     * @return the matching {@code SplitLayoutElement}
     */
    public static SplitLayoutElement getById(Page page, String id) {
        return new SplitLayoutElement(IdLocator.byId(page, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code SplitLayoutElement} by its {@code id}, scoped to the given locator.
     * <p>
     * A fallback for a component without a user-visible name. The id must match in
     * full; two components with that id inside the scope fail with a strict-mode
     * error.
     *
     * @param locator the locator whose subtree is searched
     * @param id      the full {@code id}
     * @return the matching {@code SplitLayoutElement}
     */
    public static SplitLayoutElement getById(Locator locator, String id) {
        return new SplitLayoutElement(IdLocator.byId(locator, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code SplitLayoutElement} by its test id, searching the entire page.
     * <p>
     * The test id is Playwright's test id attribute, {@code data-testid} unless
     * reconfigured — the attribute Flow's {@code Component.setTestId(String)} sets.
     * A fallback for a component without a user-visible name; it must match in
     * full, and two components with that test id fail with a strict-mode error.
     *
     * @param page   the Playwright page
     * @param testId the full test id
     * @return the matching {@code SplitLayoutElement}
     */
    public static SplitLayoutElement getByTestId(Page page, String testId) {
        return new SplitLayoutElement(IdLocator.byTestId(page, FIELD_TAG_NAME, testId));
    }

    /**
     * Get a {@code SplitLayoutElement} by its test id, scoped to the given locator.
     * <p>
     * See {@link #getByTestId(Page, String)}; only components inside the scope are
     * considered.
     *
     * @param locator the locator whose subtree is searched
     * @param testId  the full test id
     * @return the matching {@code SplitLayoutElement}
     */
    public static SplitLayoutElement getByTestId(Locator locator, String testId) {
        return new SplitLayoutElement(IdLocator.byTestId(locator, FIELD_TAG_NAME, testId));
    }
}
