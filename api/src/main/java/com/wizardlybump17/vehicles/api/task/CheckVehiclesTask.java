package com.wizardlybump17.vehicles.api.task;

import com.wizardlybump17.vehicles.api.cache.VehicleCache;
import com.wizardlybump17.vehicles.api.vehicle.Vehicle;
import com.wizardlybump17.vehicles.util.SafeBukkitRunnable;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CheckVehiclesTask extends SafeBukkitRunnable {

    private final VehicleCache cache;

    @Override
    public void run() {
        for (Vehicle<?> vehicle : cache.getAll())
            vehicle.check();
    }
}
