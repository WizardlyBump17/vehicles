package com.wizardlybump17.vehicles.api.cache;

import com.wizardlybump17.vehicles.api.model.VehicleModel;
import com.wizardlybump17.wlib.object.Cache;
import com.wizardlybump17.wlib.object.Pair;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class VehicleModelCache extends Cache<String, VehicleModel<?>, VehicleModel<?>> {

    @Override
    public @NotNull Pair<String, VehicleModel<?>> apply(VehicleModel<?> model) {
        return new Pair<>(model.getName().toLowerCase(), model);
    }

    @Override
    public @NotNull Optional<VehicleModel<?>> get(@Nullable String key) {
        return super.get(key == null ? null : key.toLowerCase());
    }

    @NotNull
    public Optional<VehicleModel<?>> get(@Nullable ItemStack item) {
        if (item == null)
            return Optional.empty();

        ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return Optional.empty();

        return get(meta.getPersistentDataContainer().get(VehicleModel.ITEM_KEY, PersistentDataType.STRING));
    }

    @Override
    public boolean has(@Nullable String key) {
        return super.has(key == null ? null : key.toLowerCase());
    }

    @Override
    public Optional<VehicleModel<?>> remove(@Nullable String key) {
        return super.remove(key == null ? null : key.toLowerCase());
    }
}
