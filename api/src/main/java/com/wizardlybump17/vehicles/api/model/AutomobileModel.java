package com.wizardlybump17.vehicles.api.model;

import com.wizardlybump17.vehicles.api.Vehicles;
import com.wizardlybump17.vehicles.api.model.info.SpeedInfo;
import com.wizardlybump17.vehicles.api.vehicle.Automobile;
import lombok.NonNull;

import java.util.Map;

public abstract class AutomobileModel<A extends Automobile<?>> extends VehicleModel<A> {

    protected AutomobileModel(
            Vehicles plugin,
            String name,
            SpeedInfo speed,
            Map<Double, Double> damage,
            long damageDelay,
            @NonNull String megModel,
            float rotationSpeed,
            float jumpHeight,
            int floatingPrecision) {
        super(plugin, name, speed, damage, damageDelay, megModel, rotationSpeed, jumpHeight, floatingPrecision);
    }
}
