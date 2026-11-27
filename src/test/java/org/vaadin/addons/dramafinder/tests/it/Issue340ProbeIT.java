package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.addons.dramafinder.element.ComboBoxElement;
import org.vaadin.addons.dramafinder.element.IntegerFieldElement;
import org.vaadin.addons.dramafinder.element.MultiSelectComboBoxElement;
import org.vaadin.addons.dramafinder.element.TextFieldElement;
import org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/** Temporary probe for issue #340: each test states the expected user-visible behaviour. */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class Issue340ProbeIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "issue-340";
    }

    @Test
    public void serverRewritesValue() {
        TextFieldElement f = TextFieldElement.getByLabel(page, "Upper");
        f.setValue("hello");
        f.assertValue("HELLO");
    }

    @Test
    public void onBlurValueChangeMode() {
        TextFieldElement f = TextFieldElement.getByLabel(page, "On blur");
        f.setValue("typed");
        assertThat(page.locator("#on-blur-value")).hasText("typed");
    }

    @Test
    public void onBlurWithBlur() {
        TextFieldElement f = TextFieldElement.getByLabel(page, "On blur");
        f.setValue("typed");
        f.getInputLocator().blur();
        assertThat(page.locator("#on-blur-value")).hasText("typed");
    }

    @Test
    public void lazyMode() {
        TextFieldElement f = TextFieldElement.getByLabel(page, "Lazy");
        f.setValue("typed");
        // no retry window longer than the 2 s debounce: check right after the action
        assertThat(page.locator("#lazy-value")).hasText("typed",
                new com.microsoft.playwright.assertions.LocatorAssertions.HasTextOptions().setTimeout(500));
    }

    private void fillAndBlur(TextFieldElement f, String v) {
        f.getInputLocator().fill(v);
        f.getInputLocator().blur();
        f.waitAfterAction();
    }

    @Test
    public void blurOnlyUpper() {
        TextFieldElement f = TextFieldElement.getByLabel(page, "Upper");
        fillAndBlur(f, "hello");
        f.assertValue("HELLO");
    }

    @Test
    public void blurOnlyLazy() {
        TextFieldElement f = TextFieldElement.getByLabel(page, "Lazy");
        fillAndBlur(f, "typed");
        assertThat(page.locator("#lazy-value")).hasText("typed",
                new com.microsoft.playwright.assertions.LocatorAssertions.HasTextOptions().setTimeout(500));
    }

    @Test
    public void blurOnlyOnBlur() {
        TextFieldElement f = TextFieldElement.getByLabel(page, "On blur");
        fillAndBlur(f, "typed");
        assertThat(page.locator("#on-blur-value")).hasText("typed",
                new com.microsoft.playwright.assertions.LocatorAssertions.HasTextOptions().setTimeout(500));
    }

    @Test
    public void binderValidation() {
        IntegerFieldElement f = IntegerFieldElement.getByLabel(page, "Amount");
        f.setValue("5");
        assertThat(page.locator("#validation-status")).hasText("invalid");
        f.assertInvalid();
    }

    @Test
    public void comboBoxClosesAfterSelect() {
        ComboBoxElement c = ComboBoxElement.getByLabel(page, "Fruit");
        c.selectItem("Banana");
        c.assertClosed();
        c.selectItem("Banana"); // already selected
        c.assertClosed();
        c.filterAndSelectItem("Ch", "Cherry");
        c.assertClosed();
        ButtonElement.getByText(page, "Below").click();
        assertThat(page.locator("#clicked")).hasText("clicked");
    }

    @Test
    public void multiSelectThenClickBelow() {
        MultiSelectComboBoxElement m = MultiSelectComboBoxElement.getByLabel(page, "Fruits");
        m.selectItem("Apple");
        ButtonElement.getByText(page, "Below").click();
        assertThat(page.locator("#clicked")).hasText("clicked");
    }
}
