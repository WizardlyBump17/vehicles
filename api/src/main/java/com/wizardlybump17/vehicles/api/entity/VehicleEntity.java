package com.wizardlybump17.vehicles.api.entity;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.wizardlybump17.vehicles.api.vehicle.Vehicle;
import com.wizardlybump17.vehicles.util.ReflectionUtil;
import lombok.Getter;
import net.minecraft.core.BlockPosition;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StreamAccumulator;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.animal.EntityCow;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.OperatorBoolean;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.util.Vector;

import java.util.stream.Stream;

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

    public void updateYaw() {
        if (handle != null)
            for (ActiveModel model : handle.getMegModel().getModeledEntity().getAllActiveModel().values())
                model.setClamp(getXRot());
    }

    @Override
    protected void collideNearby() {
    }

    @Override
    public boolean isCollidable() {
        return false;
    }

    @Override
    public void collide(Entity entity) {
    }

    @Override
    public boolean canCollideWithBukkit(Entity entity) {
        return false;
    }

    @Override
    public void move(EnumMoveType enummovetype, Vec3D vec3d) {
        if (this.P) {
            this.setPosition(this.locX() + vec3d.b, this.locY() + vec3d.c, this.locZ() + vec3d.d);
            return;
        }

        this.an = this.isBurning();
        if (enummovetype == EnumMoveType.c) {
            vec3d = this.c(vec3d);
            if (vec3d.equals(Vec3D.a))
                return;
        }

        this.t.getMethodProfiler().enter("move");
        if (this.D.g() > 1.0E-7) {
            vec3d = vec3d.h(this.D);
            this.D = Vec3D.a;
            this.setMot(Vec3D.a);
        }

        vec3d = this.a(vec3d, enummovetype);
        Vec3D vec3d1 = this.g1(vec3d);
        if (vec3d1.g() > 1.0E-7)
            this.setPosition(this.locX() + vec3d1.b, this.locY() + vec3d1.c, this.locZ() + vec3d1.d);

        this.t.getMethodProfiler().exit();
        this.t.getMethodProfiler().enter("rest");
        this.A = !MathHelper.b(vec3d.b, vec3d1.b) || !MathHelper.b(vec3d.d, vec3d1.d);
        this.B = vec3d.c != vec3d1.c;
        this.z = this.B && vec3d.c < 0.0;
        BlockPosition blockposition = this.av();
        IBlockData iblockdata = this.t.getType(blockposition);
        this.a(vec3d1.c, this.z, iblockdata, blockposition);
        if (this.isRemoved()) {
            this.t.getMethodProfiler().exit();
        } else {
            Vec3D vec3d2 = this.getMot();
            if (vec3d.b != vec3d1.b)
                this.setMot(0.0, vec3d2.c, vec3d2.d);

            if (vec3d.d != vec3d1.d)
                this.setMot(vec3d2.b, vec3d2.c, 0.0);

            net.minecraft.world.level.block.Block block = iblockdata.getBlock();
            if (vec3d.c != vec3d1.c)
                block.a(this.t, this);

            if (this.A && this.getBukkitEntity() instanceof org.bukkit.entity.Vehicle vehicle) {
                Block bl = this.t.getWorld().getBlockAt(MathHelper.floor(this.locX()), MathHelper.floor(this.locY()), MathHelper.floor(this.locZ()));
                if (vec3d.b > vec3d1.b) {
                    bl = bl.getRelative(BlockFace.EAST);
                } else if (vec3d.b < vec3d1.b) {
                    bl = bl.getRelative(BlockFace.WEST);
                } else if (vec3d.d > vec3d1.d) {
                    bl = bl.getRelative(BlockFace.SOUTH);
                } else if (vec3d.d < vec3d1.d) {
                    bl = bl.getRelative(BlockFace.NORTH);
                }

                if (!bl.getType().isAir()) {
                    VehicleBlockCollisionEvent event = new VehicleBlockCollisionEvent(vehicle, bl);
                    this.t.getCraftServer().getPluginManager().callEvent(event);
                }
            }

            if (this.z && !this.bE())
                block.stepOn(this.t, blockposition, iblockdata, this);

            MovementEmission entity_movementemission = this.aI();
            if (entity_movementemission.a() && !this.isPassenger()) {
                double d0 = vec3d1.b;
                double d1 = vec3d1.c;
                double d2 = vec3d1.d;
                this.J = (float) (this.J + vec3d1.f() * 0.6);
                if (!iblockdata.a(TagsBlock.aC) && !iblockdata.a(Blocks.oO))
                    d1 = 0.0;

                this.H += (float) vec3d1.h() * 0.6F;
                this.I += (float) Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 0.6F;
                if (this.I > ReflectionUtil.<Float>getField(Entity.class, this, "aC") && !iblockdata.isAir()) {
                    ReflectionUtil.setField(Entity.class, this, "aC", this.az());
                    if (!this.isInWater()) {
                        if (entity_movementemission.c()) {
                            this.b(iblockdata);
                            this.b(blockposition, iblockdata);
                        }

                        if (entity_movementemission.b() && !iblockdata.a(TagsBlock.aY))
                            this.a(GameEvent.Q);
                    } else {
                        if (entity_movementemission.c()) {
                            Entity entity = this.isVehicle() && this.getRidingPassenger() != null ? this.getRidingPassenger() : this;
                            float f = entity == this ? 0.35F : 0.4F;
                            Vec3D vec3d3 = entity.getMot();
                            float f1 = Math.min(1.0F, (float) Math.sqrt(vec3d3.b * vec3d3.b * 0.20000000298023224 + vec3d3.c * vec3d3.c + vec3d3.d * vec3d3.d * 0.20000000298023224) * f);
                            this.d(f1);
                        }

                        if (entity_movementemission.b())
                            this.a(GameEvent.R);
                    }
                } else if (iblockdata.isAir())
                    this.au();
            }

            this.as();
            float f2 = this.getBlockSpeedFactor();
            this.setMot(this.getMot().d(f2, 1.0, f2));

            this.t.getMethodProfiler().exit();
        }
    }

    private Vec3D g1(Vec3D vec3d) {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        VoxelShapeCollision voxelshapecollision = VoxelShapeCollision.a(this);
        VoxelShape voxelshape = this.t.getWorldBorder().c();
        Stream<VoxelShape> stream = VoxelShapes.c(voxelshape, VoxelShapes.a(axisalignedbb.shrink(1.0E-7)), OperatorBoolean.i) ? Stream.empty() : Stream.of(voxelshape);
        Stream<VoxelShape> stream1 = this.t.c(this, axisalignedbb.b(vec3d), entity -> true);
        StreamAccumulator<VoxelShape> streamaccumulator = new StreamAccumulator<>(Stream.concat(stream1, stream));
        Vec3D vec3d1 = vec3d.g() == 0.0 ? vec3d : a(this, vec3d, axisalignedbb, this.t, voxelshapecollision, streamaccumulator);
        boolean flag = vec3d.b != vec3d1.b;
        boolean flag1 = vec3d.c != vec3d1.c;
        boolean flag2 = vec3d.d != vec3d1.d;
        boolean flag3 = this.z || flag1 && vec3d.c < 0.0;
        if (this.O > 0.0F && flag3 && (flag || flag2)) {
            Vec3D vec3d2 = a(this, new Vec3D(vec3d.b, this.O, vec3d.d), axisalignedbb, this.t, voxelshapecollision, streamaccumulator);
            Vec3D vec3d3 = a(this, new Vec3D(0.0, this.O, 0.0), axisalignedbb.b(vec3d.b, 0.0, vec3d.d), this.t, voxelshapecollision, streamaccumulator);
            if (vec3d3.c < this.O) {
                Vec3D vec3d4 = a(this, new Vec3D(vec3d.b, 0.0, vec3d.d), axisalignedbb.c(vec3d3), this.t, voxelshapecollision, streamaccumulator).e(vec3d3);
                if (vec3d4.i() > vec3d2.i())
                    vec3d2 = vec3d4;
            }

            if (vec3d2.i() > vec3d1.i())
                return vec3d2.e(a(this, new Vec3D(0.0, -vec3d2.c + vec3d.c, 0.0), axisalignedbb.c(vec3d2), this.t, voxelshapecollision, streamaccumulator));
        }

        return vec3d1;
    }

    public boolean collide(Vector velocity) {
        Vec3D vec3d = new Vec3D(velocity.getX(), velocity.getY(), velocity.getZ());

        if (this.D.g() > 1.0E-7) {
            vec3d = vec3d.h(this.D);
            this.D = Vec3D.a;
            this.setMot(Vec3D.a);
        }

        return this.g1(this.a(vec3d, EnumMoveType.a)).g() > 1.0E-7;
    }
}
