package com.villager.entity.goal;

import com.villager.entity.SpearPiglinEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;

import java.util.EnumSet;

public class SpearPiglinRangedAttackGoal extends Goal {

    private final SpearPiglinEntity mob;

    private int meleeCooldownTicks = 0;

    private static final float APPROACH_DISTANCE = 10.0f;

    public SpearPiglinRangedAttackGoal(PathAwareEntity mob) {
        this.mob = (SpearPiglinEntity) mob;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override public boolean canStart()       { return mob.getTarget() != null && mob.getTarget().isAlive(); }
    @Override public boolean shouldContinue() { return canStart(); }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive()) return;

        mob.getLookControl().lookAt(target, 30.0f, 30.0f);
        mob.tickRangedCooldown();
        if (meleeCooldownTicks > 0) meleeCooldownTicks--;

        double dist2 = mob.squaredDistanceTo(target);
        double distance = Math.sqrt(dist2);

        if (mob.isThrowing()) {
            mob.getNavigation().stop();
            return;
        }

        if (distance > APPROACH_DISTANCE) {
            mob.getNavigation().startMovingTo(target, 1.2);
            return;
        } else {
            mob.getNavigation().stop();
        }

        mob.getLookControl().lookAt(target, 30f, 30f);

        if (dist2 > 16.0 && mob.isRangedCooldownReady() && !mob.isThrowing()) {
            int idx = mob.getRandom().nextInt(2) + 1;

            mob.getNavigation().stop();

            mob.triggerAnim("throw_controller", "spear_throw_pre_" + idx);
            mob.setThrowing(true);
            return;
        }

        if (dist2 <= 16.0 && meleeCooldownTicks <= 0) {
            int idx = mob.getRandom().nextInt(2) + 1;
            mob.triggerAnim("melee_controller", "spear_melee_" + idx);
            meleeCooldownTicks = 10;
        }
    }
}
