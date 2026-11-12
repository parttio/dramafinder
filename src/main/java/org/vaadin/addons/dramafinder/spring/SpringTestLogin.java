package org.vaadin.addons.dramafinder.spring;

import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.options.RequestOptions;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.TestSecurityContextHolder;
import org.springframework.util.ClassUtils;

/**
 * Signs a browser in as a Spring Security user without going through the login
 * view, for code that manages its own Playwright {@link BrowserContext} instead
 * of extending {@link SpringPlaywrightIT} — a JUnit extension, or a framework on
 * top of DramaFinder.
 * <p>
 * The user is put into a new HTTP session by a test-only endpoint, which the
 * test's Spring context must hold: import {@link TestLoginConfiguration} into
 * the {@code @SpringBootTest} ({@code SpringPlaywrightIT} already does).
 * <pre>{@code
 * @SpringBootTest(webEnvironment = RANDOM_PORT)
 * @Import(TestLoginConfiguration.class)
 * class OrdersIT {
 *     @Autowired ApplicationContext applicationContext;
 *     @LocalServerPort int port;
 *
 *     @Test
 *     void adminSeesOrders() {
 *         SpringTestLogin.signIn(applicationContext, browserContext,
 *                 "http://localhost:" + port + "/", "alice", "ADMIN");
 *         browserContext.newPage().navigate("http://localhost:" + port + "/orders");
 *     }
 * }
 * }</pre>
 * The application must keep its security context in the HTTP session (Spring
 * Security's default, and Vaadin's), and the browser must talk to the server of
 * the same Spring context. Every method needs Spring Security
 * ({@code spring-security-web}) on the classpath: check
 * {@link TestLoginConfiguration#isAvailable()} before calling one from code
 * that must also run without it, as this class names Spring Security types and
 * fails to load where they are absent.
 */
public final class SpringTestLogin {

    private static final String SECURITY_TEST = "org.springframework.security.test.context.TestSecurityContextHolder";

    private SpringTestLogin() {
    }

    /**
     * Sign {@code browserContext} in as the user Spring Security Test set up for
     * the running test ({@code @WithMockUser}, {@code @WithUserDetails} or any
     * other {@code @WithSecurityContext} annotation). Does nothing when the test
     * has no such user, its user is anonymous, or {@code spring-security-test}
     * is not on the classpath. Call it on the test's thread, after Spring
     * Security Test's listener has set the user up.
     *
     * @param applicationContext the test's Spring context, holding the endpoint
     * @param browserContext     the browser context to sign in; its cookies get
     *                           the session
     * @param baseUrl            the running application's URL, with or without
     *                           a trailing {@code /}, e.g.
     *                           {@code http://localhost:54321/ctx/}
     * @return {@code true} if the context was signed in, {@code false} if there
     *         was no user to sign in
     * @throws IllegalStateException if the endpoint is not in
     *                               {@code applicationContext} or the sign-in
     *                               request fails
     */
    public static boolean signInTestUser(ApplicationContext applicationContext, BrowserContext browserContext,
            String baseUrl) {
        if (!ClassUtils.isPresent(SECURITY_TEST, SpringTestLogin.class.getClassLoader())) {
            return false;
        }
        Authentication authentication = testSecurityContext(applicationContext).getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }
        signIn(applicationContext, browserContext, baseUrl, authentication);
        return true;
    }

    /**
     * The security context Spring Security Test's listener set up for the test.
     * Looked up as the listener does: the context's one
     * {@code SecurityContextHolderStrategy} bean (Vaadin registers its own),
     * otherwise {@code TestSecurityContextHolder}.
     */
    private static SecurityContext testSecurityContext(ApplicationContext applicationContext) {
        if (applicationContext.getBeanNamesForType(SecurityContextHolderStrategy.class).length == 1) {
            return applicationContext.getBean(SecurityContextHolderStrategy.class).getContext();
        }
        return TestSecurityContextHolder.getContext();
    }

    /**
     * Sign {@code browserContext} in as the user {@code @WithMockUser(username =
     * ..., roles = ...)} creates: password {@code "password"}, each role given
     * without and granted with the {@code ROLE_} prefix, and {@code USER} when
     * no role is given. A page already open must reload to see the user.
     *
     * @param applicationContext the test's Spring context, holding the endpoint
     * @param browserContext     the browser context to sign in; its cookies get
     *                           the session
     * @param baseUrl            the running application's URL, with or without
     *                           a trailing {@code /}
     * @param username           the user name
     * @param roles              the roles, e.g. {@code "ADMIN"}; none for
     *                           {@code USER}
     * @throws IllegalStateException if the endpoint is not in
     *                               {@code applicationContext} or the sign-in
     *                               request fails
     */
    public static void signIn(ApplicationContext applicationContext, BrowserContext browserContext, String baseUrl,
            String username, String... roles) {
        signIn(applicationContext, browserContext, baseUrl, mockUser(username, roles));
    }

    /**
     * Sign {@code browserContext} in as {@code authentication}, e.g. a token
     * holding the application's own {@code UserDetails} or authorities that are
     * not roles. A page already open must reload to see the user.
     *
     * @param applicationContext the test's Spring context, holding the endpoint
     * @param browserContext     the browser context to sign in; its cookies get
     *                           the session
     * @param baseUrl            the running application's URL, with or without
     *                           a trailing {@code /}
     * @param authentication     the authenticated user to put in the session
     * @throws IllegalStateException if the endpoint is not in
     *                               {@code applicationContext} or the sign-in
     *                               request fails
     */
    public static void signIn(ApplicationContext applicationContext, BrowserContext browserContext, String baseUrl,
            Authentication authentication) {
        TestLoginFilter endpoint = endpoint(applicationContext);
        String token = endpoint.register(new SecurityContextImpl(authentication));
        String url = loginUrl(baseUrl);
        // the context's request API shares its cookies, so the session cookie
        // lands in the browser
        APIResponse response = browserContext.request().get(url,
                RequestOptions.create().setQueryParam("token", token).setMaxRedirects(0));
        try {
            if (response.status() != 204) {
                throw new IllegalStateException("Signing in as " + authentication.getName() + " failed: HTTP "
                        + response.status() + " from " + url
                        + " (is the test running against the server of its own Spring context?)");
            }
        } finally {
            response.dispose();
            endpoint.discard(token);
        }
    }

    /**
     * A one-time URL that signs in, as {@code authentication}, the browser that
     * requests it, for a browser DramaFinder does not drive (e.g. Selenium's
     * {@code driver.get(url)}). The server answers {@code 204 No Content} with a
     * new session cookie, so the browser stays on the page it showed (Playwright
     * reports a navigation to it as aborted: use the {@code signIn} methods
     * there). The URL works once, and a second request gets {@code 404}; one
     * that is never requested stays valid while the Spring context lives.
     *
     * @param applicationContext the test's Spring context, holding the endpoint
     * @param baseUrl            the running application's URL, with or without
     *                           a trailing {@code /}
     * @param authentication     the authenticated user to put in the session
     * @return the sign-in URL, carrying its one-time token
     * @throws IllegalStateException if the endpoint is not in
     *                               {@code applicationContext}
     */
    public static String signInUrl(ApplicationContext applicationContext, String baseUrl,
            Authentication authentication) {
        String token = endpoint(applicationContext).register(new SecurityContextImpl(authentication));
        return loginUrl(baseUrl) + "?token=" + token;
    }

    /**
     * A one-time URL that signs in the browser requesting it as the user
     * {@link #signIn(ApplicationContext, BrowserContext, String, String, String...)}
     * creates; see {@link #signInUrl(ApplicationContext, String, Authentication)}.
     *
     * @param applicationContext the test's Spring context, holding the endpoint
     * @param baseUrl            the running application's URL, with or without
     *                           a trailing {@code /}
     * @param username           the user name
     * @param roles              the roles, e.g. {@code "ADMIN"}; none for
     *                           {@code USER}
     * @return the sign-in URL, carrying its one-time token
     * @throws IllegalStateException if the endpoint is not in
     *                               {@code applicationContext}
     */
    public static String signInUrl(ApplicationContext applicationContext, String baseUrl, String username,
            String... roles) {
        return signInUrl(applicationContext, baseUrl, mockUser(username, roles));
    }

    private static Authentication mockUser(String username, String... roles) {
        UserDetails user = User.withUsername(username).password("password")
                .roles(roles.length == 0 ? new String[] {"USER"} : roles).build();
        return UsernamePasswordAuthenticationToken.authenticated(user, user.getPassword(), user.getAuthorities());
    }

    private static TestLoginFilter endpoint(ApplicationContext applicationContext) {
        TestLoginFilter endpoint = applicationContext.getBeanProvider(TestLoginFilter.class).getIfAvailable();
        if (endpoint == null) {
            throw new IllegalStateException("The test sign-in endpoint is not in the Spring context: "
                    + "add @Import(TestLoginConfiguration.class) to the test, or extend SpringPlaywrightIT");
        }
        return endpoint;
    }

    private static String loginUrl(String baseUrl) {
        return (baseUrl.endsWith("/") ? baseUrl : baseUrl + "/") + TestLoginFilter.PATH;
    }
}
