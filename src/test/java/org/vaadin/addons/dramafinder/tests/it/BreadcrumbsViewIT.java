package org.vaadin.addons.dramafinder.tests.it;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.HasTestView;
import org.vaadin.addons.dramafinder.element.BreadcrumbsElement;
import org.vaadin.addons.dramafinder.element.BreadcrumbsItemElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class BreadcrumbsViewIT extends SpringPlaywrightIT implements HasTestView {

    @Override
    public String getView() {
        return "breadcrumbs";
    }

    private BreadcrumbsElement getTrail() {
        return BreadcrumbsElement.getByLabel(page, "Main breadcrumbs");
    }

    private BreadcrumbsElement getNarrowTrail() {
        return BreadcrumbsElement.getByLabel(page, "Narrow breadcrumbs");
    }

    @Test
    public void testTrail() {
        BreadcrumbsElement breadcrumbs = getTrail();

        breadcrumbs.assertVisible();
        breadcrumbs.assertAriaLabel("Main breadcrumbs");
        breadcrumbs.assertCssClass("main-breadcrumbs");
        breadcrumbs.assertTheme("slash");
        breadcrumbs.assertItemCount(4);
        breadcrumbs.assertItemTexts("Home", "Movies", "Archive", "Drama");
        breadcrumbs.assertHasNoOverflow();
        assertFalse(breadcrumbs.hasOverflow());
    }

    @Test
    public void testGetItems() {
        BreadcrumbsElement breadcrumbs = getTrail();

        List<BreadcrumbsItemElement> items = breadcrumbs.getItems();
        assertEquals(4, items.size());
        assertEquals("Home", items.get(0).getText());
        assertEquals("Drama", items.get(3).getText());

        breadcrumbs.getItem(1).assertText("Movies");
        breadcrumbs.getItem("Archive").assertText("Archive");
    }

    @Test
    public void testFirstElementOnThePage() {
        // The main trail is the first vaadin-breadcrumbs of the view.
        BreadcrumbsElement.get(page).assertAriaLabel("Main breadcrumbs");
    }

    @Test
    public void testItemLinkAndPath() {
        BreadcrumbsElement breadcrumbs = getTrail();

        BreadcrumbsItemElement home = breadcrumbs.getItem("Home");
        home.assertLink();
        home.assertPath("card");
        assertTrue(home.isLink());
        assertEquals("card", home.getPath());
        assertThat(home.getLinkLocator()).hasAttribute("href", "card");

        BreadcrumbsItemElement drama = breadcrumbs.getItem("Drama");
        drama.assertNotLink();
        drama.assertPath(null);
        assertFalse(drama.isLink());
        assertNull(drama.getPath());
    }

    @Test
    public void testCurrentItem() {
        BreadcrumbsElement breadcrumbs = getTrail();

        BreadcrumbsItemElement current = breadcrumbs.getCurrentItem();
        current.assertText("Drama");
        current.assertCurrent();
        assertTrue(current.isCurrent());

        BreadcrumbsItemElement home = breadcrumbs.getItem("Home");
        home.assertNotCurrent();
        assertFalse(home.isCurrent());
    }

    @Test
    public void testItemEnabledState() {
        BreadcrumbsElement breadcrumbs = getTrail();

        breadcrumbs.getItem("Movies").assertEnabled();
        breadcrumbs.getItem("Archive").assertDisabled();
    }

    @Test
    public void testItemPrefix() {
        BreadcrumbsElement breadcrumbs = getTrail();

        BreadcrumbsItemElement home = breadcrumbs.getItem("Home");
        home.assertHasPrefix();
        assertTrue(home.hasPrefix());
        assertThat(home.getPrefixLocator()).isVisible();

        BreadcrumbsItemElement movies = breadcrumbs.getItem("Movies");
        movies.assertHasNoPrefix();
        assertFalse(movies.hasPrefix());
    }

    @Test
    public void testGetItemByText() {
        BreadcrumbsItemElement item = BreadcrumbsItemElement.getByText(page, "Movies");
        item.assertVisible();
        item.assertPath("grid-basic");

        BreadcrumbsItemElement scoped = BreadcrumbsItemElement
                .getByText(getTrail().getLocator(), "Archive");
        scoped.assertDisabled();
    }

    @Test
    public void testClickNavigates() {
        assertThat(page).hasURL(getUrl() + getView());

        getTrail().getItem("Home").click();

        assertThat(page).hasURL(getUrl() + "card");
    }

    @Test
    public void testOverflow() {
        BreadcrumbsElement breadcrumbs = getNarrowTrail();

        breadcrumbs.assertVisible();
        breadcrumbs.assertHasOverflow();
        assertTrue(breadcrumbs.hasOverflow());
        breadcrumbs.assertOverflowButtonAriaLabel("Show more items");

        // Collapsed items stay part of the trail.
        breadcrumbs.assertItemCount(5);
        breadcrumbs.assertItemTexts("Continents", "South America", "Argentina",
                "Buenos Aires", "Palermo");

        // How many items collapse depends on the available width, but the item
        // after the root always does and the last item never does.
        assertTrue(breadcrumbs.getOverflowItems().size() >= 1);
        assertThat(breadcrumbs.getItem("South America").getLocator())
                .hasAttribute("slot", "overlay");
        assertThat(breadcrumbs.getItem("Palermo").getLocator())
                .not().hasAttribute("slot", "overlay");
        breadcrumbs.getItem("Palermo").assertCurrent();
    }

    @Test
    public void testOpenAndCloseOverflow() {
        BreadcrumbsElement breadcrumbs = getNarrowTrail();

        breadcrumbs.assertOverflowClosed();
        assertFalse(breadcrumbs.isOverflowOpen());
        assertThat(breadcrumbs.getItem("South America").getLocator()).not().isVisible();

        breadcrumbs.openOverflow();
        breadcrumbs.assertOverflowOpen();
        assertTrue(breadcrumbs.isOverflowOpen());
        assertThat(breadcrumbs.getItem("South America").getLocator()).isVisible();

        breadcrumbs.closeOverflow();
        breadcrumbs.assertOverflowClosed();
        assertThat(breadcrumbs.getItem("South America").getLocator()).not().isVisible();
    }

    @Test
    public void testClickOverflowItemNavigates() {
        BreadcrumbsElement breadcrumbs = getNarrowTrail();

        breadcrumbs.openOverflow();
        breadcrumbs.getItem("South America").click();

        assertThat(page).hasURL(getUrl() + "card");
    }
}
