package com.wizardlybump17.vehicles.api.vehicle.airplane;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.PartEntity;
import com.wizardlybump17.vehicles.api.info.TNTInfo;
import com.wizardlybump17.vehicles.api.model.airplane.military.MilitaryAirplaneModel;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;
import java.util.Map;

public class MilitaryAirplane extends Airplane {

    private final Map<String, Long> tntDelay = new HashMap<>();

    public MilitaryAirplane(MilitaryAirplaneModel model, String plate, ActiveModel megModel) {
        super(model, plate, megModel);
    }

    @Override
    public boolean onLeftClick(Player player, EquipmentSlot hand) {
        handleShot(player);
        return true;
    }

    @Override
    public void onDamage(Player player) {
        handleShot(player);
    }

    private void handleShot(Player player) {
        if (player.equals(getDriver()))
            shootTnts();
    }

    public void shootTnts() {
        PartEntity part = getMegModel().getPartEntity("tnts");

        for (Map.Entry<String, TNTInfo> entry : getModel().getTnts().entrySet()) {
            if (tntDelay.getOrDefault(entry.getKey(), System.currentTimeMillis()) > System.currentTimeMillis())
                continue;

            PartEntity entity = part.getChild(entry.getKey());
            TNTInfo info = entry.getValue();

            Location location = entity.getWorldPosition();

            location.setYaw((float) (getEntity().getLocation().getYaw() + info.getRotation().getX()));
            location.setPitch((float) info.getRotation().getY());

            getModel().createTNT(location, info);

            tntDelay.put(entry.getKey(), System.currentTimeMillis() + info.getDelay());
        }
    }

    @Override
    public MilitaryAirplaneModel getModel() {
        return (MilitaryAirplaneModel) super.getModel();
    }
}
