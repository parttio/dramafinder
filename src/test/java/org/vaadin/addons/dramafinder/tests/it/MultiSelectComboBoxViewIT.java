package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.addons.dramafinder.element.MultiSelectComboBoxElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MultiSelectComboBoxViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "multi-select-combo-box";
    }

    @Test
    public void testTitle() {
        assertThat(page).hasTitle("MultiSelectComboBox Demo");
    }

    @Test
    public void testSelection() {
        MultiSelectComboBoxElement comboBox = MultiSelectComboBoxElement.getByLabel(page, "Fruits");
        comboBox.assertVisible();
        comboBox.selectItem("Apple");
        comboBox.close();
        comboBox.assertSelectedCount(1);
    }

    @Test
    public void testMultiSelection() {
        MultiSelectComboBoxElement comboBox = MultiSelectComboBoxElement.getByLabel(page, "Fruits");
        comboBox.selectItems("Apple", "Cherry");
        comboBox.close();
        comboBox.assertSelectedCount(2);
    }

    @Test
    public void testDeselection() {
        MultiSelectComboBoxElement comboBox = MultiSelectComboBoxElement.getByLabel(page, "Fruits");
        comboBox.selectItems("Apple", "Cherry");
        comboBox.close();
        comboBox.assertSelectedCount(2);
        comboBox.deselectItem("Apple");
        comboBox.close();
        comboBox.assertSelectedCount(1);
    }

    @Test
    public void testHasHelperText() {
        MultiSelectComboBoxElement comboBox = MultiSelectComboBoxElement.getByLabel(page, "Fruits");
        comboBox.assertVisible();
        assertThat(comboBox.getHelperLocator()).hasText("Helper text");
        comboBox.assertHelperHasText("Helper text");
        assertEquals("Helper text", comboBox.getHelperText());
    }

    @Test
    public void testPlaceholder() {
        MultiSelectComboBoxElement comboBox = MultiSelectComboBoxElement.getByLabel(page, "MultiSelect with placeholder and clear button");
        comboBox.assertVisible();
        comboBox.assertPlaceholder("Select items");
        assertEquals("Select items", comboBox.getPlaceholder());
    }

    @Test
    public void testClearButton() {
        MultiSelectComboBoxElement comboBox = MultiSelectComboBoxElement.getByLabel(page, "MultiSelect with placeholder and clear button");
        comboBox.selectItems("Apple", "Banana");
        comboBox.close();
        comboBox.assertSelectedCount(2);
        comboBox.clickClearButton();
        comboBox.assertSelectedCount(0);
    }

    @Test
    public void testTheme() {
        MultiSelectComboBoxElement comboBox = MultiSelectComboBoxElement.getByLabel(page, "MultiSelect with theme");
        comboBox.assertVisible();
        comboBox.assertTheme("small");
    }

    @Test
    public void testValidation() {
        MultiSelectComboBoxElement comboBox = MultiSelectComboBoxElement.getByLabel(page, "MultiSelect with theme");
        comboBox.assertValid();
        ButtonElement.getByText(page, "Validate").click();
        comboBox.assertInvalid();
        assertThat(comboBox.getErrorMessageLocator()).hasText("Required field");
    }

    @Test
    public void testFocused() {
        MultiSelectComboBoxElement comboBox = MultiSelectComboBoxElement.getByLabel(page, "Fruits");
        MultiSelectComboBoxElement comboBoxWithHelper = MultiSelectComboBoxElement.getByLabel(page, "MultiSelect with helper component");

        assertEquals("", comboBox.getLocator().getAttribute("focused"));
        assertNull(comboBoxWithHelper.getLocator().getAttribute("focused"));
        comboBox.assertIsFocused();
        comboBoxWithHelper.assertIsNotFocused();
        comboBoxWithHelper.focus();
        comboBoxWithHelper.assertIsFocused();
    }

    @Test
    public void testAriaLabel() {
        MultiSelectComboBoxElement comboBox = MultiSelectComboBoxElement.getByLabel(page, "Invisible label");
        comboBox.assertVisible();
        comboBox.assertAriaLabel("Invisible label");
    }

    @Test
    public void testAriaLabelledBy() {
        MultiSelectComboBoxElement comboBox = MultiSelectComboBoxElement.getByLabel(page, "Labelled by");
        comboBox.assertVisible();
        comboBox.assertAriaLabel(null);
    }

    @Test
    public void testEnabledDisabled() {
        MultiSelectComboBoxElement comboBox = MultiSelectComboBoxElement.getByLabel(page, "Enabled/Disabled Field");
        comboBox.assertDisabled();

        page.locator("#enable-disable-button").click();

        comboBox.assertEnabled();
    }

    @Test
    public void testLabel() {
        MultiSelectComboBoxElement comboBox = MultiSelectComboBoxElement.getByLabel(page, "Fruits");
        comboBox.assertVisible();
        comboBox.assertLabel("Fruits");
        MultiSelectComboBoxElement ariaLabel = MultiSelectComboBoxElement.getByLabel(page, "Invisible label");
        ariaLabel.assertVisible();
        ariaLabel.assertLabel(null);
    }

    @Test
    public void testTooltip() {
        MultiSelectComboBoxElement comboBox = MultiSelectComboBoxElement.getByLabel(page, "Fruits");
        comboBox.assertVisible();
        comboBox.assertTooltipHasText("Tooltip for multi-select");
    }

    @Test
    public void testReadOnly() {
        MultiSelectComboBoxElement comboBox = MultiSelectComboBoxElement.getByLabel(page, "Read-only MultiSelect");
        comboBox.assertVisible();
        comboBox.assertReadOnly();
        comboBox.assertSelectedCount(2);
    }

    @Test
    public void testFilterAndSelect() {
        MultiSelectComboBoxElement comboBox = MultiSelectComboBoxElement.getByLabel(page, "Filterable MultiSelect");
        comboBox.filterAndSelectItem("Apr", "Apricot");
        comboBox.close();
        comboBox.assertSelectedCount(1);
    }

    @Test
    public void testItemCount() {
        MultiSelectComboBoxElement comboBox = MultiSelectComboBoxElement.getByLabel(page, "Filterable MultiSelect");
        comboBox.open();
        comboBox.assertItemCount(9);
        comboBox.close();
    }

    @Test
    public void testOpenClose() {
        MultiSelectComboBoxElement comboBox = MultiSelectComboBoxElement.getByLabel(page, "Filterable MultiSelect");
        comboBox.assertClosed();
        comboBox.open();
        comboBox.assertOpened();
        comboBox.close();
        comboBox.assertClosed();
    }

    @Test
    public void testChips() {
        MultiSelectComboBoxElement comboBox = MultiSelectComboBoxElement.getByLabel(page, "Fruits");
        comboBox.selectItems("Apple", "Cherry");
        comboBox.close();
        assertTrue(comboBox.getSelectedItems().contains("Apple"));
        assertTrue(comboBox.getSelectedItems().contains("Cherry"));
        comboBox.assertSelectedCount(2);
    }

    // --- Lazy loading tests ---

    @Test
    public void testLazySelection() {
        MultiSelectComboBoxElement comboBox = MultiSelectComboBoxElement.getByLabel(page, "Lazy MultiSelect");
        comboBox.assertVisible();
        comboBox.selectItem("Item 1");
        comboBox.close();
        comboBox.assertSelectedCount(1);
    }

    @Test
    public void testLazyFilterAndSelect() {
        MultiSelectComboBoxElement comboBox = MultiSelectComboBoxElement.getByLabel(page, "Lazy MultiSelect");
        comboBox.filterAndSelectItem("Item 250", "Item 250");
        comboBox.close();
        comboBox.assertSelectedCount(1);
        assertThat(page.locator("#lazy-selected-value")).hasText("Item 250");
    }

    @Test
    public void testLazyFilterNarrowsResults() {
        MultiSelectComboBoxElement comboBox = MultiSelectComboBoxElement.getByLabel(page, "Lazy MultiSelect");
        comboBox.setFilter("Item 500");
        comboBox.assertItemCount(1);
        comboBox.close();
    }

    @Test
    public void testLazyFilterMultipleResults() {
        MultiSelectComboBoxElement comboBox = MultiSelectComboBoxElement.getByLabel(page, "Lazy MultiSelect");
        // "Item 49" matches: Item 49, Item 490..Item 499 = 11 items
        comboBox.setFilter("Item 49");
        comboBox.assertItemCount(11);
        comboBox.close();
    }

    @Test
    public void testLazyMultiSelect() {
        MultiSelectComboBoxElement comboBox = MultiSelectComboBoxElement.getByLabel(page, "Lazy MultiSelect");
        comboBox.selectItems("Item 1", "Item 2", "Item 3");
        comboBox.close();
        comboBox.assertSelectedCount(3);
    }

    @Test
    public void testLazyClearAfterSelection() {
        MultiSelectComboBoxElement comboBox = MultiSelectComboBoxElement.getByLabel(page, "Lazy MultiSelect");
        comboBox.selectItems("Item 1", "Item 2");
        comboBox.close();
        comboBox.assertSelectedCount(2);
        comboBox.clickClearButton();
        comboBox.assertSelectedCount(0);
    }

    // --- Custom renderer tests ---

    @Test
    public void testCustomRendererItemComponent() {
        MultiSelectComboBoxElement comboBox = MultiSelectComboBoxElement.getByLabel(page, "Custom renderer");
        comboBox.open();
        ButtonElement button = comboBox.getOverlayItemComponent("Apple", ButtonElement.class);
        button.assertVisible();
        assertThat(button.getLocator()).hasText("Info Apple");
    }

    @Test
    public void testCustomRendererClickButton() {
        MultiSelectComboBoxElement comboBox = MultiSelectComboBoxElement.getByLabel(page, "Custom renderer");
        comboBox.open();
        ButtonElement button = comboBox.getOverlayItemComponent("Banana", ButtonElement.class);
        button.click();
        assertThat(page.locator("#custom-renderer-output")).hasText("Clicked: Banana");
    }

    @Test
    public void testCustomRendererSelectWithComponent() {
        MultiSelectComboBoxElement comboBox = MultiSelectComboBoxElement.getByLabel(page, "Custom renderer");
        comboBox.selectItem("Cherry");
        comboBox.close();
        comboBox.assertSelectedCount(1);
    }
}
