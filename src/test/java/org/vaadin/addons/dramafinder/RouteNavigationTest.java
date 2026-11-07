package org.vaadin.addons.dramafinder;

import java.util.List;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.router.RoutePrefix;
import com.vaadin.flow.router.RouterLayout;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Static route resolution of {@link RouteNavigation}; no Vaadin service needed. */
class RouteNavigationTest {

    @RoutePrefix("app")
    static class AppLayout extends Div implements RouterLayout {
    }

    @RoutePrefix("admin")
    @ParentLayout(AppLayout.class)
    static class AdminLayout extends Div implements RouterLayout {
    }

    @RoutePrefix(value = "reports", absolute = true)
    @ParentLayout(AppLayout.class)
    static class ReportsLayout extends Div implements RouterLayout {
    }

    @Route("")
    static class RootView extends Div {
    }

    @Route
    static class MainView extends Div {
    }

    @Route
    static class OrderListView extends Div {
    }

    @Route(value = "users", layout = AdminLayout.class)
    static class UsersView extends Div {
    }

    @Route(value = "login", layout = AdminLayout.class, absolute = true)
    static class LoginView extends Div {
    }

    @Route(value = "daily", layout = ReportsLayout.class)
    static class DailyView extends Div {
    }

    @Route(value = "user/:userId/edit/:tab?", layout = AdminLayout.class)
    static class UserEditView extends Div {
    }

    @Route(value = "item/:id([0-9]+)")
    static class ItemView extends Div {
    }

    @Route(value = "files/:path*")
    static class FilesView extends Div {
    }

    @Route("article")
    static class ArticleView extends Div implements HasUrlParameter<Integer> {
        @Override
        public void setParameter(BeforeEvent event, Integer parameter) {
        }
    }

    static class NotARoute extends Div {
    }

    @Test
    void resolvesRouteValuesAndPrefixes() {
        assertEquals("", RouteNavigation.pathOf(RootView.class));
        assertEquals("", RouteNavigation.pathOf(MainView.class));
        assertEquals("orderlist", RouteNavigation.pathOf(OrderListView.class));
        assertEquals("app/admin/users", RouteNavigation.pathOf(UsersView.class));
        assertEquals("login", RouteNavigation.pathOf(LoginView.class));
        assertEquals("reports/daily", RouteNavigation.pathOf(DailyView.class));
    }

    @Test
    void fillsTemplateParameters() {
        assertEquals("app/admin/user/:userId/edit/:tab?", RouteNavigation.templateOf(UserEditView.class));
        assertEquals("app/admin/user/42/edit",
                RouteNavigation.pathOf(UserEditView.class, new RouteParameters("userId", "42")));
        assertEquals("app/admin/user/42/edit/roles", RouteNavigation.pathOf(UserEditView.class,
                new RouteParameters(new RouteParam("userId", "42"), new RouteParam("tab", "roles"))));
        assertEquals("item/7", RouteNavigation.pathOf(ItemView.class, new RouteParameters("id", "7")));
        assertEquals("files/a/b%20c",
                RouteNavigation.pathOf(FilesView.class, new RouteParameters("path", "a/b c")));
        assertEquals("files", RouteNavigation.pathOf(FilesView.class));
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> RouteNavigation.pathOf(UserEditView.class));
        assertTrue(e.getMessage().contains(":userId"), e.getMessage());
    }

    @Test
    void appendsUrlParameter() {
        assertEquals("article/42", RouteNavigation.pathOf(ArticleView.class, 42));
        assertEquals("article", RouteNavigation.pathOf(ArticleView.class, (Object) null));
        assertThrows(IllegalArgumentException.class, () -> RouteNavigation.pathOf(UsersView.class, 1));
    }

    @Test
    void rejectsANonRoute() {
        assertThrows(IllegalArgumentException.class, () -> RouteNavigation.pathOf(NotARoute.class));
    }

    @Test
    void urlPatternMatchesTheViewOnly() {
        String base = "http://host/ctx/";
        for (String url : List.of("http://host/ctx/app/admin/user/42/edit",
                "http://host/ctx/app/admin/user/42/edit/roles?x=1",
                "http://host/ctx/app/admin/user/42/edit/#top")) {
            assertTrue(RouteNavigation.urlPattern(base, UserEditView.class).matcher(url).find(), url);
        }
        assertFalse(RouteNavigation.urlPattern(base, UserEditView.class)
                .matcher("http://host/ctx/app/admin/user/42").find());
        assertTrue(RouteNavigation.urlPattern(base, ItemView.class).matcher("http://host/ctx/item/7").find());
        assertFalse(RouteNavigation.urlPattern(base, ItemView.class).matcher("http://host/ctx/item/x").find());
        assertTrue(RouteNavigation.urlPattern(base, ArticleView.class).matcher("http://host/ctx/article/9").find());
        assertTrue(RouteNavigation.urlPattern(base, RootView.class).matcher("http://host/ctx/").find());
        assertTrue(RouteNavigation.urlPattern(base, RootView.class).matcher("http://host/ctx").find());
        assertFalse(RouteNavigation.urlPattern(base, RootView.class).matcher("http://host/ctx/other").find());
    }

    @Test
    void patternsAreJavaScriptCompatible() {
        // Playwright runs the pattern in the browser: no \\Q...\\E quoting
        String pattern = RouteNavigation.urlPattern("http://host:8080/ctx/", UsersView.class).pattern();
        assertFalse(pattern.contains("\\Q"), pattern);
        assertEquals("http:\\/\\/host\\.a\\?b", RouteNavigation.quote("http://host.a?b"));
    }

    @Test
    void baseClassSignaturesStayFreeOfVaadinTypes() {
        // JUnit reflects over AbstractBasePlaywrightIT's methods: a Vaadin type in a
        // signature would break test classpaths without flow-server
        for (java.lang.reflect.Method m : AbstractBasePlaywrightIT.class.getDeclaredMethods()) {
            List<Class<?>> types = new java.util.ArrayList<>(List.of(m.getParameterTypes()));
            types.add(m.getReturnType());
            for (Class<?> type : types) {
                assertFalse(type.getName().startsWith("com.vaadin."), m + " uses " + type.getName());
            }
        }
    }

    @Test
    void readsDecodedQueryParameters() {
        assertEquals("2", RouteNavigation.queryParameter("http://h/p?tab=2&x=1", "tab"));
        assertEquals("a b", RouteNavigation.queryParameter("http://h/p?note=a+b", "note"));
        assertEquals("a b", RouteNavigation.queryParameter("http://h/p?note=a%20b#f", "note"));
        assertEquals("", RouteNavigation.queryParameter("http://h/p?flag", "flag"));
        assertNull(RouteNavigation.queryParameter("http://h/p", "tab"));
    }

    @Test
    void joinsBaseUrlAndPath() {
        assertEquals("http://h/ctx/a/b", RouteNavigation.url("http://h/ctx/", "a/b"));
        assertEquals("http://h/ctx/a/b", RouteNavigation.url("http://h/ctx", "/a/b"));
        assertEquals("http://h/", RouteNavigation.url("http://h/", ""));
    }
}
