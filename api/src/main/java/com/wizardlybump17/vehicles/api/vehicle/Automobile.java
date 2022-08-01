package com.wizardlybump17.vehicles.api.vehicle;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.wizardlybump17.vehicles.api.entity.AutomobileEntity;
import com.wizardlybump17.vehicles.api.model.AutomobileModel;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public abstract class Automobile<M extends AutomobileModel<?>> extends Vehicle<M> {

    private long lastSpeedUpdate;
    private final Map<Entity, Long> damagedEntities = new HashMap<>();
    private boolean reverse = false;

    protected Automobile(M model, String plate, ActiveModel megModel) {
        super(model, plate, megModel);
    }

    @Override
    public void move(Player player, double xxa, double zza) {
        if (zza == 0 || !player.equals(getDriver()))
            return;

        Entity entity = getEntity();
        if (entity.getFallDistance() != 0)
            return;

        if (System.currentTimeMillis() - lastSpeedUpdate > getModel().getSpeedTimeout())
            setSpeed(0);

        lastSpeedUpdate = System.currentTimeMillis();

        setSpeed(Math.min(getModel().getMaxSpeed(), getSpeed() + getModel().getAcceleration(getSpeed())));

        Location location = entity.getLocation();
        location.setPitch(0);
        Vector direction = location.getDirection();
        direction.multiply(zza > 0 ? getSpeed() : -getSpeed() / getModel().getBreakForce(getSpeed()));

        entity.setVelocity(direction.setY(-1));

        reverse = zza < 0;
    }

    @Override
    public void rotate(Player player, double xxa, double zza) {
        if (xxa == 0 || getSpeed(true) == 0 || !player.equals(getDriver()))
            return;

        AutomobileEntity entity = (AutomobileEntity) ((CraftEntity) getEntity()).getHandle();

        if (entity.K != 0)
            return;

        float yaw = entity.getYRot();
        yaw += xxa > 0 ? -getModel().getRotationSpeed() : getModel().getRotationSpeed();
        entity.getBukkitEntity().setRotation(yaw, 0);
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

    @Override
    public void onCollide(Entity entity) {
        if (getSpeed(true) == 0 || !(entity instanceof LivingEntity living) || damagedEntities.getOrDefault(entity, System.currentTimeMillis()) > System.currentTimeMillis() || System.currentTimeMillis() - lastSpeedUpdate > getModel().getDamageDelay())
            return;

        living.damage(getModel().getDamage(getSpeed()), getEntity());
        damagedEntities.put(entity, System.currentTimeMillis() + getModel().getDamageDelay());
    }

    @Override
    public void check() {
        if (getSpeed(true) == 0 || System.currentTimeMillis() - lastSpeedUpdate < getModel().getSpeedTimeout())
            return;

        Entity entity = getEntity();

        Location location = entity.getLocation();
        location.setPitch(0);
        Vector direction;
        if (reverse)
            direction = location.getDirection().multiply(-getSpeed() * getModel().getSmoothSpeed() / getModel().getBreakForce(getSpeed()));
        else
            direction = location.getDirection().multiply(getSpeed() * getModel().getSmoothSpeed());

        entity.setVelocity(direction.setY(-1));

        setSpeed(getSpeed() * getModel().getSmoothSpeed());

        lastSpeedUpdate = System.currentTimeMillis();
    }
}
