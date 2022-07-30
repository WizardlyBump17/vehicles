package com.wizardlybump17.vehicles.api.vehicle.motorcycle;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.wizardlybump17.vehicles.api.model.motorcycle.MotorcycleModel;
import com.wizardlybump17.vehicles.api.vehicle.Automobile;

public class Motorcycle extends Automobile<MotorcycleModel> {

    public Motorcycle(MotorcycleModel model, String plate, ActiveModel megModel) {
        super(model, plate, megModel);
    }
}
