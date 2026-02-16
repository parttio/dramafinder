package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.shared.FocusableElement;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;
import org.vaadin.addons.dramafinder.element.shared.HasTooltipElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-avatar>}.
 * <p>
 * Provides helpers to read and modify avatar properties (name, abbreviation,
 * image, color index) and exposes common focus, style, theme and tooltip mixins.
 */
@PlaywrightElement(AvatarElement.FIELD_TAG_NAME)
public class AvatarElement extends VaadinElement
        implements FocusableElement, HasStyleElement, HasThemeElement, HasTooltipElement {

    public static final String FIELD_TAG_NAME = "vaadin-avatar";

    /**
     * Create a new {@code AvatarElement}.
     *
     * @param locator the locator for the {@code <vaadin-avatar>} element
     */
    public AvatarElement(Locator locator) {
        super(locator);
    }

    // ── Static Factory Methods ─────────────────────────────────────────

    /**
     * Get the first {@code AvatarElement} on the page.
     *
     * @param page the Playwright page
     * @return the first matching {@code AvatarElement}
     */
    public static AvatarElement get(Page page) {
        return new AvatarElement(page.locator(FIELD_TAG_NAME).first());
    }

    /**
     * Get the first {@code AvatarElement} within a scope.
     *
     * @param locator the scope containing the avatar (not the avatar itself)
     * @return the first matching {@code AvatarElement}
     */
    public static AvatarElement get(Locator locator) {
        return new AvatarElement(locator.locator(FIELD_TAG_NAME).first());
    }

    /**
     * Get an {@code AvatarElement} by its name attribute.
     *
     * @param page the Playwright page
     * @param name the avatar's name attribute value
     * @return the matching {@code AvatarElement}
     */
    public static AvatarElement getByName(Page page, String name) {
        return new AvatarElement(
                page.locator(FIELD_TAG_NAME + "[name='" + name + "']").first());
    }

    /**
     * Get an {@code AvatarElement} by its name attribute within a scope.
     *
     * @param locator the scope containing the avatar
     * @param name    the avatar's name attribute value
     * @return the matching {@code AvatarElement}
     */
    public static AvatarElement getByName(Locator locator, String name) {
        return new AvatarElement(
                locator.locator(FIELD_TAG_NAME + "[name='" + name + "']").first());
    }

    // ── Properties ─────────────────────────────────────────────────────

    /**
     * Get the avatar's name.
     *
     * @return the name, or {@code null} if not set
     */
    public String getName() {
        return (String) getProperty("name");
    }

    /**
     * Set the avatar's name.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        setProperty("name", name);
    }

    /**
     * Get the displayed abbreviation.
     *
     * @return the abbreviation, or {@code null} if not set
     */
    public String getAbbreviation() {
        return (String) getProperty("abbr");
    }

    /**
     * Set the abbreviation.
     *
     * @param abbr the abbreviation to display
     */
    public void setAbbreviation(String abbr) {
        setProperty("abbr", abbr);
    }

    /**
     * Get the image URL.
     *
     * @return the image URL, or {@code null} if not set
     */
    public String getImage() {
        return (String) getProperty("img");
    }

    /**
     * Set the image URL.
     *
     * @param img the image URL to set
     */
    public void setImage(String img) {
        setProperty("img", img);
    }

    /**
     * Get the background color index.
     *
     * @return the color index, or {@code null} if not set
     */
    public Integer getColorIndex() {
        Object value = getProperty("colorIndex");
        return value == null ? null : ((Number) value).intValue();
    }

    /**
     * Set the background color index.
     *
     * @param colorIndex the color index to set
     */
    public void setColorIndex(int colorIndex) {
        setProperty("colorIndex", colorIndex);
    }

    // ── Assertions ─────────────────────────────────────────────────────

    /**
     * Assert the avatar's name property value.
     *
     * @param name the expected name
     */
    public void assertName(String name) {
        assertThat(getLocator()).hasJSProperty("name", name);
    }

    /**
     * Assert the avatar's abbreviation.
     *
     * @param abbr the expected abbreviation
     */
    public void assertAbbreviation(String abbr) {
        assertThat(getLocator()).hasJSProperty("abbr", abbr);
    }

    /**
     * Assert that the avatar has an image set.
     */
    public void assertHasImage() {
        locator.page().waitForCondition(() -> getImage() != null);
    }

    /**
     * Assert that the avatar has no image set.
     */
    public void assertHasNoImage() {
        locator.page().waitForCondition(() -> getImage() == null);
    }
}
