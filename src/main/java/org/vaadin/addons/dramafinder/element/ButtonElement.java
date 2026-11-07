package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.query.ButtonQuery;
import org.vaadin.addons.dramafinder.element.query.ElementQueries;
import org.vaadin.addons.dramafinder.element.query.Findable;
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
        HasPrefixElement, HasStyleElement, HasSuffixElement, HasThemeElement, HasTooltipElement,
        Findable<ButtonQuery<ButtonElement>> {

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
     * <p>
     * The name is the {@code aria-label} when set, otherwise the button's own text; a
     * tooltip on the button is never part of it. It must match in full,
     * case-sensitively, and two matching buttons fail with a strict-mode error. Use
     * {@link #getByTextContaining(Page, String)} to match part of it.
     *
     * @param page the Playwright page
     * @param text the button's full accessible name or text content
     * @return the matching {@code ButtonElement}
     */
    public static ButtonElement getByText(Page page, String text) {
        return ElementQueries.find(page, ButtonElement.class).withAccessibleName(text).single();
    }

    /**
     * Get a {@code ButtonElement} whose accessible name or own text contains
     * {@code text}, ignoring the case of ASCII letters. Several matching buttons
     * fail with a strict-mode error.
     *
     * @param page the Playwright page
     * @param text part of the button's accessible name or text content
     * @return the matching {@code ButtonElement}
     */
    public static ButtonElement getByTextContaining(Page page, String text) {
        return ElementQueries.find(page, ButtonElement.class).withAccessibleNameContaining(text).single();
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
     * <p>
     * The name is the {@code aria-label} when set, otherwise the button's own text; a
     * tooltip on the button is never part of it. It must match in full,
     * case-sensitively, and two matching buttons fail with a strict-mode error. Use
     * {@link #getByTextContaining(Locator, String)} to match part of it.
     *
     * @param locator the scope containing the button (not the button itself)
     * @param text    the button's full accessible name or text content
     * @return the matching {@code ButtonElement}
     */
    public static ButtonElement getByText(Locator locator, String text) {
        return ElementQueries.find(locator, ButtonElement.class).withAccessibleName(text).single();
    }

    /**
     * Get a {@code ButtonElement} within a scope whose accessible name or own text
     * contains {@code text}, ignoring the case of ASCII letters. Several matching
     * buttons fail with a strict-mode error.
     *
     * @param locator the scope containing the button (not the button itself)
     * @param text    part of the button's accessible name or text content
     * @return the matching {@code ButtonElement}
     */
    public static ButtonElement getByTextContaining(Locator locator, String text) {
        return ElementQueries.find(locator, ButtonElement.class).withAccessibleNameContaining(text).single();
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

    /**
     * Alias for {@link #getByTextContaining(Page, String)}.
     *
     * @param page the Playwright page
     * @param text part of the button's accessible name or text content
     * @return the matching {@code ButtonElement}
     */
    public static ButtonElement getByLabelContaining(Page page, String text) {
        return getByTextContaining(page, text);
    }

}
