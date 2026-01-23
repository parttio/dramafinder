package org.vaadin.addons.dramafinder.element.utils;

import java.text.DecimalFormat;

/**
 * Utility class for number formatting operations.
 */
public class NumberUtils {

    /**
     * Format a double value as a string, removing unnecessary decimal places.
     * <p>
     * If the value has no fractional part, returns it as a long string.
     * Otherwise, formats with up to 6 decimal places without scientific notation.
     *
     * @param value the double value to format
     * @return the formatted string representation
     */
    public static String formatDouble(double value) {
        // Check if the value has no fractional part
        if (value == Math.rint(value)) {
            return String.valueOf((long) value);
        } else {
            // Use DecimalFormat to avoid scientific notation
            DecimalFormat df = new DecimalFormat("0.######");
            return df.format(value);
        }
    }
}
