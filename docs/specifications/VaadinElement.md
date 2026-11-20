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
String text = card.getText(); // text of the card and its children, tooltips left out
```

`getText()` reads the component's light-DOM text, the way `textContent` does —
slotted children (a card's title, a button's prefix) and child components
included — but leaves out the text of every `vaadin-tooltip` inside it. A
tooltip set with `setTooltipText(..)` is a child element of the component it
describes, yet it is not part of that component's text, so a button created as
`new Button("Save")` with a tooltip reads `"Save"`: the same text
`find(page, ButtonElement.class).withText("Save")` found it by. Read the tooltip
itself with `getTooltipText()`.

The text is returned as the DOM holds it: whitespace is not normalized, and
text a component renders in its shadow DOM is not part of it.

This differs from browserless (server-side) tests, where a component's
`getText()` is Flow's `Element.getText()`: the text of its own text nodes only.
There, `new Div(new Span("Hello"))` reads `""`, because the text belongs to the
child span; in the browser, `getText()` reads what was rendered, children
included, and that div reads `"Hello"`. A button with a tooltip reads `"Save"`
in both. Some elements
read a narrower text and say so in their own Javadoc, e.g.
`BadgeElement.getText()` (own text only) and `GridElement.CellElement.getText()`
(the cell's rendered `innerText`).

`click()` clicks the centre of the component host. Many elements offer a more
precise action (`selectTab`, `check`, `RowElement.click()`); prefer those.

`getTagName()` returns the element's tag in lower case, as the DOM's `localName`
reports it: `"vaadin-button"`, `"input"`.

## Components without an element class: `GenericElement`

`VaadinElement.of(locator)` wraps any element — an add-on, a layout, a custom
component, a plain `div` — in a `GenericElement`. It has the `VaadinElement` API
plus CSS class, inline and computed style, theme, focus and enabled-state helpers:

```java
GenericElement box = VaadinElement.of(page.locator("#summary"));
box.getTagName();                        // "div"
box.getInlineStyle("background-color");  // what Component.getStyle() set; null when unset
box.getComputedStyle("display");         // what the browser applies
box.assertCssClass("box");
box.assertTheme("compact");
box.focus();                             // focuses the host; needs a tabindex on a plain div
box.assertDisabled();                    // reads the host's disabled attribute
```

Flow writes `setEnabled(false)` as a `disabled` attribute on any component, so
`isEnabled()`, `assertEnabled()` and `assertDisabled()` read that attribute
(`HasDisabledAttributeElement`) rather than Playwright's enablement check, which
only understands native form controls and a few ARIA roles.

To find such components, query by tag: `find(page, "x-card").withLabel("Card label")`
(see [Finding components](../guide/finding-components.md#components-without-an-element-class)).
The light-DOM lookups below return `GenericElement`s too. Prefer the
component's own element class when there is one.

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

## Light-DOM children and descendants

Playwright's CSS engine pierces shadow roots, so
`getLocator().locator("vaadin-button")` also matches buttons a component
renders internally. To walk only the light DOM — the DOM counterpart of the
server-side component tree — use:

```java
VaadinElement layout = ...;
List<VaadinElement> buttons = layout.getLightDomChildren("vaadin-button");  // direct children
List<VaadinElement> all = layout.getLightDomDescendants(VaadinElement.ANY_TAG); // any depth
```

Neither method enters a shadow root, this component's or a nested one's, so
a text field's internal `vaadin-input-container` is never returned. The tag
is case-insensitive, `"*"` (`ANY_TAG`) matches any element, and anything that
is not a tag name throws `IllegalArgumentException`. The list is a snapshot
taken without waiting, each entry addressed by its position: assert on the
content you expect first (e.g. with `assertThat(...).hasCount(..)` on a
locator) when children are still being added. The entries are plain
`VaadinElement`s; wrap `getLocator()` in a typed element for more.

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
