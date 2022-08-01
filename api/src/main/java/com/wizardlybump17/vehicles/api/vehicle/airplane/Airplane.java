package com.wizardlybump17.vehicles.api.vehicle.airplane;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.wizardlybump17.vehicles.api.ButtonType;
import com.wizardlybump17.vehicles.api.entity.AirplaneEntity;
import com.wizardlybump17.vehicles.api.model.airplane.AirplaneModel;
import com.wizardlybump17.vehicles.api.model.info.airplane.FallSpeedInfo;
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

    public static final long DAMAGE_DELAY = 1000;

    private final Map<Entity, Long> damagedEntities = new HashMap<>();
    private boolean flying;

    public Airplane(AirplaneModel model, String plate, ActiveModel megModel) {
        super(model, plate, megModel);
    }

    @Override
    public void move(Player player, double xxa, double zza) {
        if (zza == 0 || !player.equals(getDriver()))
            return;

        Vector direction = getEntity().getLocation().getDirection();

        if (zza > 0 && getSpeed() > getModel().getMaxSpeed()) {
            applyVelocity(direction);
            return;
        }

        if (zza < 0)
            setSpeed(getSpeed() / getModel().getBreakForce(getSpeed()));
        else
            setSpeed(Math.min(getSpeed() + getModel().getAcceleration(getSpeed()), getModel().getMaxSpeed()));

        applyVelocity(direction);
    }

    private void applyVelocity(Vector vector) {
        Entity entity = getEntity();
        vector.multiply(getSpeed());
        entity.setVelocity(vector);
    }

    @Override
    public void rotate(Player player, double xxa, double zza) {
        if (getSpeed(true) == 0 || xxa == 0 || !player.equals(getDriver()))
            return;

        AirplaneEntity entity = (AirplaneEntity) ((CraftEntity) getEntity()).getHandle();

        float yaw = entity.getBukkitYaw();
        yaw += xxa > 0 ? -getModel().getRotationSpeed() : getModel().getRotationSpeed();
        entity.getBukkitEntity().setRotation(yaw, entity.getBukkitEntity().getLocation().getPitch());
    }

    @Override
    public void jump(Player player, double xxa, double zza) {
        if (getSpeed(true) == 0 || !player.equals(getDriver()))
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
        if (getSpeed(true) == 0 || !player.equals(getDriver()))
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
        if (getSpeed() == 0 || !(entity instanceof LivingEntity living) || damagedEntities.getOrDefault(entity, System.currentTimeMillis()) > System.currentTimeMillis())
            return;

        living.damage(getModel().getDamage(getSpeed()), getEntity());
        damagedEntities.put(entity, System.currentTimeMillis() + DAMAGE_DELAY);
    }

    @Override
    public void check() {
        if (getSpeed(true) == 0 || isKeyPressed((Player) getDriver(), ButtonType.FORWARD))
            return;

        AirplaneEntity entity = (AirplaneEntity) ((CraftEntity) getEntity()).getHandle();
        Location location = entity.getBukkitEntity().getLocation();
        Vector direction;

        if (location.getBlock().getRelative(BlockFace.DOWN).isPassable()) {
            flying = true;
            FallSpeedInfo info = getModel().getFallSpeed();
            if (location.getPitch() < info.getPitch()) {
                if (!isKeyPressed((Player) getDriver(), ButtonType.BACKWARD))
                    setSpeed(Math.min(getSpeed() * info.getSmoothSpeed(), info.getMaxSpeed()));

                if (!isKeyPressed((Player) getDriver(), ButtonType.UP)) {
                    location.setPitch(Math.min(location.getPitch() + info.getSmoothPitch(), info.getPitch()));
                    entity.getBukkitEntity().setRotation(location.getYaw(), location.getPitch());
                }
            } else if (!isKeyPressed((Player) getDriver(), ButtonType.BACKWARD))
                setSpeed(Math.min(getSpeed() * info.getSpeed(), info.getMaxSpeed()));
            direction = location.getDirection().multiply(getSpeed());
        } else {
            flying = false;
            setSpeed(getSpeed() * getModel().getSmoothSpeed());
            direction = location.getDirection().multiply(getSpeed()).setY(-1);
            entity.getBukkitEntity().setRotation(location.getYaw(), 0);
        }

        entity.getBukkitEntity().setVelocity(direction);
    }

    @Override
    public void onBlockCollide() {
        if (getSpeed(true) <= 0)
            return;

        Entity entity = getEntity();
        setSpeed(0);
        entity.setRotation(entity.getLocation().getYaw(), 0);
        flying = false;
    }
}