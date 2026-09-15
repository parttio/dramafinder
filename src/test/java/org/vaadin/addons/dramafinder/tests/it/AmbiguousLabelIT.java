package org.vaadin.addons.dramafinder.tests.it;

import java.util.List;

import com.microsoft.playwright.PlaywrightException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.HasTestView;
import org.vaadin.addons.dramafinder.element.AccordionElement;
import org.vaadin.addons.dramafinder.element.CheckboxGroupElement;
import org.vaadin.addons.dramafinder.element.ComboBoxElement;
import org.vaadin.addons.dramafinder.element.ContextMenuElement;
import org.vaadin.addons.dramafinder.element.ListBoxElement;
import org.vaadin.addons.dramafinder.element.MenuBarElement;
import org.vaadin.addons.dramafinder.element.RadioButtonGroupElement;
import org.vaadin.addons.dramafinder.element.SelectElement;
import org.vaadin.addons.dramafinder.element.SideNavigationElement;
import org.vaadin.addons.dramafinder.element.SideNavigationItemElement;
import org.vaadin.addons.dramafinder.element.TabsElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression coverage for issue #149: every container on the view holds an item
 * whose label is a prefix of an earlier item's label, so a case-insensitive
 * substring lookup followed by {@code .first()} silently resolves to the wrong
 * item — and the assertion keyed on the same lookup then passes against it.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AmbiguousLabelIT extends SpringPlaywrightIT implements HasTestView {

    @Override
    public String getView() {
        return "ambiguous-label";
    }

    @Test
    public void checkboxGroupSelectsTheShorterLabel() {
        CheckboxGroupElement group = CheckboxGroupElement.getByLabel(page, "Ambiguous Checkbox Group");

        group.selectByLabel("Option 1");

        assertEquals(List.of("Option 1"), group.getSelectedValues());
        group.assertSelected("Option 1");
        group.getCheckbox("Option 10").assertNotChecked();
    }

    @Test
    public void duplicateCheckboxLabelsFailLoudly() {
        CheckboxGroupElement group = CheckboxGroupElement.getByLabel(page, "Duplicate Checkbox Group");
        group.assertCheckboxCount(2);

        PlaywrightException exception =
                assertThrows(PlaywrightException.class, () -> group.selectByLabel("Twin"));
        assertTrue(exception.getMessage().contains("strict mode violation"), exception.getMessage());
    }

    @Test
    public void radioButtonGroupSelectsTheShorterLabel() {
        RadioButtonGroupElement group = RadioButtonGroupElement.getByLabel(page, "Ambiguous Radio Group");

        group.selectByLabel("Option 1");

        group.assertValue("Option 1");
    }

    @Test
    public void tabsSelectTheShorterLabel() {
        TabsElement tabs = TabsElement.getById(page, "ambiguous-tabs");

        tabs.selectTab("Tab 1");

        assertEquals(1, tabs.getSelectedIndex());
        tabs.assertSelectedTab("Tab 1");
    }

    @Test
    public void comboBoxSelectsTheShorterLabel() {
        ComboBoxElement comboBox = ComboBoxElement.getByLabel(page, "Ambiguous Combo Box");

        comboBox.selectItem("Option 1");

        comboBox.assertValue("Option 1");
    }

    @Test
    public void selectSelectsTheShorterLabel() {
        SelectElement select = SelectElement.getByLabel(page, "Ambiguous Select");

        select.selectItem("Option 1");

        select.assertValue("Option 1");
    }

    @Test
    public void listBoxSelectsTheShorterLabel() {
        ListBoxElement listBox = ListBoxElement.getByLabel(page, "Ambiguous List Box");

        listBox.selectItem("Option 1");

        listBox.assertSelectedValue("Option 1");
    }

    @Test
    public void menuBarClicksTheShorterLabel() {
        MenuBarElement menuBar = new MenuBarElement(page);

        menuBar.getMenuItemElement("Save").click();

        assertThat(page.locator("#menu-bar-clicked")).hasText("Save");
    }

    @Test
    public void contextMenuSelectsTheShorterLabel() {
        ContextMenuElement contextMenu = new ContextMenuElement(page);
        ContextMenuElement.openOn(page.locator("#context-menu-target"));

        contextMenu.selectItem("Delete");

        assertThat(page.locator("#context-menu-clicked")).hasText("Delete");
    }

    @Test
    public void sideNavFindsTheShorterLabel() {
        SideNavigationElement nav = SideNavigationElement.getByLabel(page, "Ambiguous Nav");

        SideNavigationItemElement itemOne = nav.getItem("Item 1");
        itemOne.assertLabel("Item 1");
    }

    @Test
    public void sideNavFindsAParentByItsOwnLabel() {
        SideNavigationElement nav = SideNavigationElement.getByLabel(page, "Ambiguous Nav");

        // Resolves to the parent, not to one of its children: the parent's own
        // label is "Section" even though its subtree text is "Section Section 1
        // Section 2".
        SideNavigationItemElement section = nav.getItem("Section");
        section.assertExpanded();
        assertThat(section.getLocator()).hasAttribute("has-children", "");

        nav.getItem("Section 1").assertLabel("Section 1");
    }

    @Test
    public void accordionOpensTheShorterSummary() {
        AccordionElement accordion = new AccordionElement(page.locator("vaadin-accordion"));

        accordion.openPanel("Panel 1");

        accordion.assertPanelOpened("Panel 1");
        accordion.assertPanelClosed("Panel 10");
    }
}
