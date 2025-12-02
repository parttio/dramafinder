package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code vaadin-split-layout}.
 * <p>
 * Provides helpers to read/change orientation, access primary/secondary slotted
 * content and control splitter position. Primary/secondary content is exposed
 * via the {@code slot} attribute, and the splitter is moved by property or drag.
 */
@PlaywrightElement(SplitLayoutElement.FIELD_TAG_NAME)
public class SplitLayoutElement extends VaadinElement implements HasStyleElement, HasThemeElement {

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
        double startX = box.x + box.width / 2;
        double startY = box.y + box.height / 2;
        Page page = splitter.page();
        page.mouse().move(startX, startY);
        page.mouse().down();
        page.mouse().move(startX + deltaX, startY + deltaY);
        page.mouse().up();
    }

}
