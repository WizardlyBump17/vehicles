package com.wizardlybump17.vehicles.util;

import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class MapUtil {

    public static double getValue(Map<Double, Double> map, double current) {
        if (map.containsKey(current))
            return map.get(current);

        double previous = 0;
        for (Map.Entry<Double, Double> entry : map.entrySet()) {
            if (entry.getKey() > current)
                return entry.getValue();
            previous = entry.getKey();
        }
        return previous;
    }
}
