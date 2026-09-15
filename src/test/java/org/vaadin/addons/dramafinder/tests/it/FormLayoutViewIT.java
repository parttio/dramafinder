package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.HasTestView;
import org.vaadin.addons.dramafinder.element.FormLayoutElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class FormLayoutViewIT extends SpringPlaywrightIT implements HasTestView {

    @Override
    public String getView() {
        return "formlayout";
    }

    @Test
    public void testGet() {
        FormLayoutElement layout = FormLayoutElement.get(page);
        layout.assertVisible();
        assertThat(layout.getLocator()).hasAttribute("id", "default-form");
    }

    @Test
    public void testFields() {
        FormLayoutElement layout = FormLayoutElement.getById(page, "default-form");
        layout.assertFieldCount(3);
        assertEquals(3, layout.getFieldCount());
        assertThat(layout.getFields()).hasCount(3);
        assertThat(layout.getField(0)).hasText("First name");
        assertThat(layout.getField(2)).hasText("Email");
        assertThat(layout.getFormItems()).hasCount(0);
    }

    @Test
    public void testFormItemsAreFields() {
        FormLayoutElement layout = FormLayoutElement.getById(page, "form-item-form");
        layout.assertFieldCount(3);
        assertThat(layout.getFormItems()).hasCount(3);
        assertThat(layout.getField(0)).hasText("First name");
    }

    @Test
    public void testLineBreakIsNotAField() {
        FormLayoutElement layout = FormLayoutElement.getById(page, "line-break-form");
        layout.assertFieldCount(2);
        assertThat(layout.getField(0)).hasText("Email");
        assertThat(layout.getField(1)).hasText("Confirm email");
    }

    @Test
    public void testFormRowsAreFlattened() {
        FormLayoutElement layout = FormLayoutElement.getById(page, "form-row-form");
        layout.assertFieldCount(4);
        assertThat(layout.getFormItems()).hasCount(4);
        assertThat(layout.getField(0)).hasText("Street");
        assertThat(layout.getField(3)).hasText("Country");
    }

    @Test
    public void testDefaultResponsiveSteps() {
        FormLayoutElement wide = FormLayoutElement.getById(page, "default-form");
        wide.assertColumnCount(2);
        wide.assertLabelPosition(FormLayoutElement.LABEL_POSITION_ASIDE);
        assertEquals(2, wide.getColumnCount());

        FormLayoutElement narrow = FormLayoutElement.getById(page, "narrow-form");
        narrow.assertColumnCount(1);
        narrow.assertLabelPosition(FormLayoutElement.LABEL_POSITION_TOP);
        // The component reflects the same decision onto its form items.
        assertThat(narrow.getFormItems().first()).hasAttribute("label-position", "top");
    }

    @Test
    public void testResponsiveStepsFollowTheViewport() {
        FormLayoutElement layout = FormLayoutElement.getById(page, "responsive-form");

        // The layout is 40vw wide, so the viewport decides which step applies.
        page.setViewportSize(600, 900);
        layout.assertColumnCount(1);
        layout.assertLabelPosition(FormLayoutElement.LABEL_POSITION_TOP);

        page.setViewportSize(1200, 900);
        layout.assertColumnCount(2);
        layout.assertLabelPosition(FormLayoutElement.LABEL_POSITION_ASIDE);

        page.setViewportSize(2000, 900);
        layout.assertColumnCount(3);
        layout.assertLabelPosition(FormLayoutElement.LABEL_POSITION_ASIDE);
    }

    @Test
    public void testAutoResponsiveColumnCount() {
        FormLayoutElement layout = FormLayoutElement.getById(page, "auto-responsive-form");
        layout.assertFieldCount(4);
        // Four fields, but the layout is capped at three columns.
        layout.assertColumnCount(3);
        layout.assertLabelPosition(FormLayoutElement.LABEL_POSITION_TOP);
    }

    @Test
    public void testAutoResponsiveLabelsAside() {
        FormLayoutElement layout = FormLayoutElement.getById(page, "form-row-form");
        layout.assertColumnCount(2);
        layout.assertLabelPosition(FormLayoutElement.LABEL_POSITION_ASIDE);
    }

    @Test
    public void testThemeAndStyle() {
        FormLayoutElement layout = FormLayoutElement.getById(page, "default-form");
        layout.assertTheme("small");
        layout.assertHasThemeVariant("small");
        layout.assertHasNoThemeVariant("large");
        layout.assertCssClass("demo-form");
    }
}
