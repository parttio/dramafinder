package org.vaadin.addons.dramafinder.tests.testuis;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.Switch;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.slider.IntegerSlider;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * For every element with a visible label, three instances of its component:
 * one with the label {@code "label <Name>"}, one named only by the aria-label
 * {@code "aria <Name>"}, and — where the component has one — one with only the
 * placeholder {@code "placeholder <Name>"}. The kind comes first so that no
 * element's text contains another's ({@code "TimePicker"} is inside
 * {@code "DateTimePicker"}). Containers also hold children whose
 * label is {@code "<Name> item"} or {@code "<Name> inner"}, which must not name
 * the container.
 */
@PageTitle("Label Lookup Demo")
@Route(value = "label-lookup", layout = MainLayout.class)
public class LabelLookupView extends Main {

    /** Element simple name without "Element" → a fresh, unlabelled component. */
    public static final Map<String, Supplier<Component>> COMPONENTS = new LinkedHashMap<>();

    static {
        COMPONENTS.put("TextField", TextField::new);
        COMPONENTS.put("EmailField", EmailField::new);
        COMPONENTS.put("PasswordField", PasswordField::new);
        COMPONENTS.put("TextArea", TextArea::new);
        COMPONENTS.put("IntegerField", IntegerField::new);
        COMPONENTS.put("NumberField", NumberField::new);
        COMPONENTS.put("BigDecimalField", BigDecimalField::new);
        COMPONENTS.put("ComboBox", ComboBox::new);
        COMPONENTS.put("MultiSelectComboBox", MultiSelectComboBox::new);
        COMPONENTS.put("DatePicker", DatePicker::new);
        COMPONENTS.put("TimePicker", TimePicker::new);
        COMPONENTS.put("DateTimePicker", DateTimePicker::new);
        COMPONENTS.put("Slider", () -> new IntegerSlider(0, 10));
        COMPONENTS.put("Checkbox", Checkbox::new);
        COMPONENTS.put("Switch", Switch::new);
        COMPONENTS.put("Select", Select::new);
        COMPONENTS.put("CustomField", InnerField::new);
        COMPONENTS.put("CheckboxGroup", () -> {
            CheckboxGroup<String> group = new CheckboxGroup<>();
            group.setItems("CheckboxGroup item");
            return group;
        });
        COMPONENTS.put("RadioButtonGroup", () -> {
            RadioButtonGroup<String> group = new RadioButtonGroup<>();
            group.setItems("RadioButtonGroup item");
            return group;
        });
        COMPONENTS.put("SideNavigation", SideNav::new);
    }

    public LabelLookupView() {
        COMPONENTS.forEach((name, factory) -> {
            Component labelled = factory.get();
            call(labelled, "setLabel", "label " + name);
            labelled.setId(name + "-label");

            add(labelled);
            // vaadin-custom-field drops an aria-label, so it has no aria-named instance
            if (!(labelled instanceof CustomField<?>)) {
                Component named = factory.get();
                if (!call(named, "setAriaLabel", "aria " + name)) {
                    named.getElement().setAttribute("aria-label", "aria " + name);
                }
                named.setId(name + "-aria");
                add(named);
            }

            Component placeholder = factory.get();
            String text = "placeholder " + name;
            if (call(placeholder, "setPlaceholder", text) || call(placeholder, "setDatePlaceholder", text)) {
                placeholder.setId(name + "-placeholder");
                add(placeholder);
            }
        });
    }

    private static boolean call(Component component, String setter, String value) {
        try {
            Method m = component.getClass().getMethod(setter, String.class);
            m.invoke(component, value);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    /** A custom field holding a text field labelled "CustomField inner". */
    public static class InnerField extends CustomField<String> {
        private final TextField inner = new TextField("CustomField inner");

        public InnerField() {
            add(inner);
        }

        @Override
        protected String generateModelValue() {
            return inner.getValue();
        }

        @Override
        protected void setPresentationValue(String value) {
            inner.setValue(value);
        }
    }
}
