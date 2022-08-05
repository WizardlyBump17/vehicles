package com.wizardlybump17.vehicles.api.model.airplane.military;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.PartEntity;
import com.ticxo.modelengine.api.model.mount.handler.IMountHandler;
import com.wizardlybump17.vehicles.api.Vehicles;
import com.wizardlybump17.vehicles.api.entity.AirplaneEntity;
import com.wizardlybump17.vehicles.api.info.DamageInfo;
import com.wizardlybump17.vehicles.api.info.LockInfo;
import com.wizardlybump17.vehicles.api.info.TNTInfo;
import com.wizardlybump17.vehicles.api.info.airplane.AirplaneSpeedInfo;
import com.wizardlybump17.vehicles.api.info.airplane.FallSpeedInfo;
import com.wizardlybump17.vehicles.api.model.airplane.AirplaneModel;
import com.wizardlybump17.vehicles.api.vehicle.airplane.MilitaryAirplane;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("military-airplane")
@Getter
@Setter
public class MilitaryAirplaneModel extends AirplaneModel {

    public static final NamespacedKey TNT_KEY = new NamespacedKey(Vehicles.getInstance(), "tnt");

    @NonNull
    private final TNTInfo tntInfo;
    @NonNull
    private final FallSpeedInfo fallSpeed;
    private final Map<String, Vector> tntsDirection = new HashMap<>();

    public MilitaryAirplaneModel(
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
            @NonNull TNTInfo tntInfo,
            @NonNull FallSpeedInfo fallSpeed,
            int floatingPrecision,
            LockInfo lock) {
        super(plugin, name, speed, damage, megModel, rotationSpeed, jumpHeight, minPitch, maxPitch, pitchSpeed, fallSpeed, floatingPrecision, lock);
        this.tntInfo = tntInfo;
        this.fallSpeed = fallSpeed;
    }

    @Override
    public MilitaryAirplane createVehicle(Location location, String plate) {
        World world = location.getWorld();
        if (world == null)
            throw new IllegalArgumentException("invalid location: " + location);

        ActiveModel model = getMegModel();
        model.setClamp(location.getYaw());

        MilitaryAirplane airplane = new MilitaryAirplane(this, plate, model);

        AirplaneEntity entity = new AirplaneEntity(location, airplane);
        ((CraftWorld) world).getHandle().addEntity(entity);
        model.setClamp(0);

        ModeledEntity modeledEntity = ModelEngineAPI.api.getModelManager().createModeledEntity(entity.getBukkitEntity());
        modeledEntity.addActiveModel(model);
        modeledEntity.setInvisible(true);
        modeledEntity.detectPlayers();
        initTNTDirections(model);

        IMountHandler mountHandler = modeledEntity.getMountHandler();
        mountHandler.setSteerable(true);
        mountHandler.setCanCarryPassenger(true);

        airplane.markEntity();
        return airplane;
    }

    private void initTNTDirections(ActiveModel model) {
        if (!tntsDirection.isEmpty())
            return;

        PartEntity part = model.getPartEntity("tnts");
        if (part == null)
            return;

        BlueprintBone bone = model.getBlueprint().getBones().get("tnts");
        if (bone == null)
            return;

        for (Map.Entry<String, BlueprintBone> entry : bone.getChildren().entrySet()) {
            if (!entry.getKey().toLowerCase().startsWith("tnt"))
                continue;

            BlueprintBone tnt = entry.getValue();
            setTNTDirection(entry.getKey(), new Vector(Math.toDegrees(-tnt.getLocalRotationX()), Math.toDegrees(-tnt.getLocalRotationY()), Math.toDegrees(tnt.getLocalRotationZ())));
        }
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        map.put("tnt", tntInfo);
        return map;
    }

    public void setTNTDirection(String bone, Vector direction) {
        tntsDirection.put(bone, direction);
    }

    public Vector getTNTDirection(String bone) {
        return tntsDirection.get(bone);
    }

    @SuppressWarnings("unchecked")
    public static MilitaryAirplaneModel deserialize(Map<String, Object> args) {
        Map<String, Object> pitch = (Map<String, Object>) args.get("pitch");
        return new MilitaryAirplaneModel(
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
                (TNTInfo) args.getOrDefault("tnt", TNTInfo.defaultInfo()),
                (FallSpeedInfo) args.getOrDefault("fall-speed", FallSpeedInfo.defaultInfo()),
                (int) args.getOrDefault("floating-precision", 2),
                (LockInfo) args.getOrDefault("lock", LockInfo.defaultInfo())
        );
    }

    public TNTPrimed createTNT(Location location, Vector direction) {
        return location.getWorld().spawn(location, TNTPrimed.class, tnt -> {
            tnt.setFuseTicks(tntInfo.getFuseTicks());
            tnt.getPersistentDataContainer().set(TNT_KEY, PersistentDataType.STRING, getName());
            tnt.setVelocity(direction);
        });
    }

    public static String getModelName(Entity entity) {
        return entity.getPersistentDataContainer().get(TNT_KEY, PersistentDataType.STRING);
    }

    public boolean isTNT(Entity entity) {
        return getName().equals(getModelName(entity));
    }
}
