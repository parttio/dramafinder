package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.button.Button;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.vaadin.addons.dramafinder.tests.testuis.navigation.GreetingView;

/**
 * Every listener here sleeps on the server before updating {@code #result}, so
 * a test that reads the page right after an action only sees the update when
 * the action waited for the round-trip. See {@code AutoWaitIT}.
 */
@PageTitle("Auto Wait Demo")
@Route(value = "auto-wait", layout = MainLayout.class)
public class AutoWaitView extends Main {

    static final long DELAY_MS = 1000;

    public AutoWaitView() {
        Span result = new Span("initial");
        result.setId("result");

        Button button = new Button("Slow button", e -> slowly(() -> result.setText("clicked")));

        TextField field = new TextField("Slow field");
        field.addValueChangeListener(e -> slowly(() -> result.setText("field: " + e.getValue())));

        Checkbox checkbox = new Checkbox("Slow checkbox");
        checkbox.addValueChangeListener(e -> slowly(() -> result.setText("checkbox: " + e.getValue())));

        ComboBox<String> combo = new ComboBox<>("Slow combo");
        combo.setItems("Apple", "Banana");
        combo.addValueChangeListener(e -> slowly(() -> result.setText("combo: " + e.getValue())));

        // Server-side navigation: Flow updates the address bar only after it
        // has handled the response (issue #304)
        Button navigate = new Button("Navigate button",
                e -> e.getSource().getUI().ifPresent(ui -> ui.navigate(GreetingView.class)));

        // Full page load to another Flow view: Flow is idle on the old page
        // before the browser starts loading the new one (issue #320)
        Button reload = new Button("Reload to greeting",
                e -> e.getSource().getUI().ifPresent(ui -> ui.getPage().setLocation("nav/greeting")));

        // Plain link to a page without Flow, loaded by the browser
        Anchor plainLink = new Anchor("plain-page.html", "Plain link");
        plainLink.setRouterIgnore(true);

        add(button, field, checkbox, combo, navigate, reload, plainLink, result);
    }

    private static void slowly(Runnable update) {
        try {
            Thread.sleep(DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        update.run();
    }
}
