package com.wizardlybump17.vehicles.api.vehicle.airplane;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.wizardlybump17.vehicles.api.entity.AirplaneEntity;
import com.wizardlybump17.vehicles.api.model.airplane.AirplaneModel;
import com.wizardlybump17.vehicles.api.vehicle.Vehicle;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class Airplane extends Vehicle<AirplaneModel> {

    public static final long SPEED_TIMEOUT = 100;
    public static final long PITCH_TIMEOUT = 100;
    public static final long DAMAGE_DELAY = 1000;

    private long lastSpeedUpdate;
    private long lastPitchUpdate;
    private final Map<Entity, Long> damagedEntities = new HashMap<>();

    public Airplane(AirplaneModel model, String plate, ActiveModel megModel) {
        super(model, plate, megModel);
    }

    @Override
    public void move(Player player, double xxa, double zza) {
        if (zza <= 0 || !player.equals(getDriver()))
            return;

        Entity entity = getEntity();

        lastSpeedUpdate = System.currentTimeMillis();

        setSpeed(Math.min(getModel().getMaxSpeed(), getSpeed() + getModel().getAcceleration(getSpeed())));

        Location location = entity.getLocation();
        if (System.currentTimeMillis() - lastPitchUpdate > PITCH_TIMEOUT)
            location.setPitch(0);

        Vector direction = location.getDirection();
        direction.multiply(getSpeed());

        entity.setVelocity(direction);
    }

    @Override
    public void rotate(Player player, double xxa, double zza) {
        if (getSpeed(true) == 0 || xxa == 0 || !player.equals(getDriver()))
            return;

        AirplaneEntity entity = (AirplaneEntity) ((CraftEntity) getEntity()).getHandle();

        float yaw = entity.getBukkitYaw();
        yaw += xxa > 0 ? -getModel().getRotationSpeed() : getModel().getRotationSpeed();
        entity.getBukkitEntity().setRotation(yaw, System.currentTimeMillis() - lastPitchUpdate > PITCH_TIMEOUT ? 0 : entity.getBukkitEntity().getLocation().getPitch());
    }

    @Override
    public void jump(Player player, double xxa, double zza) {
        if (getSpeed(true) == 0 || !player.equals(getDriver()))
            return;

        AirplaneEntity entity = (AirplaneEntity) ((CraftEntity) getEntity()).getHandle();
        Location location = entity.getBukkitEntity().getLocation();

        float pitch = location.getPitch();
        if (System.currentTimeMillis() - lastPitchUpdate > PITCH_TIMEOUT)
            pitch = 0;

        pitch -= getModel().getPitchSpeed();
        pitch = Math.max(getModel().getMinPitch(), pitch);
        entity.getBukkitEntity().setRotation(location.getYaw(), pitch);

        lastPitchUpdate = System.currentTimeMillis();
    }

    @Override
    public void shift(Player player, double xxa, double zza) {
        if (getSpeed(true) == 0 || !player.equals(getDriver()))
            return;

        AirplaneEntity entity = (AirplaneEntity) ((CraftEntity) getEntity()).getHandle();
        Location location = entity.getBukkitEntity().getLocation();

        float pitch = location.getPitch();
        if (System.currentTimeMillis() - lastPitchUpdate > PITCH_TIMEOUT)
            pitch = 0;

        pitch += getModel().getPitchSpeed();
        pitch = Math.min(getModel().getMaxPitch(), pitch);
        entity.getBukkitEntity().setRotation(location.getYaw(), pitch);

        lastPitchUpdate = System.currentTimeMillis();
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
        if (getSpeed() == 0 || !(entity instanceof LivingEntity living) || damagedEntities.getOrDefault(entity, System.currentTimeMillis()) > System.currentTimeMillis() || System.currentTimeMillis() - lastSpeedUpdate > DAMAGE_DELAY)
            return;

        living.damage(getModel().getDamage(getSpeed()), getEntity());
        damagedEntities.put(entity, System.currentTimeMillis() + DAMAGE_DELAY);
    }

    @Override
    public void check() {
        if (getSpeed(true) == 0 || System.currentTimeMillis() - lastSpeedUpdate < getModel().getSpeedTimeout())
            return;

        AirplaneEntity entity = (AirplaneEntity) ((CraftEntity) getEntity()).getHandle();
        Location location = entity.getBukkitEntity().getLocation();
        Vector direction;

        if (location.getBlock().getRelative(BlockFace.DOWN).isPassable()) {
            setSpeed(getSpeed() * getModel().getFallSpeed());
            location.setPitch(getModel().getFallPitch());
            direction = location.getDirection().multiply(getSpeed());
            entity.getBukkitEntity().setRotation(location.getYaw(), getModel().getFallPitch());
        } else {
            setSpeed(getSpeed() * getModel().getSmoothSpeed());
            direction = location.getDirection().multiply(getSpeed()).setY(-1);
            entity.getBukkitEntity().setRotation(location.getYaw(), 0);
        }

        entity.getBukkitEntity().setVelocity(direction);

        lastSpeedUpdate = System.currentTimeMillis();
    }
}
