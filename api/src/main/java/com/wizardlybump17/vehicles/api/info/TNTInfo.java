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

    private String name;
    private int fuseTicks;
    private Vector direction;
    private long delay;
    private float power;
    private boolean breakBlocks;
    private boolean setFire;
    private Vector rotation;

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        return Map.of(
                "name", name,
                "fuse-ticks", fuseTicks,
                "direction", direction,
                "delay", delay,
                "power", power,
                "break-blocks", breakBlocks,
                "set-fire", setFire,
                "rotation", rotation
        );
    }

    public static TNTInfo deserialize(Map<String, Object> map) {
        return new TNTInfo(
                (String) map.getOrDefault("name", "tnt"),
                (int) map.getOrDefault("fuse-ticks", 80),
                (Vector) map.getOrDefault("direction", new Vector(0, 0, 0)),
                ((Number) map.getOrDefault("delay", 0L)).longValue(),
                ((Number) map.getOrDefault("power", 3)).floatValue(),
                (boolean) map.getOrDefault("break-blocks", false),
                (boolean) map.getOrDefault("set-fire", false),
                (Vector) map.getOrDefault("rotation", new Vector(0, 0, 0))
        );
    }

    public static TNTInfo defaultInfo() {
        return new TNTInfo("tnt", 80, new Vector(0, 0, 0), 0, 3, false, false, new Vector(0, 0, 0));
    }
}
