package org.vaadin.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

@PlaywrightElement("H2")
public class H2Element extends VaadinElement {

	public H2Element(Locator locator) {
		super(locator);
	}
}
