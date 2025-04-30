package com.villager.entity;

import com.villager.entity.goal.SpearPiglinRangedAttackGoal;
import com.villager.network.payload.MeleeAttackC2SPayload;
import com.villager.network.payload.ShootSpearC2SPayload;
import com.villager.network.payload.StopMoveC2SPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.*;

public class SpearPiglinEntity extends HostileEntity implements GeoEntity, RangedAttackMob {

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    private int  rangedCooldownTicks = 0;
    private boolean isThrowing = false;

    public SpearPiglinEntity(EntityType<? extends HostileEntity> type, World world) {
        super(type, world);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    public boolean isThrowing() { return isThrowing; }
    public void    setThrowing(boolean flag) { isThrowing = flag; }

    public void   resetRangedCooldown() { this.rangedCooldownTicks = 25; }
    public void   tickRangedCooldown() { if (rangedCooldownTicks > 0) rangedCooldownTicks--; }
    public boolean isRangedCooldownReady() { return rangedCooldownTicks <= 0; }

    public static DefaultAttributeContainer.Builder createMobAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 24.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(3, new SpearPiglinRangedAttackGoal(this));
        this.goalSelector.add(4, new WanderAroundFarGoal(this, 0.25f));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(6, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

        controllers.add(new AnimationController<>(this, "main_controller", 10, this::mainPredicate));

        controllers.add(
                new AnimationController<>(this, "melee_controller", 0, s -> PlayState.STOP)
                        .triggerableAnim("spear_melee_1",
                                RawAnimation.begin()
                                        .then("animation.villager.spear_piglin.spear_melee_1", Animation.LoopType.PLAY_ONCE))
                        .triggerableAnim("spear_melee_2",
                                RawAnimation.begin()
                                        .then("animation.villager.spear_piglin.spear_melee_2", Animation.LoopType.PLAY_ONCE))
                        .setCustomInstructionKeyframeHandler(event -> {
                            if ("melee_attack;".equals(event.getKeyframeData().getInstructions())) {
                                ClientPlayNetworking.send(new MeleeAttackC2SPayload(this.getId()));
                            }
                        }));

        AnimationController<SpearPiglinEntity> throwCtrl =
                new AnimationController<>(this, "throw_controller", 0, s -> PlayState.STOP).transitionLength(0)
                        .triggerableAnim("spear_throw_pre_1",
                                RawAnimation.begin().then("animation.villager.spear_piglin.spear_throw_pre_1", Animation.LoopType.PLAY_ONCE))
                        .triggerableAnim("spear_throw_loop_1",
                                RawAnimation.begin().thenLoop("animation.villager.spear_piglin.spear_throw_loop_1"))
                        .triggerableAnim("spear_throw_post_1",
                                RawAnimation.begin().then("animation.villager.spear_piglin.spear_throw_post_1", Animation.LoopType.PLAY_ONCE))

                        .triggerableAnim("spear_throw_pre_2",
                                RawAnimation.begin().then("animation.villager.spear_piglin.spear_throw_pre_2", Animation.LoopType.PLAY_ONCE))
                        .triggerableAnim("spear_throw_loop_2",
                                RawAnimation.begin().thenLoop("animation.villager.spear_piglin.spear_throw_loop_2"))
                        .triggerableAnim("spear_throw_post_2",
                                RawAnimation.begin().then("animation.villager.spear_piglin.spear_throw_post_2", Animation.LoopType.PLAY_ONCE))

                        .setCustomInstructionKeyframeHandler(event -> {
                            String instr = event.getKeyframeData().getInstructions();
                            SpearPiglinEntity self = event.getAnimatable();
                            int idx;
                            switch (instr) {
                                /* pre -> loop */
                                case "step_loop;" -> {
                                    idx = self.getRandom().nextInt(2) + 1;
                                    self.triggerAnim("throw_controller", "spear_throw_loop_" + idx);

                                    if (self.getWorld().isClient) {
                                        ClientPlayNetworking.send(new StopMoveC2SPayload(self.getId()));
                                    }
                                }
                                case "step_post;" -> {
                                    idx = self.getRandom().nextInt(2) + 1;
                                    self.triggerAnim("throw_controller", "spear_throw_post_" + idx);

                                    if (self.getWorld().isClient) {
                                        ClientPlayNetworking.send(new ShootSpearC2SPayload(self.getId(), 1.0f));
                                    }
                                }
                            }
                        });

        controllers.add(throwCtrl);
    }

    private <T extends GeoAnimatable> PlayState mainPredicate(AnimationState<T> state) {
        SpearPiglinEntity self = (SpearPiglinEntity) state.getAnimatable();
        Vec3d v = self.getVelocity();
        double speed = v.horizontalLengthSquared();

        var ctrl = state.getController();

        if (speed > 0.003) {
            ctrl.setAnimation(RawAnimation.begin().thenLoop("animation.villager.spear_piglin.run"));
        } else if (speed > 1e-6) {
            ctrl.setAnimation(RawAnimation.begin().thenLoop("animation.villager.spear_piglin.walk"));
        } else {
            ctrl.setAnimation(RawAnimation.begin().thenLoop("animation.villager.spear_piglin.idle"));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void shootAt(LivingEntity target, float pullProgress) {
        if (this.getWorld().isClient) return;

        SpearEntity spear = new SpearEntity(ModEntities.SPEAR, this.getWorld());
        spear.setPosition(getX(), getEyeY(), getZ());

        Vec3d targetPos = target.getPos().add(0, target.getHeight() * 0.5, 0);
        Vec3d dir = targetPos.subtract(getPos().add(0, getEyeY() - getY(), 0));
        spear.setVelocity(dir.x, dir.y, dir.z, 1.5f, 4.0f);

        this.getWorld().spawnEntity(spear);
    }

    public void meleeAttack(LivingEntity target) {
        if (target == null || !target.isAlive()) return;

        if (this.squaredDistanceTo(target) > 36.0) return;

        RegistryEntry<DamageType> type = getWorld().getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .entryOf(RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of("minecraft", "mob_attack")));

        target.damage(new DamageSource(type), 5.0f);
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
