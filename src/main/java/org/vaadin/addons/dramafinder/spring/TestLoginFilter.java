package org.vaadin.addons.dramafinder.spring;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.Ordered;

/**
 * The test-only sign-in endpoint behind {@link SpringPlaywrightIT}: a servlet
 * filter, registered only in the test's Spring context, that puts a security
 * context the test registered into a new HTTP session.
 * <p>
 * It runs before Spring Security's filter chain, so the application's security
 * rules never see it, and it accepts only a one-time token the test JVM handed
 * out: nothing else can sign in through it. Its signatures hold no Spring
 * Security type, so a Spring Boot app without Spring Security still loads it.
 */
final class TestLoginFilter implements Filter, Ordered {

    /** The endpoint's path under the servlet context, without a leading slash. */
    static final String PATH = "__dramafinder/login";

    // HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, inlined
    // so this class needs no Spring Security on the classpath
    static final String SPRING_SECURITY_CONTEXT_KEY = "SPRING_SECURITY_CONTEXT";

    private final Map<String, Object> pending = new ConcurrentHashMap<>();

    /**
     * Hand out a one-time token that signs the next request carrying it in as
     * {@code securityContext}.
     *
     * @param securityContext the {@code SecurityContext} to put in the session
     * @return the token
     */
    String register(Object securityContext) {
        String token = UUID.randomUUID().toString();
        pending.put(token, securityContext);
        return token;
    }

    /**
     * Forget a token that was not used.
     *
     * @param token a token from {@link #register(Object)}
     */
    void discard(String token) {
        pending.remove(token);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest http && response instanceof HttpServletResponse httpResponse
                && http.getRequestURI().equals(http.getContextPath() + "/" + PATH)) {
            signIn(http, httpResponse);
        } else {
            chain.doFilter(request, response);
        }
    }

    private void signIn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = request.getParameter("token");
        Object securityContext = token == null ? null : pending.remove(token);
        if (securityContext == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // a new session, as a real sign-in does against session fixation
        HttpSession existing = request.getSession(false);
        if (existing != null) {
            existing.invalidate();
        }
        request.getSession(true).setAttribute(SPRING_SECURITY_CONTEXT_KEY, securityContext);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
