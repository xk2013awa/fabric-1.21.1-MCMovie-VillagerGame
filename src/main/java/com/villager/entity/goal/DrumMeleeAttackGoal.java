package com.villager.entity.goal;

import com.villager.entity.DrumPiglinEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.Hand;
import software.bernie.geckolib.animatable.GeoEntity;

import java.util.EnumSet;

public class DrumMeleeAttackGoal extends Goal {
    private final PathAwareEntity mob;
    private final double speed;
    private final double maxAttackRangeSq;

    private int cooldown = 0;

    public DrumMeleeAttackGoal(PathAwareEntity mob, double speed, double maxAttackRange) {
        this.mob = mob;
        this.speed = speed;
        this.maxAttackRangeSq = maxAttackRange * maxAttackRange;

        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        LivingEntity target = mob.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity target = mob.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public void stop() {
        this.mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target == null) return;

        mob.getLookControl().lookAt(target, 30.0f, 30.0f);

        double distSq = mob.squaredDistanceTo(target);
        boolean inRange = distSq <= maxAttackRangeSq;

        DrumPiglinEntity self = (DrumPiglinEntity)mob;

        if (inRange) {
            mob.getNavigation().stop();

            if (self.isGroundAttackReady() && --cooldown <= 0) {
                performAttack();
                self.resetGroundAttackCooldown();
            }
        } else {
            mob.getNavigation().startMovingTo(target, speed);
        }
    }

    protected void performAttack() {
        mob.swingHand(Hand.MAIN_HAND);

        if (!mob.getWorld().isClient && mob instanceof GeoEntity geoMob) {
            int idx = mob.getRandom().nextInt(2) + 1;
            geoMob.triggerAnim("attack_controller", "attack" + idx);
        }
    }
}
