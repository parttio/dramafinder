package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.addons.dramafinder.element.CheckboxElement;
import org.vaadin.addons.dramafinder.element.SelectElement;
import org.vaadin.addons.dramafinder.element.TabElement;
import org.vaadin.addons.dramafinder.element.TextFieldElement;
import org.vaadin.addons.dramafinder.element.TooltipElement;
import org.vaadin.addons.dramafinder.element.VaadinElement;
import org.vaadin.addons.dramafinder.element.query.ElementQuery;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.vaadin.addons.dramafinder.element.query.ElementQueries.find;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AccessibleNameIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "accessible-name";
    }

    @Test
    public void field_isNamedByItsLabel() {
        assertEquals("Name", TextFieldElement.getById(page, "label-field").getAccessibleName());
    }

    @Test
    public void field_isNamedByItsAccessibleName() {
        assertEquals("Search", TextFieldElement.getById(page, "aria-label-field").getAccessibleName());
    }

    @Test
    public void field_isNamedByItsAccessibleNameRef() {
        assertEquals("Billing address", TextFieldElement.getById(page, "labelledby-field").getAccessibleName());
    }

    @Test
    public void placeholderIsNotAName() {
        assertNull(TextFieldElement.getById(page, "unnamed-field").getAccessibleName());
    }

    @Test
    public void severalReferences_areJoinedWithASpace() {
        assertEquals("Shipping options", VaadinElement.of(page.locator("#multi-ref")).getAccessibleName());
    }

    @Test
    public void checkbox_isNamedByItsLabel() {
        assertEquals("Accept terms", CheckboxElement.getById(page, "terms").getAccessibleName());
    }

    @Test
    public void select_withoutLabel_isNamedByItsPlaceholder() {
        assertEquals("Choose size", SelectElement.getById(page, "placeholder-select").getAccessibleName());
    }

    @Test
    public void button_isNamedByItsOwnTextWithoutItsTooltip() {
        ButtonElement save = ButtonElement.getById(page, "save-button");
        save.getLocator().hover();
        assertEquals("Save", save.getAccessibleName());
    }

    @Test
    public void iconButton_isNamedByItsAriaLabel() {
        assertEquals("Close", ButtonElement.getById(page, "close-button").getAccessibleName());
        assertNull(ButtonElement.getById(page, "unnamed-button").getAccessibleName());
    }

    @Test
    public void tab_isNamedByItsOwnText() {
        assertEquals("Home", TabElement.getById(page, "home-tab").getAccessibleName());
    }

    @Test
    public void tooltip_isNamedByItsText() {
        assertEquals("Saves the form",
                TooltipElement.get(ButtonElement.getById(page, "save-button").getLocator()).getAccessibleName());
    }

    @Test
    public void assertAccessibleName_waitsForTheName() {
        TextFieldElement field = TextFieldElement.getById(page, "label-field");
        field.assertAccessibleName("Name");
        ButtonElement.getById(page, "rename").click();
        field.assertAccessibleName("Full name");
        ButtonElement.getById(page, "close-button").assertAccessibleName("Close");
    }

    @Test
    public void assertAccessibleName_null_assertsNoName() {
        TextFieldElement.getById(page, "unnamed-field").assertAccessibleName(null);
        page.setDefaultTimeout(1000);
        assertThrows(AssertionError.class, () -> TextFieldElement.getById(page, "label-field").assertAccessibleName(null));
    }

    @Test
    public void assertAccessibleName_failureShowsTheActualName() {
        page.setDefaultTimeout(1000);
        AssertionError error = assertThrows(AssertionError.class,
                () -> TabElement.getById(page, "home-tab").assertAccessibleName("Settings"));
        assertEquals("Accessible name mismatch. Expected: <Settings>, actual: <Home>", error.getMessage());
    }

    @Test
    public void everyNameRead_findsTheComponentBackWithAccessibleName() {
        assertFoundByItsName(TextFieldElement.class, TextFieldElement.getById(page, "label-field"));
        assertFoundByItsName(TextFieldElement.class, TextFieldElement.getById(page, "aria-label-field"));
        assertFoundByItsName(TextFieldElement.class, TextFieldElement.getById(page, "labelledby-field"));
        assertFoundByItsName(CheckboxElement.class, CheckboxElement.getById(page, "terms"));
        assertFoundByItsName(SelectElement.class, SelectElement.getById(page, "placeholder-select"));
        assertFoundByItsName(ButtonElement.class, ButtonElement.getById(page, "close-button"));
        assertFoundByItsName(TabElement.class, TabElement.getById(page, "home-tab"));
        ButtonElement save = ButtonElement.getById(page, "save-button");
        assertFoundByItsName(TooltipElement.class, TooltipElement.get(save.getLocator()));
        save.getLocator().hover();
        assertFoundByItsName(ButtonElement.class, save);
    }

    private static <T extends VaadinElement & Findable<Q>, Q extends ElementQuery<? super T, Q>> void assertFoundByItsName(
            Class<T> type, T element) {
        String name = element.getAccessibleName();
        Q byName = find(element.getLocator().page(), type).withAccessibleName(name);
        assertThat(byName.getLocator()).hasCount(1);
        assertThat(byName.getLocator().and(element.getLocator())).hasCount(1);
    }
}
