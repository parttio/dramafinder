package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Tooltip Demo")
@Route(value = "tooltip", layout = MainLayout.class)
public class TooltipView extends Main {

    public TooltipView() {
        Button hover = new Button("Hover me");
        hover.setId("tooltip-hover-button");
        hover.setTooltipText("Tooltip for the hover button");

        // No hover delay, so the tooltip opens as soon as the pointer arrives.
        Button instant = new Button("Instant");
        instant.setId("tooltip-instant-button");
        instant.setTooltipText("Opens without delay").setHoverDelay(0);

        Button positioned = new Button("Positioned");
        positioned.setId("tooltip-positioned-button");
        positioned.setTooltipText("Shown above the button")
                .setPosition(Tooltip.TooltipPosition.TOP_START);

        // A tooltip with no text keeps its overlay empty and hidden.
        Button empty = new Button("No tooltip text");
        empty.setId("tooltip-empty-button");
        empty.setTooltipText("");

        Button manual = new Button("Manual");
        manual.setId("tooltip-manual-button");
        Tooltip manualTooltip = manual
                .setTooltipText("Opened programmatically")
                .withManual(true);
        Button toggle = new Button("Toggle manual tooltip",
                event -> manualTooltip.setOpened(!manualTooltip.isOpened()));
        toggle.setId("tooltip-toggle-button");

        // Tooltip has no Flow API for theme or class names, so they are set on
        // the slotted <vaadin-tooltip> element directly.
        TextField styled = new TextField("Styled");
        styled.setId("tooltip-styled-field");
        styled.setTooltipText("Tooltip with theme and class");
        Element styledTooltip = tooltipElementOf(styled);
        styledTooltip.setAttribute("theme", "custom-tooltip-theme");
        styledTooltip.getClassList().add("custom-tooltip");

        TextField dynamic = new TextField("Dynamic");
        dynamic.setId("tooltip-dynamic-field");
        Tooltip dynamicTooltip = dynamic.setTooltipText("Initial tooltip");
        Button update = new Button("Update tooltip",
                event -> dynamicTooltip.setText("Updated tooltip"));
        update.setId("tooltip-update-button");

        add(hover, instant, positioned, empty, manual, toggle, styled, dynamic,
                update);
    }

    /**
     * The {@code <vaadin-tooltip>} Flow slots into a component. {@link Tooltip}
     * exposes no element accessor, so it is looked up among the component's
     * children.
     */
    private static Element tooltipElementOf(Component component) {
        return component.getElement().getChildren()
                .filter(child -> "vaadin-tooltip".equals(child.getTag()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "The component has no tooltip"));
    }
}
