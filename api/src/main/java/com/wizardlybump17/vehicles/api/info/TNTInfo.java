package com.wizardlybump17.vehicles.api.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@Data
@AllArgsConstructor
@SerializableAs("tnt-info")
public class TNTInfo implements ConfigurationSerializable {

    private int fuseTicks;
    private Vector direction;
    private boolean useDriverRotation;
    private float minPitch;
    private float maxPitch;
    private long delay;
    private float power;
    private boolean breakBlocks;
    private boolean setFire;

    public float fixPitch(float pitch) {
        if (pitch < minPitch)
            return minPitch;
        return Math.min(pitch, maxPitch);
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        return Map.of(
                "fuse-ticks", fuseTicks,
                "direction", direction,
                "use-driver-rotation", useDriverRotation,
                "min-pitch", minPitch,
                "max-pitch", maxPitch,
                "delay", delay,
                "power", power,
                "break-blocks", breakBlocks,
                "set-fire", setFire
        );
    }

    public static TNTInfo deserialize(Map<String, Object> map) {
        return new TNTInfo(
                (int) map.getOrDefault("fuse-ticks", 80),
                (Vector) map.getOrDefault("direction", new Vector(0, 0, 0)),
                (boolean) map.getOrDefault("use-driver-rotation", false),
                ((Number) map.getOrDefault("min-pitch", 45f)).floatValue(),
                ((Number) map.getOrDefault("max-pitch", -45f)).floatValue(),
                ((Number) map.getOrDefault("delay", 0L)).longValue(),
                ((Number) map.getOrDefault("power", 3)).floatValue(),
                (boolean) map.getOrDefault("break-blocks", false),
                (boolean) map.getOrDefault("set-fire", false)
        );
    }

    public static TNTInfo defaultInfo() {
        return new TNTInfo(80, new Vector(0, 0, 0), false, 45, -45, 0, 3, false, false);
    }
}
