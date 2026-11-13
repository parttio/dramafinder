# Navigation

Each test starts on the view `getView()` returns. To move to another view, or to
check where the application took the user, navigate **by route class** rather
than by a hand-built URL: renaming a route or a `@RoutePrefix` then cannot break
a test silently.

## The starting view

```java
public class OrdersViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "orders"; // appended to getUrl() before each test
    }
}
```

`getUrl()` is the application root (`http://localhost:54321/`, including the
servlet context path); `getView()` defaults to the root view when you don't
override it.

## Navigating by route class

```java
// Views
@Route(value = "profile", layout = MainLayout.class)
public class ProfileView extends VerticalLayout { }

@Route("article")
public class ArticleView extends Div implements HasUrlParameter<String> { }

@Route("user/:userId/edit")
public class UserEditView extends Div implements BeforeEnterObserver { }
```

```java
navigate(ProfileView.class);                                         // /profile
navigate(ArticleView.class, "42");                                   // /article/42
navigate(UserEditView.class, new RouteParameters("userId", "42"));   // /user/42/edit
```

The path is resolved from `@Route`, the `@RoutePrefix` of the parent layouts
and Vaadin's naming convention for a `@Route` without a value
(`GreetingView` → `greeting`). `navigate` waits until Vaadin has finished
loading the view.

## Asserting where the browser is

```java
ButtonElement.getByText(page, "Open profile").click();
assertAt(ProfileView.class);
```

`assertAt` retries until the timeout, so it follows redirects, forwards and
navigation guards:

```java
navigate(AdminView.class);
assertAt(LoginView.class);             // not signed in: the access checker reroutes

navigate(LegacyProfileView.class);     // forwards in beforeEnter
assertAt(ProfileView.class);
```

A parameterized route matches any parameter value. To check the exact path or
the query string:

```java
assertPath("article/42");              // path under getUrl(), query ignored
assertQueryParameter("tab", "2");      // ?tab=2
assertQueryParameter("note", "a b");   // compared decoded
assertQueryParameter("tab", null);     // no "tab" parameter
```

## Without the base class

The same helpers are static methods on `RouteNavigation`, for tests that manage
their own `Page`:

```java
import org.vaadin.addons.dramafinder.RouteNavigation;

String base = "http://localhost:" + port + "/";
RouteNavigation.navigate(page, base, ProfileView.class);
RouteNavigation.assertAt(page, base, ProfileView.class);
RouteNavigation.open(page, base, "orders?status=open");
String path = RouteNavigation.pathOf(UserEditView.class, new RouteParameters("userId", "42"));
```

`RouteNavigation` needs `com.vaadin:flow-server` on the test classpath, which a
Vaadin application already has.

## A grid row that navigates

```java
// View: grid.addItemClickListener(e -> UI.getCurrent().navigate(OrderView.class, e.getItem().getId()))
GridElement.get(page).findRow(0).get().click();
assertAt(OrderView.class);
assertPath("order/1001");
```
