package org.vaadin.dramafinder.element;

import java.util.Map;

import com.microsoft.playwright.Locator;
import org.vaadin.dramafinder.element.common.HasLocatorElement;

public abstract class VaadinElement implements HasLocatorElement {
    protected final Locator locator;

	public VaadinElement(Locator locator) {
        this.locator = locator;
    }

    public void click() {
        locator.click();
    }

    public String getText() {
        return locator.textContent();
    }

    @Override
    public Locator getLocator() {
        return locator;
    }

	/** Set a DOM property (e.g. `value`, `disabled`, etc.) */
	public void setProperty(String name, Object value) {
		locator.evaluate("(el, args) => el[args.name] = args.value", Map.of("name", name, "value", value));
	}

	/** Optional: get a DOM property */
	public Object getProperty(String name) {
		return locator.evaluate("(el, args) => el[args.name]", Map.of("name", name));
	}

	public boolean isVisible() {
		return locator.isVisible();
	}

	public boolean isHidden() {
		return !isVisible();
	}

	public boolean isEnabled() {
		String disabled = getDomAttribute("disabled");
		return disabled == null || disabled.equals("false");
	}

	public boolean isDisabled() {
		return !isEnabled();
	}

	public String getDomAttribute(String name) {
		return locator.getAttribute(name);
	}
	public void focus() {
		locator.focus();
	}

	public void blur() {
		locator.blur();
	}

}
