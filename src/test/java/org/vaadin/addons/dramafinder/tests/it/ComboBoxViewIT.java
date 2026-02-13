package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.addons.dramafinder.element.ComboBoxElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ComboBoxViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "combo-box";
    }

    @Test
    public void testTitle() {
        assertThat(page).hasTitle("ComboBox Demo");
    }

    @Test
    public void testSelection() {
        ComboBoxElement comboBox = ComboBoxElement.getByLabel(page, "Sort by");
        comboBox.assertVisible();
        comboBox.selectItem("Rating: high to low");
        comboBox.assertValue("Rating: high to low");
    }

    @Test
    public void testHasClass() {
        ComboBoxElement comboBox = ComboBoxElement.getByLabel(page, "Sort by");
        comboBox.assertCssClass("custom-combo-box");
        assertThat(comboBox.getLocator()).hasClass("custom-combo-box");
    }

    @Test
    public void testHasHelperText() {
        ComboBoxElement comboBox = ComboBoxElement.getByLabel(page, "Sort by");
        comboBox.assertVisible();
        assertThat(comboBox.getHelperLocator()).hasText("Helper text");
        comboBox.assertHelperHasText("Helper text");
        assertEquals("Helper text", comboBox.getHelperText());
    }

    @Test
    public void testHasHelperComponent() {
        ComboBoxElement comboBox = ComboBoxElement.getByLabel(page, "ComboBox with helper component");
        comboBox.assertVisible();
        comboBox.assertCssClass("custom-combo-box");
        ComboBoxElement helperComponent = new ComboBoxElement(comboBox.getHelperLocator());
        helperComponent.assertCssClass("custom-helper-component");
        helperComponent.assertVisible();
        assertThat(helperComponent.getHelperLocator()).hasText("Internal helper");
        helperComponent.assertHelperHasText("Internal helper");
    }

    @Test
    public void testPlaceholder() {
        ComboBoxElement comboBox = ComboBoxElement.getByLabel(page, "ComboBox with placeholder and clear button");
        comboBox.assertVisible();
        comboBox.assertPlaceholder("Enter text here");
        assertEquals("Enter text here", comboBox.getPlaceholder());
    }

    @Test
    public void testClearButton() {
        ComboBoxElement comboBox = ComboBoxElement.getByLabel(page, "ComboBox with placeholder and clear button");
        comboBox.selectItem("Most recent first");
        comboBox.assertValue("Most recent first");
        comboBox.clickClearButton();
        comboBox.assertValue("");
    }

    @Test
    public void testTheme() {
        ComboBoxElement comboBox = ComboBoxElement.getByLabel(page, "ComboBox with theme");
        comboBox.assertVisible();
        comboBox.assertTheme("small");
    }

    @Test
    public void testValidation() {
        ComboBoxElement comboBox = ComboBoxElement.getByLabel(page, "ComboBox with theme");
        comboBox.assertValid();
        ButtonElement.getByText(page, "Validate").click();
        comboBox.assertInvalid();
        assertThat(comboBox.getErrorMessageLocator()).hasText("Required field");
    }

    @Test
    public void testPrefix() {
        ComboBoxElement comboBox = ComboBoxElement.getByLabel(page, "Sort by");
        assertThat(comboBox.getPrefixLocator()).isVisible();
        assertThat(comboBox.getPrefixLocator()).hasText("Prefix");
        comboBox.assertPrefixHasText("Prefix");
        assertEquals("Prefix", comboBox.getPrefixText());
    }

    @Test
    public void testNoPrefix() {
        ComboBoxElement comboBox = ComboBoxElement.getByLabel(page, "ComboBox with helper component");
        comboBox.assertPrefixHasText(null);
    }

    @Test
    public void testFocused() {
        ComboBoxElement comboBox = ComboBoxElement.getByLabel(page, "Sort by");
        ComboBoxElement comboBoxWithHelper = ComboBoxElement.getByLabel(page, "ComboBox with helper component");

        assertEquals("", comboBox.getLocator().getAttribute("focused"));
        assertNull(comboBoxWithHelper.getLocator().getAttribute("focused"));
        comboBox.assertIsFocused();
        comboBoxWithHelper.assertIsNotFocused();
        comboBoxWithHelper.focus();
        comboBoxWithHelper.assertIsFocused();
    }

    @Test
    public void testAriaLabel() {
        ComboBoxElement comboBox = ComboBoxElement.getByLabel(page, "Invisible label");
        comboBox.assertVisible();
        comboBox.assertAriaLabel("Invisible label");
    }

    @Test
    public void testAriaLabelledBy() {
        ComboBoxElement comboBox = ComboBoxElement.getByLabel(page, "Labelled by");
        comboBox.assertVisible();
        comboBox.assertAriaLabel(null);
    }

    @Test
    public void testEnabledDisabled() {
        ComboBoxElement comboBox = ComboBoxElement.getByLabel(page, "Enabled/Disabled Field");
        comboBox.assertDisabled();

        page.locator("#enable-disable-button").click();

        comboBox.assertEnabled();
    }

    @Test
    public void testLabel() {
        ComboBoxElement comboBox = ComboBoxElement.getByLabel(page, "Sort by");
        comboBox.assertVisible();
        comboBox.assertLabel("Sort by");
        ComboBoxElement ariaLabel = ComboBoxElement.getByLabel(page, "Invisible label");
        ariaLabel.assertVisible();
        ariaLabel.assertLabel(null);
    }

    @Test
    public void testTooltip() {
        ComboBoxElement comboBox = ComboBoxElement.getByLabel(page, "Sort by");
        comboBox.assertVisible();
        comboBox.assertTooltipHasText("Tooltip for combo box");
    }

    @Test
    public void testReadOnly() {
        ComboBoxElement comboBox = ComboBoxElement.getByLabel(page, "Read-only ComboBox");
        comboBox.assertVisible();
        comboBox.assertReadOnly();
        comboBox.assertValue("Banana");
    }

    @Test
    public void testFilterAndSelect() {
        ComboBoxElement comboBox = ComboBoxElement.getByLabel(page, "Filterable ComboBox");
        comboBox.filterAndSelectItem("Apr", "Apricot");
        comboBox.assertValue("Apricot");
    }

    @Test
    public void testItemCount() {
        ComboBoxElement comboBox = ComboBoxElement.getByLabel(page, "Filterable ComboBox");
        comboBox.open();
        comboBox.assertItemCount(9);
        comboBox.close();
    }

    @Test
    public void testOpenClose() {
        ComboBoxElement comboBox = ComboBoxElement.getByLabel(page, "Filterable ComboBox");
        comboBox.assertClosed();
        comboBox.open();
        comboBox.assertOpened();
        comboBox.close();
        comboBox.assertClosed();
    }

    @Test
    public void testCustomValue() {
        ComboBoxElement comboBox = ComboBoxElement.getByLabel(page, "Custom value ComboBox");
        comboBox.getInputLocator().fill("Custom entry");
        comboBox.getInputLocator().press("Enter");
        assertThat(page.locator("#custom-value-display")).hasText("Custom entry");
    }

    @Test
    public void testAllowedCharPattern() {
        ComboBoxElement comboBox = ComboBoxElement.getByLabel(page, "Allowed char pattern ComboBox");
        comboBox.assertAllowedCharPattern("[A-Z]");
    }

    @Test
    public void testValue() {
        ComboBoxElement comboBox = ComboBoxElement.getByLabel(page, "Sort by");
        comboBox.selectItem("Rating: low to high");
        assertEquals("Rating: low to high", comboBox.getValue());
    }

    // --- Lazy loading tests ---

    @Test
    public void testLazySelection() {
        ComboBoxElement comboBox = ComboBoxElement.getByLabel(page, "Lazy ComboBox");
        comboBox.assertVisible();
        comboBox.selectItem("Item 1");
        comboBox.assertValue("Item 1");
    }

    @Test
    public void testLazyFilterAndSelect() {
        ComboBoxElement comboBox = ComboBoxElement.getByLabel(page, "Lazy ComboBox");
        comboBox.filterAndSelectItem("Item 250", "Item 250");
        comboBox.assertValue("Item 250");
        assertThat(page.locator("#lazy-selected-value")).hasText("Item 250");
    }

    @Test
    public void testLazyFilterNarrowsToOneResult() {
        ComboBoxElement comboBox = ComboBoxElement.getByLabel(page, "Lazy ComboBox");
        comboBox.setFilter("Item 500");
        comboBox.assertItemCount(1);
        comboBox.close();
    }

    @Test
    public void testLazyFilterMultipleResults() {
        ComboBoxElement comboBox = ComboBoxElement.getByLabel(page, "Lazy ComboBox");
        // "Item 49" matches: Item 49, Item 490..Item 499 = 11 items
        comboBox.setFilter("Item 49");
        comboBox.assertItemCount(11);
        comboBox.close();
    }

    @Test
    public void testLazyClearAfterSelection() {
        ComboBoxElement comboBox = ComboBoxElement.getByLabel(page, "Lazy ComboBox");
        comboBox.selectItem("Item 1");
        comboBox.assertValue("Item 1");
        comboBox.clickClearButton();
        comboBox.assertValue("");
    }
}
