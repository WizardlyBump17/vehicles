package com.wizardlybump17.vehicles.api.task;

import com.wizardlybump17.vehicles.api.cache.VehicleCache;
import com.wizardlybump17.vehicles.api.vehicle.Vehicle;
import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class CheckVehiclesTask extends BukkitRunnable {

    private final VehicleCache cache;

    @Override
    public void run() {
        for (Vehicle<?> vehicle : cache.getAll())
            vehicle.check();
    }
}
