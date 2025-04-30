package com.villager.entity;

import com.villager.entity.goal.GreatHogRangedAttackGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.*;

public class MalgoshaEntity extends HostileEntity implements GeoEntity{

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public MalgoshaEntity(EntityType<? extends HostileEntity> entityType, World world) {
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
        this.goalSelector.add(3, new GreatHogRangedAttackGoal(this, 200, 15.0f));
        this.goalSelector.add(4, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(6, new LookAroundGoal(this));

        this.targetSelector.add(1, new RevengeGoal(this));
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
        controllers.add(new AnimationController<>(this, "main_controller", 10, this::predicate));

        controllers.add(
                new AnimationController<>(this, "attack_controller", state -> PlayState.STOP)
                        .triggerableAnim("attack1", RawAnimation.begin().then("animation.villager.malgosha.staff_melee1", Animation.LoopType.PLAY_ONCE))
                        .triggerableAnim("attack2", RawAnimation.begin().then("animation.villager.malgosha.staff_melee2", Animation.LoopType.PLAY_ONCE))
                        .triggerableAnim("attack3", RawAnimation.begin().then("animation.villager.malgosha.staff_melee3", Animation.LoopType.PLAY_ONCE))
                        .triggerableAnim("attack4", RawAnimation.begin().then("animation.villager.malgosha.staff_melee4", Animation.LoopType.PLAY_ONCE))
        );
    }


    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> state) {
        if (state.isMoving()) {
            state.getController().setAnimation(RawAnimation.begin().thenLoop("animation.villager.malgosha.walk"));
            return PlayState.CONTINUE;
        }

        state.getController().setAnimation(RawAnimation.begin().thenLoop("animation.villager.malgosha.idle"));
        return PlayState.CONTINUE;
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