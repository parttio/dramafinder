package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Components without an element class, theme variants, placeholders and an
 * application-owned shadow root, for the generic element and the tag queries,
 * and plain {@code div}s to focus, enable and disable.
 */
@PageTitle("Generic Element Demo")
@Route(value = "generic-element", layout = MainLayout.class)
public class GenericElementView extends Main {

    public GenericElementView() {
        Div styled = new Div(new Span("Styled box"));
        styled.setId("styled");
        styled.addClassName("box");
        styled.getElement().setAttribute("theme", "compact");
        styled.getStyle().set("background-color", "red").set("--my-color", "blue");
        add(styled);

        add(new XCard("Card label", "Card body", "Card hint"), new XCard("Other label", "Other body", null));

        Button primary = new Button("Primary");
        primary.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        add(primary, new Button("Plain"));

        TextField search = new TextField();
        search.setId("search");
        search.setPlaceholder("Search orders");
        add(search, new DatePicker("Date"));

        // Content the application renders in a shadow root of its own
        Div host = new Div();
        host.setId("shadow-host");
        host.getElement().attachShadow().appendChild(new Button("In shadow").getElement());
        add(host);

        // Enabled state and focus of a component without an element class
        Div panel = new Div(new Span("Panel"));
        panel.setId("panel");
        panel.getElement().setAttribute("tabindex", "0");
        Button togglePanel = new Button("Toggle panel", e -> panel.setEnabled(!panel.isEnabled()));
        Div disabledPanel = new Div(new Span("Disabled panel"));
        disabledPanel.setId("disabled-panel");
        disabledPanel.setEnabled(false);
        add(panel, togglePanel, disabledPanel);

        // Components of different tags found by test id alone, and a tag with a dot
        Span billing = new Span("Billing");
        billing.getElement().setAttribute("data-testid", "billing");
        Element note = new Element("x.note");
        note.setText("Dotted tag");
        Div testIds = new Div(billing);
        testIds.setId("test-ids");
        testIds.getElement().appendChild(note);
        add(testIds);

        Span uiId = new Span();
        uiId.setId("ui-id");
        uiId.addAttachListener(e -> uiId.setText(String.valueOf(e.getUI().getUIId())));
        add(uiId);
    }

    /** A custom tag with a label slot, own text and a placeholder attribute. */
    @Tag("x-card")
    public static class XCard extends Component {
        public XCard(String label, String text, String placeholder) {
            Element labelElement = new Element("span");
            labelElement.setAttribute("slot", "label");
            labelElement.setText(label);
            getElement().appendChild(labelElement);
            getElement().appendChild(new Element("span").setText(text));
            if (placeholder != null) {
                getElement().setAttribute("placeholder", placeholder);
            }
        }
    }
}
