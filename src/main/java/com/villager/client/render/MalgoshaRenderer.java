package com.villager.client.render;

import com.villager.entity.MalgoshaEntity;
import com.villager.model.MalgoshaModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MalgoshaRenderer extends GeoEntityRenderer<MalgoshaEntity> {
    public MalgoshaRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new MalgoshaModel());
    }
}
