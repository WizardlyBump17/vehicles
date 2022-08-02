package com.wizardlybump17.vehicles.api.vehicle.airplane;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.PartEntity;
import com.wizardlybump17.vehicles.api.info.TNTInfo;
import com.wizardlybump17.vehicles.api.model.airplane.military.MilitaryAirplaneModel;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;
import java.util.Map;

public class MilitaryAirplane extends Airplane {

    private final Map<Entity, Long> tntDelay = new HashMap<>();

    public MilitaryAirplane(MilitaryAirplaneModel model, String plate, ActiveModel megModel) {
        super(model, plate, megModel);
    }

    @Override
    public boolean onLeftClick(Player player, EquipmentSlot hand) {
        if (player.equals(getDriver()) && tntDelay.getOrDefault(player, System.currentTimeMillis()) <= System.currentTimeMillis()) {
            shootTnt();
            tntDelay.put(player, System.currentTimeMillis() + getModel().getTntInfo().getDelay());
        }
        return true;
    }

    @Override
    public void onDamage(Player player) {
        if (player.equals(getDriver()) && tntDelay.getOrDefault(player, System.currentTimeMillis()) <= System.currentTimeMillis()) {
            shootTnt();
            tntDelay.put(player, System.currentTimeMillis() + getModel().getTntInfo().getDelay());
        }
    }

    public void shootTnt() {
        PartEntity part = getMegModel().getPartEntity("tnts");
        if (part == null)
            return;

        Location location = part.getWorldPosition();
        TNTInfo info = getModel().getTntInfo();

        if (info.isUseDriverRotation() && getDriver() != null) {
            Location driverLocation = getDriver().getLocation();
            location.setYaw(driverLocation.getYaw());
            location.setPitch(info.fixPitch(driverLocation.getPitch()));
        } else
            location.setYaw(((CraftEntity) getEntity()).getHandle().getBukkitYaw());
        getModel().createTNT(location);
    }

    @Override
    public MilitaryAirplaneModel getModel() {
        return (MilitaryAirplaneModel) super.getModel();
    }
}
