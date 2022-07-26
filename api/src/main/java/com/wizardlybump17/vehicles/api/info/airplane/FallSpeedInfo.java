package com.wizardlybump17.vehicles.api.info.airplane;

import com.wizardlybump17.wlib.util.MapUtils;
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
        return MapUtils.mapOf(
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
                ((Number) map.getOrDefault("smooth-speed", 1.01)).doubleValue(),
                ((Number) map.getOrDefault("smooth-pitch", 1.5f)).floatValue(),
                ((Number) map.getOrDefault("pitch", 45f)).floatValue(),
                ((Number) map.getOrDefault("max-speed", 3f)).doubleValue()
        );
    }

    public static FallSpeedInfo defaultInfo() {
        return new FallSpeedInfo(1.01, 1.01, 1.5f, 45, 3f);
    }
}
