package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.HasTestView;
import org.vaadin.addons.dramafinder.element.BadgeElement;
import org.vaadin.addons.dramafinder.element.ButtonElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class BadgeViewIT extends SpringPlaywrightIT implements HasTestView {

    @Override
    public String getView() {
        return "badge";
    }

    // ── Page title ────────────────────────────────────────────────────

    @Test
    public void testTitle() {
        assertThat(page).hasTitle("Badge Demo");
    }

    // ── Factory methods ───────────────────────────────────────────────

    @Test
    public void testGetByPage() {
        BadgeElement badge = BadgeElement.get(page);
        badge.assertVisible();
        badge.assertText("Scoped");
    }

    @Test
    public void testGetByLocator() {
        BadgeElement badge = BadgeElement.get(page.locator("#badge-container"));
        badge.assertVisible();
        badge.assertText("Scoped");
    }

    @Test
    public void testGetByText() {
        BadgeElement badge = BadgeElement.getByText(page, "Draft");
        badge.assertVisible();
        badge.assertText("Draft");
    }

    @Test
    public void testGetByTextScoped() {
        BadgeElement badge = BadgeElement.getByText(page.locator("#badge-container"), "Scoped");
        badge.assertVisible();
        badge.assertText("Scoped");
    }

    // ── Text ──────────────────────────────────────────────────────────

    @Test
    public void testText() {
        BadgeElement badge = new BadgeElement(page.locator("#badge-plain"));
        badge.assertText("Pending");
        assertEquals("Pending", badge.getText());
    }

    @Test
    public void testTextIsUpdated() {
        BadgeElement badge = new BadgeElement(page.locator("#badge-dynamic"));
        badge.assertText("Initial");
        badge.assertNumber(null);
        badge.assertHasNoThemeVariant("success");

        ButtonElement.getByText(page, "Update badge").click();

        badge.assertText("Updated");
        badge.assertNumber(9);
        badge.assertHasThemeVariant("success");
    }

    @Test
    public void testNoText() {
        BadgeElement badge = new BadgeElement(page.locator("#badge-dot"));
        badge.assertText(null);
    }

    // ── Number ────────────────────────────────────────────────────────

    @Test
    public void testNumber() {
        BadgeElement badge = new BadgeElement(page.locator("#badge-with-number"));
        badge.assertNumber(5);
        assertEquals(5, badge.getNumber());
        // The number is rendered in the shadow DOM, so it is not part of the text
        badge.assertText("unread messages");
    }

    @Test
    public void testNoNumber() {
        BadgeElement badge = new BadgeElement(page.locator("#badge-plain"));
        badge.assertNumber(null);
        assertNull(badge.getNumber());
    }

    // ── Icon slot ─────────────────────────────────────────────────────

    @Test
    public void testIcon() {
        BadgeElement badge = new BadgeElement(page.locator("#badge-with-icon"));
        badge.assertHasIcon();
        assertThat(badge.getIconLocator()).hasCount(1);
        badge.assertText("Verified");
    }

    @Test
    public void testNoIcon() {
        BadgeElement badge = new BadgeElement(page.locator("#badge-without-icon"));
        badge.assertHasNoIcon();
        assertThat(badge.getIconLocator()).hasCount(0);
    }

    // ── Theme variants ────────────────────────────────────────────────

    @Test
    public void testSuccessVariant() {
        BadgeElement badge = new BadgeElement(page.locator("#badge-success"));
        badge.assertTheme("success");
        badge.assertHasThemeVariant("success");
        assertEquals("success", badge.getTheme());
    }

    @Test
    public void testErrorVariant() {
        new BadgeElement(page.locator("#badge-error")).assertHasThemeVariant("error");
    }

    @Test
    public void testContrastVariant() {
        new BadgeElement(page.locator("#badge-contrast")).assertHasThemeVariant("contrast");
    }

    @Test
    public void testWarningVariant() {
        new BadgeElement(page.locator("#badge-warning")).assertHasThemeVariant("warning");
    }

    @Test
    public void testDotVariant() {
        new BadgeElement(page.locator("#badge-dot")).assertHasThemeVariant("dot");
    }

    @Test
    public void testCombinedVariants() {
        BadgeElement badge = new BadgeElement(page.locator("#badge-small-success"));
        badge.assertHasThemeVariant("success");
        badge.assertHasThemeVariant("small");
        badge.assertHasNoThemeVariant("error");
    }

    @Test
    public void testNoThemeVariant() {
        BadgeElement badge = new BadgeElement(page.locator("#badge-plain"));
        badge.assertTheme(null);
        badge.assertHasNoThemeVariant("success");
    }

    // ── Interface mixins ──────────────────────────────────────────────

    @Test
    public void testCssClass() {
        BadgeElement badge = new BadgeElement(page.locator("#badge-with-class"));
        badge.assertCssClass("custom-badge");
        assertEquals("custom-badge", badge.getCssClass());
    }

    @Test
    public void testVisibility() {
        BadgeElement badge = new BadgeElement(page.locator("#badge-plain"));
        badge.assertVisible();
    }
}
