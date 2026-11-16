package org.vaadin.addons.dramafinder;

import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.TimeoutError;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.DefaultRoutePathProvider;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.router.RoutePrefix;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Navigates by Vaadin route class and asserts which view the browser is on.
 * <p>
 * The path is resolved statically, without a running Vaadin service: the
 * view's {@code @Route} value (or Flow's naming convention when it has none),
 * prefixed by the {@code @RoutePrefix} of its {@code layout} and of that
 * layout's {@code @ParentLayout} chain, the way Flow does — so renaming a route
 * cannot silently break a test. Routes resolved at runtime (a custom
 * {@code RoutePathProvider}, {@code @Layout} auto layouts carrying a prefix,
 * {@code RouteConfiguration.setRoute}) are not seen; navigate to those by path.
 * <p>
 * Every method takes the application's base URL — what the path is appended
 * to, e.g. {@code http://localhost:8080/} or {@code http://host/context/}.
 * {@link AbstractBasePlaywrightIT} wraps them with its {@code page} and
 * {@code getUrl()}. Requires {@code com.vaadin:flow-server} on the test
 * classpath, which a Vaadin project already has.
 */
public final class RouteNavigation {

    private RouteNavigation() {
    }

    // ── Paths ──────────────────────────────────────────────────────────

    /**
     * The route template of {@code view}, relative to the application root and
     * without leading slash, e.g. {@code "admin/user/:userId/edit"}.
     *
     * @param view a class annotated with {@code @Route}
     * @return the route template; {@code ""} for the root view
     * @throws IllegalArgumentException if {@code view} has no {@code @Route}
     */
    public static String templateOf(Class<?> view) {
        Route route = view.getAnnotation(Route.class);
        if (route == null) {
            throw new IllegalArgumentException(view.getSimpleName() + " is not annotated with @Route");
        }
        List<String> parts = route.absolute() ? new ArrayList<>() : prefixesOf(route.layout());
        parts.add(new DefaultRoutePathProvider().getRoutePath(view));
        return String.join("/", parts.stream().filter(p -> !p.isEmpty()).toList());
    }

    /**
     * The path that opens {@code view}, for a route without parameters.
     *
     * @param view a class annotated with {@code @Route}
     * @return the path, relative to the application root
     * @throws IllegalArgumentException if the route needs parameters
     */
    public static String pathOf(Class<?> view) {
        return pathOf(view, RouteParameters.empty());
    }

    /**
     * The path that opens a {@link HasUrlParameter} view with {@code parameter}
     * appended as the last segment.
     * <p>
     * The value is percent-encoded so the URL stays valid; Flow hands the view
     * the segment as it appears in the URL, so a value with a space reaches
     * {@code setParameter} as {@code %20}, exactly as after {@code UI.navigate}.
     *
     * @param view      a {@code @Route} class implementing {@code HasUrlParameter}
     * @param parameter the URL parameter; {@code null} for an
     *                  {@code @OptionalParameter} left out
     * @return the path, relative to the application root
     * @throws IllegalArgumentException if {@code view} does not take a URL parameter
     */
    public static String pathOf(Class<?> view, Object parameter) {
        if (parameter instanceof RouteParameters routeParameters) {
            return pathOf(view, routeParameters);
        }
        if (!HasUrlParameter.class.isAssignableFrom(view)) {
            throw new IllegalArgumentException(view.getSimpleName() + " does not implement HasUrlParameter");
        }
        String path = pathOf(view);
        if (parameter == null) {
            return path;
        }
        String segments = encodeSegments(String.valueOf(parameter));
        return path.isEmpty() ? segments : path + "/" + segments;
    }

    /**
     * The path that opens a template route, e.g. {@code "user/:userId/edit"}
     * with {@code new RouteParameters("userId", "42")}.
     *
     * @param view       a class annotated with {@code @Route}
     * @param parameters values for the template's {@code :parameters}; optional
     *                   ({@code ?}) and wildcard ({@code *}) ones may be left out
     * @return the path, relative to the application root
     * @throws IllegalArgumentException if a required parameter has no value
     */
    public static String pathOf(Class<?> view, RouteParameters parameters) {
        Objects.requireNonNull(parameters, "parameters");
        List<String> out = new ArrayList<>();
        for (String segment : templateOf(view).split("/")) {
            if (!segment.startsWith(":")) {
                if (!segment.isEmpty()) {
                    out.add(segment);
                }
                continue;
            }
            String name = parameterName(segment);
            if (segment.contains("*")) {
                parameters.getWildcard(name).forEach(v -> out.add(encodeSegments(v)));
                continue;
            }
            Optional<String> value = parameters.get(name);
            if (value.isPresent()) {
                out.add(encodeSegments(value.get()));
            } else if (!isOptional(segment)) {
                throw new IllegalArgumentException("Route " + view.getSimpleName()
                        + " needs a value for :" + name + " (template \"" + templateOf(view) + "\")");
            }
        }
        return String.join("/", out);
    }

    // ── Navigation ─────────────────────────────────────────────────────

    /**
     * Open {@code view} and wait for Vaadin to finish loading it.
     *
     * @param page    the Playwright page
     * @param baseUrl the application's base URL
     * @param view    a class annotated with {@code @Route}
     */
    public static void navigate(Page page, String baseUrl, Class<?> view) {
        open(page, baseUrl, pathOf(view));
    }

    /**
     * Open a parameterized view and wait for Vaadin to finish loading it.
     *
     * @param page      the Playwright page
     * @param baseUrl   the application's base URL
     * @param view      a class annotated with {@code @Route}
     * @param parameter the {@code HasUrlParameter} value, or a
     *                  {@link RouteParameters} for a template route
     */
    public static void navigate(Page page, String baseUrl, Class<?> view, Object parameter) {
        open(page, baseUrl, pathOf(view, parameter));
    }

    /**
     * Open {@code path} under the base URL and wait for Vaadin to finish loading.
     *
     * @param page    the Playwright page
     * @param baseUrl the application's base URL
     * @param path    the path, relative to the application root, optionally
     *                with a query string
     */
    public static void open(Page page, String baseUrl, String path) {
        page.navigate(url(baseUrl, path));
        AutoWait.waitForVaadinIdle(page);
    }

    // ── Assertions ─────────────────────────────────────────────────────

    /**
     * Assert, retrying until the timeout, that the browser is on {@code view}:
     * its path matches the view's route template, any parameter value
     * accepted, whatever the query string.
     *
     * @param page    the Playwright page
     * @param baseUrl the application's base URL
     * @param view    a class annotated with {@code @Route}
     */
    public static void assertAt(Page page, String baseUrl, Class<?> view) {
        assertThat(page).hasURL(urlPattern(baseUrl, view));
    }

    /**
     * Assert, retrying until the timeout, that the browser's path is exactly
     * {@code path}, whatever the query string.
     *
     * @param page    the Playwright page
     * @param baseUrl the application's base URL
     * @param path    the expected path, relative to the application root
     */
    public static void assertPath(Page page, String baseUrl, String path) {
        assertThat(page).hasURL(Pattern.compile(
                "^" + quote(root(baseUrl) + "/" + strip(path)) + "/?" + QUERY_AND_FRAGMENT));
    }

    /**
     * Assert, retrying until the timeout, that the URL's query string holds
     * {@code name} with {@code value} (compared decoded), or no {@code name}
     * at all when {@code value} is {@code null}.
     *
     * @param page  the Playwright page
     * @param name  the query parameter name
     * @param value the expected value; {@code null} to assert absence
     */
    public static void assertQueryParameter(Page page, String name, String value) {
        try {
            page.waitForCondition(() -> Objects.equals(value, queryParameter(page.url(), name)));
        } catch (TimeoutError e) {
            throw new AssertionError((value == null
                    ? "Expected no query parameter \"" + name + "\""
                    : "Expected query parameter " + name + "=" + value)
                    + " but the URL is " + page.url(), e);
        }
    }

    // ── Reading the current location ───────────────────────────────────

    /**
     * Get the path the browser is on, relative to the application root, with no
     * leading or trailing slash and without the query string, e.g.
     * {@code "orders/42"}; {@code ""} for the root.
     * <p>
     * Reads {@code location.href} in the page, so it already sees a client-side
     * navigation ({@code RouterLink}, {@code history.pushState}) that
     * {@link Page#url()} only reports after the next call to the browser. Percent
     * escapes are decoded. Reads once, without waiting; to wait for a path, use
     * {@link #assertPath(Page, String, String)}.
     *
     * @param page    the Playwright page
     * @param baseUrl the application's base URL
     * @return the path, or {@code null} when the browser is outside the base URL
     *         (another host, or a path above the context path)
     */
    public static String currentPath(Page page, String baseUrl) {
        URI current = URI.create(currentUrl(page));
        URI base = URI.create(baseUrl);
        if (!Objects.equals(current.getScheme(), base.getScheme())
                || !Objects.equals(current.getRawAuthority(), base.getRawAuthority())) {
            return null;
        }
        String root = strip(trimTrailingSlash(base.getPath() == null ? "" : base.getPath()));
        String path = strip(current.getPath() == null ? "" : current.getPath());
        if (!root.isEmpty()) {
            if (!path.equals(root) && !path.startsWith(root + "/")) {
                return null;
            }
            path = path.substring(root.length());
        }
        return trimTrailingSlash(strip(path));
    }

    /**
     * Whether the browser is on {@code view} right now: the non-asserting form of
     * {@link #assertAt(Page, String, Class)}, reading the URL the way
     * {@link #currentPath(Page, String)} does. Reads once, without waiting.
     *
     * @param page    the Playwright page
     * @param baseUrl the application's base URL
     * @param view    a class annotated with {@code @Route}
     * @return {@code true} when the path matches the view's route template
     */
    public static boolean isAt(Page page, String baseUrl, Class<?> view) {
        return urlPattern(baseUrl, view).matcher(currentUrl(page)).find();
    }

    /**
     * Get the value of a query parameter of the URL the browser is on, decoded;
     * reads the URL the way {@link #currentPath(Page, String)} does, once,
     * without waiting.
     *
     * @param page the Playwright page
     * @param name the query parameter name
     * @return the value ({@code ""} for a parameter without one), or empty when
     *         the URL has no such parameter
     */
    public static Optional<String> queryParameter(Page page, String name) {
        return Optional.ofNullable(queryParameter(currentUrl(page), name));
    }

    // ── Internals ──────────────────────────────────────────────────────

    // location.href sees a pushState navigation before Playwright's page.url() does
    private static String currentUrl(Page page) {
        return (String) page.evaluate("() => location.href");
    }

    private static String trimTrailingSlash(String path) {
        return path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
    }

    private static final String QUERY_AND_FRAGMENT = "(\\?[^#]*)?(#.*)?$";

    /** Prefixes of a route layout and its parent layouts, outermost first. */
    private static List<String> prefixesOf(Class<?> layout) {
        List<String> prefixes = new ArrayList<>();
        Class<?> current = layout;
        while (current != null && current != UI.class) {
            RoutePrefix prefix = current.getAnnotation(RoutePrefix.class);
            if (prefix != null) {
                prefixes.add(0, prefix.value());
                if (prefix.absolute()) {
                    break;
                }
            }
            ParentLayout parent = current.getAnnotation(ParentLayout.class);
            current = parent == null ? null : parent.value();
        }
        return prefixes;
    }

    /** Pattern of every URL that shows {@code view}; package-private for tests. */
    static Pattern urlPattern(String baseUrl, Class<?> view) {
        StringBuilder regex = new StringBuilder("^").append(quote(root(baseUrl)));
        for (String segment : templateOf(view).split("/")) {
            if (segment.isEmpty()) {
                continue;
            }
            if (!segment.startsWith(":")) {
                regex.append(quote("/" + segment));
            } else if (segment.contains("*")) {
                regex.append("(/[^?#]*)?");
            } else {
                String body = segment.contains("(")
                        ? segment.substring(segment.indexOf('(') + 1, segment.lastIndexOf(')'))
                        : "[^/?#]+";
                regex.append(isOptional(segment) ? "(/(" + body + "))?" : "/(" + body + ")");
            }
        }
        if (HasUrlParameter.class.isAssignableFrom(view)) {
            regex.append("(/[^?#]*)?");
        }
        return Pattern.compile(regex.append("/?").append(QUERY_AND_FRAGMENT).toString());
    }

    static String queryParameter(String url, String name) {
        String query = URI.create(url).getRawQuery();
        if (query == null) {
            return null;
        }
        for (String pair : query.split("&")) {
            int eq = pair.indexOf('=');
            String key = URLDecoder.decode(eq < 0 ? pair : pair.substring(0, eq), StandardCharsets.UTF_8);
            if (key.equals(name)) {
                return eq < 0 ? "" : URLDecoder.decode(pair.substring(eq + 1), StandardCharsets.UTF_8);
            }
        }
        return null;
    }

    static String url(String baseUrl, String path) {
        return root(baseUrl) + "/" + strip(path);
    }

    private static String root(String baseUrl) {
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    private static String strip(String path) {
        return path.startsWith("/") ? path.substring(1) : path;
    }

    private static String parameterName(String segment) {
        String name = segment.substring(1);
        int end = name.length();
        for (char c : new char[] {'?', '*', '('}) {
            int i = name.indexOf(c);
            if (i >= 0) {
                end = Math.min(end, i);
            }
        }
        return name.substring(0, end);
    }

    /**
     * Escape regex metacharacters. Playwright evaluates the pattern as a
     * JavaScript regular expression, which does not understand the
     * {@code \\Q...\\E} quoting of {@link Pattern#quote}.
     */
    static String quote(String text) {
        StringBuilder out = new StringBuilder();
        for (char c : text.toCharArray()) {
            if ("\\^$.|?*+()[]{}/".indexOf(c) >= 0) {
                out.append('\\');
            }
            out.append(c);
        }
        return out.toString();
    }

    private static boolean isOptional(String segment) {
        String head = segment.contains("(") ? segment.substring(0, segment.indexOf('(')) : segment;
        return head.endsWith("?") || segment.endsWith("?");
    }

    private static String encodeSegments(String value) {
        List<String> parts = new ArrayList<>();
        for (String part : value.split("/", -1)) {
            parts.add(URLEncoder.encode(part, StandardCharsets.UTF_8).replace("+", "%20"));
        }
        return String.join("/", parts);
    }
}
