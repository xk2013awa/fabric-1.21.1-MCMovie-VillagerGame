package com.villager.mixin;

import com.villager.access.VillagerFriendshipAccess;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.villager.network.ModNetworking.activePiglinAttackMobs;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (self.getWorld().isClient()) {
            return;
        }

        if (self instanceof VillagerFriendshipAccess access) {
            if (access.isVillagerProtected()) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeath(net.minecraft.entity.damage.DamageSource source, CallbackInfo ci) {
        if (((LivingEntity)(Object)this).getWorld().isClient()) return;

        ServerWorld world = (ServerWorld) ((LivingEntity)(Object)this).getWorld();
        int mobId = ((LivingEntity)(Object)this).getId();

        for (var entry : activePiglinAttackMobs.entrySet()) {
            ServerPlayerEntity player = entry.getKey();
            var list = entry.getValue();

            if (list.contains(mobId)) {
                list.remove((Integer) mobId);

                if (list.isEmpty()) {
                    VillagerEntity villager = world.getEntitiesByClass(VillagerEntity.class, player.getBoundingBox().expand(20), v -> true)
                            .stream().findFirst().orElse(null);
                    if (villager instanceof VillagerFriendshipAccess access) {
                        access.setFriendshipLevel(100);
                        access.removeRedExclamation();
                        access.setVillagerProtected(false);
                    }
                    activePiglinAttackMobs.remove(player);
                }
                break;
            }
        }
    }
}
