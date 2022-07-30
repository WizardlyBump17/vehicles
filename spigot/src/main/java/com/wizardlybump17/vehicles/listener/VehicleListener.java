package com.wizardlybump17.vehicles.listener;

import com.wizardlybump17.vehicles.api.cache.VehicleCache;
import com.wizardlybump17.vehicles.api.cache.VehicleModelCache;
import com.wizardlybump17.vehicles.api.model.VehicleModel;
import com.wizardlybump17.vehicles.api.vehicle.Vehicle;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class VehicleListener implements Listener {

    private final VehicleCache cache;
    private final VehicleModelCache modelCache;

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

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        Optional<Vehicle<?>> optional = cache.get(event.getEntity());
        if (optional.isEmpty())
            return;

        event.setCancelled(true);

        if (event instanceof EntityDamageByEntityEvent damageEvent && damageEvent.getDamager() instanceof Player player)
            optional.get().onDamage(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        Optional<Vehicle<?>> optional = cache.get(event.getPlayer(), true);
        if (optional.isEmpty())
            return;

        event.setCancelled(true);

        optional.get().removeEntity(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        if (event.getNewGameMode() != GameMode.SPECTATOR)
            return;

        Optional<Vehicle<?>> optional = cache.get(event.getPlayer(), true);
        if (optional.isEmpty())
            return;

        optional.get().removeEntity(event.getPlayer()); //if the player changes its gamemode to spectator, he dismounts the vehicle
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntitiesLoad(EntitiesLoadEvent event) {
        for (Entity entity : event.getEntities()) {
            if (!Vehicle.isVehicle(entity) || cache.has(entity)) //it is already loaded
                return;

            entity.remove();

            Map<String, Object> data = Vehicle.getVehicleData(entity);

            VehicleModel<?> model = modelCache.get((String) data.get("type")).orElse(null);
            if (model == null)
                continue;

            model.createVehicle(entity.getLocation(), data.get("plate").toString());
        }
    }
}
