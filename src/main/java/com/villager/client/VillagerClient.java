package com.villager.client;

import com.villager.client.render.*;
import com.villager.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class VillagerClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.EXCLAMATION, ExclamationRenderer::new);
        EntityRendererRegistry.register(ModEntities.MALGOSHA, MalgoshaRenderer::new);
        EntityRendererRegistry.register(ModEntities.FIREBALL_SMALL, FireballSmallRenderer::new);
        EntityRendererRegistry.register(ModEntities.GREAT_HOG, GreatHogRenderer::new);
        EntityRendererRegistry.register(ModEntities.SPEAR_PIGLIN, SpearPiglinRenderer::new);
        EntityRendererRegistry.register(ModEntities.SPEAR, SpearRenderer::new);
        EntityRendererRegistry.register(ModEntities.CHEF_PIGLIN, ChefPiglinRenderer::new);
        EntityRendererRegistry.register(ModEntities.DRUM_PIGLIN, DrumPiglinRenderer::new);
        EntityRendererRegistry.register(ModEntities.WARRIOR_PIGLIN, WarriorPiglinRenderer::new);
        EntityRendererRegistry.register(ModEntities.PICKAXE_PIGLIN, PickaxePiglinRenderer::new);
        EntityRendererRegistry.register(ModEntities.HAMMER_PIGLIN, HammerPiglinRenderer::new);
        EntityRendererRegistry.register(ModEntities.PIGLIN_GENERAL, PiglinGeneralRenderer::new);
    }
}
