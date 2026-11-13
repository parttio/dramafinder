# Upload

`UploadElement` wraps `<vaadin-upload>` (Flow: `Upload`). It feeds files from
disk or from memory into the upload, waits until the server has handled them,
and asserts the file list: which rows are there, which completed, which failed,
and whether the maximum number of files is reached.

| | |
|---|---|
| Web component | `<vaadin-upload>` (file rows: `<vaadin-upload-file>`) |
| Flow component | `com.vaadin.flow.component.upload.Upload` |
| Wrapper | `org.vaadin.addons.dramafinder.element.UploadElement` |
| Typed query | `find(page, UploadElement.class)` → `ComponentQuery` |
| API | [UploadElement](../../skills/vaadin-playwright-test/api-reference.md#uploadelement) |

## Finding the component

An upload has no label; it is found by the text of its upload button, or by id.

```java
// View
Upload upload = new Upload(UploadHandler.inMemory((metadata, data) -> { /* store */ }));
upload.setUploadButton(new Button("Select single file"));
upload.setId("avatar-upload");
upload.setTestId("avatar");
```

```java
// Test
UploadElement upload = UploadElement.getByButtonText(page, "Select single file");
UploadElement byId = UploadElement.getById(page, "avatar-upload");
UploadElement byTestId = UploadElement.getByTestId(page, "avatar");
UploadElement scoped = UploadElement.getById(page.locator("#profile"), "avatar-upload");

// Typed query: only the common filters (id, test id, attribute, class, accessible name)
UploadElement viaQuery = find(page, UploadElement.class).withId("avatar-upload").single();
```

`getByButtonText` matches the upload button's **whole** accessible name (ARIA
role `button`), case-sensitively; two uploads with the same button text fail
with a strict-mode error. Without a custom button, the text is the web
component's default (`Upload Files...`, or `Upload File...` when `maxFiles` is
1), so prefer `getById` / `getByTestId` when a view has several default
uploads. `getByButtonText` has no `Locator` overload; scope with `getById(Locator, ...)`
or `find(locator, UploadElement.class)`.

## Uploading files

`uploadFiles(Path...)` feeds the files through the component's hidden native
`<input type="file">` (Playwright `setInputFiles`), not by clicking the upload
button, so no file chooser opens.

It returns once every file has **settled** — its upload request answered, or
the file rejected by a client-side constraint (type, size, number of files) —
and the server round-trip that follows is done. The app's completion listener
has therefore run, even if it clears the file list.

```java
// View
Span status = new Span("Waiting for file");
status.setId("single-upload-status");
Upload upload = new Upload(UploadHandler.inMemory((metadata, data) -> { })
        .whenComplete((result, error) -> status.setText(result.fileName())));
upload.setMaxFiles(1);
upload.setUploadButton(new Button("Select single file"));
```

```java
// Test (JUnit's @TempDir Path tempDir)
Path file = Files.writeString(tempDir.resolve("single.txt"), "single");

UploadElement upload = UploadElement.getByButtonText(page, "Select single file");
upload.uploadFiles(file);

upload.assertHasFile("single.txt");
upload.assertFileComplete("single.txt");
assertThat(page.locator("#single-upload-status")).hasText("single.txt");
```

### Files built in memory

`uploadFiles(FilePayload...)` takes Playwright's
`com.microsoft.playwright.options.FilePayload` (name, MIME type, bytes), so the
test does not need to write files to disk. It waits the same way.

```java
FilePayload payload = new FilePayload("in-memory.txt", "text/plain",
        "built in the test".getBytes(StandardCharsets.UTF_8));
UploadElement.getByButtonText(page, "Attach file").uploadFiles(payload);
```

### Several files

Pass several files in one call; the method waits for all of them.

```java
// View
upload.setMaxFiles(3);
upload.setMaxFileSize(1000000);
upload.setAcceptedFileTypes("text/plain");
upload.setUploadButton(new Button("Select multiple files"));
```

```java
// Test
UploadElement upload = UploadElement.getByButtonText(page, "Select multiple files");
upload.uploadFiles(first, second);
upload.assertFileComplete("first.txt");
upload.assertFileComplete("second.txt");

upload.uploadFiles(third);
upload.assertFileComplete("third.txt");
upload.assertMaxFilesReached();     // the host carries max-files-reached
```

### A listener that clears the list

Because `uploadFiles` waits for the upload request **and** the round-trip
after it, a test sees the result of a "receive the file, then clear the list"
pattern even with a slow handler:

```java
// View
upload.setUploadHandler(UploadHandler.inMemory((metadata, data) -> slowDown())
        .whenComplete((context, success) -> {
            upload.clearFileList();
            status.setText("Received " + context.fileName());
        }));
upload.setUploadButton(new Button("Attach file"));
```

```java
// Test
UploadElement upload = UploadElement.getByButtonText(page, "Attach file");
upload.uploadFiles(file);
assertThat(page.locator("#clearing-upload-status")).hasText("Received attached.txt");
upload.assertNoFile("attached.txt");   // the listener removed the row
```

## The file list

Each file is a `<vaadin-upload-file>` row, addressed by its **exact** file name.

```java
upload.assertHasFile("report.pdf");             // the row is visible
upload.assertFileComplete("report.pdf");        // the row has the `complete` attribute
upload.removeFile("report.pdf");                // clicks the row's remove button, waits for the server
upload.assertNoFile("report.pdf");              // no such row (or hidden)
```

For anything else on a row, use the locators with Playwright's `assertThat`:

```java
Locator row = upload.getFileItemLocator("report.pdf");    // the <vaadin-upload-file>
Locator status = upload.getFileStatusLocator("report.pdf"); // its [part="status"] cell
assertThat(row).hasAttribute("complete", "");
assertThat(status).isVisible();
```

`getFileItemLocator("report.pdf")` does not resolve to `report.pdf.bak`; two
rows for the same file name fail with a Playwright strict-mode error. A row's
state is reflected as attributes: `held` (queued), `uploading`,
`indeterminate`, `complete` and `error`.

## Rejected files and upload errors

A file the client rejects (wrong type, too big, too many files) never gets a
row; `uploadFiles` still returns normally, and the app's
`FileRejectedListener` has run.

```java
// View
upload.setAcceptedFileTypes("text/plain");
upload.addFileRejectedListener(event -> status.setText("Rejected: " + event.getErrorMessage()));
```

```java
// Test
Path forbidden = Files.writeString(tempDir.resolve("first.forbidden"), "first");
upload.uploadFiles(forbidden);
upload.assertNoFile("first.forbidden");
assertThat(page.locator("#multi-upload-status")).hasText("Rejected: Incorrect File Type.");
```

A request the server answers with an error also settles the file: the method
returns normally and the row shows the error.

```java
// View
Upload upload = new Upload(event -> {
    throw new IllegalStateException("Refused by the server");
});
upload.setUploadButton(new Button("Attach to failing upload"));
```

```java
// Test
UploadElement upload = UploadElement.getByButtonText(page, "Attach to failing upload");
upload.uploadFiles(new FilePayload("fail.txt", "text/plain", "refused".getBytes(StandardCharsets.UTF_8)));

Locator row = upload.getFileItemLocator("fail.txt");
assertThat(row).hasAttribute("error", Pattern.compile(".*"));  // present, whatever its value
assertThat(row).not().hasAttribute("complete", "");
```

## Observing progress

By the time `uploadFiles` returns the upload is over, so the in-flight state
(`uploading`, the progress in the status cell) is gone. To observe it, run the
upload inside `AutoWait.withoutWaiting(...)` — the method then returns as soon
as the files are fed — and use a handler slow enough for the state to be seen:

```java
AutoWait.withoutWaiting(() -> upload.uploadFiles(bigFile));

assertThat(upload.getFileItemLocator("big.bin")).hasAttribute("uploading", "");
upload.assertFileComplete("big.bin");   // retries until the upload finishes
```

The wait is also skipped when the upload has `noAuto` set (Flow:
`upload.setAutoUpload(false)`): no request is sent until the app or the user
starts it, so `uploadFiles` only adds the rows.

## Enabled state, focus and theme

Enablement and focus are delegated to the upload button.

```java
// View
upload.setEnabled(false);
```

```java
// Test
UploadElement upload = UploadElement.getById(page, "avatar-upload");
upload.assertDisabled();                 // the upload button is disabled

assertThat(upload.getUploadButtonLocator()).hasText("Select single file");
upload.focus();                          // focuses the upload button
upload.assertIsFocused();

upload.assertTheme(null);                // no theme attribute
```

## Pitfalls

- `uploadFiles` never opens the file chooser and never clicks the button: it
  writes to the hidden `<input type="file">`. To test the button itself, use
  `getUploadButtonLocator()`.
- File names are matched exactly; two rows with the same name are a strict-mode
  error.
- A rejected file has no row. Assert the rejection through the app's reaction
  (a status text, a notification) and `assertNoFile`, not through
  `assertFileComplete`.
- `uploadFiles` returns normally when the server fails the upload; check the
  row's `error` attribute if the failure matters.
- With `AutoWait.withoutWaiting(...)` or `-Ddramafinder.autoWait=false`,
  `uploadFiles` does not wait for the upload: follow it with a retrying
  assertion such as `assertFileComplete`.

## See also

- [Button](ButtonElement.md), [Progress Bar](ProgressBarElement.md),
  [Notification](NotificationElement.md)
- [Actions and waiting](../guide/actions-and-waiting.md),
  [Finding components](../guide/finding-components.md),
  [Assertions](../guide/assertions.md),
  [Core concepts](../guide/core-concepts.md)
- Full example: [UploadViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/UploadViewIT.java)
