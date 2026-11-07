package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Element Query Demo")
@Route(value = "element-query", layout = MainLayout.class)
public class ElementQueryView extends Main {

    public ElementQueryView() {
        TextField name = new TextField("Name");
        name.setId("name-field");
        TextField nickname = new TextField("Nickname");

        TextField search = new TextField();
        search.setAriaLabel("Search");
        search.setPlaceholder("Type to search");

        TextField street = new TextField();
        street.setPlaceholder("Street");

        ComboBox<String> country = new ComboBox<>("Country");
        country.setItems("Finland", "France");
        country.setPlaceholder("Pick one");

        Select<String> size = new Select<>();
        size.setLabel("Size");
        size.setItems("S", "M", "L");
        size.setPlaceholder("Choose size");

        EmailField email = new EmailField("Email");

        Div placeholders = new Div();
        placeholders.setId("placeholders");
        IntegerField integer = new IntegerField();
        integer.setPlaceholder("Integer placeholder");
        NumberField number = new NumberField();
        number.setPlaceholder("Number placeholder");
        BigDecimalField decimal = new BigDecimalField();
        decimal.setPlaceholder("Decimal placeholder");
        DatePicker date = new DatePicker();
        date.setPlaceholder("Date placeholder");
        TimePicker time = new TimePicker();
        time.setPlaceholder("Time placeholder");
        MultiSelectComboBox<String> tags = new MultiSelectComboBox<>();
        tags.setItems("a", "b");
        tags.setPlaceholder("Tags placeholder");
        placeholders.add(integer, number, decimal, date, time, tags);

        Div form = new Div(name, nickname, search, street, country, size, email,
                new Checkbox("Accept terms"), new Checkbox("Subscribe"));
        form.setId("form");

        Button save = new Button("Save");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.getElement().setAttribute("data-testid", "save-btn");
        save.addClassName("primary-action");
        Button saveDraft = new Button("Save draft");
        Button close = new Button(VaadinIcon.CLOSE.create());
        close.setAriaLabel("Close");
        Button retry = new Button("Retry (2)?");

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Delete account?");
        dialog.getFooter().add(new Button("Delete", e -> dialog.close()), new Button("Cancel", e -> dialog.close()));
        Button openDialog = new Button("Open dialog", e -> dialog.open());
        Button pageDelete = new Button("Delete");

        ConfirmDialog confirm = new ConfirmDialog();
        confirm.setHeader("Discard changes?");
        Button openConfirm = new Button("Open confirm", e -> confirm.open());

        Tabs tabs = new Tabs(new Tab("Home"), new Tab("Home office"));

        add(form, placeholders, new Div(save, saveDraft, close, retry, openDialog, pageDelete, openConfirm), tabs);
    }
}
