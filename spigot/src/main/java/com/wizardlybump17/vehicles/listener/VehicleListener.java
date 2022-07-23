package com.wizardlybump17.vehicles.listener;

import com.wizardlybump17.vehicles.api.cache.VehicleCache;
import com.wizardlybump17.vehicles.api.vehicle.Vehicle;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Optional;

@RequiredArgsConstructor
public class VehicleListener implements Listener {

    private final VehicleCache cache;

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractAtEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND)
            return;

        Optional<Vehicle<?>> optional = cache.get(event.getRightClicked());
        if (optional.isEmpty())
            return;

        event.setCancelled(true);

        Vehicle<?> vehicle = optional.get();
        if (vehicle.hasEntity(event.getPlayer()))
            return;

        vehicle.onInteract(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        Optional<Vehicle<?>> optional = cache.get(event.getEntity());
        if (optional.isEmpty())
            return;

        event.setCancelled(true);

        if (event.getDamager() instanceof Player player)
            optional.get().onDamage(player);
    }
}
