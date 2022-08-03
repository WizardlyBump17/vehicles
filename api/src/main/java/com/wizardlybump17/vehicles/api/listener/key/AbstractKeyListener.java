package com.wizardlybump17.vehicles.api.listener.key;

import com.wizardlybump17.vehicles.api.ButtonType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractKeyListener implements KeyListener {

    private final Map<Player, Double> xxas = new HashMap<>();
    private final Map<Player, Double> zzas = new HashMap<>();
    private final Map<Player, Boolean> jumping = new HashMap<>();
    private final Map<Player, Boolean> shifting = new HashMap<>();

    @Override
    public void handle(Player player, double xxa, double zza, boolean up, boolean down) {
        handleXxa(player, xxa);
        handleZza(player, zza);
        handleJump(player, up);
        handleShift(player, down);
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

    private void handleJump(Player player, boolean jump) {
        if (!jumping.containsKey(player) && !jump)
            return;

        boolean old = jumping.computeIfAbsent(player, p -> {
            if (jump)
                onKeyPressed(player, ButtonType.UP);
            return jump;
        });
        jumping.put(player, jump);

        if (old == jump)
            return;

        if (old)
            onKeyReleased(player, ButtonType.UP);

        if (jump)
            onKeyPressed(player, ButtonType.UP);
    }

    private void handleShift(Player player, boolean shift) {
        if (!shifting.containsKey(player) && !shift)
            return;

        boolean old = shifting.computeIfAbsent(player, p -> {
            if (shift)
                onKeyPressed(player, ButtonType.DOWN);
            return shift;
        });
        shifting.put(player, shift);

        if (old == shift)
            return;

        if (old)
            onKeyReleased(player, ButtonType.DOWN);

        if (shift)
            onKeyPressed(player, ButtonType.DOWN);
    }

    @Override
    public boolean isKeyPressed(Player player, ButtonType... buttons) {
        double xxa = xxas.getOrDefault(player, 0d);
        double zza = zzas.getOrDefault(player, 0d);

        for (ButtonType type : buttons) {
            boolean pressed = switch (type) {
                case FORWARD -> zza > 0;
                case BACKWARD -> zza < 0;
                case ROTATE_LEFT -> xxa > 0;
                case ROTATE_RIGHT -> xxa < 0;
                case UP -> jumping.getOrDefault(player, false);
                case DOWN -> shifting.getOrDefault(player, false);
            };
            if (!pressed)
                return false;
        }

        return true;
    }
}
