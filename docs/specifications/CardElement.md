# Card

`CardElement` wraps `<vaadin-card>` (Flow: `Card`). It finds a card by its
title and gives a test a locator for each of the card's slots — title,
subtitle, header and its prefix/suffix, media, content and footer — plus title
and subtitle assertions.

| | |
|---|---|
| Web component | `<vaadin-card>` |
| Flow component | `com.vaadin.flow.component.card.Card` |
| Wrapper | `org.vaadin.addons.dramafinder.element.CardElement` |
| Typed query | `find(page, CardElement.class)` → `ComponentQuery` |
| API | [CardElement](../../skills/vaadin-playwright-test/api-reference.md#cardelement) |

## Finding the component

```java
CardElement lapland = CardElement.getByTitle(page, "Lapland");
CardElement scoped = CardElement.getByTitle(page.locator("#destinations"), "Lapland");
CardElement byId = CardElement.getById(page, "booking-card");
CardElement byTestId = CardElement.getByTestId(page, "booking-card");

// Typed query: generic filters only
CardElement outlined = find(page, CardElement.class).withClassName("lapland-card").single();
List<CardElement> all = find(page, CardElement.class).all();
```

`getByTitle` matches the text of the card's `slot="title"` element in full,
case-sensitively, with no `.first()`: two cards with that title fail with a
strict-mode error.

## Title and subtitle

```java
// View
Card card = new Card();
card.setTitle(new Div("Lapland"));
card.setSubtitle(new Div("The Exotic North"));
```

```java
// Test
CardElement card = CardElement.getByTitle(page, "Lapland");
card.assertTitle("Lapland");
card.assertSubtitle("The Exotic North");

CardElement plain = CardElement.getById(page, "plain-card");
plain.assertSubtitle(null);   // null asserts there is no subtitle element
```

`assertTitle(null)` / `assertSubtitle(null)` assert that the slot is empty
(no element in it), not that it is hidden.

## Slots

Each slot has a locator scoped to the card's **direct** children, so a card
nested in another card's content does not leak into the outer card's slots:

| Slot | Locator |
|---|---|
| `title` | `getTitleLocator()` |
| `subtitle` | `getSubtitleLocator()` |
| `header` | `getHeaderLocator()` |
| `header-prefix` | `getHeaderPrefixLocator()` |
| `header-suffix` | `getHeaderSuffixLocator()` |
| `media` | `getMediaLocator()` |
| `footer` | `getFooterLocator()` |
| content (no slot) | `getContentLocator()` |

```java
// View
Card card = new Card();
card.setTitle(new Div("Lapland"));
card.setHeaderPrefix(new Span("Lapland prefix"));
Span badge = new Span("Arctic");
badge.getElement().getThemeList().add("badge success");
card.setHeaderSuffix(badge);
card.setMedia(new Span("Aurora media"));
card.add(new Paragraph("Lapland is the northern-most region of Finland and an active outdoor destination."));
card.add(new Paragraph("The land of the indigenous Sámi people, known as Sámi homeland or Sápmi, also crosses the northern part of the region."));
```

```java
// Test
CardElement card = CardElement.getByTitle(page, "Lapland");
assertThat(card.getHeaderPrefixLocator()).hasText("Lapland prefix");
assertThat(card.getHeaderSuffixLocator()).hasText("Arctic");
assertThat(card.getMediaLocator()).hasText("Aurora media");

// One element per content component, in order
assertThat(card.getContentLocator()).hasCount(2);
assertThat(card.getContentLocator().first()).containsText("northern-most region of Finland");
assertThat(card.getContentLocator().nth(1)).containsText("indigenous Sámi people");
```

## Actions in the footer and content

Search inside the card with the `Locator` overloads of other elements:

```java
// View
Span status = new Span("Awaiting action");
status.setId("card-action-status");
card.add(status);
card.addToFooter(new Button("Book now", e -> status.setText("Booked")),
        new Button("Reset", e -> status.setText("Awaiting action")));
```

```java
// Test
CardElement card = CardElement.getByTitle(page, "Booking Card");

ButtonElement.getByText(card.getLocator(), "Book now").click();
assertThat(page.locator("#card-action-status")).hasText("Booked");

// Or with a typed query scoped to the card
card.find(ButtonElement.class).withText("Reset").single().click();
assertThat(page.locator("#card-action-status")).hasText("Awaiting action");
```

`getFooterLocator()` matches each footer component, not a footer container. To
act on one of them, narrow it:

```java
ButtonElement book = new ButtonElement(
        card.getFooterLocator().filter(new Locator.FilterOptions().setHasText("Book now")));
book.click();
```

## Theme variants and CSS classes

```java
// View
card.addThemeVariants(CardVariant.OUTLINED);
card.addClassName("lapland-card");
```

```java
// Test
card.assertTheme("outlined");
card.assertHasThemeVariant("outlined");
card.assertCssClass("lapland-card");
```

A card with several variants (`OUTLINED` and `ELEVATED`, say) holds them all in
one `theme` attribute: `assertTheme("outlined elevated")` lists every one, in any
order; `assertHasThemeVariant("elevated")` checks a single one.

## Pitfalls

- Slot locators match one element **per slotted component**: `getContentLocator()`
  and `getFooterLocator()` usually resolve to several elements, so assert with
  `hasCount`, `.first()` / `.nth(i)` or a filter, not directly with `hasText`.
- `getByTitle` keeps every card that contains a matching title element anywhere
  inside it, so a card nested in another card's content makes the inner title
  match both the inner and the outer card (strict-mode error). Look such cards
  up by id or test id.
- `assertTitle` compares the title's whole text (whitespace-normalized). A title
  built from several components reads as their concatenated text.

## See also

- [Badge](BadgeElement.md), [Button](ButtonElement.md)
- [Finding components](../guide/finding-components.md),
  [Assertions](../guide/assertions.md),
  [Actions and waiting](../guide/actions-and-waiting.md),
  [Core concepts](../guide/core-concepts.md)
- Full example: [CardViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/CardViewIT.java)
