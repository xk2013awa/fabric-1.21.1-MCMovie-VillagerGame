package com.villager.client.render;

import com.villager.entity.ExclamationEntity;
import com.villager.model.ExclamationModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ExclamationRenderer extends GeoEntityRenderer<ExclamationEntity> {
    public ExclamationRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new ExclamationModel());
    }
}
