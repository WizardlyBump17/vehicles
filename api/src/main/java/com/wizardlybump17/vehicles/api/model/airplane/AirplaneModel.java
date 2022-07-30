package com.wizardlybump17.vehicles.api.model.airplane;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.mount.handler.IMountHandler;
import com.wizardlybump17.vehicles.api.Vehicles;
import com.wizardlybump17.vehicles.api.entity.AirplaneEntity;
import com.wizardlybump17.vehicles.api.model.VehicleModel;
import com.wizardlybump17.vehicles.api.vehicle.airplane.Airplane;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("airplane")
@Getter
@Setter
public class AirplaneModel extends VehicleModel<Airplane> {

    private float pitchSpeed;
    private float maxPitch;
    private float minPitch;
    private float minFallSpeed;

    public AirplaneModel(Vehicles plugin, String name, double maxSpeed, @NonNull Map<Double, Double> acceleration, Map<Double, Double> damage, @NonNull Map<Double, Double> breakForce, @NonNull String megModel, float rotationSpeed, float jumpHeight, float minPitch, float maxPitch, float pitchSpeed, float minFallSpeed) {
        super(plugin, name, maxSpeed, acceleration, damage, breakForce, megModel, rotationSpeed, jumpHeight);
        this.pitchSpeed = pitchSpeed;
        this.maxPitch = maxPitch;
        this.minPitch = minPitch;
        this.minFallSpeed = minFallSpeed;
    }

    @Override
    public Airplane createVehicle(Location location, String plate) {
        World world = location.getWorld();
        if (world == null)
            throw new IllegalArgumentException("invalid location: " + location);

        ActiveModel model = getMegModel();
        model.setClamp(location.getYaw());

        Airplane airplane = new Airplane(this, plate, model);

        AirplaneEntity entity = new AirplaneEntity(location, airplane);
        ((CraftWorld) world).getHandle().addEntity(entity);
        model.setClamp(0);

        ModeledEntity modeledEntity = ModelEngineAPI.api.getModelManager().createModeledEntity(entity.getBukkitEntity());
        modeledEntity.addActiveModel(model);
        modeledEntity.setInvisible(true);
        modeledEntity.detectPlayers();

        IMountHandler mountHandler = modeledEntity.getMountHandler();
        mountHandler.setSteerable(true);
        mountHandler.setCanCarryPassenger(true);

        airplane.markEntity();
        return airplane;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        map.put("pitch", Map.of(
                "speed", pitchSpeed,
                "max", maxPitch,
                "min", minPitch
        ));
        return map;
    }

    @SuppressWarnings("unchecked")
    public static AirplaneModel deserialize(Map<String, Object> args) {
        Map<String, Object> pitch = (Map<String, Object>) args.get("pitch");
        return new AirplaneModel(
                Vehicles.getInstance(),
                (String) args.get("name"),
                ((Number) args.getOrDefault("max-speed", 0d)).doubleValue(),
                (Map<Double, Double>) args.get("acceleration"),
                (Map<Double, Double>) args.get("damage"),
                new HashMap<>(),
                (String) args.get("meg-model"),
                ((Number) args.getOrDefault("rotation-speed", 0f)).floatValue(),
                ((Number) args.getOrDefault("jump-height", 0.6f)).floatValue(),
                ((Number) pitch.getOrDefault("min", 90f)).floatValue(),
                ((Number) pitch.getOrDefault("max", 90f)).floatValue(),
                ((Number) pitch.getOrDefault("speed", 0f)).floatValue(),
                ((Number) args.getOrDefault("min-fall-speed", 0f)).floatValue()
        );
    }
}