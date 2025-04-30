package com.villager.entity;

import com.villager.entity.goal.DrumMeleeAttackGoal;
import com.villager.network.payload.DrumAttackGroundC2SPayload;
import com.villager.network.payload.ParticlesC2SPayload;
import com.villager.network.payload.ReinforceC2SPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.*;

public class DrumPiglinEntity extends HostileEntity implements GeoEntity {

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public DrumPiglinEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    private int groundAttackCooldown = 0;

    private Vec3d rightEffectPosition;
    private Vec3d leftEffectPosition;

    public void setRightEffectPosition(Vec3d position) {
        this.rightEffectPosition = position;
    }

    public void setLeftEffectPosition(Vec3d position) {
        this.leftEffectPosition = position;
    }

    public Vec3d getRightEffectPosition() {
        return rightEffectPosition;
    }

    public Vec3d getLeftEffectPosition() {
        return leftEffectPosition;
    }

    @Override
    public void tick() {
        super.tick();
        if (groundAttackCooldown > 0) {
            groundAttackCooldown--;

            if (groundAttackCooldown == 40 && !this.getWorld().isClient) {
                this.triggerAnim("drum_controller", "drum_play_2");
            }
        }
    }

    public boolean isGroundAttackReady() {
        return groundAttackCooldown <= 0;
    }

    public void resetGroundAttackCooldown() {
        groundAttackCooldown = 120;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(3, new DrumMeleeAttackGoal(this, 1.6, 10.0));
        this.goalSelector.add(4, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(6, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    public static DefaultAttributeContainer.Builder createMobAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.23)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main_controller", 10, this::mainPredicate));
        controllers.add(
                new AnimationController<>(this, "attack_controller", state -> PlayState.STOP)
                        .triggerableAnim("attack1", RawAnimation.begin().then("animation.villager.drum_piglin.attack_1", Animation.LoopType.PLAY_ONCE))
                        .triggerableAnim("attack2", RawAnimation.begin().then("animation.villager.drum_piglin.attack_2", Animation.LoopType.PLAY_ONCE))
                        .setCustomInstructionKeyframeHandler(event -> {
                            if ("attack_ground;".equals(event.getKeyframeData().getInstructions())) {
                                DrumPiglinEntity self = event.getAnimatable();
                                if (self.getWorld().isClient) {
                                    ClientPlayNetworking.send(new DrumAttackGroundC2SPayload(self.getId()));
                                }
                            }
                        })
        );

        controllers.add(
                new AnimationController<>(this, "drum_controller", state -> PlayState.STOP)
                        .triggerableAnim("drum_play_1", RawAnimation.begin().then("animation.villager.drum_piglin.drum_play_1", Animation.LoopType.PLAY_ONCE))
                        .triggerableAnim("drum_play_2", RawAnimation.begin().then("animation.villager.drum_piglin.drum_play_2", Animation.LoopType.PLAY_ONCE))
                        .setCustomInstructionKeyframeHandler(event -> {
                            String instruction = event.getKeyframeData().getInstructions();
                            if ("reinforce;".equals(instruction)) {
                                DrumPiglinEntity self = event.getAnimatable();
                                if (self.getWorld().isClient) {
                                    ClientPlayNetworking.send(new ReinforceC2SPayload(self.getId()));
                                }
                            }
                        })
                        .setParticleKeyframeHandler(event -> {
                            String effect = event.getKeyframeData().getEffect();
                            String locator = event.getKeyframeData().getLocator();
                            DrumPiglinEntity animatable = event.getAnimatable();

                            Vec3d effectPos = switch (locator) {
                                case "right_effect" -> animatable.getRightEffectPosition();
                                case "left_effect" -> animatable.getLeftEffectPosition();
                                default -> null;
                            };
                            if (effectPos == null) return;

                            ClientPlayNetworking.send(new ParticlesC2SPayload(
                                    animatable.getId(),
                                    effectPos.x,
                                    effectPos.y,
                                    effectPos.z,
                                    effect
                            ));
                        })
        );
    }

    private <T extends GeoAnimatable> PlayState mainPredicate(AnimationState<T> state) {
        Vec3d v = this.getVelocity();
        double speed = v.horizontalLengthSquared();
        var ctrl = state.getController();

        if (speed > 0.003) {
            ctrl.setAnimation(RawAnimation.begin().thenLoop("animation.villager.drum_piglin.run"));
        } else if (speed > 1e-6) {
            ctrl.setAnimation(RawAnimation.begin().thenLoop("animation.villager.drum_piglin.walk"));
        } else {
            ctrl.setAnimation(RawAnimation.begin().thenLoop("animation.villager.drum_piglin.idle"));
        }
        return PlayState.CONTINUE;
    }

    public void healNearbyPiglins() {
        if (!(this.getWorld() instanceof ServerWorld world)) return;

        world.getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand(12), entity ->
                entity != this && (
                        entity.getType() == EntityType.PIGLIN ||
                                entity.getType() == EntityType.PIGLIN_BRUTE ||
                                entity.getType() == ModEntities.MALGOSHA ||
                                entity.getType() == ModEntities.GREAT_HOG ||
                                entity.getType() == ModEntities.SPEAR_PIGLIN ||
                                entity.getType() == ModEntities.CHEF_PIGLIN ||
                                entity.getType() == ModEntities.DRUM_PIGLIN ||
                                entity.getType() == ModEntities.WARRIOR_PIGLIN ||
                                entity.getType() == ModEntities.PICKAXE_PIGLIN ||
                                entity.getType() == ModEntities.HAMMER_PIGLIN ||
                                entity.getType() == ModEntities.PIGLIN_GENERAL
                )).forEach(entity -> {
            entity.heal(4.0f);
            world.spawnParticles(ParticleTypes.HEART, entity.getX(), entity.getY() + 1.2, entity.getZ(), 5, 0.3, 0.3, 0.3, 0.01);
        });
    }

    public void spawnNewPiglin() {
        if (!(this.getWorld() instanceof ServerWorld world)) return;

        BlockPos center = this.getBlockPos();
        BlockPos spawnPos = center.add(this.getRandom().nextBetween(-4, 4), 0, this.getRandom().nextBetween(-4, 4));

        while (world.isAir(spawnPos) && spawnPos.getY() > world.getBottomY()) {
            spawnPos = spawnPos.down();
        }

        spawnPos = spawnPos.up();
        if (!world.isAir(spawnPos)) return;

        EntityType<? extends LivingEntity>[] candidates = new EntityType[]{
                ModEntities.MALGOSHA, ModEntities.GREAT_HOG, ModEntities.SPEAR_PIGLIN,
                ModEntities.CHEF_PIGLIN, ModEntities.WARRIOR_PIGLIN,
                ModEntities.PICKAXE_PIGLIN, ModEntities.HAMMER_PIGLIN, ModEntities.PIGLIN_GENERAL
        };
        EntityType<? extends LivingEntity> type = candidates[this.getRandom().nextInt(candidates.length)];
        LivingEntity entity = type.create(world);
        if (entity == null) return;

        entity.refreshPositionAndAngles(spawnPos, this.getRandom().nextFloat() * 360f, 0);
        world.spawnEntity(entity);

        world.spawnParticles(ParticleTypes.HAPPY_VILLAGER,
                spawnPos.getX() + 0.5, spawnPos.getY() + 1.0, spawnPos.getZ() + 0.5,
                10, 0.5, 0.5, 0.5, 0.05);
    }

    @Override
    public boolean isInAttackRange(LivingEntity target) {
        return this.squaredDistanceTo(target) <= 36.0;
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
