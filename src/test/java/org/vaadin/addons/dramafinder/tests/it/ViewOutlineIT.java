package org.vaadin.addons.dramafinder.tests.it;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.agent.ViewOutline;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.addons.dramafinder.element.ConfirmDialogElement;
import org.vaadin.addons.dramafinder.element.DialogElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies {@link ViewOutline} against the demo views: each listed component
 * comes with a lookup that resolves to it on the live page.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ViewOutlineIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "grid-basic";
    }

    private String outlineOf(String view) {
        page.navigate(getUrl() + view);
        page.waitForFunction(WAIT_FOR_VAADIN_SCRIPT);
        return ViewOutline.capture(page);
    }

    private static void assertContains(String outline, String expected) {
        assertTrue(outline.contains(expected), "Expected <" + expected + "> in:\n" + outline);
    }

    @Test
    public void suggestsIdLookupForGridsAndReportsTheirState() {
        String outline = ViewOutline.capture(page);
        assertContains(outline, "vaadin-grid #basic-grid — 100 rows, columns [First Name, Last Name, Email]");
        assertContains(outline, "    GridElement.getById(page, \"basic-grid\")");
    }

    @Test
    public void suggestsTreeGridElementForTreeGrids() {
        String outline = outlineOf("tree-grid");
        assertContains(outline, "TreeGridElement.getById(page, \"two-level-tree-grid\")");
    }

    @Test
    public void suggestsLabelLookupIncludingPlaceholderFallback() {
        String outline = outlineOf("textfield-getbylabel");
        assertContains(outline, "vaadin-text-field \"My Label\"\n    TextFieldElement.getByLabel(page, \"My Label\")"
                .replace("\n", System.lineSeparator()));
        assertContains(outline, "TextFieldElement.getByLabel(page, \"My Placeholder\")");
        assertContains(outline, "TextFieldElement.getByLabel(page, \"My AriaLabel\")");
    }

    @Test
    public void listsContainerItemsByFullLabel() {
        String outline = outlineOf("ambiguous-label");
        assertContains(outline, "TabsElement.getById(page, \"ambiguous-tabs\")");
        assertContains(outline, "items (full label, case-sensitive): \"Tab 10\", \"Tab 1\"");
        assertContains(outline, "CheckboxGroupElement.getByLabel(page, \"Ambiguous Checkbox Group\")");
    }

    @Test
    public void flagsLookupThatMatchesSeveralComponents() {
        String outline = outlineOf("dialog");
        // "Open dialog" is a substring of "Open dialog with header component".
        assertContains(outline, "ButtonElement.getByText(page, \"Open dialog\") matches 2 components");
        assertContains(outline, "ButtonElement.getByText(page, \"Open non-modal 1\")");
    }

    @Test
    public void positionalFallbackPointsAtTheListedComponent() {
        String outline = outlineOf("dialog");
        String line = outline.lines()
                .filter(l -> l.contains("matches 2 components"))
                .findFirst().orElseThrow();
        int nth = Integer.parseInt(line.replaceAll(".*\\.nth\\((\\d+)\\).*", "$1"));
        assertThat(page.locator("vaadin-button").nth(nth)).hasText("Open dialog");
    }

    @Test
    public void listsOpenDialogWithHeaderLookup() {
        page.navigate(getUrl() + "dialog");
        page.waitForFunction(WAIT_FOR_VAADIN_SCRIPT);
        ButtonElement.getByText(page, "Open non-modal 1").click();
        DialogElement.getByHeaderText(page, "My non-modal Dialog 1").assertOpen();
        String outline = ViewOutline.capture(page);
        assertContains(outline, "DialogElement.getByHeaderText(page, \"My non-modal Dialog 1\")");
    }

    @Test
    public void skipsComponentsInsideComposites() {
        String outline = outlineOf("upload");
        assertContains(outline, "UploadElement.getByButtonText(page, \"Select single file\")");
        assertFalse(outline.contains("vaadin-button \"Select single file\""), outline);
    }

    @Test
    public void removesItsMarkersFromThePage() {
        ViewOutline.capture(page);
        assertEquals(0, page.locator("[data-dramafinder-outline]").count());
    }

    @Test
    public void writesOutlineFile(@TempDir Path tempDir) throws Exception {
        Path file = ViewOutline.write(page, tempDir);
        assertEquals(tempDir.resolve("view-outline.txt"), file);
        assertTrue(Files.readString(file).contains("GridElement.getById(page, \"basic-grid\")"));
    }

    @Test
    public void listsOpenConfirmDialogWithoutItsButtons() {
        page.navigate(getUrl() + "confirm-dialog");
        page.waitForFunction(WAIT_FOR_VAADIN_SCRIPT);
        ButtonElement.getByText(page, "Open alert").click();
        ConfirmDialogElement.getByHeaderText(page, "Export failed").assertOpen();
        String outline = ViewOutline.capture(page);
        assertContains(outline, "ConfirmDialogElement.getByHeaderText(page, \"Export failed\")");
        assertFalse(outline.contains("vaadin-button \"OK\""), outline);
    }
}
