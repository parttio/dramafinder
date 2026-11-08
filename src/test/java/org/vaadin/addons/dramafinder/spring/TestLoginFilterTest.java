package org.vaadin.addons.dramafinder.spring;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The test-only sign-in endpoint of {@link SpringPlaywrightIT} (issue #216). */
class TestLoginFilterTest {

    private final TestLoginFilter filter = new TestLoginFilter();

    private static MockHttpServletRequest login(String contextPath, String token) {
        MockHttpServletRequest request = new MockHttpServletRequest("GET",
                contextPath + "/" + TestLoginFilter.PATH);
        request.setContextPath(contextPath);
        if (token != null) {
            request.setParameter("token", token);
        }
        return request;
    }

    @Test
    void storesTheRegisteredContextInANewSession() throws Exception {
        SecurityContext context = new SecurityContextImpl();
        String token = filter.register(context);
        MockHttpSession old = new MockHttpSession();
        MockHttpServletRequest request = login("", token);
        request.setSession(old);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertEquals(204, response.getStatus());
        assertNull(chain.getRequest(), "the sign-in request must not reach the application");
        assertTrue(old.isInvalid(), "an existing session is replaced, as on a real sign-in");
        assertNotSame(old, request.getSession(false));
        assertSame(context, request.getSession(false).getAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY));
    }

    @Test
    void honoursTheContextPath() throws Exception {
        String token = filter.register(new SecurityContextImpl());
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(login("/ctx", token), response, new MockFilterChain());
        assertEquals(204, response.getStatus());
    }

    @Test
    void aTokenWorksOnce() throws Exception {
        String token = filter.register(new SecurityContextImpl());
        filter.doFilter(login("", token), new MockHttpServletResponse(), new MockFilterChain());

        MockHttpServletRequest again = login("", token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(again, response, new MockFilterChain());
        assertEquals(404, response.getStatus());
        assertNull(again.getSession(false));
    }

    @Test
    void refusesAnUnknownOrMissingToken() throws Exception {
        for (String token : new String[] {"guess", null}) {
            MockHttpServletResponse response = new MockHttpServletResponse();
            filter.doFilter(login("", token), response, new MockFilterChain());
            assertEquals(404, response.getStatus());
        }
    }

    @Test
    void aDiscardedTokenIsRefused() throws Exception {
        String token = filter.register(new SecurityContextImpl());
        filter.discard(token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(login("", token), response, new MockFilterChain());
        assertEquals(404, response.getStatus());
    }

    @Test
    void passesOtherRequestsOn() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/orders");
        MockFilterChain chain = new MockFilterChain();
        filter.doFilter(request, new MockHttpServletResponse(), chain);
        assertNotNull(chain.getRequest());
    }
}
