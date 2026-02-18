package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Avatar Demo")
@Route(value = "avatar", layout = MainLayout.class)
public class AvatarView extends Main {

    public AvatarView() {
        Avatar withName = new Avatar("Jane Smith");
        withName.setId("avatar-with-name");

        Avatar withAbbr = new Avatar();
        withAbbr.setId("avatar-with-abbr");
        withAbbr.setAbbreviation("AB");

        Avatar withImage = new Avatar();
        withImage.setId("avatar-with-image");
        withImage.setImage("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='1' height='1'%3E%3C/svg%3E");

        Avatar noImage = new Avatar();
        noImage.setId("avatar-no-image");

        Avatar colorIndex = new Avatar();
        colorIndex.setId("avatar-color-index");
        colorIndex.setColorIndex(3);

        Avatar withTheme = new Avatar();
        withTheme.setId("avatar-with-theme");
        withTheme.getElement().getThemeList().add("small");

        Avatar withTooltip = new Avatar();
        withTooltip.setId("avatar-with-tooltip");
        Element tooltip = new Element("vaadin-tooltip");
        tooltip.setAttribute("slot", "tooltip");
        tooltip.setProperty("text", "Tooltip");
        withTooltip.getElement().appendChild(tooltip);

        Avatar withClass = new Avatar();
        withClass.setId("avatar-with-class");
        withClass.addClassName("custom-avatar");

        Avatar focusable = new Avatar();
        focusable.setId("avatar-focusable");
        focusable.getElement().setAttribute("tabindex", "0");

        Div container = new Div();
        container.setId("avatar-container");
        container.add(withName);

        add(container, withAbbr, withImage, noImage, colorIndex, withTheme,
                withTooltip, withClass, focusable);
    }
}
