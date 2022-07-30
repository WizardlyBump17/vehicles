package com.wizardlybump17.vehicles.api;

import com.wizardlybump17.vehicles.api.cache.VehicleCache;
import com.wizardlybump17.vehicles.api.cache.VehicleModelCache;
import com.wizardlybump17.vehicles.api.task.CheckVehiclesTask;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public abstract class Vehicles extends JavaPlugin {

    private final VehicleCache vehicleCache = new VehicleCache();
    private final VehicleModelCache vehicleModelCache = new VehicleModelCache();
    private final CheckVehiclesTask checkVehiclesTask = new CheckVehiclesTask(vehicleCache);

    public static Vehicles getInstance() {
        return getPlugin(Vehicles.class);
    }
}
