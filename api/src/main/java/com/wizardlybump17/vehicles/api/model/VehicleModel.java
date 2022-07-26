package com.wizardlybump17.vehicles.api.model;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.wizardlybump17.vehicles.api.Vehicles;
import com.wizardlybump17.vehicles.api.info.DamageInfo;
import com.wizardlybump17.vehicles.api.info.LockInfo;
import com.wizardlybump17.vehicles.api.info.SpeedInfo;
import com.wizardlybump17.vehicles.api.vehicle.Vehicle;
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
    private final SpeedInfo speed;
    private final DamageInfo damage;
    @NonNull
    private String megModel;
    private float rotationSpeed;
    private float jumpHeight;
    private int floatingPrecision;
    private LockInfo lock;

    public abstract V createVehicle(Location location, String plate);

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
                "damage", damage,
                "meg-model", megModel,
                "rotation-speed", rotationSpeed,
                "jump-height", jumpHeight,
                "floating-precision", floatingPrecision,
                "lock", lock
        ));
    }
}
