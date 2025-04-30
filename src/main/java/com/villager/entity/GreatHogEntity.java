package com.villager.entity;

import com.villager.entity.goal.GreatHogRangedAttackGoal;
import com.villager.network.payload.ShootFireballC2SPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
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

public class GreatHogEntity extends HostileEntity implements GeoEntity, RangedAttackMob {

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public GreatHogEntity(EntityType<? extends HostileEntity> entityType, World world) {
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
        this.goalSelector.add(3, new GreatHogRangedAttackGoal(this, 40, 13.0f));
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
        AnimationController<GreatHogEntity> mainController = new AnimationController<>(this, "main_controller", 10, this::mainPredicate);
        controllers.add(mainController);

        AnimationController<GreatHogEntity> attackController = new AnimationController<>(this, "attack_controller", state -> PlayState.STOP)
                .triggerableAnim("attack1", RawAnimation.begin().then("animation.villager.great_hog.shoot_fireball1", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("attack2", RawAnimation.begin().then("animation.villager.great_hog.shoot_fireball2", Animation.LoopType.PLAY_ONCE))
                .setCustomInstructionKeyframeHandler(event -> {
                    String instruction = event.getKeyframeData().getInstructions();
                    if ("shoot_fireball;".equals(instruction)) {
                        ClientPlayNetworking.send(new ShootFireballC2SPayload(this.getId(), 1f));
                    }
                });

        controllers.add(attackController);
    }

    private <T extends GeoAnimatable> PlayState mainPredicate(AnimationState<T> state) {
        Vec3d vel = this.getVelocity();
        double speedSq = vel.horizontalLengthSquared();
        AnimationController<?> controller = state.getController();

        if (speedSq > 0.003) {
            controller.setAnimation(RawAnimation.begin().thenLoop("animation.villager.great_hog.run"));
        } else if (speedSq > 1e-6) {
            controller.setAnimation(RawAnimation.begin().thenLoop("animation.villager.great_hog.walk"));
        } else {
            controller.setAnimation(RawAnimation.begin().thenLoop("animation.villager.great_hog.idle"));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void shootAt(LivingEntity target, float pullProgress) {
        if (!this.getWorld().isClient) {

            double dx = target.getX() - this.getX();
            double dz = target.getZ() - this.getZ();
            this.setYaw((float)(Math.toDegrees(Math.atan2(dz, dx)) - 90.0F));
            this.bodyYaw = this.getYaw();
            this.headYaw = this.getYaw();

            FireballSmallEntity fireball = ModEntities.FIREBALL_SMALL.create(this.getWorld());
            if (fireball != null) {
                Vec3d blasterPos = this.getBlasterWorldPos();
                if (blasterPos == null || blasterPos.equals(Vec3d.ZERO)) {
                    return;
                }

                fireball.setPosition(blasterPos.x, blasterPos.y, blasterPos.z);

                double targetX = target.getX();
                double targetY = target.getBodyY(0.4);
                double targetZ = target.getZ();

                double dX = targetX - blasterPos.x;
                double dY = targetY - blasterPos.y;
                double dZ = targetZ - blasterPos.z;

                double length = Math.sqrt(dX * dX + dY * dY + dZ * dZ);
                double speed = 2;
                if (length != 0) {
                    dX = dX / length * speed;
                    dY = dY / length * speed;
                    dZ = dZ / length * speed;
                }

                fireball.setVelocity(dX, dY, dZ);
                fireball.setOwner(this);
                fireball.setTarget(target);
                this.getWorld().spawnEntity(fireball);
            }
        }
    }

    private Vec3d blasterWorldPos = Vec3d.ZERO;

    public void setBlasterWorldPos(Vec3d pos) {
        this.blasterWorldPos = pos;
    }

    public Vec3d getBlasterWorldPos() {
        return this.blasterWorldPos;
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