package com.villager.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.*;

import java.util.Optional;
import java.util.UUID;

public class ExclamationEntity extends Entity implements GeoEntity {

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    private static final TrackedData<Optional<UUID>> OWNER_UUID =
            DataTracker.registerData(ExclamationEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);

    private static final TrackedData<Boolean> IS_RED =
            DataTracker.registerData(ExclamationEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public ExclamationEntity(EntityType<? extends Entity> entityType, World world) {
        super(entityType, world);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(OWNER_UUID, Optional.empty());
        builder.add(IS_RED, false);
    }

    public void setRed(boolean red) {
        this.dataTracker.set(IS_RED, red);
    }

    public boolean isRed() {
        return this.dataTracker.get(IS_RED);
    }

    public void setOwner(Entity owner) {
        this.dataTracker.set(OWNER_UUID, Optional.of(owner.getUuid()));
    }

    public UUID getOwnerUuid() {
        return this.dataTracker.get(OWNER_UUID).orElse(null);
    }


    public Entity getOwner() {
        UUID uuid = getOwnerUuid();
        if (uuid == null) return null;
        if (!(this.getWorld() instanceof ServerWorld serverWorld)) return null;
        return serverWorld.getEntity(uuid);
    }

    @Override
    public void tick() {
        super.tick();
        Entity owner = getOwner();

        if (owner != null) {
            Vec3d targetPos = owner.getPos().add(0, 2.5, 0);
            this.setPos(targetPos.x, targetPos.y, targetPos.z);
        }
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.containsUuid("Owner")) {
            dataTracker.set(OWNER_UUID, Optional.of(nbt.getUuid("Owner")));
        }
        dataTracker.set(IS_RED, nbt.getBoolean("IsRed"));
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        UUID owner = getOwnerUuid();
        if (owner != null) {
            nbt.putUuid("Owner", owner);
        }
        nbt.putBoolean("IsRed", isRed());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, event -> {
            event.setAndContinue(RawAnimation.begin().then("animation.villager.exclamation.spin", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }));
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
