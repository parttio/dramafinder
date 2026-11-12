package org.vaadin.addons.dramafinder.spring;

import java.util.ArrayList;
import java.util.List;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
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
 * With Spring Security, a test starts signed in as the user of Spring Security
 * Test's {@code @WithMockUser}, {@code @WithUserDetails} (or any other
 * {@code @WithSecurityContext} annotation) on the test method or class, without
 * going through the login view; {@code @WithAnonymousUser} or no annotation
 * starts signed out. {@link #loginAs(String, String...)} switches user during a
 * test, {@link #newPageAs(String, String...)} opens a second user's page and
 * {@link #loginAs(BrowserContext, String, String...)} signs in a browser context
 * the test opened itself. Code that does not extend this class signs in through
 * {@link SpringTestLogin} and {@link TestLoginConfiguration}.
 * The user is put into the HTTP session by an endpoint that exists only in the
 * test's Spring context, so the application must keep its security context in
 * the session (Spring Security's default, and Vaadin's). Needs
 * {@code spring-security-test}; {@code setupBefore = TEST_EXECUTION} is not
 * supported, as the page opens before it applies.
 * <p>
 * Spring is optional for DramaFinder: only this class and its package need it,
 * and the library declares {@code spring-boot-test} and Spring Security as
 * {@code provided}, so it never adds Spring to a project's classpath. A Spring
 * Boot project already has it through {@code spring-boot-starter-test}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestLoginConfiguration.class)
public abstract class SpringPlaywrightIT extends AbstractBasePlaywrightIT {

    @Autowired
    private Environment environment;

    @Autowired
    private ApplicationContext applicationContext;

    private final List<BrowserContext> extraContexts = new ArrayList<>();

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

    /**
     * Signs the browser context in as the test's Spring Security user
     * ({@code @WithMockUser}, {@code @WithUserDetails}, …), if it has one and
     * Spring Security is on the classpath.
     *
     * @param context the browser context the test's page runs in
     */
    @Override
    protected void prepareBrowserContext(BrowserContext context) {
        if (TestLoginConfiguration.isAvailable()) {
            SpringTestLogin.signInTestUser(applicationContext, context, getUrl());
        }
    }

    /**
     * Sign the test's {@link #page} in as {@code username}, replacing its
     * session, and reload it. The user is the one
     * {@code @WithMockUser(username = ..., roles = ...)} creates: password
     * {@code "password"}, each role given without and granted with the
     * {@code ROLE_} prefix, and {@code USER} when no role is given.
     *
     * @param username the user name
     * @param roles    the roles, e.g. {@code "ADMIN"}; none for {@code USER}
     * @throws IllegalStateException if Spring Security is not on the classpath
     */
    protected void loginAs(String username, String... roles) {
        loginAs(page.context(), username, roles);
        page.reload();
        page.waitForFunction(WAIT_FOR_VAADIN_SCRIPT);
    }

    /**
     * Sign {@code context} in as {@code username}, e.g. a context from
     * {@code getBrowser().newContext(...)}, before a page of it opens a view;
     * a page already open must reload to see the user. The user is created as
     * in {@link #loginAs(String, String...)}.
     *
     * @param context  the browser context to sign in; its cookies get the session
     * @param username the user name
     * @param roles    the roles, e.g. {@code "ADMIN"}; none for {@code USER}
     * @throws IllegalStateException if Spring Security is not on the classpath
     */
    protected void loginAs(BrowserContext context, String username, String... roles) {
        if (!TestLoginConfiguration.isAvailable()) {
            throw new IllegalStateException("Signing in needs Spring Security (spring-security-web) on the classpath");
        }
        SpringTestLogin.signIn(applicationContext, context, getUrl(), username, roles);
    }

    /**
     * Open the view in a new browser context — its own cookies and session —
     * signed in as {@code username}, e.g. to drive a second user alongside the
     * test's {@link #page}. The user is created as in
     * {@link #loginAs(String, String...)}; the page is closed after the test.
     *
     * @param username the user name
     * @param roles    the roles, e.g. {@code "ADMIN"}; none for {@code USER}
     * @return the new page, showing {@code getUrl() + getView()}
     * @throws IllegalStateException if Spring Security is not on the classpath
     */
    protected Page newPageAs(String username, String... roles) {
        BrowserContext context = getBrowser().newContext();
        extraContexts.add(context);
        loginAs(context, username, roles);
        Page other = context.newPage();
        other.navigate(getUrl() + getView());
        other.waitForFunction(WAIT_FOR_VAADIN_SCRIPT);
        other.setDefaultNavigationTimeout(4000);
        other.setDefaultTimeout(15000);
        return other;
    }

    /** Closes the browser contexts {@link #newPageAs(String, String...)} opened. */
    @AfterEach
    public void closeExtraContexts() {
        extraContexts.forEach(BrowserContext::close);
        extraContexts.clear();
    }
}
