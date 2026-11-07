package org.vaadin.addons.dramafinder.tests.testuis.navigation;

import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

/** {@code nav/article/<id>}: a {@code HasUrlParameter} route. */
@Route(value = "article", layout = NavigationLayout.class)
public class ArticleView extends Main implements HasUrlParameter<String> {

    private final Span article = new Span();

    public ArticleView() {
        article.setId("article");
        add(article);
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        article.setText("Article " + parameter);
    }
}
