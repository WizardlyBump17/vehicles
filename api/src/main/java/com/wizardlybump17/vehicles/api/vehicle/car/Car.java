package com.wizardlybump17.vehicles.api.vehicle.car;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.wizardlybump17.vehicles.api.model.car.CarModel;
import com.wizardlybump17.vehicles.api.vehicle.Automobile;

public class Car extends Automobile<CarModel> {

    public Car(CarModel model, String plate, ActiveModel megModel) {
        super(model, plate, megModel);
    }
}
