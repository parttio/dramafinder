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
 * Signs a Playwright browser context in as a Spring Security user through the
 * {@link TestLoginFilter}. The only class of the {@code spring} package that
 * uses Spring Security types; {@link SpringPlaywrightIT} calls it only after
 * {@link TestLoginConfiguration#isAvailable()}, so it stays unloaded where
 * Spring Security is absent.
 */
final class SecurityLogin {

    private static final String SECURITY_TEST = "org.springframework.security.test.context.TestSecurityContextHolder";

    private SecurityLogin() {
    }

    /**
     * Sign {@code browserContext} in as the user Spring Security Test set up for
     * the running test ({@code @WithMockUser}, {@code @WithUserDetails} or any
     * {@code @WithSecurityContext} annotation); does nothing when there is none
     * or it is anonymous.
     */
    static void signInTestUser(ApplicationContext applicationContext, BrowserContext browserContext,
            String baseUrl) {
        if (!ClassUtils.isPresent(SECURITY_TEST, SecurityLogin.class.getClassLoader())) {
            return;
        }
        Authentication authentication = testSecurityContext(applicationContext).getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            signIn(applicationContext, browserContext, baseUrl, authentication);
        }
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
     * Sign {@code browserContext} in as the user {@code @WithMockUser(username,
     * roles)} would create: password {@code "password"}, each role prefixed
     * with {@code ROLE_}, and {@code USER} when no role is given.
     */
    static void signIn(ApplicationContext applicationContext, BrowserContext browserContext, String baseUrl,
            String username, String... roles) {
        UserDetails user = User.withUsername(username).password("password")
                .roles(roles.length == 0 ? new String[] {"USER"} : roles).build();
        signIn(applicationContext, browserContext, baseUrl,
                UsernamePasswordAuthenticationToken.authenticated(user, user.getPassword(), user.getAuthorities()));
    }

    private static void signIn(ApplicationContext applicationContext, BrowserContext browserContext,
            String baseUrl, Authentication authentication) {
        TestLoginFilter endpoint = applicationContext.getBeanProvider(TestLoginFilter.class).getIfAvailable();
        if (endpoint == null) {
            throw new IllegalStateException("The test sign-in endpoint is not in the Spring context: "
                    + "the test must extend SpringPlaywrightIT, which imports it");
        }
        SecurityContext securityContext = new SecurityContextImpl(authentication);
        String token = endpoint.register(securityContext);
        String url = baseUrl + TestLoginFilter.PATH;
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
}
