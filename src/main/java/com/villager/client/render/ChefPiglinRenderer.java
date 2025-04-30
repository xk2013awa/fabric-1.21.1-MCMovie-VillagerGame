package com.villager.client.render;

import com.villager.entity.ChefPiglinEntity;
import com.villager.model.ChefPiglinModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ChefPiglinRenderer extends GeoEntityRenderer<ChefPiglinEntity> {
    public ChefPiglinRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new ChefPiglinModel());
    }
}
