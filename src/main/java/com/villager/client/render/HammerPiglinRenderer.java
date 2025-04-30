package com.villager.client.render;

import com.villager.entity.HammerPiglinEntity;
import com.villager.model.HammerPiglinModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HammerPiglinRenderer extends GeoEntityRenderer<HammerPiglinEntity> {
    public HammerPiglinRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new HammerPiglinModel());
    }
}
