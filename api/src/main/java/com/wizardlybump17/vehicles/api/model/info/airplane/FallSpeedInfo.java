package com.wizardlybump17.vehicles.api.model.info.airplane;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@Data
@AllArgsConstructor
@SerializableAs("fall-speed-info")
public class FallSpeedInfo implements ConfigurationSerializable {

    private double speed;
    private double smoothSpeed;
    private float smoothPitch;
    private float pitch;
    private double maxSpeed;

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        return Map.of(
                "speed", speed,
                "smooth-speed", smoothSpeed,
                "smooth-pitch", smoothPitch,
                "pitch", pitch,
                "max-speed", maxSpeed
        );
    }

    public static FallSpeedInfo deserialize(Map<String, Object> map) {
        return new FallSpeedInfo(
                ((Number) map.getOrDefault("speed", 1.01)).doubleValue(),
                ((Number) map.getOrDefault("smooth-speed", 0.9)).doubleValue(),
                ((Number) map.getOrDefault("smooth-pitch", 1.5f)).floatValue(),
                ((Number) map.getOrDefault("pitch", 45f)).floatValue(),
                ((Number) map.getOrDefault("max-speed", 0d)).doubleValue()
        );
    }

    public static FallSpeedInfo defaultInfo() {
        return new FallSpeedInfo(1.01, 0.9, 1.5f, 45, 0);
    }
}
