package org.vaadin.dramafinder.element;

import java.util.List;
import java.util.stream.Collectors;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;


public class ElementQuery<T extends VaadinElement> {
    private final Locator locator;
	private final Page page;
    private final Class<T> clazz;

    public ElementQuery(Locator locator, Page page, Class<T> clazz) {
        this.locator = locator;
		this.page = page;
        this.clazz = clazz;
    }

    public T first() {
        return createElement(locator.first());
    }

    public T get(int index) {
        return createElement(locator.nth(index));
    }

    public List<T> all() {
        return locator.all().stream()
            .map(this::createElement)
            .collect(Collectors.toList());
    }

    public int count() {
        return locator.count();
    }

    private T createElement(Locator loc) {
        try {
            return clazz.getConstructor(Locator.class, Page.class).newInstance(loc, page);
        } catch (Exception e) {
            throw new RuntimeException("Could not instantiate element: " + clazz.getName(), e);
        }
    }

	public ElementQuery<T> withLabel(String labelText) {
		Locator labeledLocator = locator.filter(new Locator.FilterOptions()
			.setHasText(labelText));
		return new ElementQuery<>(labeledLocator, page, clazz);
	}

	public T single() {
		int count = count();
		if (count == 0) {
			throw new AssertionError("Expected a single element, but found none.");
		} else if (count > 1) {
			throw new AssertionError("Expected a single element, but found " + count + ".");
		}
		return createElement(locator.first());
	}

	public ElementQuery<T> withText(String labelText) {
		Locator labeledLocator = locator.filter(new Locator.FilterOptions()
			.setHasText(labelText));
		return new ElementQuery<>(labeledLocator, page, clazz);
	}
}
