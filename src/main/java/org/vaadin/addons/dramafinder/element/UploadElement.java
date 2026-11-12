package org.vaadin.addons.dramafinder.element;

import java.nio.file.Path;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.FilePayload;
import org.vaadin.addons.dramafinder.AutoWait;
import org.vaadin.addons.dramafinder.element.query.ComponentQuery;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.utils.IdLocator;
import org.vaadin.addons.dramafinder.element.utils.ItemLocator;
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
        HasValidationPropertiesElement, HasThemeElement, FocusableElement,
        Findable<ComponentQuery<UploadElement>> {

    public static final String FIELD_TAG_NAME = "vaadin-upload";
    public static final String FILE_ITEM_TAG_NAME = "vaadin-upload-file";

    private static final String SETTLED_PROPERTY = "__dramafinderSettledFiles";
    private static final String TRACK_SETTLED_FILES = """
            el => {
                if (el.%1$s === undefined) {
                    el.%1$s = 0;
                    const settle = () => el.%1$s++;
                    for (const type of ['upload-success', 'upload-error', 'file-reject']) {
                        el.addEventListener(type, settle);
                    }
                }
                return el.%1$s;
            }""".formatted(SETTLED_PROPERTY);

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
     * <p>
     * The name must match the file name exactly, so {@code "report.pdf"} does not
     * resolve to {@code "report.pdf.bak"}. Two rows for the same file name fail
     * with a Playwright strict-mode error.
     *
     * @param fileName the full file name to search
     * @return the matching file row locator
     */
    public Locator getFileItemLocator(String fileName) {
        return ItemLocator.byExactText(getLocator(), FILE_ITEM_TAG_NAME, fileName);
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
     * <p>
     * Returns once every file has settled — its upload request answered, or the
     * file rejected by a client-side constraint (type, size, number of files) —
     * and the server round trip that follows is done, so the app's completion
     * listener has run even if it clears the file list. A request the server
     * answers with an error also settles the file: the method returns normally
     * and the file row shows the error.
     *
     * @param files file paths to upload
     */
    public void uploadFiles(Path... files) {
        int settled = trackSettledFiles();
        getFileInputLocator().setInputFiles(files);
        waitUntilSettled(settled + files.length);
        waitAfterAction();
    }

    /**
     * Upload one or more files built in memory, without writing them to disk
     * first.
     * <p>
     * Waits like {@link #uploadFiles(Path...)}: until every file has settled and
     * the server round trip that follows is done.
     *
     * @param files the files' names, MIME types and contents
     */
    public void uploadFiles(FilePayload... files) {
        int settled = trackSettledFiles();
        getFileInputLocator().setInputFiles(files);
        waitUntilSettled(settled + files.length);
        waitAfterAction();
    }

    /**
     * Start counting settled files on the host, once per element, and return
     * the count so far. The count only grows, so the wait cannot miss a file
     * whose row the app removes as soon as it succeeds.
     */
    private int trackSettledFiles() {
        return ((Number) getLocator().evaluate(TRACK_SETTLED_FILES)).intValue();
    }

    /**
     * Wait for the settled count to reach {@code expected}. Skipped when
     * automatic waiting is off, and with {@code noAuto}, which sends no request
     * until the app starts the upload.
     */
    private void waitUntilSettled(int expected) {
        if (!AutoWait.isEnabled() || Boolean.TRUE.equals(getLocator().evaluate("el => el.noAuto"))) {
            return;
        }
        assertThat(getLocator()).hasJSProperty(SETTLED_PROPERTY, expected);
    }

    /**
     * Remove a file from the list using the remove button.
     *
     * @param fileName the file name to remove
     */
    public void removeFile(String fileName) {
        getFileItemLocator(fileName).locator("[part=\"remove-button\"]").first().click();
        waitAfterAction();
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

    public void assertMaxFilesReached() {
        assertThat(getLocator()).hasAttribute("max-files-reached", "");
    }

    /**
     * Get the {@code UploadElement} by the accessible text of its upload button.
     * <p>
     * The text must match in full, case-sensitively; two uploads with that button
     * text fail with a strict-mode error.
     *
     * @param page       the Playwright page
     * @param buttonText the full accessible text of the upload button (ARIA role {@code button})
     * @return the matching {@code UploadElement}
     */
    public static UploadElement getByButtonText(Page page, String buttonText) {
        return new UploadElement(
                page.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions()
                                .setHas(page.getByRole(AriaRole.BUTTON,
                                        new Page.GetByRoleOptions().setName(buttonText).setExact(true)))));
    }

    // ── Lookup by id or test id ────────────────────────────────────────

    /**
     * Get an {@code UploadElement} by its {@code id}, searching the entire page.
     * <p>
     * A fallback for a component without a user-visible name: prefer the label or
     * text lookups when there is one. The id must match in full; two components
     * with that id fail with a strict-mode error.
     *
     * @param page the Playwright page
     * @param id   the full {@code id}
     * @return the matching {@code UploadElement}
     */
    public static UploadElement getById(Page page, String id) {
        return new UploadElement(IdLocator.byId(page, FIELD_TAG_NAME, id));
    }

    /**
     * Get an {@code UploadElement} by its {@code id}, scoped to the given locator.
     * <p>
     * A fallback for a component without a user-visible name. The id must match in
     * full; two components with that id inside the scope fail with a strict-mode
     * error.
     *
     * @param locator the locator whose subtree is searched
     * @param id      the full {@code id}
     * @return the matching {@code UploadElement}
     */
    public static UploadElement getById(Locator locator, String id) {
        return new UploadElement(IdLocator.byId(locator, FIELD_TAG_NAME, id));
    }

    /**
     * Get an {@code UploadElement} by its test id, searching the entire page.
     * <p>
     * The test id is Playwright's test id attribute, {@code data-testid} unless
     * reconfigured — the attribute Flow's {@code Component.setTestId(String)} sets.
     * A fallback for a component without a user-visible name; it must match in
     * full, and two components with that test id fail with a strict-mode error.
     *
     * @param page   the Playwright page
     * @param testId the full test id
     * @return the matching {@code UploadElement}
     */
    public static UploadElement getByTestId(Page page, String testId) {
        return new UploadElement(IdLocator.byTestId(page, FIELD_TAG_NAME, testId));
    }

    /**
     * Get an {@code UploadElement} by its test id, scoped to the given locator.
     * <p>
     * See {@link #getByTestId(Page, String)}; only components inside the scope are
     * considered.
     *
     * @param locator the locator whose subtree is searched
     * @param testId  the full test id
     * @return the matching {@code UploadElement}
     */
    public static UploadElement getByTestId(Locator locator, String testId) {
        return new UploadElement(IdLocator.byTestId(locator, FIELD_TAG_NAME, testId));
    }
}
