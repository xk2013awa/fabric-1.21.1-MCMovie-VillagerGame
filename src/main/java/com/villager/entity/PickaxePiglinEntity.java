package com.villager.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.*;

public class PickaxePiglinEntity extends HostileEntity implements GeoEntity{

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public PickaxePiglinEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(4, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(6, new LookAroundGoal(this));

        this.goalSelector.add(3, new MeleeAttackGoal(this, 1.2, true));

        this.targetSelector.add(1, (new RevengeGoal(this)));

        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    public static DefaultAttributeContainer.Builder createMobAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.23000000417232513)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0)
                .add(EntityAttributes.GENERIC_ARMOR, 2.0);
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main_controller", 10, this::mainPredicate));

        controllers.add(
                new AnimationController<>(this, "attack_controller", state -> PlayState.STOP)
                        .triggerableAnim("attack1", RawAnimation.begin().then("animation.villager.pickaxe_piglin.melee_attack1", Animation.LoopType.PLAY_ONCE))
                        .triggerableAnim("attack2", RawAnimation.begin().then("animation.villager.pickaxe_piglin.melee_attack2", Animation.LoopType.PLAY_ONCE))
        );
    }


    private <T extends GeoAnimatable> PlayState mainPredicate(AnimationState<T> state) {
        PickaxePiglinEntity self = (PickaxePiglinEntity) state.getAnimatable();
        Vec3d v = self.getVelocity();
        double speed = v.horizontalLengthSquared();

        var ctrl = state.getController();
        if (speed > 0.003) {
            ctrl.setAnimation(RawAnimation.begin().thenLoop("animation.villager.pickaxe_piglin.run"));
        } else if (speed > 1e-6) {
            ctrl.setAnimation(RawAnimation.begin().thenLoop("animation.villager.pickaxe_piglin.walk"));
        } else {
            ctrl.setAnimation(RawAnimation.begin().thenLoop("animation.villager.pickaxe_piglin.idle"));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public boolean tryAttack(Entity target) {
        boolean success = super.tryAttack(target);

        if (success && !this.getWorld().isClient) {
            int index = this.random.nextInt(2) + 1;
            this.triggerAnim("attack_controller", "attack" + index);
        }

        return success;
    }

    @Override
    public boolean canTarget(LivingEntity target) {
        if (target.getType() == ModEntities.GREAT_HOG
                || target.getType() == ModEntities.SPEAR_PIGLIN
                || target.getType() == ModEntities.CHEF_PIGLIN
                || target.getType() == ModEntities.DRUM_PIGLIN
                || target.getType() == ModEntities.WARRIOR_PIGLIN
                || target.getType() == ModEntities.PICKAXE_PIGLIN
                || target.getType() == ModEntities.HAMMER_PIGLIN
                || target.getType() == ModEntities.PIGLIN_GENERAL
                || target.getType() == ModEntities.MALGOSHA) {
            return false;
        }
        return super.canTarget(target);
    }
}