package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.breadcrumbs.Breadcrumbs;
import com.vaadin.flow.component.breadcrumbs.BreadcrumbsItem;
import com.vaadin.flow.component.breadcrumbs.BreadcrumbsVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Demo view for {@code vaadin-breadcrumbs}.
 * <p>
 * The component is experimental, so it renders only with the
 * {@code com.vaadin.experimental.breadcrumbsComponent} feature flag enabled in
 * {@code src/main/resources/vaadin-featureflags.properties}.
 */
@PageTitle("Breadcrumbs Demo")
@Route(value = "breadcrumbs", layout = MainLayout.class)
public class BreadcrumbsView extends Main {

    public BreadcrumbsView() {
        add(new H2("Trail"), createTrail());
        add(new H2("Narrow trail"), createNarrowTrail());
    }

    /**
     * A trail that fits: a link with a prefix icon, a plain link, a disabled
     * link and the current page.
     */
    private Breadcrumbs createTrail() {
        Breadcrumbs breadcrumbs = new Breadcrumbs(Breadcrumbs.Mode.MANUAL);
        breadcrumbs.setAriaLabel("Main breadcrumbs");
        breadcrumbs.addClassName("main-breadcrumbs");
        breadcrumbs.addThemeVariants(BreadcrumbsVariant.SLASH);

        BreadcrumbsItem home = new BreadcrumbsItem("Home", "card");
        home.setPrefixComponent(VaadinIcon.HOME.create());

        BreadcrumbsItem movies = new BreadcrumbsItem("Movies", "grid-basic");

        BreadcrumbsItem archive = new BreadcrumbsItem("Archive", "grid-sorting");
        archive.setEnabled(false);

        // No path: the last item without a path is the current page.
        BreadcrumbsItem drama = new BreadcrumbsItem("Drama");

        breadcrumbs.add(home, movies, archive, drama);
        return breadcrumbs;
    }

    /**
     * A trail that is too narrow for its items, so all but the last item
     * collapse into the overflow overlay.
     */
    private Breadcrumbs createNarrowTrail() {
        Breadcrumbs breadcrumbs = new Breadcrumbs(Breadcrumbs.Mode.MANUAL);
        breadcrumbs.setAriaLabel("Narrow breadcrumbs");
        breadcrumbs.setWidth("220px");
        breadcrumbs.setI18n(new Breadcrumbs.BreadcrumbsI18n().setMoreItems("Show more items"));

        breadcrumbs.add(new BreadcrumbsItem("Continents", "card"),
                new BreadcrumbsItem("South America", "card"),
                new BreadcrumbsItem("Argentina", "card"),
                new BreadcrumbsItem("Buenos Aires", "card"),
                new BreadcrumbsItem("Palermo"));
        return breadcrumbs;
    }
}
