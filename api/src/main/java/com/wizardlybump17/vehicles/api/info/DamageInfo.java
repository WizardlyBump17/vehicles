package com.wizardlybump17.vehicles.api.info;

import com.wizardlybump17.vehicles.util.MapUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.TreeMap;

@Data
@AllArgsConstructor
@SerializableAs("damage-info")
public class DamageInfo implements ConfigurationSerializable {

    private Map<Double, Double> damage;
    private long delay;

    public double getDamage(double key) {
        return MapUtil.getValue(damage, key);
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        return Map.of(
                "damage", damage,
                "delay", delay
        );
    }

    @SuppressWarnings("unchecked")
    public static DamageInfo deserialize(Map<String, Object> map) {
        return new DamageInfo(
                new TreeMap<>((Map<Double, Double>) map.getOrDefault("damage", Map.of(
                        0.5, 10.0,
                        1.0, 20.0,
                        1.5, 30.0,
                        2.0, 40.0,
                        2.5, 50.0,
                        3.0, 60.0
                ))),
                ((Number) map.getOrDefault("delay", 1000L)).longValue()
        );
    }

    public static DamageInfo defaultInfo() {
        return new DamageInfo(
                new TreeMap<>(Map.of(
                        0.5, 10.0,
                        1.0, 20.0,
                        1.5, 30.0,
                        2.0, 40.0,
                        2.5, 50.0,
                        3.0, 60.0
                )),
                1000L
        );
    }
}
