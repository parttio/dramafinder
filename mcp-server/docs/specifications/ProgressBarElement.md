# ProgressBarElement Specification

## Overview

`ProgressBarElement` is a Playwright element wrapper for the `<vaadin-progress-bar>` web component. It supports value/min/max setters and assertions and indeterminate state.

## Tag Name

```
vaadin-progress-bar
```

## Class Hierarchy

```
VaadinElement
    └── ProgressBarElement
```

## Implemented Interfaces

| Interface | Description |
|-----------|-------------|
| `HasThemeElement` | Theme variants support |
| `HasStyleElement` | Style attribute support |

## Constants

| Constant | Value | Description |
|----------|-------|-------------|
| `FIELD_TAG_NAME` | `"vaadin-progress-bar"` | HTML tag name |
| `INDETERMINATE_ATTRIBUTE` | `"indeterminate"` | Indeterminate state attribute |

## API Methods

### Constructor

```java
ProgressBarElement(Locator locator)
```

Creates a new `ProgressBarElement` from a Playwright locator.

### Value Methods

| Method | Description |
|--------|-------------|
| `getValue()` | Get current value from `aria-valuenow` |
| `setValue(double value)` | Set the progress value |
| `assertValue(Double expected)` | Assert value matches |

### Min Property

| Method | Description |
|--------|-------------|
| `getMin()` | Get minimum from `aria-valuemin` |
| `setMin(double min)` | Set the minimum value |
| `assertMin(double min)` | Assert minimum matches |

### Max Property

| Method | Description |
|--------|-------------|
| `getMax()` | Get maximum from `aria-valuemax` |
| `setMax(double max)` | Set the maximum value |
| `assertMax(double max)` | Assert maximum matches |

### Indeterminate State

| Method | Description |
|--------|-------------|
| `isIndeterminate()` | Whether bar is indeterminate |
| `setIndeterminate(boolean)` | Set indeterminate state |
| `assertIndeterminate()` | Assert indeterminate state |
| `assertNotIndeterminate()` | Assert not indeterminate |

## Usage Examples

### Basic Usage

```java
ProgressBarElement progress = new ProgressBarElement(
    page.locator("vaadin-progress-bar").first()
);

// Set value
progress.setValue(0.5);

// Get current value
double value = progress.getValue(); // 0.5

// Assert value
progress.assertValue(0.5);
```

### Min/Max Range

```java
ProgressBarElement progress = new ProgressBarElement(locator);

// Set range
progress.setMin(0.0);
progress.setMax(100.0);
progress.setValue(75.0);

// Assert
progress.assertMin(0.0);
progress.assertMax(100.0);
progress.assertValue(75.0);
```

### Indeterminate State

```java
ProgressBarElement loading = new ProgressBarElement(
    page.locator(".loading-indicator vaadin-progress-bar")
);

// Show loading
loading.setIndeterminate(true);
loading.assertIndeterminate();

// Complete loading
loading.setIndeterminate(false);
loading.setValue(1.0);
loading.assertNotIndeterminate();
```

### Theme Variants

```java
ProgressBarElement progress = new ProgressBarElement(locator);
progress.assertHasTheme("success");
progress.assertHasTheme("error");
progress.assertHasTheme("contrast");
```

## Related Elements

- `UploadElement` - File upload with progress
