package com.wizardlybump17.vehicles.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class NumberUtil {

    public static float precision(float f, int decimals) {
        return (float) (Math.round(f * Math.pow(10, decimals)) / Math.pow(10, decimals));
    }

    public static double precision(double d, int decimals) {
        return Math.round(d * Math.pow(10, decimals)) / Math.pow(10, decimals);
    }

    public static boolean between(double n, double min, double max) {
        return n >= min && n <= max;
    }
}
