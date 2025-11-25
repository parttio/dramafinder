package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.SpringComponent;

import java.util.Locale;

/**
 * Set the default locale for the application
 */
@SpringComponent
public class ApplicationServiceInitListener implements VaadinServiceInitListener {
    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addSessionInitListener(sessionInitEvent -> {
            final VaadinSession session = sessionInitEvent.getSession();
            Locale.setDefault(Locale.UK);
            session.setLocale(Locale.getDefault());
        });
    }
}