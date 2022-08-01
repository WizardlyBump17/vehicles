package com.wizardlybump17.vehicles.api.listener;

import com.wizardlybump17.vehicles.api.ButtonType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractKeyListener implements KeyListener {

    private final Map<Player, Double> xxas = new HashMap<>();
    private final Map<Player, Double> zzas = new HashMap<>();

    @Override
    public void handle(Player player, double xxa, double zza) {
        handleXxa(player, xxa);
        handleZza(player, zza);
    }

    @Override
    public boolean isKeyPressed(Player player, ButtonType button) {
        Double xxa = xxas.get(player);
        Double zza = zzas.get(player);

        return switch (button) {
            case FORWARD -> zza != null && zza > 0;
            case BACKWARD -> zza != null && zza < 0;
            case ROTATE_LEFT -> xxa != null && xxa > 0;
            case ROTATE_RIGHT -> xxa != null && xxa < 0;
            default -> false;
        };
    }

    private void handleXxa(Player player, double xxa) {
        if (!xxas.containsKey(player) && xxa == 0)
            return;

        double old = xxas.computeIfAbsent(player, p -> {
            if (xxa > 0)
                onKeyPressed(player, ButtonType.ROTATE_LEFT);
            else if (xxa < 0)
                onKeyPressed(player, ButtonType.ROTATE_RIGHT);
            return xxa;
        });
        xxas.put(player, xxa);

        if (old == xxa)
            return;

        if (old < 0)
            onKeyReleased(player, ButtonType.ROTATE_RIGHT);
        else if (old > 0)
            onKeyReleased(player, ButtonType.ROTATE_LEFT);

        if (xxa > 0)
            onKeyPressed(player, ButtonType.ROTATE_LEFT);
        else if (xxa < 0)
            onKeyPressed(player, ButtonType.ROTATE_RIGHT);
    }

    private void handleZza(Player player, double zza) {
        if (!zzas.containsKey(player) && zza == 0)
            return;

        double old = zzas.computeIfAbsent(player, p -> {
            if (zza > 0)
                onKeyPressed(player, ButtonType.FORWARD);
            else if (zza < 0)
                onKeyPressed(player, ButtonType.BACKWARD);
            return zza;
        });
        zzas.put(player, zza);

        if (old == zza)
            return;

        if (old < 0)
            onKeyReleased(player, ButtonType.BACKWARD);
        else if (old > 0)
            onKeyReleased(player, ButtonType.FORWARD);

        if (zza > 0)
            onKeyPressed(player, ButtonType.FORWARD);
        else if (zza < 0)
            onKeyPressed(player, ButtonType.BACKWARD);
    }
}
