package com.wizardlybump17.vehicles.api.vehicle;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.mount.MountablePart;
import com.ticxo.modelengine.api.model.mount.handler.IMountHandler;
import com.wizardlybump17.vehicles.api.Vehicles;
import com.wizardlybump17.vehicles.api.cache.VehicleModelCache;
import com.wizardlybump17.vehicles.api.config.Config;
import com.wizardlybump17.vehicles.api.controller.EmptyMountController;
import com.wizardlybump17.vehicles.api.model.VehicleModel;
import com.wizardlybump17.vehicles.util.NumberUtil;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Data
public abstract class Vehicle<M extends VehicleModel<?>> {

    protected static final NamespacedKey VEHICLE = new NamespacedKey(Vehicles.getInstance(), "vehicle");
    protected static final NamespacedKey VEHICLE_PLATE = new NamespacedKey(Vehicles.getInstance(), "plate");
    protected static final NamespacedKey VEHICLE_TYPE = new NamespacedKey(Vehicles.getInstance(), "type");

    private final List<Player> viewers = new ArrayList<>();
    private final M model;
    private final String plate;
    private final ActiveModel megModel;
    private double speed;

    protected Vehicle(M model, String plate, ActiveModel megModel) {
        this.model = model;
        this.megModel = megModel;
        this.plate = plate;
    }

    public abstract void move(Player player, double xxa, double zza);

    public abstract void rotate(Player player, double xxa, double zza);

    public abstract void jump(Player player, double xxa, double zza);

    public abstract void shift(Player player, double xxa, double zza);

    public abstract void onDamage(Player player);

    public abstract void onInteract(Player player);

    public abstract void onCollide(Entity entity);

    /**
     * Called when the player do a left click while inside the vehicle.
     * @param player the player who clicked
     * @param hand the used hand
     * @return if the event should be cancelled
     */
    public boolean onLeftClick(Player player, EquipmentSlot hand) {
        return false;
    }

    /**
     * Called when the player do a right click while inside the vehicle.
     * @param player the player who clicked
     * @param hand the used hand
     * @return if the event should be cancelled
     */
    public boolean onRightClick(Player player, EquipmentSlot hand) {
        return false;
    }

    /**
     * Called by {@link com.wizardlybump17.vehicles.api.task.CheckVehiclesTask} each tick asynchronously. Can be useful for anything.<br>
     * The default implementation does nothing.
     */
    public void check() {
    }

    public void removePassenger(Entity entity) {
        megModel.getModeledEntity().getMountHandler().removePassenger(entity);
    }

    public boolean hasPassenger(Entity entity) {
        return megModel.getModeledEntity().getMountHandler().hasPassenger(entity);
    }

    /**
     * Tries to add the entity as passenger.<br>
     * It will iterate over {@code getMegModel().getModeledEntity().getMountHandler().getPassengers().values()} and add the entity to the first {@link MountablePart} with no passengers
     * @param entity the entity to add as passenger
     * @return true if the entity was added as passenger, false if not
     */
    public boolean addPassenger(Entity entity) {
        if (hasPassenger(entity))
            return false;

        IMountHandler handler = megModel.getModeledEntity().getMountHandler();
        for (MountablePart part : handler.getPassengers().values()) {
            if (part.hasPassengers())
                continue;

            handler.setController(entity, new EmptyMountController());
            part.addPassenger(entity, false);
            return true;
        }

        return false;
    }

    /**
     * Sets the driver of the vehicle.<br>
     * A null value will remove the current driver
     * @param entity the entity to set as driver
     */
    public void setDriver(@Nullable Entity entity) {
        megModel.getModeledEntity().getMountHandler().setDriver(entity, new EmptyMountController());
    }

    @Nullable
    public Entity getDriver() {
        return megModel.getModeledEntity().getMountHandler().getDriver();
    }

    @NotNull
    public List<Entity> getPassengers() {
        List<Entity> entities = new ArrayList<>();
        for (MountablePart part : megModel.getModeledEntity().getMountHandler().getPassengers().values())
            entities.addAll(part.getPassengers());
        return entities;
    }

    /**
     * Tries to add the entity to the vehicle.<br>
     * First it tries to set the driver if {@link #getDriver()} is null.<br>
     * If it is not null, it tries to add the entity as passenger.
     * @param entity the entity to add
     */
    public void addEntity(Entity entity) {
        if (hasEntity(entity))
            return;

        if (getDriver() == null)
            setDriver(entity);
        else
            addPassenger(entity);
    }

    /**
     * Tries to remove the entity from the vehicle.<br>
     * First it tries to remove the entity as driver.<br>
     * If it is not the driver, it tries to remove the entity as passenger.
     * @param entity the entity to remove
     */
    public void removeEntity(@Nullable Entity entity) {
        if (entity == null)
            return;

        if (entity.equals(getDriver()))
            setDriver(null);
        else
            removePassenger(entity);
    }

    public boolean hasEntity(Entity entity) {
        return entity.equals(getDriver()) || hasPassenger(entity);
    }

    public void teleport(Location location) {
        Bukkit.getScheduler().runTask(model.getPlugin(), () -> {
            CraftEntity entity = (CraftEntity) megModel.getModeledEntity().getEntity().getBase();
            entity.getHandle().setPositionRaw(location.getX(), location.getY(), location.getZ());
        });
    }

    public Entity getEntity() {
        return (Entity) megModel.getModeledEntity().getEntity().getBase();
    }

    public void markEntity() {
        PersistentDataContainer container = getEntity().getPersistentDataContainer();
        PersistentDataContainer data = container.getAdapterContext().newPersistentDataContainer();
        data.set(VEHICLE_PLATE, PersistentDataType.STRING, plate);
        data.set(VEHICLE_TYPE, PersistentDataType.STRING, model.getName());
        container.set(VEHICLE, PersistentDataType.TAG_CONTAINER, data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle<?> vehicle = (Vehicle<?>) o;
        return Objects.equals(model, vehicle.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(model);
    }

    @Override
    public String toString() {
        return "Vehicle{model=" + model + '}';
    }

    public void despawn() {
        getEntity().remove();
        getPassengers().forEach(this::removePassenger);
        removeEntity(getDriver());
    }

    public double getSpeed() {
        return speed;
    }

    public double getSpeed(boolean applyPrecision) {
        if (applyPrecision)
            return NumberUtil.precision(speed, Config.numberPrecision);
        return speed;
    }

    public static boolean isVehicle(Entity entity) {
        return entity.getPersistentDataContainer().has(VEHICLE, PersistentDataType.TAG_CONTAINER);
    }

    public static Map<String, Object> getVehicleData(Entity entity) {
        PersistentDataContainer container = entity.getPersistentDataContainer().get(VEHICLE, PersistentDataType.TAG_CONTAINER);
        if (container == null)
            return new HashMap<>();

        Map<String, Object> data = new HashMap<>();
        data.put("plate", container.get(VEHICLE_PLATE, PersistentDataType.STRING));
        data.put("type", container.get(VEHICLE_TYPE, PersistentDataType.STRING));
        return data;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <V extends Vehicle<?>> V createVehicle(Entity entity, VehicleModelCache cache) {
        if (!isVehicle(entity))
            return null;

        entity.remove();

        Map<String, Object> data = getVehicleData(entity);
        String type = (String) data.get("type");
        String plate = (String) data.get("plate");
        if (type == null || plate == null)
            return null;

        VehicleModel<?> model = cache.get(type).orElse(null);
        if (model == null)
            return null;

        return (V) model.createVehicle(entity.getLocation(), plate);
    }
}
