package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/** Temporary probe for issue #340. */
@PageTitle("Issue 340 probe")
@Route(value = "issue-340", layout = MainLayout.class)
public class Issue340ProbeView extends Main {

    public static class Bean {
        private Integer amount;
        public Integer getAmount() { return amount; }
        public void setAmount(Integer amount) { this.amount = amount; }
    }

    public Issue340ProbeView() {
        TextField upper = new TextField("Upper");
        upper.addValueChangeListener(e -> {
            if (e.isFromClient()) {
                upper.setValue(e.getValue().toUpperCase());
            }
        });

        TextField onBlur = new TextField("On blur");
        onBlur.setValueChangeMode(ValueChangeMode.ON_BLUR);
        Span onBlurValue = new Span("-");
        onBlurValue.setId("on-blur-value");
        onBlur.addValueChangeListener(e -> onBlurValue.setText(e.getValue()));

        IntegerField amount = new IntegerField("Amount");
        Binder<Bean> binder = new Binder<>(Bean.class);
        binder.forField(amount).withValidator(v -> v == null || v > 10, "Must be > 10").bind("amount");
        binder.setBean(new Bean());
        Span status = new Span("-");
        status.setId("validation-status");
        binder.addStatusChangeListener(e -> status.setText(e.hasValidationErrors() ? "invalid" : "valid"));

        Span unparsable = new Span("-");
        unparsable.setId("unparsable-status");
        IntegerField parse = new IntegerField("Parse");
        parse.addValidationStatusChangeListener(e -> unparsable.setText(e.getNewStatus() ? "ok" : "bad"));

        ComboBox<String> combo = new ComboBox<>("Fruit");
        combo.setItems("Apple", "Banana", "Cherry");
        MultiSelectComboBox<String> multi = new MultiSelectComboBox<>("Fruits");
        multi.setItems("Apple", "Banana", "Cherry");
        Span clicked = new Span("-");
        clicked.setId("clicked");
        Button below = new Button("Below", e -> clicked.setText("clicked"));
        TextField lazy = new TextField("Lazy");
        lazy.setValueChangeMode(ValueChangeMode.LAZY);
        lazy.setValueChangeTimeout(2000);
        Span lazyValue = new Span("-");
        lazyValue.setId("lazy-value");
        lazy.addValueChangeListener(e -> lazyValue.setText(e.getValue()));
        add(lazy, lazyValue);
        add(upper, onBlur, onBlurValue, amount, status, parse, unparsable, combo, multi, below, clicked);
        getStyle().set("display", "flex").set("flex-direction", "column").set("width", "300px");
    }
}
