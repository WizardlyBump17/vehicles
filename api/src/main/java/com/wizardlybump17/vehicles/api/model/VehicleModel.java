package com.wizardlybump17.vehicles.api.model;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.wizardlybump17.vehicles.api.Vehicles;
import com.wizardlybump17.vehicles.api.vehicle.Vehicle;
import com.wizardlybump17.vehicles.util.MapUtil;
import com.wizardlybump17.wlib.item.ItemBuilder;
import com.wizardlybump17.wlib.util.MapUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public abstract class VehicleModel<V extends Vehicle<?>> implements ConfigurationSerializable {

    public static final NamespacedKey ITEM_KEY = new NamespacedKey(Vehicles.getInstance(), "name");

    private final Vehicles plugin;
    private final String name;
    private double maxSpeed;
    @NonNull
    private Map<Double, Double> acceleration;
    private Map<Double, Double> damage;
    @NonNull
    private Map<Double, Double> breakForce;
    @NonNull
    private String megModel;
    private float rotationSpeed;

    public double getAcceleration(double speed) {
        return MapUtil.getValue(acceleration, speed);
    }

    public double getDamage(double speed) {
        return MapUtil.getValue(damage, speed);
    }

    public double getBreakForce(double speed) {
        return MapUtil.getValue(breakForce, speed);
    }

    public abstract V createVehicle(Location location);

    public ActiveModel getMegModel() {
        return Objects.requireNonNull(ModelEngineAPI.api.getModelManager().createActiveModel(megModel), "invalid ModelEngine model: " + megModel);
    }

    public ItemStack getItem() {
        ItemStack item = new ItemBuilder().type(Material.APPLE).displayName("§f" + name).build();
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(ITEM_KEY, PersistentDataType.STRING, name);
        item.setItemMeta(meta);
        return item;
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        return MapUtils.removeNullValues(MapUtils.mapOf(
                "name", name,
                "max-speed", maxSpeed,
                "acceleration", acceleration,
                "damage", damage,
                "break-force", breakForce,
                "meg-model", megModel,
                "rotation-speed", rotationSpeed
        ));
    }
}
