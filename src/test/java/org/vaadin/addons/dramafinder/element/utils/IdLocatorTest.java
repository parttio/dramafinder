package org.vaadin.addons.dramafinder.element.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IdLocatorTest {

    @Test
    void idSelectorQuotesAnyId() {
        assertEquals("[id=\"save\"]", IdLocator.idSelector("save"));
        // characters a #id selector would choke on
        assertEquals("[id=\"order:1.total\"]", IdLocator.idSelector("order:1.total"));
        assertEquals("[id=\"say \\\"hi\\\"\"]", IdLocator.idSelector("say \"hi\""));
    }
}
