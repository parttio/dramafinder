# UploadElement

> Full API (methods, signatures, descriptions): see [api-reference.md](../../skills/vaadin-playwright-test/api-reference.md). This note covers only behaviour that isn't obvious from the signatures.

## Files are fed through the hidden native file input

`uploadFiles(Path...)` feeds files via the component's hidden native `<input type="file">` (Playwright `setInputFiles`), not by clicking the upload button. Upload completion is asserted from the file row's state, so wait for it after adding a file rather than assuming the upload is instant.

```java
Path testFile = Path.of("/path/to/test.pdf");
upload.uploadFiles(testFile);

upload.assertHasFile("test.pdf");
// Wait for the row to report completion
upload.assertFileComplete("test.pdf");
```
