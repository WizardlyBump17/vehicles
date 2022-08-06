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
import com.wizardlybump17.vehicles.util.MEGUtil;
import com.wizardlybump17.wlib.object.Pair;
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
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SerializableAs("military-airplane")
@Getter
@Setter
public class MilitaryAirplaneModel extends AirplaneModel {

    public static final NamespacedKey TNT_KEY = new NamespacedKey(Vehicles.getInstance(), "tnt");
    public static final NamespacedKey TNT_NAME = new NamespacedKey(Vehicles.getInstance(), "name");
    public static final NamespacedKey TNT_MODEL = new NamespacedKey(Vehicles.getInstance(), "model");

    @NonNull
    private final FallSpeedInfo fallSpeed;
    private final Map<String, TNTInfo> tnts = new ConcurrentHashMap<>();

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
            @NonNull FallSpeedInfo fallSpeed,
            int floatingPrecision,
            LockInfo lock) {
        super(plugin, name, speed, damage, megModel, rotationSpeed, jumpHeight, minPitch, maxPitch, pitchSpeed, fallSpeed, floatingPrecision, lock);
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
        reloadTNTDirections(model);

        IMountHandler mountHandler = modeledEntity.getMountHandler();
        mountHandler.setSteerable(true);
        mountHandler.setCanCarryPassenger(true);

        airplane.markEntity();
        return airplane;
    }

    private void reloadTNTDirections(ActiveModel model) {
        tnts.clear();

        PartEntity part = model.getPartEntity("tnts");
        if (part == null)
            return;

        BlueprintBone bone = model.getBlueprint().getBones().get("tnts");
        if (bone == null)
            return;

        bone.setGlobalOffset(0, 0, 0);
        bone.setLocalOffset(0, 0, 0);

        for (Map.Entry<String, BlueprintBone> entry : bone.getChildren().entrySet()) {
            if (!entry.getKey().toLowerCase().startsWith("tnt"))
                continue;

            BlueprintBone tnt = entry.getValue();
            tnt.setGlobalOffset(0, 0, 0);
            tnt.setLocalOffset(0, 0, 0);
            reloadTNT(entry.getKey(), tnt);
        }
    }

    private void reloadTNT(String name, BlueprintBone bone) {
        TNTInfo defaultInfo = TNTInfo.defaultInfo();

        BlueprintBone fuseTicksBone = MEGUtil.getBone(bone, "fuse_ticks");
        int fuseTicks = defaultInfo.getFuseTicks();
        if (fuseTicksBone != null)
            fuseTicks = MEGUtil.getPosition(fuseTicksBone, bone).getBlockX();

        BlueprintBone directionBone = MEGUtil.getBone(bone, "direction");
        Vector direction = defaultInfo.getDirection();
        if (directionBone != null)
            direction = MEGUtil.getPosition(directionBone, bone);

        long delay = defaultInfo.getDelay();
        BlueprintBone delayBone = MEGUtil.getBone(bone, "delay");
        if (delayBone != null)
            delay = MEGUtil.getPosition(delayBone, bone).getBlockX();

        float power = defaultInfo.getPower();
        BlueprintBone powerBone = MEGUtil.getBone(bone, "power");
        if (powerBone != null)
            power = (float) MEGUtil.getPosition(powerBone, bone).getX();

        boolean setFire = defaultInfo.isSetFire();
        if (MEGUtil.getBone(bone, "set_fire") != null)
            setFire = true;

        boolean breakBlocks = defaultInfo.isBreakBlocks();
        if (MEGUtil.getBone(bone, "break_blocks") != null)
            breakBlocks = true;

        Vector rotation = defaultInfo.getRotation();
        BlueprintBone rotationBone = MEGUtil.getBone(bone, "rotation");
        if (rotationBone != null)
            rotation = MEGUtil.getRotation(rotationBone);

        TNTInfo info = new TNTInfo(
                name,
                fuseTicks,
                direction,
                delay,
                power,
                setFire,
                breakBlocks,
                rotation
        );
        tnts.put(name, info);
    }

    public void setTNTInfo(String bone, TNTInfo info) {
        tnts.put(bone, info);
    }

    public TNTInfo getTNTInfo(String bone) {
        return tnts.get(bone);
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
                (FallSpeedInfo) args.getOrDefault("fall-speed", FallSpeedInfo.defaultInfo()),
                (int) args.getOrDefault("floating-precision", 2),
                (LockInfo) args.getOrDefault("lock", LockInfo.defaultInfo())
        );
    }

    public TNTPrimed createTNT(Location location, TNTInfo info) {
        return location.getWorld().spawn(location, TNTPrimed.class, tnt -> {
            markTNTEntity(tnt, info);

            tnt.setFuseTicks(info.getFuseTicks());

            Vector direction = location.getDirection();
            direction.multiply(info.getDirection()).setY(info.getDirection().getY());

            tnt.setVelocity(direction);
        });
    }

    private void markTNTEntity(Entity entity, TNTInfo info) {
        PersistentDataContainer container = entity.getPersistentDataContainer();
        PersistentDataContainer data = container.getAdapterContext().newPersistentDataContainer();
        data.set(TNT_NAME, PersistentDataType.STRING, info.getName());
        data.set(TNT_MODEL, PersistentDataType.STRING, getName());
        container.set(TNT_KEY, PersistentDataType.TAG_CONTAINER, data);
    }

    public static Pair<String, String> getData(Entity entity) {
        PersistentDataContainer container = entity.getPersistentDataContainer();
        PersistentDataContainer data = container.get(TNT_KEY, PersistentDataType.TAG_CONTAINER);

        if (data == null || !data.has(TNT_NAME, PersistentDataType.STRING) || !data.has(TNT_MODEL, PersistentDataType.STRING))
            return null;

        return new Pair<>(data.get(TNT_MODEL, PersistentDataType.STRING), data.get(TNT_NAME, PersistentDataType.STRING));
    }
}
