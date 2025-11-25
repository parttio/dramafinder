package org.vaadin.addons.dramafinder;

public interface HasTestView {

    String getUrl();

    default String getView() {
        return "";
    }
}