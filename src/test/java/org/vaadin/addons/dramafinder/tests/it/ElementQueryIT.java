package org.vaadin.addons.dramafinder.tests.it;

import java.util.List;

import com.microsoft.playwright.PlaywrightException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.BadgeElement;
import org.vaadin.addons.dramafinder.element.BigDecimalFieldElement;
import org.vaadin.addons.dramafinder.element.BreadcrumbsElement;
import org.vaadin.addons.dramafinder.element.BreadcrumbsItemElement;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.addons.dramafinder.element.CheckboxElement;
import org.vaadin.addons.dramafinder.element.ComboBoxElement;
import org.vaadin.addons.dramafinder.element.ConfirmDialogElement;
import org.vaadin.addons.dramafinder.element.CustomFieldElement;
import org.vaadin.addons.dramafinder.element.DatePickerElement;
import org.vaadin.addons.dramafinder.element.DateTimePickerElement;
import org.vaadin.addons.dramafinder.element.DialogElement;
import org.vaadin.addons.dramafinder.element.EmailFieldElement;
import org.vaadin.addons.dramafinder.element.IntegerFieldElement;
import org.vaadin.addons.dramafinder.element.ListBoxElement;
import org.vaadin.addons.dramafinder.element.MenuBarElement;
import org.vaadin.addons.dramafinder.element.MenuItemElement;
import org.vaadin.addons.dramafinder.element.MultiSelectComboBoxElement;
import org.vaadin.addons.dramafinder.element.NumberFieldElement;
import org.vaadin.addons.dramafinder.element.SelectElement;
import org.vaadin.addons.dramafinder.element.SideNavigationElement;
import org.vaadin.addons.dramafinder.element.SideNavigationItemElement;
import org.vaadin.addons.dramafinder.element.TabElement;
import org.vaadin.addons.dramafinder.element.TextFieldElement;
import org.vaadin.addons.dramafinder.element.TimePickerElement;
import org.vaadin.addons.dramafinder.element.TooltipElement;
import org.vaadin.addons.dramafinder.element.query.InputFieldQuery;
import org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.vaadin.addons.dramafinder.element.query.ElementQueries.find;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ElementQueryIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "element-query";
    }

    @Test
    public void withLabel_matchesWholeLabelOnly() {
        TextFieldElement name = find(page, TextFieldElement.class).withLabel("Name").single();
        assertThat(name.getLocator()).hasAttribute("id", "name-field");
        name.setValue("Ada");
        name.assertValue("Ada");
    }

    @Test
    public void withLabelContaining_matchesSubstringIgnoringCase() {
        assertEquals(2, find(page, TextFieldElement.class).withLabelContaining("name").count());
    }

    @Test
    public void withLabel_ignoresAriaLabel() {
        assertFalse(find(page, TextFieldElement.class).withLabel("Search").exists());
    }

    @Test
    public void withAccessibleName_matchesAriaLabelAndVisibleLabel() {
        assertThat(find(page, TextFieldElement.class).withAccessibleName("Search").single().getLocator())
                .hasAttribute("placeholder", "Type to search");
        assertThat(find(page, TextFieldElement.class).withAccessibleName("Name").single().getLocator())
                .hasAttribute("id", "name-field");
    }

    @Test
    public void withPlaceholder() {
        assertEquals(1, find(page, TextFieldElement.class).withPlaceholder("Street").count());
        find(page, ComboBoxElement.class).withPlaceholder("Pick one").single().assertLabel("Country");
        assertEquals(1, find(page, IntegerFieldElement.class).withPlaceholder("Integer placeholder").count());
        assertEquals(1, find(page, NumberFieldElement.class).withPlaceholder("Number placeholder").count());
        assertEquals(1, find(page, BigDecimalFieldElement.class).withPlaceholder("Decimal placeholder").count());
        assertEquals(1, find(page, DatePickerElement.class).withPlaceholder("Date placeholder").count());
        assertEquals(1, find(page, TimePickerElement.class).withPlaceholder("Time placeholder").count());
        assertEquals(1, find(page, MultiSelectComboBoxElement.class).withPlaceholder("Tags placeholder").count());
    }

    @Test
    public void select_withLabel() {
        find(page, SelectElement.class).withLabel("Size").single().assertPlaceholder("Choose size");
    }

    @Test
    public void filtersCombine() {
        InputFieldQuery<TextFieldElement> names = find(page, TextFieldElement.class).withLabelContaining("name");
        assertEquals(1, names.withId("name-field").count());
        // filters return new queries: the original is unchanged
        assertEquals(2, names.count());
    }

    @Test
    public void withText_isExactAndCaseSensitive() {
        assertEquals(1, find(page, ButtonElement.class).withText("Save").count());
        assertEquals(0, find(page, ButtonElement.class).withText("save").count());
        assertEquals(2, find(page, ButtonElement.class).withTextContaining("save").count());
        assertEquals(1, find(page, ButtonElement.class).withText("Retry (2)?").count());
    }

    @Test
    public void button_withAccessibleName_findsIconOnlyAndTextButtons() {
        assertThat(find(page, ButtonElement.class).withAccessibleName("Close").single().getLocator())
                .hasAttribute("aria-label", "Close");
        assertEquals(1, find(page, ButtonElement.class).withAccessibleName("Save").count());
        assertEquals(2, find(page, ButtonElement.class).withAccessibleNameContaining("save").count());
        assertEquals(1, find(page, ButtonElement.class).withAccessibleNameContaining("clo").count());
    }

    @Test
    public void commonFilters() {
        assertEquals(1, find(page, ButtonElement.class).withTestId("save-btn").withText("Save").count());
        assertEquals(1, find(page, ButtonElement.class).withClassName("primary-action").withText("Save").count());
        assertEquals(1, find(page, ButtonElement.class).withAttribute("theme", "primary").withText("Save").count());
        assertEquals(1, find(page, ButtonElement.class).withAttribute("data-testid").count());
    }

    @Test
    public void scopedFind_fromElement() {
        find(page, ButtonElement.class).withText("Open dialog").single().click();
        // the dialog's footer holds a second "Delete" button once it is open
        assertThat(find(page, ButtonElement.class).withText("Delete").getLocator()).hasCount(2);

        DialogElement dialog = find(page, DialogElement.class).withAccessibleName("Delete account?").single();
        dialog.assertOpen();
        dialog.find(ButtonElement.class).withText("Delete").single().click();
        dialog.assertClosed();
    }

    @Test
    public void scopedFind_fromLocator() {
        assertEquals(2, find(page.locator("#form"), CheckboxElement.class).count());
    }

    @Test
    public void confirmDialog_withAccessibleName() {
        find(page, ButtonElement.class).withText("Open confirm").single().click();
        find(page, ConfirmDialogElement.class).withAccessibleName("Discard changes?").single().assertOpen();
    }

    @Test
    public void terminals() {
        List<CheckboxElement> checkboxes = find(page, CheckboxElement.class).all();
        assertEquals(2, checkboxes.size());
        checkboxes.get(1).assertLabel("Subscribe");
        find(page, CheckboxElement.class).get(1).assertLabel("Subscribe");
        find(page, CheckboxElement.class).first().assertLabel("Accept terms");
        assertTrue(find(page, CheckboxElement.class).withLabel("Accept terms").exists());
        assertFalse(find(page, CheckboxElement.class).withLabel("Accept").exists());
        assertThat(find(page, CheckboxElement.class).getLocator()).hasCount(2);
    }

    @Test
    public void single_failsOnSeveralMatches() {
        ButtonElement ambiguous = find(page, ButtonElement.class).withTextContaining("Save").single();
        PlaywrightException e = assertThrows(PlaywrightException.class, ambiguous::click);
        assertTrue(e.getMessage().contains("strict mode violation"), e.getMessage());
    }

    @Test
    public void subclass_returnsRequestedClass() {
        assertEquals(1, find(page, TextFieldElement.class).withLabel("Name").count());
        TextFieldElement email = find(page, EmailFieldElement.class).withLabel("Email").single();
        assertInstanceOf(EmailFieldElement.class, email);
        assertThat(email.getLocator()).isVisible();
    }

    @Test
    public void withText_readsOwnTextOnly() {
        // icon slot, shadow-rendered number and a slotted glyph are not part of the text
        assertEquals(1, find(page, BadgeElement.class).withText("New").count());
        assertEquals(1, find(page, BadgeElement.class).withText("Inbox").count());
        assertEquals(1, find(page, BadgeElement.class).withText("Verified").count());
        assertEquals(2, find(page, BadgeElement.class).withTextContaining("new").count());
        assertEquals(0, find(page, BadgeElement.class).withTextContaining("5").count());
        // the tooltip slotted into the button is not part of its text either
        assertEquals(1, find(page, ButtonElement.class).withText("Save").count());
        assertEquals(1, find(page, BreadcrumbsItemElement.class).withText("Home").count());
    }

    @Test
    public void menuItems_byTextAndName() {
        assertEquals(1, find(page, MenuItemElement.class).withText("File").count());
        assertEquals(1, find(page, MenuItemElement.class).withAccessibleName("Edit").count());
        // the hidden overflow button ("More options") is not an item
        assertEquals(0, find(page, MenuItemElement.class).withAccessibleName("More options").count());
        assertEquals(1, find(page, MenuBarElement.class).withAccessibleName("Main menu").count());
    }

    @Test
    public void tooltip_withText() {
        assertEquals(1, find(page, TooltipElement.class).withText("Saves the form").count());
        assertEquals(1, find(page, TooltipElement.class).withTextContaining("saves").count());
        assertEquals(1, find(page, TooltipElement.class).withAccessibleName("Saves the form").count());
    }

    @Test
    public void withAccessibleName_onEveryType() {
        assertEquals(1, find(page, TabElement.class).withAccessibleName("Home").count());
        assertEquals(1, find(page, BreadcrumbsElement.class).withAccessibleName("Trail").count());
        assertEquals(1, find(page, ListBoxElement.class).withAccessibleName("Sizes").count());
        assertEquals(1, find(page, SideNavigationElement.class).withAccessibleName("Main nav").count());
        assertEquals(1, find(page, DateTimePickerElement.class).withAccessibleName("Appointment").count());
    }

    @Test
    public void withAccessibleName_ignoresNestedComponents() {
        assertEquals(1, find(page, CustomFieldElement.class).withAccessibleName("Phone").count());
        assertEquals(0, find(page, CustomFieldElement.class).withAccessibleName("Number").count());
        assertEquals(1, find(page, TextFieldElement.class).withAccessibleName("Number").count());

        find(page, ButtonElement.class).withText("Open dialog").single().click();
        find(page, DialogElement.class).withAccessibleName("Delete account?").single().assertOpen();
        assertThat(find(page, TextFieldElement.class).withAccessibleName("Reason").getLocator()).hasCount(1);
        assertEquals(0, find(page, DialogElement.class).withAccessibleName("Reason").count());
    }

    @Test
    public void sideNav_withLabel() {
        assertEquals(1, find(page, SideNavigationElement.class).withLabel("Main nav").count());
        // a parent item is labelled by its own text, not its children's
        assertEquals(1, find(page, SideNavigationItemElement.class).withLabel("Dashboard").count());
        assertEquals(1, find(page, SideNavigationItemElement.class).withLabel("Reports").count());
        assertEquals(1, find(page, SideNavigationItemElement.class).withLabel("Dash").count());
        assertEquals(2, find(page, SideNavigationItemElement.class).withLabelContaining("dash").count());
    }

    @Test
    public void textQuery_onTabs() {
        assertEquals(1, find(page, TabElement.class).withText("Home").count());
        assertEquals(2, find(page, TabElement.class).withTextContaining("home").count());
    }
}
