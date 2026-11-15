package org.vaadin.addons.dramafinder.tests.it;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.addons.dramafinder.element.VaadinElement;
import org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Light-DOM children and descendants never enter a shadow root (issue #272). */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class LightDomIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "light-dom";
    }

    private VaadinElement container() {
        return new VaadinElement(page.locator("#light-dom-container")) {
        };
    }

    @Test
    public void childrenAreTheDirectLightDomChildrenWithTheTag() {
        assertThat(ButtonElement.getByText(page, "Direct").getLocator()).isVisible();

        List<VaadinElement> buttons = container().getLightDomChildren("vaadin-button");

        assertEquals(1, buttons.size());
        assertThat(buttons.get(0).getLocator()).hasText("Direct");
    }

    @Test
    public void anyTagMatchesEveryChildElement() {
        assertThat(ButtonElement.getByText(page, "Direct").getLocator()).isVisible();

        List<VaadinElement> children = container().getLightDomChildren(VaadinElement.ANY_TAG);

        assertEquals(3, children.size());
        assertThat(children.get(0).getLocator()).hasText("Direct");
        assertThat(children.get(1).getLocator()).hasAttribute("id", "light-dom-nested");
        assertThat(children.get(2).getLocator()).hasJSProperty("localName", "vaadin-text-field");
    }

    @Test
    public void descendantsIncludeNestedLightDomInDocumentOrder() {
        assertThat(ButtonElement.getByText(page, "Nested").getLocator()).isVisible();

        List<VaadinElement> buttons = container().getLightDomDescendants("VAADIN-BUTTON");

        assertEquals(2, buttons.size());
        assertThat(buttons.get(0).getLocator()).hasText("Direct");
        assertThat(buttons.get(1).getLocator()).hasText("Nested");
    }

    @Test
    public void descendantsDoNotEnterShadowRoots() {
        // a CSS lookup pierces the text field's shadow root ...
        assertThat(page.locator("#light-dom-container vaadin-input-container")).hasCount(1);
        // ... the light-DOM lookup does not
        assertTrue(container().getLightDomDescendants("vaadin-input-container").isEmpty());

        // a button's own shadow parts are not its light-DOM descendants
        VaadinElement direct = container().getLightDomChildren("vaadin-button").get(0);
        assertTrue(direct.getLightDomDescendants(VaadinElement.ANY_TAG).isEmpty());
    }

    @Test
    public void rejectsWhatIsNotATagName() {
        VaadinElement container = container();
        assertThrows(IllegalArgumentException.class, () -> container.getLightDomChildren(null));
        assertThrows(IllegalArgumentException.class, () -> container.getLightDomChildren("div[@id]"));
        assertThrows(IllegalArgumentException.class, () -> container.getLightDomDescendants("a b"));
    }
}
