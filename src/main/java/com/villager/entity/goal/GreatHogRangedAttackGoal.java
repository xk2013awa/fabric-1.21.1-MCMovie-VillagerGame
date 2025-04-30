package com.villager.entity.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import software.bernie.geckolib.animatable.GeoEntity;

import java.util.EnumSet;

public class GreatHogRangedAttackGoal extends Goal {
    private final PathAwareEntity mob;
    private final int attackCooldown;
    private final float maxRange;
    private int cooldownTicks;

    public GreatHogRangedAttackGoal(PathAwareEntity mob, int cooldown, float maxRange) {
        this.mob = mob;
        this.attackCooldown = cooldown;
        this.maxRange = maxRange;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override public boolean canStart() { return hasLivingTarget(); }
    @Override public boolean shouldContinue() { return hasLivingTarget(); }

    private boolean hasLivingTarget() {
        LivingEntity t = mob.getTarget();
        return t != null && t.isAlive();
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target == null) return;

        double distanceSq = mob.squaredDistanceTo(target);
        mob.getLookControl().lookAt(target, 30.0F, 30.0F);

        if (distanceSq > maxRange * maxRange) {
            mob.getNavigation().startMovingTo(target, 1.0);
        } else {
            mob.getNavigation().stop();
        }
        if (cooldownTicks > 0) {
            cooldownTicks--;
            return;
        }

        if (distanceSq <= maxRange * maxRange) {
            cooldownTicks = attackCooldown;

            if (mob instanceof GeoEntity geoMob) {
                int index = mob.getRandom().nextInt(2) + 1;
                geoMob.triggerAnim("attack_controller", "attack" + index);
            }
        }
    }
}
