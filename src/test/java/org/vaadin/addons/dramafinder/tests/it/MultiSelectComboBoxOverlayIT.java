package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.addons.dramafinder.element.MultiSelectComboBoxElement;
import org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * The selection methods of {@link MultiSelectComboBoxElement} leave the overlay
 * as they found it (issue #344), so a closed overlay never covers the next action.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MultiSelectComboBoxOverlayIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "multi-select-combo-box-overlay";
    }

    private MultiSelectComboBoxElement fruits() {
        return MultiSelectComboBoxElement.getByLabel(page, "Fruits");
    }

    private void clickButtonBelow() {
        ButtonElement.getByText(page, "Below").click();
        assertThat(page.locator("#below-clicked")).hasText("clicked");
    }

    @Test
    public void selectItemClosesTheOverlayItOpened() {
        MultiSelectComboBoxElement fruits = fruits();
        fruits.selectItem("Apple");
        fruits.assertClosed();
        clickButtonBelow();
        assertThat(page.locator("#fruits-value")).hasText("Apple");
    }

    @Test
    public void selectAndDeselectItemsCloseTheOverlayAtTheEnd() {
        MultiSelectComboBoxElement fruits = fruits();
        fruits.selectItems("Apple", "Cherry", "Elderberry");
        fruits.assertClosed();
        assertThat(page.locator("#fruits-value")).hasText("Apple, Cherry, Elderberry");

        fruits.deselectItems("Apple", "Elderberry");
        fruits.assertClosed();
        clickButtonBelow();
        assertThat(page.locator("#fruits-value")).hasText("Cherry");
    }

    @Test
    public void overlayOpenedByTheTestStaysOpen() {
        MultiSelectComboBoxElement fruits = fruits();
        fruits.open();
        fruits.selectItems("Apple", "Banana");
        fruits.assertOpened();
        fruits.deselectItem("Apple");
        fruits.assertOpened();
        assertThat(page.locator("#fruits-value")).hasText("Banana");
    }

    @Test
    public void filterAndSelectItemClosesTheOverlayItOpened() {
        MultiSelectComboBoxElement fruits = fruits();
        fruits.filterAndSelectItem("Ch", "Cherry");
        fruits.assertClosed();
        clickButtonBelow();
        assertThat(page.locator("#fruits-value")).hasText("Cherry");
    }
}
