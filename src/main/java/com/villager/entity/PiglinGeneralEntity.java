package com.villager.entity;

import com.villager.entity.goal.PiglinGeneralMaceSpinGoal;
import com.villager.network.payload.GeneralSpinAttackC2SPayload;
import com.villager.network.payload.GeneralSpinDoneC2SPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
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

public class PiglinGeneralEntity extends HostileEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private boolean isSpinning = false;

    public PiglinGeneralEntity(EntityType<? extends HostileEntity> type, World world) {
        super(type, world);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    public boolean isSpinning() {
        return isSpinning;
    }

    public void setSpinning(boolean flag) {
        isSpinning = flag;
    }

    public static DefaultAttributeContainer.Builder createMobAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0)
                .add(EntityAttributes.GENERIC_ARMOR, 2.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.goalSelector.add(2, new PiglinGeneralMaceSpinGoal(this, 2.0));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.25f));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 10, this::mainPredicate));
        controllers.add(new AnimationController<>(this, "spin_controller", 0, state -> {
            return PlayState.STOP;
        })
                .triggerableAnim("mace_spin_pre",
                        RawAnimation.begin()
                                .then("animation.villager.piglin_general.mace_spin_pre", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("mace_spin_loop",
                        RawAnimation.begin()
                                .thenLoop("animation.villager.piglin_general.mace_spin_loop"))
                .triggerableAnim("mace_spin_post",
                        RawAnimation.begin()
                                .then("animation.villager.piglin_general.mace_spin_post", Animation.LoopType.PLAY_ONCE))
                .setCustomInstructionKeyframeHandler(event -> {
                    PiglinGeneralEntity self = event.getAnimatable();
                    String instr = event.getKeyframeData().getInstructions();

                    if ("step_loop;".equals(instr)) {
                        self.triggerAnim("spin_controller", "mace_spin_loop");
                    }

                    if ("step_post;".equals(instr)) {
                        self.triggerAnim("spin_controller", "mace_spin_post");
                    }

                    if ("spin_done;".equals(instr)) {
                        if (self.getWorld().isClient) {
                            ClientPlayNetworking.send(new GeneralSpinDoneC2SPayload(self.getId()));
                        }
                    }

                    if ("attack;".equals(instr) || "attack_done;".equals(instr)) {
                        if (self.getWorld().isClient) {
                            ClientPlayNetworking.send(new GeneralSpinAttackC2SPayload(self.getId(), instr));
                        }
                    }
                }));
    }

    private <T extends GeoAnimatable> PlayState mainPredicate(AnimationState<T> state) {
        Vec3d vel = this.getVelocity();
        double speedSq = vel.horizontalLengthSquared();
        AnimationController<?> controller = state.getController();

        if (isSpinning) {
            controller.setAnimation(RawAnimation.begin().thenLoop("animation.villager.piglin_general.mace_spin_loop"));
        } else if (speedSq > 0.003) {
            controller.setAnimation(RawAnimation.begin().thenLoop("animation.villager.piglin_general.run"));
        } else if (speedSq > 1e-6) {
            controller.setAnimation(RawAnimation.begin().thenLoop("animation.villager.piglin_general.walk"));
        } else {
            controller.setAnimation(RawAnimation.begin().thenLoop("animation.villager.piglin_general.idle"));
        }
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
