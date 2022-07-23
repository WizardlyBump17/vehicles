package com.wizardlybump17.vehicles.listener;

import com.wizardlybump17.vehicles.Vehicles;
import com.wizardlybump17.vehicles.api.model.VehicleModel;
import com.wizardlybump17.vehicles.api.vehicle.Vehicle;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Optional;

public record PlayerListener(Vehicles plugin) implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND || !event.getAction().name().contains("RIGHT"))
            return;

        Optional<VehicleModel<?>> optional = plugin.getVehicleModelCache().get(event.getItem());
        if (optional.isEmpty())
            return;

        event.setCancelled(true);

        Location location = event.getPlayer().getEyeLocation();
        location.setPitch(0);
        Vehicle<?> vehicle = optional.get().createVehicle(location);
        plugin.getVehicleCache().add(vehicle);
    }
}
