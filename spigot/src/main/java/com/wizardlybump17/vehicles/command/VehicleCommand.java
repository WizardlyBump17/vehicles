package com.wizardlybump17.vehicles.command;

import com.wizardlybump17.vehicles.Vehicles;
import com.wizardlybump17.vehicles.api.config.Messages;
import com.wizardlybump17.vehicles.api.model.VehicleModel;
import com.wizardlybump17.wlib.command.Command;
import com.wizardlybump17.wlib.command.sender.GenericSender;
import org.bukkit.entity.Player;

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
}
