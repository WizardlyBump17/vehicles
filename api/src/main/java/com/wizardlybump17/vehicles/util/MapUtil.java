package com.wizardlybump17.vehicles.util;

import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class MapUtil {

    public static double getValue(Map<Double, Double> map, double current) {
        current = NumberUtil.precision(current, 4);

        if (map.containsKey(current))
            return map.get(current);

        if (map.isEmpty())
            return 0;

        double previous = map.keySet().iterator().next();
        for (Map.Entry<Double, Double> entry : map.entrySet()) {
            if (entry.getKey() > current)
                return entry.getValue();
            previous = entry.getKey();
        }

        return map.get(previous);
    }
}
