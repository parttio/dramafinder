package org.vaadin.addons.dramafinder.tests.it;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.DynamicTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.addons.dramafinder.element.VaadinElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Every element with id and test-id factories finds the component the view
 * marked for it (issue #203). The elements are discovered by reflection, so a
 * new element with the factories is covered once the view holds its component.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class IdLookupIT extends SpringPlaywrightIT {

    private static final String PACKAGE = VaadinElement.class.getPackageName();
    private static final List<String> ELEMENTS = List.of(
            "AccordionElement", "AccordionPanelElement", "AppLayoutElement", "AvatarElement",
            "AvatarGroupElement", "BadgeElement", "BigDecimalFieldElement", "BreadcrumbsElement",
            "BreadcrumbsItemElement", "ButtonElement", "CardElement", "CheckboxElement",
            "CheckboxGroupElement", "ComboBoxElement", "ConfirmDialogElement", "CustomFieldElement",
            "DatePickerElement", "DateTimePickerElement", "DetailsElement", "DialogElement",
            "EmailFieldElement", "FormLayoutElement", "GridElement", "IntegerFieldElement",
            "ListBoxElement", "LoginFormElement", "LoginOverlayElement", "MarkdownElement",
            "MenuBarElement", "MessageInputElement", "MessageListElement",
            "MultiSelectComboBoxElement", "NumberFieldElement", "PasswordFieldElement",
            "PopoverElement", "ProgressBarElement", "RadioButtonGroupElement", "SelectElement",
            "SideNavigationElement", "SideNavigationItemElement", "SliderElement",
            "SplitLayoutElement", "SwitchElement", "TabElement", "TabSheetElement", "TabsElement",
            "TextAreaElement", "TextFieldElement", "TimePickerElement", "TreeGridElement",
            "UploadElement", "VirtualListElement");

    @Override
    public String getView() {
        return "id-lookup";
    }

    @TestFactory
    Stream<DynamicTest> everyElementFindsItsComponentByIdAndTestId() {
        return ELEMENTS.stream().map(name -> DynamicTest.dynamicTest(name, () -> {
            Class<?> type = Class.forName(PACKAGE + "." + name);
            Locator body = page.locator("body");
            assertFound(type, "getById", Page.class, page, name + "-id");
            assertFound(type, "getById", Locator.class, body, name + "-id");
            assertFound(type, "getByTestId", Page.class, page, name + "-tid");
            assertFound(type, "getByTestId", Locator.class, body, name + "-tid");
        }));
    }

    @Test
    public void everyElementWithTheFactoriesIsCovered() throws Exception {
        List<String> missing = new ArrayList<>();
        for (String name : ELEMENTS) {
            Class<?> type = Class.forName(PACKAGE + "." + name);
            for (String factory : List.of("getById", "getByTestId")) {
                for (Class<?> scope : List.of(Page.class, Locator.class)) {
                    Method m = type.getMethod(factory, scope, String.class);
                    if (!Modifier.isStatic(m.getModifiers()) || m.getReturnType() != type) {
                        missing.add(name + "." + factory + "(" + scope.getSimpleName() + ")");
                    }
                }
            }
        }
        assertTrue(missing.isEmpty(), "Wrong factories: " + missing);
    }

    @Test
    public void idMustMatchInFull() {
        assertThat(ButtonElement.getById(page, "ButtonElement").getLocator()).hasCount(0);
        assertThat(ButtonElement.getByTestId(page, "ButtonElement").getLocator()).hasCount(0);
    }

    @Test
    public void idLookupIsScopedToTheElementTag() {
        // the text field's id does not resolve through the button factory
        assertThat(ButtonElement.getById(page, "TextFieldElement-id").getLocator()).hasCount(0);
    }

    @Test
    public void menuItemIdIsNotOnTheMenuBarButton() {
        // Flow puts a MenuItem's id on the vaadin-menu-bar-item inside the button,
        // which is why MenuItemElement has no id factories.
        assertThat(page.locator("vaadin-menu-bar-button[id='MenuItemElement-id']")).hasCount(0);
        assertThat(page.locator("[id='MenuItemElement-id']")).hasCount(1);
    }

    private static void assertFound(Class<?> type, String factory, Class<?> scopeType, Object scope,
            String value) throws Exception {
        VaadinElement element = (VaadinElement) type.getMethod(factory, scopeType, String.class)
                .invoke(null, scope, value);
        assertThat(element.getLocator()).hasCount(1);
    }
}
