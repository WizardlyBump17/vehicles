package com.wizardlybump17.vehicles.api.model.motorcycle;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.mount.handler.IMountHandler;
import com.wizardlybump17.vehicles.api.Vehicles;
import com.wizardlybump17.vehicles.api.entity.VehicleEntity;
import com.wizardlybump17.vehicles.api.model.AutomobileModel;
import com.wizardlybump17.vehicles.api.vehicle.motorcycle.Motorcycle;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;

import java.util.Map;

@SerializableAs("motorcycle")
public class MotorcycleModel extends AutomobileModel<Motorcycle> {

    public MotorcycleModel(Vehicles plugin, String name, double maxSpeed, @NonNull Map<Double, Double> acceleration, Map<Double, Double> damage, @NonNull Map<Double, Double> breakForce, @NonNull String megModel, float rotationSpeed, float jumpHeight) {
        super(plugin, name, maxSpeed, acceleration, damage, breakForce, megModel, rotationSpeed, jumpHeight);
    }

    @Override
    public Motorcycle createVehicle(Location location, String plate) {
        World world = location.getWorld();
        if (world == null)
            throw new IllegalArgumentException("invalid location: " + location);

        ActiveModel model = getMegModel();
        model.setClamp(location.getYaw());

        Motorcycle automobile = new Motorcycle(this, plate, model);

        VehicleEntity entity = new VehicleEntity(location, automobile);
        ((CraftWorld) world).getHandle().addEntity(entity);

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
    public static MotorcycleModel deserialize(Map<String, Object> args) {
        return new MotorcycleModel(
                Vehicles.getInstance(),
                (String) args.get("name"),
                ((Number) args.getOrDefault("max-speed", 0)).doubleValue(),
                (Map<Double, Double>) args.get("acceleration"),
                (Map<Double, Double>) args.get("damage"),
                (Map<Double, Double>) args.get("break-force"),
                (String) args.get("meg-model"),
                ((Number) args.getOrDefault("rotation-speed", 0)).floatValue(),
                ((Number) args.getOrDefault("jump-height", 0.6f)).floatValue()
        );
    }
}
