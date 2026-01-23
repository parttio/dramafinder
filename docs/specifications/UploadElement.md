# UploadElement Specification

## Overview

`UploadElement` is a Playwright element wrapper for the `<vaadin-upload>` web component. It provides helpers to feed files via the native file input, inspect the file list entries, and assert upload completion using the file row state.

## Tag Name

```
vaadin-upload
```

## Class Hierarchy

```
VaadinElement
    └── UploadElement
```

## Implemented Interfaces

| Interface | Description |
|-----------|-------------|
| `HasEnabledElement` | Enabled/disabled state |
| `HasValidationPropertiesElement` | Validation properties |
| `HasThemeElement` | Theme variants support |
| `FocusableElement` | Focus operations |

## Constants

| Constant | Value | Description |
|----------|-------|-------------|
| `FIELD_TAG_NAME` | `"vaadin-upload"` | HTML tag name |
| `FILE_ITEM_TAG_NAME` | `"vaadin-upload-file"` | File row tag name |

## API Methods

### Constructor

```java
UploadElement(Locator locator)
```

Creates a new `UploadElement` from a Playwright locator.

### Static Factory Methods

#### getByButtonText(Page page, String buttonText)

Get upload component by the text of its upload button.

```java
UploadElement upload = UploadElement.getByButtonText(page, "Upload Files");
```

### File Operations

| Method | Description |
|--------|-------------|
| `uploadFiles(Path... files)` | Upload files via hidden input |
| `removeFile(String fileName)` | Remove file using remove button |

### Locator Methods

| Method | Description |
|--------|-------------|
| `getFileInputLocator()` | Locator for native file input |
| `getUploadButtonLocator()` | Locator for upload button |
| `getFileItemLocator(String fileName)` | Locator for file row |
| `getFileStatusLocator(String fileName)` | Locator for file status |
| `getFocusLocator()` | Returns upload button locator |
| `getEnabledLocator()` | Returns upload button locator |

### Assertions

| Method | Description |
|--------|-------------|
| `assertHasFile(String fileName)` | Assert file is in list |
| `assertNoFile(String fileName)` | Assert file is not in list |
| `assertFileComplete(String fileName)` | Assert file upload complete |
| `assertMaxFilesReached()` | Assert max files limit reached |

## Usage Examples

### Basic Upload

```java
UploadElement upload = UploadElement.getByButtonText(page, "Upload");

// Upload a file
Path testFile = Path.of("/path/to/test.pdf");
upload.uploadFiles(testFile);

// Assert file appears
upload.assertHasFile("test.pdf");

// Wait for completion
upload.assertFileComplete("test.pdf");
```

### Multiple Files

```java
UploadElement upload = UploadElement.getByButtonText(page, "Upload Documents");

// Upload multiple files
upload.uploadFiles(
    Path.of("/path/to/file1.pdf"),
    Path.of("/path/to/file2.pdf"),
    Path.of("/path/to/file3.pdf")
);

// Assert all files
upload.assertHasFile("file1.pdf");
upload.assertHasFile("file2.pdf");
upload.assertHasFile("file3.pdf");
```

### Remove File

```java
UploadElement upload = UploadElement.getByButtonText(page, "Attachments");

// Upload file
upload.uploadFiles(Path.of("/path/to/document.pdf"));
upload.assertHasFile("document.pdf");

// Remove file
upload.removeFile("document.pdf");
upload.assertNoFile("document.pdf");
```

### Max Files

```java
UploadElement upload = UploadElement.getByButtonText(page, "Images");

// Upload to max limit
upload.uploadFiles(Path.of("/path/to/image1.png"));
upload.uploadFiles(Path.of("/path/to/image2.png"));

// Assert max reached
upload.assertMaxFilesReached();
```

### File Status

```java
UploadElement upload = UploadElement.getByButtonText(page, "Upload");
upload.uploadFiles(Path.of("/path/to/file.pdf"));

// Access status locator
Locator status = upload.getFileStatusLocator("file.pdf");
assertThat(status).containsText("100%");
```

## Related Elements

- `ProgressBarElement` - For general progress display
