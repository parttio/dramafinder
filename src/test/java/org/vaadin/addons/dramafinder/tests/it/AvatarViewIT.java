package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.AvatarElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AvatarViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "avatar";
    }

    // ── Page title ────────────────────────────────────────────────────

    @Test
    public void testTitle() {
        assertThat(page).hasTitle("Avatar Demo");
    }

    // ── Factory methods ───────────────────────────────────────────────

    @Test
    public void testGetByPage() {
        AvatarElement avatar = AvatarElement.get(page);
        avatar.assertVisible();
    }

    @Test
    public void testGetByLocator() {
        AvatarElement avatar = AvatarElement.get(page.locator("#avatar-container"));
        avatar.assertName("Jane Smith");
    }

    @Test
    public void testGetByName() {
        AvatarElement avatar = AvatarElement.getByName(page, "Jane Smith");
        avatar.assertVisible();
        avatar.assertName("Jane Smith");
    }

    // ── Properties ────────────────────────────────────────────────────

    @Test
    public void testName() {
        AvatarElement avatar = new AvatarElement(page.locator("#avatar-with-name"));
        avatar.assertName("Jane Smith");
        assertEquals("Jane Smith", avatar.getName());
        avatar.setName("New Name");
        avatar.assertName("New Name");
    }

    @Test
    public void testAbbreviation() {
        AvatarElement avatar = new AvatarElement(page.locator("#avatar-with-abbr"));
        avatar.assertAbbreviation("AB");
        assertEquals("AB", avatar.getAbbreviation());
        avatar.setAbbreviation("CD");
        avatar.assertAbbreviation("CD");
    }

    @Test
    public void testImage() {
        AvatarElement avatar = new AvatarElement(page.locator("#avatar-with-image"));
        avatar.assertHasImage();
        String img = avatar.getImage();
        assertThat(avatar.getLocator()).hasJSProperty("img", img);
    }

    @Test
    public void testNoImage() {
        AvatarElement avatar = new AvatarElement(page.locator("#avatar-no-image"));
        avatar.assertHasNoImage();
        avatar.setImage("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='1' height='1'%3E%3C/svg%3E");
        avatar.assertHasImage();
    }

    @Test
    public void testColorIndex() {
        AvatarElement avatar = new AvatarElement(page.locator("#avatar-color-index"));
        assertEquals(3, avatar.getColorIndex());
        avatar.setColorIndex(5);
        assertEquals(5, avatar.getColorIndex());
    }

    // ── Interface mixins ──────────────────────────────────────────────

    @Test
    public void testTheme() {
        AvatarElement avatar = new AvatarElement(page.locator("#avatar-with-theme"));
        avatar.assertTheme("small");
    }

    @Test
    public void testTooltip() {
        AvatarElement avatar = new AvatarElement(page.locator("#avatar-with-tooltip"));
        avatar.assertTooltipHasText("Tooltip");
    }

    @Test
    public void testCssClass() {
        AvatarElement avatar = new AvatarElement(page.locator("#avatar-with-class"));
        avatar.assertCssClass("custom-avatar");
    }

    @Test
    public void testFocus() {
        AvatarElement avatar = new AvatarElement(page.locator("#avatar-focusable"));
        avatar.focus();
        avatar.assertIsFocused();
        avatar.blur();
        avatar.assertIsNotFocused();
    }

    @Test
    public void testVisibility() {
        AvatarElement avatar = new AvatarElement(page.locator("#avatar-with-name"));
        avatar.assertVisible();
    }
}
