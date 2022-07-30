package com.wizardlybump17.vehicles.api.model;

import com.wizardlybump17.vehicles.api.Vehicles;
import com.wizardlybump17.vehicles.api.vehicle.Automobile;
import lombok.NonNull;

import java.util.Map;

public abstract class AutomobileModel<A extends Automobile<?>> extends VehicleModel<A> {

    protected AutomobileModel(Vehicles plugin, String name, double maxSpeed, @NonNull Map<Double, Double> acceleration, Map<Double, Double> damage, @NonNull Map<Double, Double> breakForce, @NonNull String megModel, float rotationSpeed, float jumpHeight) {
        super(plugin, name, maxSpeed, acceleration, damage, breakForce, megModel, rotationSpeed, jumpHeight);
    }
}