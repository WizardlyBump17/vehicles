package com.wizardlybump17.vehicles.command;

import com.wizardlybump17.vehicles.Vehicles;
import com.wizardlybump17.vehicles.api.config.Messages;
import com.wizardlybump17.vehicles.api.model.VehicleModel;
import com.wizardlybump17.vehicles.api.vehicle.Vehicle;
import com.wizardlybump17.wlib.command.Command;
import com.wizardlybump17.wlib.command.sender.GenericSender;
import com.wizardlybump17.wlib.command.sender.PlayerSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public record VehicleCommand(Vehicles plugin) {

    public static final String ADMIN_PERMISSION = "vehicles.admin";

    @Command(execution = "vehicle give <player> <model>", permission = ADMIN_PERMISSION)
    public void give(GenericSender sender, Player player, VehicleModel<?> model) {
        if (player == null) {
            sender.sendMessage(Messages.invalidPlayer);
            return;
        }

        if (model == null) {
            sender.sendMessage(Messages.invalidModel);
            return;
        }

        player.getInventory().addItem(model.getItem());
    }

    @Command(execution = "vehicle leave")
    public void leave(PlayerSender sender) {
        Optional<Vehicle<?>> optional = plugin.getVehicleCache().get(sender.getHandle(), true);
        if (!optional.isPresent()) {
            sender.sendMessage(Messages.notInVehicle);
            return;
        }

        optional.get().removeEntity(sender.getHandle());
        sender.sendMessage(Messages.leftVehicle);
    }
}
