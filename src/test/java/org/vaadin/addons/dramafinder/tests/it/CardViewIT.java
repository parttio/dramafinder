package org.vaadin.addons.dramafinder.tests.it;

import com.microsoft.playwright.Locator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.HasTestView;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.addons.dramafinder.element.CardElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CardViewIT extends SpringPlaywrightIT implements HasTestView {

    @Override
    public String getView() {
        return "card";
    }

    @Test
    public void cardSlotsAreReachable() {
        CardElement card = CardElement.getByTitle(page, "Lapland");

        card.assertVisible();
        card.assertTheme("outlined");
        card.assertCssClass("lapland-card");
        card.assertTitle("Lapland");
        card.assertSubtitle("The Exotic North");

        assertThat(card.getHeaderPrefixLocator()).hasText("Lapland prefix");
        assertThat(card.getHeaderSuffixLocator()).hasText("Arctic");
        assertThat(card.getMediaLocator()).hasText("Aurora media");
        assertThat(card.getContentLocator())
                .containsText("Lapland is the northern-most region of Finland");
        assertThat(card.getContentLocator())
                .containsText("indigenous Sámi people");
    }

    @Test
    public void footerActionsUpdateStatus() {
        CardElement card = CardElement.getByTitle(page, "Booking Card");

        card.assertVisible();
        card.assertTheme("elevated");
        card.assertCssClass("action-card");

        assertThat(card.getContentLocator()).containsText("Awaiting action");

        ButtonElement bookButton = ButtonElement.getByText(card.getLocator(), "Book now");
        bookButton.click();
        assertThat(page.locator("#card-action-status")).hasText("Booked");

        ButtonElement resetButton = ButtonElement.getByText(card.getLocator(), "Reset");
        resetButton.click();
        assertThat(page.locator("#card-action-status")).hasText("Awaiting action");

        ButtonElement footerBookButton = new ButtonElement(
                card.getFooterLocator().filter(
                        new Locator.FilterOptions().setHasText("Book now")
                )
        );
        footerBookButton.click();
        assertThat(page.locator("#card-action-status")).hasText("Booked");
    }
}
