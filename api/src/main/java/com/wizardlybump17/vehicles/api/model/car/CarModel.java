package com.wizardlybump17.vehicles.api.model.car;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.mount.handler.IMountHandler;
import com.wizardlybump17.vehicles.api.Vehicles;
import com.wizardlybump17.vehicles.api.entity.AutomobileEntity;
import com.wizardlybump17.vehicles.api.info.DamageInfo;
import com.wizardlybump17.vehicles.api.info.SpeedInfo;
import com.wizardlybump17.vehicles.api.model.AutomobileModel;
import com.wizardlybump17.vehicles.api.vehicle.car.Car;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;

import java.util.Map;

@SerializableAs("car")
public class CarModel extends AutomobileModel<Car> {

    public CarModel(
            Vehicles plugin,
            String name,
            SpeedInfo speed,
            DamageInfo damage,
            @NonNull String megModel,
            float rotationSpeed,
            float jumpHeight,
            int floatingPrecision) {
        super(plugin, name, speed, damage, megModel, rotationSpeed, jumpHeight, floatingPrecision);
    }

    @Override
    public Car createVehicle(Location location, String plate) {
        World world = location.getWorld();
        if (world == null)
            throw new IllegalArgumentException("invalid location: " + location);

        ActiveModel model = getMegModel();
        model.setClamp(location.getYaw());

        Car automobile = new Car(this, plate, model);

        AutomobileEntity entity = new AutomobileEntity(location, automobile);
        ((CraftWorld) world).getHandle().addEntity(entity);
        model.setClamp(0);

        ModeledEntity modeledEntity = ModelEngineAPI.api.getModelManager().createModeledEntity(entity.getBukkitEntity());
        modeledEntity.addActiveModel(model);
        modeledEntity.setInvisible(true);
        modeledEntity.detectPlayers();

        IMountHandler mountHandler = modeledEntity.getMountHandler();
        mountHandler.setSteerable(true);
        mountHandler.setCanCarryPassenger(true);

        automobile.markEntity();
        return automobile;
    }

    @SuppressWarnings("unchecked")
    public static CarModel deserialize(Map<String, Object> args) {
        return new CarModel(
                Vehicles.getInstance(),
                (String) args.get("name"),
                (SpeedInfo) args.getOrDefault("speed", SpeedInfo.defaultInfo()),
                (DamageInfo) args.getOrDefault("damage", DamageInfo.defaultInfo()),
                (String) args.get("meg-model"),
                ((Number) args.getOrDefault("rotation-speed", 0)).floatValue(),
                ((Number) args.getOrDefault("jump-height", 0.6f)).floatValue(),
                (int) args.getOrDefault("floating-precision", 2)
        );
    }
}
