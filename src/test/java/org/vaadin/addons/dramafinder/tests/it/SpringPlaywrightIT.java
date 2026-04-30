package org.vaadin.addons.dramafinder.tests.it;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.vaadin.addons.dramafinder.AbstractBasePlaywrightIT;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class SpringPlaywrightIT extends AbstractBasePlaywrightIT {

    @LocalServerPort
    private int port;

    @Override
    public String getUrl() {
        return String.format("http://localhost:%d/", port);
    }
}
