package com.villager.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;

public class SpearEntity extends PersistentProjectileEntity implements GeoEntity {

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public SpearEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getOwner() instanceof LivingEntity owner) {
            Vec3d ownerPos = owner.getPos();
            Vec3d spearPos = this.getPos();

            double deltaX = ownerPos.x - spearPos.x;
            double deltaY = ownerPos.y - spearPos.y;
            double deltaZ = ownerPos.z - spearPos.z;

            double yaw = MathHelper.atan2(deltaZ, deltaX) * (180 / Math.PI);
            double pitch = MathHelper.atan2(deltaY, Math.sqrt(deltaX * deltaX + deltaZ * deltaZ)) * (180 / Math.PI);

            this.setYaw((float) yaw);
            this.setPitch((float) pitch);
        }
    }

    @Override
    public ItemStack getDefaultItemStack() {
        return new ItemStack(Items.TRIDENT);
    }
}