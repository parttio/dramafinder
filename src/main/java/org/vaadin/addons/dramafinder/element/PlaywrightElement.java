package org.vaadin.addons.dramafinder.element;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface PlaywrightElement {

    /**
     * The tag for the element, or a CSS selector narrowing it (e.g.
     * {@code vaadin-notification-card[slot]} for open notifications only).
     * The typed query searches for it.
     *
     * @return the tag or selector for the element
     */
    String value();

}
