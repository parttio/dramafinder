package org.vaadin.addons.dramafinder.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.vaadin.addons.dramafinder.AbstractBasePlaywrightIT;

/**
 * Base class for Playwright integration tests of a Spring Boot Vaadin app.
 * <p>
 * Starts the application on a random port ({@code @SpringBootTest(webEnvironment
 * = RANDOM_PORT)}) and derives {@link #getUrl()} from the running server: the
 * local port, the servlet context path and, when SSL is enabled, {@code https}.
 * A test only extends it and names its view:
 * <pre>{@code
 * class OrderViewIT extends SpringPlaywrightIT {
 *     @Override
 *     public String getView() {
 *         return "orders";
 *     }
 * }
 * }</pre>
 * Re-annotate the test class with {@code @SpringBootTest} to change the web
 * environment, properties or configuration; {@code DEFINED_PORT} works too.
 * <p>
 * Spring is optional for DramaFinder: this is the only class that needs it, and
 * the library declares {@code spring-boot-test} as {@code provided}, so it
 * never adds Spring to a project's classpath. A Spring Boot project already has
 * it through {@code spring-boot-starter-test}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class SpringPlaywrightIT extends AbstractBasePlaywrightIT {

    @Autowired
    private Environment environment;

    /**
     * The URL of the running application, e.g.
     * {@code http://localhost:54321/} or {@code http://localhost:54321/ctx/}
     * with a servlet context path.
     *
     * @return the base URL, ending with {@code /}
     * @throws IllegalStateException if the application runs without a web
     *                               server ({@code webEnvironment = MOCK} or
     *                               {@code NONE})
     */
    @Override
    public String getUrl() {
        String port = environment.getProperty("local.server.port");
        if (port == null) {
            throw new IllegalStateException("No local server port: run the test with "
                    + "@SpringBootTest(webEnvironment = RANDOM_PORT or DEFINED_PORT)");
        }
        String scheme = environment.getProperty("server.ssl.enabled", Boolean.class, false) ? "https" : "http";
        String contextPath = environment.getProperty("server.servlet.context-path", "");
        if (contextPath.endsWith("/")) {
            contextPath = contextPath.substring(0, contextPath.length() - 1);
        }
        if (!contextPath.isEmpty() && !contextPath.startsWith("/")) {
            contextPath = "/" + contextPath;
        }
        return scheme + "://localhost:" + port + contextPath + "/";
    }
}
