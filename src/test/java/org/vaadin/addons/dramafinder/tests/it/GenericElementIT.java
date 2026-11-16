package org.vaadin.addons.dramafinder.tests.it;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.addons.dramafinder.element.GenericElement;
import org.vaadin.addons.dramafinder.element.TextFieldElement;
import org.vaadin.addons.dramafinder.element.VaadinElement;
import org.vaadin.addons.dramafinder.element.shared.FocusableElement;
import org.vaadin.addons.dramafinder.element.shared.HasDisabledAttributeElement;
import org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.vaadin.addons.dramafinder.element.query.ElementQueries.find;

/**
 * A generic element for any tag (#269, #270, #271), queries by tag (#273),
 * {@code withTheme} (#274), {@code withPlaceholderContaining} (#275) and the
 * light-DOM default of the queries (#276), focus and enabled state (#296).
 */
public class GenericElementIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "generic-element";
    }

    @Test
    public void genericElementReadsTagStyleClassAndTheme() {
        GenericElement styled = VaadinElement.of(page.locator("#styled"));
        assertEquals("div", styled.getTagName());
        assertEquals("red", styled.getInlineStyle("background-color"));
        assertEquals("blue", styled.getInlineStyle("--my-color"));
        assertNull(styled.getInlineStyle("color"));
        assertEquals("rgb(255, 0, 0)", styled.getComputedStyle("background-color"));
        assertEquals("blue", styled.getComputedStyle("--my-color"));
        assertEquals("block", styled.getComputedStyle("display"));
        styled.assertCssClass("box");
        styled.assertTheme("compact");
        styled.assertVisible();
    }

    @Test
    public void everyElementHasItsTagName() {
        assertEquals("vaadin-button", ButtonElement.getByText(page, "Plain").getTagName());
        assertEquals("input", VaadinElement.of(TextFieldElement.getById(page, "search").getInputLocator())
                .getTagName());
    }

    @Test
    public void lightDomLookupsReturnGenericElements() {
        List<VaadinElement> children = VaadinElement.of(page.locator("#styled")).getLightDomChildren("span");
        assertEquals(1, children.size());
        assertInstanceOf(GenericElement.class, children.get(0));
    }

    @Test
    public void genericElementFocusesTheHost() {
        GenericElement panel = VaadinElement.of(page.locator("#panel"));
        panel.assertIsNotFocused();
        panel.focus();
        panel.assertIsFocused();
        assertEquals("0", panel.getTabIndex());
        panel.blur();
        panel.assertIsNotFocused();
    }

    @Test
    public void genericElementReadsEnabledStateFromTheDisabledAttribute() {
        GenericElement panel = find(page, "div").withId("panel").single();
        assertTrue(panel.isEnabled());
        panel.assertEnabled();

        ButtonElement.getByText(page, "Toggle panel").click();
        panel.assertDisabled();
        assertFalse(panel.isEnabled());

        ButtonElement.getByText(page, "Toggle panel").click();
        panel.assertEnabled();

        GenericElement disabled = VaadinElement.of(page.locator("#disabled-panel"));
        assertFalse(disabled.isEnabled());
        disabled.assertDisabled();
    }

    @Test
    public void lightDomLookupsReturnFocusableElementsWithEnabledState() {
        VaadinElement child = VaadinElement.of(page.locator("#disabled-panel").locator("xpath=..")).getLightDomChildren("div").stream()
                .filter(element -> "disabled-panel".equals(element.getLocator().getAttribute("id")))
                .findFirst().orElseThrow();
        assertInstanceOf(FocusableElement.class, child);
        assertInstanceOf(HasDisabledAttributeElement.class, child);
        ((HasDisabledAttributeElement) child).assertDisabled();
    }

    @Test
    public void findByTagOffersLabelTextAndPlaceholderFilters() {
        assertThat(find(page, "x-card").getLocator()).hasCount(2);
        assertThat(find(page, "X-CARD").withLabel("Card label").getLocator()).hasCount(1);
        assertThat(find(page, "x-card").withLabel("Card").getLocator()).hasCount(0);
        assertThat(find(page, "x-card").withLabelContaining("card").getLocator()).hasCount(1);
        assertThat(find(page, "x-card").withText("Other body").getLocator()).hasCount(1);
        assertThat(find(page, "x-card").withPlaceholder("Card hint").getLocator()).hasCount(1);
        assertThat(find(page, "x-card").withPlaceholderContaining("HINT").getLocator()).hasCount(1);

        GenericElement card = find(page, "x-card").withLabel("Card label").single();
        assertEquals("x-card", card.getTagName());
        assertThat(find(page.locator("main"), "x-card").getLocator()).hasCount(2);
        assertThat(find(page, "vaadin-button").withText("Plain").getLocator()).hasCount(1);
        assertThrows(IllegalArgumentException.class, () -> find(page, "not a tag"));
    }

    @Test
    public void withThemeMatchesOneVariantAsAWholeWord() {
        assertThat(find(page, ButtonElement.class).withTheme("primary").single().getLocator()).hasText("Primary");
        assertThat(find(page, ButtonElement.class).withTheme("small").getLocator()).hasCount(1);
        assertThat(find(page, ButtonElement.class).withTheme("prim").getLocator()).hasCount(0);
        assertThat(find(page, "div").withTheme("compact").getLocator()).hasCount(1);
    }

    @Test
    public void withPlaceholderContainingIsACaseInsensitiveSubstring() {
        TextFieldElement search = find(page, TextFieldElement.class).withPlaceholderContaining("ORDERS").single();
        assertThat(search.getLocator()).hasId("search");
        assertThat(find(page, TextFieldElement.class).withPlaceholder("orders").getLocator()).hasCount(0);
    }

    @Test
    public void queriesLeaveOutShadowRootsUnlessAskedTo() {
        // every date picker renders an input container in its shadow root
        assertThat(page.locator("vaadin-input-container").first()).isAttached();
        assertThat(find(page, "vaadin-input-container").getLocator()).hasCount(0);
        assertTrue(find(page, "vaadin-input-container").includingShadowDom().count() > 0);

        // an application's own shadow root needs the opt-out too
        assertThat(find(page, ButtonElement.class).withText("In shadow").getLocator()).hasCount(0);
        assertThat(find(page, ButtonElement.class).includingShadowDom().withText("In shadow").getLocator())
                .hasCount(1);
        assertThrows(IllegalStateException.class,
                () -> find(page, ButtonElement.class).withText("Plain").includingShadowDom());
    }
}
