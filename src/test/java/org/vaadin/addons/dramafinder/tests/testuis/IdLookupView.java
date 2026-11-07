package org.vaadin.addons.dramafinder.tests.testuis;

import java.util.List;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarGroup;
import com.vaadin.flow.component.badge.Badge;
import com.vaadin.flow.component.breadcrumbs.Breadcrumbs;
import com.vaadin.flow.component.breadcrumbs.BreadcrumbsItem;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.Switch;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.markdown.Markdown;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.slider.IntegerSlider;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * One component per DramaFinder element, each given the id
 * {@code "<ElementClass>-id"} and the test id {@code "<ElementClass>-tid"}.
 * See {@code IdLookupIT}.
 */
@PageTitle("Id Lookup Demo")
@Route(value = "id-lookup", layout = MainLayout.class)
public class IdLookupView extends Main {

    public IdLookupView() {
        Accordion accordion = new Accordion();
        AccordionPanel panel = accordion.add("Panel", new Span("Content"));
        mark(accordion, "AccordionElement");
        mark(panel, "AccordionPanelElement");

        AvatarGroup avatarGroup = new AvatarGroup();
        avatarGroup.add(new AvatarGroup.AvatarGroupItem("Ada"));

        Breadcrumbs breadcrumbs = new Breadcrumbs(Breadcrumbs.Mode.MANUAL);
        BreadcrumbsItem breadcrumb = new BreadcrumbsItem("Home");
        breadcrumbs.add(breadcrumb);
        mark(breadcrumbs, "BreadcrumbsElement");
        mark(breadcrumb, "BreadcrumbsItemElement");

        Card card = new Card();
        card.setTitle("Card");

        CheckboxGroup<String> checkboxGroup = new CheckboxGroup<>("Checkbox group");
        checkboxGroup.setItems("A");
        RadioButtonGroup<String> radioGroup = new RadioButtonGroup<>("Radio group");
        radioGroup.setItems("A");
        ListBox<String> listBox = new ListBox<>();
        listBox.setItems("A");
        Select<String> select = new Select<>();
        select.setLabel("Select");
        select.setItems("A");

        Grid<String> grid = new Grid<>();
        grid.addColumn(s -> s).setHeader("Grid");
        grid.setItems("A");
        TreeGrid<String> treeGrid = new TreeGrid<>();
        treeGrid.addHierarchyColumn(s -> s).setHeader("Tree");
        treeGrid.setItems(List.of("A"), s -> List.of());

        MenuBar menuBar = new MenuBar();
        MenuItem menuItem = menuBar.addItem("Item");
        menuItem.setId("MenuItemElement-id");

        MessageList messageList = new MessageList();
        Popover popover = new Popover();
        popover.add(new Span("Popover"));

        SideNav sideNav = new SideNav("Nav");
        SideNavItem sideNavItem = new SideNavItem("Item");
        sideNav.addItem(sideNavItem);
        mark(sideNav, "SideNavigationElement");
        mark(sideNavItem, "SideNavigationItemElement");

        Tab tab = new Tab("Tab");
        Tabs tabs = new Tabs(tab);
        mark(tab, "TabElement");
        mark(tabs, "TabsElement");
        TabSheet tabSheet = new TabSheet();
        tabSheet.add("Sheet", new Span("Sheet content"));

        VirtualList<String> virtualList = new VirtualList<>();
        virtualList.setItems("A");
        virtualList.setRenderer(new ComponentRenderer<>(s -> new Span(s)));

        add(accordion, popover);
        markAll(
                new Object[] {new Avatar("Ada"), "AvatarElement"},
                new Object[] {avatarGroup, "AvatarGroupElement"},
                new Object[] {new Badge("Badge"), "BadgeElement"},
                new Object[] {new BigDecimalField("Decimal"), "BigDecimalFieldElement"},
                new Object[] {breadcrumbs, null},
                new Object[] {new Button("Button"), "ButtonElement"},
                new Object[] {card, "CardElement"},
                new Object[] {new Checkbox("Checkbox"), "CheckboxElement"},
                new Object[] {checkboxGroup, "CheckboxGroupElement"},
                new Object[] {new ComboBox<String>("Combo"), "ComboBoxElement"},
                new Object[] {new PhoneField(), "CustomFieldElement"},
                new Object[] {new DatePicker("Date"), "DatePickerElement"},
                new Object[] {new DateTimePicker("Date time"), "DateTimePickerElement"},
                new Object[] {new Details("Details", new Span("Details content")), "DetailsElement"},
                new Object[] {new EmailField("Email"), "EmailFieldElement"},
                new Object[] {new FormLayout(new TextField("In form")), "FormLayoutElement"},
                new Object[] {grid, "GridElement"},
                new Object[] {new IntegerField("Integer"), "IntegerFieldElement"},
                new Object[] {listBox, "ListBoxElement"},
                new Object[] {new LoginForm(), "LoginFormElement"},
                new Object[] {new Markdown("**Markdown**"), "MarkdownElement"},
                new Object[] {menuBar, "MenuBarElement"},
                new Object[] {new MessageInput(), "MessageInputElement"},
                new Object[] {messageList, "MessageListElement"},
                new Object[] {new MultiSelectComboBox<String>("Multi"), "MultiSelectComboBoxElement"},
                new Object[] {new NumberField("Number"), "NumberFieldElement"},
                new Object[] {new PasswordField("Password"), "PasswordFieldElement"},
                new Object[] {new ProgressBar(), "ProgressBarElement"},
                new Object[] {radioGroup, "RadioButtonGroupElement"},
                new Object[] {select, "SelectElement"},
                new Object[] {sideNav, null},
                new Object[] {new IntegerSlider("Slider"), "SliderElement"},
                new Object[] {new SplitLayout(new Span("Left"), new Span("Right")), "SplitLayoutElement"},
                new Object[] {new Switch("Switch"), "SwitchElement"},
                new Object[] {tabs, null},
                new Object[] {tabSheet, "TabSheetElement"},
                new Object[] {new TextArea("Area"), "TextAreaElement"},
                new Object[] {new TextField("Text"), "TextFieldElement"},
                new Object[] {new TimePicker("Time"), "TimePickerElement"},
                new Object[] {treeGrid, "TreeGridElement"},
                new Object[] {new Upload(), "UploadElement"},
                new Object[] {virtualList, "VirtualListElement"});
        mark(popover, "PopoverElement");
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        // Overlays live outside the view; open them so their hosts are in the DOM.
        Dialog dialog = new Dialog(new Span("Dialog"));
        dialog.setModal(false);
        mark(dialog, "DialogElement");
        dialog.open();
        ConfirmDialog confirmDialog = new ConfirmDialog("Confirm", "Text", "OK", e -> { });
        mark(confirmDialog, "ConfirmDialogElement");
        confirmDialog.open();
        LoginOverlay loginOverlay = new LoginOverlay();
        mark(loginOverlay, "LoginOverlayElement");
        loginOverlay.setOpened(true);
        findAncestor(AppLayout.class).setId("AppLayoutElement-id");
        findAncestor(AppLayout.class).setTestId("AppLayoutElement-tid");
    }

    private void markAll(Object[]... components) {
        for (Object[] entry : components) {
            Component component = (Component) entry[0];
            if (entry[1] != null) {
                mark(component, (String) entry[1]);
            }
            add(component);
        }
    }

    private static void mark(Component component, String element) {
        component.setId(element + "-id");
        component.setTestId(element + "-tid");
    }

    /** A custom field wrapping a text field. */
    private static class PhoneField extends CustomField<String> {
        private final TextField number = new TextField();

        PhoneField() {
            setLabel("Phone");
            add(number);
        }

        @Override
        protected String generateModelValue() {
            return number.getValue();
        }

        @Override
        protected void setPresentationValue(String value) {
            number.setValue(value);
        }
    }
}
