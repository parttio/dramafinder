# Login Form and Login Overlay

`LoginFormElement` wraps `<vaadin-login-form>` (Flow: `LoginForm`) and
`LoginOverlayElement` wraps `<vaadin-login-overlay>` (Flow: `LoginOverlay`).
They let a test log in, check the error message after rejected credentials,
read every i18n string, and — for the overlay — check its branding and whether
it is open.

| | |
|---|---|
| Web components | `<vaadin-login-form>`, `<vaadin-login-overlay>` |
| Flow components | `com.vaadin.flow.component.login.LoginForm`, `com.vaadin.flow.component.login.LoginOverlay` |
| Wrappers | `org.vaadin.addons.dramafinder.element.LoginFormElement`, `LoginOverlayElement` (both extend `AbstractLoginElement`) |
| Typed query | `find(page, LoginFormElement.class)` / `find(page, LoginOverlayElement.class)` → `ComponentQuery` |
| API | [LoginFormElement](../../skills/vaadin-playwright-test/api-reference.md#loginformelement), [LoginOverlayElement](../../skills/vaadin-playwright-test/api-reference.md#loginoverlayelement), [AbstractLoginElement](../../skills/vaadin-playwright-test/api-reference.md#abstractloginelement) |

Both elements extend `AbstractLoginElement`, because `<vaadin-login-form>` and
`<vaadin-login-overlay>` render the exact same login form; the overlay only adds
a modal wrapper with a branding area on top of it. Everything in the sections up
to "Disabled state" works on both.

!!! tip "Not testing the login itself?"

    A test that only needs a signed-in user does not have to go through the
    login form: with Spring Security, annotate it with `@WithMockUser` or call
    `loginAs(...)` from
    [`SpringPlaywrightIT`](../../skills/vaadin-playwright-test/api-reference.md#springplaywrightit).

## Finding the component

```java
// View
LoginForm loginForm = new LoginForm();
loginForm.setId("basic-login-form");
Div wrapper = new Div(loginForm);
wrapper.setId("basic-login-wrapper");
```

```java
// Test
LoginFormElement loginForm = LoginFormElement.get(page);              // first on the page
LoginFormElement scoped = LoginFormElement.get(page.locator("#basic-login-wrapper"));
LoginFormElement byId = LoginFormElement.getById(page, "basic-login-form");
LoginFormElement byQuery = find(page, LoginFormElement.class)
        .withId("basic-login-form").single();
```

`get(...)` returns the **first** login form in the page or the scope. A login
form has no visible label; `getById` / `getByTestId` (each with a `Page` and a
`Locator` overload) are the exact lookups.

The overlay is found by its application title, which is its accessible name:

```java
// View
LoginOverlay loginOverlay = new LoginOverlay();
loginOverlay.setTitle("DramaFinder");
loginOverlay.setDescription("Log in to find your drama");
add(new Button("Open login overlay", e -> loginOverlay.setOpened(true)));
```

```java
// Test
ButtonElement.getByText(page, "Open login overlay").click();

// by application title: exact, case-sensitive
LoginOverlayElement overlay = LoginOverlayElement.getByTitle(page, "DramaFinder");

LoginOverlayElement first = LoginOverlayElement.get(page);       // first on the page
LoginOverlayElement byRole = new LoginOverlayElement(page);      // by its dialog role
```

`LoginOverlayElement` also has `getById` / `getByTestId`.

## Logging in

`login(username, password)` fills both fields, clicks the submit button and
waits for the server round-trip:

```java
// View
loginForm.addLoginListener(event -> {
    if ("admin".equals(event.getUsername()) && "secret".equals(event.getPassword())) {
        loginOutput.setText("Welcome " + event.getUsername());
    } else {
        loginForm.setError(true);
    }
});
```

```java
// Test
LoginFormElement loginForm = LoginFormElement.get(page.locator("#basic-login-wrapper"));
loginForm.login("admin", "secret");
assertThat(page.locator("#login-output")).hasText("Welcome admin");
loginForm.assertNoError();
```

The fields and buttons are available as element wrappers for finer control:

```java
loginForm.getUsernameField().setValue("admin");          // TextFieldElement
loginForm.getPasswordField().setValue("secret");         // PasswordFieldElement
loginForm.getUsernameField().assertValue("admin");
loginForm.getSubmitButton().click();                      // ButtonElement
```

## Rejected credentials

```java
loginForm.login("admin", "wrong");

loginForm.assertErrorVisible();
loginForm.assertErrorTitle("Incorrect username or password");
loginForm.assertErrorMessage(
        "Check that you have entered the correct username and password and try again.");
```

The error message container (`error-message` part) is in the DOM at all times
and is hidden while the component's `error` property is `false`.
`isErrorVisible()`, `assertErrorVisible()` and `assertNoError()` therefore
assert visibility, not presence. `getErrorLocator()`, `getErrorTitleLocator()`
and `getErrorMessageLocator()` return the container, its title and its
description.

## Every visible string comes from the i18n object

Title, field labels, button captions, error message and footer are all driven by
the component's `LoginI18n`. The assertion helpers therefore take the expected
text as a parameter — never assume the English defaults:

```java
// View
LoginI18n i18n = LoginI18n.createDefault();
i18n.getForm().setTitle("Connexion");
i18n.getForm().setUsername("Identifiant");
i18n.getForm().setPassword("Mot de passe");
i18n.getForm().setSubmit("Se connecter");
i18n.getErrorMessage().setTitle("Identifiants incorrects");
i18n.getErrorMessage().setMessage("Vérifiez vos identifiants et réessayez.");
i18n.setAdditionalInformation("Contactez le support si besoin.");
LoginForm loginForm = new LoginForm(i18n);
```

```java
// Test
loginForm.assertTitle("Connexion");                                  // i18n.form.title
loginForm.getUsernameField().assertLabel("Identifiant");
loginForm.getPasswordField().assertLabel("Mot de passe");
assertThat(loginForm.getSubmitButton().getLocator()).hasText("Se connecter");
loginForm.assertAdditionalInformation("Contactez le support si besoin."); // footer

loginForm.login("admin", "secret");
loginForm.assertErrorTitle("Identifiants incorrects");
loginForm.assertErrorMessage("Vérifiez vos identifiants et réessayez.");
```

`getTitleLocator()` and `getFooterLocator()` return the title and footer parts.

## Fields and buttons are resolved structurally, not by text

For the same reason the lookups do not go through the captions:

* the username and password fields are found by the ids the component gives
  them (`vaadinLoginUsername` / `vaadinLoginPassword`, the `USERNAME_FIELD_ID`
  and `PASSWORD_FIELD_ID` constants),
* the submit and forgot-password buttons are found through their slots.

The slot lookups use `xpath=./vaadin-button[@slot='...']` on purpose. A CSS
`[slot='submit']` would also match the `<slot name="submit" slot="submit">`
element inside the component's shadow root, because Playwright's CSS engine
pierces open shadow roots; the relative XPath does not.

## Forgot password

```java
// View: loginForm.addForgotPasswordListener(e -> loginOutput.setText("Forgot password"));
loginForm.getForgotPasswordButton().click();
assertThat(page.locator("#login-output")).hasText("Forgot password");
```

`setForgotPasswordButtonVisible(false)` keeps the button in the DOM with the
`hidden` attribute, so assert on visibility rather than on presence:

```java
assertThat(loginForm.getForgotPasswordButton().getLocator()).isHidden();
```

## Disabled state

Submitting sets the component's `disabled` property, which the component clears
again when the application reports an error (so the user can retry). The
application can also disable it (`loginForm.setEnabled(false)`). The property is
not reflected to an attribute on the host, so:

* `isDisabled()` reads the `disabled` DOM property of the host, once,
* `assertEnabled()` / `assertDisabled()` assert on the submit button
  (`getEnabledLocator()`), which mirrors the state and lets the Playwright
  assertion retry.

```java
loginForm.assertEnabled();
ButtonElement.getByText(page, "Disable login").click();
loginForm.assertDisabled();
```

## The login overlay

### Opening and closing

Like `DialogElement`, the `<vaadin-login-overlay>` host is only a positioning
anchor: the card, the branding area and the form wrapper live in the
`<vaadin-login-overlay-wrapper>` inside its shadow root (`getOverlayLocator()`).
`isVisible()`, `assertVisible()`, `assertHidden()`, `isOpen()` and
`assertClosed()` all resolve through that overlay, while `assertOpen()` checks
the `opened` attribute on the host.

```java
LoginOverlayElement loginOverlay = new LoginOverlayElement(page);
loginOverlay.assertHidden();

ButtonElement.getByText(page, "Open login overlay").click();
loginOverlay.assertOpen();
loginOverlay.assertVisible();

loginOverlay.login("admin", "secret");   // the view closes the overlay on success
loginOverlay.assertClosed();
```

After rejected credentials the overlay stays open and shows the error:

```java
loginOverlay.login("admin", "wrong");
loginOverlay.assertOpen();
loginOverlay.assertErrorVisible();
```

### Two titles: branding and form

The overlay has two titles: `getTitleLocator()` / `assertTitle(...)` refer to
the title of the form (`i18n.form.title`), while `getHeaderTitleLocator()` /
`assertHeaderTitle(...)` and `getDescriptionLocator()` / `assertDescription(...)`
refer to the application branding above it.

```java
loginOverlay.assertHeaderTitle("DramaFinder");                   // application title
loginOverlay.assertDescription("Log in to find your drama");     // application description
loginOverlay.assertTitle("Log in");                              // form title
```

With i18n, the branding comes from `LoginI18n.Header`:

```java
// View
LoginI18n.Header header = new LoginI18n.Header();
header.setTitle("DramaFinder FR");
header.setDescription("Connectez-vous pour trouver votre drama");
i18n.setHeader(header);
```

```java
// Test
LoginOverlayElement overlay = LoginOverlayElement.getByTitle(page, "DramaFinder FR");
overlay.assertHeaderTitle("DramaFinder FR");
overlay.assertDescription("Connectez-vous pour trouver votre drama");
overlay.assertTitle("Connexion");
```

The application title is also the overlay's accessible name, which is what
`LoginOverlayElement.getByTitle(page, ...)` matches.

## Theme and CSS classes

Both elements implement `HasThemeElement` and `HasStyleElement`, so
`assertTheme(...)`, `assertHasThemeVariant(...)` and `assertCssClass(...)` work
on the host.

## Pitfalls

- **Never hard-code the English captions.** Every string comes from
  `LoginI18n`; pass the expected text to the assertion.
- **The error container is always present.** Assert visibility
  (`assertErrorVisible()` / `assertNoError()`), not presence.
- **The forgot-password button is hidden, not removed.** Assert
  `isHidden()` on its locator.
- **The form disables itself while a login is in flight.** `assertEnabled()` /
  `assertDisabled()` go through the submit button; `isDisabled()` reads the
  property once, without retrying.
- **Overlay visibility lives on the overlay wrapper**, not on the host; and
  `assertTitle` is the form title, `assertHeaderTitle` the branding title.
- **`get(...)` returns the first match.** With several login forms on the page,
  scope it with `get(Locator)` or use `getById`.

## See also

- [Text Field](TextFieldElement.md), [Password Field](PasswordFieldElement.md),
  [Button](ButtonElement.md), [Dialog](DialogElement.md)
- [Finding components](../guide/finding-components.md),
  [Assertions](../guide/assertions.md),
  [Actions and waiting](../guide/actions-and-waiting.md),
  [Core concepts](../guide/core-concepts.md)
- Full examples: [LoginFormViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/LoginFormViewIT.java),
  [LoginOverlayViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/LoginOverlayViewIT.java)
