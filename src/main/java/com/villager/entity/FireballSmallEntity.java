package com.villager.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;

public class FireballSmallEntity extends PersistentProjectileEntity implements GeoEntity {

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public FireballSmallEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }


    private Entity target;

    public void setTarget(Entity target) {
        this.target = target;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient && target != null && target.isAlive()) {
            Vec3d currentPos = this.getPos();
            Vec3d targetPos = target.getPos().add(0, target.getStandingEyeHeight() * 0.5, 0);
            Vec3d direction = targetPos.subtract(currentPos).normalize();
            double speed = 0.7;

            this.setVelocity(direction.multiply(speed));
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);

        if (this.getWorld().isClient) return;

        if (hitResult instanceof EntityHitResult entityHit) {
            Entity hitEntity = entityHit.getEntity();
            if (hitEntity instanceof PlayerEntity) {
                explode();
            }
        }
    }

    private void explode() {
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();

        this.getWorld().createExplosion(
                this,
                x, y, z,
                0.5f,
                true,
                World.ExplosionSourceType.TNT
        );

        this.discard();
    }

    @Override
    public ItemStack getDefaultItemStack() {
        return new ItemStack(Items.TRIDENT);
    }
}