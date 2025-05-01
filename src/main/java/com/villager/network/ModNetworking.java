package com.villager.network;

import com.villager.access.VillagerFriendshipAccess;
import com.villager.entity.*;
import com.villager.network.payload.*;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class ModNetworking {
    public static final Map<ServerPlayerEntity, List<Integer>> activePiglinAttackMobs = new HashMap<>();


    public static void registerC2SHandlers() {
        ServerPlayNetworking.registerGlobalReceiver(VillagerMarryC2SPayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            int villagerId = payload.villagerID();
            boolean married   = payload.married();

            Objects.requireNonNull(player.getServer()).execute(() -> {
                var entity = player.getWorld().getEntityById(villagerId);
                if (entity instanceof VillagerEntity villager) {
                    VillagerFriendshipAccess access = (VillagerFriendshipAccess) villager;
                    access.setMarried(married);
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(FriendshipC2SPayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            int villagerId = payload.villagerID();
            int newLevel   = payload.amount();

            Objects.requireNonNull(player.getServer()).execute(() -> {
                var entity = player.getWorld().getEntityById(villagerId);
                if (entity instanceof VillagerEntity villager) {
                    VillagerFriendshipAccess access = (VillagerFriendshipAccess) villager;
                    access.setFriendshipLevel(newLevel);
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(VillagerEnemySummonC2SPayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            int villagerId = payload.villagerId();

            Objects.requireNonNull(context.player().getServer()).execute(() -> {
                var entity = player.getWorld().getEntityById(villagerId);
                if (entity instanceof VillagerEntity villager) {
                    for (int i = 0; i < 3; i++) {
                        IronGolemEntity golem = new IronGolemEntity(EntityType.IRON_GOLEM, villager.getWorld());
                        golem.refreshPositionAndAngles(
                                villager.getX() + (Math.random() - 0.5) * 5,
                                villager.getY(),
                                villager.getZ() + (Math.random() - 0.5) * 5,
                                0, 0);
                        golem.setPlayerCreated(false);
                        golem.setTarget(player);
                        villager.getWorld().spawnEntity(golem);
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(VillagerAiDisableC2SPayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            int id = payload.villagerId();
            boolean disabled = payload.disabled();

            Objects.requireNonNull(player.getServer()).execute(() -> {
                var entity = player.getWorld().getEntityById(id);
                if (entity instanceof VillagerEntity villager && villager instanceof VillagerFriendshipAccess access) {
                    access.setAiDisabledByScreen(disabled);
                    access.setForcedLookTarget(player);
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(SetBlasterPosC2SPayload.ID, (payload, context) -> {
            var player = context.player();
            Objects.requireNonNull(player.getServer()).execute(() -> {
                var entity = player.getWorld().getEntityById(payload.entityId());
                if (entity instanceof GreatHogEntity hog) {
                    hog.setBlasterWorldPos(payload.getPos());
                }
            });
        });


        ServerPlayNetworking.registerGlobalReceiver(ShootFireballC2SPayload.ID, (payload, context) -> {
            var player = context.player();
            Objects.requireNonNull(player.getServer()).execute(() -> {
                var entity = player.getWorld().getEntityById(payload.entityId());
                if (entity instanceof GreatHogEntity hog) {
                    LivingEntity target = hog.getTarget();
                    if (target != null && hog.isAlive()) {
                        hog.shootAt(target, payload.pullProgress());
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(MeleeAttackC2SPayload.ID, (payload, context) -> {
            var player = context.player();
            Objects.requireNonNull(player.getServer()).execute(() -> {
                var entity = player.getWorld().getEntityById(payload.entityId());
                if (entity instanceof SpearPiglinEntity spearPiglin) {
                    LivingEntity target = spearPiglin.getTarget();
                    if (target != null) {
                        spearPiglin.meleeAttack(target);
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(ShootSpearC2SPayload.ID, (payload, context) -> {
            var player = context.player();
            Objects.requireNonNull(player.getServer()).execute(() -> {
                var entity = player.getWorld().getEntityById(payload.entityId());
                if (entity instanceof SpearPiglinEntity pig) {
                    LivingEntity target = pig.getTarget();
                    if (target != null && pig.isAlive()) {
                        pig.shootAt(target, payload.pullProgress());
                        pig.resetRangedCooldown();
                        pig.setThrowing(false);
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(StopMoveC2SPayload.ID, (payload, context) -> {
            var player = context.player();
            Objects.requireNonNull(player.getServer()).execute(() -> {
                var entity = player.getWorld().getEntityById(payload.entityId());
                if (entity instanceof SpearPiglinEntity pig) {
                    pig.getNavigation().stop();
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(DrumAttackGroundC2SPayload.ID, (payload, context) -> {
            var player = context.player();
            Objects.requireNonNull(player.getServer()).execute(() -> {
                if (!(player.getWorld().getEntityById(payload.entityId()) instanceof DrumPiglinEntity drum))
                    return;

                ServerWorld world = (ServerWorld) drum.getWorld();
                Vec3d origin = drum.getPos().add(0, 0.1, 0);
                Vec3d forward = drum.getRotationVector();
                forward = new Vec3d(forward.x, 0, forward.z).normalize();
                if (forward.lengthSquared() < 1e-4) forward = new Vec3d(0, 0, 1);
                double baseYaw = Math.atan2(forward.z, forward.x);

                final double maxDist = 16.0;
                final double arcHalf = Math.toRadians(45);
                final int rSteps = 8;
                final int thetaSteps = 12;

                for (LivingEntity entity : world.getEntitiesByClass(LivingEntity.class,
                        drum.getBoundingBox().expand(maxDist, 3, maxDist), e -> e != drum && e.isAlive())) {

                    if (
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
                    ) continue;

                    Vec3d pos = entity.getPos();
                    BlockPos below = BlockPos.ofFloored(pos.getX(), pos.getY() - 0.5, pos.getZ());

                    while (world.isAir(below) && below.getY() > world.getBottomY()) {
                        below = below.down();
                    }

                    double groundY = below.getY() + 1.0;
                    double entityY = entity.getY();
                    if (Math.abs(entityY - groundY) < 0.6) {
                        RegistryEntry<DamageType> type = world.getRegistryManager()
                                .get(RegistryKeys.DAMAGE_TYPE)
                                .entryOf(RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of("minecraft", "mob_attack")));
                        entity.damage(new DamageSource(type), 6.0f);
                    }
                }

                for (int ri = 1; ri <= rSteps; ri++) {
                    double r = (maxDist / rSteps) * ri;

                    for (int ti = 0; ti <= thetaSteps; ti++) {
                        double theta = -arcHalf + (arcHalf * 2) * (ti / (double) thetaSteps);
                        double yaw = baseYaw + theta;

                        double dx = Math.cos(yaw) * r;
                        double dz = Math.sin(yaw) * r;
                        Vec3d sample = origin.add(dx, 0, dz);

                        BlockPos pos = BlockPos.ofFloored(sample);
                        while (world.isAir(pos) && pos.getY() > world.getBottomY())
                            pos = pos.down();
                        BlockState state = world.getBlockState(pos);
                        if (state.isAir()) continue;

                        double px = pos.getX() + 0.5;
                        double py = pos.getY() + 1.1;
                        double pz = pos.getZ() + 0.5;

                        world.spawnParticles(
                                new BlockStateParticleEffect(ParticleTypes.BLOCK, state),
                                px, py, pz,
                                10,
                                0.25, 0.05, 0.25,
                                0.15
                        );
                    }

                    double sx = origin.x + forward.x * r;
                    double sz = origin.z + forward.z * r;
                    world.playSound(
                            null, sx, origin.y, sz,
                            SoundEvents.BLOCK_STONE_BREAK,
                            SoundCategory.HOSTILE,
                            0.8f, 0.9f + world.random.nextFloat() * 0.2f
                    );
                }

                drum.resetGroundAttackCooldown();
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(GeneralSpinAttackC2SPayload.ID, (payload, context) -> {
            var player = context.player();
            Objects.requireNonNull(player.getServer()).execute(() -> {
                var world = player.getWorld();
                var entity = world.getEntityById(payload.entityId());
                if (!(entity instanceof PiglinGeneralEntity general)) return;

                LivingEntity target = general.getTarget();
                if (target == null || !target.isAlive()) return;
                if (general.squaredDistanceTo(target) > 9.0) return;

                float damage = switch (payload.attackType()) {
                    case "attack_done;" -> 12.0f;
                    case "attack;" -> 6.0f;
                    default -> 4.0f;
                };

                RegistryEntry<DamageType> type = world.getRegistryManager()
                        .get(RegistryKeys.DAMAGE_TYPE)
                        .entryOf(RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of("minecraft", "mob_attack")));

                target.damage(new DamageSource(type), damage);
                general.setSpinning(false);
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(GeneralSpinDoneC2SPayload.ID, (payload, context) -> {
            var player = context.player();
            Objects.requireNonNull(player.getServer()).execute(() -> {
                var entity = player.getWorld().getEntityById(payload.entityId());
                if (entity instanceof PiglinGeneralEntity general) {
                    general.setSpinning(false);
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(HammerAttackGroundC2SPayload.ID, (payload, context) -> {
            var player = context.player();
            Objects.requireNonNull(player.getServer()).execute(() -> {
                var world = player.getWorld();
                var entity = world.getEntityById(payload.entityId());
                if (!(entity instanceof HammerPiglinEntity hammer)) return;

                LivingEntity target = hammer.getTarget();
                if (target != null && target.isAlive()) {
                    RegistryEntry<DamageType> type = world.getRegistryManager()
                            .get(RegistryKeys.DAMAGE_TYPE)
                            .entryOf(RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of("minecraft", "mob_attack")));
                    target.damage(new DamageSource(type), 6.0f);
                }

                if (world instanceof ServerWorld serverWorld) {
                    BlockPos center = hammer.getBlockPos();
                    int radius = 2;
                    for (int dx = -radius; dx <= radius; dx++) {
                        for (int dz = -radius; dz <= radius; dz++) {
                            BlockPos pos = center.add(dx, -1, dz);
                            if (world.isAir(pos.up())) {
                                var state = world.getBlockState(pos);
                                if (!state.isAir()) {
                                    serverWorld.spawnParticles(
                                            new BlockStateParticleEffect(ParticleTypes.BLOCK, state),
                                            pos.getX() + 0.5,
                                            pos.getY() + 1.0,
                                            pos.getZ() + 0.5,
                                            10,
                                            0.25, 0.1, 0.25,
                                            0.15
                                    );
                                }
                            }
                        }
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(ReinforceC2SPayload.ID, (payload, context) -> {
            var player = context.player();
            Objects.requireNonNull(player.getServer()).execute(() -> {
                var world = player.getWorld();
                var entity = world.getEntityById(payload.entityId());
                if (!(entity instanceof DrumPiglinEntity drum)) return;

                drum.healNearbyPiglins();
                if (drum.getRandom().nextFloat() < 0.05f) {
                    drum.spawnNewPiglin();
                }

                if (world instanceof ServerWorld serverWorld) {
                    serverWorld.spawnParticles(
                            ParticleTypes.NOTE,
                            drum.getX(),
                            drum.getY() + 1.2,
                            drum.getZ(),
                            2,
                            2, 2, 2,
                            0.0
                    );
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(ParticlesC2SPayload.ID, (payload, context) -> {
            var player = context.player();
            Objects.requireNonNull(player.getServer()).execute(() -> {
                if (player.getWorld() instanceof ServerWorld world) {
                    Vec3d effectPos = new Vec3d(payload.x(), payload.y(), payload.z());

                    if ("hit".equals(payload.effectType())) {
                        world.spawnParticles(
                                ParticleTypes.END_ROD,
                                effectPos.x, effectPos.y + 1, effectPos.z,
                                50,
                                0, 0, 0,
                                0
                        );
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(PiglinAmbushC2SPayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            BlockPos center = payload.center();
            boolean targetPlayer = payload.targetPlayer();
            ServerWorld world = player.getServerWorld();

            List<EntityType<? extends MobEntity>> types = List.of(
                    ModEntities.GREAT_HOG,
                    ModEntities.SPEAR_PIGLIN,
                    ModEntities.CHEF_PIGLIN,
                    ModEntities.DRUM_PIGLIN,
                    ModEntities.WARRIOR_PIGLIN,
                    ModEntities.PICKAXE_PIGLIN,
                    ModEntities.HAMMER_PIGLIN,
                    ModEntities.PIGLIN_GENERAL
            );

            VillagerEntity nearestVillager = world.getEntitiesByClass(VillagerEntity.class, player.getBoundingBox().expand(20), v -> true)
                    .stream().findFirst().orElse(null);

            List<Integer> mobIds = new ArrayList<>();

            for (int i = 0; i < types.size(); i++) {
                double angle = 2 * Math.PI * i / types.size();
                double x = center.getX() + 4 * Math.cos(angle);
                double z = center.getZ() + 4 * Math.sin(angle);
                BlockPos spawnPos = new BlockPos((int) x, center.getY(), (int) z);

                MobEntity mob = types.get(i).create(world);
                if (mob != null) {
                    mob.refreshPositionAndAngles(spawnPos, world.random.nextFloat() * 360f, 0f);
                    if (targetPlayer) {
                        mob.setTarget(player);
                    } else if (nearestVillager != null) {
                        mob.setTarget(nearestVillager);
                    }
                    world.spawnEntity(mob);
                    mobIds.add(mob.getId());
                }
            }
            activePiglinAttackMobs.put(player, mobIds);
        });

        ServerPlayNetworking.registerGlobalReceiver(VillagerSetProtectedC2SPayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            Objects.requireNonNull(player.getServer()).execute(() -> {
                var entity = player.getWorld().getEntityById(payload.villagerId());
                if (entity instanceof VillagerEntity villager && villager instanceof VillagerFriendshipAccess access) {
                    access.setVillagerProtected(payload.protectedStatus());
                }
            });
        });
    }
}
