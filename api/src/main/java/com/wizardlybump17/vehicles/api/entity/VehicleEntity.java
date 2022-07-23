package com.wizardlybump17.vehicles.api.entity;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.wizardlybump17.vehicles.api.vehicle.Vehicle;
import lombok.Getter;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.EntityCow;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;

@Getter
public class VehicleEntity extends EntityCow {

    private final Vehicle<?> handle;

    public VehicleEntity(Location location, Vehicle<?> vehicle) {
        super(EntityTypes.n, ((CraftWorld) location.getWorld()).getHandle());
        setPosition(location.getX(), location.getY(), location.getZ());
        handle = vehicle;
    }

    @Override
    protected void initPathfinder() {
    }

    @Override
    public boolean isSilent() {
        return true;
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        return false;
    }

    @Override
    protected boolean damageEntity0(DamageSource damagesource, float f) {
        return false;
    }

    @Override
    public void setHeadRotation(float f) {
        super.setHeadRotation(f);
        updateYaw();
    }

    @Override
    public void setYRot(float f) {
        super.setYRot(f);
        updateYaw();
    }

    @Override
    protected void setYawPitch(float f, float f1) {
        super.setYawPitch(f, f1);
        updateYaw();
    }

    public void updateYaw() {
        if (handle != null)
            for (ActiveModel model : handle.getMegModel().getModeledEntity().getAllActiveModel().values())
                model.setClamp(getXRot());
    }
}
