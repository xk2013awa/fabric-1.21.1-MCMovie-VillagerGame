package com.villager.access;

import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public interface VillagerFriendshipAccess {
    int tryIncreaseFriendship(int amount);
    int getFriendshipLevel();
    boolean isDailyCapReached();
    boolean hasExclamation();
    void setAiDisabledByScreen(boolean disabled);
    void setForcedLookTarget(@Nullable LivingEntity target);
    void setFriendshipLevel(int level);
    void setVillagerProtected(boolean protectedStatus);
    boolean isVillagerProtected();
    void removeRedExclamation();
    boolean hasConfessed();
    void setConfessed(boolean confessed);
    boolean isMarried();
    void setMarried(boolean married);
}