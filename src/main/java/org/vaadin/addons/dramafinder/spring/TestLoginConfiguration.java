package org.vaadin.addons.dramafinder.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.ClassUtils;

/**
 * Adds the {@link TestLoginFilter} to the test's Spring context when Spring
 * Security is on the classpath. {@link SpringPlaywrightIT} imports it, so the
 * endpoint exists only in a test context, never in the application. Not a
 * {@code @Configuration}, so a component scan that reaches this package never
 * picks it up: only the import adds it.
 */
@Conditional(TestLoginConfiguration.SpringSecurityPresent.class)
class TestLoginConfiguration {

    /**
     * Whether Spring Security's web support and the servlet API are on the
     * classpath. Lives here, not in {@link SecurityLogin}: linking that class
     * may load the Spring Security types it uses.
     *
     * @return {@code true} when a test can be signed in
     */
    static boolean isAvailable() {
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
