package com.wizardlybump17.vehicles.api.model;

import com.wizardlybump17.vehicles.api.Vehicles;
import com.wizardlybump17.vehicles.api.vehicle.Automobile;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public abstract class AutomobileModel<A extends Automobile<?>> extends VehicleModel<A> {

    private long speedTimeout;
    private long damageDelay;

    protected AutomobileModel(Vehicles plugin, String name, double maxSpeed, double smoothSpeed, @NonNull Map<Double, Double> acceleration, Map<Double, Double> damage, @NonNull Map<Double, Double> breakForce, @NonNull String megModel, float rotationSpeed, float jumpHeight, long speedTimeout, long damageDelay) {
        super(plugin, name, maxSpeed, smoothSpeed, acceleration, damage, breakForce, megModel, rotationSpeed, jumpHeight);
        this.speedTimeout = speedTimeout;
        this.damageDelay = damageDelay;
    }
}
