package com.wizardlybump17.vehicles.api.cache;

import com.wizardlybump17.vehicles.api.vehicle.Vehicle;
import com.wizardlybump17.wlib.object.Cache;
import com.wizardlybump17.wlib.object.Pair;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class VehicleCache extends Cache<Entity, Vehicle<?>, Vehicle<?>> {

    @Override
    public @NotNull Pair<Entity, Vehicle<?>> apply(Vehicle<?> vehicle) {
        return new Pair<>(vehicle.getEntity(), vehicle);
    }

    public Optional<Vehicle<?>> get(Player player, boolean checkPassengers) {
        if (checkPassengers) {
            for (Vehicle<?> vehicle : getAll())
                if (player.equals(vehicle.getDriver()) || vehicle.hasPassenger(player))
                    return Optional.of(vehicle);
            return Optional.empty();
        }

        for (Vehicle<?> vehicle : getAll())
            if (player.equals(vehicle.getDriver()))
                return Optional.of(vehicle);
        return Optional.empty();
    }

    @Override
    protected @NotNull Map<Entity, Vehicle<?>> getInitialMap() {
        return new ConcurrentHashMap<>();
    }
}
