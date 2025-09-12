package org.vaadin.dramafinder.element;

import java.lang.reflect.Constructor;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class VaadinQuery {
    private final Page page;

    public VaadinQuery(Page page) {
        this.page = page;
    }

	public <T extends VaadinElement> ElementQuery<T> $(Class<T> clazz) {
		String tagName = getTagName(clazz);
		Locator locator = page.locator(tagName);
		return new ElementQuery<>(locator, page, clazz);
	}

	public <T extends VaadinElement> T $(Class<T> clazz, int index) {
		return createInstance(clazz, page.locator(getTagName(clazz)).nth(index), page);
	}


    public <T extends VaadinElement> T first(Class<T> clazz) {
        return $(clazz, 0);
    }

    private <T extends VaadinElement> T createInstance(Class<T> clazz, Locator locator, Page page) {
        try {
            Constructor<T> cons = clazz.getConstructor(Locator.class, Page.class);
            return cons.newInstance(locator, page);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance of " + clazz.getName(), e);
        }
    }

	private static String getTagName(Class<?> elementClass) {
		PlaywrightElement annotation = elementClass.getAnnotation(PlaywrightElement.class);
		if (annotation == null) {
			throw new IllegalStateException("The given element class "
				+ elementClass.getName() + " must be annotated using @"
				+ PlaywrightElement.class.getName());
		}
		return annotation.value();
	}
}
