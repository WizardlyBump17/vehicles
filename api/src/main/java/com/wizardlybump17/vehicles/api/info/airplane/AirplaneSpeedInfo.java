package com.wizardlybump17.vehicles.api.info.airplane;

import com.wizardlybump17.vehicles.api.info.SpeedInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.TreeMap;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@SerializableAs("airplane-speed")
public class AirplaneSpeedInfo extends SpeedInfo {

    private double minFlySpeed;
    private double maxFlySpeed;

    public AirplaneSpeedInfo(double min, double max, double smooth, Map<Double, Double> acceleration, Map<Double, Double> breakForce, Map<Double, Double> breakAcceleration, double minFlySpeed, double maxFlySpeed) {
        super(min, max, smooth, acceleration, breakForce, breakAcceleration);
        this.minFlySpeed = minFlySpeed;
        this.maxFlySpeed = maxFlySpeed;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        map.put("fly-speed", Map.of(
                "min", minFlySpeed,
                "max", maxFlySpeed
        ));
        return map;
    }

    @SuppressWarnings("unchecked")
    public static AirplaneSpeedInfo deserialize(Map<String, Object> map) {
        Map<String, Object> flySpeed = (Map<String, Object>) map.getOrDefault("fly-speed", Map.of(
                "min", 0,
                "max", 3
        ));
        return new AirplaneSpeedInfo(
                ((Number) map.getOrDefault("min", 0d)).doubleValue(),
                ((Number) map.getOrDefault("max", 3d)).doubleValue(),
                ((Number) map.getOrDefault("smooth", 0.9)).doubleValue(),
                new TreeMap<>((Map<Double, Double>) map.getOrDefault(
                        "acceleration",
                        Map.of(
                                0.5, 0.02,
                                1.0, 0.04,
                                1.5, 0.06,
                                2.0, 0.08,
                                2.5, 0.1
                        )
                )),
                new TreeMap<>((Map<Double, Double>) map.getOrDefault(
                        "break-force",
                        Map.of(
                                0.5, 2.0,
                                1.0, 4.0,
                                1.5, 6.0,
                                2.0, 8.0,
                                2.5, 10.0
                        )
                )),
                new TreeMap<>((Map<Double, Double>) map.getOrDefault(
                        "break-acceleration",
                        Map.of(
                                3.0, 0.02,
                                3.1, 0.0
                        )
                )),
                ((Number) flySpeed.getOrDefault("min", 0d)).doubleValue(),
                ((Number) flySpeed.getOrDefault("max", 3d)).doubleValue()
        );
    }

    public static AirplaneSpeedInfo defaultInfo() {
        return new AirplaneSpeedInfo(
                0,
                3,
                0.9,
                new TreeMap<>(Map.of(
                        0.5, 0.02,
                        1.0, 0.04,
                        1.5, 0.06,
                        2.0, 0.08,
                        2.5, 0.1
                )),
                new TreeMap<>(Map.of(
                        0.5, 2.0,
                        1.0, 4.0,
                        1.5, 6.0,
                        2.0, 8.0,
                        2.5, 10.0
                )),
                new TreeMap<>(Map.of(
                        3.0, 0.02,
                        3.1, 0.0
                )),
                0,
                3
        );
    }
}
