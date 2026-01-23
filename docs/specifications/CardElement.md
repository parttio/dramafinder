# CardElement Specification

## Overview

`CardElement` is a Playwright element wrapper for the `<vaadin-card>` web component. It exposes slot-aware accessors (title, subtitle, header/footer, media) and lookup helpers based on the Card's ARIA region name (title).

## Tag Name

```
vaadin-card
```

## Class Hierarchy

```
VaadinElement
    └── CardElement
```

## Implemented Interfaces

| Interface | Description |
|-----------|-------------|
| `HasThemeElement` | Theme variants support |
| `HasStyleElement` | Style attribute support |

## Constants

| Constant | Value | Description |
|----------|-------|-------------|
| `FIELD_TAG_NAME` | `"vaadin-card"` | HTML tag name |

## API Methods

### Constructor

```java
CardElement(Locator locator)
```

Creates a new `CardElement` from a Playwright locator.

### Static Factory Methods

#### getByTitle(Page page, String title)

Get a card by its title.

```java
CardElement card = CardElement.getByTitle(page, "User Profile");
```

#### getByTitle(Locator locator, String title)

Get a card by title within a scope.

```java
CardElement card = CardElement.getByTitle(containerLocator, "Settings");
```

### Slot Locators

| Method | Description |
|--------|-------------|
| `getTitleLocator()` | Locator for the title slot |
| `getSubtitleLocator()` | Locator for the subtitle slot |
| `getHeaderLocator()` | Locator for the header slot |
| `getHeaderPrefixLocator()` | Locator for the header-prefix slot |
| `getHeaderSuffixLocator()` | Locator for the header-suffix slot |
| `getMediaLocator()` | Locator for the media slot |
| `getFooterLocator()` | Locator for the footer slot |
| `getContentLocator()` | Locator for the default content slot |

### Assertions

| Method | Description |
|--------|-------------|
| `assertTitle(String title)` | Assert card title or absence when `null` |
| `assertSubtitle(String subtitle)` | Assert card subtitle or absence when `null` |

## Usage Examples

### Basic Usage

```java
CardElement card = CardElement.getByTitle(page, "Product Details");

// Assert title and subtitle
card.assertTitle("Product Details");
card.assertSubtitle("Premium Edition");

// Access card sections
Locator header = card.getHeaderLocator();
Locator content = card.getContentLocator();
Locator footer = card.getFooterLocator();

// Access media slot
Locator media = card.getMediaLocator();
```

### Theme Variants

```java
CardElement card = CardElement.getByTitle(page, "Alert");
card.assertHasTheme("error");
```
