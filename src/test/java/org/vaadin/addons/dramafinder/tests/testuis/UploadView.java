package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Upload Demo")
@Route(value = "upload", layout = MainLayout.class)
public class UploadView extends Main {

    public UploadView() {
        createSingleUpload();
        createMultiUpload();
    }

    private void createSingleUpload() {
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setMaxFiles(1);
        upload.setDropLabel(new Span("Drop a single file"));
        upload.setUploadButton(new Button("Select single file"));

        Span status = new Span("Waiting for file");
        status.setId("single-upload-status");
        upload.addSucceededListener(event -> status.setText(event.getFileName()));
        upload.addFileRejectedListener(event -> status.setText("Rejected: " + event.getErrorMessage()));

        addExample("Single upload", new Div(upload, status));
    }

    private void createMultiUpload() {
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setMaxFiles(3);
        upload.setMaxFileSize(1000000);
        upload.setDropLabel(new Span("Drop multiple files"));
        upload.setUploadButton(new Button("Select multiple files"));
        upload.setAcceptedFileTypes("text/plain");

        upload.setI18n(new UploadI18N().setError(new UploadI18N.Error().setFileIsTooBig("File is too big")));
        Span status = new Span("No uploads yet");
        status.setId("multi-upload-status");
        upload.addSucceededListener(event -> status.setText("Uploaded " + event.getFileName()));
        upload.addFileRejectedListener(event -> status.setText("Rejected: " + event.getErrorMessage()));

        Button clear = new Button("Clear uploads", event -> upload.clearFileList());

        addExample("Multi upload", new Div(upload, clear, status));
    }

    private void addExample(String title, Component component) {
        add(new H2(title), component);
    }
}
