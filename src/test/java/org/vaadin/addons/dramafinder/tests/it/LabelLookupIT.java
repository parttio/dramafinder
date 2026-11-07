package org.vaadin.addons.dramafinder.tests.it;

import java.util.stream.Stream;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.vaadin.addons.dramafinder.element.CheckboxGroupElement;
import org.vaadin.addons.dramafinder.element.CustomFieldElement;
import org.vaadin.addons.dramafinder.element.RadioButtonGroupElement;
import org.vaadin.addons.dramafinder.element.TextFieldElement;
import org.vaadin.addons.dramafinder.element.VaadinElement;
import org.vaadin.addons.dramafinder.element.query.ElementQueries;
import org.vaadin.addons.dramafinder.element.query.ElementQuery;
import org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT;
import org.vaadin.addons.dramafinder.tests.testuis.LabelLookupView;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * A label is the visible label: for every element that has one, {@code getByLabel}
 * and {@code withLabel} find a component by it and by nothing else. The
 * {@code aria-label} is reached through {@code withAccessibleName}, the
 * placeholder through {@code withPlaceholder}.
 */
public class LabelLookupIT extends SpringPlaywrightIT {

    private static final String PACKAGE = VaadinElement.class.getPackageName();

    @Override
    public String getView() {
        return "label-lookup";
    }

    @TestFactory
    Stream<DynamicTest> getByLabelMatchesTheVisibleLabelOnly() {
        return LabelLookupView.COMPONENTS.keySet().stream().map(name -> DynamicTest.dynamicTest(name, () -> {
            Class<?> type = Class.forName(PACKAGE + "." + name + "Element");
            Locator body = page.locator("body");

            String label = "label " + name;
            String aria = "aria " + name;
            assertResolvesTo(factory(type, "getByLabel", Page.class, page, label), name + "-label");
            assertResolvesTo(factory(type, "getByLabel", Locator.class, body, label), name + "-label");
            assertResolvesTo(query(type, "withLabel", label), name + "-label");

            // part of the label, in any case, through the Containing variants only
            String part = partOf(label);
            assertThat(factory(type, "getByLabel", Page.class, page, part)).hasCount(0);
            assertResolvesTo(factory(type, "getByLabelContaining", Page.class, page, part), name + "-label");
            assertResolvesTo(factory(type, "getByLabelContaining", Locator.class, body, part), name + "-label");
            assertResolvesTo(query(type, "withLabelContaining", part), name + "-label");

            // an aria-label is the accessible name, not a label
            if (page.locator("#" + name + "-aria").count() > 0) {
                assertThat(factory(type, "getByLabel", Page.class, page, aria)).hasCount(0);
                assertThat(factory(type, "getByLabelContaining", Page.class, page, aria)).hasCount(0);
                assertThat(query(type, "withLabel", aria)).hasCount(0);
                assertResolvesTo(query(type, "withAccessibleName", aria), name + "-aria");
                assertResolvesTo(query(type, "withAccessibleNameContaining", partOf(aria)), name + "-aria");
            }

            // nor is a placeholder (though an unlabelled select announces it as its name)
            if (page.locator("#" + name + "-placeholder").count() > 0) {
                String placeholder = "placeholder " + name;
                assertThat(factory(type, "getByLabel", Page.class, page, placeholder)).hasCount(0);
                assertThat(query(type, "withLabel", placeholder)).hasCount(0);
                assertResolvesTo(query(type, "withPlaceholder", placeholder), name + "-placeholder");
            }
        }));
    }

    @Test
    public void aContainerIsNotNamedByTheLabelOfWhatItHolds() {
        assertThat(CustomFieldElement.getByLabel(page, "CustomField inner").getLocator()).hasCount(0);
        assertThat(TextFieldElement.getByLabel(page, "CustomField inner").getLocator()).hasCount(1);
        assertThat(CheckboxGroupElement.getByLabel(page, "CheckboxGroup item").getLocator()).hasCount(0);
        assertThat(RadioButtonGroupElement.getByLabel(page, "RadioButtonGroup item").getLocator()).hasCount(0);
        assertThat(CustomFieldElement.getByLabelContaining(page, "inner").getLocator()).hasCount(0);
    }

    /** Lower-cased and missing its last letter: matches only through a substring match. */
    private static String partOf(String text) {
        return text.toLowerCase().substring(0, text.length() - 1);
    }

    private static void assertResolvesTo(Locator locator, String id) {
        assertThat(locator).hasCount(1);
        assertThat(locator).hasId(id);
    }

    private static Locator factory(Class<?> type, String name, Class<?> scopeType, Object scope, String value)
            throws Exception {
        return ((VaadinElement) type.getMethod(name, scopeType, String.class).invoke(null, scope, value))
                .getLocator();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Locator query(Class<?> type, String filter, String value) throws Exception {
        Object query = ElementQueries.find(page, (Class) type);
        return ((ElementQuery<?, ?>) query.getClass().getMethod(filter, String.class).invoke(query, value))
                .getLocator();
    }
}
