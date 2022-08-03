package com.wizardlybump17.vehicles.api.model;

import com.wizardlybump17.vehicles.api.Vehicles;
import com.wizardlybump17.vehicles.api.info.DamageInfo;
import com.wizardlybump17.vehicles.api.info.LockInfo;
import com.wizardlybump17.vehicles.api.info.SpeedInfo;
import com.wizardlybump17.vehicles.api.vehicle.Automobile;
import lombok.NonNull;

public abstract class AutomobileModel<A extends Automobile<?>> extends VehicleModel<A> {

    protected AutomobileModel(
            Vehicles plugin,
            String name,
            SpeedInfo speed,
            DamageInfo damage,
            @NonNull String megModel,
            float rotationSpeed,
            float jumpHeight,
            int floatingPrecision,
            LockInfo info) {
        super(plugin, name, speed, damage, megModel, rotationSpeed, jumpHeight, floatingPrecision, info);
    }
}
