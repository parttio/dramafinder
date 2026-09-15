package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep.LabelsPosition;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("FormLayout Demo")
@Route(value = "formlayout", layout = MainLayout.class)
public class FormLayoutView extends Main {

    public FormLayoutView() {
        add(defaultSteps(), narrow(), customSteps(), formItems(), lineBreak(),
                autoResponsive(), formRows());
    }

    /**
     * Default responsive steps in an 800px container: wider than the 40em step,
     * so two columns with labels aside.
     */
    private Component defaultSteps() {
        FormLayout layout = new FormLayout(new TextField("First name"),
                new TextField("Last name"), new TextField("Email"));
        layout.setId("default-form");
        layout.getElement().setAttribute("theme", "small");
        layout.addClassName("demo-form");
        return container("800px", layout);
    }

    /**
     * Default responsive steps in a 200px container: narrower than the 20em
     * step, so one column with labels on top. Uses form items, so the label
     * position is also visible in the DOM.
     */
    private Component narrow() {
        FormLayout layout = new FormLayout();
        layout.addFormItem(new TextField(), "First name");
        layout.addFormItem(new TextField(), "Last name");
        layout.setId("narrow-form");
        return container("200px", layout);
    }

    /**
     * Pixel-based responsive steps in a container sized as a fraction of the
     * viewport, so resizing the viewport moves the layout across the steps
     * without depending on the drawer width of the surrounding AppLayout.
     */
    private Component customSteps() {
        FormLayout layout = new FormLayout(new TextField("One"),
                new TextField("Two"), new TextField("Three"),
                new TextField("Four"), new TextField("Five"),
                new TextField("Six"));
        layout.setResponsiveSteps(new ResponsiveStep("0px", 1, LabelsPosition.TOP),
                new ResponsiveStep("400px", 2), new ResponsiveStep("700px", 3));
        layout.setId("responsive-form");
        return container("40vw", layout);
    }

    /**
     * Fields wrapped in form items, the shape that makes the label position
     * observable.
     */
    private Component formItems() {
        FormLayout layout = new FormLayout();
        layout.addFormItem(new TextField(), "First name");
        layout.addFormItem(new TextField(), "Last name");
        layout.addFormItem(new TextField(), "Email");
        layout.setId("form-item-form");
        return container("800px", layout);
    }

    /**
     * A {@code <br>} between the fields forces a row break; it is a child of the
     * layout but not a field.
     */
    private Component lineBreak() {
        FormLayout layout = new FormLayout();
        layout.add(new TextField("Email"));
        layout.add(lineBreakElement());
        layout.add(new TextField("Confirm email"));
        layout.setId("line-break-form");
        return container("800px", layout);
    }

    /** Flow has no {@code Br} component, so wrap a raw {@code <br>} element. */
    private static Component lineBreakElement() {
        return new Component(new Element("br")) {
        };
    }

    /**
     * Auto-responsive mode: fixed 10em columns, capped at three, in a container
     * wide enough for all of them.
     */
    private Component autoResponsive() {
        FormLayout layout = new FormLayout(new TextField("One"),
                new TextField("Two"), new TextField("Three"),
                new TextField("Four"));
        layout.setAutoResponsive(true);
        layout.setAutoRows(true);
        layout.setColumnWidth("10em");
        layout.setMaxColumns(3);
        layout.setId("auto-responsive-form");
        return container("900px", layout);
    }

    /**
     * Auto-responsive mode with explicit rows and aside labels: the two rows
     * group four form items, which the element flattens back to four fields.
     */
    private Component formRows() {
        FormLayout layout = new FormLayout();
        layout.setAutoResponsive(true);
        layout.setLabelsAside(true);
        layout.setColumnWidth("10em");
        layout.setMaxColumns(2);
        layout.addFormRow(formItem("Street"), formItem("Number"));
        layout.addFormRow(formItem("City"), formItem("Country"));
        layout.setId("form-row-form");
        return container("900px", layout);
    }

    /**
     * Build a standalone form item — {@code addFormItem} always adds to the
     * layout itself, which is not what a form row needs.
     */
    private static FormLayout.FormItem formItem(String label) {
        NativeLabel labelElement = new NativeLabel(label);
        labelElement.getElement().setAttribute("slot", "label");
        return new FormLayout.FormItem(labelElement, new TextField());
    }

    private static Component container(String width, FormLayout layout) {
        Div div = new Div(layout);
        div.setWidth(width);
        return div;
    }
}
