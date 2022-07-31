package com.wizardlybump17.vehicles.api.model.airplane;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.mount.handler.IMountHandler;
import com.wizardlybump17.vehicles.api.Vehicles;
import com.wizardlybump17.vehicles.api.entity.AirplaneEntity;
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

    private int tntFuseTicks;
    private Vector tntDirection;
    private boolean useDriverRotation;
    private float maxTntPitch;
    private float minTntPitch;
    private long tntDelay;
    private float tntPower;
    private boolean breakBlocks;
    private boolean setFire;

    public MilitaryAirplaneModel(Vehicles plugin, String name, double maxSpeed, double smoothSpeed, @NonNull Map<Double, Double> acceleration, Map<Double, Double> damage, @NonNull Map<Double, Double> breakForce, @NonNull String megModel, float rotationSpeed, float jumpHeight, float minPitch, float maxPitch, float pitchSpeed, float minFallSpeed, int tntFuseTicks, Vector tntDirection, boolean useDriverRotation, float minTntPitch, float maxTntPitch, long tntDelay, float tntPower, boolean breakBlocks, boolean setFire) {
        super(plugin, name, maxSpeed, smoothSpeed, acceleration, damage, breakForce, megModel, rotationSpeed, jumpHeight, minPitch, maxPitch, pitchSpeed, minFallSpeed);
        this.tntFuseTicks = tntFuseTicks;
        this.tntDirection = tntDirection;
        this.useDriverRotation = useDriverRotation;
        this.maxTntPitch = maxTntPitch;
        this.minTntPitch = minTntPitch;
        this.tntDelay = tntDelay;
        this.tntPower = tntPower;
        this.breakBlocks = breakBlocks;
        this.setFire = setFire;
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

        IMountHandler mountHandler = modeledEntity.getMountHandler();
        mountHandler.setSteerable(true);
        mountHandler.setCanCarryPassenger(true);

        airplane.markEntity();
        return airplane;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        map.put("tnt", Map.of(
                "fuse-ticks", tntFuseTicks,
                "direction", tntDirection,
                "use-driver-rotation", useDriverRotation,
                "max-pitch", maxTntPitch,
                "min-pitch", minTntPitch,
                "delay", tntDelay,
                "power", tntPower,
                "break-blocks", breakBlocks,
                "set-fire", setFire
        ));
        return map;
    }

    @SuppressWarnings("unchecked")
    public static MilitaryAirplaneModel deserialize(Map<String, Object> args) {
        Map<String, Object> pitch = (Map<String, Object>) args.get("pitch");
        Map<String, Object> tnt = (Map<String, Object>) args.get("tnt");
        return new MilitaryAirplaneModel(
                Vehicles.getInstance(),
                (String) args.get("name"),
                ((Number) args.getOrDefault("max-speed", 0d)).doubleValue(),
                ((Number) args.getOrDefault("smooth-speed", 0.95)).doubleValue(),
                (Map<Double, Double>) args.get("acceleration"),
                (Map<Double, Double>) args.get("damage"),
                new HashMap<>(),
                (String) args.get("meg-model"),
                ((Number) args.getOrDefault("rotation-speed", 0f)).floatValue(),
                ((Number) args.getOrDefault("jump-height", 0.6f)).floatValue(),
                ((Number) pitch.getOrDefault("min", 90f)).floatValue(),
                ((Number) pitch.getOrDefault("max", 90f)).floatValue(),
                ((Number) pitch.getOrDefault("speed", 0f)).floatValue(),
                ((Number) args.getOrDefault("min-fall-speed", 0f)).floatValue(),
                (int) tnt.getOrDefault("fuse-ticks", 0),
                (Vector) tnt.getOrDefault("direction", new Vector(0, 0, 0)),
                (boolean) tnt.getOrDefault("use-driver-rotation", false),
                ((Number) tnt.getOrDefault("min-pitch", 0f)).floatValue(),
                ((Number) tnt.getOrDefault("max-pitch", 0f)).floatValue(),
                ((Number) tnt.getOrDefault("delay", 0L)).longValue(),
                ((Number) tnt.getOrDefault("power", 0d)).floatValue(),
                (boolean) tnt.getOrDefault("break-blocks", false),
                (boolean) tnt.getOrDefault("set-fire", false)
        );
    }

    public TNTPrimed createTNT(Location location) {
        return location.getWorld().spawn(location, TNTPrimed.class, tnt -> {
            tnt.setFuseTicks(tntFuseTicks);
            tnt.getPersistentDataContainer().set(TNT_KEY, PersistentDataType.STRING, getName());
            tnt.setVelocity(location.getDirection().multiply(tntDirection));
        });
    }

    public static String getModelName(Entity entity) {
        return entity.getPersistentDataContainer().get(TNT_KEY, PersistentDataType.STRING);
    }

    public boolean isTNT(Entity entity) {
        return getName().equals(getModelName(entity));
    }
}
