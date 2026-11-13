# Avatar

`AvatarElement` wraps `<vaadin-avatar>` (Flow: `Avatar`). It reads and asserts
the avatar's name, abbreviation, image and color index, and exposes the focus,
style, theme and tooltip mixins.

| | |
|---|---|
| Web component | `<vaadin-avatar>` |
| Flow component | `com.vaadin.flow.component.avatar.Avatar` |
| Wrapper | `org.vaadin.addons.dramafinder.element.AvatarElement` |
| Typed query | `find(page, AvatarElement.class)` → `ComponentQuery` |
| API | [AvatarElement](../../skills/vaadin-playwright-test/api-reference.md#avatarelement) |

## Finding the component

`getByName` matches the avatar's `name` attribute — the person's name you passed
to `new Avatar("Jane Smith")` — **in full and case-sensitively**; two avatars with
that name fail with a Playwright strict-mode error.

```java
// View
Avatar avatar = new Avatar("Jane Smith");
avatar.setId("avatar-with-name");
Div container = new Div(avatar);
container.setId("avatar-container");
```

```java
// Test
AvatarElement jane = AvatarElement.getByName(page, "Jane Smith");
AvatarElement inContainer = AvatarElement.getByName(page.locator("#avatar-container"), "Jane Smith");
AvatarElement byId = AvatarElement.getById(page, "avatar-with-name");
AvatarElement byTestId = AvatarElement.getByTestId(page, "current-user");
AvatarElement first = AvatarElement.get(page);                                // first avatar on the page
AvatarElement firstInContainer = AvatarElement.get(page.locator("#avatar-container"));
```

The typed query has the filters every component has (id, test id, attribute,
class name, accessible name):

```java
import static org.vaadin.addons.dramafinder.element.query.ElementQueries.find;

AvatarElement custom = find(page, AvatarElement.class).withClassName("custom-avatar").single();
AvatarElement named = find(page, AvatarElement.class).withAttribute("name", "Jane Smith").single();
```

## Name and abbreviation

```java
// View
Avatar withName = new Avatar("Jane Smith");
Avatar withAbbr = new Avatar();
withAbbr.setAbbreviation("AB");
```

```java
// Test
AvatarElement jane = AvatarElement.getById(page, "avatar-with-name");
jane.assertName("Jane Smith");

AvatarElement initials = AvatarElement.getById(page, "avatar-with-abbr");
initials.assertAbbreviation("AB");
```

Both assertions retry on the web component's `name` / `abbr` property. The getters
`getName()` and `getAbbreviation()` return `null` when the property is not set.

## Image

```java
// View
Avatar withImage = new Avatar();
withImage.setImage("images/jane.png");
```

```java
// Test
AvatarElement.getById(page, "avatar-with-image").assertHasImage();
AvatarElement.getById(page, "avatar-no-image").assertHasNoImage();

String url = AvatarElement.getById(page, "avatar-with-image").getImage();   // null when not set
```

## Color index

```java
// View
Avatar colored = new Avatar();
colored.setColorIndex(3);
```

```java
// Test
AvatarElement avatar = AvatarElement.getById(page, "avatar-color-index");
assertEquals(3, avatar.getColorIndex());   // null when not set
```

There is no retrying color-index assertion; use the getter, or
`assertThat(avatar.getLocator()).hasJSProperty("colorIndex", 3)` when the index
changes asynchronously.

## Changing properties from the test

The setters write the web component's property in the browser, e.g. to check
how the avatar renders without a server change:

```java
AvatarElement avatar = AvatarElement.getById(page, "avatar-with-name");
avatar.setName("New Name");
avatar.assertName("New Name");

avatar.setAbbreviation("CD");
avatar.setImage("data:image/svg+xml,...");
avatar.setColorIndex(5);
```

## Theme, tooltip, style and focus

```java
// View
Avatar small = new Avatar();
small.addThemeVariants(AvatarVariant.LUMO_SMALL);
Avatar custom = new Avatar();
custom.addClassName("custom-avatar");
Avatar focusable = new Avatar();
focusable.getElement().setAttribute("tabindex", "0");
```

```java
// Test
AvatarElement.getById(page, "avatar-with-theme").assertTheme("small");
AvatarElement.getById(page, "avatar-with-class").assertCssClass("custom-avatar");
AvatarElement.getById(page, "avatar-with-tooltip").assertTooltipHasText("Tooltip");

AvatarElement focusable = AvatarElement.getById(page, "avatar-focusable");
focusable.focus();
focusable.assertIsFocused();
focusable.blur();
focusable.assertIsNotFocused();
```

`assertTheme` compares the whole `theme` attribute; use `assertHasThemeVariant`
to check one variant among several.

## Pitfalls

- **`getByName` is exact.** A partial or differently-cased name does not match.
- **Names with a single quote.** `getByName` builds a CSS attribute selector
  (`[name='...']`), so a name containing `'` (e.g. `O'Brien`) breaks the
  selector; find such an avatar by id, test id or the typed query.

## See also

- [Avatar Group](AvatarGroupElement.md) — several avatars with an overflow menu
- [Tooltip](TooltipElement.md)
- [Assertions](../guide/assertions.md)
- [Finding components](../guide/finding-components.md)
- [Core concepts](../guide/core-concepts.md)
- Full example: [AvatarViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/AvatarViewIT.java)
