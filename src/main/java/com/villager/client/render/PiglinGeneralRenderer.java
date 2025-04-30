package com.villager.client.render;

import com.villager.entity.PiglinGeneralEntity;
import com.villager.model.PiglinGeneraModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PiglinGeneralRenderer extends GeoEntityRenderer<PiglinGeneralEntity> {
    public PiglinGeneralRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new PiglinGeneraModel());
    }
}
