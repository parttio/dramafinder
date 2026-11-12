package org.vaadin.addons.dramafinder.tests.it;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import com.microsoft.playwright.options.FilePayload;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.UploadElement;
import org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UploadViewIT extends SpringPlaywrightIT {

    @TempDir
    Path tempDir;

    @Override
    public String getView() {
        return "upload";
    }

    @Test
    public void testSingleFileUploadCompletes() throws IOException {
        Path file = Files.writeString(tempDir.resolve("single.txt"), "single");

        UploadElement upload = UploadElement.getByButtonText(page, "Select single file");
        upload.uploadFiles(file);

        upload.assertHasFile("single.txt");
        upload.assertFileComplete("single.txt");
        assertThat(page.locator("#single-upload-status")).hasText("single.txt");
    }

    @Test
    public void testMultiFileUploadAndClear() throws IOException {
        Path first = Files.writeString(tempDir.resolve("first.txt"), "first");
        Path second = Files.writeString(tempDir.resolve("second.txt"), "second");
        Path third = Files.writeString(tempDir.resolve("third.txt"), "third");

        UploadElement upload = UploadElement.getByButtonText(page, "Select multiple files");
        upload.uploadFiles(first, second);

        upload.assertHasFile("first.txt");
        upload.assertHasFile("second.txt");
        upload.assertFileComplete("first.txt");
        upload.assertFileComplete("second.txt");

        upload.uploadFiles(third);
        upload.assertHasFile("third.txt");
        upload.assertFileComplete("third.txt");
        upload.assertMaxFilesReached();

        upload.removeFile("first.txt");
        upload.assertNoFile("first.txt");

        upload.removeFile("second.txt");
        upload.assertNoFile("second.txt");

        upload.removeFile("third.txt");
        upload.assertNoFile("third.txt");

        assertThat(page.locator("#multi-upload-status")).hasText("Uploaded third.txt");
    }

    @Test
    public void textFileRejected() throws IOException {
        Path first = Files.writeString(tempDir.resolve("first.forbidden"), "first");

        UploadElement upload = UploadElement.getByButtonText(page, "Select multiple files");
        upload.uploadFiles(first);

        upload.assertNoFile("first.forbidden");

        assertThat(page.locator("#multi-upload-status")).hasText("Rejected: Incorrect File Type.");
    }

    // The clearing upload's handler is slow and its completion callback clears
    // the file list. The status is read once, without retrying: it only holds the
    // file name if uploadFiles waited for the upload request and the round trip
    // after it.

    @Test
    public void uploadFilesReturnsAfterTheServerHandledTheFile() throws IOException {
        Path file = Files.writeString(tempDir.resolve("attached.txt"), "attached");

        UploadElement upload = UploadElement.getByButtonText(page, "Attach file");
        upload.uploadFiles(file);

        assertEquals("Received attached.txt", page.locator("#clearing-upload-status").textContent());
        upload.assertNoFile("attached.txt");
    }

    @Test
    public void uploadFilesWithInMemoryContentReturnsAfterTheServerHandledIt() {
        FilePayload payload = new FilePayload("in-memory.txt", "text/plain",
                "built in the test".getBytes(StandardCharsets.UTF_8));

        UploadElement upload = UploadElement.getByButtonText(page, "Attach file");
        upload.uploadFiles(payload);

        assertEquals("Received in-memory.txt", page.locator("#clearing-upload-status").textContent());
        upload.assertNoFile("in-memory.txt");
    }

    @Test
    public void uploadFilesReturnsWhenTheServerFailsTheUpload() {
        FilePayload payload = new FilePayload("fail.txt", "text/plain",
                "refused".getBytes(StandardCharsets.UTF_8));

        UploadElement upload = UploadElement.getByButtonText(page, "Attach to failing upload");
        upload.uploadFiles(payload);

        assertNotNull(upload.getFileItemLocator("fail.txt").getAttribute("error"),
                "the file row should already show the upload error");
    }
}
