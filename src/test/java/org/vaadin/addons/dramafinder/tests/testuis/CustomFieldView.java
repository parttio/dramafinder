package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Custom Field Demo")
@Route(value = "custom-field", layout = MainLayout.class)
public class CustomFieldView extends Main {

    /** A phone number made of a country code and a number. */
    static class PhoneField extends CustomField<String> {
        private final Select<String> code = new Select<>();
        private final TextField number = new TextField();

        PhoneField(String label) {
            setLabel(label);
            code.setItems("+358", "+49", "+1");
            code.setAriaLabel("Country code");
            code.setWidth("7em");
            number.setAriaLabel("Number");
            add(code, number);
        }

        @Override
        protected String generateModelValue() {
            String c = code.getValue();
            String n = number.getValue();
            return (c == null ? "" : c) + " " + (n == null ? "" : n);
        }

        @Override
        protected void setPresentationValue(String value) {
            if (value == null || value.isBlank()) {
                code.clear();
                number.clear();
                return;
            }
            String[] parts = value.split(" ", 2);
            code.setValue(parts[0]);
            number.setValue(parts.length > 1 ? parts[1] : "");
        }
    }

    public CustomFieldView() {
        Span value = new Span("");
        value.setId("phone-value");

        PhoneField phone = new PhoneField("Phone");
        phone.setHelperText("Country code and number");
        phone.setRequiredIndicatorVisible(true);
        phone.addValueChangeListener(e -> value.setText(e.getValue()));
        addExample("Basic", phone, new Paragraph(new Span("Value: "), value));

        PhoneField prefilled = new PhoneField("Office phone");
        prefilled.setValue("+49 30 1234567");
        addExample("Prefilled", prefilled);

        PhoneField invalid = new PhoneField("Mobile phone");
        invalid.setErrorMessage("Enter a valid number");
        Button validate = new Button("Mark mobile invalid", e -> invalid.setInvalid(true));
        addExample("Invalid", invalid, validate);

        PhoneField disabled = new PhoneField("Fax");
        disabled.setEnabled(false);
        addExample("Disabled", disabled);

        PhoneField readOnly = new PhoneField("Home phone");
        readOnly.setValue("+358 9 7654321");
        readOnly.setReadOnly(true);
        addExample("Read-only", readOnly);

        PhoneField tooltip = new PhoneField("Work phone");
        tooltip.setTooltipText("Your direct line");
        tooltip.addClassName("work-phone");
        addExample("Tooltip and class", tooltip);
    }

    private void addExample(String title, Component... components) {
        add(new H2(title));
        add(components);
    }
}
