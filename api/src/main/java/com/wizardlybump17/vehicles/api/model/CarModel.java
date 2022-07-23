package com.wizardlybump17.vehicles.api.model;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.mount.handler.IMountHandler;
import com.wizardlybump17.vehicles.api.Vehicles;
import com.wizardlybump17.vehicles.api.entity.VehicleEntity;
import com.wizardlybump17.vehicles.api.vehicle.Car;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;

import java.util.Map;

@SerializableAs("car")
public class CarModel extends VehicleModel<Car> {

    public CarModel(Vehicles plugin, String name, double maxSpeed, @NonNull Map<Double, Double> acceleration, Map<Double, Double> damage, @NonNull Map<Double, Double> breakForce, @NonNull String megModel, float rotationSpeed) {
        super(plugin, name, maxSpeed, acceleration, damage, breakForce, megModel, rotationSpeed);
    }

    @Override
    public Car createVehicle(Location location) {
        ActiveModel model = getMegModel();

        World world = location.getWorld();
        if (world == null)
            throw new IllegalArgumentException("invalid location: " + location);

        Car car = new Car(this, model);

        VehicleEntity entity = new VehicleEntity(location, car);
        ((CraftWorld) world).getHandle().addEntity(entity);

        ModeledEntity modeledEntity = ModelEngineAPI.api.getModelManager().createModeledEntity(entity.getBukkitEntity());
        modeledEntity.addActiveModel(model);
        modeledEntity.setInvisible(true);

        IMountHandler mountHandler = modeledEntity.getMountHandler();
        mountHandler.setSteerable(true);
        mountHandler.setCanCarryPassenger(true);

        car.markEntity();
        return car;
    }

    @SuppressWarnings("unchecked")
    public static CarModel deserialize(Map<String, Object> args) {
        return new CarModel(
                Vehicles.getInstance(),
                (String) args.get("name"),
                ((Number) args.getOrDefault("max-speed", 0)).doubleValue(),
                (Map<Double, Double>) args.get("acceleration"),
                (Map<Double, Double>) args.get("damage"),
                (Map<Double, Double>) args.get("break-force"),
                (String) args.get("meg-model"),
                ((Number) args.getOrDefault("rotation-speed", 0)).floatValue()
        );
    }
}
