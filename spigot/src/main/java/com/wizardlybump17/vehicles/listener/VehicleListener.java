package com.wizardlybump17.vehicles.listener;

import com.wizardlybump17.vehicles.api.cache.VehicleCache;
import com.wizardlybump17.vehicles.api.cache.VehicleModelCache;
import com.wizardlybump17.vehicles.api.model.VehicleModel;
import com.wizardlybump17.vehicles.api.model.airplane.military.MilitaryAirplaneModel;
import com.wizardlybump17.vehicles.api.model.info.TNTInfo;
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
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Optional<Vehicle<?>> optional = cache.get(player, true);
        if (optional.isEmpty())
            return;

        Vehicle<?> vehicle = optional.get();
        if (event.getAction().name().contains("LEFT"))
            event.setCancelled(vehicle.onLeftClick(player, event.getHand()));
        else if (event.getAction().name().contains("RIGHT"))
            event.setCancelled(vehicle.onRightClick(player, event.getHand()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExplode(EntityExplodeEvent event) {
        String name = MilitaryAirplaneModel.getModelName(event.getEntity());
        VehicleModel<?> model = modelCache.get(name).orElse(null);
        if (model == null)
            return;

        if (!(model instanceof MilitaryAirplaneModel militaryModel))
            return;

        event.setCancelled(true);

        TNTInfo info = militaryModel.getTntInfo();
        event.getEntity().getWorld().createExplosion(event.getLocation(), info.getPower(), info.isSetFire(), info.isBreakBlocks());
    }
}
