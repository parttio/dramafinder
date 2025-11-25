package org.vaadin.addons.dramafinder.element.utils;

import java.text.DecimalFormat;

public class NumberUtils {

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
