package org.vaadin.jchristophe;

import org.springframework.boot.test.web.server.LocalServerPort;
import org.vaadin.dramafinder.AbstractBasePlaywrightIT;

public abstract class SpringPlaywrightIT extends AbstractBasePlaywrightIT {

    @LocalServerPort
    private int port;

    @Override
    public String getUrl() {
        return String.format("http://localhost:%d/", port);
    }
}
