package com.wizardlybump17.vehicles.api.vehicle.airplane;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.PartEntity;
import com.wizardlybump17.vehicles.api.info.TNTInfo;
import com.wizardlybump17.vehicles.api.model.airplane.military.MilitaryAirplaneModel;
import org.bukkit.Location;
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
            shootTnts();
            tntDelay.put(player, System.currentTimeMillis() + getModel().getTntInfo().getDelay());
        }
        return true;
    }

    @Override
    public void onDamage(Player player) {
        if (player.equals(getDriver()) && tntDelay.getOrDefault(player, System.currentTimeMillis()) <= System.currentTimeMillis()) {
            shootTnts();
            tntDelay.put(player, System.currentTimeMillis() + getModel().getTntInfo().getDelay());
        }
    }

    public void shootTnts() {
        PartEntity part = getMegModel().getPartEntity("tnts");

        for (Map.Entry<String, TNTInfo> entry : getModel().getTnts().entrySet()) {
            PartEntity entity = part.getChild(entry.getKey());
            TNTInfo info = entry.getValue();

            Location location = entity.getWorldPosition();

            location.setYaw((float) (getEntity().getLocation().getYaw() + info.getRotation().getX()));
            location.setPitch((float) info.getRotation().getY());

            getModel().createTNT(location, info);
        }
    }

    @Override
    public MilitaryAirplaneModel getModel() {
        return (MilitaryAirplaneModel) super.getModel();
    }
}
