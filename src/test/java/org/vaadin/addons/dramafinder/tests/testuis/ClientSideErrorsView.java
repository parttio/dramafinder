package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Buttons that make the browser report an error, for {@code ClientSideErrorsIT}.
 */
@PageTitle("Client-side Errors Demo")
@Route(value = "client-side-errors", layout = MainLayout.class)
public class ClientSideErrorsView extends Main {

    public ClientSideErrorsView() {
        Button log = new Button("Log error",
                e -> getElement().executeJs("console.error('logged failure')"));
        Button throwing = new Button("Throw error",
                e -> getElement().executeJs("setTimeout(() => { throw new Error('uncaught failure'); })"));
        Button info = new Button("Log info",
                e -> getElement().executeJs("console.info('just info')"));
        add(log, throwing, info);
    }
}
