package com.wizardlybump17.vehicles.api.listener.key;

import com.wizardlybump17.vehicles.api.ButtonType;
import org.bukkit.entity.Player;

public interface KeyListener {

    void handle(Player player, double xxa, double zza, boolean up, boolean down);

    void onKeyPressed(Player player, ButtonType button);

    void onKeyReleased(Player player, ButtonType button);

    boolean isKeyPressed(Player player, ButtonType... button);
}
