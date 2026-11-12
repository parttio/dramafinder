package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("MenuBar Demo")
@Route(value = "menubar", layout = MainLayout.class)
public class MenuBarView extends Main {

    private final Text selected = new Text("");
    private final ComponentEventListener<ClickEvent<MenuItem>> listener = e -> selected
            .setText(e.getSource().getText().isEmpty()
                    ? e.getSource().getAriaLabel().orElse("")
                    : e.getSource().getText());

    public MenuBarView() {
        Div message = new Div(new Text("Clicked item: "), selected);
        message.setId("text");

        add(createMainMenuBar(), createOtherMenuBar(), message);
    }

    private MenuBar createMainMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.setId("main-menu-bar");
        menuBar.addClassName("custom-menu-bar");
        menuBar.addThemeVariants(MenuBarVariant.LUMO_CONTRAST);

        menuBar.addItem("View", listener);
        menuBar.addItem("Edit", listener);

        MenuItem share = menuBar.addItem("Share");
        SubMenu shareSubMenu = share.getSubMenu();
        MenuItem onSocialMedia = shareSubMenu.addItem("On social media");
        SubMenu socialMediaSubMenu = onSocialMedia.getSubMenu();
        socialMediaSubMenu.addItem("Facebook", listener);
        socialMediaSubMenu.addItem("Twitter", listener);
        socialMediaSubMenu.addItem("Instagram", listener);
        shareSubMenu.addItem("By email", listener);
        shareSubMenu.addItem("Get Link", listener);

        // Two levels of sub-menus, a disabled item, and an item whose
        // aria-label differs from its text.
        MenuItem file = menuBar.addItem("File");
        SubMenu fileSubMenu = file.getSubMenu();
        fileSubMenu.addItem("New", listener);
        MenuItem export = fileSubMenu.addItem("Export");
        export.getSubMenu().addItem("PDF", listener);
        export.getSubMenu().addItem("CSV", listener);
        MenuItem print = fileSubMenu.addItem("Print", listener);
        print.setEnabled(false);
        MenuItem close = fileSubMenu.addItem("Close", listener);
        close.setAriaLabel("Close the document");

        MenuItem archived = menuBar.addItem("Archive", listener);
        archived.setEnabled(false);

        // Icon-only item: no text, named by its aria-label.
        MenuItem more = menuBar.addItem(VaadinIcon.ELLIPSIS_DOTS_V.create());
        more.setAriaLabel("More actions");
        more.getSubMenu().addItem("Settings", listener);
        return menuBar;
    }

    // A second menu bar with the same item labels: lookups must stay scoped.
    private MenuBar createOtherMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.setId("other-menu-bar");
        MenuItem file = menuBar.addItem("File");
        MenuItem export = file.getSubMenu().addItem("Export");
        export.getSubMenu().addItem("PDF", e -> selected.setText("Other PDF"));
        return menuBar;
    }
}
