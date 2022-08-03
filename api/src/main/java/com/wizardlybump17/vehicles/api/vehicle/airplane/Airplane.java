package com.wizardlybump17.vehicles.api.vehicle.airplane;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.wizardlybump17.vehicles.api.ButtonType;
import com.wizardlybump17.vehicles.api.entity.AirplaneEntity;
import com.wizardlybump17.vehicles.api.info.DamageInfo;
import com.wizardlybump17.vehicles.api.info.LockInfo;
import com.wizardlybump17.vehicles.api.info.airplane.AirplaneSpeedInfo;
import com.wizardlybump17.vehicles.api.info.airplane.FallSpeedInfo;
import com.wizardlybump17.vehicles.api.model.airplane.AirplaneModel;
import com.wizardlybump17.vehicles.api.vehicle.Vehicle;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class Airplane extends Vehicle<AirplaneModel> {

    private final Map<Entity, Long> damagedEntities = new HashMap<>();

    public Airplane(AirplaneModel model, String plate, ActiveModel megModel) {
        super(model, plate, megModel);
    }

    @Override
    public void move(Player player, double xxa, double zza) {
        if (zza == 0 || !player.equals(getDriver()))
            return;

        Vector direction = getEntity().getLocation().getDirection();
        AirplaneSpeedInfo speedInfo = getModel().getSpeed();

        if (isLocked(LockInfo.LockType.SPEED) || (zza > 0 && getSpeed() > speedInfo.getMax()) || (getSpeed() >= speedInfo.getMaxFlySpeed() && !isOnGround())) {
            applyVelocity(direction);
            return;
        }

        double speed = getSpeed();

        if (zza < 0) {
            if (getSpeed(true) > 0)
                speed /= speedInfo.getBreakForce(speed);
            else
                speed -= speedInfo.getBreakAcceleration(-speed); // B) :D

            setSpeed(Math.max(speed, isOnGround() ? speedInfo.getMin() : speedInfo.getMinFlySpeed()));
        } else {
            speed += speedInfo.getAcceleration(speed);
            setSpeed(Math.min(speed, isOnGround() ? speedInfo.getMax() : speedInfo.getMaxFlySpeed()));
        }

        applyVelocity(direction);
    }

    @Override
    public void rotate(Player player, double xxa, double zza) {
        if (getSpeed(true) == 0 || xxa == 0 || !player.equals(getDriver()) || isLocked(LockInfo.LockType.ROTATION))
            return;

        AirplaneEntity entity = (AirplaneEntity) ((CraftEntity) getEntity()).getHandle();

        float yaw = entity.getBukkitYaw();
        yaw += xxa > 0 ? -getModel().getRotationSpeed() : getModel().getRotationSpeed();
        entity.getBukkitEntity().setRotation(yaw, entity.getBukkitEntity().getLocation().getPitch());
    }

    @Override
    public void jump(Player player, double xxa, double zza) {
        if (getSpeed(true) == 0 || !player.equals(getDriver()) || isLocked(LockInfo.LockType.PITCH))
            return;

        AirplaneEntity entity = (AirplaneEntity) ((CraftEntity) getEntity()).getHandle();
        Location location = entity.getBukkitEntity().getLocation();

        float pitch = location.getPitch();

        pitch -= getModel().getPitchSpeed();
        pitch = Math.max(getModel().getMinPitch(), pitch);
        entity.getBukkitEntity().setRotation(location.getYaw(), pitch);
    }

    @Override
    public void shift(Player player, double xxa, double zza) {
        if (getSpeed(true) == 0 || !player.equals(getDriver()) || isLocked(LockInfo.LockType.PITCH))
            return;

        AirplaneEntity entity = (AirplaneEntity) ((CraftEntity) getEntity()).getHandle();
        Location location = entity.getBukkitEntity().getLocation();

        float pitch = location.getPitch();

        pitch += getModel().getPitchSpeed();
        pitch = Math.min(getModel().getMaxPitch(), pitch);
        entity.getBukkitEntity().setRotation(location.getYaw(), pitch);
    }

    @Override
    public void onDamage(Player player) {
    }

    @Override
    public void onInteract(Player player) {
        addEntity(player);
    }

    @Override
    public void onCollide(Entity entity) {
        if (getSpeed(true) == 0 || !(entity instanceof LivingEntity living) || damagedEntities.getOrDefault(entity, System.currentTimeMillis()) > System.currentTimeMillis())
            return;

        DamageInfo damage = getModel().getDamage();
        living.damage(damage.getDamage(getSpeed()), getEntity());
        damagedEntities.put(entity, System.currentTimeMillis() + damage.getDelay());
    }

    @Override
    public void check() {
        super.check();

        if (getSpeed(true) == 0 || isKeyPressed((Player) getDriver(), ButtonType.FORWARD))
            return;

        AirplaneEntity entity = (AirplaneEntity) ((CraftEntity) getEntity()).getHandle();
        Location location = entity.getBukkitEntity().getLocation();
        Vector direction = location.getDirection();

        if (entity.isOnGround()) { //we are on ground
            setSpeed(getSpeed() * getModel().getSpeed().getSmooth());
            entity.getBukkitEntity().setRotation(location.getYaw(), 0);
            applyVelocity(direction, -1);
            return;
        }

        // airplane falling
        FallSpeedInfo info = getModel().getFallSpeed();
        if (location.getPitch() < info.getPitch()) { //we are not in the right pitch yet
            if (!isKeyPressed((Player) getDriver(), ButtonType.BACKWARD)) //player is not trying to control the airplane
                setSpeed(Math.min(getSpeed() * info.getSmoothSpeed(), info.getMaxSpeed()));

            if (!isKeyPressed((Player) getDriver(), ButtonType.UP)) { //player is not trying to control the airplane
                location.setPitch(Math.min(location.getPitch() + info.getSmoothPitch(), info.getPitch()));
                entity.getBukkitEntity().setRotation(location.getYaw(), location.getPitch());
            }
        } else if (!isKeyPressed((Player) getDriver(), ButtonType.BACKWARD)) //right pitch and the player is not trying to control the airplane
            setSpeed(Math.min(getSpeed() * info.getSpeed(), info.getMaxSpeed()));

        applyVelocity(direction);
    }

    @Override
    public void onBlockCollide() {
        if (getSpeed(true) <= 0)
            return;

        Entity entity = getEntity();
        setSpeed(0);
        entity.setRotation(entity.getLocation().getYaw(), 0);
    }
}