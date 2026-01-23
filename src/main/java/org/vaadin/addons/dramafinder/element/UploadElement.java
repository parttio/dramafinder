package org.vaadin.addons.dramafinder.element;

import java.nio.file.Path;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.shared.FocusableElement;
import org.vaadin.addons.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;
import org.vaadin.addons.dramafinder.element.shared.HasValidationPropertiesElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code vaadin-upload}.
 * <p>
 * Provides helpers to feed files via the native file input, inspect the file
 * list entries, and assert upload completion using the file row state.
 * Factory lookup relies on the upload button {@link AriaRole#BUTTON button}
 * accessible name.
 */
@PlaywrightElement(UploadElement.FIELD_TAG_NAME)
public class UploadElement extends VaadinElement
        implements HasEnabledElement,
        HasValidationPropertiesElement, HasThemeElement, FocusableElement {

    /** The HTML tag name for this Vaadin component. */
    public static final String FIELD_TAG_NAME = "vaadin-upload";
    /** The HTML tag name for file row items. */
    public static final String FILE_ITEM_TAG_NAME = "vaadin-upload-file";

    /**
     * Create a new {@code UploadElement}.
     *
     * @param locator the locator for the {@code vaadin-upload} element
     */
    public UploadElement(Locator locator) {
        super(locator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Locator getFocusLocator() {
        return getUploadButtonLocator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Locator getEnabledLocator() {
        return getUploadButtonLocator();
    }

    /**
     * Locator for the native {@code input[type=file]} element.
     *
     * @return the file input locator
     */
    public Locator getFileInputLocator() {
        return getLocator().locator("input[type=\"file\"]").first();
    }

    /**
     * Locator for the primary upload button.
     *
     * @return the upload button locator
     */
    public Locator getUploadButtonLocator() {
        return getLocator().locator("vaadin-button[slot=\"add-button\"]").first();
    }

    /**
     * Locator for a specific file row.
     *
     * @param fileName the file name to search
     * @return the matching file row locator
     */
    public Locator getFileItemLocator(String fileName) {
        return getLocator().locator(FILE_ITEM_TAG_NAME)
                .filter(new Locator.FilterOptions().setHasText(fileName))
                .first();
    }

    /**
     * Locator for the status cell of a given file row.
     *
     * @param fileName the file name to search
     * @return the matching status locator
     */
    public Locator getFileStatusLocator(String fileName) {
        return getFileItemLocator(fileName).locator("[part=\"status\"]").first();
    }

    /**
     * Upload one or more files by feeding the hidden input.
     *
     * @param files file paths to upload
     */
    public void uploadFiles(Path... files) {
        getFileInputLocator().setInputFiles(files);
    }

    /**
     * Remove a file from the list using the remove button.
     *
     * @param fileName the file name to remove
     */
    public void removeFile(String fileName) {
        getFileItemLocator(fileName).locator("[part=\"remove-button\"]").first().click();
    }

    /**
     * Assert that a file is listed in the upload file list.
     *
     * @param fileName the expected file name
     */
    public void assertHasFile(String fileName) {
        assertThat(getFileItemLocator(fileName)).isVisible();
    }

    /**
     * Assert that a file is not present in the upload file list.
     *
     * @param fileName the file name that should be absent
     */
    public void assertNoFile(String fileName) {
        assertThat(getFileItemLocator(fileName)).isHidden();
    }

    /**
     * Assert that a file row is marked complete.
     *
     * @param fileName the file name to check
     */
    public void assertFileComplete(String fileName) {
        assertThat(getFileItemLocator(fileName)).hasAttribute("complete", "");
    }

    /**
     * Assert that the maximum number of files has been reached.
     * The upload button will typically be disabled in this state.
     */
    public void assertMaxFilesReached() {
        assertThat(getLocator()).hasAttribute("max-files-reached", "");
    }

    /**
     * Get the {@code UploadElement} by the accessible text of its upload button.
     *
     * @param page       the Playwright page
     * @param buttonText the accessible text of the upload button (ARIA role {@code button})
     * @return the matching {@code UploadElement}
     */
    public static UploadElement getByButtonText(Page page, String buttonText) {
        return new UploadElement(
                page.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions()
                                .setHas(page.getByRole(AriaRole.BUTTON,
                                        new Page.GetByRoleOptions().setName(buttonText))))
                        .first());
    }
}
