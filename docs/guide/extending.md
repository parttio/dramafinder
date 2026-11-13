# Writing your own element

When your application has a component DramaFinder does not wrap — an add-on, a
web component of your own, or a composite you test in many places — write an
element for it. It is a small class, and it gets the same lookups, waits and
assertions as the built-in ones.

## A wrapper for a web component

Say the application uses a `<rating-stars>` web component with a `value`
property, a `readonly` attribute and one `<button>` per star:

```java
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.PlaywrightElement;
import org.vaadin.addons.dramafinder.element.VaadinElement;
import org.vaadin.addons.dramafinder.element.query.FieldQuery;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.shared.HasLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasReadOnlyElement;
import org.vaadin.addons.dramafinder.element.utils.IdLocator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.vaadin.addons.dramafinder.element.query.ElementQueries.find;

/** Element for {@code <rating-stars>}: reads and sets the rating by clicking a star. */
@PlaywrightElement(RatingElement.TAG_NAME)
public class RatingElement extends VaadinElement
        implements HasLabelElement, HasReadOnlyElement, Findable<FieldQuery<RatingElement>> {

    public static final String TAG_NAME = "rating-stars";

    public RatingElement(Locator locator) {
        super(locator);
    }

    /** The rating whose visible label is exactly {@code label}. */
    public static RatingElement getByLabel(Page page, String label) {
        return find(page, RatingElement.class).withLabel(label).single();
    }

    public static RatingElement getById(Page page, String id) {
        return new RatingElement(IdLocator.byId(page, TAG_NAME, id));
    }

    /** Click the {@code stars}-th star, as a user does. */
    public void rate(int stars) {
        getLocator().locator("button").nth(stars - 1).click();
        waitAfterAction(); // wait for the server round-trip the click triggers
    }

    public int getValue() {
        return ((Number) getProperty("value")).intValue();
    }

    /** Assert the rating, retrying until it matches. */
    public void assertValue(int stars) {
        assertThat(getLocator()).hasJSProperty("value", stars);
    }
}
```

```java
RatingElement rating = RatingElement.getByLabel(page, "Your rating");
rating.rate(4);
rating.assertValue(4);
rating.assertNotReadOnly();     // from HasReadOnlyElement
rating.assertLabel("Your rating"); // from HasLabelElement
```

The rules the built-in elements follow:

- **Every action ends with `waitAfterAction()`**, right after the Playwright
  call, so the action returns once the server answered. Never call
  `waitForVaadinIdle()` there: it ignores `AutoWait.withoutWaiting(..)`.
- **Assertions retry.** Build them on Playwright's `assertThat` (`hasAttribute`,
  `hasJSProperty`, `hasText`...) or poll with `page.waitForCondition(..)` —
  never `assertEquals` on a getter.
- **`null` means absent** in an assertion that takes the expected value.
- **Lookups match exactly**, with no `.first()`, so a duplicate fails loudly.
- **Attributes vs properties:** `getAttribute(..)` for HTML attributes,
  `getProperty(..)` / `hasJSProperty(..)` for DOM properties.

## Reuse the mixins

Implement the mixin interfaces that match the component, and you inherit their
methods and assertions. Override a locator method when the state lives on an
inner element:

| Mixin | Override when |
|---|---|
| `HasValueElement` | `getInputLocator()` — the native input, if not `[slot='input']` |
| `HasEnabledElement` | `getEnabledLocator()` — the element carrying `disabled` |
| `HasDisabledAttributeElement` | use instead of `HasEnabledElement` when the host is not a native control or an ARIA role Playwright treats as disableable (`listitem`, `list`, a plain container) |
| `FocusableElement` | `getFocusLocator()` — the element that takes focus |
| `HasLabelElement`, `HasHelperElement`, `HasPrefixElement`, `HasSuffixElement` | the component uses other slot names |
| `HasThemeElement`, `HasStyleElement`, `HasTooltipElement`, `HasAriaLabelElement` | rarely |

```java
public class RatingElement extends VaadinElement
        implements HasLabelElement, HasEnabledElement, FocusableElement {

    @Override
    public Locator getEnabledLocator() {
        return getLocator().locator("button").first(); // the stars carry disabled
    }

    @Override
    public Locator getFocusLocator() {
        return getLocator().locator("button").first();
    }
}
```

The full list is under
[Shared mixins](../../skills/vaadin-playwright-test/api-reference.md#shared-mixins).

## Extend a built-in element

A component built on a Vaadin one can extend its element and add what differs:

```java
/** A text field with a "copy to clipboard" suffix button. */
public class CopyFieldElement extends TextFieldElement {

    public CopyFieldElement(Locator locator) {
        super(locator);
    }

    public static CopyFieldElement getByLabel(Page page, String label) {
        return new CopyFieldElement(TextFieldElement.getByLabel(page, label).getLocator());
    }

    public void copy() {
        getSuffixLocator().getByRole(AriaRole.BUTTON).click();
        waitAfterAction();
    }
}
```

## Wrap a composite of your own

A form or a card used across views can be wrapped the same way — a page object
built from DramaFinder elements:

```java
public class AddressFormElement extends VaadinElement {

    public AddressFormElement(Locator locator) {
        super(locator);
    }

    public static AddressFormElement getById(Page page, String id) {
        return new AddressFormElement(page.locator("#" + id));
    }

    public TextFieldElement street() {
        return TextFieldElement.getByLabel(getLocator(), "Street");
    }

    public ComboBoxElement country() {
        return ComboBoxElement.getByLabel(getLocator(), "Country");
    }

    public void fill(String street, String country) {
        street().setValue(street);
        country().selectItem(country);
    }
}
```

```java
AddressFormElement billing = AddressFormElement.getById(page, "billing-address");
billing.fill("Ruukinkatu 2", "Finland");
billing.country().assertValue("Finland");
```

## Make it findable

`@PlaywrightElement` (the tag, or a selector narrowing it) and
`Findable<Query>` make an element usable with `find(page, X.class)` — which is
also the easiest way to write its factories, as `RatingElement.getByLabel`
above does:

```java
RatingElement rating = find(page, RatingElement.class).withLabel("Your rating").single();
RatingElement byTestId = find(page, RatingElement.class).withTestId("product-rating").single();
```

Pick the query whose filters really apply to the component:

| Query | Filters on top of id, test id, class, attribute, accessible name |
|---|---|
| `ComponentQuery` | none |
| `FieldQuery` | `withLabel`, `withLabelContaining` |
| `InputFieldQuery` | label filters and `withPlaceholder` |
| `TextQuery`, `ButtonQuery` | `withText`, `withTextContaining` |

Check each filter against the real component before relying on it: a filter
reads the host's `slot="label"` child, `placeholder` attribute or own text.

## Contribute it

If the component is part of Vaadin, consider
[opening an issue or a pull request](https://github.com/parttio/dramafinder/issues).
The repository guidelines (`AGENTS.md`) list the conventions a new element
follows: factories, Javadoc, mixins and the tests that enforce them.
