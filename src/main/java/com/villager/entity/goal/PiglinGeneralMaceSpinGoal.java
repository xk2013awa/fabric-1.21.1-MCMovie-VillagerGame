package com.villager.entity.goal;

import com.villager.entity.PiglinGeneralEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;

import java.util.EnumSet;

public class PiglinGeneralMaceSpinGoal extends Goal {
    private final PiglinGeneralEntity mob;
    private LivingEntity target;

    private int cooldown = 0;
    private final double attackDistanceSqr;
    private final double speed;

    public PiglinGeneralMaceSpinGoal(PathAwareEntity mob, double distance) {
        this.mob = (PiglinGeneralEntity) mob;
        this.attackDistanceSqr = distance * distance;
        this.speed = 1;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        target = mob.getTarget();
        return target != null && target.isAlive() && !mob.isSpinning();
    }

    @Override
    public boolean shouldContinue() {
        return (target != null && target.isAlive()) || mob.isSpinning();
    }

    @Override
    public void start() {
        cooldown = 0;
    }

    @Override
    public void stop() {
        target = null;
        mob.getNavigation().stop();
        cooldown = 0;
        mob.setSpinning(false);
    }

    @Override
    public void tick() {

        if (target == null || !target.isAlive()) return;

        mob.getLookControl().lookAt(target);

        double distSq = mob.squaredDistanceTo(target);

        if (mob.isSpinning()) {
            mob.getNavigation().stop();
            return;
        }

        if (distSq <= attackDistanceSqr) {
            if (cooldown <= 0) {
                mob.getNavigation().stop();
                mob.setSpinning(true);
                mob.triggerAnim("spin_controller", "mace_spin_pre");
                cooldown = 30;
            } else {
                cooldown--;
            }
        } else {
            mob.getNavigation().startMovingTo(target, speed);
        }
    }
}