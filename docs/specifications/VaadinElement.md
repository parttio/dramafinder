# VaadinElement (base class)

`VaadinElement` is the base class of every DramaFinder element. Whatever the
component, these methods are always available: clicking, reading text,
visibility, DOM properties, the underlying locator and scoped queries.

| | |
|---|---|
| Wrapper | `org.vaadin.addons.dramafinder.element.VaadinElement` (abstract) |
| Implements | `HasLocatorElement` |
| API | [VaadinElement](../../skills/vaadin-playwright-test/api-reference.md#vaadinelement) |

## Class hierarchy

Most wrappers extend `VaadinElement` directly. A few share a base with a
sibling:

```text
VaadinElement (abstract)
    ├── TextFieldElement
    │     ├── EmailFieldElement
    │     ├── PasswordFieldElement
    │     └── TextAreaElement
    ├── AbstractNumberFieldElement
    │     ├── IntegerFieldElement
    │     └── NumberFieldElement
    ├── AbstractLoginElement
    │     ├── LoginFormElement
    │     └── LoginOverlayElement
    ├── GridElement
    │     └── TreeGridElement
    └── … every other wrapper
```

So an `EmailFieldElement` can be passed wherever a `TextFieldElement` is
expected, and a `TreeGridElement` has every `GridElement` method.

## Clicking and reading text

```java
ButtonElement save = ButtonElement.getByText(page, "Save");
save.click();                 // clicks the host, then waits for the server round-trip

CardElement card = CardElement.getByTitle(page, "Revenue");
String text = card.getText(); // text content of the component root
```

`click()` clicks the centre of the component host. Many elements offer a more
precise action (`selectTab`, `check`, `RowElement.click()`); prefer those.

## Visibility

```java
// View: details.setVisible(false) until "Show details" is clicked
DetailsElement details = DetailsElement.getBySummaryText(page, "Advanced");
details.assertHidden();
ButtonElement.getByText(page, "Show details").click();
details.assertVisible();

details.assertVisible(true);  // boolean variant, handy in parameterised tests
boolean shown = details.isVisible(); // one-shot read, no retry
```

`assertVisible` / `assertHidden` retry; `isVisible` / `isHidden` don't.
Overlay components refine them: a `DialogElement` checks its overlay, a
`ConfirmDialogElement.assertHidden()` also passes when the dialog is gone.

## DOM properties

Read or set a property of the web component when no dedicated method exists:

```java
TextFieldElement name = TextFieldElement.getByLabel(page, "Name");
Object autoselect = name.getProperty("autoselect");

ProgressBarElement progress = ProgressBarElement.getById(page, "upload-progress");
progress.setProperty("value", 0.5); // waits for the server if the property is synchronised
```

To assert a property with retries, use Playwright on the locator:

```java
assertThat(name.getLocator()).hasJSProperty("autoselect", true);
```

Remember the difference between an HTML **attribute** (`getLocator().getAttribute("theme")`)
and a DOM **property** (`getProperty("value")`): many Vaadin properties are not
reflected to attributes.

## The locator

`getLocator()` returns the Playwright `Locator` of the component host, for
anything the API does not cover:

```java
CardElement card = CardElement.getByTitle(page, "Revenue");
card.getLocator().screenshot(new Locator.ScreenshotOptions().setPath(Paths.get("target/card.png")));
card.getLocator().scrollIntoViewIfNeeded();
assertThat(card.getLocator()).hasAttribute("theme", "outlined");
```

It is also the scope for lookups inside the component:

```java
DialogElement dialog = DialogElement.getByHeaderText(page, "New contact");
TextFieldElement.getByLabel(dialog.getLocator(), "Name").setValue("Ada");
```

## Scoped queries

`find(..)` starts a [typed query](../guide/finding-components.md#the-typed-query-builder)
inside the component:

```java
DialogElement dialog = DialogElement.getByHeaderText(page, "Delete account?");
dialog.find(ButtonElement.class).withText("Delete").single().click();

FormLayoutElement form = FormLayoutElement.get(page);
List<TextFieldElement> fields = form.find(TextFieldElement.class).all();
```

## Waiting for the server

Every element exposes, through `HasLocatorElement`:

- `waitAfterAction()` — waits for the round-trip an action just triggered,
  unless automatic waiting is off. Element actions call it for you; call it
  yourself after a raw Playwright action in a [custom element](../guide/extending.md).
- `waitForVaadinIdle()` — waits until Vaadin has no request in flight, always.
  Use it in a test after a raw Playwright action, or inside
  `AutoWait.withoutWaiting(..)`.

```java
TextFieldElement search = TextFieldElement.getByLabel(page, "Search");
search.getInputLocator().press("Enter"); // raw Playwright: does not wait
search.waitForVaadinIdle();
```

See [Actions and waiting](../guide/actions-and-waiting.md).

## Shared behaviour through mixins

Elements add behaviour by implementing mixin interfaces — `HasValueElement`,
`HasEnabledElement`, `HasThemeElement`... — listed for each element in the API
reference and documented once under
[Shared mixins](../../skills/vaadin-playwright-test/api-reference.md#shared-mixins).
[Core concepts](../guide/core-concepts.md#mixins-shared-behaviour) has the
overview, [Assertions](../guide/assertions.md) shows them in use.

## Writing your own element

Extend `VaadinElement` to wrap a component DramaFinder doesn't cover:

```java
@PlaywrightElement(RatingElement.TAG_NAME)
public class RatingElement extends VaadinElement implements HasLabelElement {

    public static final String TAG_NAME = "rating-stars";

    public RatingElement(Locator locator) {
        super(locator);
    }

    public void rate(int stars) {
        getLocator().locator("button").nth(stars - 1).click();
        waitAfterAction();
    }
}
```

The full walk-through, with factories, mixins and the typed query, is in
[Writing your own element](../guide/extending.md).

## See also

- [Core concepts](../guide/core-concepts.md)
- [Finding components](../guide/finding-components.md)
- [Element index](../../skills/vaadin-playwright-test/api-reference.md#element-index)
