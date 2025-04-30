package com.villager.client.render;

import com.villager.entity.SpearPiglinEntity;
import com.villager.model.SpearPiglinModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SpearPiglinRenderer extends GeoEntityRenderer<SpearPiglinEntity> {
    public SpearPiglinRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new SpearPiglinModel());
    }
}
