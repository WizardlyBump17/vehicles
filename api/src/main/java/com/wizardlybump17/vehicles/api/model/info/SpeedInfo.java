package com.wizardlybump17.vehicles.api.model.info;

import com.wizardlybump17.vehicles.util.MapUtil;
import com.wizardlybump17.wlib.util.MapUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.TreeMap;

@Data
@AllArgsConstructor
@SerializableAs("speed-info")
public class SpeedInfo implements ConfigurationSerializable {

    private double min;
    private double max;
    private double smooth;
    private Map<Double, Double> acceleration;
    private Map<Double, Double> breakForce;

    public double getAcceleration(double speed) {
        return MapUtil.getValue(acceleration, speed);
    }

    public double getBreakForce(double speed) {
        return MapUtil.getValue(breakForce, speed);
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        return Map.of(
                "min", min,
                "max", max,
                "smooth", smooth,
                "acceleration", acceleration,
                "break-force", breakForce
        );
    }

    @SuppressWarnings("unchecked")
    public static SpeedInfo deserialize(Map<String, Object> map) {
        return new SpeedInfo(
                ((Number) map.getOrDefault("min", 0d)).doubleValue(),
                ((Number) map.getOrDefault("max", 3d)).doubleValue(),
                ((Number) map.getOrDefault("smooth", 0.9)).doubleValue(),
                new TreeMap<>((Map<Double, Double>) map.getOrDefault(
                        "acceleration",
                        MapUtils.mapOf(
                                0.5, 0.02,
                                1.0, 0.04,
                                1.5, 0.06,
                                2.0, 0.08,
                                2.5, 0.1
                        )
                )),
                new TreeMap<>((Map<Double, Double>) map.getOrDefault(
                        "break-force",
                        MapUtils.mapOf(
                                0.5, 2.0,
                                1.0, 4.0,
                                1.5, 6.0,
                                2.0, 8.0,
                                2.5, 10.0
                        )
                ))
        );
    }

    public static SpeedInfo defaultInfo() {
        return new SpeedInfo(
                0,
                3,
                0.9,
                new TreeMap<>(MapUtils.mapOf(
                        0.5, 0.02,
                        1.0, 0.04,
                        1.5, 0.06,
                        2.0, 0.08,
                        2.5, 0.1
                )),
                new TreeMap<>(MapUtils.mapOf(
                        0.5, 2.0,
                        1.0, 4.0,
                        1.5, 6.0,
                        2.0, 8.0,
                        2.5, 10.0
                ))
        );
    }
}
