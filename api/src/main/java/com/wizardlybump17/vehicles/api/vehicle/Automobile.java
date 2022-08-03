package com.wizardlybump17.vehicles.api.vehicle;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.wizardlybump17.vehicles.api.ButtonType;
import com.wizardlybump17.vehicles.api.entity.AutomobileEntity;
import com.wizardlybump17.vehicles.api.info.DamageInfo;
import com.wizardlybump17.vehicles.api.info.SpeedInfo;
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

    private final Map<Entity, Long> damagedEntities = new HashMap<>();

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

        Location location = getEntity().getLocation();
        location.setPitch(0);

        Vector direction = location.getDirection();
        SpeedInfo speedInfo = getModel().getSpeed();

        if (zza > 0 && getSpeed() > speedInfo.getMax()) {
            applyVelocity(direction);
            return;
        }

        double speed = getSpeed();

        if (zza < 0) {
            if (getSpeed(true) > 0)
                speed /= speedInfo.getBreakForce(speed);
            else
                speed -= speedInfo.getBreakAcceleration(-speed); // B) :D

            setSpeed(Math.max(speed, speedInfo.getMin()));
        } else {
            speed += speedInfo.getAcceleration(speed);
            setSpeed(Math.min(speed, speedInfo.getMax()));
        }

        applyVelocity(direction, -1);
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
        if (getSpeed(true) == 0 || !(entity instanceof LivingEntity living) || damagedEntities.getOrDefault(entity, System.currentTimeMillis()) > System.currentTimeMillis())
            return;

        DamageInfo damageInfo = getModel().getDamage();
        living.damage(damageInfo.getDamage(getSpeed()), getEntity());
        damagedEntities.put(entity, System.currentTimeMillis() + damageInfo.getDelay());
    }

    @Override
    public void check() {
        super.check();

        if (getSpeed(true) == 0 || isKeyPressed((Player) getDriver(), ButtonType.FORWARD) || isKeyPressed((Player) getDriver(), ButtonType.BACKWARD))
            return;

        SpeedInfo speedInfo = getModel().getSpeed();
        setSpeed(getSpeed() * speedInfo.getSmooth());

        Entity entity = getEntity();

        Location location = entity.getLocation();
        location.setPitch(0);

        Vector direction = location.getDirection().multiply(getSpeed());
        entity.setVelocity(direction.setY(-1));
    }
}
