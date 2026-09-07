# LoginFormElement / LoginOverlayElement

> Full API (methods, signatures, descriptions): see [api-reference.md](../../skills/vaadin-playwright-test/api-reference.md). This note covers only behaviour that isn't obvious from the signatures.

Both elements extend `AbstractLoginElement`, because `<vaadin-login-form>` and
`<vaadin-login-overlay>` render the exact same login form; the overlay only adds
a modal wrapper with a branding area on top of it.

## Every visible string comes from the i18n object

Title, field labels, button captions, error message and footer are all driven by
the component's `LoginI18n`. The assertion helpers therefore take the expected
text as a parameter — never assume the English defaults:

```java
LoginFormElement loginForm = LoginFormElement.get(page);
loginForm.assertTitle("Log in");                 // i18n.form.title
loginForm.getUsernameField().assertLabel("Username");
loginForm.assertErrorTitle("Incorrect username or password");
```

## Fields and buttons are resolved structurally, not by text

For the same reason the lookups do not go through the captions:

* the username and password fields are found by the ids the component gives
  them (`vaadinLoginUsername` / `vaadinLoginPassword`),
* the submit and forgot-password buttons are found through their slots.

The slot lookups use `xpath=./vaadin-button[@slot='...']` on purpose. A CSS
`[slot='submit']` would also match the `<slot name="submit" slot="submit">`
element inside the component's shadow root, because Playwright's CSS engine
pierces open shadow roots; the relative XPath does not.

## The forgot-password button is hidden, not removed

`setForgotPasswordButtonVisible(false)` keeps the button in the DOM with the
`hidden` attribute, so assert on visibility rather than on presence:

```java
assertThat(loginForm.getForgotPasswordButton().getLocator()).isHidden();
```

## The error message container is always present

The `error-message` part is in the DOM at all times and is hidden while the
component's `error` property is `false`. `isErrorVisible()`, `assertErrorVisible()`
and `assertNoError()` therefore assert visibility, not presence.

## The form self-disables while a login is in flight

Submitting sets the component's `disabled` property, which the component clears
again when the application reports an error (so the user can retry). The property
is not reflected to an attribute on the host, so:

* `isDisabled()` reads the `disabled` DOM property of the host,
* `assertEnabled()` / `assertDisabled()` assert on the submit button, which
  mirrors the state and lets the Playwright assertion retry.

## The overlay resolves through its overlay element

Like `DialogElement`, the `<vaadin-login-overlay>` host is only a positioning
anchor: the card, the branding area and the form wrapper live in the
`<vaadin-login-overlay-wrapper>` inside its shadow root. `isVisible()`,
`assertVisible()`, `assertHidden()`, `isOpen()` and `assertClosed()` all resolve
through that overlay, while `assertOpen()` checks the `opened` attribute on the
host.

Note that the overlay has two titles: `getTitleLocator()` / `assertTitle(...)`
refer to the title of the form (`i18n.form.title`), while
`getHeaderTitleLocator()` / `assertHeaderTitle(...)` and `getDescriptionLocator()`
/ `assertDescription(...)` refer to the application branding above it.

```java
loginOverlay.assertOpen();
loginOverlay.assertHeaderTitle("DramaFinder");        // application title
loginOverlay.assertDescription("Log in to continue"); // application description
loginOverlay.assertTitle("Log in");                   // form title
loginOverlay.login("admin", "secret");
loginOverlay.assertClosed();
```

The application title is also the overlay's accessible name, which is what
`LoginOverlayElement.getByTitle(page, ...)` matches.
