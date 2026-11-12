package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.UploadHandler;

@PageTitle("Upload Demo")
@Route(value = "upload", layout = MainLayout.class)
public class UploadView extends Main {

    public UploadView() {
        createSingleUpload();
        createMultiUpload();
        createClearingUpload();
        createFailingUpload();
    }

    private void createSingleUpload() {
        Span status = new Span("Waiting for file");
        status.setId("single-upload-status");
        Upload upload = new Upload(
                UploadHandler.inMemory((metadata, data) -> {
                            // No-op handler, demo asserts component behavior only.
                        })
                        .whenComplete((result, error) -> status.setText(result.fileName())));
        upload.setMaxFiles(1);
        upload.setDropLabel(new Span("Drop a single file"));
        upload.setUploadButton(new Button("Select single file"));
        upload.addFileRejectedListener(event -> status.setText("Rejected: " + event.getErrorMessage()));

        addExample("Single upload", new Div(upload, status));
    }

    private void createMultiUpload() {
        Span status = new Span("No uploads yet");
        status.setId("multi-upload-status");
        Upload upload = new Upload(
                UploadHandler.inMemory((metadata, data) -> {
                            // No-op handler, demo asserts component behavior only.
                        })
                        .whenComplete((result, error) -> status.setText("Uploaded " + result.fileName())));
        upload.setMaxFiles(3);
        upload.setMaxFileSize(1000000);
        upload.setDropLabel(new Span("Drop multiple files"));
        upload.setUploadButton(new Button("Select multiple files"));
        upload.setAcceptedFileTypes("text/plain");

        upload.setI18n(new UploadI18N().setError(new UploadI18N.Error().setFileIsTooBig("File is too big")));
        upload.addFileRejectedListener(event -> status.setText("Rejected: " + event.getErrorMessage()));

        Button clear = new Button("Clear uploads", event -> upload.clearFileList());

        addExample("Multi upload", new Div(upload, clear, status));
    }

    /**
     * The "attach one file" pattern: the handler is slow, so the upload request
     * outlasts the Flow round trip that feeding the input triggers, and the
     * completion callback clears the list, so the file row vanishes as soon as the
     * upload succeeds. Only a wait on the upload request itself sees the status.
     */
    private void createClearingUpload() {
        Span status = new Span("Nothing received");
        status.setId("clearing-upload-status");
        Upload upload = new Upload();
        upload.setUploadHandler(UploadHandler.inMemory((metadata, data) -> {
                    slowDown();
                })
                .whenComplete((context, success) -> {
                    upload.clearFileList();
                    status.setText("Received " + context.fileName());
                }));
        upload.setUploadButton(new Button("Attach file"));

        addExample("Clearing upload", new Div(upload, status));
    }

    /** An upload whose server handler fails every request, after a delay. */
    private void createFailingUpload() {
        Upload upload = new Upload(event -> {
            slowDown();
            throw new IllegalStateException("Refused by the server");
        });
        upload.setUploadButton(new Button("Attach to failing upload"));

        addExample("Failing upload", upload);
    }

    private static void slowDown() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void addExample(String title, Component component) {
        add(new H2(title), component);
    }
}
