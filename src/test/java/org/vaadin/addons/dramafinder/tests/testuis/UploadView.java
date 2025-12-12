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

    private void addExample(String title, Component component) {
        add(new H2(title), component);
    }
}
