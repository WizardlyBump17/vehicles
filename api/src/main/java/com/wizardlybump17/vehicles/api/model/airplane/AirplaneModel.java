package com.wizardlybump17.vehicles.api.model.airplane;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.mount.handler.IMountHandler;
import com.wizardlybump17.vehicles.api.Vehicles;
import com.wizardlybump17.vehicles.api.entity.AirplaneEntity;
import com.wizardlybump17.vehicles.api.info.DamageInfo;
import com.wizardlybump17.vehicles.api.info.LockInfo;
import com.wizardlybump17.vehicles.api.info.airplane.AirplaneSpeedInfo;
import com.wizardlybump17.vehicles.api.info.airplane.FallSpeedInfo;
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

import java.util.Map;

@SerializableAs("airplane")
@Getter
@Setter
public class AirplaneModel extends VehicleModel<Airplane> {

    private float pitchSpeed;
    private float maxPitch;
    private float minPitch;
    private double minSpeed;
    @NonNull
    private final FallSpeedInfo fallSpeed;

    public AirplaneModel(
            Vehicles plugin,
            String name,
            AirplaneSpeedInfo speed,
            DamageInfo damage,
            @NonNull String megModel,
            float rotationSpeed,
            float jumpHeight,
            float minPitch,
            float maxPitch,
            float pitchSpeed,
            @NonNull FallSpeedInfo fallSpeed,
            int floatingPrecision,
            LockInfo lock) {
        super(plugin, name, speed, damage, megModel, rotationSpeed, jumpHeight, floatingPrecision, lock);
        this.pitchSpeed = pitchSpeed;
        this.maxPitch = maxPitch;
        this.minPitch = minPitch;
        this.fallSpeed = fallSpeed;
    }

    @Override
    public AirplaneSpeedInfo getSpeed() {
        return (AirplaneSpeedInfo) super.getSpeed();
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
        map.put("fall-speed", fallSpeed.serialize());
        return map;
    }

    @SuppressWarnings("unchecked")
    public static AirplaneModel deserialize(Map<String, Object> args) {
        Map<String, Object> pitch = (Map<String, Object>) args.get("pitch");
        return new AirplaneModel(
                Vehicles.getInstance(),
                (String) args.get("name"),
                (AirplaneSpeedInfo) args.getOrDefault("speed", AirplaneSpeedInfo.defaultInfo()),
                (DamageInfo) args.getOrDefault("damage", DamageInfo.defaultInfo()),
                (String) args.get("meg-model"),
                ((Number) args.getOrDefault("rotation-speed", 0f)).floatValue(),
                ((Number) args.getOrDefault("jump-height", 0.6f)).floatValue(),
                ((Number) pitch.getOrDefault("min", 90f)).floatValue(),
                ((Number) pitch.getOrDefault("max", 90f)).floatValue(),
                ((Number) pitch.getOrDefault("speed", 0f)).floatValue(),
                (FallSpeedInfo) args.getOrDefault("fall-speed", FallSpeedInfo.defaultInfo()),
                (int) args.getOrDefault("floating-precision", 2),
                (LockInfo) args.getOrDefault("lock", LockInfo.defaultInfo())
        );
    }
}
