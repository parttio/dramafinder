package org.vaadin.addons.dramafinder.element;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Every public element offers {@code getById} and {@code getByTestId}, page-level
 * and scoped (issue #203), returning its own type.
 */
class IdFactoriesTest {

    /**
     * Wrappers whose tag is not the element Flow puts the id on: the notification
     * card (Flow sets it on {@code vaadin-notification}), the tooltip (no Flow
     * component), menu overlays, and the menu-bar button (Flow sets it on the
     * {@code vaadin-menu-bar-item} inside it).
     */
    private static final Set<String> NO_ID = Set.of(
            "NotificationElement", "TooltipElement", "MenuElement", "MenuItemElement", "ContextMenuElement");

    @Test
    void everyPublicElementHasIdAndTestIdFactories() throws Exception {
        List<String> missing = new ArrayList<>();
        int checked = 0;
        File dir = new File(VaadinElement.class.getResource("VaadinElement.class").toURI()).getParentFile();
        for (String file : dir.list()) {
            if (!file.endsWith("Element.class") || file.contains("$")) {
                continue;
            }
            Class<?> type = Class.forName(VaadinElement.class.getPackageName() + "."
                    + file.substring(0, file.length() - ".class".length()));
            if (!VaadinElement.class.isAssignableFrom(type) || !Modifier.isPublic(type.getModifiers())
                    || Modifier.isAbstract(type.getModifiers()) || NO_ID.contains(type.getSimpleName())) {
                continue;
            }
            for (String factory : List.of("getById", "getByTestId")) {
                for (Class<?> scope : List.of(Page.class, Locator.class)) {
                    try {
                        Method m = type.getMethod(factory, scope, String.class);
                        if (!Modifier.isStatic(m.getModifiers()) || m.getReturnType() != type) {
                            missing.add(type.getSimpleName() + "." + factory + "(" + scope.getSimpleName() + ")");
                        }
                    } catch (NoSuchMethodException e) {
                        missing.add(type.getSimpleName() + "." + factory + "(" + scope.getSimpleName() + ")");
                    }
                }
            }
            checked++;
        }
        assertTrue(missing.isEmpty(), "Missing id factories: " + missing);
        assertTrue(checked > 40, "only " + checked + " element classes checked");
    }
}
