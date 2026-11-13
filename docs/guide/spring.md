# Spring Boot and Spring Security

## Spring Boot

A test of a Spring Boot application extends `SpringPlaywrightIT`:

```java
import org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT;

public class OrdersViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "orders";
    }

    @Test
    void showsTheOpenOrders() {
        GridElement.get(page).assertRowCount(3);
    }
}
```

`SpringPlaywrightIT` is annotated with
`@SpringBootTest(webEnvironment = RANDOM_PORT)`: it starts the application on a
random port and builds `getUrl()` from it, servlet context path included. The
test class is a regular Spring test, so you can inject beans and mock them:

```java
public class OrdersViewIT extends SpringPlaywrightIT {

    @Autowired
    OrderRepository orders;

    @MockitoBean
    PaymentGateway payments;

    @BeforeEach
    void seed() {
        orders.save(new Order("1001", Status.OPEN));
        page.reload(); // the view was opened before this method ran
    }
}
```

!!! note "Order of `@BeforeEach` methods"

    The base class opens the view in its own `@BeforeEach`, which runs before
    yours. Data you create in your `@BeforeEach` is only visible after a
    reload or a `navigate(..)`.

DramaFinder never adds Spring to your classpath: `SpringPlaywrightIT` uses the
`spring-boot-starter-test` your project already has.

## Signing in with Spring Security

Don't drive the login view in every test. Annotate the test (method or class)
with Spring Security Test's annotations and the page starts signed in:

```java
public class AdminViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "admin";
    }

    @Test
    @WithMockUser(username = "alice", roles = "ADMIN")
    void adminSeesTheUserList() {
        GridElement.get(page).assertColumnPresent("Username");
    }

    @Test
    @WithMockUser(roles = "USER")
    void userIsSentAway() {
        assertAt(AccessDeniedView.class);
    }

    @Test
    @WithUserDetails("bob")          // loaded from your UserDetailsService
    void auditorSeesReadOnlyList() {
        ButtonElement.getByText(page, "Delete").assertDisabled();
    }

    @Test
    @WithAnonymousUser
    void anonymousIsRedirectedToLogin() {
        assertAt(LoginView.class);
    }
}
```

It needs `spring-security-test` in test scope:

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
```

The user is put into the HTTP session through an endpoint registered only in
the test's Spring context; your security configuration and access checks run
as they do in production.

### Switching user during a test

```java
@Test
@WithMockUser("alice")
void ownerSeesTheEditButton() {
    ButtonElement.getByText(page, "Edit").assertVisible();

    loginAs("erin", "USER");          // replaces the session and reloads
    ButtonElement.getByText(page, "Edit").assertHidden();
}
```

### Two users at once

`newPageAs` opens the view in a new browser context, with its own cookies and
session:

```java
@Test
@WithMockUser("alice")
void messagesReachTheOtherUser() {
    Page bob = newPageAs("bob");

    MessageInputElement.get(page).typeAndSubmit("Hi Bob");
    MessageListElement.get(bob).assertMessageCount(1);
}
```

The extra contexts close after the test.

### Without `SpringPlaywrightIT`

A test that manages its own `BrowserContext` (a JUnit extension, another
framework) imports the sign-in endpoint and calls `SpringTestLogin`:

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(TestLoginConfiguration.class)
class MyIT {

    @Autowired
    ApplicationContext applicationContext;

    @LocalServerPort
    int port;

    @Test
    void signsIn() {
        BrowserContext context = browser.newContext();
        String baseUrl = "http://localhost:" + port + "/";
        SpringTestLogin.signIn(applicationContext, context, baseUrl, "alice", "ADMIN");
        Page page = context.newPage();
        page.navigate(baseUrl + "admin");
    }
}
```

`signInTestUser(..)` signs in the `@WithMockUser` user of the running test,
`signIn(.., authentication)` takes your own `Authentication`, and
`signInUrl(..)` returns a one-time URL for a browser DramaFinder does not
drive. Check `TestLoginConfiguration.isAvailable()` first if Spring Security may
be missing from the classpath.

## Testing the login view itself

Keep one test that goes through the real login form:

```java
@Test
void loginWithValidCredentials() {
    navigate(LoginView.class);
    LoginFormElement login = LoginFormElement.get(page);
    login.login("alice", "password");
    assertAt(DashboardView.class);
}
```

See [Login](../specifications/LoginElement.md).
