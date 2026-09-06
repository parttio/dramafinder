package org.vaadin.addons.dramafinder.tests.it;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.AvatarElement;
import org.vaadin.addons.dramafinder.element.AvatarGroupElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AvatarGroupViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "avatar-group";
    }

    private AvatarGroupElement basicGroup() {
        return new AvatarGroupElement(page.locator("#avatar-group-basic"));
    }

    private AvatarGroupElement overflowGroup() {
        return new AvatarGroupElement(page.locator("#avatar-group-overflow"));
    }

    // ── Page title ────────────────────────────────────────────────────

    @Test
    public void testTitle() {
        assertThat(page).hasTitle("Avatar Group Demo");
    }

    // ── Factory methods ───────────────────────────────────────────────

    @Test
    public void testGetByPage() {
        AvatarGroupElement group = AvatarGroupElement.get(page);
        group.assertVisible();
        group.assertNames("Jane Smith", "John Doe", "Alice Cooper");
    }

    @Test
    public void testGetByLocator() {
        AvatarGroupElement group = AvatarGroupElement.get(page.locator("#avatar-group-container"));
        group.assertVisible();
        group.assertVisibleCount(3);
    }

    // ── Visible avatars ───────────────────────────────────────────────

    @Test
    public void testVisibleCount() {
        basicGroup().assertVisibleCount(3);
        assertEquals(3, basicGroup().getVisibleCount());
    }

    @Test
    public void testNames() {
        basicGroup().assertNames("Jane Smith", "John Doe", "Alice Cooper");
        assertEquals(List.of("Jane Smith", "John Doe", "Alice Cooper"),
                basicGroup().getNames());
    }

    @Test
    public void testGetAvatars() {
        List<AvatarElement> avatars = basicGroup().getAvatars();
        assertEquals(3, avatars.size());
        avatars.get(0).assertName("Jane Smith");
        avatars.get(2).assertName("Alice Cooper");
    }

    @Test
    public void testGetAvatarByIndex() {
        basicGroup().getAvatar(1).assertName("John Doe");
    }

    // ── maxItemsVisible ───────────────────────────────────────────────

    @Test
    public void testMaxItemsVisibleUnset() {
        assertNull(basicGroup().getMaxItemsVisible());
    }

    @Test
    public void testMaxItemsVisible() {
        assertEquals(3, overflowGroup().getMaxItemsVisible());
    }

    @Test
    public void testSetMaxItemsVisible() {
        AvatarGroupElement group = basicGroup();
        group.assertHasNoOverflow();
        group.setMaxItemsVisible(2);
        assertEquals(2, group.getMaxItemsVisible());
        group.assertHasOverflow();
    }

    // ── Overflow ──────────────────────────────────────────────────────

    @Test
    public void testNoOverflow() {
        AvatarGroupElement group = basicGroup();
        group.assertHasNoOverflow();
        assertFalse(group.hasOverflow());
        group.getOverflowAvatar().assertHidden();
    }

    @Test
    public void testOverflowVisibleAvatars() {
        AvatarGroupElement group = overflowGroup();
        group.assertHasOverflow();
        assertTrue(group.hasOverflow());
        group.assertVisibleCount(2);
        group.assertNames("Bob Ross", "Carol Danvers");
    }

    @Test
    public void testOverflowAvatar() {
        AvatarElement overflowAvatar = overflowGroup().getOverflowAvatar();
        overflowAvatar.assertVisible();
        overflowAvatar.assertAbbreviation("+3");
    }

    @Test
    public void testOpenAndCloseOverflow() {
        AvatarGroupElement group = overflowGroup();
        group.assertOverflowClosed();
        assertFalse(group.isOverflowOpen());

        group.openOverflow();
        group.assertOverflowOpen();
        assertTrue(group.isOverflowOpen());
        assertThat(group.getOverflowMenuLocator()).isVisible();

        group.closeOverflow();
        group.assertOverflowClosed();
        assertFalse(group.isOverflowOpen());
    }

    @Test
    public void testOpenOverflowIsIdempotent() {
        AvatarGroupElement group = overflowGroup();
        group.openOverflow();
        group.openOverflow();
        group.assertOverflowOpen();
    }

    @Test
    public void testOverflowNames() {
        AvatarGroupElement group = overflowGroup();
        group.openOverflow();
        group.assertOverflowNames("Dana Scully", "Erin Fisher", "Xavier Young");
        assertEquals(List.of("Dana Scully", "Erin Fisher", "Xavier Young"),
                group.getOverflowNames());
    }

    @Test
    public void testOverflowAvatars() {
        AvatarGroupElement group = overflowGroup();
        group.openOverflow();

        List<AvatarElement> overflowAvatars = group.getOverflowAvatars();
        assertEquals(3, overflowAvatars.size());
        overflowAvatars.get(0).assertName("Dana Scully");
        assertEquals(3, overflowAvatars.get(1).getColorIndex());
        overflowAvatars.get(2).assertAbbreviation("XY");
    }

    // ── Interface mixins ──────────────────────────────────────────────

    @Test
    public void testTheme() {
        AvatarGroupElement group = new AvatarGroupElement(page.locator("#avatar-group-styled"));
        group.assertTheme("small");
        assertEquals("small", group.getTheme());
    }

    @Test
    public void testCssClass() {
        AvatarGroupElement group = new AvatarGroupElement(page.locator("#avatar-group-styled"));
        group.assertCssClass("custom-avatar-group");
    }

    @Test
    public void testVisibility() {
        basicGroup().assertVisible();
    }
}
