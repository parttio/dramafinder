package org.vaadin.addons.dramafinder.spring;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.Conditional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Spring Security is optional for {@link SpringPlaywrightIT} (issue #216): a
 * Spring Boot app without it must still load and run the base class, and the
 * sign-in endpoint is registered only when Spring Security is there.
 */
class SpringSecurityOptionalTest {

    /** Hides Spring Security, as in a Spring Boot app that doesn't use it. */
    private static final class WithoutSpringSecurity extends URLClassLoader {

        WithoutSpringSecurity() throws Exception {
            super(classPath(), ClassLoader.getPlatformClassLoader());
        }

        private static URL[] classPath() throws Exception {
            List<URL> urls = new ArrayList<>();
            for (String entry : System.getProperty("java.class.path").split(File.pathSeparator)) {
                urls.add(new File(entry).toURI().toURL());
            }
            return urls.toArray(URL[]::new);
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            if (name.startsWith("org.springframework.security.")) {
                throw new ClassNotFoundException(name);
            }
            return super.loadClass(name, resolve);
        }
    }

    @Test
    void baseClassAndEndpointLoadWithoutSpringSecurity() throws Exception {
        try (WithoutSpringSecurity loader = new WithoutSpringSecurity()) {
            for (Class<?> type : List.of(SpringPlaywrightIT.class, TestLoginFilter.class,
                    TestLoginConfiguration.class)) {
                Class<?> loaded = Class.forName(type.getName(), true, loader);
                // what JUnit and Spring reflect over
                loaded.getDeclaredMethods();
                loaded.getDeclaredFields();
                loaded.getDeclaredConstructors();
            }
        }
    }

    @Test
    void endpointIsRegisteredOnlyWithSpringSecurity() throws Exception {
        Class<? extends Condition> condition = TestLoginConfiguration.class
                .getAnnotation(Conditional.class).value()[0];
        var constructor = condition.getDeclaredConstructor();
        constructor.setAccessible(true);
        assertTrue(constructor.newInstance().matches(null, null));
        try (WithoutSpringSecurity loader = new WithoutSpringSecurity()) {
            Method isAvailable = Class.forName(TestLoginConfiguration.class.getName(), true, loader)
                    .getDeclaredMethod("isAvailable");
            isAvailable.setAccessible(true);
            assertFalse((Boolean) isAvailable.invoke(null));
        }
    }
}
