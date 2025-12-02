package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.shared.FocusableElement;
import org.vaadin.addons.dramafinder.element.shared.HasAriaLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.addons.dramafinder.element.shared.HasPrefixElement;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasSuffixElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;
import org.vaadin.addons.dramafinder.element.shared.HasTooltipElement;

/**
 * PlaywrightElement for {@code <vaadin-button>}.
 * <p>
 * Provides lookup helpers based on accessible name or visible text and
 * exposes common focus, aria-label, enablement, prefix/suffix and theming mixins.
 */
@PlaywrightElement(ButtonElement.FIELD_TAG_NAME)
public class ButtonElement extends VaadinElement
        implements FocusableElement, HasAriaLabelElement, HasEnabledElement,
        HasPrefixElement, HasStyleElement, HasSuffixElement, HasThemeElement, HasTooltipElement {

    public static final String FIELD_TAG_NAME = "vaadin-button";

    /**
     * Create a new {@code ButtonElement}.
     *
     * @param locator the locator for the {@code <vaadin-button>} element
     */
    public ButtonElement(Locator locator) {
        super(locator);
    }

    /**
     * Get a {@code ButtonElement} by its accessible name or visible text.
     *
     * @param page the Playwright page
     * @param text the button's accessible name or text content
     * @return the matching {@code ButtonElement}
     */
    public static ButtonElement getByText(Page page, String text) {
        return getByText(page, new Page.GetByRoleOptions().setName(text));
    }

    /**
     * Get a {@code ButtonElement} by its accessible name with custom role options.
     *
     * @param page    the Playwright page
     * @param options options for {@code getByRole}
     * @return the matching {@code ButtonElement}
     */
    public static ButtonElement getByText(Page page, Page.GetByRoleOptions options) {
        return new ButtonElement(
                page.getByRole(
                        AriaRole.BUTTON,
                        options
                ).and(page.locator(FIELD_TAG_NAME))
        );
    }

    /**
     * Get a {@code ButtonElement} by its accessible name or visible text within a scope.
     *
     * @param locator the scope containing the button (not the button itself)
     * @param text    the button's accessible name or text content
     * @return the matching {@code ButtonElement}
     */
    public static ButtonElement getByText(Locator locator, String text) {
        return getByText(locator, new Locator.GetByRoleOptions().setName(text));
    }

    /**
     * Get a {@code ButtonElement} by its accessible name with custom role options within a scope.
     *
     * @param locator the scope to search within
     * @param options options for {@code getByRole}
     * @return the matching {@code ButtonElement}
     */
    public static ButtonElement getByText(Locator locator, Locator.GetByRoleOptions options) {
        return new ButtonElement(
                locator.getByRole(
                        AriaRole.BUTTON,
                        options
                ).and(locator.page().locator(FIELD_TAG_NAME))
        );
    }

    /**
     * Alias for {@link #getByText(Page, String)}.
     */
    public static ButtonElement getByLabel(Page page, String text) {
        return getByText(page, text);
    }

}
