package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.avatar.AvatarGroup;
import com.vaadin.flow.component.avatar.AvatarGroup.AvatarGroupItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Avatar Group Demo")
@Route(value = "avatar-group", layout = MainLayout.class)
public class AvatarGroupView extends Main {

    public AvatarGroupView() {
        AvatarGroup basic = new AvatarGroup(
                new AvatarGroupItem("Jane Smith"),
                new AvatarGroupItem("John Doe"),
                new AvatarGroupItem("Alice Cooper"));
        basic.setId("avatar-group-basic");
        basic.setWidth("600px");

        // Five items with a maximum of three visible: the group keeps
        // maxItemsVisible - 1 avatars in view and moves the rest behind the
        // overflow avatar.
        AvatarGroupItem withAbbreviation = new AvatarGroupItem("Xavier Young");
        withAbbreviation.setAbbreviation("XY");
        AvatarGroupItem withColorIndex = new AvatarGroupItem("Erin Fisher");
        withColorIndex.setColorIndex(3);

        AvatarGroup overflow = new AvatarGroup(
                new AvatarGroupItem("Bob Ross"),
                new AvatarGroupItem("Carol Danvers"),
                new AvatarGroupItem("Dana Scully"),
                withColorIndex,
                withAbbreviation);
        overflow.setId("avatar-group-overflow");
        overflow.setMaxItemsVisible(3);
        overflow.setWidth("600px");

        AvatarGroup styled = new AvatarGroup(
                new AvatarGroupItem("Grace Hopper"),
                new AvatarGroupItem("Hedy Lamarr"));
        styled.setId("avatar-group-styled");
        styled.setWidth("600px");
        styled.addClassName("custom-avatar-group");
        styled.getElement().getThemeList().add("small");

        Div container = new Div();
        container.setId("avatar-group-container");
        container.add(basic);

        add(container, overflow, styled);
    }
}
