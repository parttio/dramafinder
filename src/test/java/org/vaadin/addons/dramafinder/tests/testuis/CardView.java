package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Card Demo")
@Route(value = "card", layout = MainLayout.class)
public class CardView extends Main {

    public CardView() {
        add(createDestinationCard(), createActionCard());
    }

    private Card createDestinationCard() {
        Card card = new Card();
        card.addClassName("lapland-card");
        card.addThemeName("outlined");

        card.setTitle(new Div("Lapland"));
        card.setSubtitle(new Div("The Exotic North"));

        Span headerPrefix = new Span("Lapland prefix");
        headerPrefix.addClassName("card-header-prefix");
        card.setHeaderPrefix(headerPrefix);

        Span headerSuffix = new Span("Arctic");
        headerSuffix.getElement().getThemeList().add("badge success");
        card.setHeaderSuffix(headerSuffix);

        Span media = new Span("Aurora media");
        media.addClassName("card-media");
        card.setMedia(media);

        card.add(new Paragraph("Lapland is the northern-most region of Finland and an active outdoor destination."));
        card.add(new Paragraph("The land of the indigenous Sámi people, known as Sámi homeland or Sápmi, also crosses the northern part of the region."));

        return card;
    }

    private Card createActionCard() {
        Card card = new Card();
        card.addClassName("action-card");
        card.addThemeName("elevated");

        card.setTitle(new Div("Booking Card"));
        card.setSubtitle(new Div("Footer actions stay anchored"));

        card.add(new Paragraph("Confirm your booking to lock the seats."));

        Span status = new Span("Awaiting action");
        status.setId("card-action-status");
        card.add(status);

        Button bookButton = new Button("Book now", event -> status.setText("Booked"));
        Button resetButton = new Button("Reset", event -> status.setText("Awaiting action"));
        card.addToFooter(bookButton, resetButton);

        return card;
    }
}
