package org.vaadin.addons.dramafinder.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.ClassUtils;

/**
 * Adds the test-only sign-in endpoint behind {@link SpringTestLogin} to the
 * test's Spring context when Spring Security is on the classpath; without it,
 * the import adds nothing. {@link SpringPlaywrightIT} imports it; any other
 * {@code @SpringBootTest} that signs users in imports it itself:
 * <pre>{@code
 * @SpringBootTest(webEnvironment = RANDOM_PORT)
 * @Import(TestLoginConfiguration.class)
 * class OrdersIT { ... }
 * }</pre>
 * The endpoint so exists only in a test context, never in the application. Not
 * a {@code @Configuration}, so a component scan that reaches this package never
 * picks it up: only the import adds it.
 */
@Conditional(TestLoginConfiguration.SpringSecurityPresent.class)
public class TestLoginConfiguration {

    /**
     * Whether Spring Security's web support and the servlet API are on the
     * classpath, so {@link SpringTestLogin} can sign a browser in. Lives here,
     * not in {@code SpringTestLogin}: loading that class may load the Spring
     * Security types it uses.
     *
     * @return {@code true} when a test can be signed in
     */
    public static boolean isAvailable() {
        ClassLoader loader = TestLoginConfiguration.class.getClassLoader();
        return ClassUtils.isPresent("org.springframework.security.web.context.HttpSessionSecurityContextRepository",
                loader) && ClassUtils.isPresent("jakarta.servlet.Filter", loader);
    }

    @Bean
    TestLoginFilter dramafinderTestLoginFilter() {
        return new TestLoginFilter();
    }

    /** Matches when Spring Security's web support and the servlet API are there. */
    static final class SpringSecurityPresent implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return isAvailable();
        }
    }
}
