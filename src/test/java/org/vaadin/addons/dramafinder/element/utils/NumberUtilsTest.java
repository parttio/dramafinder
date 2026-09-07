package org.vaadin.addons.dramafinder.element.utils;

import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NumberUtilsTest {

    private final Locale defaultLocale = Locale.getDefault();
    private final Locale defaultFormatLocale = Locale
            .getDefault(Locale.Category.FORMAT);

    @AfterEach
    void restoreLocale() {
        Locale.setDefault(defaultLocale);
        Locale.setDefault(Locale.Category.FORMAT, defaultFormatLocale);
    }

    @Test
    void formatDouble_wholeValue_noFractionalPart() {
        assertEquals("5", NumberUtils.formatDouble(5d));
        assertEquals("0", NumberUtils.formatDouble(0d));
        assertEquals("-3", NumberUtils.formatDouble(-3d));
    }

    @Test
    void formatDouble_fractionalValue_dotSeparator() {
        assertEquals("0.5", NumberUtils.formatDouble(0.5));
        assertEquals("5.5", NumberUtils.formatDouble(5.5));
        assertEquals("-1.25", NumberUtils.formatDouble(-1.25));
    }

    @Test
    void formatDouble_commaSeparatorLocale_stillUsesDot() {
        Locale.setDefault(Locale.FRANCE);

        assertEquals("0.5", NumberUtils.formatDouble(0.5));
        assertEquals("5.5", NumberUtils.formatDouble(5.5));
    }

    @Test
    void formatDouble_commaSeparatorFormatCategory_stillUsesDot() {
        Locale.setDefault(Locale.Category.FORMAT, Locale.GERMANY);

        assertEquals("0.5", NumberUtils.formatDouble(0.5));
    }

    @Test
    void formatDouble_smallValue_noScientificNotation() {
        assertEquals("0.000001", NumberUtils.formatDouble(0.000001));
    }
}
