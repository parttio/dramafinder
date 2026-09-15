package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Every container here holds one item whose label is a prefix of another item's
 * label, with the longer one first in DOM order, so a substring lookup for the
 * shorter label resolves to the wrong item. See {@code AmbiguousLabelIT}.
 */
@PageTitle("Ambiguous Label Demo")
@Route(value = "ambiguous-label", layout = MainLayout.class)
public class AmbiguousLabelView extends Main {

    public AmbiguousLabelView() {
        createCheckboxGroup();
        createDuplicateCheckboxGroup();
        createRadioButtonGroup();
        createTabs();
        createComboBox();
        createSelect();
        createListBox();
        createMenuBar();
        createContextMenu();
        createSideNav();
        createAccordion();
    }

    private void createCheckboxGroup() {
        CheckboxGroup<String> group = new CheckboxGroup<>();
        group.setLabel("Ambiguous Checkbox Group");
        group.setItems("Option 10", "Option 1");
        addExample("Checkbox group", group);
    }

    private void createDuplicateCheckboxGroup() {
        CheckboxGroup<String> group = new CheckboxGroup<>();
        group.setLabel("Duplicate Checkbox Group");
        group.setItems("Twin A", "Twin B");
        group.setItemLabelGenerator(item -> "Twin");
        addExample("Duplicate checkbox group", group);
    }

    private void createRadioButtonGroup() {
        RadioButtonGroup<String> group = new RadioButtonGroup<>();
        group.setLabel("Ambiguous Radio Group");
        group.setItems("Option 10", "Option 1");
        addExample("Radio button group", group);
    }

    private void createTabs() {
        Tabs tabs = new Tabs(new Tab("Tab 10"), new Tab("Tab 1"));
        tabs.setId("ambiguous-tabs");
        addExample("Tabs", tabs);
    }

    private void createComboBox() {
        ComboBox<String> comboBox = new ComboBox<>("Ambiguous Combo Box");
        comboBox.setItems("Option 10", "Option 1");
        addExample("Combo box", comboBox);
    }

    private void createSelect() {
        Select<String> select = new Select<>();
        select.setLabel("Ambiguous Select");
        select.setItems("Option 10", "Option 1");
        addExample("Select", select);
    }

    private void createListBox() {
        ListBox<String> listBox = new ListBox<>();
        listBox.setAriaLabel("Ambiguous List Box");
        listBox.setItems("Option 10", "Option 1");
        addExample("List box", listBox);
    }

    private void createMenuBar() {
        MenuBar menuBar = new MenuBar();
        Span clicked = new Span();
        clicked.setId("menu-bar-clicked");
        menuBar.addItem("Save as copy", event -> clicked.setText("Save as copy"));
        menuBar.addItem("Save", event -> clicked.setText("Save"));
        addExample("Menu bar", menuBar);
        add(clicked);
    }

    private void createContextMenu() {
        Span target = new Span("Right-click me");
        target.setId("context-menu-target");
        Span clicked = new Span();
        clicked.setId("context-menu-clicked");
        ContextMenu contextMenu = new ContextMenu(target);
        contextMenu.addItem("Delete all", event -> clicked.setText("Delete all"));
        contextMenu.addItem("Delete", event -> clicked.setText("Delete"));
        addExample("Context menu", target);
        add(clicked);
    }

    private void createSideNav() {
        SideNav nav = new SideNav("Ambiguous Nav");
        nav.setId("ambiguous-nav");

        SideNavItem section = new SideNavItem("Section");
        section.setExpanded(true);
        SideNavItem sectionOne = new SideNavItem("Section 1");
        sectionOne.setPath("ambiguous-label/section-1");
        SideNavItem sectionTwo = new SideNavItem("Section 2");
        sectionTwo.setPath("ambiguous-label/section-2");
        section.addItem(sectionOne, sectionTwo);
        nav.addItem(section);

        SideNavItem itemTen = new SideNavItem("Item 10");
        itemTen.setPath("ambiguous-label/item-10");
        SideNavItem itemOne = new SideNavItem("Item 1");
        itemOne.setPath("ambiguous-label/item-1");
        nav.addItem(itemTen, itemOne);

        addExample("Side nav", nav);
    }

    private void createAccordion() {
        Accordion accordion = new Accordion();
        accordion.add("Panel 10", new Span("Content 10"));
        accordion.add("Panel 1", new Span("Content 1"));
        addExample("Accordion", accordion);
    }

    private void addExample(String title, Component component) {
        add(new H2(title), component);
    }
}
