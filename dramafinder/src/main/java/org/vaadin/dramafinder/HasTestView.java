package org.vaadin.dramafinder;

public interface HasTestView {

    String getUrl();

    default String getView() {
        return "";
    }
}