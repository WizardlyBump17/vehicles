package com.wizardlybump17.vehicles.api.info;

import com.wizardlybump17.wlib.util.MapUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@SerializableAs("lock")
public class LockInfo implements ConfigurationSerializable {

    private Map<LockType, Boolean> locks;

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        return Map.of("locks", MapUtils.mapKeys(locks, LockType::name));
    }

    public void setCanLock(LockType type, boolean locked) {
        locks.put(type, locked);
    }

    public boolean canLock(LockType type) {
        return locks.getOrDefault(type, false);
    }

    @SuppressWarnings("unchecked")
    public static LockInfo deserialize(Map<String, Object> map) {
        return new LockInfo(MapUtils.mapKeys((Map<String, Boolean>) map.getOrDefault("locks", new HashMap<>()), s -> LockType.valueOf(s.toUpperCase())));
    }

    public static LockInfo defaultInfo() {
        return new LockInfo(new EnumMap<>(LockInfo.LockType.class));
    }

    public enum LockType {
        ROTATION,
        PITCH,
        SPEED
    }
}
