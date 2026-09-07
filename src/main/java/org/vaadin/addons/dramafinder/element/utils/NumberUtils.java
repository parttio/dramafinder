package org.vaadin.addons.dramafinder.element.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class NumberUtils {

    public static String formatDouble(double value) {
        // Check if the value has no fractional part
        if (value == Math.rint(value)) {
            return String.valueOf((long) value);
        } else {
            // Use DecimalFormat to avoid scientific notation. Locale.ROOT keeps the
            // decimal separator a "." regardless of the JVM default locale, matching
            // what the browser reports for native input value/step/min/max.
            DecimalFormat df = new DecimalFormat("0.######",
                    DecimalFormatSymbols.getInstance(Locale.ROOT));
            return df.format(value);
        }
    }
}
