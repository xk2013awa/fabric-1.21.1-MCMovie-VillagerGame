package com.villager;

import com.villager.ai.AIConfig;
import com.villager.entity.ModEntities;
import com.villager.network.ModNetworking;
import com.villager.network.payload.*;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class Villager implements ModInitializer {

    public static final String MOD_ID = "villager";

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playC2S().register(FriendshipC2SPayload.ID, FriendshipC2SPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(VillagerEnemySummonC2SPayload.ID, VillagerEnemySummonC2SPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(VillagerAiDisableC2SPayload.ID, VillagerAiDisableC2SPayload.CODEC);


        PayloadTypeRegistry.playC2S().register(SetBlasterPosC2SPayload.ID, SetBlasterPosC2SPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ShootFireballC2SPayload.ID, ShootFireballC2SPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(MeleeAttackC2SPayload.ID, MeleeAttackC2SPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ShootSpearC2SPayload.ID, ShootSpearC2SPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(StopMoveC2SPayload.ID, StopMoveC2SPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(DrumAttackGroundC2SPayload.ID, DrumAttackGroundC2SPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(GeneralSpinAttackC2SPayload.ID, GeneralSpinAttackC2SPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(GeneralSpinDoneC2SPayload.ID, GeneralSpinDoneC2SPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(HammerAttackGroundC2SPayload.ID, HammerAttackGroundC2SPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ReinforceC2SPayload.ID, ReinforceC2SPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ParticlesC2SPayload.ID, ParticlesC2SPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(PiglinAmbushC2SPayload.ID, PiglinAmbushC2SPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(VillagerSetProtectedC2SPayload.ID, VillagerSetProtectedC2SPayload.CODEC);

        AutoConfig.register(AIConfig.class, Toml4jConfigSerializer::new);

        ModNetworking.registerC2SHandlers();
        ModEntities.registerEntities();
        ModEntities.registerAttributes();
    }
}