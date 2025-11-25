package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.ProgressBarElement;
import org.vaadin.addons.dramafinder.tests.it.SpringPlaywrightIT;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ProgressBarViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "progress-bar";
    }

    @Test
    public void testTitle() {
        assertThat(page).hasTitle("ProgressBar Demo");
    }

    @Test
    public void testProgressBar() {
        ProgressBarElement progressBar = new ProgressBarElement(page.locator("#progress-bar"));
        progressBar.assertVisible();
        progressBar.assertValue(0.5);
        progressBar.assertNotIndeterminate();
    }

    @Test
    public void testIndeterminateProgressBar() {
        ProgressBarElement progressBar = new ProgressBarElement(page.locator("#indeterminate-progress-bar"));
        progressBar.assertVisible();
        progressBar.assertIndeterminate();
    }

    @Test
    public void testMinMaxProgressBar() {
        ProgressBarElement progressBar = new ProgressBarElement(page.locator("#min-max-progress-bar"));
        progressBar.assertVisible();
        progressBar.assertMin(10.50);
        progressBar.assertMax(100);
        progressBar.assertValue(50.0);
        progressBar.assertNotIndeterminate();
        progressBar.setValue(31);
        progressBar.assertValue(31.0);
        progressBar.setMin(12);
        progressBar.assertMin(12.0);
        progressBar.setMax(120);
        progressBar.assertMax(120.0);

    }
}
