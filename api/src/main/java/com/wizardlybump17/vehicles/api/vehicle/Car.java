package com.wizardlybump17.vehicles.api.vehicle;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.wizardlybump17.vehicles.api.entity.VehicleEntity;
import com.wizardlybump17.vehicles.api.model.CarModel;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Car extends Vehicle<CarModel> {

    public Car(CarModel model, ActiveModel megModel) {
        super(model, megModel);
    }

    @Override
    public void move(Player player, double xxa, double zza) {
        if (zza == 0)
            return;

        setSpeed(Math.min(getModel().getMaxSpeed(), getSpeed() + getModel().getAcceleration(getSpeed())));

        Entity entity = getEntity();

        Location location = entity.getLocation();
        location.setPitch(0);
        Vector direction = location.getDirection();
        direction.multiply(zza > 0 ? getSpeed() : -getSpeed() / getModel().getBreakForce(getSpeed()));

        entity.setVelocity(direction.setY(-1));
    }

    @Override
    public void rotate(Player player, double xxa, double zza) {
        if (xxa == 0)
            return;

        VehicleEntity entity = (VehicleEntity) ((CraftEntity) getEntity()).getHandle();
        float yaw = entity.getYRot();
        yaw += xxa > 0 ? -getModel().getRotationSpeed() : getModel().getRotationSpeed();
        entity.getBukkitEntity().setRotation(yaw, 0);
        entity.updateYaw();
    }

    @Override
    public void jump(Player player, double xxa, double zza) {
    }

    @Override
    public void shift(Player player, double xxa, double zza) {
        removeEntity(player);
    }

    @Override
    public void onDamage(Player player) {
    }

    @Override
    public void onInteract(Player player) {
        addEntity(player);
    }
}
